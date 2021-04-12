package visitors.helpers;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.specs.util.utilities.StringLines;
import visitors.OllirVisitor;
import visitors.helpers.data_helpers.MethodBodyDataHelper;

import java.util.List;
import java.util.stream.Collectors;

public class ClassBodyVisitor extends PreorderJmmVisitor<String, String> {

    private SymbolTable symbolTable;

    public ClassBodyVisitor(SymbolTable symbolTable) {
       super(ClassBodyVisitor::reduce);
        addVisit("ClassMethod", this::dealWithClassMethod);
        setDefaultVisit(this::defaultVisit);
    }


    public String dealWithClassMethod(JmmNode node, String code){
        code += node.get("value") + "{\n";
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
