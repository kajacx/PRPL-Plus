package com.prplplus.shipconstruct;

import static com.prplplus.Settings.MAX_SIZE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.prplplus.MyWriter;
import com.prplplus.shipconstruct.ShipConstructor.Ship;

public class ShipExporter {
    private int[] hull;
    private List<ModuleAtPosition> modules;
    private ShipConstructor.Ship ship;

    private int minX, minY;
    private int width, height;
    private int commandX, commandY;
    private int[] newHull;
    List<ModuleAtPosition> customModules = new ArrayList<>(); //with re-computed coordinates

    public ShipExporter(int[] hull, List<ModuleAtPosition> modules) {
        this.hull = hull;
        this.modules = modules;
    }

    public String exportToBase64(String name, String designer, String description, String citg, boolean instabuild) {
        return exportToBase64(name, designer, description, citg, instabuild,
                Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    public String exportToBase64(String name, String designer, String description, String citg, boolean instabuild,
            int minX, int minY, int maxX, int maxY) {
        if (name.length() == 0) {
            return "Error: Empty name";
        }

        //get bounds
        for (int x = 0; x < MAX_SIZE; x++) {
            for (int y = 0; y < MAX_SIZE; y++) {
                if (hull[x * MAX_SIZE + y] != Hull.HULL_SPACE) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }

        if (minX == Integer.MAX_VALUE) {
            return "Error: No hull pieces";
        }

        this.minX = minX;
        this.minY = minY;

        int width = this.width = maxX - minX + 1;
        int height = this.height = maxY - minY + 1;



        //remap modules
        List<ModuleAtPosition> newModuels = new ArrayList<>();
        int commandX = -1;
        int commandY = -1;
        for (ModuleAtPosition module : modules) {
            if (module.module.isCustom()) {
                //export custom modules separately
                continue;
            }

            ModuleAtPosition newModule = module.copy();
            newModule.x = newModule.x - minX; //relative remap
            newModule.y = newModule.y - minY; //relative remap
            newModule.y = height - newModule.y - newModule.module.height; //flip Y axis

            if (newModule.module == Module.COMMAND) {
                if (commandX != -1) {
                    return "Error: Multiple command modules";
                }
                commandX = newModule.x;
                commandY = newModule.y;
            } else {
                newModuels.add(newModule);
            }
        }
        if (commandX == -1) {
            return "Error: No command module";
        }

        this.commandX = commandX;
        this.commandY = commandY;

        //remap hull from col-major to row-major
        int[] newHull = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                newHull[y * width + x] = hull[(x + minX) * MAX_SIZE + (height - y - 1 + minY)];
                System.out.format("%3d", newHull[y * width + x]);
            }
            System.out.println();
        }
        System.out.println();
        this.newHull = newHull;

        //check for hull consistency
        if (!isConnected()) {
            return "Error: Hull is not connected";
        }

        ship = new Ship(width, height, newHull, newModuels, commandX, commandY, name, designer, description, citg, instabuild);

        return ShipConstructor.construct(ship);
    }

    public String exportCustomModules() {
        customModules.clear();

        //search for custom modules and command module
        for (ModuleAtPosition module : modules) {
            if (module.module.isCustom()) {
                ModuleAtPosition newModule = module.copy();
                newModule.x -= minX;
                newModule.y -= minY;
                customModules.add(newModule);
            }
        }

        //flip Y values - we care about relative placement only
        for (ModuleAtPosition module : customModules) {
            module.y = ship.height - module.y - module.module.height;
        }

        //now just feed it into a string buffer
        StringBuffer buffer = new StringBuffer();
        for (ModuleAtPosition module : customModules) {
            buffer.append(module.module.name).append(',');
            buffer.append(module.x).append(',');
            buffer.append(module.y).append(';');
        }

        return buffer.toString();
    }

    public String exportCommandLocation() {
        return String.format("CommandCenter:%d,%d", ship.commandX + 1, ship.commandY + 1);
    }

    //returns true on success, closes the printsream after done
    public boolean writeModuleAdder(MyWriter printer) {
        //try {
        printer.println("once");
        printer.addSpaces();

        printer.format("%s %d %d @RegisterShipSizeRemote%n", escapePrplString(ship.name), ship.width, ship.height);
        for (ModuleAtPosition customModule : customModules) {
            printer.format("%s %d %d %d %d \"TODO: put image name here\" @RegisterShipModuleRemote # Custom module name: %s%n",
                    escapePrplString(ship.name), customModule.x, customModule.y, customModule.module.width, customModule.module.height, customModule.module.name);
        }

        printer.removeSpaces();
        printer.println("endonce");
        printer.println();

        printer.println("24 0 do");
        printer.addSpaces();

        printer.println("I GetShipNameFromSlot ->shipName");
        printer.println("<-shipName " + escapePrplString(ship.name) + " eq if");
        printer.addSpaces();

        printer.println("I GetShipFromSlot ->uid");
        printer.println("<-uid.ShipIsDestroyed not <-uid -?! not and if");
        printer.addSpaces();

        printer.println("1 <-uid ->!");
        printer.println("1 \"_custom_module_ban_\" <-uid concat ->!* #add to a global banlist");
        printer.println();

        for (ModuleAtPosition module : customModules) {
            if (module.module.scriptName == null) {
                printer.println("# Module \"" + module.module.name + "\" skipped because it doesn't have a script name attached");
                continue;
            }

            printer.println("\"PRPLCORE\" 0 0 CreateUnit ->module");
            printer.println("<-module \"ShipModule.prpl\" AddScriptToUnit");
            printer.println("<-module \"ShipModule.prpl\" \"width\" " + module.module.width + " SetScriptVar");
            printer.println("<-module \"ShipModule.prpl\" \"height\" " + module.module.height + " SetScriptVar");
            printer.println("<-module \"ShipModule.prpl\" \"hullX\" " + module.x + " SetScriptVar");
            printer.println("<-module \"ShipModule.prpl\" \"hullY\" " + module.y + " SetScriptVar");
            printer.println("<-module \"ShipModule.prpl\" \"CommandX\" " + (ship.commandX + 1) + " SetScriptVar");
            printer.println("<-module \"ShipModule.prpl\" \"CommandY\" " + (ship.commandY + 1) + " SetScriptVar");
            printer.println("<-module \"ShipModule.prpl\" \"BuildCost\" " + module.module.buildCost + " SetScriptVar");
            printer.println("<-module \"ShipModule.prpl\" \"Ship\" <-uid SetScriptVar");
            printer.println("<-module " + escapePrplString(module.module.scriptName) + " AddScriptToUnit");
            printer.println("");
        }

        printer.removeSpaces();
        printer.println("endif");

        printer.removeSpaces();
        printer.println("endif");

        printer.removeSpaces();
        printer.println("loop");

        printer.println();
        printer.println();

        printer.println("#Same function as RegisterShipSize, but you can call it from another core\r\n" +
                "#first you need t oregister the ship's size (in cells)\r\n" +
                ":RegisterShipSizeRemote # shipName width height -\r\n" +
                "    ->RSS_Height\r\n" +
                "    ->RSS_Width\r\n" +
                "    ->RSS_ShipName\r\n" +
                "    \r\n" +
                "    -1 ->RSS_Uid\r\n" +
                "    \"IsCmDisplay\" 1 GetCoresWithVar 0 do\r\n" +
                "        ->RSS_Uid\r\n" +
                "    loop\r\n" +
                "    <-RSS_Uid -1 eq if\r\n" +
                "        return\r\n" +
                "    endif\r\n" +
                "    \r\n" +
                "    <-RSS_Uid \"CmDisplay.prpl\" \"Ship\" <-RSS_ShipName concat \"Width\"  concat <-RSS_Width  SetScriptVar\r\n" +
                "    <-RSS_Uid \"CmDisplay.prpl\" \"Ship\" <-RSS_ShipName concat \"Height\" concat <-RSS_Height SetScriptVar\r\n" +
                "    \r\n" +
                "#Same function as RegisterShipModule, but you can call it from another core\r\n" +
                "#then you can add one module at a time. X Y is the lower-left position of the module, stating from 0,0 at bottom-left\r\n" +
                ":RegisterShipModuleRemote # shipName x y width height image -\r\n" +
                "    ->RSM_Image  #4\r\n" +
                "    ->RSM_Height #3\r\n" +
                "    ->RSM_Width  #2\r\n" +
                "    ->RSM_Y      #1\r\n" +
                "    ->RSM_X      #0\r\n" +
                "    ->RSM_ShipName\r\n" +
                "    \r\n" +
                "    -1 ->RSS_Uid\r\n" +
                "    \"IsCmDisplay\" 1 GetCoresWithVar 0 do\r\n" +
                "        ->RSS_Uid\r\n" +
                "    loop\r\n" +
                "    <-RSS_Uid -1 eq if\r\n" +
                "        return\r\n" +
                "    endif\r\n" +
                "    \r\n" +
                "    CreateList ->RSM_List\r\n" +
                "    <-RSM_List <-RSM_X AppendToList\r\n" +
                "    <-RSM_List <-RSM_Y AppendToList\r\n" +
                "    <-RSM_List <-RSM_Width AppendToList\r\n" +
                "    <-RSM_List <-RSM_Height AppendToList\r\n" +
                "    <-RSM_List <-RSM_Image AppendToList\r\n" +
                "    \r\n" +
                "    <-RSS_Uid \"CmDisplay.prpl\" \"Ship\" <-RSS_ShipName concat GetScriptVar ->RSS_ShipList    \r\n" +
                "    <-RSS_ShipList eq0 if\r\n" +
                "        CreateList ->RSS_ShipList\r\n" +
                "        <-RSS_Uid \"CmDisplay.prpl\" \"Ship\" <-RSS_ShipName concat <-RSS_ShipList SetScriptVar\r\n" +
                "    endif\r\n" +
                "    \r\n" +
                "    <-RSS_ShipList <-RSM_List AppendToList #add the module to the list\r\n" +
                "\r\n" +
                "#Resets custom modules for a ship. This is useful when integrating with scritps that dynamicly re-create ships with the same name\r\n" +
                ":ResetShipModulesRemote # shipName -\r\n" +
                "    ->RSM_ShipName\r\n" +
                "    \r\n" +
                "    -1 ->RSS_Uid\r\n" +
                "    \"IsCmDisplay\" 1 GetCoresWithVar 0 do\r\n" +
                "        ->RSS_Uid\r\n" +
                "    loop\r\n" +
                "    <-RSS_Uid -1 eq if\r\n" +
                "        return\r\n" +
                "    endif\r\n" +
                "    \r\n" +
                "    <-RSS_Uid \"CmDisplay.prpl\" \"Ship\" <-RSS_ShipName concat CreateList SetScriptVar%n");

        printer.removeSpaces();

        printer.close();

        return true;
        /*} catch (IOException ex) {
            ex.printStackTrace(System.out);
            return false;
        }*/
    }

    private String escapePrplString(String text) {
        if (!text.contains("\"")) {
            return "\"" + text + "\"";
        }

        return "\"\" \"" + text.replaceAll("\"", "\" concat DoubleQuote concat \"") + "\" concat";
    }

    public boolean writeRobotModuleAdder(MyWriter printer) {
        int xOffset = (commandX + 1) - width / 2;
        int yOffset = (commandY + 1) - height / 2;

        StringBuilder hullCheck = new StringBuilder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                hullCheck.append(String.format("            <-sid %d %d GetShipHullSection %d eq and%n", x, y, newHull[y * width + x]));
            }
        }

