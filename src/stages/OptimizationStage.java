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
import visitors.ollir.SethiUllman;

import java.util.ArrayList;
import java.util.List;

public class OptimizationStage implements JmmOptimization {
    private static SymbolTableIml symbolTable;
    private static MethodSymbol currentMethod;
    private static int numberIfs = 0;
    private static int numberWhiles = 0;
    private static String offset = "\t\t";


    @Override
    public OllirResult toOllir(JmmSemanticsResult semanticsResult) {
        // Convert the AST to a String containing the equivalent OLLIR code
        symbolTable = (SymbolTableIml) semanticsResult.getSymbolTable();

        String ollirCode = ollirCodeString();

        System.out.println(ollirCode);

        // More reports from this stage
        List<Report> reports = new ArrayList<>();

        // Fac {} must be replaced by ollirCode
        return new OllirResult(semanticsResult, ollirCode, reports);
    }

    private String ollirCodeString() {
        StringBuilder code = new StringBuilder();

        code.append(dealWithClassHeaders());

        for (MethodSymbol method : symbolTable.getMethodsHashmap().values()) {
            SethiUllman.initialize(symbolTable, method.getName());
            code.append(dealWithMethod(method));
        }

        code.append(dealWithFooter());

        return code.toString();
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

    //------Deal With Things------

    private String dealWithClassHeaders() {
        StringBuilder code = new StringBuilder();

        code.append(symbolTable.getClassName()).append(" ").append("{").append("\n");
        code.append("\t").append(".construct ").append(symbolTable.getClassName()).append("().V").append("{").append("\n");
        code.append("\t\t").append("invokespecial").append("(this,").append("\"<init>\")").append(".V;").append("\n");
        code.append("\t").append("}");

        code.append("\n");

        return code.toString();
    }

    private String dealWithMethod(MethodSymbol method) {
        StringBuilder code = new StringBuilder();

        currentMethod = method;

        code.append("\t").append(dealWithMethodHeader());

        code.append(applyOffsetToString(offset, dealWithMethodBody()));

        code.append(applyOffsetToString(offset, dealWithReturn()));

        code.append("\t").append(dealWithFooter());

        code.append("\n");

        return code.toString();
    }

    private String dealWithMethodHeader() {
        StringBuilder code = new StringBuilder();

        code.append(".method public").append(" ");

        if (currentMethod.getName().equals("main"))
            code.append("static").append(" ");

        code.append(currentMethod.getName()).append("(").append(dealWithMethodHeaderParameters(currentMethod.getParameters())).append(")").append(dealWithType(currentMethod.getType())).append(" ").append("{");

        code.append("\n");

        return code.toString();

    }

    private String dealWithMethodHeaderParameters(List<Symbol> parameters) {
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < parameters.size(); i++) {
            Symbol parameter = parameters.get(i);

            code.append(dealWithSymbol(parameter));

            if (i != parameters.size() - 1)
                code.append(", ");

        }

        return code.toString();
    }

    private String dealWithMethodBody() {
        StringBuilder code = new StringBuilder();
        final JmmNode bodyNode = currentMethod.getNode().getChildren().get(2);

        for (JmmNode statement : bodyNode.getChildren())
            code.append(dealWithStatement(statement));

        return code.toString();
    }

    private String dealWithStatement(JmmNode statement) {
        StringBuilder code = new StringBuilder();
        switch (statement.getKind()) {
            case "While" -> code.append(dealWithWhile(statement));
            case "If" -> code.append(dealWithIf(statement));
            case "Assignment" -> code.append(dealWithAssignment(statement));
            case "MethodCall" -> code.append(dealWithMethodCall(statement));
        }
        return code.toString();
    }


    private String dealWithWhile(JmmNode statement) {
        StringBuilder code = new StringBuilder();
        String labelAppender = "";

        final JmmNode condition = statement.getChildren().get(0);
        final JmmNode thenNode = statement.getChildren().get(1);

        if (numberWhiles > 0)
            labelAppender = String.valueOf(numberWhiles);

        code.append("Loop").append(labelAppender).append(":").append("\n");

        code.append(applyOffsetToString("\t", dealWithCondition(condition, labelAppender, "Body")));

        code.append("goto EndLoop").append(labelAppender).append(";").append("\n");

        code.append("Body").append(labelAppender).append(":").append("\n");

        for (JmmNode node : thenNode.getChildren())
            code.append(applyOffsetToString("\t", dealWithStatement(node)));

        code.append("goto Loop").append(labelAppender).append(";").append("\n");

        code.append("EndLoop").append(labelAppender).append(":");

        code.append("\n");

        numberWhiles++;

        return code.toString();
    }

