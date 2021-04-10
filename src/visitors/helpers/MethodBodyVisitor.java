package visitors.helpers;

import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;
import symbols.MethodSymbol;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import visitors.helpers.data_helpers.MethodBodyDataHelper;


public class MethodBodyVisitor extends PreorderJmmVisitor<MethodBodyDataHelper, Boolean> {

    public MethodBodyVisitor() {
        addVisit("VarDeclaration", this::dealWithField);
        setDefaultVisit(this::defaultVisit);
    }

    protected Boolean dealWithField(JmmNode node, MethodBodyDataHelper methodBodyDataHelper) {
        String variableType = node.getChildren().get(0).get("value");

        //If not a primitive it must be imported
        if (!variableType.equals("int") && !variableType.equals("boolean") && !variableType.equals(methodBodyDataHelper.getSymbolTableIml().getClassName()) && !variableType.equals(methodBodyDataHelper.getSymbolTableIml().getSuper())) {
            if (!methodBodyDataHelper.getSymbolTableIml().getImportedClasses().contains(variableType)) {
                methodBodyDataHelper.getReportList().add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), "This object type don't exist. Try to import it."));
                return true;
            }
        }

        methodBodyDataHelper.getMethodSymbol().getVariables().put(
                new Symbol(new Type(variableType, node.getChildren().get(0).get("isArray").equals("true")), node.get("value")), ""
        );

        return true;
    }


    protected Boolean defaultVisit(JmmNode node, MethodBodyDataHelper methodSymbol) {
        return true;
    }
}
