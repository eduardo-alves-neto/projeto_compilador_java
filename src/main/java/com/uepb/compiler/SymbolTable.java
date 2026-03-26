package com.uepb.compiler;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

    private final Map<String, Integer> nameToAddress = new HashMap<>();
    private int nextAddress = 0;

    public int declare(String name) {
        if (nameToAddress.containsKey(name)) {
            throw new IllegalStateException("Variável já declarada: " + name);
        }
        int address = nextAddress++;
        nameToAddress.put(name, address);
        return address;
    }

    public int getAddress(String name) {
        Integer address = nameToAddress.get(name);
        if (address == null) {
            throw new IllegalStateException("Variável não declarada: " + name);
        }
        return address;
    }
}
