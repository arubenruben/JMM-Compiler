package visitors.ollir;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import symbols.SymbolTableIml;

import java.util.ArrayList;
import java.util.List;

public class SethiUllman {

    private static List<Integer> registersAvailable;
    private static SymbolTableIml symbolTable;
    private static String currentMethod;

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
            return;
        }

        for (JmmNode child : node.getChildren())
            firstStep(child);

        fillNonTerminalValue(node);
    }

    private static String secondStep(JmmNode node) {
        StringBuilder code = new StringBuilder();

        if (!canDismember(node)) {
            fillNonDismember(node);
            return code.toString();
        }

        if (node.getKind().equals("MethodCall")) {
            code.append(dismemberMethodCall(node));
            return code.toString();
        }

        if (node.getKind().equals("NewArray")) {
            code.append(dismemberNewArray(node));
            return code.toString();
        }

        for (JmmNode child : node.getChildren())
            code.append(codeDismember(child));


        if (!node.getParent().getKind().equals("Assignment") && !node.getParent().getKind().equals("Condition"))
            code.append(dismemberHelper(node));
        else
            code.append(rootNodeNotDismember(node));

        return code.toString();
    }

    private static void fillNonDismember(JmmNode node) {
        String typePrefix = "";
        String typeSuffix = "";

        switch (node.getKind()) {
            case "Integer" -> typeSuffix = ".i32";
            case "Boolean" -> typeSuffix = ".bool";
            case "Identifier" -> {

                Symbol variable = null;

                if (symbolTable.getMethodsHashmap().get(currentMethod).getLocalVariables().containsKey(node.get("value")))
                    variable = symbolTable.getMethodsHashmap().get(currentMethod).getLocalVariables().get(node.get("value"));

                if (variable == null) {
                    for (int i = 0; i < symbolTable.getMethodsHashmap().get(currentMethod).getParameters().size(); i++) {
                        variable = symbolTable.getMethodsHashmap().get(currentMethod).getParameters().get(i);
                        if (variable.getName().equals(node.get("value"))) {
                            typePrefix = "$" + i + ".";
                            break;
                        }
                    }
                }

                if (variable == null) {
                    variable = symbolTable.getClassFields().get(node.get("value"));
                }

                if (variable == null)
                    return;

                if (variable.getType().getName().equals("int"))
                    if (!variable.getType().isArray())
                        typeSuffix = ".i32";
                    else
                        typeSuffix = ".array.i32";
                else if (variable.getType().getName().equals("boolean"))
                    typeSuffix = ".bool";
                else
                    typeSuffix = "." + variable.getType().getName();

            }
        }

        node.put("typePrefix", typePrefix);
        node.put("typeSuffix", typeSuffix);

        node.put("result", node.get("value"));
    }

    private static String rootNodeNotDismember(JmmNode node) {
        if ("ArrayAccess".equals(node.getKind())) {
            StringBuilder code = new StringBuilder();
            code.append(node.getChildren().get(0).get("result")).append("[");

            if (node.getChildren().get(1).getAttributes().contains("typePrefix"))
                code.append(node.getChildren().get(1).get("typePrefix"));

            code.append(node.getChildren().get(1).get("result"));

            if (node.getChildren().get(1).getAttributes().contains("typeSuffix"))
                code.append(node.getChildren().get(1).get("typeSuffix"));

            code.append("]");

            node.put("typeSuffix", ".i32");
            node.put("result", code.toString());
        }
        return "";
    }

    private static boolean canDismember(JmmNode node) {
        return switch (node.getKind()) {
            case "Identifier", "This", "Boolean", "Integer" -> false;
            default -> true;
        };
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

            default -> System.err.println("Not implemented yet");
        }
    }

    private static void fillNonTerminalValue(JmmNode node) {

        int leftChildValue = Integer.parseInt(node.getChildren().get(0).get("registers"));

        //For root
        if (node.getNumChildren() == 1) {
            node.put("registers", String.valueOf(leftChildValue));
            return;
        }

        int rightChildValue = Integer.parseInt(node.getChildren().get(1).get("registers"));

        if (leftChildValue == rightChildValue)
            node.put("registers", String.valueOf(leftChildValue + 1));
        else
            node.put("registers", String.valueOf(Math.max(leftChildValue, rightChildValue)));

    }

    ///Step 1 helpers - Step 2 helpers
    ///----------------------------------------///
    private static String codeDismember(JmmNode node) {
        StringBuilder code = new StringBuilder();

        if (isTerminal(node) && node.getAttributes().contains("value")) {
            fillNonDismember(node);
            return "";
        }

        if (node.getKind().equals("MethodCall")) {
            code.append(dismemberMethodCall(node));
            return code.toString();
        }

        if (node.getKind().equals("NewArray")) {
            code.append(dismemberNewArray(node));
            return code.toString();
        }

        if (node.getNumChildren() == 1) {
            code.append(codeDismember(node.getChildren().get(0)));
        } else if (node.getNumChildren() == 2) {
            if (Integer.parseInt(node.getChildren().get(0).get("registers")) >= Integer.parseInt(node.getChildren().get(1).get("registers"))) {
                code.append(codeDismember(node.getChildren().get(0)));
                code.append(codeDismember(node.getChildren().get(1)));
            } else {
                code.append(codeDismember(node.getChildren().get(1)));
                code.append(codeDismember(node.getChildren().get(0)));
            }
        }

        if (Integer.parseInt(node.get("registers")) >= 1)
            code.append(dismemberHelper(node));

        return code.toString();
    }

    private static String dismemberMethodCall(JmmNode node) {
        StringBuilder code = new StringBuilder();

        if (node.getChildren().get(1).get("value").equals("length"))
            code.append(dismemberLength(node));
        else
            code.append(dismemberMethodCallNonLength(node));

        return code.toString();
    }

    private static String dismemberLength(JmmNode node) {
        StringBuilder code = new StringBuilder();
        int registerUsed = registersAvailable.remove(0);
        node.put("result", "t" + registerUsed + ".i32");

        SethiUllman.run(node.getChildren().get(0));

        code.append("t").append(registerUsed).append(".i32").append(" :=");
        code.append(".i32 ").append("arraylength(");

        if (node.getChildren().get(0).getAttributes().contains("typePrefix"))
            code.append(node.getChildren().get(0).get("typePrefix"));

        code.append(node.getChildren().get(0).get("result"));

        if (node.getChildren().get(0).getAttributes().contains("typeSuffix"))
            code.append(node.getChildren().get(0).get("typeSuffix"));

        code.append(")").append(".i32;");

        code.append("\n");

        return code.toString();
    }

    private static String dismemberMethodCallNonLength(JmmNode node) {
        StringBuilder code = new StringBuilder();
        int registerUsed = registersAvailable.remove(0);

        code.append(SethiUllman.run(node.getChildren().get(0)));

        node.put("result", "t" + registerUsed);
        node.put("typePrefix", ".i32");

        for (JmmNode parameter : node.getChildren().get(1).getChildren().get(0).getChildren()) {
            registersAvailable.remove(0);
            code.append(SethiUllman.run(parameter));
        }
        code.append("t").append(registerUsed).append(".i32").append(" :=");
        code.append(".i32 ").append("invokestatic(").append(node.getChildren().get(0).get("result"));

        for (JmmNode parameter : node.getChildren().get(1).getChildren().get(0).getChildren())
            code.append(",").append(parameter.get("result"));

        code.append(").V;");
        code.append("\n");

        return code.toString();
    }

    private static String dismemberNewArray(JmmNode node) {
        StringBuilder code = new StringBuilder();

        code.append(SethiUllman.run(node.getChildren().get(0)));

        node.put("result", "new(array," + node.getChildren().get(0).get("result") + ")");
        node.put("typeSuffix", ".array.i32");

        return code.toString();
    }

    private static String dismemberHelper(JmmNode node) {
        StringBuilder code = new StringBuilder();
        int registerUsed = registersAvailable.remove(0);

        switch (node.getKind()) {
            case "Less" -> {
                node.put("result", "t" + registerUsed);
                node.put("typePrefix", ".bool");
                code.append("t").append(registerUsed).append(".bool").append(" :=").append(".bool ");
                code.append(node.getChildren().get(0).get("result")).append(" <.i32").append(node.getChildren().get(1).get("result"));
            }
            case "And" -> {
                node.put("result", "t" + registerUsed);
                node.put("typePrefix", ".bool");
                code.append("t").append(registerUsed).append(".bool").append(" :=").append(".bool ");
                if (node.getChildren().get(0).getAttributes().contains("typePrefix"))
                    code.append(node.getChildren().get(0).get("typePrefix"));

                code.append(node.getChildren().get(0).get("result"));

                if (node.getChildren().get(0).getAttributes().contains("typeSuffix"))
                    code.append(node.getChildren().get(0).get("typeSuffix"));


                code.append("&&").append(".i32 ");

                if (node.getChildren().get(1).getAttributes().contains("typePrefix"))
                    code.append(node.getChildren().get(1).get("typePrefix"));

                code.append(node.getChildren().get(1).get("result"));

                if (node.getChildren().get(1).getAttributes().contains("typeSuffix"))
                    code.append(node.getChildren().get(1).get("typeSuffix"));
            }
            case "Add" -> {
                node.put("result", "t" + registerUsed);
                node.put("typePrefix", ".i32");
                code.append("t").append(registerUsed).append(".i32").append(" :=").append(".i32 ");

                if (node.getChildren().get(0).getAttributes().contains("typePrefix"))
                    code.append(node.getChildren().get(0).get("typePrefix"));

                code.append(node.getChildren().get(0).get("result"));

                if (node.getChildren().get(0).getAttributes().contains("typeSuffix"))
                    code.append(node.getChildren().get(0).get("typeSuffix"));


                code.append("+").append(".i32 ");

                if (node.getChildren().get(1).getAttributes().contains("typePrefix"))
                    code.append(node.getChildren().get(1).get("typePrefix"));

                code.append(node.getChildren().get(1).get("result"));

                if (node.getChildren().get(1).getAttributes().contains("typeSuffix"))
                    code.append(node.getChildren().get(1).get("typeSuffix"));
            }
            case "Sub" -> {
                node.put("result", "t" + registerUsed);
                node.put("typePrefix", ".i32");
                code.append("t").append(registerUsed).append(".i32").append(" :=").append(".i32 ");

                if (node.getChildren().get(0).getAttributes().contains("typePrefix"))
                    code.append(node.getChildren().get(0).get("typePrefix"));

                code.append(node.getChildren().get(0).get("result"));

                if (node.getChildren().get(0).getAttributes().contains("typeSuffix"))
                    code.append(node.getChildren().get(0).get("typeSuffix"));


                code.append("-").append(".i32 ");

                if (node.getChildren().get(1).getAttributes().contains("typePrefix"))
                    code.append(node.getChildren().get(1).get("typePrefix"));

                code.append(node.getChildren().get(1).get("result"));

                if (node.getChildren().get(1).getAttributes().contains("typeSuffix"))
                    code.append(node.getChildren().get(1).get("typeSuffix"));
            }
            case "Mult" -> {
                node.put("result", "t" + registerUsed);
                node.put("typePrefix", ".i32");
                code.append("t").append(registerUsed).append(".i32").append(" :=").append(".i32 ");

                if (node.getChildren().get(0).getAttributes().contains("typePrefix"))
                    code.append(node.getChildren().get(0).get("typePrefix"));

                code.append(node.getChildren().get(0).get("result"));

                if (node.getChildren().get(0).getAttributes().contains("typeSuffix"))
                    code.append(node.getChildren().get(0).get("typeSuffix"));

                code.append("*").append(".i32 ");

                if (node.getChildren().get(1).getAttributes().contains("typePrefix"))
                    code.append(node.getChildren().get(1).get("typePrefix"));

                code.append(node.getChildren().get(1).get("result"));

                if (node.getChildren().get(1).getAttributes().contains("typeSuffix"))
                    code.append(node.getChildren().get(1).get("typeSuffix"));

            }
            case "Div" -> {
                node.put("result", "t" + registerUsed);
                node.put("typePrefix", ".i32");
                code.append("t").append(registerUsed).append(".i32").append(" :=").append(".i32 ");
                if (node.getChildren().get(0).getAttributes().contains("typePrefix"))
                    code.append(node.getChildren().get(0).get("typePrefix"));

                code.append(node.getChildren().get(0).get("result"));

                if (node.getChildren().get(0).getAttributes().contains("typeSuffix"))
                    code.append(node.getChildren().get(0).get("typeSuffix"));


                code.append("/").append(".i32 ");

                if (node.getChildren().get(1).getAttributes().contains("typePrefix"))
                    code.append(node.getChildren().get(1).get("typePrefix"));

                code.append(node.getChildren().get(1).get("result"));

                if (node.getChildren().get(1).getAttributes().contains("typeSuffix"))
                    code.append(node.getChildren().get(1).get("typeSuffix"));
            }
            case "ArrayAccess" -> {
                node.put("result", "t" + registerUsed);
                node.put("typeSuffix", ".i32");
                code.append("t").append(registerUsed).append(".i32").append(" :=").append(".i32 ");
                code.append(node.getChildren().get(0).get("result")).append("[");

                if (node.getChildren().get(1).getAttributes().contains("typePrefix"))
                    code.append(node.getChildren().get(1).get("typePrefix"));

                code.append(node.getChildren().get(1).get("result"));

                if (node.getChildren().get(1).getAttributes().contains("typeSuffix"))
                    code.append(node.getChildren().get(1).get("typeSuffix"));

                code.append("]").append(".i32;");
            }
            case "NewObject" -> {
                node.put("result", "t" + registerUsed);
                node.put("typeSuffix", node.get("value"));
                code.append("t").append(registerUsed).append(".").append(node.get("value")).append(" :=.");
                code.append(node.get("value")).append(" new(").append(node.get("value")).append(").").append(node.get("value")).append(";");
                code.append("\n");
                code.append("invokespecial(").append("t").append(registerUsed).append(".").append(node.get("value")).append(",\"<init>\").V").append(";");
            }
        }
        code.append("\n");
        return code.toString();
    }
}
