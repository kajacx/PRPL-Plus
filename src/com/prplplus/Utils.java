package com.prplplus;

import java.awt.Dimension;

import javax.swing.JPanel;

public class Utils {
    public static int clamp(int lower, int upper, int value) {
        if (value < lower)
            return lower;
        if (value > upper)
            return upper;
        return value;
    }

    public static String getPFLocalFilesFolder() {
        String format;
        if (OSValidator.isWindows()) {
            format = "c:/Users/%s/Documents/My Games/particlefleet/";
        } else if (OSValidator.isUnix() || OSValidator.isSolaris()) {
            format = "/home/%s/.local/share/knucklecracker/particlefleet/";
        } else if (OSValidator.isMac()) {
            format = "/Users/%s/Library/Application Support/com.knucklecracker/particlefleet/";
        } else {
            return null;
        }

        return String.format(format, System.getProperty("user.name"));
    }

    public static JPanel createSpace(int w, int h) {
        JPanel space = new JPanel();
        space.setPreferredSize(new Dimension(w, h));
        space.setOpaque(false);
        return space;
    }
}
