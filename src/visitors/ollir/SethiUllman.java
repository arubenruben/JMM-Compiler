package visitors.ollir;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import stages.OptimizationStage;
import symbols.MethodSymbol;
import symbols.SymbolTableIml;

import java.util.ArrayList;
import java.util.List;

public class SethiUllman {

    private static SymbolTableIml symbolTable;
    private static String currentMethod;
    public static List<Integer> registersAvailable;

    public static void initialize(SymbolTableIml symbolTable, String currentMethod) {

        SethiUllman.symbolTable = symbolTable;
        SethiUllman.currentMethod = currentMethod;

        registersAvailable = new ArrayList<>();

        for (int i = 1; i <= 1000; i++)
            registersAvailable.add(i);

    }

    public static String run(JmmNode node) {
        firstStep(node);
        return secondStep(node);
    }

    private static void firstStep(JmmNode node) {

        if (isTerminal(node)) {
            fillTerminalValue(node);
        } else {
            for (JmmNode child : node.getChildren())
                firstStep(child);

            fillNonTerminalValue(node);
        }

        node.put("result", "");
        node.put("prefix", "");
        node.put("suffix", "");
    }

    private static void fillNonTerminalValue(JmmNode node) {
        if (node.getNumChildren() == 1 && node.getKind().equals("Not")) {
            int leftChildValue = Integer.parseInt(node.getChildren().get(0).get("registers"));
            node.put("registers", String.valueOf(Math.max(1, leftChildValue)));
        } else if (node.getNumChildren() == 1 && !node.getKind().equals("Not")) {
            int leftChildValue = Integer.parseInt(node.getChildren().get(0).get("registers"));
            node.put("registers", String.valueOf(leftChildValue));
        } else if (node.getNumChildren() == 2) {
            int leftChildValue = Integer.parseInt(node.getChildren().get(0).get("registers"));
            int rightChildValue = Integer.parseInt(node.getChildren().get(1).get("registers"));

            if (leftChildValue == rightChildValue)
                node.put("registers", String.valueOf(leftChildValue + 1));
            else
                node.put("registers", String.valueOf(Math.max(leftChildValue, rightChildValue)));
        }
    }

    private static String secondStep(JmmNode node) {
        StringBuilder code = new StringBuilder();

        if (isTerminal(node) && Integer.parseInt(node.get("registers")) == 0) {
            fillTerminalNonDismember(node);
            return code.toString();
        }

        if (!isTerminal(node) && node.getNumChildren() == 1)
            code.append(secondStep(node.getChildren().get(0)));

        //For Method Call
        if (!isTerminal(node) && node.getNumChildren() == 2) {
            if (Integer.parseInt(node.getChildren().get(0).get("registers")) >= Integer.parseInt(node.getChildren().get(1).get("registers"))) {
                code.append(secondStep(node.getChildren().get(0)));
                code.append(secondStep(node.getChildren().get(1)));
            } else {
                code.append(secondStep(node.getChildren().get(1)));
                code.append(secondStep(node.getChildren().get(0)));
            }
        }

        //Fire dismember
        if (canDismember(node)) {
            code.append(dismember(node));
            return code.toString();
        }

        return code.toString();
    }

    private static void fillTerminalNonDismember(JmmNode node) {
        switch (node.getKind()) {
            case "Identifier" -> {
                node.put("prefix", prefixSeeker(node.get("value")));
                node.put("result", node.get("value"));
                node.put("suffix", suffixSeeker(node.get("value")));
            }
            case "Integer" -> {
                node.put("result", node.get("value"));
                node.put("suffix", ".i32");
            }
            case "Boolean" -> {
                node.put("result", node.get("value"));
                node.put("suffix", ".bool");
            }
            case "This" -> {
                node.put("result", "this");
                node.put("suffix", "." + symbolTable.getClassName());
            }
        }
    }

    private static boolean canDismember(JmmNode node) {

        if (node.getKind().equals("MethodCall"))
            return true;

        if (node.getKind().equals("NewObject"))
            return true;

        if (Integer.parseInt(node.get("registers")) >= 1)
            return true;

        if (Integer.parseInt(node.get("registers")) == 0)
            return false;


        return false;
    }


    private static boolean isTerminal(JmmNode node) {
        return switch (node.getKind()) {
            case "Identifier", "This", "Boolean", "Integer", "NewObject", "MethodCall", "NewArray", "ArrayAccess" -> true;
            default -> false;
        };
    }

