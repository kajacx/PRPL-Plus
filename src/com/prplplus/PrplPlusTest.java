package com.prplplus;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.prplplus.jflex.PrplPlusLexer;
import com.prplplus.jflex.Symbol;

public class PrplPlusTest {
    public static void main(String[] args) throws IOException {
        File fileIn = new File("c:\\Users\\kajacx\\Documents\\My Games\\particlefleet\\editor\\Creative Shipyard\\scripts\\ship_spawn.prpl");
        FileReader reader = new FileReader(fileIn);

        PrplPlusLexer lexer = new PrplPlusLexer(reader);

        Symbol symbol;

        while ((symbol = lexer.yylex()) != null) {
            System.out.print(symbol.text);
        }

        reader.close();
    }
}