        StringBuilder modules = new StringBuilder();
        for (ModuleAtPosition module : customModules) {
            if (module.module.scriptName == null) {
                modules.append("                # Module \"" + module.module.name + "\" skipped, because it doesn't have a script name attached").append(System.lineSeparator());
                modules.append("                ").append(System.lineSeparator());
                continue;
            }

            modules.append("                \"PRPLCORE\" 0 0 CreateUnit ->module").append(System.lineSeparator());
            modules.append("                <-module \"ShipModule.prpl\" AddScriptToUnit").append(System.lineSeparator());
            modules.append("                <-module \"ShipModule.prpl\" \"width\" " + module.module.width + " SetScriptVar").append(System.lineSeparator());
            modules.append("                <-module \"ShipModule.prpl\" \"height\" " + module.module.height + " SetScriptVar").append(System.lineSeparator());
            modules.append("                <-module \"ShipModule.prpl\" \"hullX\" " + module.x + " SetScriptVar").append(System.lineSeparator());
            modules.append("                <-module \"ShipModule.prpl\" \"hullY\" " + module.y + " SetScriptVar").append(System.lineSeparator());
            modules.append("                <-module \"ShipModule.prpl\" \"CommandX\" " + (ship.commandX + 1) + " SetScriptVar").append(System.lineSeparator());
            modules.append("                <-module \"ShipModule.prpl\" \"CommandY\" " + (ship.commandY + 1) + " SetScriptVar").append(System.lineSeparator());
            modules.append("                <-module \"ShipModule.prpl\" \"BuildCost\" " + module.module.buildCost + " SetScriptVar").append(System.lineSeparator());
            modules.append("                <-module \"ShipModule.prpl\" \"Ship\" <-sid SetScriptVar").append(System.lineSeparator());
            modules.append("                <-module \"" + module.module.scriptName + "\" AddScriptToUnit").append(System.lineSeparator());
            modules.append("                ").append(System.lineSeparator());
        }

