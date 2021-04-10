package visitors;

import symbols.MethodSymbol;
import visitors.helpers.SeekMethodParametersVisitor;
import visitors.helpers.SeekObjectCallerVisitor;
import visitors.helpers.SeekReturnTypeVisitor;
import visitors.helpers.data_helpers.SecondVisitorHelper;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

import java.util.List;

public class ThirdVisitor extends PreorderJmmVisitor<SecondVisitorHelper, Boolean> {

    public ThirdVisitor() {
        addVisit("Body", this::dealWithVariables);
        addVisit("Add", this::dealWithVariables);
        addVisit("Sub", this::dealWithVariables);
        addVisit("Mult", this::dealWithVariables);
        addVisit("Div", this::dealWithVariables);

        addVisit("ArrayAccess", this::dealWithArrayAccess);
        addVisit("MethodCall", this::dealWithMethodCall);
        addVisit("NewArray", this::dealWithNewArray);
        addVisit("NewObject", this::dealWithNewObject);

        /* TODO:Look At boolean operations
        addVisit("And", this::dealWithBooleanOperation);
        addVisit("Not", this::dealWithBooleanOperation);
         */
        setDefaultVisit(this::defaultVisit);
    }

    protected Boolean dealWithVariables(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        SeekObjectCallerVisitor seekObjectCallerVisitor = new SeekObjectCallerVisitor();
        seekObjectCallerVisitor.visit(node, secondVisitorHelper);

        return true;
    }

    protected Boolean dealWithArrayAccess(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        SeekObjectCallerVisitor seekObjectCallerVisitor = new SeekObjectCallerVisitor();
        seekObjectCallerVisitor.visit(node.getChildren().get(0), secondVisitorHelper);

        Symbol symbol = seekObjectCallerVisitor.getSymbol();

        if (symbol == null)
            secondVisitorHelper.getReportList().add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), "Non declared array"));


        return true;
    }

    protected Boolean dealWithMethodCall(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        String methodName = node.getChildren().get(1).get("value");

        SeekObjectCallerVisitor seekObjectCallerVisitor = new SeekObjectCallerVisitor();

        //Test if it is an Imported Class
        if (node.getChildren().get(0).getKind().equals("Identifier") && secondVisitorHelper.getSymbolTableIml().getImportedClasses().contains(node.getChildren().get(0).get("value")))
            return true;


        seekObjectCallerVisitor.visit(node.getChildren().get(0), secondVisitorHelper);

        Symbol symbol = seekObjectCallerVisitor.getSymbol();

        if (symbol == null) {
            secondVisitorHelper.getReportList().add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), "Non declared object"));
            return true;
        }
        if (symbol.equals(new Symbol(new Type("this", false), "this"))) {
            //A class that extends something is to assumes the method exists always
            if (secondVisitorHelper.getSymbolTableIml().getSuper() != null) {
                return true;
            }
        }
        //All objects contains length method in this grammar
        if (!symbol.getType().isArray() && methodName.equals("length")) {
            secondVisitorHelper.getReportList().add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), "Only arrays have length method"));
            return true;
        } else if (symbol.getType().isArray() && methodName.equals("length"))
            return true;

        if (!secondVisitorHelper.getSymbolTableIml().getMethodsHashmap().containsKey(methodName)) {
            secondVisitorHelper.getReportList().add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), "This object don't contains this method"));
            return true;
        }
        MethodSymbol method = secondVisitorHelper.getSymbolTableIml().getMethodsHashmap().get(methodName);

        SeekMethodParametersVisitor seekMethodParametersVisitor = new SeekMethodParametersVisitor();
        seekMethodParametersVisitor.visit(node, secondVisitorHelper);

        List<Type> listParameters = seekMethodParametersVisitor.getParameters();

        if (listParameters.size() != method.getParameters().size()) {
            secondVisitorHelper.getReportList().add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), "Method invocation with the wrong number of parameters"));
            return true;
        }

        for (int i = 0; i < listParameters.size(); i++) {
            if (listParameters.get(i) != method.getParameters().get(i).getType()) {
                secondVisitorHelper.getReportList().add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), "Type of the parameters don't match function arguments"));
            }
        }

        return true;
    }

    protected Boolean dealWithNewArray(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        SeekReturnTypeVisitor seekReturnTypeVisitor = new SeekReturnTypeVisitor();

        seekReturnTypeVisitor.visit(node.getChildren().get(0), secondVisitorHelper);
        Type type = seekReturnTypeVisitor.getType();

        if (type != null && !type.equals(new Type("int", false))) {
            secondVisitorHelper.getReportList().add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), "The initialization of an array requires the index parameter to be of type int"));
            return true;
        }

        return true;
    }

    private Boolean dealWithNewObject(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        if (node.get("value").equals(secondVisitorHelper.getSymbolTableIml().getClassName()))
            return true;

        if (node.get("value").equals(secondVisitorHelper.getSymbolTableIml().getSuper()))
            return true;

        if (secondVisitorHelper.getSymbolTableIml().getImportedClasses().contains(node.get("value")))
            return true;


        secondVisitorHelper.getReportList().add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), "The initialization of non unknown object"));

        return true;
    }

    protected Boolean defaultVisit(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        return true;
    }
}
