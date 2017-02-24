package com.prplplus.scanner;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;

import com.prplplus.Settings;
import com.prplplus.errors.ErrorHandler;
import com.prplplus.errors.ErrorHandler.ErrorType;
import com.prplplus.jflex.PrplPlusLexer;
import com.prplplus.jflex.Symbol;
import com.prplplus.jflex.symbols.SpecialSymbol;
import com.prplplus.jflex.symbols.UserFunctionSymbol;
import com.prplplus.jflex.symbols.VarSymbol;
import com.prplplus.jflex.symbols.VarSymbol.Operation;
import com.prplplus.jflex.symbols.VarSymbol.Scope;

public class PRPLCompiler {

    private PrintWriter writer;
    private NamespaceManager manager = new NamespaceManager();
    private Queue<CachedLexer> includeQueue = new ArrayDeque<>();

    private HashSet<String> importedScripts = new HashSet<>(); //import done or functions pending; full path
    private HashSet<String> openedScripts = new HashSet<>(); //import in progress; full path

    private HashMap<String, Variable> varaibleUsage = new HashMap<>();

    public PRPLCompiler(PrintWriter writer, String primaryFile) {
        this.writer = writer;
        openedScripts.add(new File(primaryFile).getAbsolutePath());
    }

    private void include(String fname, Symbol symbol) {

        try {
            File importFile = new File(Settings.WORK_IN + "/editor/" + fname);
            if (!importFile.exists()) {
                ErrorHandler.reportError(ErrorType.INCLUDE_FILE_NOT_FOUND, symbol, importFile.getAbsolutePath());
                return;
            }

            String absPath = importFile.getAbsolutePath();
            if (importedScripts.contains(absPath)) {
                //already imported, do nothing
                return;
            }
            if (openedScripts.contains(absPath)) {
                //import loop
                ErrorHandler.reportError(ErrorType.INCLUDE_CYCLE_DETECTED, symbol);
                return;
            }

            FileReader reader = new FileReader(importFile);
            PrplPlusLexer lexer = new PrplPlusLexer(reader, fname);
            CachedLexer cached = new CachedLexer(lexer);

            openedScripts.add(absPath);

            writer.println();
            writer.println("# -- Start import from '" + fname + "' -- #");

            compile(cached, false);

            writer.println();
            writer.println("# -- End import from '" + fname + "' -- #");

            openedScripts.remove(absPath);
            importedScripts.add(absPath);

        } catch (IOException ex) {
            ErrorHandler.reportError(ErrorType.INCLUDE_FAILED, symbol);
            ex.printStackTrace(System.out);
        }
    }

