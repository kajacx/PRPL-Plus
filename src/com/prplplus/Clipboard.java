package com.prplplus;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class Clipboard {
    /**
     * Copies string s to clipboard
     * @param s String to be copied into clipboard
     */
    public static void copy(String s) {
        StringSelection stringSelection = new StringSelection(s);
        java.awt.datatransfer.Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        c.setContents(stringSelection, null);
    }

    /**
     * Returns the content of the clipboard
     * @return Clipboard content or <code>null</code> on failure
     */
    public static String paste() {
        java.awt.datatransfer.Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable t = c.getContents(null);
        if (t == null)
            return null;
        try {
            return (String) t.getTransferData(DataFlavor.stringFlavor);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return null;
        }
    }
}
