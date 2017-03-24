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

    //@formatter:off
    public static final Module UNKNOWN         = new Module( 1, 1,     -2, "Unknown"); //an unknown custom module
    public static final Module COMMAND         = new Module( 3, 3,     -1, "Command");
    public static final Module ENGINE          = new Module( 2, 3, 0x40A0, "Engine");
    public static final Module LATHE           = new Module( 3, 3, 0x4110, "Lathe");
    public static final Module LASER           = new Module( 1, 1, 0x4000, "Laser");
    public static final Module CANNON          = new Module( 2, 2, 0x3F80, "Cannon");
    public static final Module MISSLE_LAUNCHER = new Module( 2, 2, 0x4040, "MissleLauncher");
    public static final Module PARTICLE_BEAM   = new Module( 1, 1, 0x4080, "ParticleBeam");
    public static final Module DISCHARGE       = new Module( 3, 3, 0x4190, "Discharge");
    public static final Module ENERGY_TANK     = new Module( 3, 3, 0x40C0, "EnergyTank");
    public static final Module PORT            = new Module( 3, 3, 0x4100, "Port");
    public static final Module GUPPY           = new Module( 3, 3, 0x40E0, "Guppy");
    public static final Module SHIELD          = new Module( 3, 3, 0x4140, "Shield");
    public static final Module REACTOR         = new Module( 3, 3, 0x4160, "Reactor");
    public static final Module FIGHTER_BASE    = new Module(15, 3, 0x4120, "FighterBase");
    public static final Module GRABBER         = new Module( 3, 3, 0x4180, "Grabber");
    public static final Module MK7             = new Module( 5, 5, 0x4188, "MK7");
    public static final Module HQ_COMMAND      = new Module( 5, 9, 0x4170, "HQCommand");
    
    //brushes used for collision detection in mirrored editing
    public static final Module BRUSH_1X1       = new Module( 1, 1,     -3, "Brush 1x1", true);
    public static final Module BRUSH_3X3       = new Module( 3, 3,     -3, "Brush 3x3", true);
    public static final Module BRUSH_5X5       = new Module( 5, 5,     -3, "Brush 5x5", true);
    public static final Module BRUSH_9X9       = new Module( 9, 9,     -3, "Brush 9x9", true);
    //@formatter:on

    public static final Module[] brushByIndex = { BRUSH_1X1, BRUSH_3X3, BRUSH_5X5, BRUSH_9X9 };

    public static final Module[] brushBySize = { null, BRUSH_1X1, null, BRUSH_3X3, null, BRUSH_5X5,
            null, null, null, BRUSH_9X9 };

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
    public final int code; //-1: command, -2: custom
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

    //TODO: replace this with Float.floatToIntBits()
    public static final int[] indexToPos = {
            0x0000, //+??
            0x3F80, //+80
            0x4000, //+40
            0x4040, //+40
            0x4080, //+20
            0x40A0, //+20
            0x40C0, //+20
            0x40E0, //+20
            0x4100, //+10
            0x4110, //+10
            0x4120, //+10
            0x4130, //+10
            0x4140, //+10
            0x4150, //+10
            0x4160, //+10
            0x4170, //+10
            0x4180, //+08
            0x4188, //+08
            0x4190, //+08
            0x4198, //+08
            0x41A0, //+08
            0x41A8, //+08
            0x41B0, //+08
            0x41B8, //+08
            0x41C0, //+08
            0x41C8, //+08
            0x41D0, //+08
            0x41D8, //+08
            0x41E0, //+08
            0x41E8, //+08
            0x41F0, //+08
            0x41F8, //+08
            0x4200, //+04
            0x4204, //+04
            0x4208, //+04
            0x420C, //+04
            0x4210, //+04
            0x4214, //+04
            0x4218, //+04
            0x421C, //+04
            0x4220, //+04
            0x4224, //+04
            0x4228, //+04
            0x422C, //+04
            0x4230, //+04
            0x4234, //+04
            0x4238, //+04
            0x423C, //+04
            0x4240, //+04
            0x4244, //+04
            0x4248, //+04
            0x424C, //+04
            0x4250, //+04
            0x4254, //+04
            0x4258, //+04
            0x425C, //+04
            0x4260, //+04
            0x4264, //+04
            0x4268, //+04
            0x426C, //+04
            0x4270, //+04
            0x4274, //+04
            0x4278, //+04
            0x427C, //+04
            0x4280, //+02
            0x4282, //+02
            0x4284, //+02
            0x4286, //+02
            0x4288, //+02
            0x428A, //+02
            0x428C, //+02
            0x428E, //+02
            0x4290, //+02
            0x4292, //+02
            0x4294, //+02
            0x4296, //+02
            0x4298, //+02
            0x429A, //+02
            0x429C, //+02
            0x429E, //+02
            0x42A0, //+02
            0x42A2, //+02
            0x42A4, //+02
            0x42A6, //+02
            0x42A8, //+02
            0x42AA, //+02
            0x42AC, //+02
            0x42AE, //+02
            0x42B0, //+02
            0x42B2, //+02
            0x42B4, //+02
            0x42B6, //+02
            0x42B8, //+02
            0x42BA, //+02
            0x42BC, //+02
            0x42BE, //+02
            0x42C0, //+02
            0x42C2, //+02
            0x42C4, //+02
            0x42C6, //+02
            0x42C8, //+02
            0x42CA, //+02
            0x42CC, //+02
            0x42CE, //+02
            0x42D0, //+02
            0x42D2, //+02
            0x42D4, //+02
            0x42D6, //+02
            0x42D8, //+02
            0x42DA, //+02
            0x42DC, //+02
            0x42DE, //+02
            0x42E0, //+02
            0x42E2, //+02
            0x42E4, //+02
            0x42E6, //+02
            0x42E8, //+02
            0x42EA, //+02
            0x42EC, //+02
            0x42EE, //+02
            0x42F0, //+02
            0x42F2, //+02
            0x42F4, //+02
            0x42F6, //+02
            0x42F8, //+02
            0x42FA, //+02
            0x42FC, //+02
            0x42FE, //+02
            0x4300
    };

    public static int getDistFromPos(int pos) {
        for (int i = 0; i < Module.indexToPos.length; i++) {
            if (Module.indexToPos[i] == pos)
                return i;
        }
        System.out.format("Pos not found: %04X%n", pos);
        return -1;
    }

    public static Module getById(int id) {
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
}
