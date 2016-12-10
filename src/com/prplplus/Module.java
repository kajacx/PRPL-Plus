package com.prplplus;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public enum Module {
    //@formatter:off
    COMMAND        (3, 3,     -1, "Command"),
    ENGINE         (2, 3, 0x40A0, "Engine"),
    LATHE          (3, 3, 0x4110, "Lathe"),
    LASER          (1, 1, 0x4000, "Laser"),
    CANNON         (2, 2, 0x3F80, "Cannon"),
    MISSLE_LAUNCHER(2, 2, 0x4040, "MissleLauncher"),
    PARTICLE_BEAM  (1, 1, 0x4080, "ParticleBeam"),
    DISCHARGE      (3, 3, 0x4190, "Discharge"),
    ENERGY_TANK    (3, 3, 0x40C0, "EnergyTank"),
    PORT           (3, 3, 0x4100, "Port"),
    GUPPY          (3, 3, 0x40E0, "Guppy"),
    SHIELD         (3, 3, 0x4140, "Shield"),
    REACTOR        (3, 3, 0x4160, "Reactor"),
    FIGHTER_BASE  (15, 3, 0x4120, "FighterBase"),
    GRABBER        (3, 3, 0x4180, "Grabber"),
    MK7            (5, 5, 0x4188, "MK7");
    //@formatter:on

    public final int width, height;
    public final int code;
    public final String name;
    public final Image image;

    private Module(int width, int height, int code, String name) {
        this.width = width;
        this.height = height;
        this.code = code;
        this.name = name;

        Image i;
        try {
            i = ImageIO.read(new File("img/modules/module" + name + ".png"));
        } catch (IOException e) {
            e.printStackTrace(System.out);
            i = null;
        }
        image = i;
    }
}
