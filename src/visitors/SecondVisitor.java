package visitors;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;
import utils.ReportsUtils;
import visitors.helpers.SeekReturnTypeVisitor;
import visitors.helpers.data_helpers.SecondVisitorHelper;


public class SecondVisitor extends PreorderJmmVisitor<SecondVisitorHelper, Boolean> {

    public SecondVisitor() {
        addVisit("Add", this::dealWithMathOperation);
        addVisit("Sub", this::dealWithMathOperation);
        addVisit("Mult", this::dealWithMathOperation);
        addVisit("Div", this::dealWithMathOperation);
        addVisit("ArrayAccess", this::dealWithArrayAccess);
        addVisit("Assignment", this::dealWithAssignment);

        addVisit("Not", this::dealWithNot);
        addVisit("And", this::dealWithAnd);
        addVisit("Less", this::dealWithLess);

        addVisit("If", this::dealWithConditionOperation);
        addVisit("While", this::dealWithConditionOperation);

        setDefaultVisit(this::defaultVisit);
    }


    protected Boolean dealWithMathOperation(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        SeekReturnTypeVisitor seekReturnTypeVisitorLeft = new SeekReturnTypeVisitor();
        SeekReturnTypeVisitor seekReturnTypeVisitorRight = new SeekReturnTypeVisitor();

        seekReturnTypeVisitorLeft.visit(node.getChildren().get(0), secondVisitorHelper);
        Type typeLeft = seekReturnTypeVisitorLeft.getType();

        if (typeLeft == null)
            return true;

        seekReturnTypeVisitorRight.visit(node.getChildren().get(1), secondVisitorHelper);
        Type typeRight = seekReturnTypeVisitorRight.getType();

        if (typeRight == null)
            return true;

        if (typeLeft.equals(secondVisitorHelper.getSymbolTableIml().getVariableThis().getType())) {
            secondVisitorHelper.getReportList().add(ReportsUtils.reportEntryError(Stage.SEMANTIC, "Attempt to do a math operation using the this variable. This can only be used to access class methods", Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col"))));
            return true;
        }

        if (!typeLeft.equals(typeRight)) {
            secondVisitorHelper.getReportList().add(ReportsUtils.reportEntryError(Stage.SEMANTIC, "Attempt to do a math operation under operands of different types", Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col"))));
            return true;
        }

        if (typeLeft.isArray()) {
            secondVisitorHelper.getReportList().add(ReportsUtils.reportEntryError(Stage.SEMANTIC, "Left operand could not be an array pointer. Use the syntax array[index] to access and array", Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col"))));
            return true;
        }

        if (typeRight.isArray()) {
            secondVisitorHelper.getReportList().add(ReportsUtils.reportEntryError(Stage.SEMANTIC, "Right operand could not be an array pointer. Use the syntax array[index] to access and array", Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col"))));
            return true;
        }
        return true;
    }

    protected Boolean dealWithArrayAccess(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        SeekReturnTypeVisitor seekReturnTypeVisitorLeft = new SeekReturnTypeVisitor();
        SeekReturnTypeVisitor seekReturnTypeVisitorRight = new SeekReturnTypeVisitor();

        seekReturnTypeVisitorLeft.visit(node.getChildren().get(0), secondVisitorHelper);
        Type typeLeft = seekReturnTypeVisitorLeft.getType();

        if (typeLeft == null)
            return true;

        seekReturnTypeVisitorRight.visit(node.getChildren().get(1), secondVisitorHelper);
        Type typeRight = seekReturnTypeVisitorRight.getType();

        if (typeRight == null)
            return true;

        if (!typeLeft.isArray()) {
            secondVisitorHelper.getReportList().add(ReportsUtils.reportEntryError(Stage.SEMANTIC, "Try to array access in non array type. You can only perform array access on arrays", Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col"))));
            return false;
        }

        if (!typeRight.isArray() && !typeRight.getName().equals("int")) {
            secondVisitorHelper.getReportList().add(ReportsUtils.reportEntryError(Stage.SEMANTIC, "Index in array access must be of type integer", Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col"))));
            return false;
        }

        return true;
    }

    protected Boolean dealWithAnd(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        SeekReturnTypeVisitor seekReturnTypeVisitorLeft = new SeekReturnTypeVisitor();
        SeekReturnTypeVisitor seekReturnTypeVisitorRight = new SeekReturnTypeVisitor();


        seekReturnTypeVisitorLeft.visit(node.getChildren().get(0), secondVisitorHelper);
        Type typeLeft = seekReturnTypeVisitorLeft.getType();

        if (!typeLeft.getName().equals("boolean") || typeLeft.isArray()) {
            secondVisitorHelper.getReportList().add(ReportsUtils.reportEntryError(Stage.SEMANTIC, "And Operators demand that left operand be a boolean expression", Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col"))));
            return false;
        }

        seekReturnTypeVisitorRight.visit(node.getChildren().get(1), secondVisitorHelper);
        Type typeRight = seekReturnTypeVisitorRight.getType();

        if (!typeRight.getName().equals("boolean") || typeRight.isArray()) {
            secondVisitorHelper.getReportList().add(ReportsUtils.reportEntryError(Stage.SEMANTIC, "And Operators demand that right operand be a boolean expression", Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col"))));
            return false;
        }

        return true;
    }

    protected Boolean dealWithNot(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        SeekReturnTypeVisitor seekReturnType = new SeekReturnTypeVisitor();

        seekReturnType.visit(node.getChildren().get(0), secondVisitorHelper);
        Type type = seekReturnType.getType();

        if (!type.getName().equals("boolean") || type.isArray()) {
            secondVisitorHelper.getReportList().add(ReportsUtils.reportEntryError(Stage.SEMANTIC, "Not Operator demand that the expression result in a boolean", Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col"))));
            return false;
        }
        return true;
    }

    protected Boolean dealWithLess(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        SeekReturnTypeVisitor seekReturnTypeVisitorLeft = new SeekReturnTypeVisitor();
        SeekReturnTypeVisitor seekReturnTypeVisitorRight = new SeekReturnTypeVisitor();


        seekReturnTypeVisitorLeft.visit(node.getChildren().get(0), secondVisitorHelper);
        Type typeLeft = seekReturnTypeVisitorLeft.getType();

        if (!typeLeft.getName().equals("int") || typeLeft.isArray()) {
            secondVisitorHelper.getReportList().add(ReportsUtils.reportEntryError(Stage.SEMANTIC, "Less Operators demand that left operand be an integer", Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col"))));
            return false;
        }

        seekReturnTypeVisitorRight.visit(node.getChildren().get(1), secondVisitorHelper);
        Type typeRight = seekReturnTypeVisitorRight.getType();

        if (!typeRight.getName().equals("int") || typeRight.isArray()) {
            secondVisitorHelper.getReportList().add(ReportsUtils.reportEntryError(Stage.SEMANTIC, "Less Operators demand that right operand be an integer", Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col"))));
            return false;
        }

        return true;
    }

    protected Boolean dealWithAssignment(JmmNode node, SecondVisitorHelper secondVisitorHelper) {

        SeekReturnTypeVisitor seekReturnTypeVisitorLeft = new SeekReturnTypeVisitor();
        SeekReturnTypeVisitor seekReturnTypeVisitorRight = new SeekReturnTypeVisitor();

        seekReturnTypeVisitorLeft.visit(node.getChildren().get(0), secondVisitorHelper);
        Type typeLeft = seekReturnTypeVisitorLeft.getType();

        if (typeLeft == null)
            return true;

        seekReturnTypeVisitorRight.visit(node.getChildren().get(1), secondVisitorHelper);
        Type typeRight = seekReturnTypeVisitorRight.getType();

        if (typeRight == null)
            return true;

        if (!typeLeft.equals(typeRight)) {
            secondVisitorHelper.getReportList().add(ReportsUtils.reportEntryError(Stage.SEMANTIC, "Try to assign operands of different types", Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col"))));
            return true;
        }

        return true;
    }


    protected Boolean dealWithConditionOperation(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
/*
        JmmNode conditionNode = node.getChildren().get(0).getChildren().get(0);
        String nodeType = conditionNode.getKind();

        if (nodeType.equals("Boolean")) {
            return true;
        } else if (nodeType.equals("And") || nodeType.equals("Not") || nodeType.equals("Less")) {
            dealWithBooleanOperation(conditionNode, secondVisitorHelper);
        } else if (nodeType.equals("Add") || nodeType.equals("Sub") || nodeType.equals("Mult") || nodeType.equals("Div")) {
            secondVisitorHelper.getReportList().add(ReportsUtils.reportEntryError(Stage.SEMANTIC, "Attempt to do a condition operation without a valid condition", Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col"))));
        }

 */
        return true;
    }


    protected Boolean defaultVisit(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        return true;
    }


}