    private static void fillTerminalValue(JmmNode node) {
        switch (node.getKind()) {
            case "Boolean", "Integer", "This" -> node.put("registers", "0");
            case "Identifier" -> {
                if (isGetter(node))
                    node.put("registers", "1");
                else
                    node.put("registers", "0");
            }
            case "NewObject", "MethodCall", "NewArray", "ArrayAccess" -> node.put("registers", "1");
        }
    }

    private static boolean isGetter(JmmNode node) {
        final String variableName = node.get("value");

        if (node.getParent().getKind().equals("Assignment"))
            return false;

        if (!symbolTable.getClassFields().containsKey(variableName))
            return false;

        if (symbolTable.getMethodsHashmap().get(currentMethod).getLocalVariables().containsKey(variableName))
            return false;

        for (Symbol parameter : symbolTable.getMethodsHashmap().get(currentMethod).getParameters()) {
            if (parameter.getName().equals(variableName))
                return false;
        }

        return true;
    }

    public static boolean isMethodCallStatic(JmmNode node) {

        final JmmNode leftChild = node.getChildren().get(0);
        final String invokerName = leftChild.get("result");

        if (!symbolTable.getImportedClasses().contains(invokerName))
            return false;

        if (symbolTable.getClassFields().containsKey(invokerName))
            return false;

        if (symbolTable.getMethodsHashmap().get(currentMethod).getLocalVariables().containsKey(invokerName))
            return false;

        for (Symbol parameter : symbolTable.getMethodsHashmap().get(currentMethod).getParameters()) {
            if (parameter.getName().equals(invokerName))
                return false;
        }

        return true;

    }

    private static String prefixSeeker(String variableName) {

        if (symbolTable.getMethodsHashmap().get(currentMethod).getLocalVariables().containsKey(variableName))
            return "";

        final MethodSymbol method = symbolTable.getMethodsHashmap().get(currentMethod);

        for (int i = 0; i < symbolTable.getMethodsHashmap().get(currentMethod).getParameters().size(); i++) {
            Symbol iterator = symbolTable.getMethodsHashmap().get(currentMethod).getParameters().get(i);
            if (iterator.getName().equals(variableName))
                if (method.getName().equals("main"))
                    return "$" + i + ".";
                else {
                    final int value = i + 1;
                    return "$" + value + ".";
                }
        }

        return "";
    }

    private static String suffixSeeker(String variableName) {
        Symbol variable = null;

        if (symbolTable.getMethodsHashmap().get(currentMethod).getLocalVariables().containsKey(variableName))
            variable = symbolTable.getMethodsHashmap().get(currentMethod).getLocalVariables().get(variableName);

        for (int i = 0; i < symbolTable.getMethodsHashmap().get(currentMethod).getParameters().size(); i++) {
            if (symbolTable.getMethodsHashmap().get(currentMethod).getParameters().get(i).getName().equals(variableName)) {
                variable = symbolTable.getMethodsHashmap().get(currentMethod).getParameters().get(i);
                break;
            }
        }

        if (symbolTable.getClassFields().containsKey(variableName))
            variable = symbolTable.getClassFields().get(variableName);

        //TODO:Because of imported methods. Refactor this
        if (variable == null)
            return "";

        if (variable.getType().getName().equals("int"))
            if (!variable.getType().isArray())
                return ".i32";
            else
                return ".array.i32";
        else if (variable.getType().getName().equals("boolean"))
            return ".bool";
        else
            return "." + variable.getType().getName();

    }

    private static String dismember(JmmNode node) {

        return switch (node.getKind()) {
            case "Add" -> dismemberMath(node, "+");
            case "Sub" -> dismemberMath(node, "-");
            case "Mult" -> dismemberMath(node, "*");
            case "Div" -> dismemberMath(node, "/");
            case "And" -> dismemberLogic(node, "&&");
            case "Less" -> dismemberLogic(node, "<");
            case "Not" -> dismemberNot(node);
            case "Identifier" -> dismemberGetter(node);
            case "MethodCall" -> dealWithMethodCall(node);
            case "NewObject" -> dealWithNewObject(node);
            case "NewArray" -> dealWithNewArray(node);
            case "ArrayAccess" -> dealWithArrayAccess(node);
            default -> "";
        };
    }


    private static String dismemberMath(JmmNode node, String operator) {
        StringBuilder code = new StringBuilder();
        final int registerUsed = registersAvailable.remove(0);
        final JmmNode leftChild = node.getChildren().get(0);
        final JmmNode rightChild = node.getChildren().get(1);

        node.put("result", "t" + registerUsed);
        node.put("suffix", ".i32");

        code.append("t").append(registerUsed).append(".i32").append(" :=").append(".i32 ");

        code.append(leftChild.get("prefix")).append(leftChild.get("result")).append(leftChild.get("suffix"));
        code.append(" ").append(operator).append(".i32").append(" ");
        code.append(rightChild.get("prefix")).append(rightChild.get("result")).append(rightChild.get("suffix"));

        code.append(";");
        code.append("\n");

        return code.toString();
    }

