package Symbols;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SymbolTableIml implements SymbolTable {

    private String className;
    private String superName;
    private final List<String> imports = new ArrayList<>();

    //TODO:Maybe a direct hashmap
    private final List<Symbol> fields = new ArrayList<>();

    private final HashMap<String, MethodSymbol> methodsHashmap = new HashMap<>();

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
        return null;
    }


    @Override
    public Type getReturnType(String methodName) {
        return methodsHashmap.containsKey(methodName) ? methodsHashmap.get(methodName).getType() : null;
    }

    @Override
    public List<Symbol> getParameters(String methodName) {
        return methodsHashmap.containsKey(methodName) ? methodsHashmap.get(methodName).getParameters() : null;

    }

    @Override
    public List<Symbol> getLocalVariables(String methodName) {
        return methodsHashmap.containsKey(methodName) ? methodsHashmap.get(methodName).getVariables() : null;
    }

    public void setSuperName(String superName) {
        this.superName = superName;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return "SymbolTableIml{" +
                "className='" + className + '\'' +"\n"+
                ", superName='" + superName + '\'' +"\n"+
                ", imports=" + imports +"\n"+
                ", fields=" + fields +"\n"+
                ", methods=" + methodsHashmap.toString() +"\n"+
                '}';
    }

    public HashMap<String, MethodSymbol> getMethodsHashmap() {
        return methodsHashmap;
    }
}
