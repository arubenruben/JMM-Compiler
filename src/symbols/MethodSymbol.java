package symbols;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodSymbol extends Symbol {

    private List<Symbol> parameters;

    //TODO:Maybe an hashmap to store the values directly
    private Map<Symbol, String> variables;

    public MethodSymbol(Type returnType, String name) {
        super(returnType, name);
        this.parameters = new ArrayList<>();
        this.variables = new HashMap<>();
    }

    public List<Symbol> getParameters() {
        return parameters;
    }

    public void setParameters(List<Symbol> parameters) {
        this.parameters = parameters;
    }

    public Map<Symbol, String> getVariables() {
        return variables;
    }

    public void setVariables(Map<Symbol, String> variables) {
        this.variables = variables;
    }

    @Override
    public String toString() {
        return "Symbols.MethodSymbol{" +
                "return type" + getType() +
                "parameters=" + parameters +
                ", variables=" + variables.toString() +
                '}';
    }
}
