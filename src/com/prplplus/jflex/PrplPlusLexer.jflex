/* JFlex example: partial Java language lexer specification */
package com.prplplus.jflex;

import com.prplplus.jflex.symbols.*;
import com.prplplus.jflex.symbols.VarSymbol.*;

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
  
  public int getLineNumber() {
	  return yyline;
  }
  
  public int getColumnNumber() {
	  return yycolumn;
  }
  
%}

Identifier = [a-zA-Z_][0-9a-zA-Z_]*

%%

/*  ----  VARIABLES  ----  */

/* Local variables */
\<\+{Identifier}		{ return new VarSymbol(this, Operation.READ, Scope.LOCAL, false); }
\+\>{Identifier}		{ return new VarSymbol(this, Operation.WRITE, Scope.LOCAL, false); }
\+\?{Identifier}		{ return new VarSymbol(this, Operation.EXISTS, Scope.LOCAL, false); }
\+\+{Identifier}		{ return new VarSymbol(this, Operation.DELETE, Scope.LOCAL, false); }

/* Local variables with reference */
"<+!"				{ return new VarSymbol(this, Operation.READ, Scope.LOCAL, true); }
"+>!"				{ return new VarSymbol(this, Operation.WRITE, Scope.LOCAL, true); }
"+?!"				{ return new VarSymbol(this, Operation.EXISTS, Scope.LOCAL, true); }
"++?"				{ return new VarSymbol(this, Operation.DELETE, Scope.LOCAL, true); }

/* Semi-global variables */
\<\-{Identifier}		{ return new VarSymbol(this, Operation.READ, Scope.SEMI_GLOBAL, false); }
\-\>{Identifier}		{ return new VarSymbol(this, Operation.WRITE, Scope.SEMI_GLOBAL, false); }
\-\?{Identifier}		{ return new VarSymbol(this, Operation.EXISTS, Scope.SEMI_GLOBAL, false); }
\-\-{Identifier}		{ return new VarSymbol(this, Operation.DELETE, Scope.SEMI_GLOBAL, false); }

/* Semi-global variables with reference */
"<-!"				{ return new VarSymbol(this, Operation.READ, Scope.SEMI_GLOBAL, true); }
"->!"				{ return new VarSymbol(this, Operation.WRITE, Scope.SEMI_GLOBAL, true); }
"-?!"				{ return new VarSymbol(this, Operation.EXISTS, Scope.SEMI_GLOBAL, true); }
"--?"				{ return new VarSymbol(this, Operation.DELETE, Scope.SEMI_GLOBAL, true); }

/* Global variables */
\<\*{Identifier}		{ return new VarSymbol(this, Operation.READ, Scope.SEMI_GLOBAL, false); }
\*\>{Identifier}		{ return new VarSymbol(this, Operation.WRITE, Scope.SEMI_GLOBAL, false); }
\*\?{Identifier}		{ return new VarSymbol(this, Operation.EXISTS, Scope.SEMI_GLOBAL, false); }
\*\*{Identifier}		{ return new VarSymbol(this, Operation.DELETE, Scope.SEMI_GLOBAL, false); }

/* Global variables with reference */
"<*!"				{ return new VarSymbol(this, Operation.READ, Scope.SEMI_GLOBAL, true); }
"*>!"				{ return new VarSymbol(this, Operation.WRITE, Scope.SEMI_GLOBAL, true); }
"*?!"				{ return new VarSymbol(this, Operation.EXISTS, Scope.SEMI_GLOBAL, true); }
"**?"				{ return new VarSymbol(this, Operation.DELETE, Scope.SEMI_GLOBAL, true); }

/* Global argument variables */
\${Identifier}		{ return new VarSymbol(this, Operation.WRITE, Scope.ARGUMENT, true); }


/*  ----  WHITESPACE  ----  */
\#.*				{ return new WhitespaceSymbol(this); } /* comment */
[ \t]+				{ return new WhitespaceSymbol(this); }
\r\n|\n|\r			{ return new WhitespaceSymbol(this); }



/*  ----  USER FUNCTIONS  ----  */
\:{Identifier}		{ return new UserFunctionSymbol(this, true); } /* definition */
\@{Identifier}		{ return new UserFunctionSymbol(this, false); } /* usage */



/* ----  COPY PASTA ----  */
{Identifier}		{ return new Symbol(this); } /* Build-in function */
[\:\[\]]			{ return new Symbol(this); } /* Random characters */



/* error fallback */
[^]                 { throw new Error("Illegal character <"+yytext()+">"); }
                                                        