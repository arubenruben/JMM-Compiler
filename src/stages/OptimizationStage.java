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

import java.util.ArrayList;

public class OptimizationStage implements JmmOptimization {

    @Override
    public OllirResult toOllir(JmmSemanticsResult semanticsResult) {
        // Convert the AST to a String containing the equivalent OLLIR code
        String ollirCode = ollirCodeString((SymbolTableIml) semanticsResult.getSymbolTable());
        System.out.println(ollirCode);

        return new OllirResult(semanticsResult, ollirCode, new ArrayList<>());
    }

    private String ollirCodeString(SymbolTableIml symbolTable) {
        StringBuilder code = new StringBuilder();

        code.append(dealWithClassHeaders(symbolTable));

        for (MethodSymbol method : symbolTable.getMethodsHashmap().values()) {

            SethiUllman.initialize((SymbolTableIml) symbolTable, method.getName());

            final JmmNode methodBody = method.getNode().getChildren().get(2);
            code.append("\t").append(dealWithMethodHeader(method)).append(" {\n");

            String stringBodyRaw = dealWithBody(methodBody);
            StringBuilder stringBuilder = new StringBuilder();

            for (String string : stringBodyRaw.split("\n"))
                stringBuilder.append("\t\t").append(string).append("\n");

            code.append(stringBuilder);

            code.append("\t").append("}\n");
        }
        code.append("\n}\n");

        return code.toString();
    }

    private String dealWithClassHeaders(SymbolTableIml symbolTable) {
        StringBuilder code = new StringBuilder();
        // Create class declaration
        code.append(symbolTable.getClassName()).append("{\n");

        // Create global variable declaration
        for (Symbol variable : symbolTable.getFields())
            code.append("\t").append(dealWithClassField(variable)).append("\n");

        // Create default constructor
        code.append("\t").append(".construct ").append(symbolTable.getClassName()).append("().V{\n")
                .append("\t\tinvokespecial(this, \"<init>\").V;").append("\n");

        code.append("\t").append("}\n");

        return code.toString();
    }

    private String dealWithMethodHeader(MethodSymbol method) {
        StringBuilder code = new StringBuilder();

        code.append(".method public ");

        if (method.getName().equals("main")) {
            code.append("static ");
        }

        code.append(method.getName()).append(" (");

        for (int i = 0; i < method.getParameters().size(); i++) {

            code.append(dealWithField(method.getParameters().get(i)));

            if (i != method.getParameters().size() - 1) {
                code.append(", ");
            }
        }

        code.append(")").append(dealWithFieldType(method.getType()));

        return code.toString();
    }

    private String dealWithBody(JmmNode bodyNode) {
        StringBuilder code = new StringBuilder();

        for (JmmNode child : bodyNode.getChildren()) {
            switch (child.getKind()) {
                case "While" -> code.append(dealWithWhile(child));
                case "If" -> code.append(dealWithIf(child));
                case "Assignment" -> code.append(dealWithAssignment(child));
                case "MethodCall" -> code.append(dealWithMethodCall(child));
            }
        }

        return code.toString();
    }

    private String dealWithWhile(JmmNode node) {
        StringBuilder code = new StringBuilder();

        final JmmNode whileCondition = node.getChildren().get(0).getChildren().get(0);


        //Condition of while
        final String conditionStringRaw = dealWithWhileCondition(whileCondition);

        StringBuilder conditionStringParsed = new StringBuilder();

        code.append("Loop:\n");

        for (String str : conditionStringRaw.split("\n"))
            conditionStringParsed.append("\t ").append(str).append("\n");

        code.append(conditionStringParsed);

        //Body of while
        final String bodyStringRaw = dealWithBody(node.getChildren().get(1));
        StringBuilder bodyStringParsed = new StringBuilder();

        bodyStringParsed.append("Body:").append("\n");

        for (String str : bodyStringRaw.split("\n"))
            bodyStringParsed.append("\t ").append(str).append("\n");

        code.append(bodyStringParsed);

        code.append("EndLoop:\n");

        return code.toString();
    }