    private String dealWithIf(JmmNode statement) {
        StringBuilder code = new StringBuilder();
        String labelAppender = "";
        final JmmNode condition = statement.getChildren().get(0);
        final JmmNode thenNode = statement.getChildren().get(1);
        final JmmNode elseNode = statement.getChildren().get(2);

        if (numberIfs > 0)
            labelAppender = String.valueOf(numberIfs);

        code.append(dealWithCondition(condition, labelAppender, "else"));

        for (JmmNode node : thenNode.getChildren())
            code.append(applyOffsetToString("\t", dealWithStatement(node)));

        code.append(applyOffsetToString("\t", "goto endif" + labelAppender + ";" + "\n"));

        code.append("else").append(labelAppender).append(":").append("\n");

        for (JmmNode node : elseNode.getChildren())
            code.append(applyOffsetToString("\t", dealWithStatement(node)));

        code.append("endif").append(labelAppender).append(":");

        code.append("\n");

        numberIfs++;

        return code.toString();
    }

    private String dealWithCondition(JmmNode condition, String labelAppender, String gotoLabel) {
        StringBuilder code = new StringBuilder();

        final JmmNode logicCondition = condition.getChildren().get(0);


        switch (logicCondition.getKind()) {
            case "Less" -> {
                code.append(SethiUllman.run(logicCondition.getChildren().get(0)));
                code.append(SethiUllman.run(logicCondition.getChildren().get(1)));
                code.append("if(");
                code.append(logicCondition.getChildren().get(0).get("prefix")).append(logicCondition.getChildren().get(0).get("result")).append(logicCondition.getChildren().get(0).get("suffix"));
                code.append(" ").append(">=.i32").append(" ");
                code.append(logicCondition.getChildren().get(1).get("prefix")).append(logicCondition.getChildren().get(1).get("result")).append(logicCondition.getChildren().get(1).get("suffix"));
                code.append(") goto").append(" ").append(gotoLabel).append(labelAppender).append(";").append("\n");
            }
            case "And" -> {
                code.append(SethiUllman.run(logicCondition.getChildren().get(0)));
                code.append(SethiUllman.run(logicCondition.getChildren().get(1)));
                code.append("if(");
                code.append(logicCondition.getChildren().get(0).get("prefix")).append(logicCondition.getChildren().get(0).get("result")).append(logicCondition.getChildren().get(0).get("suffix"));
                code.append(" ").append("&&.bool").append(" ");
                code.append(logicCondition.getChildren().get(1).get("prefix")).append(logicCondition.getChildren().get(1).get("result")).append(logicCondition.getChildren().get(1).get("suffix"));
                code.append(") goto").append(" ").append(gotoLabel).append(labelAppender).append(";").append("\n");
            }
            case "Not" -> {
                code.append(SethiUllman.run(logicCondition.getChildren().get(0)));
                code.append("if(");
                code.append(logicCondition.getChildren().get(0).get("prefix")).append(logicCondition.getChildren().get(0).get("result")).append(logicCondition.getChildren().get(0).get("suffix"));
                code.append(" ").append("!.bool").append(" ");
                code.append(logicCondition.getChildren().get(0).get("prefix")).append(logicCondition.getChildren().get(0).get("result")).append(logicCondition.getChildren().get(0).get("suffix"));
                code.append(") goto").append(" ").append(gotoLabel).append(labelAppender).append(";").append("\n");
            }
            case "Identifier" -> {
                code.append(SethiUllman.run(logicCondition));
                code.append("if(");
                code.append(logicCondition.get("prefix")).append(logicCondition.get("result")).append(logicCondition.get("suffix"));
                code.append(") goto").append(" ").append(gotoLabel).append(labelAppender).append(";").append("\n");
            }
        }

        return code.toString();
    }

    public String dealWithMethodCall(JmmNode statement) {
        StringBuilder code = new StringBuilder();

        final JmmNode leftChild = statement.getChildren().get(0);
        final JmmNode rightChild = statement.getChildren().get(1);

        if (rightChild.getNumChildren() > 0) {
            for (JmmNode parameter : rightChild.getChildren().get(0).getChildren())
                code.append(SethiUllman.run(parameter));
        }

        code.append(SethiUllman.run(leftChild));

        if (rightChild.get("value").equals("length"))
            code.append(SethiUllman.dealWithLengthCall(statement));
        else if (SethiUllman.isMethodCallStatic(statement))
            code.append(dealWithStaticMethodCall(statement));
        else
            code.append(dealWithNonStaticMethodCall(statement));

        return code.toString();
    }

