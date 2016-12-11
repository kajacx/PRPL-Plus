package com.prplplus.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.prplplus.Hull;
import com.prplplus.Module;
import com.prplplus.ModuleAtPosition;
import com.prplplus.Settings;

public class ShipConstructorPanel extends JPanel {
    public static final int MAX_SIZE = 32;

    private JTextField nameField;
    private Module selectedModule;
    private int selectedHull = -1;
    private int[] hullSection = new int[MAX_SIZE * MAX_SIZE];

    private List<ModuleAtPosition> modules = new ArrayList<>();

    public ShipConstructorPanel() {
        setLayout(new BorderLayout());

        setBackground(new Color(255, 255, 196));

        add(createTopBar(), BorderLayout.NORTH);
        add(new ShipRenderer(), BorderLayout.CENTER);
        add(createModulesPanel(), BorderLayout.EAST);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JPanel nameBar = new JPanel(new FlowLayout());
        nameBar.setOpaque(false);
        nameBar.add(new JLabel("Name:"));
        nameBar.add(nameField = new JTextField(20));
        topBar.add(nameBar, BorderLayout.WEST);

        //JPanel hullBar = new JPanel(new FlowLayout());
        //hullBar.setOpaque(false);
        //hullBar.add(new JLabel("Hull"));
        //hullBar.add(new JLabel("Section"));
        topBar.add(new HullSelector(), BorderLayout.EAST);

        return topBar;
    }

    private JPanel createModulesPanel() {
        JPanel modulesPanel = new JPanel();
        modulesPanel.setLayout(new BoxLayout(modulesPanel, BoxLayout.Y_AXIS));
        modulesPanel.setBackground(new Color(255, 226, 196));

        modulesPanel.add(new JLabel("Modules"));

        for (Module m : Module.values()) {
            JButton button = new JButton(m.name);
            button.setIcon(getImageForModule(m));
            button.addActionListener(e -> {
                selectedModule = m;
                selectedHull = -1;
            });

            modulesPanel.add(button);
        }

        return modulesPanel;
    }

    private ImageIcon getImageForModule(Module m) {
        int scaleFactor = 24 / Math.max(3, Math.max(m.width, m.height));
        Image i = m.image;
        if (i == null) {
            return null;
        }
        i = i.getScaledInstance(m.width * scaleFactor, m.height * scaleFactor, Image.SCALE_DEFAULT);
        return new ImageIcon(i);
    }

    private int[] hullToDraw = { Hull.HULL_BLOCK, Hull.HULL_CORNER_LB, Hull.HULL_SPIKE_B };

    private class HullSelector extends JPanel implements MouseListener {
        public HullSelector() {
            setPreferredSize(new Dimension(300, 30));
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
                g.drawRect(x - 1, y - 1, 14 + 1, 14 + 1);
                g.drawImage(Hull.hullImages[hullToDraw[i]], x, y, null);
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            //System.out.println("Kappa " + e.getPoint());
            if (e.getButton() == MouseEvent.BUTTON1) {
                for (int i = 0; i < hullToDraw.length; i++) {
                    int x = 8 + i * (14 + 8);
                    int y = 8;

                    if (e.getX() >= x && e.getX() < x + 14 && e.getY() >= y && e.getY() <= y + 14) {
                        selectedHull = hullToDraw[i];
                        selectedModule = null;
                        //System.out.println("Selected " + selectedHull);
                        break;
                    }
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
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

    private class ShipRenderer extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
        private int posX = 5; //offset, in pixels
        private int posY = 5; //offset, in pixels

        //private boolean dragging = false;
        private int lastX, lastY; //last position of the mouse

        private ShipRenderer() {
            setPreferredSize(new Dimension(512, 512));

            setBackground(Color.lightGray);

            addMouseListener(this);
            addMouseMotionListener(this);
            addMouseWheelListener(this);

            for (int i = 2; i < 10; i++) {
                hullSection[i * MAX_SIZE + i + 2] = Hull.HULL_CORNER_LB;
                for (int j = i + 3; j < 16; j++) {
                    hullSection[i * MAX_SIZE + j] = Hull.HULL_BLOCK;
                }
            }
        }

        private Color transparentRed = new Color(255, 0, 0, 64);

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.translate(-posX, -posY);

            //lines
            g.setColor(Color.BLACK);

            for (int i = 0; i <= MAX_SIZE; i++) {
                g.drawLine(i * zoom, 0, i * zoom, MAX_SIZE * zoom);
                g.drawLine(0, i * zoom, MAX_SIZE * zoom, i * zoom);
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
                g.drawImage(Hull.hullImages[selectedHull], pos.x * zoom, pos.y * zoom, pos.module.width * zoom, pos.module.height * zoom, null);
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
                if (m.intersectsWith(pos)) {
                    module = m;
                    break;
                }
            }

            modules.remove(module);
        }

        private boolean nothingSelected() {
            return selectedModule == null && selectedHull == -1;
        }

        private boolean inBounds(ModuleAtPosition module) {
            return module.x >= 0 &&
                    module.y >= 0 &&
                    module.x + module.module.width <= MAX_SIZE &&
                    module.y + module.module.height <= MAX_SIZE;
        }

        private boolean canBePlaced(ModuleAtPosition module) {
            if (!inBounds(module))
                return false;

            for (int i = module.x; i < module.x + module.module.width; i++) {
                for (int j = module.y; j < module.y + module.module.height; j++) {
                    if (hullSection[i * MAX_SIZE + j] != Hull.HULL_BLOCK) {
                        return false;
                    }
                }
            }

            for (ModuleAtPosition m : modules) {
                if (m.intersectsWith(module))
                    return false;
            }
            return true;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                if (selectedModule != null) { //module placement
                    ModuleAtPosition pos = tryPlace(selectedModule);
                    if (canBePlaced(pos)) {
                        modules.add(pos);
                    }
                } else if (selectedHull != -1) {
                    ModuleAtPosition pos = tryPlace(Module.LASER);
                    if (canBePlaced(pos)) {
                        hullSection[pos.x * MAX_SIZE + pos.y] = selectedHull;
                    }
                }
            }
            if (e.getButton() == MouseEvent.BUTTON3) {
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

        private int zoomIndex = 6;
        private int[] zoomIndexTable = { 4, 6, 8, 11, 15, 21, 29, 42 };

        private int zoom = zoomIndexTable[zoomIndex]; //number of pixels per 1 slot

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            int prevZoom = zoom;
            zoomIndex -= e.getWheelRotation();
            zoomIndex = Math.max(zoomIndex, 0);
            zoomIndex = Math.min(zoomIndex, zoomIndexTable.length - 1);
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

                this.repaint();
            }
        }



    }
}
