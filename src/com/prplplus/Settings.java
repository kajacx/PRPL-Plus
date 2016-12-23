package com.prplplus;

import java.io.FileInputStream;
import java.util.Properties;

public class Settings {
    /**
     * When zooming in ship editor, keep cursor at the same place, rathen than the center of the screen
     */
    public static boolean ZOOM_TO_CURSOR = false;

    /**
     * When in ship editor, show the original 35x25 maximal size
     */
    public static boolean OUTLINE_35_25_BOX = true;

    public static String WORK_IN = "c:/Users/Karel/Documents/My Games/particlefleet/";

    public static int MAX_SIZE = 128;

    static {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream("settings.properties"));
            ZOOM_TO_CURSOR = props.getProperty("zoomToCursor", "false").equals("true");
            OUTLINE_35_25_BOX = props.getProperty("displayMaxSizeOutline", "true").equals("true");
            MAX_SIZE = Integer.parseInt(props.getProperty("maxSize", "128"));
            MAX_SIZE = Math.min(Math.max(35, MAX_SIZE), 128);
            WORK_IN = props.getProperty("PFDirectory");
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }
}
