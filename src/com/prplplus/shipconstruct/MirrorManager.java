package com.prplplus.shipconstruct;

import java.util.ArrayList;
import java.util.List;

public class MirrorManager {

    public static final int MIRROR_NONE = 0, MIRROR_HORIZONTAL = 1, MIRROR_VERTICAL = 2, MIRROR_ROTATION = 3;

    private int verticalMirror = -1; //even: at edge
    private int horizontalMirror = -1; //odd: at center

    private int rotationMirrorX = -1;
    private int rotationMirrorY = -1;

    public int getVerticalMirror() {
        return verticalMirror;
    }

    public void setVerticalMirror(int verticalMirror) {
        this.verticalMirror = verticalMirror;
        if (verticalMirror >= 0)
            clearRotationMirror();
    }

    public void clearVerticalMirror() {
        verticalMirror = -1;
    }

    public boolean hasVerticalMirror() {
        return verticalMirror >= 0;
    }

    public int getHorizontalMirror() {
        return horizontalMirror;
    }

    public void setHorizontalMirror(int horizontalMirror) {
        this.horizontalMirror = horizontalMirror;
        if (horizontalMirror >= 0)
            clearRotationMirror();
    }

    public void clearHorizontalMirror() {
        horizontalMirror = -1;
    }

    public boolean hasHorizontalMirror() {
        return horizontalMirror >= 0;
    }

    public int getRotationMirrorX() {
        return rotationMirrorX;
    }

    public int getRotationMirrorY() {
        return rotationMirrorY;
    }

    public void setRotationMirror(int rotationMirrorX, int rotationMirrorY) {
        this.rotationMirrorX = rotationMirrorX;
        this.rotationMirrorY = rotationMirrorY;

        if (rotationMirrorX >= 0 || rotationMirrorY >= 0) {
            clearVerticalMirror();
            clearHorizontalMirror();
        }
    }

    public void clearRotationMirror() {
        rotationMirrorX = -1;
        rotationMirrorY = -1;
    }

    public boolean hasRotationMirror() {
        return rotationMirrorX >= 0 || rotationMirrorY >= 0;
    }

    public boolean hasAnyMirror() {
        return hasHorizontalMirror() || hasVerticalMirror() || hasRotationMirror();
    }

    public void fixMirrors(int maxSize) {
        if (horizontalMirror < 0 || horizontalMirror > 2 * maxSize)
            horizontalMirror = -1;
        if (verticalMirror < 0 || verticalMirror > 2 * maxSize)
            verticalMirror = -1;
        if (rotationMirrorX < 0 || rotationMirrorX > 2 * maxSize ||
                rotationMirrorY < 0 || rotationMirrorY > 2 * maxSize) {
            rotationMirrorX = -1;
            rotationMirrorY = -1;
        }
    }

    private void addRotatedModule(List<HullAtPosition> list, Module module, int xOff, int yOff, int hull) {
        list.add(new HullAtPosition((rotationMirrorX + xOff - module.width) / 2,
                (rotationMirrorY + yOff - module.height) / 2, module, hull));
    }

    private boolean anyCollide(List<HullAtPosition> modules) {
        for (int i = 1; i < modules.size(); i++) {
            for (int j = 0; j < i; j++) {
                if (modules.get(i).intersectsWith(modules.get(j), true))
                    return true;
            }
        }
        return false;
    }

    public List<? extends ModuleAtPosition> getMirroredModules(ModuleAtPosition module) {
        return getMirroredHull(new HullAtPosition(module.x, module.y, module.module, 0), true);
    }

    private List<HullAtPosition> getMirroredHull(HullAtPosition module, boolean detectCollisions) {
        ArrayList<HullAtPosition> ret = new ArrayList<>();
        ret.add(module);

        if (horizontalMirror >= 0) {
            int yDist = horizontalMirror - 2 * module.y;
            if (detectCollisions && yDist > 0 && yDist < 2 * module.module.height) {
                //the module collides with the mirror line: 
                //the mirrored module would collide with the original: do nothing
            } else { // mirror the module to the other side
                int newY = (horizontalMirror + yDist - 2 * module.module.height) / 2;
                ret.add(new HullAtPosition(module.x, newY, module.module, Hull.flipHorizontaly(module.hull)));
            }
        }

        if (verticalMirror >= 0) {
            int curSize = ret.size();
            for (int i = 0; i < curSize; i++) {
                HullAtPosition newModule = ret.get(i);

                int xDist = verticalMirror - 2 * newModule.x;
                if (detectCollisions && xDist > 0 && xDist < 2 * newModule.module.width) {
                    //the module collides with the mirror line: 
                    //the mirrored module would collide with the original: do nothing
                } else { // mirror the module to the other side
                    int newX = (verticalMirror + xDist - 2 * newModule.module.width) / 2;
                    ret.add(new HullAtPosition(newX, newModule.y, newModule.module, Hull.flipVerticaly(newModule.hull)));
                }
            }
        }

        if (hasRotationMirror()) {
            int halfX = rotationMirrorX - 2 * module.x - module.module.width;
            int halfY = rotationMirrorY - 2 * module.y - module.module.height;

            addRotatedModule(ret, module.module, halfY, -halfX, Hull.rotateCW(module.hull));
            addRotatedModule(ret, module.module, halfX, halfY, Hull.rotateCCW(Hull.rotateCCW(module.hull)));
            addRotatedModule(ret, module.module, -halfY, halfX, Hull.rotateCCW(module.hull));

            if (detectCollisions && anyCollide(ret)) {
                //remove all added modules on colision
                ret.remove(3);
                ret.remove(2);
                ret.remove(1);
            }
        }

        ret.remove(0);
        return ret;
    }

    public List<MirroredHullBrush> getMirroredHull(ModuleAtPosition pos, int hullType, int brushSize) {
        ArrayList<MirroredHullBrush> ret = new ArrayList<>();

        Module m = null;
        switch (brushSize) {
        case 1:
            m = Module.BRUSH_1X1;
            break;
        case 3:
            m = Module.BRUSH_3X3;
            break;
        case 5:
            m = Module.BRUSH_5X5;
            break;
        case 9:
            m = Module.BRUSH_9X9;
            break;
        }

        HullAtPosition brush = new HullAtPosition(pos.x - brushSize / 2, pos.y - brushSize / 2, m, hullType);

        for (HullAtPosition mirrored : getMirroredHull(brush, false)) {
            ret.add(new MirroredHullBrush(mirrored.hull, mirrored.x + brushSize / 2, mirrored.y + brushSize / 2));
        }

        return ret;
    }

    private static class HullAtPosition extends ModuleAtPosition {
        public int hull;

        public HullAtPosition(int x, int y, Module module, int hull) {
            super(x, y, module);
            this.hull = hull;
        }

    }

    public static class MirroredHullBrush {
        public int hullPiece;
        public int x, y;

        public MirroredHullBrush(int hullPiece, int x, int y) {
            this.hullPiece = hullPiece;
            this.x = x;
            this.y = y;
        }
    }
}
