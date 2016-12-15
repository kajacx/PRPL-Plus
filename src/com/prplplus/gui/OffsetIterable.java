package com.prplplus.gui;

public class OffsetIterable {
    public static int FULL = 0;
    public static int VERTICAL = 1;
    public static int HORIZONTAL = 2;
    public static int DIAG_LB = 3;
    public static int DIAG_LT = 4;

    private static Offset[][] offsets; //brush size-major

    public static void init(int[] brushSizes) {
        offsets = new Offset[brushSizes.length * 5][];
        for (int i = 0; i < brushSizes.length; i++) {
            int size = brushSizes[i];
            int radius = size / 2;

            //FULL
            offsets[i * 5 + FULL] = new Offset[size * size];
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    offsets[i * 5 + FULL][x * size + y] = new Offset(x - radius, y - radius);
                }
            }

            //VERTICAL
            offsets[i * 5 + VERTICAL] = new Offset[size];
            for (int y = 0; y < size; y++) {
                offsets[i * 5 + VERTICAL][y] = new Offset(0, y - radius);
            }

            //HORIZONTAL
            offsets[i * 5 + HORIZONTAL] = new Offset[size];
            for (int x = 0; x < size; x++) {
                offsets[i * 5 + HORIZONTAL][x] = new Offset(x - radius, 0);
            }

            //DIAG_LB
            offsets[i * 5 + DIAG_LB] = new Offset[size];
            for (int j = 0; j < size; j++) {
                offsets[i * 5 + DIAG_LB][j] = new Offset(j - radius, j - radius);
            }

            //DIAG_LT
            offsets[i * 5 + DIAG_LT] = new Offset[size];
            for (int j = 0; j < size; j++) {
                offsets[i * 5 + DIAG_LT][j] = new Offset(j - radius, -j + radius);
            }
        }
    }

    public static Offset[] getFor(int brushSizeIndex, int direction) {
        return offsets[brushSizeIndex * 5 + direction];
    }

    public static class Offset {
        public final int x, y;

        public Offset(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
