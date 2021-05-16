package visitors.semantic;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Stage;
import symbols.MethodSymbol;
import utils.ReportsUtils;
import visitors.semantic.helpers.SeekMethodParametersVisitor;
import visitors.semantic.helpers.SeekObjectCallerVisitor;
import visitors.semantic.helpers.SeekReturnTypeVisitor;
import visitors.semantic.helpers.data_helpers.SecondVisitorHelper;

import java.util.ArrayList;
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
            secondVisitorHelper.getReportList().add(ReportsUtils.reportEntryError(Stage.SEMANTIC, "Non declared array", Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col"))));


        return true;
    }

    protected Boolean dealWithMethodCall(JmmNode node, SecondVisitorHelper secondVisitorHelper) {

        final List<Symbol> parameters = new ArrayList<>();
        final JmmNode method_called = node.getChildren().get(1);

        if (method_called.getChildren().size() > 0) {
            final JmmNode parameterBlock = method_called.getChildren().get(0);
            for (JmmNode parameter : parameterBlock.getChildren()) {
                final SeekReturnTypeVisitor returnTypeVisitor = new SeekReturnTypeVisitor();
                returnTypeVisitor.visit(parameter, secondVisitorHelper);
                if (parameter.getKind().equals("Identifier") && parameter.getKind().contains("value"))
                    parameters.add(new Symbol(returnTypeVisitor.getType(), parameter.get("value")));
                else
                    parameters.add(new Symbol(returnTypeVisitor.getType(), ""));

            }
        }
        String methodName = secondVisitorHelper.nameGenerator(node.getChildren().get(1).get("value"), parameters);
        ///Here
        SeekObjectCallerVisitor seekObjectCallerVisitor = new SeekObjectCallerVisitor();

        seekObjectCallerVisitor.visit(node.getChildren().get(0), secondVisitorHelper);

        Symbol symbol = seekObjectCallerVisitor.getSymbol();

        if (symbol == null) {
            secondVisitorHelper.getReportList().add(ReportsUtils.reportEntryError(Stage.SEMANTIC, "Non declared object", Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col"))));
            return true;
        }
        //Imported Class
        if (secondVisitorHelper.getSymbolTableIml().getImportedClasses().contains(symbol.getType().getName()))
            return true;

        //A class that extends something is to assumes the method exists always
        if (secondVisitorHelper.getSymbolTableIml().getSuper() != null) {
            return true;
        }

        //All objects contains length method in this grammar
        if (!symbol.getType().isArray() && methodName.equals("length")) {
            secondVisitorHelper.getReportList().add(ReportsUtils.reportEntryError(Stage.SEMANTIC, "Only arrays have length method", Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col"))));
            return true;
        } else if (symbol.getType().isArray() && methodName.equals("length"))
            return true;

        if (!secondVisitorHelper.getSymbolTableIml().getMethodsHashmap().containsKey(methodName)) {
            System.out.println(secondVisitorHelper.getSymbolTableIml().toString());
            System.out.println(methodName);
            secondVisitorHelper.getReportList().add(ReportsUtils.reportEntryError(Stage.SEMANTIC, "This object don't contains this method", Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col"))));
            return true;
        }
        MethodSymbol method = secondVisitorHelper.getSymbolTableIml().getMethodsHashmap().get(methodName);

        SeekMethodParametersVisitor seekMethodParametersVisitor = new SeekMethodParametersVisitor();
        seekMethodParametersVisitor.visit(node, secondVisitorHelper);

        List<Type> listParameters = seekMethodParametersVisitor.getParameters();

        if (listParameters.size() != method.getParameters().size()) {
            secondVisitorHelper.getReportList().add(ReportsUtils.reportEntryError(Stage.SEMANTIC, "Method invocation with the wrong number of parameters", Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col"))));
            return true;
        }

        for (int i = 0; i < listParameters.size(); i++) {

            if (listParameters.get(i).getName().equals("static"))
                continue;

            if (!listParameters.get(i).equals(method.getParameters().get(i).getType())) {
                secondVisitorHelper.getReportList().add(ReportsUtils.reportEntryError(Stage.SEMANTIC, "Type of the parameters don't match function arguments", Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col"))));
                return true;
            }
        }

        return true;
    }


    protected Boolean dealWithNewArray(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        SeekReturnTypeVisitor seekReturnTypeVisitor = new SeekReturnTypeVisitor();

        seekReturnTypeVisitor.visit(node.getChildren().get(0), secondVisitorHelper);
        Type type = seekReturnTypeVisitor.getType();

        if (type != null && !type.equals(new Type("int", false))) {
            secondVisitorHelper.getReportList().add(ReportsUtils.reportEntryError(Stage.SEMANTIC, "The initialization of an array requires the index parameter to be of type int", Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col"))));
            return true;
        }

        return true;
    }

    protected Boolean dealWithNewObject(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        if (node.get("value").equals(secondVisitorHelper.getSymbolTableIml().getClassName()))
            return true;

        if (node.get("value").equals(secondVisitorHelper.getSymbolTableIml().getSuper()))
            return true;

        if (secondVisitorHelper.getSymbolTableIml().getImportedClasses().contains(node.get("value")))
            return true;


        secondVisitorHelper.getReportList().add(ReportsUtils.reportEntryError(Stage.SEMANTIC, "The initialization of unknown object", Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col"))));

        return true;
    }

    protected Boolean defaultVisit(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        return true;
    }
}
