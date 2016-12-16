package com.prplplus.errors;

import com.prplplus.jflex.Symbol;

public class ErrorHandler {
    public static enum ErrorLevel {
        IGNORE, WARNING, ERROR;
    }

    public static enum ErrorType {
        // @formatter:off
        INVALID_CHARACTER,
        UNEXPECTED_RIGHT_PAR,
        UNCLOSED_LEFT_PAR,
        NOT_INSIDE_FUNCTION,
        HEXA_CONST_TOO_LARGE,
        COMPILER_IN_TROUBLE,
        IMPORT_FAILED,
        INCLUDE_INSIDE_FUNCTION,
        INCLUDE_MISSING_FILENAME;
        // @formatter:on
    }

    public static void reportError(ErrorType type, Symbol symbol) {
        System.out.format("Error %s on line %d at column %d with symbol '%s' in file '%s'%n",
                type, symbol.line, symbol.column, symbol.text, symbol.fileFrom);
    }
}
