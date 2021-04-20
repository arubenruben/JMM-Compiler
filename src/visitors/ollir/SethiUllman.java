package visitors.ollir;

import pt.up.fe.comp.jmm.JmmNode;

import java.util.ArrayList;
import java.util.List;

public class SethiUllman {
    private static List<Integer> registers;

    public static int firstStep(JmmNode node) {
        treeFilling(node);
        return 0;
    }

    private static void treeFilling(JmmNode node) {

        //Terminal
        if (isTerminal(node)) {
            fillTerminalValue(node);
            return;
        }

        for (JmmNode child : node.getChildren())
            treeFilling(child);


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

        for (int i = 1; i <= numberRegistersRequired; i++)
            registers.add(i);


        if (numberRegistersRequired > 1) {
            codeDismember(node.getChildren().get(0), stringBuilder);
            codeDismember(node.getChildren().get(1), stringBuilder);
        }

    }

    private static void codeDismember(JmmNode node, StringBuilder stringBuilder) {

        if (Integer.parseInt(node.get("registers")) == 1) {
            int register = registers.remove(0);
            dismemberHelper(node, register, stringBuilder);
            return;
        }

        if (Integer.parseInt(node.getChildren().get(0).get("registers")) >= Integer.parseInt(node.getChildren().get(1).get("registers"))) {
            codeDismember(node.getChildren().get(0), stringBuilder);
            codeDismember(node.getChildren().get(1), stringBuilder);
        } else {
            codeDismember(node.getChildren().get(1), stringBuilder);
            codeDismember(node.getChildren().get(0), stringBuilder);
        }
    }

    private static void dismemberHelper(JmmNode node, int registerUsed, StringBuilder stringBuilder) {

        if (node.getKind().equals("Less")) {
            stringBuilder
                    .append("\t\t")
                    .append("t")
                    .append(registerUsed)
                    .append(".bool")
                    .append(" :=")
                    .append(".bool ")
                    .append(node.getChildren().get(0).get("value"))
                    .append(" .i32")
                    .append(" < ")
                    .append(node.getChildren().get(1).get("value"))
                    .append(" .i32;");

            node.put("result", "t" + registerUsed + ".bool");
        }
        stringBuilder.append("\n");

    }
}
