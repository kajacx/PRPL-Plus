/* The following code was generated by JFlex 1.6.1 */

/* JFlex example: partial Java language lexer specification */
package com.prplplus.jflex;

import com.prplplus.jflex.symbols.*;
import com.prplplus.jflex.symbols.VarSymbol.*;
import com.prplplus.errors.*;

/**
 * This class is a simple example lexer.
 */

public class PrplPlusLexer {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;
  public static final int COMMENT = 2;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0,  0,  1, 1
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\37\1\34\1\36\1\36\1\35\22\0\1\37\1\7\1\50"+
    "\1\33\1\12\1\13\2\0\1\44\1\45\1\52\1\4\1\0\1\10"+
    "\1\47\1\51\1\42\11\2\1\40\1\0\1\3\1\0\1\5\1\6"+
    "\1\41\32\1\1\46\1\0\1\46\1\0\1\1\1\0\1\25\1\23"+
    "\1\16\1\21\1\22\3\1\1\14\1\1\1\30\1\17\1\1\1\15"+
    "\1\27\2\1\1\24\1\31\1\32\1\20\2\1\1\43\1\26\1\1"+
    "\3\0\1\11\6\0\1\36\u1fa2\0\1\36\1\36\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\udfe6\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\2\0\1\1\2\2\6\1\4\3\1\2\1\1\1\2"+
    "\1\4\1\5\1\2\2\1\1\6\13\0\1\7\3\0"+
    "\1\10\1\11\2\0\1\12\1\0\1\13\1\2\1\14"+
    "\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24"+
    "\1\25\1\26\1\27\1\30\1\31\1\32\1\33\1\34"+
    "\1\35\1\36\3\0\1\37\15\0\1\40\1\41\2\0"+
    "\1\42\2\0";

