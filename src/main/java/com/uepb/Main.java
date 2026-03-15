package com.uepb;

import com.uepb.cli.CompilerCLI;
import com.uepb.compiler.Antlr4BasicExample;
import com.uepb.interfaces.CompilerEngine;

import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        CompilerEngine engine = new Antlr4BasicExample();
        CompilerCLI cli = new CompilerCLI(engine);

        int exitCode = new CommandLine(cli).execute(args);
        System.exit(exitCode);
    }
}