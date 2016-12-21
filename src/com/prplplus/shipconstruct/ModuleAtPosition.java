package com.prplplus.shipconstruct;

public class ModuleAtPosition {
    public int x;
    public int y;
    public Module module;

    public ModuleAtPosition(int x, int y, Module module) {
        this.x = x;
        this.y = y;
        this.module = module;
    }

    public boolean intersectsWith(ModuleAtPosition other) {
        boolean xCol = false; //x colision
        if (x >= other.x && x < other.x + other.module.width)
            xCol = true;
        if (other.x >= x && other.x < x + module.width)
            xCol = true;

        boolean yCol = false; //y colision
        if (y >= other.y && y < other.y + other.module.height)
            yCol = true;
        if (other.y >= y && other.y < y + module.height)
            yCol = true;

        return xCol && yCol;
    }

    public ModuleAtPosition copy() {
        return new ModuleAtPosition(x, y, module);
    }

}
