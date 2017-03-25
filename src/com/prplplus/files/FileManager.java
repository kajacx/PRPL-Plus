package com.prplplus.files;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.prplplus.Settings;
import com.prplplus.shipconstruct.ShipPart;

public class FileManager {
    public static List<ShipPart> loadShipParts() {
        List<ShipPart> parts = new ArrayList<>();
        File partsFolder = new File(Settings.WORK_IN + "/ships/parts");
        for (File file : partsFolder.listFiles()) {
            try {
                if (file.getName().endsWith(".txt")) {
                    Scanner scan = new Scanner(file);
                    ShipPart part = ShipPart.loadFromB64(scan.nextLine());
                    scan.close();
                    Image image = ImageIO.read(new File(file.getName().replace(".txt", ".png")));
                    part.image = image;
                    parts.add(part);
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }
        }
        return parts;
    }
}
