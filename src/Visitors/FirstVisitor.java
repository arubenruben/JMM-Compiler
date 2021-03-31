package Visitors;

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

import java.util.List;
import java.util.stream.Collectors;

public class FirstVisitor extends AJmmVisitor<SymbolTable,String> {

    private final String identifierAttribute;
    private SymbolTable symbolTable;

    public FirstVisitor( String identifierAttribute, SymbolTable symbolTable) {

        this.identifierAttribute = identifierAttribute;
        this.symbolTable = symbolTable;
        String importIdentifier = "ImportPath";
        String fieldIdentifier = "VarDeclaration";

        addVisit(importIdentifier, this::dealWithImport);
        addVisit(fieldIdentifier, this::dealWithField);
        setDefaultVisit(this::defaultVisit);
    }

    public String dealWithField(JmmNode node, SymbolTable symbolTable) {

        if (node.getParent().getKind().equals("Class")){

            String name = node.getChildren().get(0).get("value");
            boolean isArray = (name.contains("[")) ? true: false;
            Type nodeType = new Type(name, isArray);
            Symbol fieldSymbol = new Symbol(nodeType,node.get("value"));

            symbolTable.getFields().add(fieldSymbol);
        }

        return "";
    }

    public String dealWithImport(JmmNode node, SymbolTable symbolTable) {

        symbolTable.getImports().add(node.get(identifierAttribute).substring(0,node.get(identifierAttribute).length()-1));

        return "";
    }

    private String defaultVisit(JmmNode node, SymbolTable symbolTable) {

        for (JmmNode child : node.getChildren()) {
            visit(child, symbolTable);
        }

        return "";
    }




}
