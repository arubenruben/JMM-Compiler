package Visitors.helpers;

import Visitors.helpers.data_helpers.SecondVisitorHelper;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.AJmmVisitor;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

import java.util.ArrayList;
import java.util.List;

public class SeekObjectCallerVisitor extends PreorderJmmVisitor<SecondVisitorHelper, Symbol> {
    private Symbol symbol;
    private final List<JmmNode> nodeList;

    public SeekObjectCallerVisitor() {
        this.nodeList = new ArrayList<>();
        addVisit("Identifier", this::dealWithIdentifier);
        addVisit("This", this::dealWithIdentifier);
        setDefaultVisit(this::defaultVisit);
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
            secondVisitorHelper.getReportList().add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), "Non declared variable"));

        return symbol;
    }

    protected Symbol dealWithArrayAccess(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        if (symbol != null)
            return symbol;

        if (node.getKind().equals("This")) {
            secondVisitorHelper.getReportList().add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), "This is not an array"));
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
