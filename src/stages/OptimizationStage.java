package stages;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ollir.JmmOptimization;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import symbols.MethodSymbol;
import symbols.SymbolTableIml;
import visitors.ollir.SethiUllman;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OptimizationStage implements JmmOptimization {

    @Override
    public OllirResult toOllir(JmmSemanticsResult semanticsResult) {

        // Convert the AST to a String containing the equivalent OLLIR code
        String ollirCode = ollirCodeString((SymbolTableIml) semanticsResult.getSymbolTable());

        System.out.println(ollirCode);

        writeToFile(semanticsResult.getRootNode().toJson(), "results/ollir.txt");

        return new OllirResult(semanticsResult, ollirCode, new ArrayList<>());
    }

    private String ollirCodeString(SymbolTableIml symbolTable) {
        StringBuilder code = new StringBuilder();

        code.append(dealWithClassHeaders(symbolTable));

        for (MethodSymbol method : symbolTable.getMethodsHashmap().values()) {
            final JmmNode methodBody = method.getNode().getChildren().get(2);
            code.append("\t").append(dealWithMethodHeader(method)).append(" {\n");
            //TODO:Insert the \t using split and replace
            code.append(dealWithBody(methodBody));
            code.append("\t").append("}\n");
        }
        code.append("\n}\n");

        return code.toString();
    }

    private String dealWithClassHeaders(SymbolTableIml symbolTable) {
        StringBuilder code = new StringBuilder();
        // Create class declaration
        code.append(symbolTable.getClassName()).append(" {\n");

        // Create global variable declaration
        for (Symbol variable : symbolTable.getFields()) {
            code.append("\t").append(dealWithClassField(variable)).append("\n");
        }

        // Create default constructor
        code.append("\t").append(".construct ").append(symbolTable.getClassName()).append("().V{\n")
                .append("\t\t").append("invokespecial(this, \"<init>\").V;\n")
                .append("\t").append("}\n");

        return code.toString();
    }

    private String dealWithMethodHeader(MethodSymbol method) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(".method public ");

        if (method.getName().equals("main")) {
            stringBuilder.append("static ");
        }

        stringBuilder.append(method.getName()).append(" (");

        for (int i = 0; i < method.getParameters().size(); i++) {

            stringBuilder.append(dealWithField(method.getParameters().get(i)));

            if (i != method.getParameters().size() - 1) {
                stringBuilder.append(", ");
            }
        }

        stringBuilder.append(")").append(dealWithFieldType(method.getType()));

        return stringBuilder.toString();
    }

    private String dealWithBody(JmmNode bodyNode) {
        StringBuilder stringBuilder = new StringBuilder();

        for (JmmNode child : bodyNode.getChildren()) {
            switch (child.getKind()) {
                case "While" -> stringBuilder.append(dealWithWhile(child));
                case "Assignment" -> stringBuilder.append(dealWithAssignment(child));
            }
        }

        return stringBuilder.toString();
    }

    private String dealWithWhile(JmmNode node) {
        StringBuilder code = new StringBuilder();

        final JmmNode whileCondition = node.getChildren().get(0).getChildren().get(0);

        final String conditionStringRaw = dealWithWhileCondition(whileCondition);

        StringBuilder conditionStringParsed = new StringBuilder();
        code.append("\t\tLoop:\n");
        for (String str : conditionStringRaw.split("\n"))
            conditionStringParsed.append("\t\t ").append(str).append("\n");

        code.append(conditionStringParsed.toString());


        final String bodyStringRaw = dealWithBody(node.getChildren().get(1));

        StringBuilder bodyStringParsed = new StringBuilder();

        bodyStringParsed.append("\t\t").append("Body:").append("\n");

        for (String str : bodyStringRaw.split("\n"))
            bodyStringParsed.append("\t\t ").append(str).append("\n");

        code.append(bodyStringParsed.toString());

        code.append("\t\tEndLoop:\n");


        return code.toString();
    }

    private String dealWithWhileCondition(JmmNode node) {
        StringBuilder code = new StringBuilder();

        SethiUllman.firstStep(node);
        code.append(SethiUllman.secondStep(node));

        code.append("if(");
        code.append(node.getChildren().get(0).get("result"));

        if (node.getKind().equals("And"))
            code.append("&&.bool");
        else
            code.append(">=.i32");

        code.append(node.getChildren().get(1).get("result"));

        code.append(") goto Body;\n");
        code.append("goto EndLoop;\n");

        return code.toString();
    }

    private String dealWithAssignment(JmmNode node) {
        StringBuilder code = new StringBuilder();

        SethiUllman.firstStep(node.getChildren().get(1));
        code.append(SethiUllman.secondStep(node.getChildren().get(1)));
        code.append(node.getChildren().get(0).get("value"));
        code.append(":=");
        code.append(node.getChildren().get(1).getChildren().get(0).get("result"));

        switch (node.getChildren().get(1).getKind()) {
            case "Add" -> code.append("+");
            case "Sub" -> code.append("-");
            case "Mul" -> code.append("*");
            case "Div" -> code.append("/");
        }
        code.append(node.getChildren().get(1).getChildren().get(1).get("result"));

        code.append("\n");

        return code.toString();
    }

    private String dealWithClassField(Symbol variable) {
        return ".field protected " +
                dealWithField(variable);
    }

    private String dealWithField(Symbol variable) {
        return variable.getName() +
                dealWithFieldType(variable.getType());
    }

    private String dealWithFieldType(Type type) {
        StringBuilder stringBuilder = new StringBuilder();

        if (type.isArray())
            stringBuilder.append(".array");

        switch (type.getName()) {
            case "int" -> stringBuilder.append(".i32");
            case "boolean" -> stringBuilder.append(".bool");
            case "void" -> stringBuilder.append(".V");
            default -> stringBuilder.append(".").append(type.getName());
        }

        return stringBuilder.toString();
    }

    public static void writeToFile(String content, String path) {
        try {
            FileWriter myWriter = new FileWriter(path);
            myWriter.write(content);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred writing to file");
            e.printStackTrace();
        }
    }

    @Override
    public JmmSemanticsResult optimize(JmmSemanticsResult semanticsResult) {
        // THIS IS JUST FOR CHECKPOINT 3
        return semanticsResult;
    }

    @Override
    public OllirResult optimize(OllirResult ollirResult) {
        // THIS IS JUST FOR CHECKPOINT 3
        return ollirResult;
    }

}
