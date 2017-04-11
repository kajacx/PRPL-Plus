package com.prplplus.test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class CooldownCreator {

    public static int width = 64, height = 64;

    public static void main(String[] args) throws IOException {

        for (int i = 0; i < 65; i++) {
            BufferedImage buff = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = buff.createGraphics();

            paintCD(g, i / 64f);
            g.dispose();

            ImageIO.write(buff, "png", new File(String.format("trash/cooldowns2/cooldown%02d.png", i)));
        }
    }

    private static void paintCD(Graphics2D g, float percentage) {
        g.setTransform(AffineTransform.getRotateInstance(-Math.PI / 2, width / 2, height / 2));

        g.setColor(Color.WHITE);
        //g.drawRect(0, 0, width - 1, height - 1);
        g.fillArc(-width, -height, 3 * width, 3 * height, 0, (int) (percentage * 360));
        //g.fillArc(-2 * width, -2 * height, 4 * width, 4 * height, 270, (int) (percentage * 360));
        //g.fillArc(0, 0, width, height, 0, (int) (percentage * 360));
    }

}
