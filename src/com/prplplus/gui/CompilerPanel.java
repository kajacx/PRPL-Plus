package com.prplplus.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import com.prplplus.Settings;
import com.prplplus.errors.ErrorHandler;
import com.prplplus.errors.ErrorHandler.ErrorLevel;
import com.prplplus.scanner.GlobalCompiler;

@SuppressWarnings("serial")
public class CompilerPanel extends JPanel {
    public JTextPane errorArea;
    public QuickDisplay display;

    private Timer autoCompileTimer;
    private GlobalCompiler compiler;

    public CompilerPanel() {
        setPreferredSize(new Dimension(800, 600));

        if (Settings.WORK_IN == null || !new File(Settings.WORK_IN).exists()) {
            add(new JLabel("Please set the PFDirectory in the settings file and restart the program."));
            return;
        }

        ErrorHandler.errorSink = this;

        autoCompileTimer = new Timer(1000, e -> scanAndCompile(false));
        autoCompileTimer.setRepeats(true);

        compiler = new GlobalCompiler();

        setLayout(new BorderLayout());

        add(createTopBar(), BorderLayout.NORTH);

        errorArea = new JTextPane();
        add(new JScrollPane(errorArea), BorderLayout.CENTER);

        display = new QuickDisplay();
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton showDisplay = new JButton("Show status display");
        showDisplay.addActionListener(e -> display.setVisible(true));
        topBar.add(showDisplay);

        JCheckBox autoCompile = new JCheckBox("Compile every second");
        autoCompile.addActionListener(e -> {
            if (autoCompile.isSelected())
                autoCompileTimer.start();
            else
                autoCompileTimer.stop();
        });
        topBar.add(autoCompile);

        JButton compileNow = new JButton("Compile");
        compileNow.addActionListener(e -> scanAndCompile(true));
        topBar.add(compileNow);

        return topBar;
    }

    public void onError(ErrorLevel level, String message) {
        appendErrorMessage(message, level.color);
        display.onError(level);
    }

    private void appendErrorMessage(String msg, Color c) {
        appendToPane(errorArea, msg + System.lineSeparator(), c);
    }

    private void appendToPane(JTextPane tp, String msg, Color c) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
    }

    private void scanAndCompile(boolean manualRequest) {
        File scanIn = new File(Settings.WORK_IN + "/editor");
        display.compilationStarted();
        int compiled = compiler.compileAll(scanIn);
        if (compiled > 0) {
            appendErrorMessage(String.format("Compiled %d files with %d errors, %d warnings and %d ignored warnings.",
                    compiled, display.errors, display.warnings, display.ignores), Color.black);
            display.compilationFinished();
        } else if (manualRequest) {
            appendErrorMessage("No files changed since the last compilation.", Color.black);
        }
    }

    public static class QuickDisplay extends JFrame {
        public JLabel errorStatus;
        private int errors;
        private int warnings;
        private int ignores;

        private Timer timer;

        public static Color ready = new Color(128, 196, 255);
        public static Color OK = new Color(64, 255, 64);
        public static Color warning = new Color(255, 196, 32);
        public static Color error = new Color(255, 64, 32);

        public QuickDisplay() {
            super("Compile status");
            setLayout(new BorderLayout());

            setUndecorated(true);
            this.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);

            errorStatus = new JLabel("Ready");
            errorStatus.setHorizontalAlignment(JLabel.CENTER);
            errorStatus.setPreferredSize(new Dimension(150, 30));
            errorStatus.setOpaque(true);

            errorStatus.setBackground(ready);

            timer = new Timer(5 * 1000, e -> onTimerFired()); //5 seconds

            add(errorStatus, BorderLayout.CENTER);

            pack();
            //setAlwaysOnTop(true);
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        }

        public void compilationStarted() {
            errors = warnings = ignores = 0;
        }

        public void onError(ErrorLevel level) {
            switch (level) {
            case ERROR:
                errors++;
                break;
            case WARNING:
                warnings++;
                break;
            case IGNORE:
                ignores++;
                break;
            }
        }

        public void compilationFinished() {
            if (errors > 0) {
                errorStatus.setText("Errors (" + errors + ")");
                errorStatus.setBackground(error);
            } else if (warnings > 0) {
                errorStatus.setText("Warnings (" + warnings + ")");
                errorStatus.setBackground(warning);
            } else {
                errorStatus.setText("OK (" + ignores + ")");
                errorStatus.setBackground(OK);
            }
            timer.restart();
        }

        private void onTimerFired() {
            errorStatus.setText("Ready");
            errorStatus.setBackground(ready);
        }
    }
}