        String code = String.format("once\r\n" +
                "    -1 ->uid\r\n" +
                "endonce\r\n" +
                "\r\n" +
                "<-uid -1 eq if\r\n" +
                "    CurrentCoords 0 0 GetAllUnitsInRange ->units\r\n" +
                "    <-units GetListCount 0 do\r\n" +
                "        <-units[I] GetUnitType \"ShipSpawner\" eq if\r\n" +
                "            <-units[I] ->uid\r\n" +
                "        endif\r\n" +
                "    loop\r\n" +
                "endif\r\n" +
                "\r\n" +
                "<-uid 0 gte <-uid GetUnitIsDestroyed and if\r\n" +
                "    Self 0 DestroyUnit\r\n" +
                "endif\r\n" +
                "\r\n" +
                "CurrentCoords swap %d add swap %d add 0 0 GetAllShipsInRange ->ships\r\n" +
                "<-ships GetListCount 0 do\r\n" +
                "    <-ships[I] ->sid\r\n" +
                "    <-sid -?! \"_custom_module_ban_\" <-sid concat -?! or not if # ship isn't in local or global banlist\r\n" +
                "        <-sid GETSHIPHULLWIDTH %d eq <-sid GETSHIPHULLHeight %d eq <-sid.ShipIsDestroyed eq0 and and if #fast check\r\n" +
                "            1 #leave the answer always on stack\r\n" +
                "%s" +
                "            if #long check\r\n" +
                "                1 \"_custom_module_ban_\" <-sid concat ->!* #add to a global banlist\r\n" +
                "                \r\n" +
                "%s" +
                "            endif\r\n" +
                "        endif\r\n" +
                "        1 <-sid ->! #the ship was precessed (accepted/rejected), add to local banlist eighter way\r\n" +
                "    endif\r\n" +
                "loop\r\n" +
                "", xOffset, yOffset, width, height, hullCheck, modules);

