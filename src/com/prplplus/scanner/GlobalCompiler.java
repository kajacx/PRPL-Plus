package com.prplplus.scanner;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import com.prplplus.errors.ErrorHandler;
import com.prplplus.errors.ErrorHandler.ErrorType;
import com.prplplus.jflex.PrplPlusLexer;

public class GlobalCompiler {
    public static String[] extensions = { ".prpl+", ".prpl.lib", "prpl+lib" };

    public int compileAll(File file) {
        scanned = 0;
        int compiled = compileAllIn(file);
        if (scanned >= limit) {
            ErrorHandler.reportError(ErrorType.TOO_MANY_FILES_SCANNED,
                    "Maximum file limit (" + limit + ") reached. Make sure you have the correct PF directory slelected.");
        }
        return compiled;
    }

    private int scanned;
    private int limit = 10000;

    private HashMap<String, Long> lastCompiled = new HashMap<>(); //full name to milis

    private int compileAllIn(File file) {
        if (scanned >= limit) {
            return 0;
        }
        scanned++;

        int compiled = 0;
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                compiled += compileAllIn(f);
            }
            return compiled;
        }

        int extension = -1;
        for (int i = 0; i < extensions.length; i++) {
            if (file.getName().toLowerCase().endsWith(extensions[i])) {
                extension = i;
            }
        }

        if (extension == -1) {
            //not a prpl+ code
            return 0;
        }

        //check if already compiled
        String fullName = file.getAbsolutePath();
        if (lastCompiled.containsKey(fullName) && lastCompiled.get(fullName) >= file.lastModified()) {
            return 0;
        }

        CachedLexer cached = null;
        PrintWriter writer = null;
        try {
            FileReader reader = new FileReader(file);

            PrplPlusLexer lexer = new PrplPlusLexer(reader, file.getPath());
            cached = new CachedLexer(lexer);

            if (cached.peekNextUseful().isLibrary()) {
                //dont compile library code
                cached.close();
                return 0;
            }

            String newFile = file.getAbsolutePath();
            newFile = newFile.substring(0, newFile.length() - extensions[extension].length());
            newFile += ".prpl";

            writer = new PrintWriter(newFile);
            PRPLCompiler compiler = new PRPLCompiler(writer, file.getAbsolutePath());

            compiler.compile(cached, true);

            reader.close();
            writer.close();

            lastCompiled.put(fullName, file.lastModified());
            return 1;
        } catch (IOException ex) {
            ErrorHandler.reportError(ErrorType.COMPILATION_FAILED, ex.toString());
            ex.printStackTrace(System.out);

            return 0;
        } finally {
            if (cached != null) {
                cached.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
    }
}