    private static String dismemberLogic(JmmNode node, String operator) {
        StringBuilder code = new StringBuilder();
        final int registerUsed = registersAvailable.remove(0);
        final JmmNode leftChild = node.getChildren().get(0);
        final JmmNode rightChild = node.getChildren().get(1);

        node.put("result", "t" + registerUsed);
        node.put("suffix", ".bool");

        code.append("t").append(registerUsed).append(".bool").append(" :=").append(".bool ");


        code.append(leftChild.get("prefix")).append(leftChild.get("result")).append(leftChild.get("suffix"));
        code.append(" ").append(operator).append(".bool").append(" ");
        code.append(rightChild.get("prefix")).append(rightChild.get("result")).append(rightChild.get("suffix"));

        code.append(";");
        code.append("\n");
        return code.toString();
    }

    private static String dismemberNot(JmmNode node) {
        StringBuilder code = new StringBuilder();
        final int registerUsed = registersAvailable.remove(0);
        final JmmNode leftChild = node.getChildren().get(0);

        node.put("result", "t" + registerUsed);
        node.put("suffix", ".bool");

        code.append("t").append(registerUsed).append(".bool").append(" :=").append(".bool ");

        code.append(leftChild.get("prefix")).append(leftChild.get("result")).append(leftChild.get("suffix"));
        code.append(" ").append("!").append(".bool").append(" ");
        code.append(leftChild.get("prefix")).append(leftChild.get("result")).append(leftChild.get("suffix"));

        code.append(";");
        code.append("\n");
        return code.toString();
    }

    private static String dismemberGetter(JmmNode node) {
        StringBuilder code = new StringBuilder();

        final String variableName = node.get("value");
        final Symbol field = symbolTable.getClassFields().get(variableName);
        final String suffix = OptimizationStage.dealWithType(field.getType());

        final int registerUsed = registersAvailable.remove(0);

        node.put("result", "t" + registerUsed);
        node.put("suffix", suffix);

        code.append("t").append(registerUsed).append(suffix).append(" :=").append(suffix).append(" ").append("getfield(this, ").append(field.getName()).append(suffix).append(")").append(suffix).append(";");

        code.append("\n");
        return code.toString();
    }

    public static String dealWithMethodCall(JmmNode statement) {
        StringBuilder code = new StringBuilder();

        final JmmNode leftChild = statement.getChildren().get(0);
        final JmmNode rightChild = statement.getChildren().get(1);


        if (rightChild.getNumChildren() > 0) {
            for (JmmNode parameter : rightChild.getChildren().get(0).getChildren())
                code.append(SethiUllman.run(parameter));
        }
        code.append(SethiUllman.run(leftChild));

        if (rightChild.get("value").equals("length"))
            code.append(dealWithLengthCall(statement));
        else if (SethiUllman.isMethodCallStatic(statement))
            code.append(dealWithStaticMethodCall(statement));
        else
            code.append(dealWithNonStaticMethodCall(statement));

        return code.toString();
    }

    public static String dealWithLengthCall(JmmNode node) {
        StringBuilder code = new StringBuilder();

        final JmmNode leftChild = node.getChildren().get(0);

        final int registerUsed = registersAvailable.remove(0);

        node.put("result", "t" + registerUsed);
        node.put("suffix", ".i32");

        code.append("t").append(registerUsed).append(node.get("suffix")).append(" :=").append(node.get("suffix")).append(" ").append("arraylength(").append(leftChild.get("prefix")).append(leftChild.get("result")).append(leftChild.get("suffix")).append(")").append(node.get("suffix"));

        code.append(";");
        code.append("\n");
        return code.toString();
    }

    private static String dealWithStaticMethodCall(JmmNode node) {
        StringBuilder code = new StringBuilder();

        final JmmNode leftChild = node.getChildren().get(0);
        final JmmNode rightChild = node.getChildren().get(1);

        final int registerUsed = registersAvailable.remove(0);
        final String returnType = seekReturnTypeStaticCall(node);

        node.put("result", "t" + registerUsed);
        node.put("suffix", returnType);

        code.append("t").append(registerUsed).append(node.get("suffix")).append(" :=").append(node.get("suffix")).append(" ").append("invokestatic(").append(leftChild.get("result")).append(", \"").append(rightChild.get("value")).append("\"");

        if (rightChild.getNumChildren() > 0) {
            for (JmmNode parameter : rightChild.getChildren().get(0).getChildren())
                code.append(", ").append(parameter.get("prefix")).append(parameter.get("result")).append(parameter.get("suffix"));
        }

        code.append(")").append(returnType);
        code.append(";");
        code.append("\n");

        return code.toString();
    }

