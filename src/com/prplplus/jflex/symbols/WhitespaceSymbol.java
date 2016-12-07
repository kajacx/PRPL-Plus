package com.prplplus.jflex.symbols;

import com.prplplus.jflex.PrplPlusLexer;
import com.prplplus.jflex.Symbol;

public class WhitespaceSymbol extends Symbol {

    public WhitespaceSymbol(PrplPlusLexer lexer) {
        super(lexer);
    }

    @Override
    public boolean isImportant() {
        return false;
    }

}