    public void compile(CachedLexer lexer, boolean isPrimary) throws IOException {
        ArrayList<VarSymbol> varRefParStack = new ArrayList<>();
        varRefParStack.add(null);
        int parDepth = 0;
        boolean ignoreNextLPar = false;

        String scriptNamespace = manager.getPrefixFor("script");
        String functNamespace = manager.getPrefixFor("main");
        boolean isInFunction = false;

        lexer.setScriptNamespace(scriptNamespace);

        while (true) {
            if (!isPrimary && lexer.peekNextUseful().isFunctionDefinition()) {
                includeQueue.add(lexer);
                return;
            }

            Symbol curSymbol = lexer.getNextSymbol();

            //parenthesis
            if (curSymbol.isLeftPar()) {
                if (ignoreNextLPar) {
                    ignoreNextLPar = false;
                } else {
                    varRefParStack.add(null);
                    parDepth++;
                }
                writer.print(curSymbol.text);
                continue;
            }

            if (curSymbol.isRightPar()) {
                if (parDepth == 0) {
                    ErrorHandler.reportError(ErrorType.UNEXPECTED_RIGHT_PAR, curSymbol);
                } else if (lexer.peekNextUseful().isLeftPar()) {
                    ignoreNextLPar = true;
                } else {
                    varRefParStack.remove(parDepth);
                    parDepth--;
                    VarSymbol varSym = varRefParStack.get(parDepth);
                    if (varSym != null) {
                        printVarRefWorkaround(varSym, scriptNamespace, functNamespace);
                        varRefParStack.set(parDepth, null);
                    }
                }
                writer.print(curSymbol.text);
                continue;
            }

            //end of comment
            if (curSymbol.isEndOfComment()) {
                writer.print(curSymbol.text);
                if (lexer.peekNextUseful().line == curSymbol.line)
                    writer.println();
                continue;
            }

            //function definition
            if (curSymbol instanceof UserFunctionSymbol) {
                UserFunctionSymbol funcSym = (UserFunctionSymbol) curSymbol;
                if (funcSym.isDefinition) {
                    if (parDepth != 0) {
                        ErrorHandler.reportError(ErrorType.UNCLOSED_LEFT_PAR, curSymbol);
                        parDepth = 0;
                        varRefParStack.clear();
                        varRefParStack.add(null);
                    }

                    isInFunction = true;
                    functNamespace = manager.getPrefixFor(funcSym.functionName);
                }
                writer.print(curSymbol.text);
                continue;
            }

            //a special symbol
            if (curSymbol instanceof SpecialSymbol) {
                SpecialSymbol specSym = (SpecialSymbol) curSymbol;
                switch (specSym.type) {
                case LOCAL_PREFIX:
                    writer.print("\"" + functNamespace + "\"");
                    break;
                case SEMI_GLOBAL_PREFIX:
                    writer.print("\"" + scriptNamespace + "\"");
                    break;
                case PRPL_PLUS_PREFIX:
                    writer.print("\"" + NamespaceManager.PRPL_PREFIX + "\"");
                    break;
                case BLOCK_FOLD:
                case LIBRARY:
                    writer.print("#" + curSymbol.text.substring(1));
                    if (lexer.peekNextUseful().line == curSymbol.line)
                        writer.println();
                    break;
                case INCLUDE:
                    if (isInFunction) {
                        ErrorHandler.reportError(ErrorType.INCLUDE_INSIDE_FUNCTION, curSymbol);
                    }
                    Symbol next = lexer.peekNextUseful();
                    if (next.isString()) {
                        if (parDepth != 0) {
                            ErrorHandler.reportError(ErrorType.UNCLOSED_LEFT_PAR, curSymbol);
                            parDepth = 0;
                            varRefParStack.clear();
                            varRefParStack.add(null);
                        }
                        String fname = next.text.substring(1, next.text.length() - 1);
                        next.text = "";
                        include(fname, curSymbol);
                    } else {
                        ErrorHandler.reportError(ErrorType.INCLUDE_MISSING_FILENAME, curSymbol);
                    }
                    break;
                default:
                    ErrorHandler.reportError(ErrorType.COMPILER_IN_TROUBLE, curSymbol);
                    writer.print(curSymbol.text);
                    break;
                }
                continue;
            }

            //a variable
            if (curSymbol instanceof VarSymbol) {
                VarSymbol varSym = (VarSymbol) curSymbol;

                //add usage
                addVaraibleUsage(varaibleUsage, varSym, scriptNamespace, functNamespace);

                if (varSym.scope == Scope.ARGUMENT) {
                    writer.print(varSym.text);
                    continue; //no need to do anything for argument
                }

                if (varSym.scope == Scope.GLOBAL) {
                    if (varSym.isRef) {
                        writer.print(varSym.op.refInstr);
                    } else {
                        writer.print(varSym.op.opInstr);
                        writer.print(varSym.varName);
                    }
                    continue; //dont change the name on global variables
                }

                if (varSym.isRef) {
                    //we will prefix concat to solve this
                    if (lexer.peekNextUseful().isLeftPar()) {
                        //problem: need to move code due to warping
                        varRefParStack.set(parDepth, varSym);
                    } else {
                        //can solve right away
                        printVarRefWorkaround(varSym, scriptNamespace, functNamespace);
                    }
                    writer.print(varSym.op.refInstr);

                } else {
                    //we can always prefix in-place
                    writer.print(varSym.op.opInstr);
                    if (varSym.scope == Scope.LOCAL) {
                        writer.print(functNamespace);
                    } else {
                        writer.print(scriptNamespace);
                    }
                    writer.print(varSym.varName);
                }

                continue;
            } //DONE if for variable

            //end of file
            if (curSymbol.isEOF()) {
                if (parDepth != 0) {
                    ErrorHandler.reportError(ErrorType.UNCLOSED_LEFT_PAR, curSymbol);
                    parDepth = 0;
                    varRefParStack.clear();
                    varRefParStack.add(null);
                }

                if (isPrimary && !includeQueue.isEmpty()) {
                    CachedLexer newLexer = includeQueue.poll();
                    writer.println();
                    writer.println("# -- Functions from '" + newLexer.getFilename() + "' -- #");
                    lexer.close();
                    lexer = newLexer;
                    scriptNamespace = newLexer.getScriptNamespace();
                    //don't bother with function namespace, first symbol is function definition anyway
                    continue;
                } else {
                    //work done
                    lexer.close();
                    checkVariableUsage();
                    break;
                }
            }

            writer.print(curSymbol.text); //just print text for everything else
        }
    }

