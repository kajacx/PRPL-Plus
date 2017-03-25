package com.prplplus.shipconstruct.parts;

import java.util.Arrays;

public enum SquareIsomorph {
    ABCD, BADC, DCBA, DABC, CDAB, CBAD, ADCB, BCDA;
    private SquareIsomorph[] combinationTable = new SquareIsomorph[8];

    public static SquareIsomorph Identity = ABCD;
    public static SquareIsomorph RotateCCW = BCDA;
    public static SquareIsomorph RotateCW = DABC;
    public static SquareIsomorph FlipHorizontaly = DCBA;
    public static SquareIsomorph FlipVerticaly = BADC;

    private static int indexOf(int[][] haystack, int[] needle) {
        for (int i = 0; i < haystack.length; i++) {
            if (Arrays.equals(haystack[i], needle)) {
                return i;
            }
        }
        return -1;
    }

    static {
        //fill the combination table
        int[][] mappingFunctions = new int[8][4];
        SquareIsomorph[] isomorphs = SquareIsomorph.values();
        for (SquareIsomorph iso : isomorphs) {
            int id = iso.ordinal();
            for (int i = 0; i < 4; i++) {
                mappingFunctions[id][i] = iso.name().indexOf('A' + i);
            }
        }

        int[] newMapping = new int[4];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                for (int k = 0; k < 4; k++) {
                    int[] mappingI = mappingFunctions[i];
                    int[] mappingJ = mappingFunctions[j];
                    newMapping[k] = mappingJ[mappingI[k]];
                }
                int index = indexOf(mappingFunctions, newMapping);
                isomorphs[i].combinationTable[j] = isomorphs[index];
            }
        }
    }

    public SquareIsomorph andThen(SquareIsomorph other) {
        return combinationTable[other.ordinal()];
    }
}
