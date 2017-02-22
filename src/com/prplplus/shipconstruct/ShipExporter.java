package com.prplplus.shipconstruct;

import static com.prplplus.Settings.MAX_SIZE;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.prplplus.shipconstruct.ShipConstructor.Ship;

public class ShipExporter {
    private int[] hull;
    private List<ModuleAtPosition> modules;
    private ShipConstructor.Ship ship;

    private int minX, minY;
    List<ModuleAtPosition> customModules = new ArrayList<>(); //with re-computed coordinates

    public ShipExporter(int[] hull, List<ModuleAtPosition> modules) {
        this.hull = hull;
        this.modules = modules;
    }

    public String exportToBase64(String name, String designer, String description, String citg) {
        if (name.length() == 0) {
            return "Error: Empty name";
        }

        //get bounds
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

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

        this.minX = minX;
        this.minY = minY;

        int width = maxX - minX + 1;
        int height = maxY - minY + 1;

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

        //remap hull from col-major to row-major
        int[] newHull = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                newHull[y * width + x] = hull[(x + minX) * MAX_SIZE + (height - y - 1 + minY)];
            }
        }

        //check for hull consistency
        if (!isConnected()) {
            return "Error: Hull is not connected";
        }

        ship = new Ship(width, height, newHull, newModuels, commandX, commandY, name, designer, description, citg);

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
    public boolean writeModuleAdder(PrintStream printer) {
        //try {
        printer.println("24 0 do");
        printer.println("\tI GetShipNameFromSlot ->shipName");
        printer.println("\t<-shipName \"" + ship.name + "\" eq if");
        printer.println("\t\tI GetShipFromSlot ->uid");
        printer.println("\t\t<-uid.ShipIsDestroyed eq0 if");
        printer.println("\t\t\t<-uid <-! eq0 if");
        printer.println("\t\t\t\t1 <-uid ->!");
        printer.println("\t\t\t\t");

        for (ModuleAtPosition module : customModules) {
            if (module.module.scriptName == null) {
                printer.println("\t\t\t\t# Module \"" + module.module.name + "\" skipped because it doesn't have a script name attached");
                continue;
            }

            printer.println("\t\t\t\t\"PRPLCORE\" 0 0 CreateUnit ->module");
            printer.println("\t\t\t\t<-module \"ShipModule.prpl\" AddScriptToUnit");
            printer.println("\t\t\t\t<-module \"ShipModule.prpl\" \"width\" " + module.module.width + " SetScriptVar");
            printer.println("\t\t\t\t<-module \"ShipModule.prpl\" \"height\" " + module.module.height + " SetScriptVar");
            printer.println("\t\t\t\t<-module \"ShipModule.prpl\" \"hullX\" " + module.x + " SetScriptVar");
            printer.println("\t\t\t\t<-module \"ShipModule.prpl\" \"hullY\" " + module.y + " SetScriptVar");
            printer.println("\t\t\t\t<-module \"ShipModule.prpl\" \"CommandX\" " + (ship.commandX + 1) + " SetScriptVar");
            printer.println("\t\t\t\t<-module \"ShipModule.prpl\" \"CommandY\" " + (ship.commandY + 1) + " SetScriptVar");
            printer.println("\t\t\t\t<-module \"ShipModule.prpl\" \"BuildCost\" " + module.module.buildCost + " SetScriptVar");
            printer.println("\t\t\t\t<-module \"ShipModule.prpl\" \"Ship\" <-uid SetScriptVar");
            printer.println("\t\t\t\t<-module \"" + module.module.scriptName + "\" AddScriptToUnit");
            printer.println("\t\t\t\t");
        }

        printer.println("\t\t\tendif");
        printer.println("\t\tendif");
        printer.println("\tendif");
        printer.println("loop");

        printer.close();

        return true;
        /*} catch (IOException ex) {
            ex.printStackTrace(System.out);
            return false;
        }*/
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
        if (x < 0 || x >= MAX_SIZE || y < 0 || y >= MAX_SIZE)
            return;
        if (hull[x * MAX_SIZE + y] == Hull.HULL_SPACE)
            return;
        hull[x * MAX_SIZE + y] = Hull.HULL_SPACE;
        searchConnected(hull, x + 1, y);
        searchConnected(hull, x, y + 1);
        searchConnected(hull, x - 1, y);
        searchConnected(hull, x, y - 1);
    }
}
