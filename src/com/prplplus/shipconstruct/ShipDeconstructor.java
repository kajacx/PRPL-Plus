package com.prplplus.shipconstruct;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.prplplus.shipconstruct.ShipConstructor.Ship;

public class ShipDeconstructor {

    /** reads int, composing of <code>bytes</code> bytes, staring at
     * <code>index</code> in little endian format
     */
    public static int readIntLE(byte[] data, int index, int bytes) {
        int res = 0;
        for (int i = index + bytes - 1; i >= index; i--) {
            int byteRead = data[i] & 0xFF;
            //System.out.format("At %02d reading 0x%02X (%03d)%n", i, byteRead, byteRead);
            res = 256 * res + byteRead;
        }
        //System.out.format("Returning: 0x%04X (%05d)%n", res, res);
        return res;
    }

    public static String readString(byte[] data, int index) {
        int length = readIntLE(data, index, 2);
        index += 2; //consume nameLength

        return new String(data, index, length, Charset.forName("UTF-8"));
    }

    public static Ship deconstruct(String base64Ship) {
        byte[] data = Base64.getDecoder().decode(base64Ship);

        String designer = null;
        String description = null;
        String CITG = null;

        //int index = 19; //first 19 bytes are just header
        //int index = 18; //HQ workaround, DON'T COMMIT THIS!!!!!

        int index = 11; //at index 11 starts ship type

        String type = readString(data, index);
        index += type.length() + 2;

        boolean instabuild = type.equals("Ship8");

        //look at 19th byte to decide if it has description info
        if (data[index] == 0x03) {
            //ship description detected

            index += 4; //skip header
            designer = readString(data, index);
            index += designer.length() + 2;

            index += 4; //skip header
            description = readString(data, index);
            index += description.length() + 2;

            index += 5; //skip header
            CITG = readString(data, index);
            index += CITG.length() + 2;
        }

        index += 6; //skip the rest of the header

        int modules = readIntLE(data, index, 4);
        index += 4; //read number of modules

        List<ModuleAtPosition> moduleList = new ArrayList<>(modules);

        for (int i = 0; i < modules; i++) {
            index += 2; //skip zeros
            int moduleId = readIntLE(data, index, 2);
            index += 2; //consume data
            Module m = Module.getById(moduleId);

            index += 2; //skip zeros
            int xPos = readIntLE(data, index, 2);
            index += 2; //consume data
            int x = Module.getDistFromPos(xPos);

            index += 2; //skip zeros
            int yPos = readIntLE(data, index, 2);
            index += 2; //consume data
            int y = Module.getDistFromPos(yPos);

            moduleList.add(new ModuleAtPosition(x, y, m));
        }

        index += 5; //skip some data
        int width = readIntLE(data, index, 4);
        index += 4; //consume width

        index += 5; //skip some data
        int height = readIntLE(data, index, 4);
        index += 4; //consume height

        index += 5; //skip data
        index += 4; //skip hull size

        int[] hull = new int[width * height];
        for (int i = 0; i < hull.length; i++) {
            hull[i] = readIntLE(data, index, 4);
            index += 4; //consume hull info
        }

        index += 6; //skip data
        int commandX = readIntLE(data, index, 4);
        index += 4; //consume commandX

        index += 6; //skip data
        int commandY = readIntLE(data, index, 4);
        index += 4; //consume commandY

        index += 5; //skip data

        String name = readString(data, index);
        index += name.length() + 2;

        index += 1; //consume last zero

        if (index != data.length) {
            throw new IllegalArgumentException("Input string has incorrect size");
        }

        return new Ship(width, height, hull, moduleList, commandX, commandY, name, designer, description, CITG, instabuild);
    }
}
