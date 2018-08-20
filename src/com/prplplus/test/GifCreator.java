package com.prplplus.test;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GifCreator {

    static int width = 64;
    static int height = 64;
    static float min = width / 4 * 3;
    static float speed = 1.5f; //angle = speed radians per second
    static float offset = (float) (Math.PI / 6); //angle offset, in radians

    static BufferedImage image1;
    static BufferedImage image2;

    public static void main(String[] args) throws Exception {
        image1 = ImageIO.read(new File("c:\\kajacx\\img\\part_fleet\\gif\\emitter1.png"));
        image2 = ImageIO.read(new File("c:\\kajacx\\img\\part_fleet\\gif\\emitter2.png"));

        for (int i = 0; i < 150; i++) {
            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bi.createGraphics();
            paintInto(g, i / 150f * (float) Math.PI * 2);
            g.dispose();
            ImageIO.write(bi, "png", new File("c:\\kajacx\\img\\part_fleet\\gif\\renderT\\" + String.format("emitter%03d.png", i)));
        }

        JFrame frame = new JFrame("Image Test");
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.BLUE);

        frame.add(new MyGifPanel(), BorderLayout.CENTER);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SwingUtilities.invokeAndWait(() -> {
            frame.setVisible(true);
        });

        while (true) {
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace(System.out);
            }
            SwingUtilities.invokeAndWait(() -> {
                frame.repaint();
            });
        } //*/
    }

    public static void paintInto(Graphics2D g, float angle) {
        //g.setColor(Color.blue);
        //g.drawRect((int) secondsElapsed, 0, 20, 20);

        g.setComposite(AlphaComposite.Clear);
        //g.setColor(Color.blue);
        g.fillRect(0, 0, width, height);
        g.setComposite(AlphaComposite.SrcOver);
        /*g.setColor(new Color(16, 0, 64));
        g.fillRect(0, 0, width, height);*/

        float w = 14 + (float) (Math.cos(angle) / 2 * (width - min) + min);
        AffineTransform t = new AffineTransform();
        t.translate((width - w) / 2, (width - w) / 2); // x/y set here, ball.x/y = double, ie: 10.33
        t.scale(w / width / 1, w / width / 1); // scale = 1 
        g.drawImage(image1, t, null);

        float w2 = 14 + (float) (Math.cos(angle + offset) / 2 * (width - min) + min);
        AffineTransform t2 = new AffineTransform();
        t2.translate((width - w2) / 2, (width - w2) / 2); // x/y set here, ball.x/y = double, ie: 10.33
        t2.scale(w2 / width / 1, w2 / width / 1); // scale = 1 
        g.drawImage(image2, t2, null);
    }

    public static class MyGifPanel extends JPanel {
        private Dimension size = new Dimension(width, height);
        long start = 0;

        @Override
        public Dimension getPreferredSize() {
            return size;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);

            long elapsed;
            if (start == 0) {
                start = System.currentTimeMillis();
                elapsed = 0;
            } else {
                elapsed = System.currentTimeMillis() - start;
            }

            paintInto((Graphics2D) g, (elapsed / 1000f) * speed);
        }
    }

}
