package com.prplplus.shipconstruct;

import java.util.List;

public class ShipPartRotator {
    //private int[] hullBuffer = new int[Settings.MAX_SIZE * Settings.MAX_SIZE];

    public static ShipPart flipHorizontaly(ShipPart part) { // flip by horizontal axis
        int width = part.getWidth();
        int height = part.getHeight();
        ShipPart flipped = new ShipPart(part.getName(), width, height);

        //flip hull
        int[] origHull = part.getHull();
        int[] newHull = flipped.getHull();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                newHull[i * height + (height - j - 1)] = Hull.flipHorizontaly(origHull[i * height + j]);
            }
        }

        //flip modules
        List<ModuleAtPosition> modules = flipped.getModules();
        for (ModuleAtPosition module : part.getModules()) {
            int newY = height - module.y - module.module.height;
            modules.add(new ModuleAtPosition(module.x, newY, module.module));
        }

        return flipped;
    }

    public static ShipPart flipVerticaly(ShipPart part) { // flip by vertical axis
        int width = part.getWidth();
        int height = part.getHeight();
        ShipPart flipped = new ShipPart(part.getName(), width, height);

        //flip hull
        int[] origHull = part.getHull();
        int[] newHull = flipped.getHull();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                newHull[(width - i - 1) * height + j] = Hull.flipVerticaly(origHull[i * height + j]);
            }
        }

        //flip modules
        List<ModuleAtPosition> modules = flipped.getModules();
        for (ModuleAtPosition module : part.getModules()) {
            int newX = width - module.x - module.module.width;
            modules.add(new ModuleAtPosition(newX, module.y, module.module));
        }

        return flipped;
    }

    public static ShipPart rotateCCW(ShipPart part) {
        int width = part.getWidth();
        int height = part.getHeight();
        ShipPart flipped = new ShipPart(part.getName(), height, width, !part.isRotated90());

        //rotate hull
        int[] origHull = part.getHull();
        int[] newHull = flipped.getHull();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                newHull[j * width + (height - i + 1)] = Hull.rotateCCW(origHull[i * height + j]);
            }
        }

        //flip modules
        List<ModuleAtPosition> modules = flipped.getModules();
        for (ModuleAtPosition module : part.getModules()) {
            int newX = module.y;
            int newY = width - module.x - module.module.width;
            modules.add(new ModuleAtPosition(newX, newY, module.module));
        }

        return flipped;
    }

    public static ShipPart rotateCW(ShipPart part) {
        int width = part.getWidth();
        int height = part.getHeight();
        ShipPart flipped = new ShipPart(part.getName(), height, width, !part.isRotated90());

        //rotate hull
        int[] origHull = part.getHull();
        int[] newHull = flipped.getHull();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                newHull[(height - j - 1) * width + i] = Hull.rotateCW(origHull[i * height + j]);
            }
        }

        //flip modules
        List<ModuleAtPosition> modules = flipped.getModules();
        for (ModuleAtPosition module : part.getModules()) {
            int newX = height - module.y - module.module.height;
            int newY = module.x;
            modules.add(new ModuleAtPosition(newX, newY, module.module));
        }

        return flipped;
    }
}
