package com.prplplus.test;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ExtractModules {
    public static void main(String[] args) throws IOException {
        //BufferedImage modules = ImageIO.read(new File("img/modules/modules.png"));

        String[] names = { "Engine", "Lathe", "Laster", "Cannon", "MissleLauncher", "ParticleBeam",
                "Discharge", "EnergyTank", "Port", "Guppy", "Shield", "Reactor",
                "FighterBase", "Grabber", "MK7" };

        BufferedImage from = ImageIO.read(new File("img/modules/modules.png"));
        BufferedImage to = new BufferedImage(from.getWidth(), from.getHeight() / names.length, BufferedImage.TYPE_INT_ARGB);

        int w = to.getWidth();
        int h = to.getHeight();

        for (int i = 0; i < names.length; i++) {
            Graphics g = to.createGraphics();
            g.drawImage(from, 0, 0, w, h, 0, i * h, w, (i + 1) * h, null);
            g.dispose();
            ImageIO.write(to, "png", new File("img/modules/module_" + names[i] + ".png"));
        }
    }
}
