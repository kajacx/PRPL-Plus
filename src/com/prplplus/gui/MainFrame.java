package com.prplplus.gui;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class MainFrame extends JFrame {
    public static final String version = "v0.0.1";
    public static final String title = "PRPL Toolset";

    public static void main(String[] args) {
        MainFrame frame = new MainFrame();
        frame.setVisible(true);
    }

    public MainFrame() {
        super(title + " " + version);
        setLayout(new BorderLayout());
        setJMenuBar(createMenu());

        JTabbedPane tabs = new JTabbedPane();

        ImageIcon prplIcon = new ImageIcon("img/icons/prpl_plus.png");
        JPanel prplPanel = new CompilerPanel();
        tabs.addTab("PRPL+", prplIcon, prplPanel, "Compiles PRPL+ to PRPL");

        ImageIcon constIcon = new ImageIcon("img/icons/ship_constr.png");
        JPanel constPanel = new ShipConstructorPanel();
        tabs.addTab("Ship Construct", constIcon, constPanel, "Build large ships up to size 129x129");

        add(tabs, BorderLayout.CENTER);
        pack();

        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private JMenuBar createMenu() {
        JMenuBar bar = new JMenuBar();
        JMenu menu;
        JMenuItem item;


        menu = new JMenu("File");

        item = new JMenuItem("Exit");
        item.addActionListener(e -> this.dispose());
        menu.add(item);

        bar.add(menu);


        menu = new JMenu("Help");

        item = new JMenuItem("About");
        item.addActionListener(e -> JOptionPane.showMessageDialog(this, title + "\nCreated by kajacx\nVersion: " + version));
        menu.add(item);

        bar.add(menu);


        return bar;
    }
}








