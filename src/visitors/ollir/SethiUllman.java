package visitors.ollir;

import pt.up.fe.comp.jmm.JmmNode;

import java.util.ArrayList;
import java.util.List;

public class SethiUllman {
    private static List<Integer> registers;

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

    private static boolean isTerminal(JmmNode node) {
        if (node.getNumChildren() == 0)
            return true;

        if (node.getKind().equals("ArrayAccess") || node.getKind().equals("NewArray") || node.getKind().equals("NewObject"))
            return true;

        return false;
    }

    private static void fillTerminalValue(JmmNode node) {
        if (node.getKind().equals("Identifier"))
            node.put("registers", "0");

        if (node.getKind().equals("Integer"))
            node.put("registers", "0");

        if (node.getKind().equals("Boolean"))
            node.put("registers", "0");

        if (node.getKind().equals("This"))
            node.put("registers", "0");

        if (node.getKind().equals("ArrayAccess")) {
            node.put("registers", "1");
            node.getChildren().get(0).put("registers", "0");
            node.getChildren().get(1).put("registers", "0");
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

    public static void secondStep(JmmNode node, StringBuilder stringBuilder) {

        int numberRegistersRequired = Integer.parseInt(node.get("registers"));

        registers = new ArrayList<>();

        for (int i = 1; i <= 100; i++)
            registers.add(i);

        if (numberRegistersRequired > 0) {
            codeDismember(node.getChildren().get(0), stringBuilder);
            codeDismember(node.getChildren().get(1), stringBuilder);
        }

    }

    private static void codeDismember(JmmNode node, StringBuilder stringBuilder) {

        if (node.getNumChildren() == 0) {
            node.put("result", node.get("value"));
            return;
        }

        if (Integer.parseInt(node.getChildren().get(0).get("registers")) >= Integer.parseInt(node.getChildren().get(1).get("registers"))) {
            codeDismember(node.getChildren().get(0), stringBuilder);
            codeDismember(node.getChildren().get(1), stringBuilder);
        } else {
            codeDismember(node.getChildren().get(1), stringBuilder);
            codeDismember(node.getChildren().get(0), stringBuilder);
        }

        if (Integer.parseInt(node.get("registers")) == 1) {
            int register = registers.remove(0);
            dismemberHelper(node, register, stringBuilder);
        }
    }

    private static void dismemberHelper(JmmNode node, int registerUsed, StringBuilder stringBuilder) {

        if (node.getKind().equals("Less")) {
            stringBuilder.append("\t\t");
            stringBuilder.append("t");
            stringBuilder.append(registerUsed);
            stringBuilder.append(".bool");
            stringBuilder.append(" :=");
            stringBuilder.append(".bool ");
            stringBuilder.append(node.getChildren().get(0).get("result"));
            stringBuilder.append(" < ");
            stringBuilder.append(node.getChildren().get(1).get("result"));

            node.put("result", "t" + registerUsed + ".bool");
        } else if (node.getKind().equals("ArrayAccess")) {
            stringBuilder.append("\t\t");
            stringBuilder.append("t");
            stringBuilder.append(registerUsed);
            stringBuilder.append(".i32");
            stringBuilder.append(" :=");
            stringBuilder.append(".i32 ");
            stringBuilder.append(node.getChildren().get(0).get("result"));
            stringBuilder.append("[");
            stringBuilder.append(node.getChildren().get(1).get("result"));
            stringBuilder.append("]");
            stringBuilder.append(".i32;");
            node.put("result", "t" + registerUsed + ".i32");
        } else if (node.getKind().equals("Add")) {
            stringBuilder.append("\t");
            stringBuilder.append("t");
            stringBuilder.append(registerUsed);
            stringBuilder.append(".i32");
            stringBuilder.append(" :=");
            stringBuilder.append(".i32 ");
            stringBuilder.append(node.getChildren().get(0).get("result"));
            stringBuilder.append("+");
            stringBuilder.append(node.getChildren().get(1).get("result"));
            stringBuilder.append(".i32;");
            node.put("result", "t" + registerUsed + ".i32");
        } else if (node.getKind().equals("Mul")) {
            stringBuilder.append("\t");
            stringBuilder.append("t");
            stringBuilder.append(registerUsed);
            stringBuilder.append(".i32");
            stringBuilder.append(" :=");
            stringBuilder.append(".i32 ");
            stringBuilder.append(node.getChildren().get(0).get("result"));
            stringBuilder.append("*");
            stringBuilder.append(node.getChildren().get(1).get("result"));
            stringBuilder.append(".i32;");
            node.put("result", "t" + registerUsed + ".i32");
        } else if (node.getKind().equals("Div")) {
            stringBuilder.append("\t");
            stringBuilder.append("t");
            stringBuilder.append(registerUsed);
            stringBuilder.append(".i32");
            stringBuilder.append(" :=");
            stringBuilder.append(".i32 ");
            stringBuilder.append(node.getChildren().get(0).get("result"));
            stringBuilder.append("/");
            stringBuilder.append(node.getChildren().get(1).get("result"));
            stringBuilder.append(".i32;");
            node.put("result", "t" + registerUsed + ".i32");
        }


        stringBuilder.append("\n");

    }
}