    public static String dealWithNonStaticMethodCall(JmmNode node) {
        StringBuilder code = new StringBuilder();

        final int registerUsed = registersAvailable.remove(0);
        node.put("result", "t" + registerUsed);

        final String methodCallResult = OptimizationStage.dealWithNonStaticMethodCall(node);

        code.append("t").append(registerUsed).append(node.get("suffix")).append(" :=").append(node.get("suffix")).append(" ").append(methodCallResult);

        return code.toString();
    }

    private static String dealWithNewObject(JmmNode node) {
        StringBuilder code = new StringBuilder();

        final int registerUsed = registersAvailable.remove(0);

        node.put("result", "t" + registerUsed);
        node.put("suffix", "." + node.get("value"));
        code.append(node.get("result")).append(node.get("suffix")).append(" ").append(":=").append(node.get("suffix")).append(" ").append("new(").append(node.get("value")).append(")").append(node.get("suffix")).append(";").append("\n");
        code.append("invokespecial(").append(node.get("result")).append(node.get("suffix")).append(", \"<init>\"").append(").V").append(";").append("\n");


        return code.toString();
    }

    private static String dealWithNewArray(JmmNode node) {
        StringBuilder code = new StringBuilder();

        //Deal With index expression
        code.append(SethiUllman.run(node.getChildren().get(0)));

        final int registerUsed = registersAvailable.remove(0);

        node.put("result", "t" + registerUsed);
        node.put("suffix", ".array.i32");

        code.append(node.get("result")).append(node.get("suffix")).append(" ").append(":=").append(node.get("suffix")).append(" ").append("new(array, ").append(node.getChildren().get(0).get("result")).append(node.getChildren().get(0).get("suffix")).append(")").append(node.get("suffix"));

        code.append(";");
        code.append("\n");

        return code.toString();
    }

    private static String dealWithArrayAccess(JmmNode node) {
        StringBuilder code = new StringBuilder();

        final JmmNode leftChild = node.getChildren().get(0);
        final JmmNode rightChild = node.getChildren().get(1);

        code.append(SethiUllman.run(leftChild));
        code.append(SethiUllman.run(rightChild));

        final int registerUsed = registersAvailable.remove(0);

        node.put("result", "t" + registerUsed);
        node.put("suffix", ".i32");

        code.append(node.get("result")).append(node.get("suffix")).append(" ").append(":=").append(node.get("suffix")).append(" ").append(leftChild.get("prefix")).append(leftChild.get("result")).append("[").append(rightChild.get("prefix")).append(rightChild.get("result")).append(rightChild.get("suffix")).append("]").append(node.get("suffix"));

        code.append(";");
        code.append("\n");

        return code.toString();
    }

    public static String seekReturnTypeStaticCall(JmmNode node) {
        String suffix = null;

        while (node.getParent() != null) {
            JmmNode parent = node.getParent();

            String kind = parent.getKind();
            if ("Add".equals(kind) || "Sub".equals(kind) || "Mult".equals(kind) || "Div".equals(kind)) {
                suffix = ".i32";
            } else if ("Less".equals(kind) || "And".equals(kind) || "Not".equals(kind)) {
                suffix = ".bool";
            } else if ("Assignment".equals(kind)) {
                suffix = parent.getChildren().get(0).get("suffix");
            } else if ("Parameters".equals(kind)) {
                suffix = seekReturnTypeParameterCase(node, parent);
                if (suffix != null)
                    break;
            }
            node = node.getParent();
        }

        if (suffix == null)
            suffix = ".V";

        return suffix;
    }

    private static String seekReturnTypeParameterCase(JmmNode node, JmmNode parametersNode) {
        final JmmNode methodCall = parametersNode.getParent().getParent();

        if (!methodCall.getChildren().get(0).getKind().equals("Identifier"))
            return null;

        MethodSymbol method = symbolTable.getMethodsHashmap().get(methodCall.getChildren().get(1).get("value"));

        if (method == null)
            return null;

        for (int i = 0; i < parametersNode.getNumChildren(); i++) {
            if (node.equals(parametersNode.getChildren().get(i)))
                return OptimizationStage.dealWithType(method.getParameters().get(i).getType());
        }

        return null;
    }

}