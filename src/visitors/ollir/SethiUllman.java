package visitors.ollir;

import pt.up.fe.comp.jmm.JmmNode;
import symbols.SymbolTableIml;

import java.util.ArrayList;
import java.util.List;

public class SethiUllman {

    private static SymbolTableIml symbolTable;
    public static List<Integer> registersAvailable;

    public static void initialize(SymbolTableIml symbolTable) {

        SethiUllman.symbolTable = symbolTable;

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
        }

        node.put("result", "");
        node.put("prefix", "");
        node.put("suffix", "");
    }

    private static String secondStep(JmmNode node) {
        return "";
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
}