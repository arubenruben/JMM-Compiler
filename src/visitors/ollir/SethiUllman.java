package visitors.ollir;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
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

        for (int i = 1; i <= 100; i++)
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

        int leftChildValue = Integer.parseInt(node.getChildren().get(0).get("registers"));
        int rightChildValue = Integer.parseInt(node.getChildren().get(1).get("registers"));

        if (leftChildValue == rightChildValue)
            node.put("registers", String.valueOf(leftChildValue + 1));
        else
            node.put("registers", String.valueOf(Math.max(leftChildValue, rightChildValue)));


    }

    private static String secondStep(JmmNode node) {
        StringBuilder code = new StringBuilder();

        if (node.getNumChildren() == 0) {
            fillTerminalNonDismember(node);
            return code.toString();
        }

        if (node.getNumChildren() == 1) {
            code.append(secondStep(node.getChildren().get(0)));
            return code.toString();
        }

        if (Integer.parseInt(node.getChildren().get(0).get("registers")) >= Integer.parseInt(node.getChildren().get(1).get("registers"))) {
            code.append(secondStep(node.getChildren().get(0)));
            code.append(secondStep(node.getChildren().get(1)));
        } else {
            code.append(secondStep(node.getChildren().get(1)));
            code.append(secondStep(node.getChildren().get(0)));
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
        }
    }

    private static boolean canDismember(JmmNode node) {

        if (node.getKind().equals("MethodCall"))
            return true;

        if (Integer.parseInt(node.get("registers")) == 1)
            return true;

        if (Integer.parseInt(node.get("registers")) == 0)
            return false;


        return false;
    }


    private static boolean isTerminal(JmmNode node) {
        return switch (node.getKind()) {
            case "Identifier", "This", "Boolean", "Integer", "NewObject", "MethodCall" -> true;
            default -> false;
        };
    }

    private static void fillTerminalValue(JmmNode node) {
        switch (node.getKind()) {
            case "Identifier", "Boolean", "Integer", "This", "MethodCall" -> node.put("registers", "0");
            case "NewObject" -> node.put("registers", "1");
        }
    }

    private static String prefixSeeker(String variableName) {
        StringBuilder code = new StringBuilder();

        if (symbolTable.getMethodsHashmap().get(currentMethod).getLocalVariables().containsKey(variableName))
            return "";

        for (int i = 0; i < symbolTable.getMethodsHashmap().get(currentMethod).getParameters().size(); i++) {
            Symbol iterator = symbolTable.getMethodsHashmap().get(currentMethod).getParameters().get(i);
            if (iterator.getName().equals(variableName))
                return "$" + i + ".";
        }

        return code.toString();
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
    
}