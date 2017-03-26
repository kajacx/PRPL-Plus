package com.prplplus.files;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.prplplus.shipconstruct.parts.ShipPart;

public class FileManager {
    public static List<ShipPart> loadShipParts() {
        List<ShipPart> parts = new ArrayList<>();
        File partsFolder = new File("parts");
        for (File file : partsFolder.listFiles()) {
            try {
                if (file.getName().endsWith(".txt")) {
                    Scanner scan = new Scanner(file);
                    ShipPart part = ShipPart.loadFromB64(scan.nextLine());
                    scan.close();

                    File pngFile = new File(file.getAbsolutePath().replace(".txt", ".png"));
                    //System.out.println(pngFile + " " + pngFile.exists());
                    Image image = ImageIO.read(pngFile);
                    part.image = image;
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
