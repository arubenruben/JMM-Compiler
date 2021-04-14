package visitors;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.ast.examples.ExamplePreorderVisitor;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.specs.util.utilities.StringLines;
import utils.ReportsUtils;
import visitors.helpers.OllirVisitorHelper;
import visitors.helpers.data_helpers.VisitorOllirDataHelper;

import java.util.List;
import java.util.stream.Collectors;

public class OllirMethodVisitor extends PreorderJmmVisitor<VisitorOllirDataHelper, String> {

    private SymbolTable symbolTable;

    public OllirMethodVisitor(SymbolTable symbolTable) {
        super(OllirMethodVisitor::reduce);

        this.symbolTable = symbolTable;

        addVisit("If", this::dealWithIf);
        addVisit("While", this::dealWithWhile);
        addVisit("Return", this::dealWithReturn);

        setDefaultVisit(this::defaultVisit);
    }

    protected String dealWithIf(JmmNode node, VisitorOllirDataHelper visitorOllirDataHelper) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\t\tif (");

        //Code Condition
        JmmNode conditionNode = node.getChildren().get(0).getChildren().get(0);
        OllirVisitorHelper ollirVisitorHelperCondition = new OllirVisitorHelper(symbolTable);
        stringBuilder.append(ollirVisitorHelperCondition.visit(conditionNode));

        stringBuilder.append(") goto else;\n");

        //Code inside the if
        JmmNode thenNode = node.getChildren().get(1);

        for (JmmNode nodeInstruction : thenNode.getChildren()) {
            OllirVisitorHelper ollirVisitorHelperBody = new OllirVisitorHelper(symbolTable);
            stringBuilder.append("\t\t\t").append(ollirVisitorHelperBody.visit(nodeInstruction)).append("\n");
        }

        stringBuilder.append("\t\t\tgoto endif;\n");
        stringBuilder.append("\t\telse:\n");

        //Code inside the else
        JmmNode elseNode = node.getChildren().get(2);

        for (JmmNode nodeInstruction : elseNode.getChildren()) {
            OllirVisitorHelper ollirVisitorHelperBody = new OllirVisitorHelper(symbolTable);
            stringBuilder.append("\t\t\t").append(ollirVisitorHelperBody.visit(nodeInstruction)).append("\n");
        }

        stringBuilder.append("\t\tendif:");

        return stringBuilder.toString();
    }

    protected String dealWithWhile(JmmNode node, VisitorOllirDataHelper visitorOllirDataHelper) {
        StringBuilder stringBuilder = new StringBuilder();
        return stringBuilder.toString();
    }

    protected String dealWithReturn(JmmNode node, VisitorOllirDataHelper visitorOllirDataHelper) {
        StringBuilder stringBuilder = new StringBuilder();
        return stringBuilder.toString();
    }

    protected String defaultVisit(JmmNode node, VisitorOllirDataHelper visitorOllirDataHelper) {
        return visitorOllirDataHelper.getCode();
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