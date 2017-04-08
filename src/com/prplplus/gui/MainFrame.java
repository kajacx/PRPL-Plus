package com.prplplus.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import com.prplplus.OSValidator;
import com.prplplus.Settings;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = -3729420411039341803L;

    public static final String version = "v0.2.1";
    public static final String title = "PRPL Toolset";
    public static final String contact = "kajacx@gmail.com";

    private static boolean openShipBuild = true;

    public static void main(String[] args) {
        processArgs(args);

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }

    private static void processArgs(String[] args) {
        for (String arg : args) {
            if (arg.equalsIgnoreCase("-forceExport")) {
                Settings.enableForceExport = true;
            }
            if (arg.equalsIgnoreCase("-shipBuild")) {
                openShipBuild = true;
            }
        }
    }

    public MainFrame() {
        super(title + " " + version);
        setLayout(new BorderLayout());
        setJMenuBar(createMenu());

        ImageIcon icon = new ImageIcon("img/icons/main_icon.png");
        setIconImage(icon.getImage());

        JTabbedPane tabs = new JTabbedPane();

        ImageIcon prplIcon = new ImageIcon("img/icons/prpl_plus.png");
        CompilerPanel prplPanel = new CompilerPanel();
        tabs.addTab("PRPL+", prplIcon, prplPanel, "Compiles PRPL+ to PRPL");

        ImageIcon constIcon = new ImageIcon("img/icons/ship_constr.png");
        ShipConstructorPanel constPanel = new ShipConstructorPanel();
        tabs.addTab("Ship Construct", constIcon, constPanel, "Build large ships up to size "
                + Settings.MAX_SIZE + "x" + Settings.MAX_SIZE);

        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher());

        add(tabs, BorderLayout.CENTER);
        pack();

        if (!OSValidator.isMac()) {
            //don't use this hack on mac, it causes null pointer exception
            constPanel.modulesTabs.setPreferredSize(
                    new Dimension(ShipConstructorPanel.getTabsWidth(constPanel.modulesTabs), 300));
            pack(); //need to call pack() twice because of broken tab pane
        }

        if (openShipBuild) {
            tabs.setSelectedIndex(1);
        }

        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private JMenuBar createMenu() {
        JMenuBar bar = new JMenuBar();
        JMenu menu;
        JMenuItem item;

        menu = new JMenu("File");

        item = new JMenuItem("Exit");
        item.addActionListener(e -> {
            System.exit(0);
        });
        menu.add(item);

        bar.add(menu);

        menu = new JMenu("Help");

        item = new JMenuItem("About");
        String about = title + "\nCreated by kajacx\nContact: " + contact + "\nVersion: " + version;
        item.addActionListener(e -> JOptionPane.showMessageDialog(this, about));
        menu.add(item);

        item = new JMenuItem("PRPL+ forum post");
        item.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://knucklecracker.com/forums/index.php?topic=22809.0"));
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(this, "Cannot open the forum link. Please read the offline capture instead.");
            }
        });
        menu.add(item);

        item = new JMenuItem("Ship Construct Controls 1");
        String controls1 = getFileContents("tutorials/ship_edit_controls1.txt", "Unable to load controls file.");
        item.addActionListener(e -> JOptionPane.showMessageDialog(this, controls1));
        menu.add(item);

        item = new JMenuItem("Ship Construct Controls 2");
        String controls2 = getFileContents("tutorials/ship_edit_controls2.txt", "Unable to load controls file.");
        item.addActionListener(e -> JOptionPane.showMessageDialog(this, controls2));
        menu.add(item);

        item = new JMenuItem("How to Import Ships");
        String howto = getFileContents("tutorials/ship_how_to_import.txt", "Unable to load ship import tutorial.");
        item.addActionListener(e -> JOptionPane.showMessageDialog(this, howto));
        menu.add(item);

        item = new JMenuItem("How to use Custom Modules");
        String howto2 = getFileContents("tutorials/how_to_custom_modules.txt", "Unable to load custom module tutorial.");
        item.addActionListener(e -> JOptionPane.showMessageDialog(this, howto2));
        menu.add(item);

        bar.add(menu);

        return bar;
    }

    private String getFileContents(String fileName, String orElse) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append(System.lineSeparator());
            }
            return buffer.toString();
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
            return orElse;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace(System.out);
            }
        }
    }

    private static class MyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ALT) {
                e.consume();
            }

            /*if (e.getID() == KeyEvent.KEY_PRESSED) {
                System.out.println("tester");
            } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                System.out.println("2test2");
            } else if (e.getID() == KeyEvent.KEY_TYPED) {
                System.out.println("3test3");
            }*/
            return false;
        }
    }
}
