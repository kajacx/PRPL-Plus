package com.prplplus.gui;

import static com.prplplus.Settings.MAX_SIZE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.prplplus.Clipboard;
import com.prplplus.OSValidator;
import com.prplplus.Settings;
import com.prplplus.Utils;
import com.prplplus.gui.OffsetIterable.Offset;
import com.prplplus.shipconstruct.Hull;
import com.prplplus.shipconstruct.Module;
import com.prplplus.shipconstruct.ModuleAtPosition;
import com.prplplus.shipconstruct.ShipConstructor.Ship;
import com.prplplus.shipconstruct.ShipDeconstructor;
import com.prplplus.shipconstruct.ShipExporter;

public class ShipConstructorPanel extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = -7436127974014599287L;

    private JTextField nameField;
    private JTextField designerField;
    private JTextArea descriptionField;
    private JTextField citgField;
    private JTextField statusArea;
    private JCheckBox instabuildBox;

    private Module selectedModule;
    private int selectedHull = -1;

    private int[] hullSection = new int[MAX_SIZE * MAX_SIZE];
    private List<ModuleAtPosition> modules = new ArrayList<>();

    private ShipRenderer shipRenderer;

    public ShipConstructorPanel() {
        setLayout(new BorderLayout());

        setBackground(new Color(255, 255, 196));

        add(createTopBar(), BorderLayout.NORTH);
        add(createLeftBar(), BorderLayout.WEST);
        add(shipRenderer = new ShipRenderer(), BorderLayout.CENTER);
        add(createModulesPanel(), BorderLayout.EAST);
        add(createBottomBar(), BorderLayout.SOUTH);
    }

    private int brushSizeIndex = 0;
    private int[] brushSizes = { 1, 3, 5, 9 };

    /*private static GridBagConstraints createConstraints(int posX, int posY, int width, int height) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = posX;
        c.gridy = posY;
        c.gridwidth = width;
        c.gridheight = height;
        //c.weightx = c.weighty = 1;
        //c.anchor = GridBagConstraints.CENTER;
        return c;
    }*/

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JLabel warning = new JLabel("Warning! Particle fleet was probably not designed to support uber large ships, " +
                "they just happen to work. Beware of potencial lag issues.", JLabel.CENTER);
        warning.setBackground(new Color(255, 196, 0));
        warning.setOpaque(true);
        topBar.add(warning, BorderLayout.NORTH);

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        row1.setOpaque(false);

        row1.add(new JLabel("Name:"));
        row1.add(nameField = new JTextField("My Ship", 15));

        row1.add(new JLabel("Designer:"));
        row1.add(designerField = new JTextField(15));

        row1.add(new JLabel("CITG:"));
        row1.add(citgField = new JTextField(20));

        JButton pasteCitg = new JButton("Paste");
        pasteCitg.addActionListener(e -> {
            try {
                String shipinfo = Clipboard.paste();
                Ship ship = ShipDeconstructor.deconstruct(shipinfo);
                citgField.setText(ship.CITG_ID);
            } catch (RuntimeException ex) {
                ex.printStackTrace(System.out);
                statusArea.setText("Cannot read CITG: " + ex);
            }
        });
        row1.add(pasteCitg);

        row1.add(instabuildBox = new JCheckBox("Instabuild"));
        instabuildBox.setOpaque(false);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        row2.setOpaque(false);

        row2.add(new JLabel("Description:"));
        row2.add(descriptionField = new JTextArea(2, 40));

        HullSelector hullSelector = new HullSelector();
        hullSelector.setLayout(new FlowLayout(FlowLayout.RIGHT));

        hullSelector.add(new JLabel("Brush Size:"));
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < brushSizes.length; i++) {
            int j = i;
            JRadioButton radio = new JRadioButton(brushSizes[i] + "", brushSizeIndex == i);
            radio.addActionListener(e -> brushSizeIndex = j);
            radio.setOpaque(false);
            group.add(radio);
            hullSelector.add(radio);
        }

        row2.add(hullSelector);

        topBar.add(row1, BorderLayout.CENTER);
        topBar.add(row2, BorderLayout.SOUTH);

        return topBar;
    }

    private JPanel createLeftBar() {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapper.setBackground(new Color(255, 226, 196));

        JPanel leftBar = new JPanel(new GridLayout(10, 1));
        leftBar.setOpaque(false);

        JButton moveUp = new JButton("Move Up");
        moveUp.addActionListener(e -> shipRenderer.shiftUp());
        leftBar.add(moveUp);

        JButton moveLeft = new JButton("Move Left");
        moveLeft.addActionListener(e -> shipRenderer.shiftLeft());
        leftBar.add(moveLeft);

        JButton moveRight = new JButton("Move Right");
        moveRight.addActionListener(e -> shipRenderer.shiftRight());
        leftBar.add(moveRight);

        JButton moveDown = new JButton("Move Down");
        moveDown.addActionListener(e -> shipRenderer.shiftDown());
        leftBar.add(moveDown);

        JPanel space = new JPanel();
        space.setPreferredSize(new Dimension(30, 30));
        space.setOpaque(false);
        leftBar.add(space);

        JButton saveAs = new JButton("Save as");
        saveAs.addActionListener(e -> saveAs());
        leftBar.add(saveAs);

        JButton loadFrom = new JButton("Load from");
        loadFrom.addActionListener(e -> loadFrom());
        leftBar.add(loadFrom);

        wrapper.add(leftBar);
        return wrapper;
    }

    private JPanel createModulesPanel() {
        JPanel modulesPanel = new JPanel();
        modulesPanel.setLayout(new BorderLayout());
        modulesPanel.setBackground(new Color(255, 226, 196));

        modulesPanel.add(new JLabel("Modules", JLabel.CENTER), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setPreferredSize(new Dimension(210, 300));
        tabs.setOpaque(false);

        //default modules
        JPanel defaultModules = new JPanel();
        defaultModules.setLayout(new BoxLayout(defaultModules, BoxLayout.Y_AXIS));
        defaultModules.setOpaque(false);

        JPanel weaponModules = new JPanel();
        weaponModules.setLayout(new BoxLayout(weaponModules, BoxLayout.Y_AXIS));
        weaponModules.setOpaque(false);

        for (Module m : Module.standardModules) {
            JButton button = new JButton(m.name);
            button.setIcon(getImageForModule(m));
            button.addActionListener(e -> {
                selectedModule = m;
                selectedHull = -1;
            });

            if (m.isWeapon) {
                weaponModules.add(button);
            } else {
                defaultModules.add(button);
            }
        }

        tabs.addTab("Default", defaultModules);
        tabs.addTab("Weapons", weaponModules);

        if (!Module.customModules.isEmpty()) {
            JPanel customModules = new JPanel();
            customModules.setLayout(new BoxLayout(customModules, BoxLayout.Y_AXIS));
            customModules.setOpaque(false);
            for (Module m : Module.customModules) {
                JButton button = new JButton(m.name);
                button.setIcon(getImageForModule(m));
                button.addActionListener(e -> {
                    selectedModule = m;
                    selectedHull = -1;
                });

                customModules.add(button);
            }

            tabs.addTab("Custom", customModules);
        }

        modulesPanel.add(tabs, BorderLayout.CENTER);

        return modulesPanel;
    }

    private JPanel createBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bar.setOpaque(false);

        JButton exportButton = new JButton("Export to Clipboard");
        JButton importButton = new JButton("Import from Clipboard");
        statusArea = new JTextField();

        statusArea.setColumns(30);
        //area.setEditable(false);
        exportButton.addActionListener(e -> {
            String text = new ShipExporter(hullSection, modules).exportToBase64(nameField.getText(),
                    designerField.getText(), descriptionField.getText(), citgField.getText(), instabuildBox.isSelected());
            if (!text.startsWith("Error: ")) {
                Clipboard.copy(text);
                statusArea.setText("Export successful");
            } else {
                statusArea.setText(text);
            }
        });

        importButton.addActionListener(e -> {
            String text = Clipboard.paste().trim();
            if (text == null) {
                statusArea.setText("Error: Cannot paste from clipboard");
            } else {
                try {
                    Ship imported = ShipDeconstructor.deconstruct(text);
                    importShip(imported);
                    statusArea.setText("Import successful");
                } catch (RuntimeException ex) {
                    ex.printStackTrace(System.out);
                    statusArea.setText("Error: Invalid import data");
                }
            }
        });

        bar.add(exportButton);
        bar.add(statusArea);
        bar.add(importButton);

        return bar;
    }

    private ImageIcon getImageForModule(Module m) {
        int scaleFactor = 32 / Math.max(3, Math.max(m.width, m.height));
        Image i = m.image;
        if (i == null) {
            return null;
        }
        i = i.getScaledInstance(m.width * scaleFactor, m.height * scaleFactor, Image.SCALE_DEFAULT);
        return new ImageIcon(i);
    }

    //import everything except custom modules
    private void importShip(Ship ship) {
        //erase existing ship
        for (int i = 0; i < hullSection.length; i++) {
            hullSection[i] = Hull.HULL_SPACE;
        }

        modules.clear();

        //import hull
        for (int y = 0; y < ship.height; y++) {
            for (int x = 0; x < ship.width; x++) {
                hullSection[x * MAX_SIZE + ship.height - y - 1] = ship.hull[y * ship.width + x];
            }
        }

        //import modules
        for (ModuleAtPosition m : ship.modules) {
            ModuleAtPosition module = m.copy();
            module.y = ship.height - module.y - module.module.height; //flip Y axis
            modules.add(module);
        }

        //command module
        ModuleAtPosition command = new ModuleAtPosition(ship.commandX, ship.commandY, Module.COMMAND);
        command.y = ship.height - command.y - command.module.height;
        modules.add(command);

        //set name
        nameField.setText(ship.name);

        //set description
        designerField.setText(ship.designer);
        descriptionField.setText(ship.description);
        citgField.setText(ship.CITG_ID);

        //set instabuild
        instabuildBox.setSelected(ship.instabuild);

        //and repaint
        repaint();
    }

    //imports custom modules, return true on success
    private boolean importCustomModules(Ship ship, String modules) {

        try {
            for (String moduleData : modules.split(";")) {
                if (moduleData.isEmpty())
                    continue;

                //read data
                String[] parts = moduleData.split(",");
                Module module = Module.getCustomByName(parts[0]);
                int posX = Integer.parseInt(parts[1]);
                int posY = Integer.parseInt(parts[2]);

                //flip Y axis
                posY = ship.height - posY - module.height;

                //and done
                this.modules.add(new ModuleAtPosition(posX, posY, module));
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
            return false;
        }

        return true;
    }

    private JFileChooser fileChooser = new JFileChooser();

    private void saveAs() {
        ShipExporter exporter = new ShipExporter(hullSection, modules);

        //check if export is legit
        String exportData = exporter.exportToBase64(nameField.getText(),
                designerField.getText(), descriptionField.getText(), citgField.getText(), instabuildBox.isSelected());
        if (exportData.startsWith("Error")) {
            statusArea.setText(exportData);
            return;
        }

        //select file
        int result = fileChooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        //set text to success, replace with something else on error
        statusArea.setText("Save successful");

        //save file
        File file;
        try {
            file = fileChooser.getSelectedFile();
            PrintWriter writer = new PrintWriter(file);
            writer.println(exportData);
            writer.println(exporter.exportCustomModules());
            writer.println(exporter.exportCommandLocation());
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
            statusArea.setText("Cannot save to file");
            return;
        }

        //export custom modules maker
        if (exporter.hasCustomModules()) {
            String adder = file.getParentFile().getAbsolutePath() + "/" + nameField.getText().replaceAll("\\s+", "") + "Adder.prpl";
            try {
                PrintStream stream = new PrintStream(new FileOutputStream(adder));
                exporter.writeModuleAdder(stream);
            } catch (IOException e) {
                e.printStackTrace(System.out);
                statusArea.setText("Cannot create PRPL custom module adder");
            }
        }

        //convert to .dat
        try {
            String datFile = file.getParentFile().getAbsolutePath() + "/" + nameField.getText() + ".dat";
            //System.out.println("Saving to:" + datFile);

            ProcessBuilder builder = null;

            if (OSValidator.isWindows()) {
                builder = new ProcessBuilder("GZipExporter/PRPLDatGZip.exe",
                        file.getAbsolutePath(), datFile, "-b64");
            } else if (OSValidator.isUnix() || OSValidator.isSolaris()) {
                builder = new ProcessBuilder("mono", "GZipExporter/PRPLDatGZip.exe",
                        file.getAbsolutePath(), datFile, "-b64");
            } else if (OSValidator.isMac()) {
                statusArea.setText("Conversion to .dat failed, Mac OS is not supported. Please contact me at " + MainFrame.contact);
            } else {
                statusArea.setText("Conversion to .dat failed, Unknown OS: " + OSValidator.getOS());
            }

            if (builder != null) {
                builder.redirectErrorStream(true);
                Process process = builder.start();

                Scanner scan = new Scanner(process.getInputStream());
                while (scan.hasNextLine()) {
                    String line = scan.nextLine();
                    if (!line.isEmpty()) {
                        System.out.println("Error in .dat conversion: " + line);
                        statusArea.setText("Error in .dat conversion: " + line);
                    }
                }
                scan.close();
                process.waitFor();
            }

        } catch (Exception ex) {
            ex.printStackTrace(System.out);
            statusArea.setText("Cannot convert to .dat file");
            return;
        }
    }

    private void loadFrom() {
        //select file
        int result = fileChooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Scanner scan = null;
        try {
            scan = new Scanner(fileChooser.getSelectedFile());
            try {
                String data = scan.nextLine();
                Ship ship = ShipDeconstructor.deconstruct(data);
                importShip(ship);
                statusArea.setText("Load successful");
                if (scan.hasNextLine()) {
                    if (!importCustomModules(ship, scan.nextLine())) {
                        statusArea.setText("Could not load custom modules");
                    }
                }
            } catch (RuntimeException ex) {
                ex.printStackTrace(System.out);
                statusArea.setText("Load failed. Make sure it's the base64-encoded variant");
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
            statusArea.setText("Cannot read ship from file");
        } finally {
            if (scan != null) {
                scan.close();
            }
        }
    }

    private int[] hullToDraw = { Hull.HULL_BLOCK, Hull.HULL_CORNER_LB, Hull.HULL_SPIKE_B, Hull.HULL_ARMOR_MASK, Hull.HULL_SPACE };

    private class HullSelector extends JPanel implements MouseListener {
        private static final long serialVersionUID = 5928644466757778196L;

        public HullSelector() {
            setPreferredSize(new Dimension(350, 30));
            setBackground(new Color(196, 128, 64));

            addMouseListener(this);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.black);

            for (int i = 0; i < hullToDraw.length; i++) {
                int x = 8 + i * (14 + 8);
                int y = 8;
                //g.drawRect(x - 1, y - 1, 14 + 1, 14 + 1);
                g.drawImage(Hull.hullImages[hullToDraw[i]], x, y, null);
                g.drawRect(x, y, 13, 13);
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            //System.out.println("Kappa " + e.getPoint());
            if (e.getButton() == MouseEvent.BUTTON1) {
                for (int i = 0; i < hullToDraw.length; i++) {
                    int x = 8 + i * (14 + 8);
                    int y = 8;

                    if (e.getX() >= x && e.getX() < x + 14 && e.getY() >= y && e.getY() <= y + 14) {
                        selectedHull = hullToDraw[i];
                        if (selectedHull == Hull.HULL_SPACE) {
                            selectedHull = -1;
                        }
                        selectedModule = null;
                        //System.out.println("Selected " + selectedHull);
                        break;
                    }
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

    }

    private class ShipRenderer extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {
        /**
         * 
         */
        private static final long serialVersionUID = -3801512788255149378L;
        private int posX = 5; //offset, in pixels
        private int posY = 5; //offset, in pixels

        //private boolean dragging = false;
        private int lastX, lastY; //last position of the mouse

        private Color semiTransparentBackground;

        private ShipRenderer() {
            setPreferredSize(new Dimension(640, 480));

            OffsetIterable.init(brushSizes);

            Color bgColor = new Color(128, 96, 0);
            setBackground(bgColor);
            semiTransparentBackground = new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 128);

            addMouseListener(this);
            addMouseMotionListener(this);
            addMouseWheelListener(this);
            addKeyListener(this);
            setFocusable(true);

            int startX = 10;
            int startY = 10;

            for (int i = startX; i < startX + 5; i++) {
                for (int j = startY; j < startY + 5; j++) {
                    hullSection[i * MAX_SIZE + j] = Hull.HULL_BLOCK;
                }
            }

            modules.add(new ModuleAtPosition(startX + 1, startY + 1, Module.COMMAND));

            //importShip(ShipDeconstructor.deconstruct("CgQAcm9vdAMBAHMGAFNoaXA5OekDAG1wcwIAAAAAAKBAAACAPwAAAEAAABBBAAAAQQAAAEABAgBodw0AAAABAgBoaAcAAAALAgBocFsAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABIAAAAAAAAAAAAAABIAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAUAAAAAAAAAAAAAAA4AAAALAAAACwAAAAsAAAALAAAADwAAAAAAAAAAAAAAAAAAAAYAAAABAAAAAQAAAAEAAAABAAAAAQAAAAAAAAAAAAAAAQAAAAEAAAABAAAADwAAAAAAAAAAAAAAAQAAAAEAAAABAAAAAQAAAAEAAAAAAAAAAAAAAAEAAAABAAAAAQAAAAsAAAATAAAACAAAAAEAAAABAAAAAQAAAAEAAAABAAAAAAAAAAAAAAABAAAAAQAAAAEAAAAMAAAAAAAAAAMAAAACAAAAAAAAAAAAAAADAAAAAQAAAAEAAAALAAAAAQAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAYAAAAAAAAAAAAAAAYAAAAAAAAAAAAAAAAAAAAAAAAAAQMAY214AwAAAAEDAGNteQIAAAADAgBzbgcATXkgU2hpcAA="));
        }

        private Color transparentRed = new Color(255, 0, 0, 64);

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.translate(-posX, -posY);

            //grid
            g.setColor(Color.BLACK);

            for (int i = 0; i <= MAX_SIZE; i++) {
                g.drawLine(i * zoom, 0, i * zoom, MAX_SIZE * zoom);
                g.drawLine(0, i * zoom, MAX_SIZE * zoom, i * zoom);
            }

            //35x25 original size
            if (Settings.OUTLINE_35_25_BOX) {
                g.setColor(new Color(64, 128, 255));
                g.drawRect(0, 0, 35 * zoom, 25 * zoom);
            }

            //hull
            for (int i = 0; i < MAX_SIZE; i++) {
                for (int j = 0; j < MAX_SIZE; j++) {
                    int index = i * MAX_SIZE + j;
                    if (hullSection[index] != 0) {
                        g.drawImage(Hull.hullImages[hullSection[index]], i * zoom, j * zoom, zoom, zoom, null);
                    }
                }
            }

            //modules
            for (ModuleAtPosition m : modules) {
                g.drawImage(m.module.image, m.x * zoom, m.y * zoom, m.module.width * zoom, m.module.height * zoom, null);
            }

            //selected hull
            if (selectedHull != -1) {
                ModuleAtPosition pos = tryPlace(Module.LASER);
                //if(inBounds(pos))
                int w = pos.module.width * zoom;
                int h = pos.module.height * zoom;

                int brushSize = brushSizeIndex;
                int direction = Hull.getOffsetDirection(selectedHull);

                for (Offset off : OffsetIterable.getFor(brushSize, direction)) {
                    int x = (pos.x + off.x) * zoom;
                    int y = (pos.y + off.y) * zoom;

                    //if (selectedHull != Hull.HULL_ARMOR_MASK) {
                    g.setColor(semiTransparentBackground);
                    g.fillRect(x, y, w, h);
                    //}
                    g.drawImage(Hull.hullImages[selectedHull], x, y, w, h, null);
                    g.setColor(Color.black);
                    g.drawRect(x, y, w, h);
                }
            }

            //selected module
            if (selectedModule != null) {
                //System.out.println(selectedModule);
                ModuleAtPosition pos = tryPlace(selectedModule);
                g.drawImage(pos.module.image, pos.x * zoom, pos.y * zoom, pos.module.width * zoom, pos.module.height * zoom, null);
                if (!canBePlaced(pos)) {
                    //draw a red rectangle over it
                    g.setColor(transparentRed);
                    g.fillRect(pos.x * zoom, pos.y * zoom, pos.module.width * zoom, pos.module.height * zoom);
                }
            }

            g.translate(posX, posY);
        }

        /**
         * Computed where the module would be placed,
         * according to the position of the cursor.
         * (Position read from lastX and lastY)
         * @param m Module to be placed
         * @return Module m with computed placement coordinates
         */
        private ModuleAtPosition tryPlace(Module m) {
            double xPos = lastX + posX; //pixels from 0-0 Left top corner
            xPos /= zoom; //convert to grid coords
            xPos -= m.width / 2d;

            double yPos = lastY + posY; //pixels from 0-0 Left top corner
            yPos /= zoom; //convert to grid coords
            yPos -= m.height / 2d;

            return new ModuleAtPosition((int) Math.round(xPos), (int) Math.round(yPos), m);
        }

        private void removeModuleAtMouseLocation() {
            ModuleAtPosition pos = tryPlace(Module.LASER);
            ModuleAtPosition module = null;

            for (ModuleAtPosition m : modules) {
                if (m.intersectsWith(pos, true)) {
                    module = m;
                    break;
                }
            }

            modules.remove(module);
        }

        private boolean nothingSelected() {
            return selectedModule == null && selectedHull == -1;
        }

        private boolean canBePlaced(ModuleAtPosition module) {
            return canBePlaced(module, true, true, true, false);
        }

        private boolean canBePlaced(ModuleAtPosition module, boolean checkBounds, boolean checkHull, boolean checkModules, boolean forceCollide) {
            if (checkBounds && !(module.x >= 0 &&
                    module.y >= 0 &&
                    module.x + module.module.width <= MAX_SIZE &&
                    module.y + module.module.height <= MAX_SIZE))
                return false;

            if (checkHull)
                for (int i = module.x; i < module.x + module.module.width; i++) {
                    for (int j = module.y; j < module.y + module.module.height; j++) {
                        if (hullSection[i * MAX_SIZE + j] != Hull.HULL_BLOCK) {
                            return false;
                        }
                    }
                }

            if (checkModules)
                for (ModuleAtPosition m : modules) {
                    if (m.intersectsWith(module, forceCollide))
                        return false;
                }

            return true;
        }

        private boolean shiftLeft() {
            //check hull
            int x = 0;
            for (int y = 0; y < MAX_SIZE; y++) {
                if (hullSection[x * MAX_SIZE + y] != Hull.HULL_SPACE) {
                    return false;
                }
            }

            //move hull
            for (int i = 0; i < MAX_SIZE - 1; i++) {
                for (int j = 0; j < MAX_SIZE; j++) {
                    hullSection[i * MAX_SIZE + j] = hullSection[(i + 1) * MAX_SIZE + j];
                }
            }

            //move modules
            for (ModuleAtPosition module : modules) {
                module.x -= 1;
            }

            //clear hull
            x = MAX_SIZE - 1;
            for (int y = 0; y < MAX_SIZE; y++) {
                hullSection[x * MAX_SIZE + y] = Hull.HULL_SPACE;
            }

            repaint();
            return true;
        }

        private boolean shiftRight() {
            //check hull
            int x = MAX_SIZE - 1;
            for (int y = 0; y < MAX_SIZE; y++) {
                if (hullSection[x * MAX_SIZE + y] != Hull.HULL_SPACE) {
                    return false;
                }
            }

            //move hull
            for (int i = MAX_SIZE - 1; i >= 1; i--) {
                for (int j = 0; j < MAX_SIZE; j++) {
                    hullSection[i * MAX_SIZE + j] = hullSection[(i - 1) * MAX_SIZE + j];
                }
            }

            //move modules
            for (ModuleAtPosition module : modules) {
                module.x += 1;
            }

            //clear hull
            x = 0;
            for (int y = 0; y < MAX_SIZE; y++) {
                hullSection[x * MAX_SIZE + y] = Hull.HULL_SPACE;
            }

            repaint();
            return true;
        }

        private boolean shiftUp() {
            //check hull
            int y = 0;
            for (int x = 0; x < MAX_SIZE; x++) {
                if (hullSection[x * MAX_SIZE + y] != Hull.HULL_SPACE) {
                    return false;
                }
            }

            //move hull
            for (int i = 0; i < MAX_SIZE; i++) {
                for (int j = 0; j < MAX_SIZE - 1; j++) {
                    hullSection[i * MAX_SIZE + j] = hullSection[i * MAX_SIZE + (j + 1)];
                }
            }

            //move modules
            for (ModuleAtPosition module : modules) {
                module.y -= 1;
            }

            //clear hull
            y = MAX_SIZE - 1;
            for (int x = 0; x < MAX_SIZE; x++) {
                hullSection[x * MAX_SIZE + y] = Hull.HULL_SPACE;
            }

            repaint();
            return true;
        }

        private boolean shiftDown() {
            //check hull
            int y = MAX_SIZE - 1;
            for (int x = 0; x < MAX_SIZE; x++) {
                if (hullSection[x * MAX_SIZE + y] != Hull.HULL_SPACE) {
                    return false;
                }
            }

            //move hull
            for (int i = 0; i < MAX_SIZE; i++) {
                for (int j = MAX_SIZE - 1; j >= 1; j--) {
                    hullSection[i * MAX_SIZE + j] = hullSection[i * MAX_SIZE + (j - 1)];
                }
            }

            //move modules
            for (ModuleAtPosition module : modules) {
                module.y += 1;
            }

            //clear hull
            y = 0;
            for (int x = 0; x < MAX_SIZE; x++) {
                hullSection[x * MAX_SIZE + y] = Hull.HULL_SPACE;
            }

            repaint();
            return true;
        }

        private void onHullClick(ModuleAtPosition position, int mouseButton) {
            ModuleAtPosition pos = position.copy();
            for (Offset off : OffsetIterable.getFor(brushSizeIndex, Hull.getOffsetDirection(selectedHull))) {
                pos.x = position.x + off.x;
                pos.y = position.y + off.y;

                if (canBePlaced(pos, true, false, true, true)) {
                    int index = pos.x * MAX_SIZE + pos.y;
                    if (selectedHull == Hull.HULL_ARMOR_MASK) {
                        hullSection[index] = Hull.withArmor(hullSection[index], mouseButton == MouseEvent.BUTTON1);
                    } else if (mouseButton == MouseEvent.BUTTON1) {
                        hullSection[index] = selectedHull;
                    } else {
                        hullSection[index] = Hull.HULL_SPACE;
                    }
                }
            }

        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (selectedHull != -1) { //hull placement
                ModuleAtPosition pos = tryPlace(Module.LASER);
                onHullClick(pos, e.getButton());
            } else if (e.getButton() == MouseEvent.BUTTON1) {
                if (selectedModule != null) { //module placement
                    ModuleAtPosition pos = tryPlace(selectedModule);
                    if (canBePlaced(pos)) {
                        modules.add(pos);
                    }
                }
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                if (nothingSelected()) {
                    removeModuleAtMouseLocation();
                }
                selectedModule = null;
            }

            this.repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            /*if (e.getButton() == MouseEvent.BUTTON1) {
            dragging = false;
            }*/
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void mouseDragged(MouseEvent e) {
            posX += lastX - e.getX();
            posY += lastY - e.getY();

            lastX = e.getX();
            lastY = e.getY();

            this.repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            lastX = e.getX();
            lastY = e.getY();

            this.repaint();
        }

        private void onHullWheel(MouseWheelEvent e) {
            if (e.isControlDown()) {
                processZoom(e);
            } else if (e.isShiftDown()) {
                //TODO: hull type rotation
            } else if (e.isAltDown()) {
                brushSizeIndex -= e.getWheelRotation();
                brushSizeIndex = Utils.clamp(0, brushSizes.length - 1, brushSizeIndex);
            } else {
                int rotation = e.getWheelRotation();
                if (rotation > 0) {
                    selectedHull = Hull.rotateCW(selectedHull);
                } else {
                    selectedHull = Hull.rotateCCW(selectedHull);
                }
            }
        }

        private int zoomIndex = 6;
        private int[] zoomIndexTable = { 4, 6, 8, 11, 15, 21, 29, 42 };

        private int zoom = zoomIndexTable[zoomIndex]; //number of pixels per 1 slot

        private void processZoom(MouseWheelEvent e) {
            int prevZoom = zoom;
            zoomIndex -= e.getWheelRotation();
            zoomIndex = Utils.clamp(0, zoomIndexTable.length - 1, zoomIndex);
            zoom = zoomIndexTable[zoomIndex];

            if (prevZoom != zoom) {
                //recompute posY and posY

                double relX;
                double relY;

                if (Settings.ZOOM_TO_CURSOR) {
                    relX = e.getPoint().getX();
                    relY = e.getPoint().getY();
                } else {
                    relX = getWidth() / 2;
                    relY = getHeight() / 2;
                }

                //seems wierd, but we need positive zoom ratio when zooming out
                double zoomRatio = (double) prevZoom / zoom;

                double newX = posX + relX - relX * zoomRatio;
                double newY = posY + relY - relY * zoomRatio;

                posX = (int) (newX / zoomRatio);
                posY = (int) (newY / zoomRatio);
            }
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (selectedHull != -1) {
                onHullWheel(e);
            } else {
                processZoom(e);
            }

            this.repaint();
        }

        @Override
        public void keyTyped(KeyEvent e) {
            System.out.println("Key typed: " + e.getKeyChar());
            e.consume();
        }

        @Override
        public void keyPressed(KeyEvent e) {
            System.out.println("Key pressed: " + e.getKeyChar());
            e.consume();
        }

        @Override
        public void keyReleased(KeyEvent e) {
            System.out.println("Key released: " + e.getKeyChar());
            e.consume();
        }

    }
}
