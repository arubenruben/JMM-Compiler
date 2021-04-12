package visitors;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.ast.examples.ExamplePreorderVisitor;
import pt.up.fe.specs.util.utilities.StringLines;
import symbols.SymbolTableIml;
import visitors.helpers.ClassBodyVisitor;
import visitors.helpers.data_helpers.VisitorDataHelper;

import java.util.List;
import java.util.stream.Collectors;

public class OllirVisitor extends PreorderJmmVisitor<String, String> {

    private SymbolTable symbolTable;

    public OllirVisitor(SymbolTable symbolTable) {
        super(OllirVisitor::reduce);

        this.symbolTable = symbolTable;

        addVisit("Class", this::dealWithClass);
        setDefaultVisit(this::defaultVisit);
    }


    public String dealWithClass(JmmNode node, String code){
        code += node.get("value") + " {\n";

        code += "  .construct " + node.get("value") + "().V{\n" +
                "      invokespecial(this, \"<init>\").V;\n" +
                "  }\n";

        ClassBodyVisitor classBodyVisitor = new ClassBodyVisitor(symbolTable);
        code += classBodyVisitor.visit(node, "");

        return code + "}";
    }

    private String defaultVisit(JmmNode node, String code) {
        return code;
    }

    private static String reduce(String nodeResult, List<String> childrenResults) {
        var content = new StringBuilder();

        if(!nodeResult.equals(""))
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