package visitors.semantic.helpers;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import symbols.MethodSymbol;
import visitors.semantic.helpers.data_helpers.SecondVisitorHelper;

import java.util.ArrayList;
import java.util.List;

public class SeekReturnTypeVisitor extends PreorderJmmVisitor<SecondVisitorHelper, Type> {
    private Type type;
    private boolean mustFail = false;

    public SeekReturnTypeVisitor() {
        addVisit("ArrayAccess", this::dealWithArrayAccess);
        addVisit("MethodCall", this::dealWithMethodCall);
        addVisit("Integer", this::dealWithInteger);
        addVisit("Boolean", this::dealWithBoolean);
        addVisit("This", this::dealWithThis);
        addVisit("Identifier", this::dealWithIdentifier);
        addVisit("NewArray", this::dealWithNewArray);
        addVisit("NewObject", this::dealWithNewObject);
        addVisit("Less", this::dealWithBooleanOperands);
        addVisit("Not", this::dealWithBooleanOperands);
        addVisit("And", this::dealWithBooleanOperands);
        setDefaultVisit(this::defaultVisit);
    }

    protected Type dealWithArrayAccess(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        if (type != null || mustFail)
            return type;

        type = new Type("int", false);

        return type;
    }


    protected Type dealWithMethodCall(JmmNode node, SecondVisitorHelper secondVisitorHelper) {

        if (type != null || mustFail)
            return type;

        if (node.getChildren().get(1).getKind().equals("Identifier")) {
            if (node.getChildren().get(1).get("value").equals("length")) {
                type = new Type("int", false);
                return type;
            }

            final List<Symbol> parameters = new ArrayList<>();
            final JmmNode method_called = node.getChildren().get(1);

            if (method_called.getChildren().size() > 0) {
                final JmmNode parameterBlock = method_called.getChildren().get(0);
                for (JmmNode parameter : parameterBlock.getChildren()) {
                    final SeekReturnTypeVisitor returnTypeVisitor = new SeekReturnTypeVisitor();
                    returnTypeVisitor.visit(parameter, secondVisitorHelper);

                    if (returnTypeVisitor.getType() == null)
                        return type;

                    if (parameter.getKind().equals("Identifier") && parameter.getKind().contains("value"))
                        parameters.add(new Symbol(returnTypeVisitor.getType(), parameter.get("value")));
                    else
                        parameters.add(new Symbol(returnTypeVisitor.getType(), ""));

                }
            }
            String methodName = secondVisitorHelper.nameGenerator(node.getChildren().get(1).get("value"), parameters);


            MethodSymbol method = secondVisitorHelper.getSymbolTableIml().getMethodsHashmap().get(methodName);

            if (method == null) {
                mustFail = true;
                return type;
            }


            type = method.getType();
        }

        return type;
    }

    protected Type dealWithInteger(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        if (type != null || mustFail)
            return type;

        type = new Type("int", false);

        return type;
    }

    protected Type dealWithBoolean(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        if (type != null || mustFail)
            return type;

        type = new Type("boolean", false);

        return type;
    }

    protected Type dealWithThis(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        if (type != null || mustFail)
            return type;

        type = new Type(secondVisitorHelper.getSymbolTableIml().getClassName(), false);

        return type;
    }

    protected Type dealWithIdentifier(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        if (type != null || mustFail)
            return type;

        Symbol symbol = secondVisitorHelper.getSymbolTableIml().lookup(node.get("value"), secondVisitorHelper.getCurrentMethodName());


        if (symbol == null) {
            mustFail = true;
            return type;
        }

        type = symbol.getType();

        return type;
    }

    protected Type dealWithNewArray(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        if (type != null || mustFail)
            return type;

        type = new Type(node.get("value"), node.get("isArray").equals("true"));

        return type;
    }

    private Type dealWithNewObject(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        if (type != null || mustFail)
            return type;

        if (node.get("value").equals(secondVisitorHelper.getSymbolTableIml().getClassName())) {
            type = new Type(node.get("value"), false);
            return type;
        }
        if (node.get("value").equals(secondVisitorHelper.getSymbolTableIml().getSuper())) {
            type = new Type(node.get("value"), false);
            return type;
        }
        if (secondVisitorHelper.getSymbolTableIml().getImportedClasses().contains(node.get("value"))) {
            type = new Type(node.get("value"), false);
            return type;
        }

        return type;
    }

    protected Type dealWithBooleanOperands(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        if (type != null || mustFail)
            return type;

        type = new Type("boolean", false);

        return type;
    }


    protected Type defaultVisit(JmmNode node, SecondVisitorHelper secondVisitorHelper) {

        return type;
    }

    public Type getType() {
        return type;
    }

}
