package stages;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ollir.JmmOptimization;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.report.Report;
import symbols.MethodSymbol;
import symbols.SymbolTableIml;
import visitors.ollir.SeekStatementsVisitor;
import visitors.ollir.SethiUllman;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2021 SPeCS.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

public class OptimizationStage implements JmmOptimization {

    @Override
    public OllirResult toOllir(JmmSemanticsResult semanticsResult) {

        JmmNode node = semanticsResult.getRootNode();

        // Convert the AST to a String containing the equivalent OLLIR code
        String ollirCode = ollirCodeString((SymbolTableIml) semanticsResult.getSymbolTable());

        System.out.println(ollirCode);

        /*
        String ollirCode = "HelloWorld {\n" +
                ".construct HelloWorld().V {\n" +
                "invokespecial(this, \"<init>\").V;\n" +
                "}\n" +
                ".method public static main(args.array.String).V {\n" +
                "invokestatic(ioPlus, \"printHelloWorld\").V;\n" +
                "}\n" +
                "}\n";

         */

        // More reports from this stage
        List<Report> reports = new ArrayList<>();

        // Fac {} must be replaced by ollirCode
        return new OllirResult(semanticsResult, ollirCode, reports);
    }


    private String ollirCodeString(SymbolTableIml symbolTable) {
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


        for (MethodSymbol method : symbolTable.getMethodsHashmap().values()) {
            code.append("\t").append(dealWithMethodHeader(method)).append(" {\n");
            code.append("\t").append(dealWithMethodBody(method));
            code.append("\t").append("}\n");
        }
        code.append("\n}\n");

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

    private String dealWithMethodBody(MethodSymbol method) {
        StringBuilder stringBuilder = new StringBuilder();
        JmmNode methodBodyNode = method.getNode().getChildren().get(2);
        SeekStatementsVisitor statementsVisitor = new SeekStatementsVisitor();
        statementsVisitor.visit(methodBodyNode);

        List<JmmNode> statements = statementsVisitor.getNodes();

        for (JmmNode node : statements) {
            if (node.getKind().equals("While")) {
                stringBuilder.append("Loop:\n");
                SethiUllman.firstStep(node.getChildren().get(0));
                SethiUllman.secondStep(node.getChildren().get(0).getChildren().get(0), stringBuilder);
                stringBuilder.append(dealWithWhile(node));
            }
        }

        stringBuilder.append("\n");

        return stringBuilder.toString();
    }

    private String dealWithWhile(JmmNode node) {
        JmmNode whileCondition = node.getChildren().get(0).getChildren().get(0);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\tBody:\n");

        if (Integer.parseInt(whileCondition.get("registers")) > 1) {

            stringBuilder.append("\t\t");
            stringBuilder.append("if(");
            stringBuilder.append(whileCondition.getChildren().get(0).get("result"));

            if (whileCondition.getKind().equals("And")) {
                stringBuilder.append(" &&.bool ");
            } else
                stringBuilder.append(" >=.i32 ");

            stringBuilder.append(whileCondition.getChildren().get(1).get("result"));
            stringBuilder.append(")");
            stringBuilder.append(" goto Body;\n");
        }

        stringBuilder.append("\t\tEndLoop:\n");

        return stringBuilder.toString();
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
