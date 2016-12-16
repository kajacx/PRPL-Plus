package com.prplplus;

public class Utils {
    public static int clamp(int lower, int upper, int value) {
        if (value < lower)
            return lower;
        if (value > upper)
            return upper;
        return value;
    }
}
