package com.uepb.interfaces;

import java.io.File;

public interface CompilerEngine {
    void execute(File input, File output, boolean verbose) throws Exception;
}
