package com.prplplus.osu;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class ImageChopper {
    public static String chopImage(File imageFile, int offset, boolean clearChopDir) {
        int w = 256;
        int h = 256;

        try {
            BufferedImage orig = ImageIO.read(imageFile);
            if (orig == null) {
                return imageFile.getName() + " isn't a readable image file";
            }

            int width = orig.getWidth();
            int height = orig.getHeight();

            String filename = imageFile.getName().substring(0, imageFile.getName().lastIndexOf('.'));

            File targetDir = new File(imageFile.getParentFile().getAbsolutePath() + File.separatorChar + filename + "-chopped");

            if (clearChopDir && targetDir.exists()) {
                for (File f : targetDir.listFiles()) {
                    f.delete();
                }
            }

            targetDir.mkdir();

            int index = offset;
            for (int x = 0; x < (width + w - 1) / w; x++) {
                for (int y = 0; y < (height + h - 1) / h; y++) {
                    BufferedImage tile = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                    Graphics g = tile.createGraphics();
                    g.drawImage(orig, -x * w, -y * h, null);
                    g.dispose();
                    File result = new File(String.format("%s%stile-%02d-%s.png", targetDir.getAbsoluteFile(), File.separatorChar, index, filename));
                    ImageIO.write(tile, "png", result);
                    index++;
                }
            }

            return null;
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
            return ex.getMessage() != null ? ex.getMessage() : ex.getClass().toString();
        }
    }
}
