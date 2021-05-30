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
import utils.ConstantPropagatingHelper;
import visitors.ollir.Dismember;
import visitors.semantic.helpers.SeekReturnTypeVisitor;
import visitors.semantic.helpers.data_helpers.SecondVisitorHelper;

import java.util.ArrayList;
import java.util.List;

public class OptimizationStage implements JmmOptimization {
    private static SymbolTableIml symbolTable;
    private static MethodSymbol currentMethod;
    private static int numberIfs = 0;
    private static int numberWhiles = 0;
    final private static String offset = "\t\t";
    private static boolean optimizeActive;
    public static List<ConstantPropagatingHelper> constantPropagating;
    private List<ConstantPropagatingHelper> deniedAdd;

    @Override
    public OllirResult toOllir(JmmSemanticsResult semanticsResult, boolean optimize) {

        optimizeActive = optimize;
        constantPropagating = new ArrayList<>();
        deniedAdd = new ArrayList<>();

        // Convert the AST to a String containing the equivalent OLLIR code
        symbolTable = (SymbolTableIml) semanticsResult.getSymbolTable();

        String ollirCode = ollirCodeString();

        System.out.println(ollirCode);

        // More reports from this stage
        List<Report> reports = new ArrayList<>();

        // Fac {} must be replaced by ollirCode
        return new OllirResult(semanticsResult, ollirCode, reports);
    }

    @Override
    public OllirResult toOllir(JmmSemanticsResult semanticsResult) {
        // Convert the AST to a String containing the equivalent OLLIR code
        optimizeActive = false;

        constantPropagating = new ArrayList<>();
        deniedAdd = new ArrayList<>();


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

            Dismember.initialize(symbolTable, method.getName());

            currentMethod = method;

            if (optimizeActive) {
                constantPropagating = new ArrayList<>();
                this.deniedAdd = new ArrayList<>();
                bootstrapConstantPropagating(method.getNode().getChildren().get(2));
            }

            code.append(dealWithMethod());
        }

        code.append(dealWithFooter());

