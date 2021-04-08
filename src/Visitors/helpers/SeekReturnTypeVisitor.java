package Visitors.helpers;

import Symbols.MethodSymbol;
import Visitors.helpers.data_helpers.SecondVisitorHelper;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

public class SeekReturnTypeVisitor extends PreorderJmmVisitor<SecondVisitorHelper, Type> {
    private Type type;

    public SeekReturnTypeVisitor() {
        addVisit("ArrayAccess", this::dealWithArrayAccess);
        addVisit("MethodCall", this::dealWithMethodCall);
        addVisit("Integer", this::dealWithInteger);
        addVisit("Boolean", this::dealWithBoolean);
        addVisit("This", this::dealWithThis);
        addVisit("Identifier", this::dealWithIdentifier);
        setDefaultVisit(this::defaultVisit);
    }

    protected Type dealWithArrayAccess(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        if (type != null)
            return type;

        type = new Type("int", false);

        return type;
    }

    protected Type dealWithMethodCall(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        //TODO:Falta
        if (type != null)
            return type;
        return type;
    }

    protected Type dealWithInteger(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        if (type != null)
            return type;

        type = new Type("int", false);

        return type;
    }

    protected Type dealWithBoolean(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        if (type != null)
            return type;

        type = new Type("boolean", false);

        return type;
    }

    protected Type dealWithThis(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        if (type != null)
            return type;

        type = new Type("this", false);

        return type;
    }

    protected Type dealWithIdentifier(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        if (type != null)
            return type;

        MethodSymbol methodSymbol = secondVisitorHelper.getSymbolTableIml().getMethodsHashmap().get(secondVisitorHelper.getCurrentMethodName());

        for (Symbol symbol : methodSymbol.getVariables().keySet()) {
            //Exists a local variable with that name
            if (symbol.getName().equals(node.get("value"))) {
                type = symbol.getType();
                return type;
            }
        }
        for (Symbol symbol : methodSymbol.getParameters()) {
            //Exists a method parameter variable with that name
            if (symbol.getName().equals(node.get("value"))) {
                type = symbol.getType();
                return type;
            }
        }
        for (Symbol symbol : secondVisitorHelper.getSymbolTableIml().getHashMapClassFields().keySet()) {
            //Exists a class Field variable with that name
            if (symbol.getName().equals(node.get("value"))) {
                type = symbol.getType();
                return type;
            }
        }
        return type;
    }

    protected Type defaultVisit(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        return type;
    }

    public Type getType() {
        return type;
    }
}
