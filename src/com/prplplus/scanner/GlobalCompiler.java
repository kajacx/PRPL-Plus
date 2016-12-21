package com.prplplus.scanner;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.prplplus.errors.ErrorHandler;
import com.prplplus.errors.ErrorHandler.ErrorType;
import com.prplplus.jflex.PrplPlusLexer;

public class GlobalCompiler {
    public static String[] extensions = { ".prpl+", ".prpl.lib", "prpl+lib" };

    public void compileAllIn(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                compileAllIn(f);
            }
        }

        int extension = -1;
        for (int i = 0; i < extensions.length; i++) {
            if (file.getName().toLowerCase().endsWith(extensions[i])) {
                extension = i;
            }
        }

        if (extension == -1) {
            //not a prpl+ code
            return;
        }

        try {
            FileReader reader = new FileReader(file);

            PrplPlusLexer lexer = new PrplPlusLexer(reader, file.getPath());
            CachedLexer cached = new CachedLexer(lexer);

            if (cached.peekNextUseful().isLibrary()) {
                //dont compile library code
                cached.close();
                return;
            }

            String newFile = file.getAbsolutePath();
            newFile = newFile.substring(0, newFile.length() - extensions[extension].length());
            newFile += ".prpl";

            PrintWriter writer = new PrintWriter(newFile);
            PRPLCompiler compiler = new PRPLCompiler(writer, file.getAbsolutePath());

            compiler.compile(cached, true);

            reader.close();
            writer.close();
        } catch (IOException ex) {
            ErrorHandler.reportError(ErrorType.COMPILATION_FAILED, ex.toString());
            ex.printStackTrace(System.out);
        }
    }
}