        printer.print(code);
        printer.close();
        return true;
    }

    public void copyCustomModuleCreators(String... scripts) throws IOException {
        String dest = new File(scripts[0]).getParent() + "/../editor/";

        Set<String> maps = new HashSet<>();
        for (ModuleAtPosition custom : customModules) {
            for (String map : custom.module.saveToMaps) {
                maps.add(map);
            }
        }

        for (String map : maps) {
            for (String script : scripts) {
                copyFileUsingStream(new File(script), new File(dest + map + "/scripts/" + new File(script).getName()), false);
            }

            for (ModuleAtPosition custom : customModules) {
                if (custom.module.scriptName == null || custom.module.scriptName.trim().isEmpty()) {
                    continue;
                }
                copyFileUsingStream(new File("CsBin/editor/CustomModuleTemplate.prpl"), new File(dest + map + "/scripts/" + custom.module.scriptName), false);
            }
        }
    }

    public void copyFileUsingStream(File source, File dest, boolean override) throws IOException {
        if (!override && dest.exists()) {
            return;
        } else {
            dest.getParentFile().mkdirs();
        }

        try (InputStream is = new FileInputStream(source); OutputStream os = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }

    public boolean hasCustomModules() {
        return !customModules.isEmpty();
    }

    private int[] searchHull;

    private boolean isConnected() {
        //ensire correct temp hull size
        if (searchHull == null || searchHull.length != hull.length) {
            searchHull = new int[hull.length];
        }
        System.arraycopy(hull, 0, searchHull, 0, hull.length);

        //find where to search from
        int startX = 0, startY = 0;
        for (int i = 0; i < searchHull.length; i++) {
            if (searchHull[i] != Hull.HULL_SPACE) {
                startX = i / MAX_SIZE;
                startY = i % MAX_SIZE;
            }
        }

        //do the search
        searchConnected(searchHull, startX, startY);

        //test is all blocks have been found
        for (int i = 0; i < searchHull.length; i++) {
            if (searchHull[i] != Hull.HULL_SPACE) {
                return false;
            }
        }

        return true;
    }

    //true - connected, false - disconnected
    private void searchConnected(int[] hull, int x, int y) {
        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(x);
        queue.add(y);
        while (!queue.isEmpty()) {
            x = queue.poll();
            y = queue.poll();

            if (x < 0 || x >= MAX_SIZE || y < 0 || y >= MAX_SIZE)
                continue;
            if (hull[x * MAX_SIZE + y] == Hull.HULL_SPACE)
                continue;

            hull[x * MAX_SIZE + y] = Hull.HULL_SPACE;

            queue.add(x + 1);
            queue.add(y);

            queue.add(x);
            queue.add(y + 1);

            queue.add(x - 1);
            queue.add(y);

            queue.add(x);
            queue.add(y - 1);
        }
    }
}
