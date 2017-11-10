package com.prplplus.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.prplplus.Settings;
import com.prplplus.debugger.DebuggerCompiler;
import com.prplplus.debugger.DebuggerCompiler.DebuggerOptions;

public class DebuggerPanel extends JPanel {
    private JPanel fileListPanel;
    private List<CompileFile> fileList = new ArrayList<>();
    private Timer autoCompileTimer;
    private JFileChooser fileChooser;

    private JCheckBox detailedMode;

    private Color bgColor = new Color(208, 255, 192);
    private Color okColor = new Color(64, 192, 16);
    private Color errColor = new Color(255, 64, 16);

    public DebuggerPanel() {
        setBackground(bgColor);

        autoCompileTimer = new Timer(1000, e -> compileAll(false));
        autoCompileTimer.setRepeats(true);

        if (new File(Settings.WORK_IN + "/editor").exists()) {
            fileChooser = new JFileChooser(Settings.WORK_IN + "/editor");
        } else {
            fileChooser = new JFileChooser(Settings.WORK_IN);
        }
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PRPL scripts (*.prpl)", "prpl"));

        setLayout(new BorderLayout());

        add(createTopBar(), BorderLayout.NORTH);

        add(fileListPanel = createFilesList(), BorderLayout.CENTER);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setOpaque(false);

        JButton addFile = new JButton("Add file");
        addFile.addActionListener(e -> addFile());
        topBar.add(addFile);

        JCheckBox autoCompile = new JCheckBox("Compile every second");
        autoCompile.setOpaque(false);
        autoCompile.addActionListener(e -> {
            if (autoCompile.isSelected())
                autoCompileTimer.start();
            else
                autoCompileTimer.stop();
        });
        topBar.add(autoCompile);

        JButton compileNow = new JButton("Compile");
        compileNow.addActionListener(e -> compileAll(true));
        topBar.add(compileNow);

        detailedMode = new JCheckBox("Detailed mode", new DebuggerOptions().detailedMode);
        detailedMode.setToolTipText("Display what command put what data on stack - this is quite ocmputionaly demanding");
        detailedMode.setOpaque(false);
        topBar.add(detailedMode);

        return topBar;
    }

    private JPanel createFilesList() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    private void addFile() {
        if (fileChooser.showOpenDialog(MainFrame.instance) == JFileChooser.APPROVE_OPTION) {
            CompileFile file = new CompileFile(fileChooser.getSelectedFile());
            fileList.add(file);
            fileListPanel.add(file.rowPanel);

            /*MainFrame.instance.revalidate();
            MainFrame.instance.repaint();*/

            fileListPanel.revalidate();
            fileListPanel.repaint();
        }

    }

    private void compileAll(boolean force) {
        long stamp = System.currentTimeMillis();
        for (CompileFile file : fileList) {
            if (force || file.origFile.lastModified() > file.lastChecked) {
                DebuggerOptions options = new DebuggerOptions();
                options.setDetailedMode(detailedMode.isSelected());
                file.error = DebuggerCompiler.compile(file.origFile, options);
                if (file.error == null) {
                    file.fileStatus.setText("Compiled");
                    file.fileStatus.setForeground(okColor);
                } else {
                    file.fileStatus.setText(file.error);
                    file.fileStatus.setForeground(errColor);
                }
                file.lastChecked = stamp;
            }
        }
    }

    private class CompileFile {
        private File origFile;
        private File compiledFile;
        private String displayPath;
        private String error; //null for ok
        private long lastChecked; // 0 stands for 1.1.1970, i think we are good here

        private JPanel rowPanel;
        private JLabel fileLabel;
        private JLabel fileStatus;
        private JButton fileButton;

        public CompileFile(File file) {
            origFile = file;
            compiledFile = DebuggerCompiler.getCompiledFile(file);

            String workIn = new File(Settings.WORK_IN + "/editor").getAbsolutePath().replace('\\', '/').toLowerCase();
            if (!workIn.endsWith("/")) {
                workIn = workIn + '/';
            }
            String path = origFile.getAbsolutePath().replace('\\', '/');

            if (path.toLowerCase().startsWith(workIn)) {
                displayPath = path.substring(workIn.length());
            } else {
                displayPath = path;
            }

            rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            rowPanel.setOpaque(false);

            fileLabel = new JLabel(displayPath);
            fileStatus = new JLabel("Waiting for compile");
            fileStatus.setForeground(errColor);
            fileButton = new JButton("Remove");

            fileButton.addActionListener(e -> {
                //if(JOptionPane.showConfirmDialog(MainFrame.instance, "Are you sire you want to remove this file from the compilatio"))
                fileList.remove(this);
                fileListPanel.remove(this.rowPanel);

                compiledFile.delete();

                fileListPanel.revalidate();
                fileListPanel.repaint();
            });

            fileLabel.setPreferredSize(new Dimension(300, 15));
            fileStatus.setPreferredSize(new Dimension(650, 15));

            rowPanel.add(fileLabel);
            rowPanel.add(fileStatus);
            rowPanel.add(fileButton);
        }
    }
}
