package com.prplplus.test;

import java.util.Base64;
import java.util.Scanner;

public class Base64Printer {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while (true) {
            String line = in.nextLine();
            byte[] data = Base64.getDecoder().decode(line);
            for (byte value : data) {
                int intVal = value < 0 ? value + 256 : value;
                System.out.format("%02X%n", intVal);
            }
        }
    }
}
