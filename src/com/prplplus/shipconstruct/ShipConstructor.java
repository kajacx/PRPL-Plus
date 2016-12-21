package com.prplplus.shipconstruct;

//static imports are here so that the hull in examples isn't infinitely long
import static com.prplplus.shipconstruct.Hull.HULL_ARMOR_BLOCK;
import static com.prplplus.shipconstruct.Hull.HULL_ARMOR_CORNER_LB;
import static com.prplplus.shipconstruct.Hull.HULL_ARMOR_CORNER_LT;
import static com.prplplus.shipconstruct.Hull.HULL_ARMOR_CORNER_RB;
import static com.prplplus.shipconstruct.Hull.HULL_ARMOR_CORNER_RT;
import static com.prplplus.shipconstruct.Hull.HULL_ARMOR_SPIKE_B;
import static com.prplplus.shipconstruct.Hull.HULL_ARMOR_SPIKE_L;
import static com.prplplus.shipconstruct.Hull.HULL_ARMOR_SPIKE_T;
import static com.prplplus.shipconstruct.Hull.HULL_BLOCK;
import static com.prplplus.shipconstruct.Hull.HULL_SPACE;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ShipConstructor {

    private static void pushBytes(ByteArrayOutputStream buffer, String bytes) {
        bytes = bytes.replaceAll("\\s+", "");
        for (int i = 0; i < bytes.length(); i += 2) {
            String str = bytes.substring(i, i + 2);
            int data = Integer.parseInt(str, 16);
            buffer.write(data);
        }
    }

    //in little endian format
    private static void pushIntLE(ByteArrayOutputStream buffer, int val, int bytes) {
        for (int i = 0; i < bytes; i++) {
            buffer.write(val);
            val = val >>> 8;
        }
    }

    private static void pushIntLE(ByteArrayOutputStream buffer, int val) {
        pushIntLE(buffer, val, 4);
    }

    public static String construct(Ship ship) {
        return construct(ship.width, ship.height, ship.hull, ship.modules, ship.commandX, ship.commandY, ship.name);
    }

    public static String construct(int width, int height, int[] hull, List<ModuleAtPosition> modules, int centerX, int centerY, String name) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        //header
        pushBytes(buffer, "0A04 0072 6F6F 7403 0100 7306 0053 6869 7039 39E9 0300 6D70 73");

        //number of modules
        pushIntLE(buffer, modules.size());

        /*//zeroes if some modules are present
        if (!modules.isEmpty()) {
            pushBytes(buffer, "00 00");
        }*/

        //module data
        for (ModuleAtPosition module : modules) {
            pushBytes(buffer, "00 00");
            pushIntLE(buffer, module.module.code, 2);
            pushBytes(buffer, "00 00");
            pushIntLE(buffer, Module.indexToPos[module.x], 2);
            pushBytes(buffer, "00 00");
            pushIntLE(buffer, Module.indexToPos[module.y], 2);
        }

        //always the same data
        pushBytes(buffer, "01 0200 6877");
        //width
        pushIntLE(buffer, width);

        //always the same data
        pushBytes(buffer, "0102 0068 68");
        //height
        pushIntLE(buffer, height);

        //always the same data
        pushBytes(buffer, "0B 0200 6870");
        //total hull size
        pushIntLE(buffer, width * height);

        //hull info
        for (int hullPiece : hull) {
            pushIntLE(buffer, hullPiece);
        }

        //always the same data
        pushBytes(buffer, "0103 0063 6D78");
        //x position of the center
        pushIntLE(buffer, centerX);

        //always the same data
        pushBytes(buffer, "0103 0063 6D79");
        //y position of the center
        pushIntLE(buffer, centerY);

        //always the same data
        pushBytes(buffer, "0302 0073 6E");
        //length of the name
        pushIntLE(buffer, name.length(), 2); //only 2 byte in the name length

        //the name itself
        try {
            buffer.write(name.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }

        //and finally a zero at the end
        pushBytes(buffer, "00");

        byte[] result = buffer.toByteArray();

        //System.out.println("Width: " + width + ", height: " + height);
        //result = Base64.getDecoder().decode("CgQAcm9vdAMBAHMGAFNoaXA5OekDAG1wcwIAAAAAAABAAAAAAAAAAEAAAABAAACAQAAAAEABAgBodwUAAAABAgBoaAUAAAALAgBocBkAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQMAY214AQAAAAEDAGNteQEAAAADAgBzbgoARXhwb3J0VGVzdAA=");
        /*int[] result1 = new int[result.length];
        for (int i = 0; i < result1.length; i++) {
            result1[i] = result[i] >= 0 ? result[i] : result[i] + 256;
        }
        System.out.println(Arrays.toString(result1));*/

        return Base64.getEncoder().encodeToString(result);
    }

    // A test/showcase method for building a Lathe
    public static String constructLathe() {
        int width = 12;
        int height = 7;

        int[] hull = {
                HULL_SPACE, HULL_ARMOR_SPIKE_T, HULL_SPACE, HULL_SPACE, HULL_SPACE, HULL_SPACE, HULL_SPACE, HULL_SPACE, HULL_ARMOR_SPIKE_T, HULL_SPACE, HULL_SPACE, HULL_SPACE,
                HULL_ARMOR_CORNER_RT, HULL_ARMOR_BLOCK, HULL_ARMOR_BLOCK, HULL_ARMOR_BLOCK, HULL_ARMOR_BLOCK, HULL_ARMOR_BLOCK, HULL_ARMOR_BLOCK, HULL_ARMOR_BLOCK, HULL_ARMOR_BLOCK, HULL_ARMOR_CORNER_LT, HULL_SPACE, HULL_SPACE,
                HULL_SPACE, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_ARMOR_BLOCK, HULL_ARMOR_CORNER_LT, HULL_SPACE,
                HULL_SPACE, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_ARMOR_BLOCK, HULL_ARMOR_SPIKE_L,
                HULL_SPACE, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_ARMOR_BLOCK, HULL_ARMOR_CORNER_LB, HULL_SPACE,
                HULL_ARMOR_CORNER_RB, HULL_ARMOR_BLOCK, HULL_ARMOR_BLOCK, HULL_ARMOR_BLOCK, HULL_ARMOR_BLOCK, HULL_ARMOR_BLOCK, HULL_ARMOR_BLOCK, HULL_ARMOR_BLOCK, HULL_ARMOR_BLOCK, HULL_ARMOR_CORNER_LB, HULL_SPACE, HULL_SPACE,
                HULL_SPACE, HULL_ARMOR_SPIKE_B, HULL_SPACE, HULL_SPACE, HULL_SPACE, HULL_SPACE, HULL_SPACE, HULL_SPACE, HULL_ARMOR_SPIKE_B, HULL_SPACE, HULL_SPACE, HULL_SPACE
        };

        List<ModuleAtPosition> modules = new ArrayList<>();
        modules.add(new ModuleAtPosition(1, 2, Module.ENGINE));
        modules.add(new ModuleAtPosition(6, 2, Module.LATHE));
        modules.add(new ModuleAtPosition(9, 3, Module.LASER));

        int centerX = 3;
        int centerY = 2;
        String name = "LATHE";

        return construct(width, height, hull, modules, centerX, centerY, name);
    }

    // A test/showcase method for building a Cruiser
    public static String constructCruiser() {
        int width = 11;
        int height = 9;

        int[] hull = {
                HULL_SPACE, HULL_SPACE, HULL_SPACE, HULL_SPACE, HULL_ARMOR_CORNER_RT, HULL_ARMOR_BLOCK, HULL_ARMOR_BLOCK, HULL_ARMOR_BLOCK, HULL_ARMOR_CORNER_LT, HULL_SPACE, HULL_SPACE,
                HULL_SPACE, HULL_SPACE, HULL_SPACE, HULL_ARMOR_CORNER_RT, HULL_ARMOR_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_ARMOR_BLOCK, HULL_ARMOR_CORNER_LT, HULL_SPACE,
                HULL_ARMOR_CORNER_RT, HULL_ARMOR_BLOCK, HULL_ARMOR_BLOCK, HULL_ARMOR_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_ARMOR_BLOCK, HULL_ARMOR_CORNER_LT,
                HULL_SPACE, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_ARMOR_BLOCK,
                HULL_SPACE, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_ARMOR_BLOCK,
                HULL_SPACE, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_ARMOR_BLOCK,
                HULL_ARMOR_CORNER_RB, HULL_ARMOR_BLOCK, HULL_ARMOR_BLOCK, HULL_ARMOR_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_ARMOR_BLOCK, HULL_ARMOR_CORNER_LB,
                HULL_SPACE, HULL_SPACE, HULL_SPACE, HULL_ARMOR_CORNER_RB, HULL_ARMOR_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_BLOCK, HULL_ARMOR_BLOCK, HULL_ARMOR_CORNER_LB, HULL_SPACE,
                HULL_SPACE, HULL_SPACE, HULL_SPACE, HULL_SPACE, HULL_ARMOR_CORNER_RB, HULL_ARMOR_BLOCK, HULL_ARMOR_BLOCK, HULL_ARMOR_BLOCK, HULL_ARMOR_CORNER_LB, HULL_SPACE, HULL_SPACE
        };

        List<ModuleAtPosition> modules = new ArrayList<>();
        modules.add(new ModuleAtPosition(1, 3, Module.ENGINE));
        modules.add(new ModuleAtPosition(5, 6, Module.MISSLE_LAUNCHER));
        modules.add(new ModuleAtPosition(7, 5, Module.CANNON));
        modules.add(new ModuleAtPosition(9, 4, Module.LASER));
        modules.add(new ModuleAtPosition(8, 4, Module.PARTICLE_BEAM));
        modules.add(new ModuleAtPosition(7, 2, Module.CANNON));
        modules.add(new ModuleAtPosition(5, 1, Module.MISSLE_LAUNCHER));

        int centerX = 4;
        int centerY = 3;
        String name = "CRUISER";

        return construct(width, height, hull, modules, centerX, centerY, name);
    }

    //a debug method
    public static void printB64(String encoded) {
        byte[] data = Base64.getDecoder().decode(encoded);

        for (int i = 0; i < data.length; i += 16) {
            for (int j = 0; j < 16 && i + j < data.length; j++) {
                int value = data[i + j];
                value += value < 0 ? 256 : 0;
                System.out.format("%02X", value);
                if (j % 2 == 1) {
                    System.out.print(' ');
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public static class Ship {
        public int width, height;
        public int[] hull;
        public List<ModuleAtPosition> modules;
        public int commandX, commandY;
        public String name;

        public Ship(int width, int height, int[] hull, List<ModuleAtPosition> modules, int commandX, int commandY, String name) {
            super();
            this.width = width;
            this.height = height;
            this.hull = hull;
            this.modules = modules;
            this.commandX = commandX;
            this.commandY = commandY;
            this.name = name;
        }

    }

}
