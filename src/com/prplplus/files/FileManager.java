package com.prplplus.files;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.prplplus.shipconstruct.Module;
import com.prplplus.shipconstruct.ModuleAtPosition;
import com.prplplus.shipconstruct.parts.ShipPart;

public class FileManager {
    public static File getSipPartsFolder() {
        return new File("parts");
    }

    public static List<ShipPart> loadShipParts() {
        List<ShipPart> parts = new ArrayList<>();
        File partsFolder = getSipPartsFolder();

        for (File file : partsFolder.listFiles()) {
            try {
                if (file.getName().endsWith(".txt")) {
                    Scanner scan = new Scanner(file);
                    ShipPart part = ShipPart.loadFromB64(scan.nextLine());

                    if (scan.hasNextLine()) { //custom modules
                        for (String moduleData : scan.nextLine().split(";")) {
                            if (moduleData.isEmpty())
                                continue;

                            //read data
                            String[] tokens = moduleData.split(",");
                            Module module = Module.getCustomByName(tokens[0]);
                            int posX = Integer.parseInt(tokens[1]);
                            int posY = Integer.parseInt(tokens[2]);

                            //and done
                            part.getModules().add(new ModuleAtPosition(posX, posY, module));
                        }
                    }

                    scan.close();

                    //image
                    File pngFile = new File(file.getAbsolutePath().replace(".txt", ".png"));
                    if (pngFile.exists()) {
                        //use the image
                        part.image = ImageIO.read(pngFile);
                    } else {
                        //create the image
                        part.image = part.createImage();
                    }

                    parts.add(part);
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }
        }
        return parts;
    }

    private static Image selectionCursor;

    public static Image getSelectionCursor() {
        if (selectionCursor == null) {
            try {
                selectionCursor = ImageIO.read(new File("img/other/selectionCursor.png"));
            } catch (IOException e) {
                e.printStackTrace(System.out);
                if (e.getCause() != null) {
                    e.getCause().printStackTrace(System.out);
                }
            }
        }
        return selectionCursor;
    }
}
