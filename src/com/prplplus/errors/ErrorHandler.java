package com.prplplus.errors;

import com.prplplus.jflex.Symbol;

public class ErrorHandler {
    public static enum ErrorLevel {
        IGNORE, WARNING, ERROR;
    }

    public static enum ErrorType {
        // @formatter:off
        INVALID_CHARACTER(ErrorLevel.WARNING),
        UNEXPECTED_RIGHT_PAR(ErrorLevel.ERROR),
        UNCLOSED_LEFT_PAR(ErrorLevel.ERROR),
        NOT_INSIDE_FUNCTION(ErrorLevel.WARNING),
        HEXA_CONST_TOO_LARGE(ErrorLevel.ERROR),
        COMPILER_IN_TROUBLE(ErrorLevel.ERROR),
        INCLUDE_FAILED(ErrorLevel.ERROR),
        INCLUDE_INSIDE_FUNCTION(ErrorLevel.WARNING),
        INCLUDE_MISSING_FILENAME(ErrorLevel.ERROR),
        INCLUDE_FILE_NOT_FOUND(ErrorLevel.ERROR),
        INCLUDE_CYCLE_DETECTED(ErrorLevel.WARNING),
        COMPILATION_FAILED(ErrorLevel.ERROR);
        // @formatter:on

        public ErrorLevel level;

        private ErrorType(ErrorLevel level) {
            this.level = level;
        }
    }

    public static void reportError(ErrorType type, Symbol symbol) {
        System.out.format("Error %s on line %d at column %d with symbol '%s' in file '%s'%n",
                type, symbol.line, symbol.column, symbol.text, symbol.fileFrom);
    }

    public static void reportError(ErrorType type, String message) {
        System.out.format("Error %s: '%s'%n", type, message);
    }
}
