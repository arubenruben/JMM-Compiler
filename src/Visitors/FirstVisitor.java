package Visitors;

import Symbols.MethodSymbol;
import Symbols.SymbolTableIml;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
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
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FirstVisitor extends PreorderJmmVisitor<SymbolTableIml, Boolean> {

    public FirstVisitor() {

        addVisit("ImportPath", this::dealWithImport);
        addVisit("Class", this::dealWithClassDefinition);
        addVisit("Extends", this::dealWithSuper);
        addVisit("VarDeclaration", this::dealWithVarDeclaration);
        addVisit("ClassMethod", this::dealWithClassMethod);
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

    protected Boolean dealWithVarDeclaration(JmmNode node, SymbolTableIml symbolTable) {

        if (node.getParent().getKind().equals("Class"))
            return dealWithClassField(node, symbolTable);
        /*
        if (node.getParent().getKind().equals("Body"))
            return dealWithMethodVariable(node, symbolTable);
         */
        return false;
    }

    protected Boolean dealWithClassField(JmmNode node, SymbolTableIml symbolTable) {

        Type nodeType = new Type(node.getChildren().get(0).get("value"), node.getChildren().get(0).get("isArray").equals("true"));
        Symbol fieldSymbol = new Symbol(nodeType, node.get("value"));

        symbolTable.getFields().add(fieldSymbol);

        return true;
    }

    protected Boolean dealWithMethodVariable(JmmNode node, SymbolTableIml symbolTable) {
        System.err.println("Not implemented yet");
        return false;
    }

    protected Boolean dealWithClassMethod(JmmNode node, SymbolTableIml symbolTable) {


        return false;
    }

    protected Boolean defaultVisit(JmmNode node, SymbolTableIml symbolTable) {
        return true;
    }

}
