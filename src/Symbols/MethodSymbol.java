package Symbols;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.ArrayList;
import java.util.List;

public class MethodSymbol extends Symbol {

    private final List<Symbol> parameters;

    //TODO:Maybe an hashmap to store the values directly
    private final List<Symbol> variables;

    public MethodSymbol(Type type, String name) {
        super(type, name);
        this.parameters = new ArrayList<>();
        this.variables = new ArrayList<>();
    }

    public MethodSymbol(Type type, String name, List<Symbol> parameters, List<Symbol> variables) {
        super(type, name);
        this.parameters = parameters != null ? parameters : new ArrayList<>();
        this.variables = variables != null ? variables : new ArrayList<>();
    }


    public List<Symbol> getParameters() {
        return parameters;
    }

    public List<Symbol> getVariables() {
        return variables;
    }

    @Override
    public String toString() {
        return "Symbols.MethodSymbol{" +
                "parameters=" + parameters +
                ", variables=" + variables +
                '}';
    }
}
