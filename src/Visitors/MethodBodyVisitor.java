package Visitors;

import Symbols.SymbolTableIml;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.AJmmVisitor;

import java.util.ArrayList;
import java.util.List;

public class MethodBodyVisitor extends AJmmVisitor<List<Symbol>, String> {

    public MethodBodyVisitor() {
        String fieldIdentifier = "VarDeclaration";

        addVisit(fieldIdentifier, this::dealWithField);
        setDefaultVisit(this::defaultVisit);
    }

    private String dealWithField(JmmNode node, List<Symbol> symbolList) {

        String name = node.getChildren().get(0).get("value");
        boolean isArray = node.getChildren().get(0).get("isArray").equals("true");
        Type nodeType = new Type(name, isArray);
        Symbol fieldSymbol = new Symbol(nodeType,node.get("value"));
        symbolList.add(fieldSymbol);

        return "";
    }


    private String defaultVisit(JmmNode node, List<Symbol> symbolList) {

        for (JmmNode child : node.getChildren()) {
            visit(child, symbolList);
        }

        return "";
    }
}
