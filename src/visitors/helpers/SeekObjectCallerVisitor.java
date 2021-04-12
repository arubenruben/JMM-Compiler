package visitors.helpers;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;
import visitors.helpers.data_helpers.SecondVisitorHelper;

public class SeekObjectCallerVisitor extends PreorderJmmVisitor<SecondVisitorHelper, Symbol> {
    private Symbol symbol;

    public SeekObjectCallerVisitor() {
        addVisit("Not", this::dealWithUnaryOperator);
        addVisit("Identifier", this::dealWithIdentifier);
        addVisit("This", this::dealWithIdentifier);
        setDefaultVisit(this::defaultVisit);
    }

    private Symbol dealWithUnaryOperator(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        if (symbol != null)
            return symbol;

        if (node.getChildren().get(0).getKind().equals("Identifier"))
            return dealWithVariable(node.getChildren().get(0), secondVisitorHelper);

        if (node.getChildren().get(0).getKind().equals("This"))
            return dealWithVariable(node.getChildren().get(0), secondVisitorHelper);

        return symbol;
    }


    protected Symbol dealWithIdentifier(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        if (symbol != null)
            return symbol;

        if (node.getParent().getKind().equals("Body"))
            return dealWithVariable(node, secondVisitorHelper);
        if (node.getParent().getKind().equals("Add"))
            return dealWithVariable(node, secondVisitorHelper);
        if (node.getParent().getKind().equals("Minus"))
            return dealWithVariable(node, secondVisitorHelper);
        if (node.getParent().getKind().equals("Mult"))
            return dealWithVariable(node, secondVisitorHelper);
        if (node.getParent().getKind().equals("Div"))
            return dealWithVariable(node, secondVisitorHelper);
        if (node.getParent().getKind().equals("ArrayAccess") && node.getParent().getChildren().get(0).equals(node))
            return dealWithArrayAccess(node, secondVisitorHelper);
        if (node.getParent().getKind().equals("MethodCall") && node.getParent().getChildren().get(0).equals(node))
            return dealWithMethodCall(node, secondVisitorHelper);

        return symbol;
    }

    protected Symbol dealWithVariable(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        if (symbol != null)
            return symbol;

        if (node.getKind().equals("This")) {
            symbol = secondVisitorHelper.getSymbolTableIml().getVariableThis();
            return symbol;
        }

        symbol = secondVisitorHelper.getSymbolTableIml().lookup(node.get("value"), secondVisitorHelper.getCurrentMethodName());

        if (symbol == null)
            secondVisitorHelper.getReportList().add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Non declared variable"));

        return symbol;
    }

    protected Symbol dealWithArrayAccess(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        if (symbol != null)
            return symbol;

        if (node.getKind().equals("This")) {
            secondVisitorHelper.getReportList().add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "This is not an array"));
            return symbol;
        }

        symbol = secondVisitorHelper.getSymbolTableIml().lookup(node.get("value"), secondVisitorHelper.getCurrentMethodName());

        return symbol;

    }

    protected Symbol dealWithMethodCall(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        if (symbol != null)
            return symbol;

        if (node.getKind().equals("This")) {
            symbol = secondVisitorHelper.getSymbolTableIml().getVariableThis();
            return symbol;
        }

        symbol = secondVisitorHelper.getSymbolTableIml().lookup(node.get("value"), secondVisitorHelper.getCurrentMethodName());

        return symbol;
    }

    protected Symbol defaultVisit(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        return symbol;
    }

    public Symbol getSymbol() {
        return symbol;
    }
}
