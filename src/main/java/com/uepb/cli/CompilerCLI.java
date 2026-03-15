package com.uepb.cli;

import java.io.File;
import java.util.concurrent.Callable;

import com.uepb.interfaces.CompilerEngine;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "compilador-antlr", mixinStandardHelpOptions = true, version = "1.0",
    description = "Compila arquivos da linguagem Expr usando ANTLR4"
)
public class CompilerCLI implements Callable<Integer>{

    @Option(names = {"-i", "--input"}, description = "O arquivo fonte para compilação")
    private File input;

    @Option(names = {"-o", "--output"}, description = "Arquivo de saída (default: out.pcode)")
    private File output = new File("out.pcode");

    @Option(names = {"-v", "--verbose"}, description = "Mostra árvore sintática")
    private boolean verbose = false;

    private final CompilerEngine engine;

    public CompilerCLI(CompilerEngine engine){
        this.engine = engine;
    }

    @Override
    public Integer call() throws Exception {
        engine.execute(input, output, verbose);
        return 0;
    }
}
