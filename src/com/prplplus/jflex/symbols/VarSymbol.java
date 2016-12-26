package com.prplplus.jflex.symbols;

import com.prplplus.jflex.PrplPlusLexer;
import com.prplplus.jflex.Symbol;

public class VarSymbol extends Symbol {

    public static enum Operation {
        READ("<-", "<-!"), WRITE("->", "->!"), EXISTS("-?", "-?!"), DELETE("--", "--!");

        public String opInstr;
        public String refInstr;

        private Operation(String opInstr, String refInstr) {
            this.opInstr = opInstr;
            this.refInstr = refInstr;
        }
    }

    public static enum Scope {
        LOCAL, SEMI_GLOBAL, GLOBAL, ARGUMENT;
    }

    public String varName;
    public Operation op;
    public Scope scope;
    public boolean isRef;

    public VarSymbol(PrplPlusLexer lexer, Operation op, Scope scope, boolean isRef) {
        super(lexer);

        if (scope == Scope.ARGUMENT) {
            varName = text.substring(1);
        } else if (!isRef) {
            varName = text.substring(2);
        }

        this.op = op;
        this.scope = scope;
        this.isRef = isRef;
    }

}
