package com.prplplus.jflex.symbols;

import com.prplplus.jflex.PrplPlusLexer;
import com.prplplus.jflex.Symbol;

public class SpecialSymbol extends Symbol {

    public static enum Type {
        LOCAL_PREFIX, SEMI_GLOBAL_PREFIX
    }

    public Type type;

    public SpecialSymbol(PrplPlusLexer lexer, Type type) {
        super(lexer);
        this.type = type;
    }

}
