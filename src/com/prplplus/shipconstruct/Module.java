package com.prplplus.shipconstruct;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class Module {
    public static final int COMMAND_CODE = -1;
    public static final int CUSTOM_CODE = -2;
    public static final int BRUSH_CODE = -3;

    public static final int DEFAULT_BUILD_COST = 50;

    public static final BrushManager brushManager = new BrushManager();

    //@formatter:off                                         W, H, ID
    public static final Module UNKNOWN         = new Module( 1, 1, -2, "Unknown"); //an unknown custom module
    public static final Module COMMAND         = new Module( 3, 3, -1, "Command");
    public static final Module ENGINE          = new Module( 2, 3,  5, "Engine");
    public static final Module LATHE           = new Module( 3, 3,  9, "Lathe");
    public static final Module LASER           = new Module( 1, 1,  2, "Laser");
    public static final Module CANNON          = new Module( 2, 2,  1, "Cannon");
    public static final Module MISSLE_LAUNCHER = new Module( 2, 2,  3, "MissleLauncher");
    public static final Module PARTICLE_BEAM   = new Module( 1, 1,  4, "ParticleBeam");
    public static final Module DISCHARGE       = new Module( 3, 3, 18, "Discharge");
    public static final Module ENERGY_TANK     = new Module( 3, 3,  6, "EnergyTank");
    public static final Module PORT            = new Module( 3, 3,  8, "Port");
    public static final Module GUPPY           = new Module( 3, 3,  7, "Guppy");
    public static final Module SHIELD          = new Module( 3, 3, 12, "Shield");
    public static final Module REACTOR         = new Module( 3, 3, 14, "Reactor");
    public static final Module FIGHTER_BASE    = new Module(15, 3, 10, "FighterBase");
    public static final Module GRABBER         = new Module( 3, 3, 16, "Grabber");
    public static final Module MK7             = new Module( 5, 5, 17, "MK7");
    public static final Module HQ_COMMAND      = new Module( 5, 9, 15, "HQCommand");
    
    public static final Module BRUSH_1X1       = brushManager.getBrush(1, 1);
    public static final Module BRUSH_2X2       = brushManager.getBrush(2, 2);
    //@formatter:on

    /*public static void main(String[] main) {
        for (Module m : standardModules) {
            System.out.format("%20s -> %02d%n", m.name, decodePositition(m.code << 16));
        }
    }*/

    //someone should put me to programming prison for this...
    public static final Module[] standardModules = { COMMAND, ENGINE, LATHE, LASER, CANNON, MISSLE_LAUNCHER, PARTICLE_BEAM, DISCHARGE,
            ENERGY_TANK, PORT, GUPPY, SHIELD, REACTOR, FIGHTER_BASE, GRABBER, MK7, HQ_COMMAND };

    public static final Module[] defaultModules = { COMMAND, ENGINE, ENERGY_TANK,
            PORT, GUPPY, SHIELD, REACTOR, GRABBER, HQ_COMMAND };

    public static final Module[] weaponModules = { LATHE, LASER, CANNON, MISSLE_LAUNCHER,
            PARTICLE_BEAM, DISCHARGE, FIGHTER_BASE, MK7 };

    public static final List<Module> customModules = new ArrayList<>();

    public static final List<Module> allModules = new ArrayList<>();

    public final int width, height;
    public final int code; //-1: command, -2: custom, -3: brush
    public final String name;
    public final Image image;
    public final int buildCost;
    public final String scriptName;

    //create an arbitary module
    public Module(int width, int height, int code, String name, Image image, int buildCost, String scriptName) {
        this.width = width;
        this.height = height;
        this.code = code;
        this.name = name;
        this.image = image;
        this.buildCost = buildCost;
        this.scriptName = scriptName;
    }

    //create a standart module
    private Module(int width, int height, int code, String name) {
        this(width, height, code, name, false);
    }

    //create a standart module
    private Module(int width, int height, int code, String name, boolean skipImage) {
        this.width = width;
        this.height = height;
        this.code = code;
        this.name = name;

        buildCost = DEFAULT_BUILD_COST;
        scriptName = null;

        Image i = null;
        if (!skipImage) {
            try {
                i = ImageIO.read(new File("img/modules/module" + name + ".png"));
            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
        }
        image = i;
    }

    //create a custom module
    private Module(int width, int height, String name, String imgName, int buildCost, String scriptName) {
        this.width = width;
        this.height = height;
        this.code = CUSTOM_CODE;
        this.name = name;
        this.buildCost = buildCost;
        this.scriptName = scriptName;

        Image i;
        try {
            i = ImageIO.read(new File("img/customModules/" + imgName));
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
            i = null;
        }
        image = i;
    }

    //create an unknown custom module with this name
    private Module(String name) {
        this.width = UNKNOWN.width;
        this.height = UNKNOWN.height;
        this.code = UNKNOWN.code;
        this.image = UNKNOWN.image;
        this.name = name;
        this.buildCost = UNKNOWN.buildCost;
        this.scriptName = UNKNOWN.scriptName;
    }

    static {
        //load custom modules here
        File customModulesFolder = new File("customModules");
        Scanner scan = null;

        for (File f : customModulesFolder.listFiles()) {
            try {
                if (f.getName().endsWith(".txt")) {
                    Properties moduleProps = new Properties();

                    FileInputStream in = new FileInputStream(f);
                    moduleProps.load(in);
                    in.close();

                    String name = moduleProps.getProperty("name");
                    int width = Integer.parseInt(moduleProps.getProperty("width"));
                    int height = Integer.parseInt(moduleProps.getProperty("height"));
                    String imgName = moduleProps.getProperty("imgName");
                    int buildCost = Integer.parseInt(moduleProps.getProperty("buildCost", "50"));
                    String scriptName = moduleProps.getProperty("scriptName");

                    customModules.add(new Module(width, height, name, imgName, buildCost, scriptName));
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            } finally {
                if (scan != null) {
                    scan.close();
                }
            }
        }

        allModules.addAll(Arrays.asList(defaultModules));
        allModules.addAll(Arrays.asList(weaponModules));
        allModules.addAll(customModules);
    }

    public static int encodePosition(int pos) {
        return Float.floatToIntBits(pos);
    }

    public static int decodePositition(int encoded) {
        return (int) Float.intBitsToFloat(encoded);
    }

    public static Module getById(int id) {
        if (id == COMMAND_CODE) {
            return COMMAND;
        }

        for (Module m : Module.standardModules) {
            if (m.code == id) {
                return m;
            }
        }
        System.out.format("Module with id 0x%04X not found (%05d).%n", id, id);
        return null;
    }

    public static Module getCustomByName(String name) {
        for (Module m : Module.customModules) {
            if (m.name.equals(name)) {
                return m;
            }
        }
        System.out.format("Custom module with name '%s' not found.%n", name);

        return new Module(name);
    }

    public boolean isCustom() {
        return code == CUSTOM_CODE;
    }

    public boolean isCommand() {
        return code == COMMAND_CODE;
    }

    public boolean doesCollide() {
        return code != HQ_COMMAND.code;
    }

    public boolean isSquare() {
        return width == height;
    }

    public static class BrushManager {
        private List<List<Module>> brushes = new ArrayList<>();

        public Module getBrush(int width, int height) {
            for (int i = brushes.size(); i < width; i++) {
                brushes.add(new ArrayList<>());
            }

            List<Module> inner = brushes.get(width - 1);
            for (int i = inner.size(); i < height; i++) {
                inner.add(new Module(width, height, BRUSH_CODE, "Brush" + width + "x" + height, null, 0, null));
            }

            return inner.get(height - 1);
        }
    }
}
