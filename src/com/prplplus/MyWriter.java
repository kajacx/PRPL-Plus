package com.prplplus;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;

public class MyWriter {
    private PrintStream stream;
    private String spaces = "";

    public MyWriter(PrintStream stream) {
        this.stream = stream;
    }

    public MyWriter(OutputStream stream) {
        this(new PrintStream(stream));
    }

    public void addSpaces() {
        this.spaces = this.spaces + "    ";
    }

    public void removeSpaces() {
        if (this.spaces.length() >= 4) {
            this.spaces = this.spaces.substring(4);
        }
    }

    @Override
    public int hashCode() {
        return stream.hashCode();
    }

    public void write(byte[] b) throws IOException {
        stream.write(b);
    }

    @Override
    public boolean equals(Object obj) {
        return stream.equals(obj);
    }

    @Override
    public String toString() {
        return stream.toString();
    }

    public void flush() {
        stream.flush();
    }

    public void close() {
        stream.close();
    }

    public boolean checkError() {
        return stream.checkError();
    }

    public void write(int b) {
        stream.write(b);
    }

    public void write(byte[] buf, int off, int len) {
        stream.write(buf, off, len);
    }

    public void print(boolean b) {
        stream.print(b);
    }

    public void print(char c) {
        stream.print(c);
    }

    public void print(int i) {
        stream.print(i);
    }

    public void print(long l) {
        stream.print(l);
    }

    public void print(float f) {
        stream.print(f);
    }

    public void print(double d) {
        stream.print(d);
    }

    public void print(char[] s) {
        stream.print(s);
    }

    public void print(String s) {
        stream.print(s);
    }

    public void print(Object obj) {
        stream.print(obj);
    }

    public void println() {
        stream.println(spaces);
    }

    public void println(boolean x) {
        stream.println(spaces + x);
    }

    public void println(char x) {
        stream.println(spaces + x);
    }

    public void println(int x) {
        stream.println(spaces + x);
    }

    public void println(long x) {
        stream.println(spaces + x);
    }

    public void println(float x) {
        stream.println(spaces + x);
    }

    public void println(double x) {
        stream.println(spaces + x);
    }

    public void println(char[] x) {
        stream.println(spaces + x);
    }

    public void println(String x) {
        stream.println(spaces + x);
    }

    public void println(Object x) {
        stream.println(spaces + x);
    }

    public PrintStream printf(String format, Object... args) {
        return stream.printf(spaces + format, args);
    }

    public PrintStream printf(Locale l, String format, Object... args) {
        return stream.printf(l, spaces + format, args);
    }

    public PrintStream format(String format, Object... args) {
        return stream.format(spaces + format, args);
    }

    public PrintStream format(Locale l, String format, Object... args) {
        return stream.format(l, spaces + format, args);
    }

    public PrintStream append(CharSequence csq) {
        return stream.append(csq);
    }

    public PrintStream append(CharSequence csq, int start, int end) {
        return stream.append(csq, start, end);
    }

    public PrintStream append(char c) {
        return stream.append(c);
    }


}
