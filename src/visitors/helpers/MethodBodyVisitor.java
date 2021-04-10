package visitors.helpers;

import symbols.MethodSymbol;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;


public class MethodBodyVisitor extends PreorderJmmVisitor<MethodSymbol, Boolean> {

    public MethodBodyVisitor() {
        addVisit("VarDeclaration", this::dealWithField);
        setDefaultVisit(this::defaultVisit);
    }

    protected Boolean dealWithField(JmmNode node, MethodSymbol methodSymbol) {

        methodSymbol.getVariables().put(
                new Symbol(
                        new Type(
                                node.getChildren().get(0).get("value"),
                                node.getChildren().get(0).get("isArray").equals("true")),
                        node.get("value")),
                ""
        );

        return true;
    }


    protected Boolean defaultVisit(JmmNode node, MethodSymbol methodSymbol) {
        return true;
    }
}
