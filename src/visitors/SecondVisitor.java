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

        addVisit("And", this::dealWithBooleanOperation);
        addVisit("Not", this::dealWithBooleanOperation);
        addVisit("Less", this::dealWithBooleanOperation);

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

    protected Boolean dealWithBooleanOperation(JmmNode node, SecondVisitorHelper secondVisitorHelper) {

        SeekReturnTypeVisitor seekReturnTypeVisitorLeft = new SeekReturnTypeVisitor();
        SeekReturnTypeVisitor seekReturnTypeVisitorRight = new SeekReturnTypeVisitor();


        seekReturnTypeVisitorLeft.visit(node.getChildren().get(0), secondVisitorHelper);
        Type TypeLeft = seekReturnTypeVisitorLeft.getType();



        if (node.getKind().equals("And"))
        {
            seekReturnTypeVisitorRight.visit(node.getChildren().get(1), secondVisitorHelper);

            Type TypeRight = seekReturnTypeVisitorRight.getType();

            System.out.println(!TypeLeft.getName().equals("boolean"));
            System.out.println(!TypeRight.getName().equals("boolean"));

            if (TypeLeft == null)
                System.err.println("Left child is null");

            if (TypeRight == null)
                System.err.println("Right child is null");

            else if ((!TypeLeft.getName().equals("boolean")) || (!TypeRight.getName().equals("boolean")))
                System.err.println("One or more elements are not boooleans");

        }
        else if(node.getKind().equals("Not")){

            if (TypeLeft == null)
                System.err.println("Left child is null");

            else if ((!TypeLeft.getName().equals("boolean")))
                System.err.println("One or more elements are not booleans");

        }
        else if(node.getKind().equals("Less")){

            seekReturnTypeVisitorRight.visit(node.getChildren().get(1), secondVisitorHelper);

            Type TypeRight = seekReturnTypeVisitorRight.getType();

            if (TypeLeft == null)
                System.err.println("Left child is null");

            if (TypeRight == null)
                System.err.println("Right child is null");

            else if ((!TypeLeft.getName().equals("int")) || (!TypeRight.getName().equals("int")))
                System.err.println("The *less* operation is not being used between integers");

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

        JmmNode conditionNode = node.getChildren().get(0).getChildren().get(0);
        String nodeType = conditionNode.getKind();

        if (nodeType.equals("Boolean")) {
            return true;
        } else if (nodeType.equals("And") || nodeType.equals("Not") || nodeType.equals("Less")) {
            dealWithBooleanOperation(conditionNode, secondVisitorHelper);
        } else if (nodeType.equals("Add") || nodeType.equals("Sub") || nodeType.equals("Mult") || nodeType.equals("Div")) {
            System.err.println("If or While statement is not a boolean");
        }
        return true;
    }


    protected Boolean defaultVisit(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        return true;
    }


}
