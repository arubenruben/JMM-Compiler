package visitors.ollir;

import pt.up.fe.comp.jmm.JmmNode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SethiUllman {
    private static List<Integer> registersAvailable;

    public static void initializeRegisters() {

        registersAvailable = new ArrayList<>();

        for (int i = 1; i <= 100; i++)
            registersAvailable.add(i);
    }

    public static String run(JmmNode node) {
        firstStep(node);
        writeToFile(node.toJson(), "results/ollir.txt");
        return secondStep(node);
    }

    private static void firstStep(JmmNode node) {

        if (registersAvailable == null)
            initializeRegisters();

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
            node.put("result", node.get("value"));
            return code.toString();
        }

        if (node.getKind().equals("MethodCall")) {
            code.append(dismemberMethodCall(node));
            return code.toString();
        }

        for (JmmNode child : node.getChildren())
            code.append(codeDismember(child));


        if (!node.getParent().getKind().equals("Assignment") && !node.getParent().getKind().equals("Condition"))
            code.append(dismemberHelper(node));

        return code.toString();
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
            node.put("result", node.get("value"));
            return "";
        }

        if (node.getKind().equals("MethodCall")) {
            code.append(dismemberMethodCall(node));
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

        code.append("t").append(registerUsed).append(".i32").append(":=");
        code.append(".i32 ").append("arraylength(").append(node.getChildren().get(0).get("result")).append(".array").append(".i32").append(")").append(".i32;");

        code.append("\n");

        return code.toString();
    }

    private static String dismemberMethodCallNonLength(JmmNode node) {
        StringBuilder code = new StringBuilder();
        int registerUsed = registersAvailable.remove(0);

        code.append(SethiUllman.run(node.getChildren().get(0)));

        node.put("result", "t" + registerUsed + ".i32");

        for (JmmNode parameter : node.getChildren().get(1).getChildren().get(0).getChildren()) {
            registersAvailable.remove(0);
            code.append(SethiUllman.run(parameter));
        }
        code.append("t").append(registerUsed).append(".i32").append(":=");
        code.append(".i32 ").append("invokestatic(").append(node.getChildren().get(0).get("result"));

        for (JmmNode parameter : node.getChildren().get(1).getChildren().get(0).getChildren())
            code.append(",").append(parameter.get("result"));

        code.append(").V;");
        code.append("\n");

        return code.toString();
    }

    private static String dismemberHelper(JmmNode node) {
        StringBuilder code = new StringBuilder();
        int registerUsed = registersAvailable.remove(0);

        switch (node.getKind()) {
            case "Less" -> {
                node.put("result", "t" + registerUsed + ".bool");
                code.append("t").append(registerUsed).append(".bool").append(":=");
                code.append(".bool ").append(node.getChildren().get(0).get("result")).append(" < ").append(node.getChildren().get(1).get("result"));
            }
            case "And" -> {
                node.put("result", "t" + registerUsed + ".bool");
                code.append("t").append(registerUsed).append(".bool").append(":=");
                code.append(".bool ").append(node.getChildren().get(0).get("result")).append(" && ").append(node.getChildren().get(1).get("result"));
            }
            case "Add" -> {
                node.put("result", "t" + registerUsed + ".i32");
                code.append("t").append(registerUsed).append(".i32").append(":=");
                code.append(".i32 ").append(node.getChildren().get(0).get("result")).append("+").append(node.getChildren().get(1).get("result")).append(".i32;");
            }
            case "Sub" -> {
                node.put("result", "t" + registerUsed + ".i32");
                code.append("t").append(registerUsed).append(".i32").append(":=");
                code.append(".i32 ").append(node.getChildren().get(0).get("result")).append("-").append(node.getChildren().get(1).get("result")).append(".i32;");
            }
            case "Mult" -> {
                node.put("result", "t" + registerUsed + ".i32");
                code.append("t").append(registerUsed).append(".i32").append(":=");
                code.append(".i32 ").append(node.getChildren().get(0).get("result")).append("*").append(node.getChildren().get(1).get("result")).append(".i32;");
            }
            case "Div" -> {
                node.put("result", "t" + registerUsed + ".i32");
                code.append("t").append(registerUsed).append(".i32").append(":=");
                code.append(".i32 ").append(node.getChildren().get(0).get("result")).append("/").append(node.getChildren().get(1).get("result")).append(".i32;");
            }
            case "ArrayAccess" -> {
                node.put("result", "t" + registerUsed + ".i32");
                code.append("t").append(registerUsed).append(".i32").append(":=");
                code.append(".i32 ").append(node.getChildren().get(0).get("result")).append("[").append(node.getChildren().get(1).get("result")).append("]").append(".i32;");
            }
            case "NewObject" -> {
                node.put("result", "t" + registerUsed + ".i32");
                code.append("t").append(registerUsed).append(".").append(node.get("value")).append(":=.");
                code.append(node.get("value")).append(" new(").append(node.get("value")).append(").").append(node.get("value")).append(";");
                code.append("\n");
                code.append("invokespecial(").append("t").append(registerUsed).append(".").append(node.get("value")).append(", \"<init>\" ).v").append(";");
            }
        }
        code.append("\n");
        return code.toString();
    }

    public static void writeToFile(String content, String path) {
        try {
            FileWriter myWriter = new FileWriter(path);
            myWriter.write(content);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred writing to file");
            e.printStackTrace();
        }
    }
}
