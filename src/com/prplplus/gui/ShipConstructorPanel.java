package com.prplplus.gui;

import static com.prplplus.Settings.MAX_SIZE;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
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

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.prplplus.Clipboard;
import com.prplplus.OSValidator;
import com.prplplus.Settings;
import com.prplplus.Utils;
import com.prplplus.files.FileManager;
import com.prplplus.gui.OffsetIterable.Offset;
import com.prplplus.shipconstruct.Hull;
import com.prplplus.shipconstruct.MirrorManager;
import com.prplplus.shipconstruct.MirrorManager.MirroredHullBrush;
import com.prplplus.shipconstruct.Module;
import com.prplplus.shipconstruct.ModuleAtPosition;
import com.prplplus.shipconstruct.ShipConstructor.Ship;
import com.prplplus.shipconstruct.ShipDeconstructor;
import com.prplplus.shipconstruct.ShipExporter;
import com.prplplus.shipconstruct.parts.PartAtPosition;
import com.prplplus.shipconstruct.parts.PartSelection;
import com.prplplus.shipconstruct.parts.ShipPart;
import com.prplplus.shipconstruct.parts.SquareIsomorph;

public class ShipConstructorPanel extends JPanel {
    private static final long serialVersionUID = -7436127974014599287L;

    public JTabbedPane modulesTabs; //an ugly workaround around tabs width bug

    private JTextField nameField;
    private JTextField designerField;
    private JTextArea descriptionField;
    private JTextField citgField;
    private JTextField statusArea;
    private JCheckBox instabuildBox;

    private Module selectedModule = null;
    private int selectedHull = -1;
    private int selectedMirror = MirrorManager.MIRROR_NONE;
    private ShipPart selectedPart = null;

    private MirrorManager mirrorManager = new MirrorManager();
    private PartSelection partSelection = new PartSelection();
    private List<ShipPart> avaliableParts = new ArrayList<>();

    private int[] hullSection = new int[MAX_SIZE * MAX_SIZE];
    private List<ModuleAtPosition> modules = new ArrayList<>();

    private ShipRenderer shipRenderer;

    private JFileChooser fileChooser = new JFileChooser();

    public ShipConstructorPanel() {
        setLayout(new BorderLayout());

        setBackground(new Color(255, 255, 196));
        shipRenderer = new ShipRenderer();

        add(createTopBar(), BorderLayout.NORTH);
        add(createLeftBar(), BorderLayout.WEST);
        add(shipRenderer, BorderLayout.CENTER);
        add(createModulesPanel(), BorderLayout.EAST);
        add(createBottomBar(), BorderLayout.SOUTH);

        fileChooser.setCurrentDirectory(new File(Settings.WORK_IN + "/ships/"));
        FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("Text files (*.txt)", "txt");
        // add filters
        fileChooser.addChoosableFileFilter(txtFilter);
        fileChooser.setFileFilter(txtFilter);
    }

    private int brushSizeIndex = 0;
    private int[] brushSizes = { 1, 3, 5, 9 };

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

    private class PartButton extends JPanel implements MouseListener {
        private static final long serialVersionUID = -5869951129835489796L;

        private Dimension size;
        private ShipPart part;
        private Image image;

        private int drawX, drawY;
        private int drawW, drawH;

        private PartButton(int width, int height, ShipPart part, Image image) {
            size = new Dimension(width, height);
            this.part = part;
            this.image = image;

            setOpaque(true);
            setBackground(shipRenderer.getBackground());

            int imageW = image.getWidth(null);
            int imageH = image.getHeight(null);

            if (imageW * height >= imageH * width) {
                drawW = width;
                drawH = imageH * width / imageW;
                drawX = 0;
                drawY = (height - drawH) / 2;
            } else {
                drawW = imageW * height / imageH;
                drawH = height;
                drawX = (width - drawW) / 2;
                drawY = 0;
            }

            addMouseListener(this);
        }

