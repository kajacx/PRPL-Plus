package com.prplplus.jflex;

public class Symbol {
    public String fileFrom;
    public String text;
    public final String originalText;
    public int line;
    public int column;

    /**
     * Reads text, line number and column number from the lexer
     * @param lexer
     */
    public Symbol(PrplPlusLexer lexer) {
        originalText = text = lexer.yytext();
        line = lexer.getLineNumber() + 1;
        column = lexer.getColumnNumber() + 1;
        fileFrom = lexer.getFileName();
    }

    /**
     * Is this symbol NOT a whitespace or comment?
     * @return <code>false</code> if this Symbol is a whitespace or comment, <code>true</code> otherwise
     */
    public boolean isImportant() {
        return true;
    }

    public boolean isEOF() {
        return false;
    }

    public boolean isLeftPar() {
        return false;
    }

    public boolean isRightPar() {
        return false;
    }

    public boolean isFunctionDefinition() {
        return false;
    }

    public boolean isEndOfComment() {
        return originalText.equals("*/");
    }

    public boolean isString() {
        return text.length() > 2 && text.startsWith("\"") && text.endsWith("\"");
    }

    public boolean isLibrary() {
        return false;
    }

    public boolean isShareNamespace() {
        return false;
    }

    @Override
    public String toString() {
        return String.format("Symbol '%s':%d:%d: '%s'", fileFrom, line, column, text);
    }
}
