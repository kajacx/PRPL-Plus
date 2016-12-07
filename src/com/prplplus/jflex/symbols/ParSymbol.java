package com.prplplus.jflex.symbols;

import com.prplplus.jflex.PrplPlusLexer;
import com.prplplus.jflex.Symbol;

public class ParSymbol extends Symbol {

    public static enum Type {
        LEFT_PAR, RIGHT_PAR;
    }

    public Type type;

    public ParSymbol(PrplPlusLexer lexer, Type type) {
        super(lexer);
        this.type = type;
    }

}