        @Override
        public Dimension getPreferredSize() {
            return size;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, drawX, drawY, drawW, drawH, null);
            g.setColor(Color.BLACK);
            g.drawRect(0, 0, size.width - 1, size.height - 1);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            ShipPart selectedPart = ShipConstructorPanel.this.selectedPart;
            unselectAll();
            if (part != selectedPart) {
                ShipConstructorPanel.this.selectedPart = part;
                ShipConstructorPanel.this.selectedPart.getRotator().reset();
            }
            ShipConstructorPanel.this.repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }
    }

    private JPanel createPartsBar() {
        JPanel partsPanel = new JPanel();
        //parts.setLayout(new GridLayout(15, 1));
        partsPanel.setLayout(new BoxLayout(partsPanel, BoxLayout.Y_AXIS));
        partsPanel.setOpaque(false);

        partsPanel.add(new JLabel("Ship parts"));
        partsPanel.add(Utils.createSpace(10, 10));

        JPanel partsList = new JPanel();
        partsList.setLayout(new BoxLayout(partsList, BoxLayout.Y_AXIS));

        List<ShipPart> parts = FileManager.loadShipParts();
        for (int i = 0; i < parts.size(); i++) {
            if (i != 0) {
                partsList.add(Utils.createSpace(10, 10));
            }
            ShipPart part = parts.get(i);
            partsList.add(new JLabel(part.getName()));
            partsList.add(new PartButton(80, 60, part, part.image));
            avaliableParts.add(part);
        }

        partsList.setAutoscrolls(true);

        JScrollPane scrollPane = new JScrollPane(partsList);
        scrollPane.setPreferredSize(new Dimension(80, 400));
        partsPanel.add(scrollPane);

        JButton selectPart = new JButton("Select");
        selectPart.addActionListener(e -> {
            unselectAll();
            partSelection.state = PartSelection.STATE_READY;
            repaint();
        });
        partsPanel.add(selectPart);

        JButton savePart = new JButton("Save Part");
        savePart.addActionListener(e -> {
            if (partSelection.state == PartSelection.STATE_SELECTED) {
                ShipPart part = partSelection.doSelectPart(hullSection, modules);
                if (part != null) {
                    String name = JOptionPane.showInputDialog(this, "Choose a name for the part");
                    if (name != null) {
                        part.setName(name);
                        savePart(part);

                        partsList.add(Utils.createSpace(10, 10));
                        partsList.add(new JLabel(part.getName()));
                        partsList.add(new PartButton(80, 60, part, part.image));
                        avaliableParts.add(part);

                        unselectAll();
                        selectedPart = part;

                        this.revalidate();
                        this.repaint();
                    }
                }
            }
        });
        partsPanel.add(savePart);

        JButton cancelPart = new JButton("Cancel");
        cancelPart.addActionListener(e -> {
            unselectAll();
            repaint();
        });
        partsPanel.add(cancelPart);

        return partsPanel;
    }

    private JPanel createLeftBar() {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapper.setBackground(new Color(255, 226, 196));
        wrapper.add(createPartsBar());

        JPanel leftBar = new JPanel(new GridLayout(15, 1));
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
        space.setPreferredSize(new Dimension(20, 20));
        space.setOpaque(false);
        leftBar.add(space);

        JButton saveAs = new JButton("Save as");
        saveAs.addActionListener(e -> saveAs(false));
        leftBar.add(saveAs);

        if (Settings.enableForceExport) {
            JButton forceExport = new JButton("Force export");
            forceExport.addActionListener(e -> saveAs(true));
            leftBar.add(forceExport);
        }

        JButton loadFrom = new JButton("Load from");
        loadFrom.addActionListener(e -> loadFrom());
        leftBar.add(loadFrom);

        space = new JPanel();
        space.setPreferredSize(new Dimension(20, 20));
        space.setOpaque(false);
        leftBar.add(space);

        JButton resetCamera = new JButton("Reset camera");
        resetCamera.addActionListener(e -> {
            shipRenderer.posX = 0;
            shipRenderer.posY = 0;
            shipRenderer.repaint();
        });
        leftBar.add(resetCamera);

        space = new JPanel();
        space.setPreferredSize(new Dimension(20, 20));
        space.setOpaque(false);
        leftBar.add(space);

        JButton horizontalMirror = new JButton("Horizontal Mirror");
        horizontalMirror.addActionListener(e -> mirrorButtonPressed(MirrorManager.MIRROR_HORIZONTAL));
        leftBar.add(horizontalMirror);

        JButton verticalMirror = new JButton("Vertical Mirror");
        verticalMirror.addActionListener(e -> mirrorButtonPressed(MirrorManager.MIRROR_VERTICAL));
        leftBar.add(verticalMirror);

        JButton rotationMirror = new JButton("Rotation Mirror");
        rotationMirror.addActionListener(e -> mirrorButtonPressed(MirrorManager.MIRROR_ROTATION));
        leftBar.add(rotationMirror);

        wrapper.add(leftBar);
        return wrapper;
    }

    public static int getTabsWidth(JTabbedPane pane) {
        int width = 0;

        for (int i = 0; i < pane.getTabCount(); i++) {
            width += pane.getUI().getTabBounds(pane, i).width;
            //System.out.println(pane.getUI().getTabBounds(pane, i));
        }

        return width;
    }

    private JPanel createModulesPanel() {
        JPanel modulesPanel = new JPanel();
        modulesPanel.setLayout(new BorderLayout());
        modulesPanel.setBackground(new Color(255, 226, 196));

        modulesPanel.add(new JLabel("Modules", JLabel.CENTER), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        //tabs.setPreferredSize(new Dimension(210, 300));
        modulesTabs = tabs;
        tabs.setOpaque(false);

        //default modules
        JPanel defaultModules = new JPanel();
        defaultModules.setLayout(new BoxLayout(defaultModules, BoxLayout.Y_AXIS));
        defaultModules.setOpaque(false);

        JPanel weaponModules = new JPanel();
        weaponModules.setLayout(new BoxLayout(weaponModules, BoxLayout.Y_AXIS));
        weaponModules.setOpaque(false);

        for (Module m : Module.defaultModules) {
            JButton button = new JButton(m.name);
            button.setIcon(getImageForModule(m));
            button.addActionListener(e -> {
                unselectAll();
                selectedModule = m;
                shipRenderer.repaint();
            });
            defaultModules.add(button);
        }

        for (Module m : Module.weaponModules) {
            JButton button = new JButton(m.name);
            button.setIcon(getImageForModule(m));
            button.addActionListener(e -> {
                selectedModule = m;
                selectedHull = -1;
                selectedMirror = MirrorManager.MIRROR_NONE;
                shipRenderer.repaint();
            });
            weaponModules.add(button);
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

    private void unselectAll() {
        selectedModule = null;
        selectedHull = -1;
        selectedMirror = MirrorManager.MIRROR_NONE;
        selectedPart = null;
        partSelection.state = PartSelection.STATE_IDLE;
    }

    private void mirrorButtonPressed(int mirror) {
        int selectedMirror0 = this.selectedMirror;
        unselectAll();
        this.selectedMirror = selectedMirror0;

        if (selectedMirror == mirror) {
            selectedMirror = MirrorManager.MIRROR_NONE;
        } else if (selectedMirror != MirrorManager.MIRROR_NONE) {
            selectedMirror = mirror;
        } else {

            //move/remove mirror
            switch (mirror) {
            case MirrorManager.MIRROR_HORIZONTAL:
                if (mirrorManager.hasHorizontalMirror() && mirrorManager.getHorizontalMirror() % 2 == 1) {
                    mirrorManager.setHorizontalMirror(mirrorManager.getHorizontalMirror() & ~1);
                } else if (mirrorManager.hasHorizontalMirror()) {
                    mirrorManager.clearHorizontalMirror();
                } else {
                    selectedMirror = MirrorManager.MIRROR_HORIZONTAL;
                }
                break;
            case MirrorManager.MIRROR_VERTICAL:
                if (mirrorManager.hasVerticalMirror() && mirrorManager.getVerticalMirror() % 2 == 1) {
                    mirrorManager.setVerticalMirror(mirrorManager.getVerticalMirror() & ~1);
                } else if (mirrorManager.hasVerticalMirror()) {
                    mirrorManager.clearVerticalMirror();
                } else {
                    selectedMirror = MirrorManager.MIRROR_VERTICAL;
                }
                break;
            case MirrorManager.MIRROR_ROTATION:
                if (mirrorManager.hasRotationMirror() && mirrorManager.getRotationMirrorX() % 2 == 1) {
                    mirrorManager.setRotationMirror(mirrorManager.getRotationMirrorX() & ~1,
                            mirrorManager.getRotationMirrorY() & ~1);
                } else if (mirrorManager.hasRotationMirror()) {
                    mirrorManager.clearRotationMirror();
                } else {
                    selectedMirror = MirrorManager.MIRROR_ROTATION;
                }
                break;
            }
        }

        shipRenderer.repaint();
    }

    //import everything except custom modules
    private void importShip(Ship ship) {
        //erase existing ship
        for (int i = 0; i < hullSection.length; i++) {
            hullSection[i] = Hull.HULL_SPACE;
        }

        modules.clear();

        //import hull
        int offsetX = Math.min(10, (MAX_SIZE - ship.width) / 2);
        int offsetY = Math.min(10, (MAX_SIZE - ship.height) / 2);

        for (int y = 0; y < ship.height; y++) {
            for (int x = 0; x < ship.width; x++) {
                hullSection[(x + offsetX) * MAX_SIZE + ship.height - y + offsetY - 1] = ship.hull[y * ship.width + x];
            }
        }

        //import modules
        for (ModuleAtPosition m : ship.modules) {
            ModuleAtPosition module = m.copy();
            module.y = ship.height - module.y - module.module.height; //flip Y axis
            module.x += offsetX;
            module.y += offsetY;
            modules.add(module);
        }

        //command module
        ModuleAtPosition command = new ModuleAtPosition(ship.commandX, ship.commandY, Module.COMMAND);
        command.y = ship.height - command.y - command.module.height; //flip Y
        command.x += offsetX;
        command.y += offsetY;
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
            int offsetX = Math.min(10, (MAX_SIZE - ship.width) / 2);
            int offsetY = Math.min(10, (MAX_SIZE - ship.height) / 2);

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
                posX += offsetX;
                posY += offsetY;

                //and done
                this.modules.add(new ModuleAtPosition(posX, posY, module));
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
            return false;
        }

        return true;
    }

    private void saveAs(boolean forceExport) {
        ShipExporter exporter = new ShipExporter(hullSection, modules);

        //check if export is legit
        String exportData;

        if (partSelection.state == PartSelection.STATE_SELECTED) {
            exportData = exporter.exportToBase64(nameField.getText(),
                    designerField.getText(), descriptionField.getText(), citgField.getText(), instabuildBox.isSelected(),
                    partSelection.fromXs, partSelection.fromYs, partSelection.toXs - 1, partSelection.toYs - 1);
        } else {
            exportData = exporter.exportToBase64(nameField.getText(),
                    designerField.getText(), descriptionField.getText(), citgField.getText(), instabuildBox.isSelected());
        }
        if (exportData.startsWith("Error")) {
            statusArea.setText(exportData);
            return;
        }

        //select file
        File nowSelected = fileChooser.getSelectedFile();
        if (nowSelected == null) {
            fileChooser.setSelectedFile(new File(Settings.WORK_IN + "/ships/" + nameField.getText() + ".txt"));
        }
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

            if (fileChooser.getFileFilter() != fileChooser.getAcceptAllFileFilter()
                    && !file.getName().contains(".")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }

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
                builder = new ProcessBuilder("mono", "GZipExporter/PRPLDatGZip.exe",
                        file.getAbsolutePath(), datFile, "-b64");
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
                if (ship.designer.equals(ShipPart.designer)) {
                    statusArea.setText("Cannot import a part as a ship");
                } else {
                    importShip(ship);
                    statusArea.setText("Load successful");
                    if (scan.hasNextLine()) {
                        if (!importCustomModules(ship, scan.nextLine())) {
                            statusArea.setText("Could not load custom modules");
                        }
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

    private void savePart(ShipPart part) {
        String fileNameNoExt = FileManager.getSipPartsFolder() + "/" + part.getName();
        File partFile = new File(fileNameNoExt + ".txt");

        try (PrintWriter writer = new PrintWriter(partFile)) {
            String b64 = part.saveToB64();
            writer.println(b64);

            for (ModuleAtPosition pos : part.getModules()) {
                if (pos.module.isCustom()) {
                    writer.print(pos.module.name);
                    writer.print(",");
                    writer.print(pos.x);
                    writer.print(",");
                    writer.print(pos.y);
                    writer.print(";");
                }
            }
            writer.println();

            ImageIO.write(part.image, "png", new File(fileNameNoExt + ".png"));

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace(System.out);
        }
    }

    private int[] hullToDraw = { Hull.HULL_BLOCK, Hull.HULL_CORNER_LB, Hull.HULL_SPIKE_B,
            Hull.HULL_ARMOR_MASK, Hull.HULL_MODULE_REMOVAL, Hull.HULL_SPACE };

    private class HullSelector extends JPanel implements MouseListener {
        private static final long serialVersionUID = 5928644466757778196L;

        public HullSelector() {
            setPreferredSize(new Dimension(390, 30));
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
                        unselectAll();
                        selectedHull = hullToDraw[i];
                        if (selectedHull == Hull.HULL_SPACE) {
                            selectedHull = -1;
                        }
                        shipRenderer.repaint();
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

        private void paintModulePreview(Graphics g, ModuleAtPosition pos) {
            g.drawImage(pos.module.image, pos.x * zoom, pos.y * zoom, pos.module.width * zoom, pos.module.height * zoom, null);
            if (!canBePlaced(pos)) {
                //draw a red rectangle over it
                g.setColor(transparentRed);
                g.fillRect(pos.x * zoom, pos.y * zoom, pos.module.width * zoom, pos.module.height * zoom);
            }
        }

        private void paintShipPart(Graphics g, PartAtPosition part) {
            for (int i = 0; i < part.part.getWidth(); i++) {
                for (int j = 0; j < part.part.getHeight(); j++) {
                    int index = i * part.part.getHeight() + j;
                    g.drawImage(Hull.hullImages[part.part.getHull()[index]], (i + part.x) * zoom, (j + part.y) * zoom, zoom, zoom, null);
                }
            }

            for (ModuleAtPosition m : part.part.getModules()) {
                if (!part.part.isRotated90() || m.module.isSquare()) {
                    g.drawImage(m.module.image, (m.x + part.x) * zoom, (m.y + part.y) * zoom, m.module.width * zoom, m.module.height * zoom, null);
                }
            }
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            //TODO: paintComponent bookmark
            int zoom = this.zoom; //make a local cache for that efficiency value

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
                ModuleAtPosition pos = tryPlace(Module.BRUSH_1X1);
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

                if (mirrorManager.hasAnyMirror()) {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                    List<MirroredHullBrush> mirrored = mirrorManager.getMirroredHull(
                            pos, selectedHull, brushSizes[brushSizeIndex]);
                    for (MirroredHullBrush mirror : mirrored) {
                        direction = Hull.getOffsetDirection(mirror.hullPiece);
                        for (Offset off : OffsetIterable.getFor(brushSize, direction)) {
                            int x = (mirror.x + off.x) * zoom;
                            int y = (mirror.y + off.y) * zoom;

                            //if (selectedHull != Hull.HULL_ARMOR_MASK) {
                            g.setColor(semiTransparentBackground);
                            g.fillRect(x, y, w, h);
                            //}
                            g.drawImage(Hull.hullImages[mirror.hullPiece], x, y, w, h, null);
                            g.setColor(Color.black);
                            g.drawRect(x, y, w, h);
                        }
                    }
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                }
            }

            //selected module
            if (selectedModule != null) {
                //System.out.println(selectedModule);
                ModuleAtPosition pos = tryPlace(selectedModule);
                paintModulePreview(g, pos);

                if (mirrorManager.hasAnyMirror()) {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                    List<? extends ModuleAtPosition> mirroredModules = mirrorManager.getMirroredModules(pos);
                    for (ModuleAtPosition mirrored : mirroredModules) {
                        paintModulePreview(g, mirrored);
                    }
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                }
            }

            //parts
            if (selectedPart != null) {
                ModuleAtPosition pos = tryPlace(selectedPart.getPlacementBrush());
                PartAtPosition partPos = new PartAtPosition(pos.x, pos.y, selectedPart);
                paintShipPart(g, partPos);

                if (mirrorManager.hasAnyMirror()) {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                    List<PartAtPosition> mirroredParts = mirrorManager.getMirroredParts(pos, selectedPart.getRotator());
                    for (PartAtPosition part : mirroredParts) {
                        paintShipPart(g, part);
                    }
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                }
            }

            //mirrors
            if (mirrorManager.hasHorizontalMirror()) {
                g.setColor(Color.GREEN);
                int posY = mirrorManager.getHorizontalMirror() * zoom / 2;
                g.drawLine(0, posY, zoom * MAX_SIZE, posY);
            }
            if (mirrorManager.hasVerticalMirror()) {
                g.setColor(Color.GREEN);
                int posX = mirrorManager.getVerticalMirror() * zoom / 2;
                g.drawLine(posX, 0, posX, zoom * MAX_SIZE);
            }
            if (mirrorManager.hasRotationMirror()) {
                g.setColor(Color.CYAN);
                int posX = mirrorManager.getRotationMirrorX() * zoom / 2;
                g.drawLine(posX, 0, posX, zoom * MAX_SIZE);
                int posY = mirrorManager.getRotationMirrorY() * zoom / 2;
                g.drawLine(0, posY, zoom * MAX_SIZE, posY);
                g.fillOval(posX - zoom / 4, posY - zoom / 4, zoom / 2, zoom / 2);
            }

            //selectedMirror
            if (selectedMirror == MirrorManager.MIRROR_HORIZONTAL) {
                ModuleAtPosition pos = tryPlace(Module.BRUSH_1X1);
                g.setColor(Color.ORANGE);
                int posY = pos.y * zoom + zoom / 2;
                g.drawLine(0, posY, zoom * MAX_SIZE, posY);
            }
            if (selectedMirror == MirrorManager.MIRROR_VERTICAL) {
                ModuleAtPosition pos = tryPlace(Module.BRUSH_1X1);
                g.setColor(Color.ORANGE);
                int posX = pos.x * zoom + zoom / 2;
                g.drawLine(posX, 0, posX, zoom * MAX_SIZE);
            }
            if (selectedMirror == MirrorManager.MIRROR_ROTATION) {
                ModuleAtPosition pos = tryPlace(Module.BRUSH_1X1);
                g.setColor(Color.ORANGE);
                int posX = pos.x * zoom + zoom / 2;
                g.drawLine(posX, 0, posX, zoom * MAX_SIZE);
                int posY = pos.y * zoom + zoom / 2;
                g.drawLine(0, posY, zoom * MAX_SIZE, posY);
                g.fillOval(posX - zoom / 4, posY - zoom / 4, zoom / 2, zoom / 2);
            }

            //selection cursor
            if (partSelection.state == PartSelection.STATE_READY) {
                ModuleAtPosition pos = tryPlace(Module.BRUSH_2X2);

                //g.drawImage(FileManager.getSelectionCursor(), zoom * pos.x + zoom / 2, zoom * pos.y + zoom / 2, zoom, zoom, null);

                g.setColor(Color.RED);
                g.drawLine(zoom * pos.x + zoom / 2, zoom * pos.y + zoom, zoom * (pos.x + 1) + zoom / 2, zoom * pos.y + zoom);
                g.drawLine(zoom * pos.x + zoom, zoom * pos.y + zoom / 2, zoom * pos.x + zoom, zoom * (pos.y + 1) + zoom / 2);
            } else if (partSelection.state == PartSelection.STATE_SELECTING || partSelection.state == PartSelection.STATE_SELECTED) {
                g.setColor(Color.RED);
                g.drawRect(partSelection.fromXs * zoom, partSelection.fromYs * zoom,
                        (partSelection.toXs - partSelection.fromXs) * zoom, (partSelection.toYs - partSelection.fromYs) * zoom);
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
            ModuleAtPosition pos = tryPlace(Module.BRUSH_1X1);
            ModuleAtPosition module = null;

            for (ModuleAtPosition m : modules) {
                if (m.intersectsWith(pos, true)) {
                    module = m;
                    //break; lose a bit of efficiency, but remove the last module found
                }
            }

            if (module != null) {
                modules.remove(module);
            }
        }

        private boolean nothingSelected() {
            return selectedModule == null
                    && selectedHull == -1
                    && selectedMirror == MirrorManager.MIRROR_NONE
                    && selectedPart == null
                    && partSelection.state == PartSelection.STATE_IDLE;
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

        private void removeColliding(ModuleAtPosition module) {
            for (int i = 0; i < modules.size(); i++) {
                if (modules.get(i).intersectsWith(module, true)) {
                    modules.remove(i);
                    i--;
                }
            }
        }

        private void shiftMirrors(int xShift, int yShift) {
            if (mirrorManager.hasHorizontalMirror()) {
                mirrorManager.setHorizontalMirror(mirrorManager.getHorizontalMirror() + 2 * yShift);
            }
            if (mirrorManager.hasVerticalMirror()) {
                mirrorManager.setVerticalMirror(mirrorManager.getVerticalMirror() + 2 * xShift);
            }
            if (mirrorManager.hasRotationMirror()) {
                mirrorManager.setRotationMirror(mirrorManager.getRotationMirrorX() + 2 * xShift,
                        mirrorManager.getRotationMirrorY() + 2 * yShift);
            }
            mirrorManager.fixMirrors(MAX_SIZE);
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

            //move (and remove) mirrors
            shiftMirrors(-1, 0);

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

            //move (and remove) mirrors
            shiftMirrors(1, 0);

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

            //move (and remove) mirrors
            shiftMirrors(0, -1);

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

            //move (and remove) mirrors
            shiftMirrors(0, 1);

            repaint();
            return true;
        }

        private void fillHull(int x, int y, MouseEvent e, int hullType) {
            ModuleAtPosition pos = new ModuleAtPosition(x, y, Module.BRUSH_1X1);
            boolean isLeftButton = SwingUtilities.isLeftMouseButton(e);
            boolean processOffsets = true;
            if (hullType == Hull.HULL_MODULE_REMOVAL) {
                int brushSize = brushSizes[brushSizeIndex];
                Module brush = Module.brushManager.getBrush(brushSize, brushSize);
                removeColliding(new ModuleAtPosition(x - brush.width / 2, y - brush.height / 2, brush));
                processOffsets = !isLeftButton;
            }
            if (processOffsets) {
                for (Offset off : OffsetIterable.getFor(brushSizeIndex, Hull.getOffsetDirection(hullType))) {
                    pos.x = x + off.x;
                    pos.y = y + off.y;

                    if (canBePlaced(pos, true, false, true, true)) {
                        int index = pos.x * MAX_SIZE + pos.y;
                        if (hullType == Hull.HULL_ARMOR_MASK) {
                            hullSection[index] = Hull.withArmor(hullSection[index], isLeftButton);
                        } else if (isLeftButton) {
                            hullSection[index] = hullType;
                        } else {
                            hullSection[index] = Hull.HULL_SPACE;
                        }
                    }
                }
            }
        }

        private void onHullClick(ModuleAtPosition position, MouseEvent e) {
            if (e.isControlDown()) {
                //move camera instead
                return;
            }

            fillHull(position.x, position.y, e, selectedHull);

            if (!e.isShiftDown() && mirrorManager.hasAnyMirror()) {
                for (MirroredHullBrush mirror : mirrorManager.getMirroredHull(
                        position, selectedHull, brushSizes[brushSizeIndex])) {
                    fillHull(mirror.x, mirror.y, e, mirror.hullPiece);
                }
            }
        }

        private void onModuleClick(ModuleAtPosition pos, MouseEvent e) {
            if (canBePlaced(pos)) {
                modules.add(pos);
            }

            if (!e.isShiftDown() && mirrorManager.hasAnyMirror()) {
                for (ModuleAtPosition mirrored : mirrorManager.getMirroredModules(pos)) {
                    if (canBePlaced(mirrored)) {
                        modules.add(mirrored);
                    }
                }
            }
        }

        private void onMirrorClick(ModuleAtPosition pos) {
            switch (selectedMirror) {
            case MirrorManager.MIRROR_HORIZONTAL:
                mirrorManager.setHorizontalMirror(pos.y * 2 + 1);
                break;
            case MirrorManager.MIRROR_VERTICAL:
                mirrorManager.setVerticalMirror(pos.x * 2 + 1);
                break;
            case MirrorManager.MIRROR_ROTATION:
                mirrorManager.setRotationMirror(pos.x * 2 + 1, pos.y * 2 + 1);
                break;
            }
            selectedMirror = MirrorManager.MIRROR_NONE;

            mirrorManager.fixMirrors(MAX_SIZE);
        }

        //with regard to hull
        private void deleteInvalidModules() {
            delete: for (int i = 0; i < modules.size(); i++) {
                ModuleAtPosition pos = modules.get(i);

                for (int k = 0; k < pos.module.width; k++) {
                    for (int j = 0; j < pos.module.height; j++) {
                        if (hullSection[(pos.x + k) * MAX_SIZE + (pos.y + j)] != Hull.HULL_BLOCK) {
                            modules.remove(i);
                            i--;
                            continue delete;
                        }
                    }
                }
            }
        }

        private void placeShipPart(PartAtPosition part) {
            //placeHull
            int fromX = Math.max(0, -part.x);
            int fromY = Math.max(0, -part.y);
            int toX = Math.min(part.part.getWidth(), MAX_SIZE - part.x);
            int toY = Math.min(part.part.getHeight(), MAX_SIZE - part.y);

            //pre-place hull
            int[] partHull = part.part.getHull();
            for (int i = fromX; i < toX; i++) {
                for (int j = fromY; j < toY; j++) {
                    int partIndex = i * part.part.getHeight() + j;
                    if (partHull[partIndex] != Hull.HULL_SPACE) {
                        int hullIndex = (i + part.x) * MAX_SIZE + (j + part.y);
                        hullSection[hullIndex] = Hull.HULL_ARMOR_BLOCK; //partHull[partIndex];
                    }
                }
            }

            //delete old modules
            deleteInvalidModules();

            //place hull, for real
            for (int i = fromX; i < toX; i++) {
                for (int j = fromY; j < toY; j++) {
                    int partIndex = i * part.part.getHeight() + j;
                    if (partHull[partIndex] != Hull.HULL_SPACE) {
                        int hullIndex = (i + part.x) * MAX_SIZE + (j + part.y);
                        hullSection[hullIndex] = partHull[partIndex];
                    }
                }
            }

            //place modules
            for (ModuleAtPosition module : part.part.getModules()) {
                if (!part.part.isRotated90() || module.module.isSquare()) {
                    ModuleAtPosition newMod = new ModuleAtPosition(part.x + module.x, part.y + module.y, module.module);
                    if (canBePlaced(newMod, true, false, false, false)) {
                        modules.add(newMod);
                    }
                }
            }
        }

        private void onShipPartClick(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                unselectAll();
            } else if (e.getButton() == MouseEvent.BUTTON1) {
                ModuleAtPosition partMod = tryPlace(selectedPart.getPlacementBrush());
                PartAtPosition partPos = new PartAtPosition(partMod.x, partMod.y, selectedPart);

                if (!e.isShiftDown() && mirrorManager.hasAnyMirror()) {
                    List<PartAtPosition> parts = mirrorManager.getMirroredParts(partMod, selectedPart.getRotator());
                    for (PartAtPosition part : parts) {
                        placeShipPart(part);
                    }
                }
                //place current part last, to override mirrored once
                placeShipPart(partPos);
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (selectedHull != -1) { //hull placement
                ModuleAtPosition pos = tryPlace(Module.BRUSH_1X1);
                onHullClick(pos, e);
            } else if (selectedPart != null) {
                onShipPartClick(e);
            } else if (e.getButton() == MouseEvent.BUTTON1) {
                if (selectedModule != null) { //module placement
                    ModuleAtPosition pos = tryPlace(selectedModule);
                    onModuleClick(pos, e);
                }
                if (selectedMirror != MirrorManager.MIRROR_NONE) {
                    ModuleAtPosition pos = tryPlace(Module.BRUSH_1X1);
                    onMirrorClick(pos);
                }
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                if (nothingSelected()) {
                    removeModuleAtMouseLocation();
                }
                selectedModule = null;
                selectedMirror = MirrorManager.MIRROR_NONE;
            }

            this.repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1 &&
                    (partSelection.state == PartSelection.STATE_READY || partSelection.state == PartSelection.STATE_SELECTED)) {
                ModuleAtPosition pos = tryPlace(Module.BRUSH_2X2);
                partSelection.setFromSelection(pos.x + 1, pos.y + 1);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1 && partSelection.state == PartSelection.STATE_SELECTING) {
                ModuleAtPosition pos = tryPlace(Module.BRUSH_2X2);
                partSelection.setToSelection(pos.x + 1, pos.y + 1);
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (selectedHull != -1 && !e.isControlDown()) {
                //drag-paint hull
                onHullClick(tryPlace(Module.BRUSH_1X1), e);
            } else if (partSelection.state == PartSelection.STATE_SELECTING && SwingUtilities.isLeftMouseButton(e)) {
                ModuleAtPosition pos = tryPlace(Module.BRUSH_2X2);
                partSelection.changeSelection(pos.x + 1, pos.y + 1);
            } else {
                //move camera
                posX += lastX - e.getX();
                posY += lastY - e.getY();
            }

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
                if (e.getWheelRotation() > 0) {
                    selectedHull = Hull.nextHull(selectedHull);
                } else {
                    selectedHull = Hull.prevHull(selectedHull);
                }
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

        private void onModuleWheel(MouseWheelEvent e) {
            if (e.isShiftDown()) {
                int index = Module.allModules.indexOf(selectedModule);
                index += e.getWheelRotation();
                index %= Module.allModules.size();
                index += index < 0 ? Module.allModules.size() : 0;
                selectedModule = Module.allModules.get(index);
            } else {
                processZoom(e);
            }
        }

        private void onShipPartWheel(MouseWheelEvent e) {
            if (e.isControlDown()) {
                processZoom(e);
            } else if (e.isShiftDown()) {
                //swap parts
                int index = avaliableParts.indexOf(selectedPart.getRotator().getOriginalPart());
                index = (index - e.getWheelRotation()) % avaliableParts.size();
                index += index < 0 ? avaliableParts.size() : 0;
                selectedPart = avaliableParts.get(index);
                selectedPart.getRotator().reset();
            } else if (e.isAltDown()) {
                //flip
                if (e.getWheelRotation() > 0) {
                    selectedPart = selectedPart.getRotator().applyRotation(SquareIsomorph.FlipVerticaly);
                } else {
                    selectedPart = selectedPart.getRotator().applyRotation(SquareIsomorph.FlipHorizontaly);
                }
            } else {
                int rotation = e.getWheelRotation();
                if (rotation > 0) {
                    selectedPart = selectedPart.getRotator().applyRotation(SquareIsomorph.RotateCW);
                } else {
                    selectedPart = selectedPart.getRotator().applyRotation(SquareIsomorph.RotateCCW);
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
            } else if (selectedModule != null) {
                onModuleWheel(e);
            } else if (selectedPart != null) {
                onShipPartWheel(e);
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
