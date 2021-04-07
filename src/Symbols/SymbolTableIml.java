package Symbols;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTableIml implements SymbolTable {

    private String className;
    private String superName;
    private final List<String> imports = new ArrayList<>();

    private final Map<Symbol, String> hashMapClassFields = new HashMap<>();

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
        return null;
    }

    @Override
    public List<String> getMethods() {
        return null;
    }


    @Override
    public Type getReturnType(String methodName) {
        //return methods.containsKey(methodName) ? methods.get(methodName).getType() : null;
        return null;
    }

    @Override
    public List<Symbol> getParameters(String methodName) {
        //return methods.containsKey(methodName) ? methods.get(methodName).getParameters() : null;
        return null;
    }

    @Override
    public List<Symbol> getLocalVariables(String methodName) {
        //return methods.containsKey(methodName) ? methods.get(methodName).getVariables() : null;
        return null;
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
                "className='" + className + '\'' + "\n" +
                ", superName='" + superName + '\'' + "\n" +
                ", imports=" + imports + "\n" +
                ", fields=" + hashMapClassFields.toString() + "\n" +
                ", methods=" + methodsHashmap.toString() + "\n" +
                '}';
    }

    public HashMap<String, MethodSymbol> getMethodsHashmap() {
        return methodsHashmap;
    }

    public Map<Symbol, String> getHashMapClassFields() {
        return hashMapClassFields;
    }
}
