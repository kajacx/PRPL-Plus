package com.prplplus.debugger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.util.Scanner;

public class DebuggerCompiler {
    private static final String referencePath = "CsBin/editor/DebuggerTemplate.prpl"; //use the prpl file, not the prpl+ one
    private static final String textBindPath = "CsBin/editor/TextBind.prpl"; //use the prpl file, not the prpl+ one
    private static final String badCompilationPath = "CsBin/editor/CompileError.prpl"; //use the prpl file, not the prpl+ one
    private static final String execPath = "CsBin/DebugerCompiler.exe";

    private static final String debuggerCodeStart = "#DebuggerGeneratedCodeStart";
    private static final String debuggerCodeEnd = "#DebuggerGeneratedCodeEnd";

    public static File getCompiledFile(File file) {
        return new File(file.getAbsolutePath().replaceAll("\\.prpl$", "_debug.prpl"));
    }

    /**
     * compiles file, returns error or null on success
     * @param file file to be compiled
     * @return Error or null on success
     */
    public static String compile(File file, DebuggerOptions options) {

        Scanner scan = null;
        PrintWriter writer = null;

        try {
            //prepare target file
            File targetFile = getCompiledFile(file);

            //get the compiled source
            String compiledSource;
            try {
                compiledSource = getCompiledText(file, options);
            } catch (Exception ex) {
                copyFile(new File(badCompilationPath), targetFile); //copy the original script, so that user gets the compile error in PRPL
                throw ex;
            }

            //start the execution process
            scan = new Scanner(new File(referencePath));
            writer = new PrintWriter(targetFile);

            //write the first half
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                writer.println(line);
                if (line.trim().equals(debuggerCodeStart)) {
                    break;
                }
            }

            //write the code
            writer.println();
            writer.println(compiledSource);
            writer.println();

            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                if (line.trim().equals(debuggerCodeEnd)) {
                    writer.println(line);
                    break;
                }
            }

            //write the second half
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                writer.println(line);
            }

            scan.close();
            writer.close();

            //copy the TextBind script
            File textBindOrig = new File(textBindPath);
            File textBindNew = new File(file.getParentFile().getAbsolutePath() + '/' + textBindOrig.getName());

            copyFile(textBindOrig, textBindNew);

        } catch (Exception ex) {
            ex.printStackTrace(System.out);

            if (scan != null) {
                scan.close();
            }
            if (writer != null) {
                writer.close();
            }

            return ex.getMessage();
        }

        return null; //success
    }

    private static void copyFile(File from, File to) throws IOException {

        FileInputStream fis = null;
        FileOutputStream fos = null;

        FileChannel src = null;
        FileChannel dest = null;

        try {
            src = (fis = new FileInputStream(from)).getChannel();
            dest = (fos = new FileOutputStream(to)).getChannel();
            dest.transferFrom(src, 0, src.size());
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (src != null) {
                src.close();
            }
            if (dest != null) {
                dest.close();
            }
        }
    }

    private static String getCompiledText(File file, DebuggerOptions options) throws Exception {
        Process proc = Runtime.getRuntime().exec(execPath);

        //write the options and file path into the process
        PrintWriter writer = new PrintWriter(proc.getOutputStream());
        writer.println("detailedMode:" + options.detailedMode);
        writer.println("file:" + file.getAbsolutePath());
        writer.close();

        //read the compile result
        Scanner scan = new Scanner(proc.getInputStream());
        String message = scan.nextLine();

        StringBuilder source = new StringBuilder();
        while (scan.hasNextLine()) {
            source.append(scan.nextLine()).append(System.lineSeparator());
        }
        String sourceCode = source.toString();
        scan.close();

        int result = proc.waitFor();
        if (result != 0 && !message.isEmpty()) {
            throw new Exception(message);
        }

        return sourceCode;
    }

    public static void main(String[] args) {
        String result = DebuggerCompiler.compile(new File("c:\\Users\\Karel\\Documents\\My Games\\particlefleet\\editor\\CannonSpawnTest\\scripts\\SpawnCannonShot.prpl"), new DebuggerOptions());

        System.out.println("Result: " + result);
    }

    public static class DebuggerOptions {
        public boolean detailedMode = false;

        public DebuggerOptions setDetailedMode(boolean detailedMode) {
            this.detailedMode = detailedMode;
            return this;
        }
    }
}
