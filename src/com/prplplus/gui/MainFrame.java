package com.prplplus.gui;

import java.awt.BorderLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = -3729420411039341803L;

    public static final String version = "v0.0.4";
    public static final String title = "PRPL Toolset";
    public static final String contact = "kajacx@gmail.com";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
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
        tabs.addTab("Ship Construct", constIcon, constPanel, "Build large ships up to size "
                + ShipConstructorPanel.MAX_SIZE + "x" + ShipConstructorPanel.MAX_SIZE);

        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher());

        add(tabs, BorderLayout.CENTER);
        pack();

        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
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
        String about = title + "\nCreated by kajacx\nContact: " + contact + "\nVersion: " + version;
        item.addActionListener(e -> JOptionPane.showMessageDialog(this, about));
        menu.add(item);

        item = new JMenuItem("Ship Construct Controls");
        String controls = getFileContents("ship_edit_controls.txt", "Unable to load controls file.");
        item.addActionListener(e -> JOptionPane.showMessageDialog(this, controls));
        menu.add(item);

        item = new JMenuItem("How to Import Ships");
        String howto = getFileContents("ship_how_to_import.txt", "Unable to load ship import tutorial.");
        item.addActionListener(e -> JOptionPane.showMessageDialog(this, howto));
        menu.add(item);

        item = new JMenuItem("How to use Custom Modules");
        String howto2 = getFileContents("how_to_custom_modules.txt", "Unable to load custom module tutorial.");
        item.addActionListener(e -> JOptionPane.showMessageDialog(this, howto2));
        menu.add(item);

        bar.add(menu);

        return bar;
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
