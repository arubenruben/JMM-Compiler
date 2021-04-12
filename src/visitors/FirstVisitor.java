package visitors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;
import symbols.MethodSymbol;
import utils.ReportsUtils;
import visitors.helpers.MethodBodyVisitor;
import visitors.helpers.data_helpers.MethodBodyDataHelper;
import visitors.helpers.data_helpers.VisitorDataHelper;

import java.util.ArrayList;
import java.util.List;

public class FirstVisitor extends PreorderJmmVisitor<VisitorDataHelper, Boolean> {

    public FirstVisitor() {

        addVisit("ImportPath", this::dealWithImport);
        addVisit("Class", this::dealWithClassDefinition);
        addVisit("Extends", this::dealWithSuper);
        addVisit("VarDeclaration", this::dealWithClassFieldDeclaration);
        addVisit("ClassMethod", this::dealWithClassMethod);
        setDefaultVisit(this::defaultVisit);
    }

    protected Boolean dealWithImport(JmmNode node, VisitorDataHelper visitorDataHelper) {
        StringBuilder importPath = new StringBuilder();

        Gson gson = new Gson();
        List<String> importList = gson.fromJson(node.get("value"), new TypeToken<List<String>>() {
        }.getType());

        for (int i = 0; i < importList.size(); i++) {
            importPath.append(importList.get(i));
            if (i < importList.size() - 1)
                importPath.append(".");
        }

        visitorDataHelper.getSymbolTableIml().getImports().add(importPath.toString());

        return true;
    }

    protected Boolean dealWithClassDefinition(JmmNode node, VisitorDataHelper visitorDataHelper) {
        visitorDataHelper.getSymbolTableIml().setClassName(node.get("value"));
        return true;
    }

    protected Boolean dealWithSuper(JmmNode node, VisitorDataHelper visitorDataHelper) {
        visitorDataHelper.getSymbolTableIml().setSuperName(node.get("value"));
        return true;
    }

    protected Boolean dealWithClassFieldDeclaration(JmmNode node, VisitorDataHelper visitorDataHelper) {

        if (!node.getParent().getKind().equals("Class"))
            return false;

        String variableName = node.get("value");
        String variableType = node.getChildren().get(0).get("value");

        //If not a primitive it must be imported
        if (!variableType.equals("int") && !variableType.equals("boolean") && !variableType.equals(visitorDataHelper.getSymbolTableIml().getClassName()) && !variableType.equals(visitorDataHelper.getSymbolTableIml().getSuper())) {
            if (!visitorDataHelper.getSymbolTableIml().getImportedClasses().contains(variableType)) {
                visitorDataHelper.getReportList().add(ReportsUtils.reportEntryError(Stage.SEMANTIC, "This object type don't exist. Try to import it.", Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col"))));
                return true;
            }
        }

        Type nodeType = new Type(
                variableType,
                node.getChildren().get(0).get("isArray").equals("true")
        );
        Symbol fieldSymbol = new Symbol(nodeType, variableName);

        visitorDataHelper.getSymbolTableIml().getHashMapClassFields().put(fieldSymbol, "");

        return true;


    }

    protected Boolean dealWithClassMethod(JmmNode node, VisitorDataHelper visitorDataHelper) {
        List<Symbol> parameters = new ArrayList<>();

        JmmNode parameterBlock = node.getChildren().get(1);
        JmmNode bodyBlock = node.getChildren().get(2);

        MethodSymbol methodSymbol = new MethodSymbol(
                new Type(node.getChildren().get(0).get("value"), node.getChildren().get(0).get("isArray").equals("true")),
                node.get("value"));

        for (JmmNode parameter : parameterBlock.getChildren()) {
            JmmNode nodeType = parameter.getChildren().get(0);
            JmmNode nodeIdentifier = parameter.getChildren().get(1);
            Type type = new Type(nodeType.get("value"), nodeType.get("isArray").equals("true"));
            parameters.add(new Symbol(type, nodeIdentifier.get("value")));
        }

        methodSymbol.setParameters(parameters);

        visitorDataHelper.getSymbolTableIml().getMethodsHashmap().put(methodSymbol.getName(), methodSymbol);

        visitorDataHelper.getSymbolTableIml().getNodeMap().put(methodSymbol.getName(), node);

        MethodBodyVisitor methodBodyVisitor = new MethodBodyVisitor();

        methodBodyVisitor.visit(bodyBlock, new MethodBodyDataHelper(methodSymbol, visitorDataHelper.getSymbolTableIml(), visitorDataHelper.getReportList()));

        return true;
    }

    protected Boolean defaultVisit(JmmNode node, VisitorDataHelper visitorDataHelper) {
        return true;
    }

}
