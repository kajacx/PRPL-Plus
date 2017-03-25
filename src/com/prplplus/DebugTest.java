package com.prplplus;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.prplplus.shipconstruct.Hull;
import com.prplplus.shipconstruct.Module;
import com.prplplus.shipconstruct.ModuleAtPosition;
import com.prplplus.shipconstruct.ShipPart;
import com.prplplus.shipconstruct.ShipPartRotator;

public class DebugTest {
    public static void main(String[] args) {
        /*SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test");
            frame.setLayout(new BorderLayout());
        
            frame.add(new MyPanel(), BorderLayout.CENTER);
        
            frame.pack();
            frame.setLocationRelativeTo(null);
        
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });*/

        /*for (int i = 0; i < Hull.hullImages.length; i++) {
            System.out.format("%02X -> %02X%n", i, Hull.rotateCCW(i));
        }*/

        ShipPart part = new ShipPart("Test6x4", 6, 4);

        for (int i = 1; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                part.getHull()[i * 4 + j] = Hull.HULL_BLOCK;
            }
        }
        part.getHull()[0] = Hull.HULL_ARMOR_CORNER_RT;
        part.getHull()[2] = Hull.HULL_ARMOR_CORNER_RB;
        part.getHull()[3] = Hull.HULL_ARMOR_CORNER_RT;
        part.getHull()[4 * 4 + 0] = Hull.HULL_SPIKE_L;
        part.getHull()[5 * 4 + 3] = Hull.HULL_SPIKE_L;
        part.getHull()[5 * 4 + 1] = Hull.HULL_ARMOR_CORNER_LB;
        part.getHull()[5 * 4 + 2] = Hull.HULL_ARMOR_CORNER_LT;

        part.getModules().add(new ModuleAtPosition(1, 0, Module.CANNON));
        part.getModules().add(new ModuleAtPosition(1, 2, Module.MISSLE_LAUNCHER));
        part.getModules().add(new ModuleAtPosition(3, 0, Module.PARTICLE_BEAM));
        part.getModules().add(new ModuleAtPosition(4, 1, Module.LASER));
        part.getModules().add(new ModuleAtPosition(4, 2, Module.LASER));
        part.getModules().add(new ModuleAtPosition(3, 3, Module.PARTICLE_BEAM));

        ShipPart rotated90CCW = ShipPartRotator.rotateCCW(part);
        ShipPart rotated90CW = ShipPartRotator.rotateCW(part);
        ShipPart rotatedHor = ShipPartRotator.flipHorizontaly(part);
        ShipPart rotatedVer = ShipPartRotator.flipVerticaly(part);

        try {
            ImageIO.write(part.createImage(), "png", new FileOutputStream("TestPart.png"));
            ImageIO.write(rotated90CCW.createImage(), "png", new FileOutputStream("TestPart90CCW.png"));
            ImageIO.write(rotated90CW.createImage(), "png", new FileOutputStream("TestPart90CW.png"));
            ImageIO.write(rotatedHor.createImage(), "png", new FileOutputStream("TestPartHor.png"));
            ImageIO.write(rotatedVer.createImage(), "png", new FileOutputStream("TestPartVer.png"));

            PrintWriter writer = new PrintWriter(new File("TestPart.txt"));
            writer.println(part.saveToB64());
            writer.close();

            writer = new PrintWriter(new File("TestPart90CCW.txt"));
            writer.println(rotated90CCW.saveToB64());
            writer.close();

            writer = new PrintWriter(new File("TestPart90CW.txt"));
            writer.println(rotated90CW.saveToB64());
            writer.close();

            writer = new PrintWriter(new File("TestPartHor.txt"));
            writer.println(rotatedHor.saveToB64());
            writer.close();

            writer = new PrintWriter(new File("TestPartVer.txt"));
            writer.println(rotatedVer.saveToB64());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }

    @SuppressWarnings("serial")
    static class MyPanel extends JPanel {
        public MyPanel() {
            setPreferredSize(new Dimension(50, 50));
            setBackground(new Color(128, 128, 255));
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(Color.black);
            g.drawRect(10, 10, 14, 14);
        }
    }
}