    private String dealWithIf(JmmNode node) {
        StringBuilder code = new StringBuilder();

        final JmmNode conditionNode = node.getChildren().get(0).getChildren().get(0);
        final JmmNode thenNode = node.getChildren().get(1);
        final JmmNode elseNode = node.getChildren().get(2);

        //Condition
        code.append(SethiUllman.run(conditionNode));

        code.append("if(").append(conditionNode.getChildren().get(0).get("result"));

        if (conditionNode.getKind().equals("And"))
            code.append(" &&.bool ");
        else
            code.append(" >=.i32 ");

        code.append(conditionNode.getChildren().get(1).get("result")).append(")goto else;\n");

        //If Body
        StringBuilder bodyStringParsed = new StringBuilder();
        String bodyStringRaw = dealWithBody(thenNode);

        for (String str : bodyStringRaw.split("\n"))
            bodyStringParsed.append("\t").append(str).append("\n");

        code.append(bodyStringParsed).append("goto endif;\n");

        //Else
        code.append("else:\n");

        StringBuilder elseBodyStringParsed = new StringBuilder();
        String elseBodyStringRaw = dealWithBody(elseNode);

        for (String str : elseBodyStringRaw.split("\n"))
            elseBodyStringParsed.append("\t").append(str).append("\n");

        code.append(elseBodyStringParsed);

        //EndIf
        code.append("endif:\n");

        return code.toString();
    }

    private String dealWithWhileCondition(JmmNode node) {
        StringBuilder code = new StringBuilder();

        code.append(SethiUllman.run(node));

        code.append("if(");

        if (node.getChildren().get(0).getAttributes().contains("typePrefix")) {
            code.append(node.getChildren().get(0).get("typePrefix"));
        }
        code.append(node.getChildren().get(0).get("result"));

        if (node.getChildren().get(0).getAttributes().contains("typeSuffix")) {
            code.append(node.getChildren().get(0).get("typeSuffix"));
        }

        if (node.getKind().equals("And"))
            code.append(" &&.bool ");
        else
            code.append(" >=.i32 ");

        code.append(node.getChildren().get(1).get("result")).append(") goto Body;\n");
        code.append("goto EndLoop;\n");

        return code.toString();
    }

    private String dealWithAssignment(JmmNode node) {
        StringBuilder code = new StringBuilder();

        code.append(SethiUllman.run(node.getChildren().get(0)));

        code.append(SethiUllman.run(node.getChildren().get(1)));

        if (node.getChildren().get(0).getAttributes().contains("typePrefix"))
            code.append(node.getChildren().get(0).get("typePrefix"));

        code.append(node.getChildren().get(0).get("result"));

        if (node.getChildren().get(0).getAttributes().contains("typeSuffix"))
            code.append(node.getChildren().get(0).get("typeSuffix"));

        code.append(" :=").append(node.getChildren().get(0).get("typeSuffix")).append(" ");

        if (node.getChildren().get(1).getAttributes().contains("result")) {
            if (node.getChildren().get(1).getAttributes().contains("typePrefix"))
                code.append(node.getChildren().get(1).get("typePrefix"));

            code.append(node.getChildren().get(1).get("result"));

            if (node.getChildren().get(1).getAttributes().contains("typeSuffix"))
                code.append(node.getChildren().get(1).get("typeSuffix"));


        } else {

            if (node.getChildren().get(1).getChildren().get(0).getAttributes().contains("typePrefix"))
                code.append(node.getChildren().get(1).getChildren().get(1).get("typePrefix"));

            code.append(node.getChildren().get(1).getChildren().get(0).get("result"));

            if (node.getChildren().get(1).getChildren().get(0).getAttributes().contains("typeSuffix"))
                code.append(node.getChildren().get(1).getChildren().get(0).get("typeSuffix"));


            switch (node.getChildren().get(1).getKind()) {
                case "Add" -> code.append(" +.i32 ");
                case "Sub" -> code.append(" -.i32 ");
                case "Mult" -> code.append(" *.i32 ");
                case "Div" -> code.append(" /.i32 ");
                case "And" -> code.append(" &&.bool ");
                case "Less" -> code.append(" <.bool ");
            }

            if (node.getChildren().get(1).getChildren().get(1).getAttributes().contains("typePrefix"))
                code.append(node.getChildren().get(1).getChildren().get(1).get("typePrefix"));

            code.append(node.getChildren().get(1).getChildren().get(1).get("result"));

            if (node.getChildren().get(1).getChildren().get(1).getAttributes().contains("typeSuffix"))
                code.append(node.getChildren().get(1).getChildren().get(1).get("typeSuffix"));
        }
        code.append(";");
        code.append("\n");

        return code.toString();
    }

    private String dealWithMethodCall(JmmNode node) {
        StringBuilder code = new StringBuilder();

        code.append(SethiUllman.run(node.getChildren().get(0)));

        for (JmmNode parameter : node.getChildren().get(1).getChildren().get(0).getChildren())
            code.append(SethiUllman.run(parameter));

        code.append("invokestatic(").append(node.getChildren().get(0).get("result")).append(",").append(node.getChildren().get(1).get("value"));

        for (JmmNode parameter : node.getChildren().get(1).getChildren().get(0).getChildren())
            code.append(",").append(parameter.get("result"));

        code.append(").V");

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
