package com.prplplus.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

public class BinaryToHexConvertor {
    public static void main(String[] args) throws IOException {
        String fileName = "c:\\kajacx\\games\\pf\\Creative Shipyard\\ships\\corporate-nohq.txt";

        File fileFrom = new File(fileName);
        File fileTo = new File(fileFrom.getAbsolutePath() + "_hex.txt");

        InputStream is = new FileInputStream(fileFrom);
        PrintWriter writer = new PrintWriter(fileTo);
        //PrintWriter writer = new PrintWriter(System.out);

        int read;
        while ((read = is.read()) != -1) {
            writer.printf("%02X %s%n", read, convert(read));
        }

        is.close();
        writer.close();
    }

    private static String convert(int byteValue) {
        if (byteValue == '\u0000')
            return "[NULL]";
        if (byteValue == '\n')
            return "[NL]";
        if (byteValue == '\r')
            return "[CR]";
        if (byteValue == ' ')
            return "[SPACE]";
        if (byteValue == '\t')
            return "[TAB]";
        if (byteValue > 32 && byteValue < 127)
            return Character.toString((char) byteValue);
        return "[-]";
    }
}
