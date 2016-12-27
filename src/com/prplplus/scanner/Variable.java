package com.prplplus.scanner;

import com.prplplus.jflex.symbols.VarSymbol;

public class Variable {
    public boolean readFrom; //has the variable been read from?
    public boolean writtenTo; //has this variable benn written to?

    public VarSymbol symbol;

    public Variable(VarSymbol symbol) {
        this.symbol = symbol;
    }
}