  private static int [] zzUnpackAction() {
    int [] result = new int[90];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\53\0\126\0\201\0\254\0\327\0\u0102\0\u012d"+
    "\0\u0158\0\u0183\0\u01ae\0\u01d9\0\126\0\u0204\0\u022f\0\u025a"+
    "\0\u0285\0\u02b0\0\126\0\126\0\126\0\u02db\0\u0306\0\u0331"+
    "\0\u035c\0\u0387\0\u03b2\0\u03dd\0\u0408\0\u0433\0\u045e\0\u0489"+
    "\0\u04b4\0\u04df\0\u050a\0\u0535\0\u0560\0\u058b\0\u05b6\0\u05e1"+
    "\0\u060c\0\u0637\0\u02db\0\126\0\u0662\0\126\0\u0387\0\u068d"+
    "\0\126\0\u06b8\0\126\0\u06e3\0\126\0\126\0\u070e\0\126"+
    "\0\u0739\0\126\0\u0764\0\126\0\u078f\0\126\0\u07ba\0\126"+
    "\0\126\0\126\0\u07e5\0\u0810\0\u083b\0\u0637\0\u0866\0\u0891"+
    "\0\u08bc\0\u08e7\0\u0912\0\u093d\0\u0968\0\u0993\0\u09be\0\u09e9"+
    "\0\u0a14\0\u0a3f\0\u0a6a\0\126\0\126\0\u0a95\0\u0ac0\0\126"+
    "\0\u0aeb\0\u0b16";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[90];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\3\1\4\1\5\1\6\1\7\3\3\1\10\1\11"+
    "\1\12\1\13\17\4\1\14\1\15\1\16\1\3\1\17"+
    "\1\20\1\21\1\22\1\4\1\23\1\24\1\25\1\3"+
    "\1\26\1\27\1\3\34\30\1\15\1\16\14\30\1\31"+
    "\54\0\2\4\11\0\17\4\7\0\2\4\11\0\1\5"+
    "\37\0\1\5\4\0\1\32\7\0\1\33\3\0\2\34"+
    "\45\0\1\35\1\36\1\37\46\0\1\5\2\0\1\40"+
    "\1\41\1\0\1\42\31\0\1\22\15\0\1\40\1\41"+
    "\2\0\1\43\42\0\1\44\12\0\17\44\10\0\1\44"+
    "\23\0\1\45\2\0\1\46\3\0\1\47\27\0\34\14"+
    "\3\0\14\14\34\0\1\15\55\0\1\17\14\0\1\50"+
    "\12\0\17\50\10\0\1\50\10\0\1\51\12\0\17\51"+
    "\10\0\1\51\11\0\1\5\37\0\1\5\1\52\3\0"+
    "\1\32\3\0\33\53\1\0\14\53\1\25\2\53\52\0"+
    "\1\54\34\30\2\0\14\30\1\55\34\30\2\0\13\30"+
    "\1\56\1\30\2\0\1\57\37\0\1\57\11\0\1\60"+
    "\5\0\1\61\4\0\17\60\10\0\1\60\10\0\1\62"+
    "\5\0\1\63\4\0\17\62\10\0\1\62\10\0\1\64"+
    "\4\0\1\65\4\0\1\66\17\64\10\0\1\64\10\0"+
    "\1\67\5\0\1\70\4\0\17\67\10\0\1\67\10\0"+
    "\1\71\5\0\1\72\4\0\17\71\10\0\1\71\10\0"+
    "\1\73\5\0\1\74\4\0\17\73\10\0\1\73\10\0"+
    "\1\75\5\0\1\76\4\0\17\75\10\0\1\75\10\0"+
    "\1\77\4\0\1\100\4\0\1\101\17\77\10\0\1\77"+
    "\10\0\1\77\4\0\1\100\4\0\1\102\17\77\10\0"+
    "\1\77\10\0\2\44\11\0\17\44\7\0\2\44\24\0"+
    "\1\103\51\0\1\104\55\0\1\105\34\0\2\50\11\0"+
    "\17\50\7\0\2\50\10\0\2\51\11\0\17\51\7\0"+
    "\2\51\11\0\1\106\37\0\1\106\10\0\34\30\2\0"+
    "\13\30\1\0\1\30\1\0\2\60\11\0\17\60\7\0"+
    "\2\60\10\0\2\62\11\0\17\62\7\0\2\62\10\0"+
    "\2\64\11\0\17\64\7\0\2\64\10\0\2\67\11\0"+
    "\17\67\7\0\2\67\10\0\2\71\11\0\17\71\7\0"+
    "\2\71\10\0\2\73\11\0\17\73\7\0\2\73\10\0"+
    "\2\75\11\0\17\75\7\0\2\75\10\0\2\77\11\0"+
    "\17\77\7\0\2\77\25\0\1\107\57\0\1\110\56\0"+
    "\1\111\42\0\1\112\57\0\1\113\44\0\1\114\54\0"+
    "\1\115\57\0\1\116\55\0\1\117\43\0\1\120\55\0"+
    "\1\121\50\0\1\122\6\0\1\123\43\0\1\124\56\0"+
    "\1\125\41\0\1\126\67\0\1\127\41\0\1\130\56\0"+
    "\1\131\51\0\1\132\60\0\1\130\20\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[2881];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unknown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\2\0\1\11\11\1\1\11\5\1\3\11\3\1\13\0"+
    "\1\1\3\0\2\1\2\0\1\11\1\0\1\11\2\1"+
    "\1\11\1\1\1\11\1\1\2\11\1\1\1\11\1\1"+
    "\1\11\1\1\1\11\1\1\1\11\1\1\3\11\3\0"+
    "\1\1\15\0\2\11\2\0\1\11\2\0";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[90];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;
  
  /** 
   * The number of occupied positions in zzBuffer beyond zzEndRead.
   * When a lead/high surrogate has been read from the input stream
   * into the final zzBuffer position, this will have a value of 1;
   * otherwise, it will have a value of 0.
   */
  private int zzFinalHighSurrogate = 0;

  /* user code: */
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
  


  /**
   * Creates a new scanner
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public PrplPlusLexer(java.io.Reader in) {
    this.zzReader = in;
  }


  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x110000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 168) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length - zzFinalHighSurrogate) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzBuffer.length*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
    }

    /* fill the buffer with new input */
    int requested = zzBuffer.length - zzEndRead;
    int numRead = zzReader.read(zzBuffer, zzEndRead, requested);

    /* not supposed to occur according to specification of java.io.Reader */
    if (numRead == 0) {
      throw new java.io.IOException("Reader returned 0 characters. See JFlex examples for workaround.");
    }
    if (numRead > 0) {
      zzEndRead += numRead;
      /* If numRead == requested, we might have requested to few chars to
         encode a full Unicode character. We assume that a Reader would
         otherwise never return half characters. */
      if (numRead == requested) {
        if (Character.isHighSurrogate(zzBuffer[zzEndRead - 1])) {
          --zzEndRead;
          zzFinalHighSurrogate = 1;
        }
      }
      /* potentially more input available */
      return false;
    }

    /* numRead < 0 ==> end of stream */
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * Internal scan buffer is resized down to its initial length, if it has grown.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEOFDone = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    zzFinalHighSurrogate = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
    if (zzBuffer.length > ZZ_BUFFERSIZE)
      zzBuffer = new char[ZZ_BUFFERSIZE];
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public Symbol yylex() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      boolean zzR = false;
      int zzCh;
      int zzCharCount;
      for (zzCurrentPosL = zzStartRead  ;
           zzCurrentPosL < zzMarkedPosL ;
           zzCurrentPosL += zzCharCount ) {
        zzCh = Character.codePointAt(zzBufferL, zzCurrentPosL, zzMarkedPosL);
        zzCharCount = Character.charCount(zzCh);
        switch (zzCh) {
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
          yyline++;
          yycolumn = 0;
          zzR = false;
          break;
        case '\r':
          yyline++;
          yycolumn = 0;
          zzR = true;
          break;
        case '\n':
          if (zzR)
            zzR = false;
          else {
            yyline++;
            yycolumn = 0;
          }
          break;
        default:
          zzR = false;
          yycolumn += zzCharCount;
        }
      }

      if (zzR) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean zzPeek;
        if (zzMarkedPosL < zzEndReadL)
          zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        else if (zzAtEOF)
          zzPeek = false;
        else {
          boolean eof = zzRefill();
          zzEndReadL = zzEndRead;
          zzMarkedPosL = zzMarkedPos;
          zzBufferL = zzBuffer;
          if (eof) 
            zzPeek = false;
          else 
            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        }
        if (zzPeek) yyline--;
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];

