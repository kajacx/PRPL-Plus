package com.prplplus.scanner;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.prplplus.errors.ErrorHandler;
import com.prplplus.errors.ErrorHandler.ErrorType;
import com.prplplus.jflex.Symbol;
import com.prplplus.jflex.symbols.VarSymbol;
import com.prplplus.jflex.symbols.VarSymbol.Operation;
import com.prplplus.jflex.symbols.VarSymbol.Scope;

public class Compiler {

    private PrintWriter writer;
    private NamespaceManager manager = new NamespaceManager();

    public Compiler(PrintWriter writer) {
        this.writer = writer;
    }

    public void compile(CachedLexer lexer) throws IOException {
        ArrayList<VarSymbol> varRefParStack = new ArrayList<>();
        varRefParStack.add(null);
        int parDepth = 0;
        boolean ignoreNextLPar = false;

        String scriptNamespace = manager.getPrefixFor("main");
        String functNamespace = null;

        Symbol curSymbol;

        while (!(curSymbol = lexer.getNextSymbol()).isEOF()) {

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
                if (lexer.peekNextUseful().isLeftPar()) {
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

            //a variable
            if (curSymbol instanceof VarSymbol) {
                VarSymbol varSym = (VarSymbol) curSymbol;

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
                        if (functNamespace == null) {
                            ErrorHandler.reportError(ErrorType.NOT_INSIDE_FUNCTION, varSym);
                        }
                        writer.print(functNamespace);
                    } else {
                        writer.print(scriptNamespace);
                    }
                    writer.print(varSym.varName);
                }

                continue;
            } //DONE if for variable

            writer.print(curSymbol.text); //just print text for now
        }
    }

    //writes the stuff that comes before the ref isntruction
    //doesn't write the ref instruction itself
    private void printVarRefWorkaround(VarSymbol var, String scriptNamespace, String functionNamespace) {
        if (!var.isRef)
            throw new IllegalArgumentException("Var must be reference");

        String prefix;
        if (var.scope == Scope.LOCAL) {
            if (functionNamespace == null) {
                ErrorHandler.reportError(ErrorType.NOT_INSIDE_FUNCTION, var);
                writer.print(var.op.refInstr);
                return;
            }
            prefix = functionNamespace;
        } else if (var.scope == Scope.SEMI_GLOBAL) {
            prefix = scriptNamespace;
        } else {
            throw new IllegalArgumentException("Var has wrong visibility");
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
}
