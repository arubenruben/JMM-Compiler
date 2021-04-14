package visitors.helpers;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.ast.AJmmVisitor;

public class OllirVisitorHelper {

    private final SymbolTable symbolTable;

    public OllirVisitorHelper(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public String visit(JmmNode node) {
        StringBuilder stringBuilder = new StringBuilder();
        switch (node.getKind()) {
            case "Less" -> stringBuilder.append(dealWithLess(node));
            case "And" -> stringBuilder.append(dealWithAnd(node));
            case "Boolean" -> stringBuilder.append(dealWithBoolean(node));
            case "Integer" -> stringBuilder.append(dealWithInteger(node));
            case "Not" -> stringBuilder.append(dealWithNot(node));
            case "MethodCall" -> stringBuilder.append(dealWithMethodCall(node));
            case "ArrayAccess" -> stringBuilder.append(dealWithArrayAccess(node));
        }

        return stringBuilder.toString();
    }


    protected String dealWithLess(JmmNode node) {
        return visit(node.getChildren().get(0)) +
                " >= " +
                visit(node.getChildren().get(1));
    }

    protected String dealWithAnd(JmmNode node) {
        return visit(node.getChildren().get(0)) +
                " && " +
                visit(node.getChildren().get(1));
    }

    protected String dealWithNot(JmmNode node) {
        return visit(node.getChildren().get(0)) +
                " !.bool " +
                visit(node.getChildren().get(0));

    }

    protected String dealWithBoolean(JmmNode node) {
        StringBuilder stringBuilder = new StringBuilder();

        if (node.get("value").equals("true"))
            stringBuilder.append("1.bool");
        else
            stringBuilder.append("0.bool");

        return stringBuilder.toString();
    }

    protected String dealWithInteger(JmmNode node) {
        return node.get("value") + ".i32";
    }

    protected String dealWithMethodCall(JmmNode node) {
        return null;
    }

    protected String dealWithArrayAccess(JmmNode node) {
        return null;
    }

}
