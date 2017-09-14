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
    "\11\0\1\47\1\44\1\46\1\46\1\45\22\0\1\47\1\10\1\60"+
    "\1\43\1\13\1\15\1\0\1\4\1\54\1\55\1\14\1\7\1\0"+
    "\1\12\1\57\1\61\1\52\11\2\1\50\1\0\1\3\1\0\1\5"+
    "\1\6\1\51\32\1\1\56\1\0\1\56\1\0\1\1\1\0\1\31"+
    "\1\30\1\22\1\25\1\26\2\1\1\37\1\20\1\1\1\34\1\23"+
    "\1\40\1\21\1\33\1\41\1\1\1\27\1\35\1\36\1\24\1\42"+
    "\1\1\1\53\1\32\1\1\1\16\1\0\1\17\1\11\6\0\1\46"+
    "\u1fa2\0\1\46\1\46\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\udfe6\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\2\0\1\1\2\2\7\1\1\3\1\4\4\5\1\2"+
    "\1\1\1\2\1\6\1\7\1\2\2\1\1\10\23\0"+
    "\1\11\5\0\1\12\1\13\2\0\1\14\1\0\1\15"+
    "\1\2\1\16\1\17\1\20\1\21\1\22\1\23\1\24"+
    "\1\25\1\26\1\27\1\30\1\31\1\32\1\33\1\34"+
    "\1\35\1\36\1\37\1\40\1\41\1\42\1\43\1\44"+
    "\1\45\1\46\1\47\1\50\1\51\1\52\1\53\1\54"+
    "\1\55\1\56\2\57\1\60\5\0\1\61\1\62\31\0"+
    "\1\63\1\64\7\0\1\65\2\0\1\66\3\0\1\67"+
    "\3\0";

  private static int [] zzUnpackAction() {
    int [] result = new int[148];
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
    "\0\0\0\62\0\144\0\226\0\310\0\372\0\u012c\0\u015e"+
    "\0\u0190\0\u01c2\0\u01f4\0\u0226\0\144\0\144\0\u0258\0\144"+
    "\0\u028a\0\u02bc\0\u02ee\0\u0320\0\u0352\0\144\0\144\0\144"+
    "\0\u0384\0\u03b6\0\u03e8\0\u041a\0\u044c\0\u047e\0\u04b0\0\u04e2"+
    "\0\u0514\0\u0546\0\u0578\0\u05aa\0\u05dc\0\u060e\0\u0640\0\u0672"+
    "\0\u06a4\0\u06d6\0\u0708\0\u073a\0\u076c\0\u079e\0\u07d0\0\u0802"+
    "\0\u0834\0\u0866\0\u0898\0\u08ca\0\u08fc\0\u092e\0\u0960\0\u0384"+
    "\0\144\0\u0992\0\144\0\u044c\0\u09c4\0\144\0\u09f6\0\144"+
    "\0\u0a28\0\144\0\u0a5a\0\u0a8c\0\144\0\u0abe\0\144\0\u0af0"+
    "\0\144\0\u0b22\0\u0b54\0\144\0\u0b86\0\144\0\u0bb8\0\144"+
    "\0\144\0\u0bea\0\144\0\u0c1c\0\144\0\u0c4e\0\144\0\144"+
    "\0\u0c80\0\u0a8c\0\u0cb2\0\u0a8c\0\u0ce4\0\u0d16\0\u0a8c\0\144"+
    "\0\u0d48\0\u0d7a\0\u0dac\0\u0dde\0\u0e10\0\u0960\0\144\0\u0e42"+
    "\0\u0e74\0\u0ea6\0\u0ed8\0\u0f0a\0\u0f3c\0\u0f6e\0\u0fa0\0\u0fd2"+
    "\0\u1004\0\u1036\0\u1068\0\u109a\0\u10cc\0\u10fe\0\u1130\0\u1162"+
    "\0\u1194\0\u11c6\0\u11f8\0\u122a\0\u125c\0\u128e\0\u12c0\0\u12f2"+
    "\0\144\0\144\0\u1324\0\u1356\0\u1388\0\u13ba\0\u13ec\0\u141e"+
    "\0\u1450\0\144\0\u1482\0\u14b4\0\144\0\u14e6\0\u1518\0\u154a"+
    "\0\144\0\u157c\0\u15ae\0\u15e0";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[148];
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
    "\1\3\1\4\1\5\1\6\1\7\2\3\1\10\1\3"+
    "\1\11\1\12\1\13\1\3\1\14\1\15\1\16\23\4"+
    "\1\17\1\20\1\21\1\3\1\22\1\23\1\24\1\25"+
    "\1\4\1\26\1\27\2\30\1\31\1\32\14\33\1\34"+
    "\27\33\1\20\1\21\14\33\63\0\2\4\15\0\23\4"+
    "\7\0\2\4\10\0\1\5\47\0\1\5\4\0\1\35"+
    "\6\0\1\36\2\0\1\37\1\0\1\40\1\41\53\0"+
    "\1\42\1\43\1\44\1\45\57\0\1\46\1\47\1\50"+
    "\57\0\1\51\1\52\2\0\1\53\52\0\1\5\2\0"+
    "\1\54\1\55\3\0\1\56\37\0\1\25\10\0\1\57"+
    "\16\0\23\57\10\0\1\57\26\0\1\60\2\0\1\61"+
    "\3\0\1\62\1\63\4\0\1\64\24\0\44\17\3\0"+
    "\13\17\44\0\1\20\64\0\1\22\13\0\1\65\16\0"+
    "\23\65\10\0\1\65\7\0\1\66\16\0\23\66\10\0"+
    "\1\66\10\0\1\5\47\0\1\5\1\67\3\0\1\35"+
    "\2\0\60\70\1\30\1\70\14\0\1\71\45\0\14\33"+
    "\1\72\27\33\2\0\60\33\2\0\13\33\1\73\2\0"+
    "\1\74\47\0\1\74\10\0\1\75\6\0\1\76\7\0"+
    "\23\75\10\0\1\75\7\0\1\77\6\0\1\100\7\0"+
    "\23\77\10\0\1\77\7\0\1\101\6\0\1\102\7\0"+
    "\23\101\10\0\1\101\7\0\1\103\6\0\1\104\3\0"+
    "\1\30\3\0\23\103\10\0\1\103\14\0\1\105\1\0"+
    "\1\105\52\0\1\106\6\0\1\107\7\0\23\106\10\0"+
    "\1\106\7\0\1\110\6\0\1\111\7\0\23\110\10\0"+
    "\1\110\7\0\1\112\16\0\23\112\10\0\1\112\7\0"+
    "\1\113\6\0\1\114\7\0\23\113\10\0\1\113\7\0"+
    "\1\115\6\0\1\116\7\0\23\115\10\0\1\115\7\0"+
    "\1\117\4\0\1\120\1\0\1\120\4\0\1\121\2\0"+
    "\23\117\10\0\1\117\7\0\1\122\6\0\1\123\7\0"+
    "\23\122\10\0\1\122\7\0\1\124\6\0\1\125\7\0"+
    "\23\124\10\0\1\124\7\0\1\126\4\0\1\127\1\0"+
    "\1\127\4\0\1\130\2\0\23\126\10\0\1\126\7\0"+
    "\1\131\6\0\1\132\3\0\1\30\3\0\23\131\10\0"+
    "\1\131\7\0\1\133\6\0\1\134\3\0\1\30\3\0"+
    "\23\133\10\0\1\133\7\0\1\135\4\0\1\136\1\0"+
    "\1\137\3\0\1\30\1\140\2\0\23\135\10\0\1\135"+
    "\7\0\2\57\15\0\23\57\7\0\2\57\27\0\1\141"+
    "\60\0\1\142\67\0\1\143\56\0\1\144\75\0\1\145"+
    "\23\0\2\65\15\0\23\65\7\0\2\65\7\0\2\66"+
    "\15\0\23\66\7\0\2\66\10\0\1\146\47\0\1\146"+
    "\7\0\44\33\2\0\13\33\2\0\2\75\15\0\23\75"+
    "\7\0\2\75\7\0\2\77\15\0\23\77\7\0\2\77"+
    "\7\0\2\101\15\0\23\101\7\0\2\101\7\0\2\103"+
    "\15\0\23\103\7\0\2\103\22\0\1\30\46\0\2\106"+
    "\15\0\23\106\7\0\2\106\7\0\2\110\15\0\23\110"+
    "\7\0\2\110\7\0\2\112\15\0\23\112\7\0\2\112"+
    "\7\0\2\113\15\0\23\113\7\0\2\113\7\0\2\115"+
    "\15\0\23\115\7\0\2\115\7\0\2\117\15\0\23\117"+
    "\7\0\2\117\7\0\2\122\15\0\23\122\7\0\2\122"+
    "\7\0\2\124\15\0\23\124\7\0\2\124\7\0\2\126"+
    "\15\0\23\126\7\0\2\126\7\0\2\131\15\0\23\131"+
    "\7\0\2\131\7\0\2\133\15\0\23\133\7\0\2\133"+
    "\7\0\2\135\15\0\23\135\7\0\2\135\22\0\1\147"+
    "\67\0\1\150\67\0\1\151\53\0\1\152\1\153\71\0"+
    "\1\154\57\0\1\155\53\0\1\156\65\0\1\157\56\0"+
    "\1\160\55\0\1\161\63\0\1\162\66\0\1\163\56\0"+
    "\1\164\66\0\1\165\57\0\1\166\53\0\1\167\74\0"+
    "\1\170\53\0\1\171\60\0\1\172\63\0\1\173\67\0"+
    "\1\174\46\0\1\175\65\0\1\176\6\0\1\177\45\0"+
    "\1\200\66\0\1\201\65\0\1\202\47\0\1\203\64\0"+
    "\1\204\57\0\1\205\76\0\1\206\54\0\1\207\72\0"+
    "\1\210\43\0\1\211\62\0\1\212\65\0\1\213\70\0"+
    "\1\214\47\0\1\215\60\0\1\216\63\0\1\217\60\0"+
    "\1\220\61\0\1\221\71\0\1\212\60\0\1\222\65\0"+
    "\1\223\51\0\1\224\52\0\1\210\37\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[5650];
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
    "\2\0\1\11\11\1\2\11\1\1\1\11\5\1\3\11"+
    "\3\1\23\0\1\1\5\0\2\1\2\0\1\11\1\0"+
    "\1\11\2\1\1\11\1\1\1\11\1\1\1\11\2\1"+
    "\1\11\1\1\1\11\1\1\1\11\2\1\1\11\1\1"+
    "\1\11\1\1\2\11\1\1\1\11\1\1\1\11\1\1"+
    "\2\11\7\1\1\11\5\0\1\1\1\11\31\0\2\11"+
    "\7\0\1\11\2\0\1\11\3\0\1\11\3\0";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[148];
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
    while (i < 180) {
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
          case 56: break;
          case 2: 
            { return new Symbol(this);
            }
          case 57: break;
          case 3: 
            { return SpecialSymbol.withText(this, "\"[\"");
            }
          case 58: break;
          case 4: 
            { return SpecialSymbol.withText(this, "\"]\"");
            }
          case 59: break;
          case 5: 
            { return new WhitespaceSymbol(this);
            }
          case 60: break;
          case 6: 
            { return new ParSymbol(this, ParSymbol.Type.LEFT_PAR);
            }
          case 61: break;
          case 7: 
            { return new ParSymbol(this, ParSymbol.Type.RIGHT_PAR);
            }
          case 62: break;
          case 8: 
            { return SpecialSymbol.addComment(this);
            }
          case 63: break;
          case 9: 
            { return new VarSymbol(this, Operation.WRITE, Scope.ARGUMENT, false);
            }
          case 64: break;
          case 10: 
            { return new UserFunctionSymbol(this, true);
            }
          case 65: break;
          case 11: 
            { return new UserFunctionSymbol(this, false);
            }
          case 66: break;
          case 12: 
            { yybegin(COMMENT); return SpecialSymbol.addComment(this);
            }
          case 67: break;
          case 13: 
            { yybegin(YYINITIAL); return SpecialSymbol.addComment(this);
            }
          case 68: break;
          case 14: 
            { return new VarSymbol(this, Operation.READ, Scope.RECURSION, false);
            }
          case 69: break;
          case 15: 
            { return new VarSymbol(this, Operation.READ, Scope.RECURSION, true);
            }
          case 70: break;
          case 16: 
            { return new VarSymbol(this, Operation.READ, Scope.LOCAL, false);
            }
          case 71: break;
          case 17: 
            { return new VarSymbol(this, Operation.READ, Scope.LOCAL, true);
            }
          case 72: break;
          case 18: 
            { return new VarSymbol(this, Operation.READ, Scope.SEMI_GLOBAL, false);
            }
          case 73: break;
          case 19: 
            { return new VarSymbol(this, Operation.READ, Scope.SEMI_GLOBAL, true);
            }
          case 74: break;
          case 20: 
            { return new VarSymbol(this, Operation.READ, Scope.GLOBAL, false);
            }
          case 75: break;
          case 21: 
            { return new VarSymbol(this, Operation.READ, Scope.GLOBAL, true);
            }
          case 76: break;
          case 22: 
            { return new VarSymbol(this, Operation.DELETE, Scope.RECURSION, true);
            }
          case 77: break;
          case 23: 
            { return new VarSymbol(this, Operation.WRITE, Scope.RECURSION, false);
            }
          case 78: break;
          case 24: 
            { return new VarSymbol(this, Operation.WRITE, Scope.RECURSION, true);
            }
          case 79: break;
          case 25: 
            { return new VarSymbol(this, Operation.EXISTS, Scope.RECURSION, false);
            }
          case 80: break;
          case 26: 
            { return new VarSymbol(this, Operation.EXISTS, Scope.RECURSION, true);
            }
          case 81: break;
          case 27: 
            { return new VarSymbol(this, Operation.DELETE, Scope.RECURSION, false);
            }
          case 82: break;
          case 28: 
            { return new VarSymbol(this, Operation.WRITE, Scope.LOCAL, false);
            }
          case 83: break;
          case 29: 
            { return new VarSymbol(this, Operation.WRITE, Scope.LOCAL, true);
            }
          case 84: break;
          case 30: 
            { return new VarSymbol(this, Operation.EXISTS, Scope.LOCAL, false);
            }
          case 85: break;
          case 31: 
            { return new VarSymbol(this, Operation.EXISTS, Scope.LOCAL, true);
            }
          case 86: break;
          case 32: 
            { return new VarSymbol(this, Operation.DELETE, Scope.LOCAL, false);
            }
          case 87: break;
          case 33: 
            { return new VarSymbol(this, Operation.DELETE, Scope.LOCAL, true);
            }
          case 88: break;
          case 34: 
            { return new SpecialSymbol(this, SpecialSymbol.Type.LOCAL_PREFIX);
            }
          case 89: break;
          case 35: 
            { return new VarSymbol(this, Operation.WRITE, Scope.SEMI_GLOBAL, false);
            }
          case 90: break;
          case 36: 
            { return new VarSymbol(this, Operation.WRITE, Scope.SEMI_GLOBAL, true);
            }
          case 91: break;
          case 37: 
            { return new VarSymbol(this, Operation.EXISTS, Scope.SEMI_GLOBAL, false);
            }
          case 92: break;
          case 38: 
            { return new VarSymbol(this, Operation.EXISTS, Scope.SEMI_GLOBAL, true);
            }
          case 93: break;
          case 39: 
            { return new VarSymbol(this, Operation.DELETE, Scope.SEMI_GLOBAL, false);
            }
          case 94: break;
          case 40: 
            { return new VarSymbol(this, Operation.DELETE, Scope.SEMI_GLOBAL, true);
            }
          case 95: break;
          case 41: 
            { return new SpecialSymbol(this, SpecialSymbol.Type.SEMI_GLOBAL_PREFIX);
            }
          case 96: break;
          case 42: 
            { return new VarSymbol(this, Operation.WRITE, Scope.GLOBAL, false);
            }
          case 97: break;
          case 43: 
            { return new VarSymbol(this, Operation.WRITE, Scope.GLOBAL, true);
            }
          case 98: break;
          case 44: 
            { return new VarSymbol(this, Operation.EXISTS, Scope.GLOBAL, false);
            }
          case 99: break;
          case 45: 
            { return new VarSymbol(this, Operation.EXISTS, Scope.GLOBAL, true);
            }
          case 100: break;
          case 46: 
            { return new VarSymbol(this, Operation.DELETE, Scope.GLOBAL, false);
            }
          case 101: break;
          case 47: 
            { return new VarSymbol(this, Operation.DELETE, Scope.GLOBAL, true);
            }
          case 102: break;
          case 48: 
            { return new SpecialSymbol(this, SpecialSymbol.Type.PRPL_PLUS_PREFIX);
            }
          case 103: break;
          case 49: 
            { return SpecialSymbol.pasreBase16(this);
            }
          case 104: break;
          case 50: 
            { return SpecialSymbol.withText(this, "--!*");
            }
          case 105: break;
          case 51: 
            { return new SpecialSymbol(this, SpecialSymbol.Type.INCLUDE);
            }
          case 106: break;
          case 52: 
            { return new SpecialSymbol(this, SpecialSymbol.Type.LIBRARY);
            }
          case 107: break;
          case 53: 
            { return new SpecialSymbol(this, SpecialSymbol.Type.BLOCK_FOLD);
            }
          case 108: break;
          case 54: 
            { return new SpecialSymbol(this, SpecialSymbol.Type.SHARE_NAMESPACE);
            }
          case 109: break;
          case 55: 
            { return new SpecialSymbol(this, SpecialSymbol.Type.REL_INCLUDE);
            }
          case 110: break;
          default:
            zzScanError(ZZ_NO_MATCH);
        }
      }
    }
  }


}
