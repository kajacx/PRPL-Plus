package com.prplplus.jflex.symbols;

import com.prplplus.jflex.PrplPlusLexer;
import com.prplplus.jflex.Symbol;

public class EOFSymbol extends Symbol {

    public EOFSymbol(PrplPlusLexer lexer) {
        super(lexer);
    }

    @Override
    public boolean isEOF() {
        return true;
    }
}
