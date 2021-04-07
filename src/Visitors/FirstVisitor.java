package Visitors;

import Symbols.MethodSymbol;
import Symbols.SymbolTableIml;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

import java.util.ArrayList;
import java.util.List;

public class FirstVisitor extends PreorderJmmVisitor<SymbolTableIml, Boolean> {

    public FirstVisitor() {

        addVisit("ImportPath", this::dealWithImport);
        addVisit("Class", this::dealWithClassDefinition);
        addVisit("Extends", this::dealWithSuper);
        addVisit("VarDeclaration", this::dealWithClassFieldDeclaration);
        addVisit("ClassMethod", this::dealWithClassMethod);
        addVisit("Main", this::dealWithMain);
        setDefaultVisit(this::defaultVisit);
    }


    private Boolean dealWithSuper(JmmNode node, SymbolTableIml symbolTableIml) {
        symbolTableIml.setSuperName(node.get("value"));
        return true;
    }

    private Boolean dealWithClassDefinition(JmmNode node, SymbolTableIml symbolTableIml) {
        symbolTableIml.setClassName(node.get("value"));
        return true;
    }

    protected Boolean dealWithImport(JmmNode node, SymbolTableIml symbolTable) {
        StringBuilder importPath = new StringBuilder();

        Gson gson = new Gson();
        List<String> importList = gson.fromJson(node.get("value"), new TypeToken<List<String>>() {
        }.getType());

        for (int i = 0; i < importList.size(); i++) {
            importPath.append(importList.get(i));
            if (i < importList.size() - 1)
                importPath.append(".");
        }

        symbolTable.getImports().add(importPath.toString());

        return true;
    }

    protected Boolean dealWithClassFieldDeclaration(JmmNode node, SymbolTableIml symbolTable) {

        if (node.getParent().getKind().equals("Class"))
            return dealWithClassField(node, symbolTable);

        return false;
    }

    protected Boolean dealWithClassField(JmmNode node, SymbolTableIml symbolTable) {

        Type nodeType = new Type(node.getChildren().get(0).get("value"), node.getChildren().get(0).get("isArray").equals("true"));
        Symbol fieldSymbol = new Symbol(nodeType, node.get("value"));

        symbolTable.getFields().add(fieldSymbol);

        return true;
    }

    protected Boolean dealWithClassMethod(JmmNode node, SymbolTableIml symbolTable) {
        List<Symbol> parameters = new ArrayList<>();

        JmmNode parameterBlock = node.getChildren().get(1);
        JmmNode bodyBlock = node.getChildren().get(2);


        MethodSymbol methodSymbol = new MethodSymbol(
                new Type(node.getChildren().get(0).get("value"), node.getChildren().get(0).get("isArray").equals("true")),
                node.get("value"));


        for (JmmNode parameter : parameterBlock.getChildren()) {
            JmmNode nodeType = parameter.getChildren().get(0);
            JmmNode nodeIdentifier = parameter.getChildren().get(1);
            Type type = new Type(nodeType.get("value"), nodeType.get("isArray").equals("true"));
            parameters.add(new Symbol(type, nodeIdentifier.get("value")));
        }

        methodSymbol.setParameters(parameters);

        symbolTable.getMethodsHashmap().put(methodSymbol.getName(), methodSymbol);


        MethodBodyVisitor methodBodyVisitor = new MethodBodyVisitor();

        methodBodyVisitor.visit(bodyBlock, methodSymbol);

        return true;
    }

    protected Boolean dealWithMain(JmmNode node, SymbolTableIml symbolTableIml) {
        List<Symbol> parameters = new ArrayList<>();
        MethodSymbol methodSymbol = new MethodSymbol(
                new Type("void", false),
                "Main");

        parameters.add(new Symbol(new Type("String", true), node.get("value")));

        methodSymbol.setParameters(parameters);
        symbolTableIml.getMethodsHashmap().put(methodSymbol.getName(), methodSymbol);

        return true;
    }

    protected Boolean defaultVisit(JmmNode node, SymbolTableIml symbolTable) {
        return true;
    }

}