      // set up zzAction for empty match case:
      int zzAttributes = zzAttrL[zzState];
      if ( (zzAttributes & 1) == 1 ) {
        zzAction = zzState;
      }


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL) {
            zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
            zzCurrentPosL += Character.charCount(zzInput);
          }
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
              zzCurrentPosL += Character.charCount(zzInput);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
        zzAtEOF = true;
              {
                return new EOFSymbol(this);
              }
      }
      else {
        switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
          case 1: 
            { Symbol s = new Symbol(this);
					ErrorHandler.reportError(ErrorHandler.ErrorType.INVALID_CHARACTER, s);
					return s;
            }
          case 35: break;
          case 2: 
            { return new Symbol(this);
            }
          case 36: break;
          case 3: 
            { return new WhitespaceSymbol(this);
            }
          case 37: break;
          case 4: 
            { return new ParSymbol(this, ParSymbol.Type.LEFT_PAR);
            }
          case 38: break;
          case 5: 
            { return new ParSymbol(this, ParSymbol.Type.RIGHT_PAR);
            }
          case 39: break;
          case 6: 
            { return SpecialSymbol.addComment(this);
            }
          case 40: break;
          case 7: 
            { return new VarSymbol(this, Operation.WRITE, Scope.ARGUMENT, true);
            }
          case 41: break;
          case 8: 
            { return new UserFunctionSymbol(this, true);
            }
          case 42: break;
          case 9: 
            { return new UserFunctionSymbol(this, false);
            }
          case 43: break;
          case 10: 
            { yybegin(COMMENT); return SpecialSymbol.addComment(this);
            }
          case 44: break;
          case 11: 
            { yybegin(YYINITIAL); return SpecialSymbol.addComment(this);
            }
          case 45: break;
          case 12: 
            { return new VarSymbol(this, Operation.READ, Scope.LOCAL, false);
            }
          case 46: break;
          case 13: 
            { return new VarSymbol(this, Operation.READ, Scope.LOCAL, true);
            }
          case 47: break;
          case 14: 
            { return new VarSymbol(this, Operation.READ, Scope.SEMI_GLOBAL, false);
            }
          case 48: break;
          case 15: 
            { return new VarSymbol(this, Operation.READ, Scope.SEMI_GLOBAL, true);
            }
          case 49: break;
          case 16: 
            { return new VarSymbol(this, Operation.DELETE, Scope.LOCAL, false);
            }
          case 50: break;
          case 17: 
            { return new VarSymbol(this, Operation.DELETE, Scope.LOCAL, true);
            }
          case 51: break;
          case 18: 
            { return new SpecialSymbol(this, SpecialSymbol.Type.LOCAL_PREFIX);
            }
          case 52: break;
          case 19: 
            { return new VarSymbol(this, Operation.WRITE, Scope.LOCAL, false);
            }
          case 53: break;
          case 20: 
            { return new VarSymbol(this, Operation.WRITE, Scope.LOCAL, true);
            }
          case 54: break;
          case 21: 
            { return new VarSymbol(this, Operation.EXISTS, Scope.LOCAL, false);
            }
          case 55: break;
          case 22: 
            { return new VarSymbol(this, Operation.EXISTS, Scope.LOCAL, true);
            }
          case 56: break;
          case 23: 
            { return new VarSymbol(this, Operation.WRITE, Scope.SEMI_GLOBAL, false);
            }
          case 57: break;
          case 24: 
            { return new VarSymbol(this, Operation.WRITE, Scope.SEMI_GLOBAL, true);
            }
          case 58: break;
          case 25: 
            { return new VarSymbol(this, Operation.EXISTS, Scope.SEMI_GLOBAL, false);
            }
          case 59: break;
          case 26: 
            { return new VarSymbol(this, Operation.EXISTS, Scope.SEMI_GLOBAL, true);
            }
          case 60: break;
          case 27: 
            { return new VarSymbol(this, Operation.DELETE, Scope.SEMI_GLOBAL, false);
            }
          case 61: break;
          case 28: 
            { return new VarSymbol(this, Operation.DELETE, Scope.SEMI_GLOBAL, true);
            }
          case 62: break;
          case 29: 
            { return new SpecialSymbol(this, SpecialSymbol.Type.SEMI_GLOBAL_PREFIX);
            }
          case 63: break;
          case 30: 
            { return new SpecialSymbol(this, SpecialSymbol.Type.PRPL_PLUS_PREFIX);
            }
          case 64: break;
          case 31: 
            { return SpecialSymbol.pasreBase16(this);
            }
          case 65: break;
          case 32: 
            { return new SpecialSymbol(this, SpecialSymbol.Type.INCLUDE);
            }
          case 66: break;
          case 33: 
            { return new SpecialSymbol(this, SpecialSymbol.Type.LIBRARY);
            }
          case 67: break;
          case 34: 
            { return new SpecialSymbol(this, SpecialSymbol.Type.BLOCK_FOLD);
            }
          case 68: break;
          default:
            zzScanError(ZZ_NO_MATCH);
        }
      }
    }
  }


}
