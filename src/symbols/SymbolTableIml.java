package symbols;

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
    private final Symbol variableThis;
    private final List<String> imports;
    private final Map<String, MethodSymbol> methodsHashmap;
    private final Map<String, Symbol> classFields;


    public SymbolTableIml() {
        this.classFields = new HashMap<>();
        this.methodsHashmap = new HashMap<>();
        this.imports = new ArrayList<>();
        this.variableThis = new Symbol(new Type("this", false), "this");
    }

    public List<String> getImportedClasses() {
        List<String> importedClasses = new ArrayList<>();

        for (String importStr : imports) {
            String[] result = importStr.split("\\.");
            importedClasses.add(result[result.length - 1]);
        }

        return importedClasses;
    }

    public Symbol lookup(String variableName, String currentMethodName) {

        MethodSymbol methodSymbol = methodsHashmap.get(currentMethodName);

        if (methodSymbol.getLocalVariables().containsKey(variableName)) {
            return methodSymbol.getLocalVariables().get(variableName);
        }

        //Exists a method parameter variable with that name
        for (Symbol symbol : methodSymbol.getParameters()) {
            if (symbol.getName().equals(variableName))
                return symbol;
        }
        //Exists a class Field variable with that name
        if (classFields.containsKey(variableName))
            return classFields.get(variableName);

        return null;

    }

    public void setSuperName(String superName) {
        this.superName = superName;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Map<String, MethodSymbol> getMethodsHashmap() {
        return methodsHashmap;
    }

    public Symbol getVariableThis() {
        return variableThis;
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
        return new ArrayList<>(classFields.values());
    }

    @Override
    public List<String> getMethods() {
        return new ArrayList<>(methodsHashmap.keySet());
    }

    @Override
    public Type getReturnType(String methodName) {
        return methodsHashmap.get(methodName).getType();
    }

    @Override
    public List<Symbol> getParameters(String methodName) {
        return methodsHashmap.get(methodName).getParameters();
    }

    @Override
    public List<Symbol> getLocalVariables(String methodName) {
        return new ArrayList<>(methodsHashmap.get(methodName).getLocalVariables().values());
    }

    @Override
    public String toString() {
        return "SymbolTableIml{" + '\n' +
                '\t' + "className='" + className + "\n" +
                "\t" + "superName='" + superName + '\n' +
                "\t" + "variableThis=" + variableThis + '\n' +
                "\t" + "imports=" + imports + '\n' +
                "\t" + "methodsHashmap" + "\n" + methodsPrint() + '\n' +
                "\t" + "hashMapClassFields=" + "\n" + ClassFieldsPrint() + '\n' +
                '}';
    }

    public String methodsPrint(){

        String return_string= "";

        for (Map.Entry<String, MethodSymbol> entry : methodsHashmap.entrySet()) {

            return_string = return_string.concat("\t\t" + entry.getKey() + ":" + entry.getValue().toString() + '\n');
        }

        return return_string;
    }

    public String ClassFieldsPrint(){

        String return_string= "";

        for (Map.Entry<String, Symbol> entry : classFields.entrySet()) {

            return_string = return_string.concat("\t\t" + entry.getKey() + ":" + entry.getValue().toString() + '\n');
        }

        return return_string;
    }

    public Map<String, Symbol> getClassFields() {
        return classFields;
    }
}
