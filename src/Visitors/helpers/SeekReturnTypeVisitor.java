package Visitors.helpers;

import Symbols.MethodSymbol;
import Visitors.helpers.data_helpers.SecondVisitorHelper;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

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

        if (node.getChildren().get(0).getKind().equals("Identifier")) {
            Symbol symbol = variableLookup(node.getChildren().get(0).get("value"), secondVisitorHelper);
            if (symbol == null) {
                secondVisitorHelper.getReportList().add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.getChildren().get(0).get("line")), "Attempt to call an non declared array"));
                return type;
            }
        }
        /*
        //Todo:Check the array index
        if (node.getChildren().get(0).getKind().equals("Integer")) {

        }
         */


        return new Type("int", false);
    }

    protected Type dealWithMethodCall(JmmNode node, SecondVisitorHelper secondVisitorHelper) {

        if (type != null)
            return type;

        //Variable dont exist
        if (node.getChildren().get(0).getKind().equals("Identifier")) {
            if (variableLookup(node.getChildren().get(0).get("value"), secondVisitorHelper) == null) {
                secondVisitorHelper.getReportList().add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), "Attempt to call on method on non declared variable"));
                return type;
            }
        }

        if (node.getChildren().get(1).getKind().equals("Identifier")) {
            if (node.getChildren().get(1).get("value").equals("length")) {
                type = new Type("int", false);
                return type;
            }

            MethodSymbol method = secondVisitorHelper.getSymbolTableIml().getMethodsHashmap().get(node.getChildren().get(1).get("value"));

            if (method == null) {
                secondVisitorHelper.getReportList().add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), "Attempt to call on method non existent in this object"));
                return type;
            }

            return method.getType();
        }


        return type;
    }

    protected Type dealWithInteger(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        if (type != null)
            return type;

        return new Type("int", false);
    }

    protected Type dealWithBoolean(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        if (type != null)
            return type;

        return new Type("boolean", false);
    }

    protected Type dealWithThis(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        if (type != null)
            return type;

        return new Type("this", false);
    }

    protected Type dealWithIdentifier(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        if (type != null)
            return type;

        Symbol symbol = variableLookup(node.get("value"), secondVisitorHelper);

        if (symbol == null) {
            secondVisitorHelper.getReportList().add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), "Attempt to access variable not declared"));
            return type;
        }

        return symbol.getType();
    }

    protected Type defaultVisit(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        return type;
    }

    public Type getType() {
        return type;
    }

    private Symbol variableLookup(String variableName, SecondVisitorHelper secondVisitorHelper) {
        MethodSymbol methodSymbol = secondVisitorHelper.getSymbolTableIml().getMethodsHashmap().get(secondVisitorHelper.getCurrentMethodName());

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
        for (Symbol symbol : secondVisitorHelper.getSymbolTableIml().getHashMapClassFields().keySet()) {
            //Exists a class Field variable with that name
            if (symbol.getName().equals(variableName))
                return symbol;
        }
        return null;
    }
}
