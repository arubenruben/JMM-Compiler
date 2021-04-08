package Visitors.helpers;

import Visitors.helpers.data_helpers.SecondVisitorHelper;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SeekTypesVisitor extends PreorderJmmVisitor<SecondVisitorHelper, List<Type>> {
    private List<Type> nodesFound;

    public SeekTypesVisitor(String[] typesToSeek) {

        this.nodesFound = new ArrayList<>();

        for (String typeToSeek : typesToSeek)
            addVisit(typeToSeek, this::dealWithType);

        setDefaultVisit(this::defaultVisit);
    }

    protected List<Type> dealWithType(JmmNode node, SecondVisitorHelper secondVisitorHelper) {

        Map<Symbol, String> hashmap = secondVisitorHelper.getSymbolTableIml().getMethodsHashmap().get(secondVisitorHelper.getCurrentMethodName()).getVariables();

        if (node.getKind().equals("Identifier")) {
            for (Symbol methodSymbol : hashmap.keySet()) {
                if (methodSymbol.getName().equals(node.get("value"))) {
                    nodesFound.add(methodSymbol.getType());
                    return nodesFound;
                }
            }
            System.err.println(("Variable not declared"));
        } else
            nodesFound.add(new Type(node.getKind(), false));

        return nodesFound;
    }

    protected List<Type> defaultVisit(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        return nodesFound;
    }

}
