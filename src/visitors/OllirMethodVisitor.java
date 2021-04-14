package visitors;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.ast.examples.ExamplePreorderVisitor;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.specs.util.utilities.StringLines;
import utils.ReportsUtils;
import visitors.helpers.OllirVisitorHelper;

import java.util.List;
import java.util.stream.Collectors;

public class OllirMethodVisitor extends PreorderJmmVisitor<String, String> {

    private SymbolTable symbolTable;

    public OllirMethodVisitor(SymbolTable symbolTable) {
        super(OllirMethodVisitor::reduce);

        this.symbolTable = symbolTable;

        addVisit("If", this::dealWithIf);
        addVisit("While", this::dealWithWhile);
        addVisit("Return", this::dealWithReturn);

        setDefaultVisit(this::defaultVisit);
    }

    private String dealWithIf(JmmNode node, String code) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\t\tif (");

        //Code Condition
        JmmNode conditionNode = node.getChildren().get(0).getChildren().get(0);
        OllirVisitorHelper ollirVisitorHelper = new OllirVisitorHelper(symbolTable);
        stringBuilder.append(ollirVisitorHelper.visit(conditionNode));

        stringBuilder.append(") goto else;\n");

        //Code inside the if

        stringBuilder.append("\t\t\tgoto endif;\n");
        stringBuilder.append("\t\telse:\n");

        //Code inside the else

        stringBuilder.append("\t\tendif:");

        return stringBuilder.toString();
    }

    private String dealWithWhile(JmmNode node, String code) {
        StringBuilder stringBuilder = new StringBuilder();
        return stringBuilder.toString();
    }

    private String dealWithReturn(JmmNode node, String code) {
        StringBuilder stringBuilder = new StringBuilder();
        return stringBuilder.toString();
    }

    private String defaultVisit(JmmNode node, String code) {
        return code;
    }

    private static String reduce(String nodeResult, List<String> childrenResults) {
        var content = new StringBuilder();

        if (!nodeResult.equals(""))
            content.append(nodeResult).append("\n");

        for (var childResult : childrenResults) {
            var childContent = StringLines.getLines(childResult).stream()
                    .map(line -> " " + line + "\n")
                    .collect(Collectors.joining());

            content.append(childContent);
        }
        return content.toString();
    }

}