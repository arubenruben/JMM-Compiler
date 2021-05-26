package utils;

import pt.up.fe.comp.jmm.analysis.table.Symbol;

public class ConstantPropagatingHelper {
    final private Symbol symbol;
    final private String value;

    public ConstantPropagatingHelper(Symbol symbol, String value) {
        this.symbol = symbol;
        this.value = value;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public String getValue() {
        return value;
    }

}
