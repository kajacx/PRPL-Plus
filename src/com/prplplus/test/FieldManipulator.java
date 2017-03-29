package com.prplplus.test;

import java.io.PrintWriter;

public class FieldManipulator {
    private static int width = 20;
    private static int length = 25;
    private static int borderW = 5;

    public static void main(String[] args) {
        generateDiag();
    }

    public static void generateDiag() {
        int effW = width - 2 * borderW;
        PrintWriter writer = new PrintWriter(System.out);

        //precompute
        int startX = -length / 2 - effW / 2;
        int startY = -length / 2 + effW / 2;

        //header
        writer.println("%library");
        writer.println("# params: width: " + width + ", length: " + length);
        writer.println("# args: centerX, cetnerY, forceX, forceY");
        writer.println(":fillFields # [ i1 i2 f1 f1 - ]");

        //load vars
        writer.println("  +>forceY +>forceX");
        writer.format("  %d add +>startY%n", startY);
        writer.format("  %d add +>startX%n", startX);

        //main loop
        writer.format("  %d 0 do%n", length);
        writer.format("    <+startX <+startY #keep on stack%n%n");
        writer.format("    %%blockstart #inner loop%n");
        for (int i = 0; i < effW; i++) {
            writer.format("      dup2 <+forceX <+forceY SetFieldCell%n");
            writer.format("      swap 1 add swap # increase x%n");

            writer.format("      dup2 <+forceX <+forceY SetFieldCell%n");
            writer.format("      1 sub # decrease y%n%n");
        }
        writer.format("    %%blockend #inner loop%n%n");

        writer.format("    pop pop # remove startx and starty%n");
        writer.format("    <+startX 1 add +>startX%n");
        writer.format("    <+startY 1 add +>startY%n");

        writer.println("  loop");

        writer.flush();
    }
}
