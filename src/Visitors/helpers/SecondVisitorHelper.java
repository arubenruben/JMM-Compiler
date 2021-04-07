package Visitors.helpers;

import Symbols.SymbolTableIml;

public class SecondVisitorHelper {

    private final String currentMethodName;
    private final SymbolTableIml symbolTableIml;

    public SecondVisitorHelper(String currentMethodName, SymbolTableIml symbolTableIml) {
        this.currentMethodName = currentMethodName;
        this.symbolTableIml = symbolTableIml;
    }

    public String getCurrentMethodName() {
        return currentMethodName;
    }

    public SymbolTableIml getSymbolTableIml() {
        return symbolTableIml;
    }
}

