/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.brysgo.VertxToSpring;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import org.apache.commons.cli.*;

public class App {

    public static void main(String[] args) throws ParseException {
        Options options = new Options();
        String directoryValue = null;

        // add the --write or -w option
        options.addOption(Option.builder("w")
                .longOpt("write")
                .desc("write something to a file")
                .build());

        // add the --directory or -d option
        options.addOption(Option.builder("d")
                .longOpt("directory")
                .required()
                .hasArg()
                .desc("directory to write to")
                .build());

        CommandLineParser p = new DefaultParser();
        CommandLine cmd = p.parse(options, args);

        // get the value of the --directory or -d option
        directoryValue = cmd.getOptionValue('d');
        System.out.println("The --directory or -d option is present with value: " + directoryValue);

        // check if the --write or -w option is present
        Boolean writeValue = cmd.hasOption('w');
        if (writeValue) {
            // get the value of the --write or -w option
            System.out.println("The --write or -w option is present");
        } else {
            System.out.println("The --write or -w option is not present.");
        }

        walkDir(directoryValue, writeValue);
    }

    public static void walkDir(String directoryValue, Boolean writeValue) {
        File directory = new File(directoryValue);
        File[] files = directory.listFiles();
        Arrays.sort(files);

        for (File file : files) {
            if (file.isDirectory()) {
                // Recursively print files in subdirectories
                walkDir(file.getAbsolutePath(), writeValue);
            } else if (file.getName().endsWith(".java")) {
                // Parse and print contents of Java files
                try {
                    String code = Files.readString(file.toPath(), StandardCharsets.UTF_8);
                    ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
                    parser.setSource(code.toCharArray());
                    parser.setKind(ASTParser.K_COMPILATION_UNIT);
                    parser.setResolveBindings(true);
                    parser.setBindingsRecovery(true);
                    parser.setStatementsRecovery(true);
                    parser.setEnvironment(null, null, null, true);
                    CompilationUnit cu = (CompilationUnit) parser.createAST(null);

                    ConvertVerticlesToSpringComponentsCodemod.install(cu);
                    ConvertEventLoopsToSpringBootCodemod.install(cu);
                    if (writeValue) {
                        // overwrite the file
                        File fileToWrite = new File(file.getAbsolutePath());
                        fileToWrite.setWritable(true);
                        Files.writeString(fileToWrite.toPath(), cu.toString(), StandardCharsets.UTF_8);
                        System.out.println("File written: " + file.getName());
                    } else {
                        System.out.println("File: " + file.getName());
                        System.out.println(cu.toString());
                        System.out.println();
                    }
            } catch (IOException e) {
                System.err.println("Error reading file: " + file.getName());
            }
        }
    }
}
}
