package com.prplplus.errors;

import java.awt.Color;

import com.prplplus.gui.CompilerPanel;
import com.prplplus.jflex.Symbol;

public class ErrorHandler {
    public static enum ErrorLevel {
        IGNORE("Ignored warning", Color.gray), WARNING("Warning", new Color(255, 128, 0)), ERROR("Error", Color.red);

        public String text;
        public Color color; //bad design FTW

        private ErrorLevel(String text, Color color) {
            this.text = text;
            this.color = color;
        }
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
        COMPILATION_FAILED(ErrorLevel.ERROR),
        TOO_MANY_FILES_SCANNED(ErrorLevel.ERROR);
        // @formatter:on

        public ErrorLevel level;

        private ErrorType(ErrorLevel level) {
            this.level = level;
        }
    }

    public static CompilerPanel errorSink;

    public static void reportError(ErrorType type, Symbol symbol) {
        String msg = String.format("%s %s on line %d at symbol '%s' in file '%s'",
                type.level.text, type, symbol.line, symbol.originalText, symbol.fileFrom);
        System.out.println(msg);
        if (errorSink != null) {
            errorSink.onError(type.level, msg);
        }
    }

    public static void reportError(ErrorType type, Symbol symbol, String addMsg) {
        String msg = String.format("%s %s on line %d at symbol '%s' in file '%s' (%s)",
                type.level.text, type, symbol.line, symbol.originalText, symbol.fileFrom, addMsg);
        System.out.println(msg);
        if (errorSink != null) {
            errorSink.onError(type.level, msg);
        }
    }

    public static void reportError(ErrorType type, String message) {
        String msg = String.format("%s %s: '%s'", type.level.text, type, message);
        System.out.println(msg);
        if (errorSink != null) {
            errorSink.onError(type.level, msg);
        }
    }
}
