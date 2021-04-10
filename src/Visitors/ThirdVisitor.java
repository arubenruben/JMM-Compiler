package Visitors;

import Visitors.helpers.SeekObjectCallerVisitor;
import Visitors.helpers.data_helpers.SecondVisitorHelper;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

public class ThirdVisitor extends PreorderJmmVisitor<SecondVisitorHelper, Boolean> {

    public ThirdVisitor() {
        addVisit("Body", this::dealWithVariables);
        addVisit("Add", this::dealWithVariables);
        addVisit("Sub", this::dealWithVariables);
        addVisit("Mult", this::dealWithVariables);
        addVisit("Div", this::dealWithVariables);

        addVisit("ArrayAccess", this::dealWithArrayAccess);
        addVisit("MethodCall", this::dealWithMethodCall);

        /* TODO:Look At boolean operations
        addVisit("Assignment", this::dealWithAssignment);
        addVisit("And", this::dealWithBooleanOperation);
        addVisit("Not", this::dealWithBooleanOperation);
         */
        setDefaultVisit(this::defaultVisit);
    }

    protected Boolean dealWithVariables(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        SeekObjectCallerVisitor seekObjectCallerVisitor = new SeekObjectCallerVisitor();
        seekObjectCallerVisitor.visit(node, secondVisitorHelper);

        return true;
    }

    private Boolean dealWithArrayAccess(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        SeekObjectCallerVisitor seekObjectCallerVisitor = new SeekObjectCallerVisitor();
        seekObjectCallerVisitor.visit(node.getChildren().get(0), secondVisitorHelper);

        Symbol symbol = seekObjectCallerVisitor.getSymbol();

        if (symbol == null)
            secondVisitorHelper.getReportList().add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), "Non declared array"));


        return true;
    }

    private Boolean dealWithMethodCall(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        String methodName = node.getChildren().get(1).get("value");

        SeekObjectCallerVisitor seekObjectCallerVisitor = new SeekObjectCallerVisitor();

        //Test if it is an Imported Class
        if (node.getChildren().get(0).getKind().equals("Identifier") && secondVisitorHelper.getSymbolTableIml().getImportedClasses().contains(node.getChildren().get(0).get("value")))
            return true;


        seekObjectCallerVisitor.visit(node.getChildren().get(0), secondVisitorHelper);

        Symbol symbol = seekObjectCallerVisitor.getSymbol();

        if (symbol == null) {
            secondVisitorHelper.getReportList().add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), "Non declared object"));
            return true;
        }
        if (symbol.equals(new Symbol(new Type("this", false), "this"))) {
            //A class that extends something is to assumes the method exists always
            if (secondVisitorHelper.getSymbolTableIml().getSuper() != null) {
                return true;
            }
        }
        if (!secondVisitorHelper.getSymbolTableIml().getMethodsHashmap().containsKey(methodName)) {
            secondVisitorHelper.getReportList().add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), "This object don't contains this method"));
            return true;
        }


        return true;
    }

    protected Boolean defaultVisit(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        return true;
    }
}
