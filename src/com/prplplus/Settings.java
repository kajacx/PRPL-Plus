package com.prplplus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Settings {
    /**
     * When zooming in ship editor, keep cursor at the same place, rathen than the center of the screen
     */
    public static boolean ZOOM_TO_CURSOR = false;

    /**
     * When in ship editor, show the original 35x25 maximal size
     */
    public static boolean OUTLINE_35_25_BOX = false;

    public static String WORK_IN = "default";

    public static int MAX_SIZE = 128;

    public static String PREFIX_DELIM = "__";

    /**
     * enables force export, which fakes the ship size to be larger than it actually is
     * settable via a -forceExport argument
     */
    public static boolean enableForceExport = false;

    public static boolean colorGrid = false;

    static {
        Properties props = new Properties();
        InputStream stream = null;
        try {
            stream = new FileInputStream("settings.properties");
            props.load(stream);
            ZOOM_TO_CURSOR = props.getProperty("zoomToCursor", "false").equals("true");
            OUTLINE_35_25_BOX = props.getProperty("displayMaxSizeOutline", "true").equals("true");
            //MAX_SIZE = readInt(props, "maxSize", 128);
            //MAX_SIZE = Math.min(Math.max(35, MAX_SIZE), 128);
            WORK_IN = props.getProperty("PFDirectory", WORK_IN);
            PREFIX_DELIM = props.getProperty("PrefixDelimitier", PREFIX_DELIM);
            if (WORK_IN.equals("default")) {
                WORK_IN = Utils.getPFLocalFilesFolder();
            }
            if (!(new File(WORK_IN).exists())) {
                System.out.println("Warning: PF directiory doesn't exists: " + WORK_IN);
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace(System.out);
                }
            }
        }
    }

    private static int readInt(Properties props, String key, int defaultValue) {
        if (props.containsKey(key)) {
            try {
                return Integer.parseInt(props.getProperty(key));
            } catch (NumberFormatException ex) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }
}
