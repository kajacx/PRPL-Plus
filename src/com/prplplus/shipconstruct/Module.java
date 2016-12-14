package com.prplplus.shipconstruct;

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

    public static final int[] indexToPos = {
            0x0000, //+??
            0x3F80, //+80
            0x4000, //+40
            0x4040, //+40
            0x4080, //+20
            0x40A0, //+20
            0x40C0, //+20
            0x40E0, //+20
            0x4100, //+10
            0x4110, //+10
            0x4120, //+10
            0x4130, //+10
            0x4140, //+10
            0x4150, //+10
            0x4160, //+10
            0x4170, //+10
            0x4180, //+08
            0x4188, //+08
            0x4190, //+08
            0x4198, //+08
            0x41A0, //+08
            0x41A8, //+08
            0x41B0, //+08
            0x41B8, //+08
            0x41C0, //+08
            0x41C8, //+08
            0x41D0, //+08
            0x41D8, //+08
            0x41E0, //+08
            0x41E8, //+08
            0x41F0, //+08
            0x41F8, //+08
            0x4200, //+04
            0x4204, //+04
            0x4208, //+04
            0x420C, //+04
            0x4210, //+04
            0x4214, //+04
            0x4218, //+04
            0x421C, //+04
            0x4220, //+04
            0x4224, //+04
            0x4228, //+04
            0x422C, //+04
            0x4230, //+04
            0x4234, //+04
            0x4238, //+04
            0x423C, //+04
            0x4240, //+04
            0x4244, //+04
            0x4248, //+04
            0x424C, //+04
            0x4250, //+04
            0x4254, //+04
            0x4258, //+04
            0x425C, //+04
            0x4260, //+04
            0x4264, //+04
            0x4268, //+04
            0x426C, //+04
            0x4270, //+04
            0x4274, //+04
            0x4278, //+04
            0x427C, //+04
            0x4280, //+02
            0x4282, //+02
            0x4284, //+02
            0x4286, //+02
            0x4288, //+02
            0x428A, //+02
            0x428C, //+02
            0x428E, //+02
            0x4290, //+02
            0x4292, //+02
            0x4294, //+02
            0x4296, //+02
            0x4298, //+02
            0x429A, //+02
            0x429C, //+02
            0x429E, //+02
            0x42A0, //+02
            0x42A2, //+02
            0x42A4, //+02
            0x42A6, //+02
            0x42A8, //+02
            0x42AA, //+02
            0x42AC, //+02
            0x42AE, //+02
            0x42B0, //+02
            0x42B2, //+02
            0x42B4, //+02
            0x42B6, //+02
            0x42B8, //+02
            0x42BA, //+02
            0x42BC, //+02
            0x42BE, //+02
            0x42C0, //+02
            0x42C2, //+02
            0x42C4, //+02
            0x42C6, //+02
            0x42C8, //+02
            0x42CA, //+02
            0x42CC, //+02
            0x42CE, //+02
            0x42D0, //+02
            0x42D2, //+02
            0x42D4, //+02
            0x42D6, //+02
            0x42D8, //+02
            0x42DA, //+02
            0x42DC, //+02
            0x42DE, //+02
            0x42E0, //+02
            0x42E2, //+02
            0x42E4, //+02
            0x42E6, //+02
            0x42E8, //+02
            0x42EA, //+02
            0x42EC, //+02
            0x42EE, //+02
            0x42F0, //+02
            0x42F2, //+02
            0x42F4, //+02
            0x42F6, //+02
            0x42F8, //+02
            0x42FA, //+02
            0x42FC, //+02
            0x42FE, //+02
            0x4300
    };

    public static int getDistFromPos(int pos) {
        for (int i = 0; i < Module.indexToPos.length; i++) {
            if (Module.indexToPos[i] == pos)
                return i;
        }
        System.out.format("Pos not found: %04X%n", pos);
        return -1;
    }

    public static Module getById(int id) {
        for (Module m : Module.values()) {
            if (m.code == id) {
                return m;
            }
        }
        System.out.format("Module with id 0x%04X not found (%05d).%n", id, id);
        return null;
    }
}
