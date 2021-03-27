import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.ArrayList;
import java.util.List;

public class MethodSymbol extends Symbol {

    private List<Symbol> parameters;
    private List<Symbol> variables;

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

    public void addParameter(Symbol parameter){
        parameters.add(parameter);
    }

    public void removeParameter(Symbol parameter){
        parameters.remove(parameter);
    }

    public void addVariables(Symbol variable){
        variables.add(variable);
    }

    public void removeVariables(Symbol variable){
        variables.remove(variable);
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

}
