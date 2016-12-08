package com.prplplus;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.prplplus.jflex.PrplPlusLexer;
import com.prplplus.scanner.CachedLexer;
import com.prplplus.scanner.Compiler;

public class PrplPlusTest {
    public static void main(String[] args) throws IOException {
        File fileIn = new File("c:\\Users\\kajacx\\Documents\\My Games\\particlefleet\\editor\\Creative Shipyard\\scripts\\test.prpl+");
        FileReader reader = new FileReader(fileIn);
        PrintWriter writer = new PrintWriter(System.out);

        PrplPlusLexer lexer = new PrplPlusLexer(reader);
        CachedLexer cached = new CachedLexer(lexer);

        Compiler compiler = new Compiler(writer);

        compiler.compile(cached);

        reader.close();
        writer.flush();
    }
}
