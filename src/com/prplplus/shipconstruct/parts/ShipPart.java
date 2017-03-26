package com.prplplus.shipconstruct.parts;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.prplplus.shipconstruct.Hull;
import com.prplplus.shipconstruct.Module;
import com.prplplus.shipconstruct.ModuleAtPosition;
import com.prplplus.shipconstruct.ShipConstructor;
import com.prplplus.shipconstruct.ShipConstructor.Ship;
import com.prplplus.shipconstruct.ShipDeconstructor;

public class ShipPart {
    public static final String designer = "kajacx's Part Builder";

    private String name;
    private int width;
    private int height;

    private int[] hull; //row major: x * height + y
    private List<ModuleAtPosition> modules = new ArrayList<>();

    private boolean rotated90; //is rotated 90 degrees / don't place non-square modules
    private Module placementBrush;
    protected RotatablePart rotator;

    public Image image;

    //when loading from a file
    public ShipPart(String name, int width, int height) {
        this(name, width, height, false);
    }

    //when selection in editor, automaticly creates the image
    public ShipPart(int width, int height, int[] hull, List<ModuleAtPosition> modules) {
        this.width = width;
        this.height = height;
        this.hull = hull;
        this.modules = modules;

        placementBrush = Module.brushManager.getBrush(width, height);
        image = createImage();
    }

    //when created via rotation
    protected ShipPart(String name, int width, int height, boolean rotated90) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.rotated90 = rotated90;

        hull = new int[width * height];
        placementBrush = Module.brushManager.getBrush(width, height);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getHull() {
        return hull;
    }

    public List<ModuleAtPosition> getModules() {
        return modules;
    }

    public boolean isRotated90() {
        return rotated90;
    }

    public Module getPlacementBrush() {
        return placementBrush;
    }

    public RotatablePart getRotator() {
        if (rotator == null) {
            rotator = new RotatablePart(this);
        }
        return rotator;
    }

    public BufferedImage createImage() {
        int zoom = 32;

        BufferedImage image = new BufferedImage(width * zoom, height * zoom, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        //paint hull
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int hull = this.hull[i * height + j];
                if (hull != 0) {
                    g2d.drawImage(Hull.hullImages[hull], i * zoom, j * zoom, zoom, zoom, null);
                }
            }
        }

        //paint modules
        for (ModuleAtPosition module : modules) {
            g2d.drawImage(module.module.image, module.x * zoom, module.y * zoom,
                    module.module.width * zoom, module.module.height * zoom, null);
        }

        g2d.dispose();
        return image;
    }

    public String saveToB64() {
        return ShipConstructor.construct(width, height, hull, modules, 0, 0, name, designer,
                "This is only a part of a ship. Don't import this as a ship!", "c94bc32d1c0e9e18c6566cfa9087bc7f", false);
    }

    public static ShipPart loadFromB64(String data) {
        Ship ship = ShipDeconstructor.deconstruct(data);
        ShipPart part = new ShipPart(ship.name, ship.width, ship.height);
        part.modules.addAll(ship.modules);
        System.arraycopy(ship.hull, 0, part.hull, 0, ship.hull.length);
        return part;
    }

}
