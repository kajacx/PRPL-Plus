package com.prplplus;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.prplplus.shipconstruct.Hull;

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

        for (int i = 0; i < Hull.hullImages.length; i++) {
            System.out.format("%02X -> %02X%n", i, Hull.rotateCCW(i));
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
