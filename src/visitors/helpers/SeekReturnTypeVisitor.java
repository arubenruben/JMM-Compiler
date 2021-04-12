package visitors.helpers;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import symbols.MethodSymbol;
import visitors.helpers.data_helpers.SecondVisitorHelper;

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

            MethodSymbol method = secondVisitorHelper.getSymbolTableIml().getMethodsHashmap().get(node.getChildren().get(1).get("value"));

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

        type = new Type("this", false);

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

    protected Type defaultVisit(JmmNode node, SecondVisitorHelper secondVisitorHelper) {

        return type;
    }

    public Type getType() {
        return type;
    }

}
