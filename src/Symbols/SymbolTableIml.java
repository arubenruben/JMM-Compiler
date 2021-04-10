package Symbols;

import pt.up.fe.comp.jmm.JmmNode;
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
    private final List<String> imports;
    private final Map<String, JmmNode> nodeMap;

    private final Map<String, MethodSymbol> methodsHashmap;

    private final Map<Symbol, String> hashMapClassFields;


    public SymbolTableIml() {
        this.hashMapClassFields = new HashMap<>();
        this.methodsHashmap = new HashMap<>();
        this.imports = new ArrayList<>();
        this.nodeMap = new HashMap<>();
    }


    @Override
    public List<String> getImports() {
        return imports;
    }

    public List<String> getImportedClasses() {
        List<String> importedClasses = new ArrayList<>();

        for (String importStr : imports) {
            String[] result = importStr.split("\\.");
            importedClasses.add(result[result.length - 1]);
        }

        return importedClasses;
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
        return methodsHashmap.containsKey(methodName) ? methodsHashmap.get(methodName).getType() : null;
    }

    @Override
    public List<Symbol> getParameters(String methodName) {
        return methodsHashmap.containsKey(methodName) ? methodsHashmap.get(methodName).getParameters() : null;

    }

    @Override
    public List<Symbol> getLocalVariables(String methodName) {
        //return methodsHashmap.containsKey(methodName) ? methodsHashmap.get(methodName).getVariables() : null;
        return null;
    }

    public Symbol lookup(String variableName, String currentMethodName) {
        MethodSymbol methodSymbol = methodsHashmap.get(currentMethodName);

        for (Symbol symbol : methodSymbol.getVariables().keySet()) {
            //Exists a local variable with that name
            if (symbol.getName().equals(variableName))
                return symbol;
        }
        for (Symbol symbol : methodSymbol.getParameters()) {
            //Exists a method parameter variable with that name
            if (symbol.getName().equals(variableName))
                return symbol;
        }
        for (Symbol symbol : hashMapClassFields.keySet()) {
            //Exists a class Field variable with that name
            if (symbol.getName().equals(variableName))
                return symbol;
        }
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
        return "ClassName='" + className + '\'' + "\n" +
                "superName='" + superName + '\'' + "\n" +
                "imports=" + imports + "\n" +
                "fields=" + hashMapClassFields.toString() + "\n" +
                "methods=" + methodsHashmap.toString() + "\n" +
                '}';
    }

    public Map<String, MethodSymbol> getMethodsHashmap() {
        return methodsHashmap;
    }

    public Map<Symbol, String> getHashMapClassFields() {
        return hashMapClassFields;
    }

    public Map<String, JmmNode> getNodeMap() {
        return nodeMap;
    }
}