    private String dealWithAssignment(JmmNode statement) {
        StringBuilder code = new StringBuilder();
        final JmmNode leftChild = statement.getChildren().get(0);
        final JmmNode rightChild = statement.getChildren().get(1);

        code.append(SethiUllman.run(leftChild));
        code.append(SethiUllman.run(rightChild));

        if (isSetter(statement)) {
            code.append("putfield(this,").append(" ").append(leftChild.get("result")).append(leftChild.get("suffix")).append(", ").append(rightChild.get("prefix")).append(rightChild.get("result")).append(rightChild.get("suffix")).append(").V").append(";");
        } else {

            code.append(leftChild.get("prefix")).append(leftChild.get("result")).append(leftChild.get("suffix"));

            code.append(" :=").append(leftChild.get("suffix")).append(" ");

            code.append(rightChild.get("prefix")).append(rightChild.get("result")).append(rightChild.get("suffix")).append(";");

        }

        code.append("\n");

        return code.toString();
    }

    private boolean isSetter(JmmNode statement) {
        final JmmNode leftChild = statement.getChildren().get(0);

        if (!leftChild.getKind().equals("Identifier"))
            return false;

        final String variableName = leftChild.get("value");

        if (!symbolTable.getClassFields().containsKey(variableName))
            return false;

        if (symbolTable.getMethodsHashmap().get(currentMethod.getName()).getLocalVariables().containsKey(variableName))
            return false;

        for (Symbol parameter : symbolTable.getMethodsHashmap().get(currentMethod.getName()).getParameters()) {
            if (parameter.getName().equals(variableName))
                return false;
        }

        return true;
    }

    private String dealWithSymbol(Symbol symbol) {
        StringBuilder code = new StringBuilder();
        code.append(symbol.getName()).append(dealWithType(symbol.getType()));
        return code.toString();
    }

    public static String dealWithType(Type type) {
        StringBuilder code = new StringBuilder();

        switch (type.getName()) {
            case "int" -> {
                if (type.isArray())
                    code.append(".array.i32");
                else
                    code.append(".i32");
            }
            case "boolean" -> code.append(".bool");
            case "void" -> code.append(".V");
            default -> {
                if (type.isArray())
                    code.append(".array");
                code.append(".").append(type.getName());
            }
        }

        return code.toString();
    }

    private String dealWithFooter() {
        return "}";
    }

    private String applyOffsetToString(String offset, String rawCode) {
        StringBuilder code = new StringBuilder();

        if (rawCode.length() == 0)
            return code.toString();

        for (String str : rawCode.split("\n"))
            code.append(offset).append(str).append("\n");

        return code.toString();
    }

    private String dealWithStaticMethodCall(JmmNode node) {
        StringBuilder code = new StringBuilder();

        final JmmNode leftChild = node.getChildren().get(0);
        final JmmNode rightChild = node.getChildren().get(1);

        code.append("invokestatic(").append(leftChild.get("result")).append(", \"").append(rightChild.get("value")).append("\"");

        if (rightChild.getNumChildren() > 0) {
            for (JmmNode parameter : rightChild.getChildren().get(0).getChildren())
                code.append(", ").append(parameter.get("prefix")).append(parameter.get("result")).append(parameter.get("suffix"));
        }

        code.append(").V");
        code.append(";");
        code.append("\n");

        return code.toString();
    }

    public static String dealWithNonStaticMethodCall(JmmNode node) {
        StringBuilder code = new StringBuilder();
        final JmmNode leftChild = node.getChildren().get(0);
        final JmmNode rightChild = node.getChildren().get(1);

        Type type = null;

        if (symbolTable.getMethodsHashmap().containsKey(rightChild.get("value")))
            type = symbolTable.getMethodsHashmap().get(rightChild.get("value")).getType();


        code.append("invokevirtual(").append(leftChild.get("prefix")).append(leftChild.get("result")).append(leftChild.get("suffix")).append(", \"").append(rightChild.get("value")).append("\"");

        if (rightChild.getNumChildren() > 0) {
            for (JmmNode parameter : rightChild.getChildren().get(0).getChildren())
                code.append(", ").append(parameter.get("prefix")).append(parameter.get("result")).append(parameter.get("suffix"));
        }


        String suffix;

        if (type != null)
            suffix = OptimizationStage.dealWithType(type);
        else
            suffix = SethiUllman.seekReturnTypeStaticCall(node);

        node.put("suffix", suffix);

        code.append(")").append(suffix);

        code.append(";");
        code.append("\n");

        return code.toString();
    }

    private String dealWithReturn() {
        StringBuilder code = new StringBuilder();

        if (currentMethod.getNode().getNumChildren() < 4)
            return code.toString();

        final JmmNode returnNode = currentMethod.getNode().getChildren().get(3);

        code.append(SethiUllman.run(returnNode.getChildren().get(0)));

        code.append("ret").append(dealWithType(currentMethod.getType())).append(" ").append(returnNode.getChildren().get(0).get("prefix")).append(returnNode.getChildren().get(0).get("result")).append(returnNode.getChildren().get(0).get("suffix"));

        code.append(";");
        code.append("\n");

        return code.toString();
    }


}