    //writes the stuff that comes before the ref isntruction
    //doesn't write the ref instruction itself
    private void printVarRefWorkaround(VarSymbol var, String scriptNamespace, String functionNamespace) {
        if (!var.isRef) {
            ErrorHandler.reportError(ErrorType.COMPILER_IN_TROUBLE, var, "Var must be a reference");
            return;
        }

        String prefix = null;
        if (var.scope == Scope.LOCAL) {
            prefix = functionNamespace;
        } else if (var.scope == Scope.SEMI_GLOBAL) {
            prefix = scriptNamespace;
        } else {
            ErrorHandler.reportError(ErrorType.COMPILER_IN_TROUBLE, var, "Wrong variable visibility");
        }

        if (var.op == Operation.WRITE) {
            writer.print("->" + NamespaceManager.PRPL_PREFIX + "value ");
            writer.print("->" + NamespaceManager.PRPL_PREFIX + "varname ");
            writer.print("\"" + prefix + "\" ");
            writer.print("<-" + NamespaceManager.PRPL_PREFIX + "varname ");
            writer.print("Concat ");
            writer.print("<-" + NamespaceManager.PRPL_PREFIX + "value ");
        } else {
            writer.print("->" + NamespaceManager.PRPL_PREFIX + "varname ");
            writer.print("\"" + prefix + "\" ");
            writer.print("<-" + NamespaceManager.PRPL_PREFIX + "varname ");
            writer.print("Concat ");
        }
    }

    private void addVaraibleUsage(HashMap<String, Variable> usageMap, VarSymbol var, String scriptNamespace, String functNamespace) {
        if (var.isRef) {
            return;
        }

        String name = var.varName;
        if (var.scope == Scope.LOCAL) {
            name = functNamespace + name;
        } else if (var.scope == Scope.SEMI_GLOBAL) {
            name = scriptNamespace + name;
        }

        Variable v = usageMap.get(name);
        if (v == null) {
            v = new Variable(var);
            usageMap.put(name, v);
        }

        if (var.op == Operation.READ) {
            v.readFrom = true;
        } else if (var.op == Operation.WRITE) {
            v.writtenTo = true;
        }
    }

    private void checkVariableUsage() {
        for (Variable v : varaibleUsage.values()) {
            if (!v.readFrom && (v.symbol.scope == Scope.LOCAL || v.symbol.scope == Scope.SEMI_GLOBAL)) {
                ErrorHandler.reportError(ErrorType.VARIABLE_NEVER_READ_FROM, v.symbol);
            }
            if (!v.writtenTo) {
                ErrorHandler.reportError(ErrorType.VARIABLE_NEVER_WRITTEN_TO, v.symbol);
            }
        }
    }
}
