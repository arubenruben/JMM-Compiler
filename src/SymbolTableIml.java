import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SymbolTableIml implements SymbolTable {

    String superName;
    String className;
    List<String> imports = new ArrayList<>();
    List<Symbol> fields = new ArrayList<>();
    HashMap<String, MethodSymbol> methods = new HashMap<>();

    public SymbolTableIml(){ }

    public SymbolTableIml(String className){
        this.className = className;
    }

    public SymbolTableIml(String className, String superName) {
        this.superName = superName;
        this.className = className;
    }

    @Override
    public List<String> getImports() {
        return imports;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getSuper() {
        return superName;
    }

    @Override
    public List<Symbol> getFields() {
        return fields;
    }

    @Override
    public List<String> getMethods() {
        List<String> methodsName = new ArrayList<>();
        methods.forEach((k, v) ->{
            methodsName.add(v.getName());
        } );
        return methodsName;
    }

    @Override
    public Type getReturnType(String methodName) {
        return methods.containsKey(methodName) ? methods.get(methodName).getType() : null;
    }

    @Override
    public List<Symbol> getParameters(String methodName) {
        return methods.containsKey(methodName) ? methods.get(methodName).getParameters() : null;
    }

    @Override
    public List<Symbol> getLocalVariables(String methodName) {
        return methods.containsKey(methodName) ? methods.get(methodName).getVariables() : null;
    }

    public String getSuperName() {
        return superName;
    }

    public void setSuperName(String superName) {
        this.superName = superName;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void addFields(Symbol field){
        fields.add(field);
    }

    public void addImport(String importName){
        imports.add(importName);
    }

    public void addMethod(String methodName, MethodSymbol methodSymbol){
        methods.put(methodName, methodSymbol);
    }

    public MethodSymbol getMethod(String methodName){
        return methods.getOrDefault(methodName, null);
    }
}
