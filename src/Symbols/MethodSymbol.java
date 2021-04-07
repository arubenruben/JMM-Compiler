package Symbols;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.ArrayList;
import java.util.List;

public class MethodSymbol extends Symbol {

    private List<Symbol> parameters;

    //TODO:Maybe an hashmap to store the values directly
    private List<Symbol> variables;

    public MethodSymbol(Type returnType, String name) {
        super(returnType, name);
        this.parameters = new ArrayList<>();
        this.variables = new ArrayList<>();
    }

    public List<Symbol> getParameters() {
        return parameters;
    }

    public void setParameters(List<Symbol> parameters) {
        this.parameters = parameters;
    }

    public List<Symbol> getVariables() {
        return variables;
    }

    public void setVariables(List<Symbol> variables) {
        this.variables = variables;
    }

    @Override
    public String toString() {
        return "Symbols.MethodSymbol{" +
                "return type" + getType() +
                "parameters=" + parameters +
                ", variables=" + variables +
                '}';
    }
}
