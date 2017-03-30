package com.prplplus.test;

import java.io.PrintWriter;

public class FieldManipulator {
    private static int width = 10;
    private static int length = 25;
    //private static int borderW = 3;

    public static void main(String[] args) {
        generateDiag();
    }

    public static void generateDiag() {
        PrintWriter writer = new PrintWriter(System.out);

        //precompute
        int startX = -length / 2 - width / 2;
        int startY = -length / 2 + width / 2;

        //header
        writer.println("%library");
        writer.println("# params: width: " + width + ", length: " + length + ", dir: Diag");
        writer.println("# args: centerX, cetnerY, forceX, forceY");
        writer.println(":fillFields # [ i1 i2 f1 f1 - ]");

        //load vars
        writer.println("  +>forceY +>forceX");
        writer.format("  %d add swap #startY%n", startY);
        writer.format("  %d add swap #startX%n", startX);

        //main loop
        writer.format("  %d 0 do%n", length);
        writer.format("    dup2 #copy startX startY to CurX CurY%n%n");
        writer.format("    %%blockstart #inner loop%n");
        for (int i = 0; i < width; i++) {
            writer.format("      dup2 <+forceX <+forceY SetFieldCell%n");
            writer.format("      swap 1 add swap # increase x%n%n");

            writer.format("      dup2 <+forceX <+forceY SetFieldCell%n");
            writer.format("      1 sub # decrease y%n%n");
        }
        writer.format("    %%blockend #inner loop%n%n");

        writer.format("    pop pop #remove curX, curY%n");

        writer.format("    1 add swap # increase startY%n");
        writer.format("    1 add swap # increase startX%n");

        writer.println("  loop");
        writer.println("  pop pop #remove startX, startY");

        writer.flush();
    }
}
