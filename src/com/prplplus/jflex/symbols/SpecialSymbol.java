package com.prplplus.jflex.symbols;

import com.prplplus.errors.ErrorHandler;
import com.prplplus.errors.ErrorHandler.ErrorType;
import com.prplplus.jflex.PrplPlusLexer;
import com.prplplus.jflex.Symbol;

public class SpecialSymbol extends Symbol {

    public static enum Type {
        LOCAL_PREFIX, SEMI_GLOBAL_PREFIX, PRPL_PLUS_PREFIX, BLOCK_FOLD, INCLUDE, LIBRARY
    }

    public Type type;

    public SpecialSymbol(PrplPlusLexer lexer, Type type) {
        super(lexer);
        this.type = type;
    }

    public static Symbol pasreBase16(PrplPlusLexer lexer) {
        Symbol result = new Symbol(lexer);

        int mul;
        if (result.text.charAt(0) == '-') {
            mul = -1;
            result.text = result.text.substring(3);
        } else {
            result.text = result.text.substring(2);
            mul = 1;
        }

        try {
            int value = Integer.parseUnsignedInt(result.text, 16);
            result.text = Integer.toString(value * mul);
        } catch (NumberFormatException ex) {
            ErrorHandler.reportError(ErrorType.HEXA_CONST_TOO_LARGE, result);
            ex.printStackTrace(System.out);
        }

        return result;
    }

    public static Symbol addComment(PrplPlusLexer lexer) {
        Symbol result = new WhitespaceSymbol(lexer);
        result.text = "#" + result.text;
        return result;
    }

}
