package com.prplplus.jflex.symbols;

import com.prplplus.jflex.PrplPlusLexer;
import com.prplplus.jflex.Symbol;

public class UserFunctionSymbol extends Symbol {
    public String functionName;
    public boolean isDefinition;
    public boolean isRecursive;

    public UserFunctionSymbol(PrplPlusLexer lexer, boolean isDefinition) {
        super(lexer);

        functionName = text.substring(1);
        this.isDefinition = isDefinition;
    }

    @Override
    public boolean isFunctionDefinition() {
        return isDefinition;
    }

}
