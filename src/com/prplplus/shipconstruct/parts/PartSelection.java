package com.prplplus.shipconstruct.parts;

import static com.prplplus.Settings.MAX_SIZE;

import java.util.ArrayList;
import java.util.List;

import com.prplplus.shipconstruct.Hull;
import com.prplplus.shipconstruct.ModuleAtPosition;

public class PartSelection {
    public static final int STATE_IDLE = 0; //this is completely unselected and inactive
    public static final int STATE_READY = 1; //the "Select" button has been clicked, waiting for select
    public static final int STATE_SELECTING = 2; //The selection drag has begun
    public static final int STATE_SELECTED = 3; //The selection is complete

    public int state = STATE_IDLE;

    public int fromX, fromY;
    public int toX, toY;

    //sorted
    public int fromXs, fromYs;
    public int toXs, toYs;

    //sets both the data and state
    public void setFromSelection(int fromX, int fromY) {
        this.fromX = fromX;
        this.fromY = fromY;

        state = STATE_SELECTING;
    }

    //sets both the data and state, and ensures that from < to
    public void setToSelection(int toX, int toY) {
        changeSelection(toX, toY);
        state = STATE_SELECTED;
    }

    public void changeSelection(int toX, int toY) {
        this.toX = toX;
        this.toY = toY;

        this.fromXs = Math.min(this.fromX, toX);
        this.fromYs = Math.min(this.fromY, toY);

        this.toXs = Math.max(this.fromX, toX);
        this.toYs = Math.max(this.fromY, toY);
    }

    public ShipPart doSelectPart(int[] hull, List<ModuleAtPosition> modules) {
        //find hull bounds
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        int fromX = Math.max(fromXs, 0);
        int fromY = Math.max(fromYs, 0);
        int toX = Math.min(toXs, MAX_SIZE);
        int toY = Math.min(toYs, MAX_SIZE);

        for (int x = fromX; x < toX; x++) {
            for (int y = fromY; y < toY; y++) {
                if (hull[x * MAX_SIZE + y] != Hull.HULL_SPACE) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }

        if (minX == Integer.MAX_VALUE) {
            return null;
        }

        int width = maxX - minX + 1;
        int height = maxY - minY + 1;

        //remap hull
        int[] newHull = new int[width * height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                newHull[x * height + y] = hull[(x + minX) * MAX_SIZE + (y + minY)];
            }
        }

        //remap modules
        List<ModuleAtPosition> newModuels = new ArrayList<>();
        for (ModuleAtPosition module : modules) {
            if (module.x >= fromXs && module.y >= fromYs && //module fits
                    module.x + module.module.width <= toXs && module.y + module.module.height <= toYs) {
                ModuleAtPosition newModule = module.copy();
                newModule.x = newModule.x - minX; //relative remap
                newModule.y = newModule.y - minY; //relative remap
                newModuels.add(newModule);
            }
        }

        return new ShipPart(width, height, newHull, newModuels);
    }
}