        return code.toString();
    }

    private void bootstrapConstantPropagating(JmmNode node) {

        for (JmmNode statement : node.getChildren()) {

            if (statement.getKind().equals("While")) {
                bootstrapConstantPropagating(statement.getChildren().get(1));
            }
            if (statement.getKind().equals("If")) {
                bootstrapConstantPropagating(statement.getChildren().get(1));
                bootstrapConstantPropagating(statement.getChildren().get(2));
            }

            if (!statement.getKind().equals("Assignment"))
                continue;

            if (!statement.getChildren().get(0).getKind().equals("Identifier"))
                continue;

            for (ConstantPropagatingHelper helper : constantPropagating) {
                if (helper.getSymbol().getName().equals(statement.getChildren().get(0).get("value"))) {
                    constantPropagating.remove(helper);
                    deniedAdd.add(helper);
                    break;
                }
            }
            if (!statement.getChildren().get(1).getKind().equals("Integer") && !statement.getChildren().get(1).getKind().equals("Boolean"))
                continue;

            final ConstantPropagatingHelper helper = new ConstantPropagatingHelper(symbolTable.lookup(statement.getChildren().get(0).get("value"), currentMethod.getName()), statement.getChildren().get(1).get("value"));

            if (!deniedAdd.contains(helper))
                constantPropagating.add(helper);
        }
    }


    //------Deal With Things------

    private String dealWithClassFields() {
        StringBuilder code = new StringBuilder();

        for (Symbol field : symbolTable.getClassFields().values()) {
            code.append("\t").append(".field").append(" ").append("protected").append(" ").append(dealWithSymbol(field));
            code.append(";");
            code.append("\n");
        }
        return code.toString();
    }

    private String dealWithClassHeaders() {
        StringBuilder code = new StringBuilder();

        code.append(symbolTable.getClassName()).append(" ").append("{").append("\n");

        code.append(dealWithClassFields());

        code.append("\t").append(".construct ").append(symbolTable.getClassName()).append("().V").append("{").append("\n");
        code.append("\t\t").append("invokespecial").append("(this,").append("\"<init>\")").append(".V;").append("\n");
        code.append("\t").append("}");

        code.append("\n");

        return code.toString();
    }

    private String dealWithMethod() {
        StringBuilder code = new StringBuilder();

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
            case "While" -> {
                if (!optimizeActive)
                    code.append(dealWithLoopUnrolling(statement));
                else
                    code.append(dealWithWhile(statement));
            }
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

        numberWhiles++;
        code.append("Loop").append(labelAppender).append(":").append("\n");

        if (optimizeActive) {

            code.append(applyOffsetToString("\t", dealWithCondition(condition, labelAppender, "EndLoop")));

            for (JmmNode node : thenNode.getChildren())
                code.append(applyOffsetToString("\t", dealWithStatement(node)));

        } else {

            code.append(applyOffsetToString("\t", dealWithWhileCondition(condition, labelAppender, "Body")));

            code.append("goto EndLoop").append(labelAppender).append(";").append("\n");

            code.append("Body").append(labelAppender).append(":").append("\n");

            for (JmmNode node : thenNode.getChildren())
                code.append(applyOffsetToString("\t", dealWithStatement(node)));

        }
        code.append("\t").append("goto Loop").append(labelAppender).append(";").append("\n");

        code.append("EndLoop").append(labelAppender).append(":");

        code.append("\n");

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

        numberIfs++;

        code.append(dealWithCondition(condition, labelAppender, "else"));

        for (JmmNode node : thenNode.getChildren())
            code.append(applyOffsetToString("\t", dealWithStatement(node)));

        code.append(applyOffsetToString("\t", "goto endif" + labelAppender + ";" + "\n"));

        code.append("else").append(labelAppender).append(":").append("\n");

        for (JmmNode node : elseNode.getChildren())
            code.append(applyOffsetToString("\t", dealWithStatement(node)));

        code.append("endif").append(labelAppender).append(":");

        code.append("\n");

        return code.toString();
    }

    private String dealWithWhileCondition(JmmNode condition, String labelAppender, String gotoLabel) {
        StringBuilder code = new StringBuilder();

        final JmmNode logicCondition = condition.getChildren().get(0);

        switch (logicCondition.getKind()) {
            case "Less" -> {
                code.append(Dismember.run(logicCondition.getChildren().get(0)));
                code.append(Dismember.run(logicCondition.getChildren().get(1)));
                code.append("if(");
                code.append(logicCondition.getChildren().get(0).get("prefix")).append(logicCondition.getChildren().get(0).get("result")).append(logicCondition.getChildren().get(0).get("suffix"));
                code.append(" ").append("<.i32").append(" ");
                code.append(logicCondition.getChildren().get(1).get("prefix")).append(logicCondition.getChildren().get(1).get("result")).append(logicCondition.getChildren().get(1).get("suffix"));
                code.append(") goto").append(" ").append(gotoLabel).append(labelAppender).append(";").append("\n");
            }
            case "And" -> {
                code.append(Dismember.run(logicCondition.getChildren().get(0)));
                code.append(Dismember.run(logicCondition.getChildren().get(1)));
                code.append("if(");
                code.append(logicCondition.getChildren().get(0).get("prefix")).append(logicCondition.getChildren().get(0).get("result")).append(logicCondition.getChildren().get(0).get("suffix"));
                code.append(" ").append("&&.bool").append(" ");
                code.append(logicCondition.getChildren().get(1).get("prefix")).append(logicCondition.getChildren().get(1).get("result")).append(logicCondition.getChildren().get(1).get("suffix"));
                code.append(") goto").append(" ").append(gotoLabel).append(labelAppender).append(";").append("\n");
            }
            case "Not" -> {
                code.append(Dismember.run(logicCondition.getChildren().get(0)));
                code.append("if(");
                code.append(logicCondition.getChildren().get(0).get("prefix")).append(logicCondition.getChildren().get(0).get("result")).append(logicCondition.getChildren().get(0).get("suffix"));
                code.append(" ").append("!.bool").append(" ");
                code.append(logicCondition.getChildren().get(0).get("prefix")).append(logicCondition.getChildren().get(0).get("result")).append(logicCondition.getChildren().get(0).get("suffix"));
                code.append(") goto").append(" ").append(gotoLabel).append(labelAppender).append(";").append("\n");
            }
            case "Identifier", "Integer", "Boolean" -> {
                code.append(Dismember.run(logicCondition));
                code.append("if(");
                code.append(logicCondition.get("prefix")).append(logicCondition.get("result")).append(logicCondition.get("suffix"));
                code.append(" ").append("&&.bool").append(" ");
                code.append(logicCondition.get("prefix")).append(logicCondition.get("result")).append(logicCondition.get("suffix"));
                code.append(") goto").append(" ").append(gotoLabel).append(labelAppender).append(";").append("\n");
            }
        }

        return code.toString();
    }

    private String dealWithCondition(JmmNode condition, String labelAppender, String gotoLabel) {
        StringBuilder code = new StringBuilder();

        final JmmNode logicCondition = condition.getChildren().get(0);

        switch (logicCondition.getKind()) {
            case "Less" -> {
                code.append(Dismember.run(logicCondition.getChildren().get(0)));
                code.append(Dismember.run(logicCondition.getChildren().get(1)));
                code.append("if(");
                code.append(logicCondition.getChildren().get(0).get("prefix")).append(logicCondition.getChildren().get(0).get("result")).append(logicCondition.getChildren().get(0).get("suffix"));
                code.append(" ").append(">=.i32").append(" ");
                code.append(logicCondition.getChildren().get(1).get("prefix")).append(logicCondition.getChildren().get(1).get("result")).append(logicCondition.getChildren().get(1).get("suffix"));
                code.append(") goto").append(" ").append(gotoLabel).append(labelAppender).append(";").append("\n");
            }
            case "And" -> {
                code.append(Dismember.run(logicCondition.getChildren().get(0)));
                code.append(Dismember.run(logicCondition.getChildren().get(1)));
                code.append(invertAnd(logicCondition.getChildren().get(0)));
                code.append(invertAnd(logicCondition.getChildren().get(1)));
                code.append("if(");
                code.append(logicCondition.getChildren().get(0).get("prefix")).append(logicCondition.getChildren().get(0).get("result")).append(logicCondition.getChildren().get(0).get("suffix"));
                code.append(" ").append("||.bool").append(" ");
                code.append(logicCondition.getChildren().get(1).get("prefix")).append(logicCondition.getChildren().get(1).get("result")).append(logicCondition.getChildren().get(1).get("suffix"));
                code.append(") goto").append(" ").append(gotoLabel).append(labelAppender).append(";").append("\n");
            }
            case "Not" -> {
                code.append(Dismember.run(logicCondition.getChildren().get(0)));
                code.append("if(");
                code.append(logicCondition.getChildren().get(0).get("prefix")).append(logicCondition.getChildren().get(0).get("result")).append(logicCondition.getChildren().get(0).get("suffix"));
                code.append(" ").append("&&.bool").append(" ");
                code.append(logicCondition.getChildren().get(0).get("prefix")).append(logicCondition.getChildren().get(0).get("result")).append(logicCondition.getChildren().get(0).get("suffix"));
                code.append(") goto").append(" ").append(gotoLabel).append(labelAppender).append(";").append("\n");
            }
            case "Identifier", "Integer", "Boolean" -> {
                code.append(Dismember.run(logicCondition));
                code.append("if(");
                code.append(logicCondition.get("prefix")).append(logicCondition.get("result")).append(logicCondition.get("suffix"));
                code.append(" ").append("!.bool").append(" ");
                code.append(logicCondition.get("prefix")).append(logicCondition.get("result")).append(logicCondition.get("suffix"));
                code.append(") goto").append(" ").append(gotoLabel).append(labelAppender).append(";").append("\n");
            }
        }

        return code.toString();
    }

    private String invertAnd(JmmNode node) {

        StringBuilder code = new StringBuilder();
        final int registerUsed = Dismember.registersAvailable.remove(0);


        code.append("t").append(registerUsed).append(".bool").append(" :=").append(".bool ");

        code.append(node.get("prefix")).append(node.get("result")).append(node.get("suffix"));
        code.append(" ").append("!").append(".bool").append(" ");
        code.append(node.get("prefix")).append(node.get("result")).append(node.get("suffix"));

        code.append(";");
        code.append("\n");

        node.put("result", "t" + registerUsed);
        node.put("suffix", ".bool");

        return code.toString();

    }

    public String dealWithMethodCall(JmmNode statement) {
        StringBuilder code = new StringBuilder();

        final JmmNode leftChild = statement.getChildren().get(0);
        final JmmNode rightChild = statement.getChildren().get(1);

        if (rightChild.getNumChildren() > 0) {
            for (JmmNode parameter : rightChild.getChildren().get(0).getChildren())
                code.append(Dismember.run(parameter));
        }

        code.append(Dismember.run(leftChild));

        if (rightChild.get("value").equals("length"))
            code.append(Dismember.dealWithLengthCall(statement));
        else if (Dismember.isMethodCallStatic(statement))
            code.append(dealWithStaticMethodCall(statement));
        else
            code.append(dealWithNonStaticMethodCall(statement));

        return code.toString();
    }

    //TODO:Refactor this
    private String dealWithAssignment(JmmNode statement) {
        StringBuilder code = new StringBuilder();
        final JmmNode leftChild = statement.getChildren().get(0);
        final JmmNode rightChild = statement.getChildren().get(1);

        code.append(Dismember.run(leftChild));
        final String rightSideStr = Dismember.run(rightChild);

        if (leftChild.getKind().equals("ArrayAccess")) {

            final StringBuilder arrayAccessStr = new StringBuilder(rightSideStr);

            arrayAccessStr.append(leftChild.getChildren().get(0).get("prefix")).append(leftChild.getChildren().get(0).get("result")).append("[").append(leftChild.getChildren().get(1).get("prefix")).append(leftChild.getChildren().get(1).get("result")).append(leftChild.getChildren().get(1).get("suffix")).append("]").append(".i32");

            code.append(arrayAccessStr);
            code.append(" :=").append(leftChild.get("suffix")).append(" ");
            code.append(rightChild.get("prefix")).append(rightChild.get("result")).append(rightChild.get("suffix")).append(";");
            code.append("\n");

            return code.toString();
        }

        if (isSetter(statement)) {
            code.append(rightSideStr);

            if (!leftChild.get("suffix").equals(""))
                code.append("putfield(this,").append(" ").append(leftChild.get("result")).append(leftChild.get("suffix")).append(", ").append(rightChild.get("prefix")).append(rightChild.get("result")).append(rightChild.get("suffix")).append(").V").append(";");
            else
                code.append("putfield(this,").append(" ").append(leftChild.get("result")).append(rightChild.get("suffix")).append(", ").append(rightChild.get("prefix")).append(rightChild.get("result")).append(rightChild.get("suffix")).append(").V").append(";");

            code.append("\n");
            return code.toString();
        }
        if (Integer.parseInt(rightChild.get("registers")) == 1) {
            String operator = null;

            switch (rightChild.getKind()) {
                case "Add" -> operator = "+";
                case "Sub" -> operator = "-";
                case "Mult" -> operator = "*";
                case "Div" -> operator = "/";
                case "And" -> operator = "&&";
                case "Less" -> operator = "<";
            }
            if (operator != null) {

                code.append(leftChild.get("prefix"));

                if (leftChild.getKind().equals("Identifier"))
                    code.append(leftChild.get("value"));
                else
                    code.append(leftChild.get("result"));

                code.append(leftChild.get("suffix"));
                code.append(" :=").append(leftChild.get("suffix")).append(" ");

                final String codeOptimization = Dismember.expressionMathSimplification(rightChild, operator);

                if (codeOptimization.equals("")) {
                    code.append(rightChild.getChildren().get(0).get("prefix")).append(rightChild.getChildren().get(0).get("result")).append(rightChild.getChildren().get(0).get("suffix"));
                    code.append(" ").append(operator).append(leftChild.get("suffix")).append(" ");
                    code.append(rightChild.getChildren().get(1).get("prefix")).append(rightChild.getChildren().get(1).get("result")).append(rightChild.getChildren().get(1).get("suffix")).append(";");
                    code.append("\n");
                } else
                    code.append(codeOptimization);

                return code.toString();
            }
        }

        code.append(rightSideStr);

        code.append(leftChild.get("prefix"));

        if (leftChild.getKind().equals("Identifier"))
            code.append(leftChild.get("value"));
        else
            code.append(leftChild.get("result"));

        code.append(leftChild.get("suffix"));
        code.append(" :=").append(leftChild.get("suffix")).append(" ");
        code.append(rightChild.get("prefix")).append(rightChild.get("result")).append(rightChild.get("suffix")).append(";");

        code.append("\n");

        return code.toString();
    }

    private boolean isSetter(JmmNode statement) {
        final JmmNode leftChild = statement.getChildren().get(0);

        if (!leftChild.getKind().equals("Identifier"))
            return false;

        final String variableName = leftChild.get("value");

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


        final List<Symbol> parameters = new ArrayList<>();
        final JmmNode method_called = node.getChildren().get(1);
        final SecondVisitorHelper secondVisitorHelper = new SecondVisitorHelper(currentMethod.getName(), symbolTable, new ArrayList<>());
        if (method_called.getChildren().size() > 0) {
            final JmmNode parameterBlock = method_called.getChildren().get(0);
            for (JmmNode parameter : parameterBlock.getChildren()) {
                final SeekReturnTypeVisitor returnTypeVisitor = new SeekReturnTypeVisitor();
                returnTypeVisitor.visit(parameter, secondVisitorHelper);

                if (returnTypeVisitor.getType() == null)
                    return "";

                if (parameter.getKind().equals("Identifier") && parameter.getKind().contains("value"))
                    parameters.add(new Symbol(returnTypeVisitor.getType(), parameter.get("value")));
                else
                    parameters.add(new Symbol(returnTypeVisitor.getType(), ""));

            }
        }
        String methodName = secondVisitorHelper.nameGenerator(node.getChildren().get(1).get("value"), parameters);


        if (symbolTable.getMethodsHashmap().containsKey(methodName))
            type = symbolTable.getMethodsHashmap().get(methodName).getType();


        code.append("invokevirtual(");

        if (!leftChild.getKind().equals("This"))
            code.append(leftChild.get("prefix"));

        code.append(leftChild.get("result"));

        if (!leftChild.getKind().equals("This"))
            code.append(leftChild.get("suffix"));

        code.append(", \"").append(methodName).append("\"");

        if (rightChild.getNumChildren() > 0) {
            for (JmmNode parameter : rightChild.getChildren().get(0).getChildren())
                code.append(", ").append(parameter.get("prefix")).append(parameter.get("result")).append(parameter.get("suffix"));
        }


        String suffix;

        if (type != null)
            suffix = OptimizationStage.dealWithType(type);
        else
            suffix = Dismember.seekReturnTypeStaticCall(node);

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

        code.append(Dismember.run(returnNode.getChildren().get(0)));

        code.append("ret").append(dealWithType(currentMethod.getType())).append(" ").append(returnNode.getChildren().get(0).get("prefix")).append(returnNode.getChildren().get(0).get("result")).append(returnNode.getChildren().get(0).get("suffix"));

        code.append(";");
        code.append("\n");

        return code.toString();
    }

    private String dealWithLoopUnrolling(JmmNode statement) {
        int amountIterations;
        final JmmNode condition = statement.getChildren().get(0);
        final JmmNode thenNode = statement.getChildren().get(1);

        if (!condition.getChildren().get(0).getKind().equals("Less") || !condition.getChildren().get(0).getChildren().get(1).getKind().equals("Integer"))
            return dealWithWhile(statement);

        amountIterations = Integer.parseInt(condition.getChildren().get(0).getChildren().get(1).get("value"));

        if (amountIterations % 2 != 0)
            return dealWithWhile(statement);

        if (thenNode.getNumChildren() != 2)
            return dealWithWhile(statement);

        if (!thenNode.getChildren().get(0).getKind().equals("Assignment"))
            return dealWithWhile(statement);

        if (!thenNode.getChildren().get(0).getChildren().get(1).getKind().equals("Add") && !thenNode.getChildren().get(0).getChildren().get(1).getKind().equals("Sub") && !thenNode.getChildren().get(0).getChildren().get(1).getKind().equals("Div") && !thenNode.getChildren().get(0).getChildren().get(1).getKind().equals("Mult"))
            return dealWithWhile(statement);

        if (!thenNode.getChildren().get(1).getKind().equals("Assignment"))
            return dealWithWhile(statement);

        if (!thenNode.getChildren().get(1).getChildren().get(1).getKind().equals("Add"))
            return dealWithWhile(statement);

        if (!thenNode.getChildren().get(1).getChildren().get(1).getChildren().get(1).getKind().equals("Integer"))
            return dealWithWhile(statement);

        condition.getChildren().get(0).getChildren().get(1).put("value", String.valueOf(amountIterations / 2));

        JmmNode iPlus = thenNode.removeChild(1);

        thenNode.add(thenNode.getChildren().get(0));

        iPlus.getChildren().get(1).getChildren().get(1).put("value", String.valueOf(Integer.parseInt(iPlus.getChildren().get(1).getChildren().get(1).get("value"))));

        thenNode.add(iPlus);

        return dealWithWhile(statement);
    }
}
