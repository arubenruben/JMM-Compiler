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

        code.append(applyOffsetToString("\t\t", dealWithMethodBody()));

        code.append("\t").append(dealWithFooter());

        code.append("\n");

        return code.toString();
    }

    private String dealWithMethodHeader() {
        StringBuilder code = new StringBuilder();

        code.append(".method public ").append(currentMethod.getName()).append("(").append(dealWithMethodHeaderParameters(currentMethod.getParameters())).append(")").append(dealWithType(currentMethod.getType())).append(" ").append("{");

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

        for (JmmNode statement : bodyNode.getChildren()) {
            switch (statement.getKind()) {
                case "While" -> code.append(dealWithWhile(statement));
                case "If" -> code.append(dealWithIf(statement));
                case "Assignment" -> code.append(dealWithAssignment(statement));
                case "MethodCall" -> code.append(dealWithMethodCall(statement));
            }
        }
        return code.toString();
    }


    private String dealWithWhile(JmmNode statement) {
        StringBuilder code = new StringBuilder();

        code.append("\n");
        return code.toString();
    }

    private String dealWithIf(JmmNode statement) {
        StringBuilder code = new StringBuilder();

        code.append("\n");
        return code.toString();
    }

    public String dealWithMethodCall(JmmNode statement) {
        StringBuilder code = new StringBuilder();

        final JmmNode leftChild = statement.getChildren().get(0);
        final JmmNode rightChild = statement.getChildren().get(1);

        for (JmmNode parameter : rightChild.getChildren().get(0).getChildren())
            code.append(SethiUllman.run(parameter));

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
            case "int" -> code.append(".i32");
            case "boolean" -> code.append(".bool");
            case "void" -> code.append(".V");
            default -> code.append(".").append(type.getName());
        }

        return code.toString();
    }

    private String dealWithFooter() {
        return "}";
    }

    private String applyOffsetToString(String offset, String rawCode) {
        StringBuilder code = new StringBuilder();

        for (String str : rawCode.split("\n"))
            code.append(offset).append(str).append("\n");

        return code.toString();
    }

    private String dealWithStaticMethodCall(JmmNode node) {
        StringBuilder code = new StringBuilder();

        final JmmNode leftChild = node.getChildren().get(0);
        final JmmNode rightChild = node.getChildren().get(1);

        code.append("invokestatic(").append(leftChild.get("result")).append(", \"").append(rightChild.get("value")).append("\"");

        for (JmmNode parameter : rightChild.getChildren().get(0).getChildren())
            code.append(", ").append(parameter.get("prefix")).append(parameter.get("result")).append(parameter.get("suffix"));

        code.append(").V");
        code.append(";");
        code.append("\n");

        return code.toString();
    }

    public static String dealWithNonStaticMethodCall(JmmNode node) {
        StringBuilder code = new StringBuilder();
        final JmmNode leftChild = node.getChildren().get(0);
        final JmmNode rightChild = node.getChildren().get(1);

        MethodSymbol method = symbolTable.getMethodsHashmap().get(rightChild.get("value"));

        if (method == null)
            return "";

        code.append("invokevirtual(").append(leftChild.get("prefix")).append(leftChild.get("result")).append(leftChild.get("suffix")).append(", \"").append(rightChild.get("value")).append("\"");

        for (JmmNode parameter : rightChild.getChildren().get(0).getChildren())
            code.append(", ").append(parameter.get("prefix")).append(parameter.get("result")).append(parameter.get("suffix"));

        final String suffix = OptimizationStage.dealWithType(method.getType());

        node.put("suffix", suffix);

        code.append(")").append(suffix);

        code.append(";");
        code.append("\n");

        return code.toString();
    }


}
