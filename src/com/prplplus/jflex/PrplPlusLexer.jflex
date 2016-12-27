/* JFlex example: partial Java language lexer specification */
package com.prplplus.jflex;

import com.prplplus.jflex.symbols.*;
import com.prplplus.jflex.symbols.VarSymbol.*;
import com.prplplus.errors.*;

/**
 * This class is a simple example lexer.
 */
%%
 
%class PrplPlusLexer
%type Symbol
%public
%unicode
%line
%column

%{
  /* Here goes this code I guess */
  
    private String fileName;

    public PrplPlusLexer(java.io.Reader in, String fileName) {
        this(in);
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public int getLineNumber() {
         return yyline;
    }

    public int getColumnNumber() {
         return yycolumn;
    }
  
%}

Identifier = [a-zA-Z_][0-9a-zA-Z_]*

%xstate COMMENT

%%

/*  ----  VARIABLES  ----  */

/* Local variables */
\<\+{Identifier}	{ return new VarSymbol(this, Operation.READ, Scope.LOCAL, false); }
\+\>{Identifier}	{ return new VarSymbol(this, Operation.WRITE, Scope.LOCAL, false); }
\+\?{Identifier}	{ return new VarSymbol(this, Operation.EXISTS, Scope.LOCAL, false); }
\+\+{Identifier}	{ return new VarSymbol(this, Operation.DELETE, Scope.LOCAL, false); }

/* Local variables with reference */
"<+!"				{ return new VarSymbol(this, Operation.READ, Scope.LOCAL, true); }
"+>!"				{ return new VarSymbol(this, Operation.WRITE, Scope.LOCAL, true); }
"+?!"				{ return new VarSymbol(this, Operation.EXISTS, Scope.LOCAL, true); }
"++!"				{ return new VarSymbol(this, Operation.DELETE, Scope.LOCAL, true); }
"++?"				{ return new VarSymbol(this, Operation.DELETE, Scope.LOCAL, true); }

/* Semi-global variables */
\<\~{Identifier}	{ return new VarSymbol(this, Operation.READ, Scope.SEMI_GLOBAL, false); }
\~\>{Identifier}	{ return new VarSymbol(this, Operation.WRITE, Scope.SEMI_GLOBAL, false); }
\~\?{Identifier}	{ return new VarSymbol(this, Operation.EXISTS, Scope.SEMI_GLOBAL, false); }
\~\~{Identifier}	{ return new VarSymbol(this, Operation.DELETE, Scope.SEMI_GLOBAL, false); }

/* Semi-global variables with reference */
"<~!"				{ return new VarSymbol(this, Operation.READ, Scope.SEMI_GLOBAL, true); }
"~>!"				{ return new VarSymbol(this, Operation.WRITE, Scope.SEMI_GLOBAL, true); }
"~?!"				{ return new VarSymbol(this, Operation.EXISTS, Scope.SEMI_GLOBAL, true); }
"~~!"				{ return new VarSymbol(this, Operation.DELETE, Scope.SEMI_GLOBAL, true); }
"~~?"				{ return new VarSymbol(this, Operation.DELETE, Scope.SEMI_GLOBAL, true); }

/* Global variables */
\<\-{Identifier}	{ return new VarSymbol(this, Operation.READ, Scope.GLOBAL, false); }
\-\>{Identifier}	{ return new VarSymbol(this, Operation.WRITE, Scope.GLOBAL, false); }
\-\?{Identifier}	{ return new VarSymbol(this, Operation.EXISTS, Scope.GLOBAL, false); }
\-\-{Identifier}	{ return new VarSymbol(this, Operation.DELETE, Scope.GLOBAL, false); }

/* Global variables with reference */
"<-!"				{ return new VarSymbol(this, Operation.READ, Scope.GLOBAL, true); }
"->!"				{ return new VarSymbol(this, Operation.WRITE, Scope.GLOBAL, true); }
"-?!"				{ return new VarSymbol(this, Operation.EXISTS, Scope.GLOBAL, true); }
"--!"				{ return new VarSymbol(this, Operation.DELETE, Scope.GLOBAL, true); }
"--?"				{ return new VarSymbol(this, Operation.DELETE, Scope.GLOBAL, true); }

/* Global argument variables */
\${Identifier}  	{ return new VarSymbol(this, Operation.WRITE, Scope.ARGUMENT, false); }

/* Super global varaibles */
"<-*"               { return new Symbol(this); }
"->*"               { return new Symbol(this); }
"-?*"               { return new Symbol(this); }
"--*"               { return new Symbol(this); }

"<-!*"               { return new Symbol(this); }
"->!*"               { return new Symbol(this); }
"-?!*"               { return new Symbol(this); }
"--!*"               { return new Symbol(this); }
"--?*"               { return SpecialSymbol.withText(this, "--!*"); }



/*  ----  SPECIAL SYMBOLS  ----  */

"++%"				{ return new SpecialSymbol(this, SpecialSymbol.Type.LOCAL_PREFIX); }
"~~%"				{ return new SpecialSymbol(this, SpecialSymbol.Type.SEMI_GLOBAL_PREFIX); }
"--%"				{ return new SpecialSymbol(this, SpecialSymbol.Type.PRPL_PLUS_PREFIX); }

"%include"          { return new SpecialSymbol(this, SpecialSymbol.Type.INCLUDE); }
"%library"          { return new SpecialSymbol(this, SpecialSymbol.Type.LIBRARY); }
"%blockstart"       { return new SpecialSymbol(this, SpecialSymbol.Type.BLOCK_FOLD); }
"%blockend"         { return new SpecialSymbol(this, SpecialSymbol.Type.BLOCK_FOLD); }



/*  ----  WHITESPACE  ----  */
\#.*				{ return new WhitespaceSymbol(this); } /* comment */
[ \t]+				{ return new WhitespaceSymbol(this); } /* normal whitespace */
\r\n|\n|\r			{ return new WhitespaceSymbol(this); } /* new line */



/*  ----  USER FUNCTIONS  ----  */
\:{Identifier}		{ return new UserFunctionSymbol(this, true); } /* definition */
\@{Identifier}		{ return new UserFunctionSymbol(this, false); } /* usage */



/*  ---- RANDOM STUFF  ----  */
-?0x[0-9]+          { return SpecialSymbol.pasreBase16(this); } //hexadecimal constant
"("					{ return new ParSymbol(this, ParSymbol.Type.LEFT_PAR); } // Left (
")"					{ return new ParSymbol(this, ParSymbol.Type.RIGHT_PAR); } //Right )



/*  ----  COPY PASTA ----  */
{Identifier}		{ return new Symbol(this); } /* Build-in function/operator/whatever */
[\[\]\:\.]	    	{ return new Symbol(this); } /* List indexers and stuff */
-?[0-9]+(\.[0-9]+)?	{ return new Symbol(this); } /* Number constant */
\"[^\"\#]*\"		{ return new Symbol(this); } /* String constant */



/* ----  MULTI-LINE COMMENTS  ----  */
"/*"                        { yybegin(COMMENT); return SpecialSymbol.addComment(this); }
<COMMENT> {
([^\*\n\r]|\*[^\/\n\r])+    { return SpecialSymbol.addComment(this); }
\r\n|\n|\r			        { return new WhitespaceSymbol(this); } /* new line */
"*/"                        { yybegin(YYINITIAL); return SpecialSymbol.addComment(this);}
}



/*  ----  END OF FILE  ----  */
<<EOF>>				{ return new EOFSymbol(this); }



/* error fallback */
[^]                 { 
					Symbol s = new Symbol(this);
					ErrorHandler.reportError(ErrorHandler.ErrorType.INVALID_CHARACTER, s);
					return s;
					}



                    