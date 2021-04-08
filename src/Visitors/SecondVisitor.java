package Visitors;

import Symbols.MethodSymbol;
import Visitors.helpers.SeekTypesVisitor;
import Visitors.helpers.data_helpers.SecondVisitorHelper;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SecondVisitor extends PreorderJmmVisitor<SecondVisitorHelper, Boolean> {

    public SecondVisitor() {
        addVisit("Add", this::dealWithMathOperation);
        addVisit("Sub", this::dealWithMathOperation);
        addVisit("Mult", this::dealWithMathOperation);
        addVisit("Div", this::dealWithMathOperation);
        setDefaultVisit(this::defaultVisit);
    }

    protected Boolean dealWithMathOperation(JmmNode node, SecondVisitorHelper secondVisitorHelper) {

        Type[] types = new Type[2];

        types[0] = seekReturnType(node.getChildren().get(0), secondVisitorHelper);
        types[1] = seekReturnType(node.getChildren().get(1), secondVisitorHelper);

        if (!types[0].equals(types[1])) {
            System.err.println("Not equals");
        }

        return true;
    }

    private Type seekReturnType(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        Type typeReturned = null;

        switch (node.getKind()) {
            case "ArrayAccess":
            case "Integer":
                typeReturned = new Type("int", false);
                break;
            case "Boolean":
                typeReturned = new Type("boolean", false);
                break;
            case "This":
                typeReturned = new Type("this", false);
                break;
            //Check Symbol Table
            case "Identifier":
                MethodSymbol methodSymbol = secondVisitorHelper.getSymbolTableIml().getMethodsHashmap().get(secondVisitorHelper.getCurrentMethodName());

                for (Symbol symbol : methodSymbol.getVariables().keySet()) {
                    //Exists a local variable with that name
                    if (symbol.getName().equals(node.get("value"))) {
                        return symbol.getType();
                    }
                }
                for (Symbol symbol : methodSymbol.getParameters()) {
                    //Exists a method parameter variable with that name
                    if (symbol.getName().equals(node.get("value"))) {
                        return symbol.getType();
                    }
                }
                for (Symbol symbol : secondVisitorHelper.getSymbolTableIml().getHashMapClassFields().keySet()) {
                    //Exists a class Field variable with that name
                    if (symbol.getName().equals(node.get("value")))
                        return symbol.getType();
                }
                break;
            case "MethodCall":
                break;
        }
        return typeReturned;
    }

    protected Boolean defaultVisit(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        return true;
    }

}
