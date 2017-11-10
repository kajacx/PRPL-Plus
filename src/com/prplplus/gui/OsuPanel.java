package com.prplplus.gui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.prplplus.osu.ImageChopper;
import com.prplplus.osu.OsuToPrplConverter;


public class OsuPanel extends JPanel {
    private JFileChooser imageFileChooser = new JFileChooser();
    private File chosenImageFile;

    private JFileChooser osuFileChooser = new JFileChooser();
    private File chosenOsuFile;

    private Color okColor = new Color(64, 192, 16);
    private Color errColor = new Color(255, 64, 16);

    public OsuPanel() {
        setLayout(new GridLayout(3, 1));

        imageFileChooser.setCurrentDirectory(new File("c:\\kajacx\\img\\"));
        imageFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image file", "jpg", "jpeg", "png", "bmp"));

        osuFileChooser.setCurrentDirectory(new File("c:\\kajacx\\programming\\other\\particlefleet\\509064 REOL - YoiYoi Kokon\\"));
        osuFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Osu! beatmap", "osu"));

        add(createImageChopper());
        add(createOsuConvenrter());
    }

    private JPanel createImageChopper() {
        JLabel label = new JLabel("Image chopper");
        JButton button = new JButton("Choose image");
        JLabel chosenFile = new JLabel("No file chosen");
        JButton execute = new JButton("Chop image");
        JLabel status = new JLabel("Status");

        button.addActionListener(e -> {
            if (imageFileChooser.showOpenDialog(MainFrame.instance) == JFileChooser.APPROVE_OPTION) {
                chosenImageFile = imageFileChooser.getSelectedFile();
                chosenFile.setText(chosenImageFile.getName());
            }
        });

        execute.addActionListener(e -> {
            if (chosenImageFile != null) {
                String msg = ImageChopper.chopImage(chosenImageFile, 0, true);
                if (msg == null) {
                    status.setText(chosenImageFile.getName() + " chopped OK!");
                    status.setForeground(okColor);
                } else {
                    status.setText(msg);
                    status.setForeground(errColor);
                }
            }
        });

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(label);
        panel.add(button);
        panel.add(chosenFile);
        panel.add(execute);
        panel.add(status);
        return panel;
    }

    private JPanel createOsuConvenrter() {
        JLabel label = new JLabel("Osu! converter");
        JButton button = new JButton("Choose Osu! file");
        JLabel chosenFile = new JLabel("No file chosen");
        JButton execute = new JButton("Convert");
        JLabel status = new JLabel("Status");

        button.addActionListener(e -> {
            if (osuFileChooser.showOpenDialog(MainFrame.instance) == JFileChooser.APPROVE_OPTION) {
                chosenOsuFile = osuFileChooser.getSelectedFile();
                chosenFile.setText(chosenOsuFile.getName());
            }
        });

        execute.addActionListener(e -> {
            if (chosenOsuFile != null) {
                OsuToPrplConverter converter = new OsuToPrplConverter(chosenOsuFile);
                String msg = converter.convert();
                if (msg == null) {
                    status.setText(chosenOsuFile.getName() + " converted OK!");
                    status.setForeground(okColor);
                } else {
                    status.setText(msg);
                    status.setForeground(errColor);
                }
            }
        });

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(label);
        panel.add(button);
        panel.add(chosenFile);
        panel.add(execute);
        panel.add(status);
        return panel;

    }

}
