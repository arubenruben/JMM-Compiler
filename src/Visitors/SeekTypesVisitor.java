package Visitors;

import Visitors.helpers.SecondVisitorHelper;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SeekTypesVisitor extends PreorderJmmVisitor<SecondVisitorHelper, List<Type>> {
    private final List<Type> nodesFound = new ArrayList<>();

    public SeekTypesVisitor(String[] typesToSeek) {

        for (String typeToSeek : typesToSeek)
            addVisit(typeToSeek, this::dealWithType);

        setDefaultVisit(this::defaultVisit);
    }

    protected List<Type> dealWithType(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        //TODO:Refactor this. This is used to detect a variable that doesn't Exist
        if (nodesFound == null)
            return null;

        Map<Symbol, String> hashmap = secondVisitorHelper.getSymbolTableIml().getMethodsHashmap().get(secondVisitorHelper.getCurrentMethodName()).getVariables();

        if (node.getKind().equals("Identifier")) {
            for (Symbol methodSymbol : hashmap.keySet()) {
                if (methodSymbol.getName().equals(node.get("value"))) {
                    nodesFound.add(methodSymbol.getType());
                    return nodesFound;
                } else
                    return null;
            }
        } else
            nodesFound.add(new Type(node.getKind(), false));

        return nodesFound;
    }

    protected List<Type> defaultVisit(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        return nodesFound;
    }

}
