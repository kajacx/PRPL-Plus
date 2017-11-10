package com.prplplus.osu;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OsuToPrplConverter {
    private File osuBeatmap;
    private List<HitObject> hitObjects = new ArrayList<>();
    private Color[] comboColors = new Color[8];

    public OsuToPrplConverter(File osuBeatmap) {
        this.osuBeatmap = osuBeatmap;
    }

    public String convert() {
        try {
            loadObjects();

            File prplScript = new File(osuBeatmap.getAbsolutePath() + ".prpl");
            PrintWriter prplCode = new PrintWriter(prplScript);

            writeHitObjects(prplCode);

            prplCode.close();

            return null;
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
            return ex.getMessage() != null ? ex.getMessage() : ex.getClass().toString();
        }
    }

    private void loadObjects() throws IOException {
        Scanner scan = new Scanner(osuBeatmap);


        //read colors objects
        while (scan.hasNextLine()) {
            String line = scan.nextLine().trim();
            if (line.equals("[Colours]")) {
                break;
            }
        }
        Pattern pattern = Pattern.compile("Combo([0-9]) : ([0-9]+),([0-9]+),([0-9]+)");
        while (scan.hasNextLine()) {
            String line = scan.nextLine().trim();
            if (line.length() > 0) {
                Matcher matcher = pattern.matcher(line);
                matcher.find();
                int index = Integer.parseInt(matcher.group(1));
                int r = Integer.parseInt(matcher.group(2));
                int g = Integer.parseInt(matcher.group(3));
                int b = Integer.parseInt(matcher.group(4));
                comboColors[index - 1] = new Color(r, g, b);
            } else {
                break;
            }
        }

        //read hit objects
        while (scan.hasNextLine()) {
            String line = scan.nextLine().trim();
            if (line.equals("[HitObjects]")) {
                break;
            }
        }

        while (scan.hasNextLine()) {
            String line = scan.nextLine().trim();
            if (!line.isEmpty()) {
                hitObjects.add(new HitObject(line));
            }
        }

        scan.close();
    }

    private void writeHitObjects(PrintWriter prplCode) {
        int comboColor = 0;
        int comboCounter = 0;
        for (HitObject hit : hitObjects) {
            if ((hit.type & HitObject.MASK_NEW_COMBO) != 0) {
                comboColor = (comboColor + 1) % comboColors.length;
                comboCounter = 0;
            }
            comboCounter++;

            Color color = comboColors[comboColor];
            //# x y time r g b number
            prplCode.format("%f %f %d %d %d %d %d @spawnCircle%n", (float) hit.x, (float) hit.y, (int) (hit.time / 1000f * 30f), color.getRed(), color.getGreen(), color.getBlue(), comboCounter);
        }
    }

    public static class HitObject {
        public static final int MASK_NEW_COMBO = 0b100;

        private int x;
        private int y;
        private int time;
        private int type;

        public HitObject(String line) {
            String[] parts = line.split(",");
            x = Integer.parseInt(parts[0]);
            y = Integer.parseInt(parts[1]);
            time = Integer.parseInt(parts[2]);
            type = Integer.parseInt(parts[3]);
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
