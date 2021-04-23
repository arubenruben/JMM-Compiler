package visitors.ollir;

import pt.up.fe.comp.jmm.JmmNode;

import java.util.ArrayList;
import java.util.List;

public class SethiUllman {
    private static List<Integer> registersAvailable;

    public static void firstStep(JmmNode node) {
        //Terminal
        if (isTerminal(node)) {
            fillTerminalValue(node);
            return;
        }

        for (JmmNode child : node.getChildren())
            firstStep(child);

        fillNonTerminalValue(node);
    }

    public static String secondStep(JmmNode node) {
        StringBuilder code = new StringBuilder();

        registersAvailable = new ArrayList<>();

        for (int i = 1; i <= 100; i++)
            registersAvailable.add(i);

        for (JmmNode child : node.getChildren())
            code.append(codeDismember(child));

        if (node.getNumChildren() == 0) {
            node.put("result", node.get("value"));
            return code.toString();
        }

        if (!node.getAttributes().contains("result"))
            node.put("result", node.getChildren().get(0).get("result"));


        return code.toString();
    }

    private static boolean isTerminal(JmmNode node) {
        if (node.getNumChildren() == 0)
            return true;

        //if (node.getKind().equals("NewArray") || node.getKind().equals("NewObject"))
        //  return true;

        return false;
    }

    private static void fillTerminalValue(JmmNode node) {

        switch (node.getKind()) {
            case "Identifier", "Boolean", "Integer", "This" -> node.put("registers", "0");

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

        return;

    }

    ///Step 1 helpers - Step 2 helpers
    ///----------------------------------------///
    private static String codeDismember(JmmNode node) {
        StringBuilder code = new StringBuilder();

        if (node.getNumChildren() == 0) {
            node.put("result", node.get("value"));
            return code.toString();
        }

        if (Integer.parseInt(node.getChildren().get(0).get("registers")) >= Integer.parseInt(node.getChildren().get(1).get("registers"))) {
            code.append(codeDismember(node.getChildren().get(0)));
            code.append(codeDismember(node.getChildren().get(1)));
        } else {
            code.append(codeDismember(node.getChildren().get(1)));
            code.append(codeDismember(node.getChildren().get(0)));
        }
        //Is never the root. Only childs could be dismembered
        if (Integer.parseInt(node.get("registers")) >= 1)
            code.append(dismemberHelper(node, registersAvailable.remove(0)));

        return code.toString();
    }

    private static String dismemberHelper(JmmNode node, int registerUsed) {
        StringBuilder code = new StringBuilder();

        switch (node.getKind()) {
            case "Less" -> {
                node.put("result", "t" + registerUsed + ".bool");
                code.append("t");
                code.append(registerUsed);
                code.append(".bool");
                code.append(" :=");
                code.append(".bool ");
                code.append(node.getChildren().get(0).get("result"));
                code.append(" < ");
                code.append(node.getChildren().get(1).get("result"));
                code.append("\n");
            }
            case "And" -> {
                node.put("result", "t" + registerUsed + ".bool");
                code.append("t");
                code.append(registerUsed);
                code.append(".bool");
                code.append(" :=");
                code.append(".bool ");
                code.append(node.getChildren().get(0).get("result"));
                code.append(" && ");
                code.append(node.getChildren().get(1).get("result"));
                code.append("\n");
            }
            case "Add" -> {
                node.put("result", "t" + registerUsed + ".i32");
                code.append("t");
                code.append(registerUsed);
                code.append(".i32");
                code.append(" :=");
                code.append(".i32 ");
                code.append(node.getChildren().get(0).get("result"));
                code.append("+");
                code.append(node.getChildren().get(1).get("result"));
                code.append(".i32;");
                code.append("\n");
            }
            case "Sub" -> {
                node.put("result", "t" + registerUsed + ".i32");
                code.append("t");
                code.append(registerUsed);
                code.append(".i32");
                code.append(" :=");
                code.append(".i32 ");
                code.append(node.getChildren().get(0).get("result"));
                code.append("-");
                code.append(node.getChildren().get(1).get("result"));
                code.append(".i32;");
                code.append("\n");
            }
            case "Mult" -> {
                node.put("result", "t" + registerUsed + ".i32");
                code.append("t");
                code.append(registerUsed);
                code.append(".i32");
                code.append(" :=");
                code.append(".i32 ");
                code.append(node.getChildren().get(0).get("result"));
                code.append("*");
                code.append(node.getChildren().get(1).get("result"));
                code.append(".i32;");
                code.append("\n");
            }
            case "Div" -> {
                node.put("result", "t" + registerUsed + ".i32");
                code.append("t");
                code.append(registerUsed);
                code.append(".i32");
                code.append(" :=");
                code.append(".i32 ");
                code.append(node.getChildren().get(0).get("result"));
                code.append("/");
                code.append(node.getChildren().get(1).get("result"));
                code.append(".i32;");
                code.append("\n");
            }
            case "ArrayAccess" -> {
                node.put("result", "t" + registerUsed + ".i32");
                code.append("t");
                code.append(registerUsed);
                code.append(".i32");
                code.append(" :=");
                code.append(".i32 ");
                code.append(node.getChildren().get(0).get("result"));
                code.append("[");
                code.append(node.getChildren().get(1).get("result"));
                code.append("]");
                code.append(".i32;");
                code.append("\n");
            }
            case "MethodCall" -> {
                node.put("result", "t" + registerUsed + ".i32");
                if (node.getChildren().get(1).get("value").equals("length")) {
                    code.append("t");
                    code.append(registerUsed);
                    code.append(".i32");
                    code.append(" :=");
                    code.append(".i32 ");
                    code.append("arraylength(");
                    code.append(node.getChildren().get(0).get("result"));
                    code.append(".array");
                    code.append(".i32");
                    code.append(")");
                    code.append(".i32;");
                    code.append("\n");
                }
            }
        }

        return code.toString();
    }

}
