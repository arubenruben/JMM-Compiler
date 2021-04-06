package Visitors;

import Symbols.MethodSymbol;
import Symbols.SymbolTableIml;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.AJmmVisitor;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.ast.examples.ExamplePreorderVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.specs.util.providers.ResourceProvider;
import pt.up.fe.specs.util.utilities.StringLines;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FirstVisitor extends AJmmVisitor<SymbolTableIml,String> {

    private final String identifierAttribute;

    public FirstVisitor( String identifierAttribute) {

        this.identifierAttribute = identifierAttribute;
        String importIdentifier = "ImportPath";
        String fieldIdentifier = "VarDeclaration";
        String methodIdentifier = "ClassMethod";

        addVisit(importIdentifier, this::dealWithImport);
        addVisit(fieldIdentifier, this::dealWithField);
        addVisit(methodIdentifier, this::dealWithClassMethod);
        setDefaultVisit(this::defaultVisit);
    }

    private String dealWithField(JmmNode node, SymbolTable symbolTable) {

        if (node.getParent().getKind().equals("Class")){

            String name = node.getChildren().get(0).get("value");
            boolean isArray = node.getChildren().get(0).get("isArray").equals("true");
            Type nodeType = new Type(name, isArray);
            Symbol fieldSymbol = new Symbol(nodeType,node.get("value"));

            symbolTable.getFields().add(fieldSymbol);
        }
        return "";
    }

    private String dealWithImport(JmmNode node, SymbolTableIml symbolTable) {

        symbolTable.getImports().add(node.get(identifierAttribute).substring(0,node.get(identifierAttribute).length()-1));

        return "";
    }

    private String dealWithClassMethod(JmmNode node, SymbolTableIml symbolTable) {

        List<Symbol> parameters = new ArrayList<>();
        List<Symbol> variables = new ArrayList<>();
        JmmNode returnType = node.getChildren().get(0);
        JmmNode parameterBlock = node.getChildren().get(1);

        for (JmmNode parameter : parameterBlock.getChildren()) {
            JmmNode nodeType = parameter.getChildren().get(0);
            JmmNode nodeIdentifier = parameter.getChildren().get(1);
            Type type = new Type(nodeType.get("value"), nodeType.get("isArray").equals("true"));
            parameters.add(new Symbol(type, nodeIdentifier.get("value")));
        }

        MethodBodyVisitor methodBodyVisitor = new MethodBodyVisitor();

        JmmNode bodyBlock = node.getChildren().get(2);

        methodBodyVisitor.visit(bodyBlock, variables);

        MethodSymbol methodSymbol = new MethodSymbol(new Type(node.get("value"), returnType.get("value").equals("true")), node.get("value"), parameters, variables);
        symbolTable.addMethod(node.get("value"), methodSymbol);
        return "";
    }

    private String defaultVisit(JmmNode node, SymbolTableIml symbolTable) {

        for (JmmNode child : node.getChildren()) {
            visit(child, symbolTable);
        }

        return "";
    }

}
