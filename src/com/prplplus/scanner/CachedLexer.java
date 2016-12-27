package com.prplplus.scanner;

import java.io.IOException;
import java.util.ArrayDeque;

import com.prplplus.jflex.PrplPlusLexer;
import com.prplplus.jflex.Symbol;

public class CachedLexer {
    private PrplPlusLexer lexer;
    private ArrayDeque<Symbol> symbolCache = new ArrayDeque<>();
    private String scriptNamespace; //wow, what a horrible design
    private String mainNamespace; //well never mind, at least it works

    public CachedLexer(PrplPlusLexer lexer) {
        this.lexer = lexer;
    }

    public Symbol getNextSymbol() throws IOException {
        while (symbolCache.isEmpty()) {
            symbolCache.add(lexer.yylex());
        }

        return symbolCache.pollFirst();
    }

    public Symbol peekNextUseful() throws IOException {
        if (symbolCache.isEmpty()) {
            do {
                symbolCache.add(lexer.yylex());
            } while (!symbolCache.peekLast().isImportant());
        }

        return symbolCache.peekLast();
    }

    public String getFilename() {
        return lexer.getFileName();
    }

    public void close() {
        try {
            lexer.yyclose();
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }

    public String getScriptNamespace() {
        return scriptNamespace;
    }

    public void setScriptNamespace(String scriptNamespace) {
        this.scriptNamespace = scriptNamespace;
    }

    public String getMainNamespace() {
        return mainNamespace;
    }

    public void setMainNamespace(String mainNamespace) {
        this.mainNamespace = mainNamespace;
    }
}
