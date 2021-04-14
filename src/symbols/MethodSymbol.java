package symbols;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodSymbol extends Symbol {

    private List<Symbol> parameters;
    private final JmmNode node;
    private final Map<String, Symbol> localVariables;

    public MethodSymbol(Type returnType, String name, JmmNode node) {
        super(returnType, name);
        this.node = node;
        this.parameters = new ArrayList<>();
        this.localVariables = new HashMap<>();
    }

    public List<Symbol> getParameters() {
        return parameters;
    }

    public void setParameters(List<Symbol> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "\n\t\t\t" + "Symbols.MethodSymbol{" +
                "\n\t\t\t\t" + "return type=" + getType() +
                "\n\t\t\t\t" + "parameters=" + parameters +
                "\n\t\t\t\t" + "variables=" + localVariables.toString() +
                '}';
    }

    public JmmNode getNode() {
        return node;
    }

    public Map<String, Symbol> getLocalVariables() {
        return localVariables;
    }
}
