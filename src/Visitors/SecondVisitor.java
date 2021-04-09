package Visitors;

import Visitors.helpers.SeekReturnTypeVisitor;
import Visitors.helpers.data_helpers.SecondVisitorHelper;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;


public class SecondVisitor extends PreorderJmmVisitor<SecondVisitorHelper, Boolean> {

    public SecondVisitor() {
        addVisit("Add", this::dealWithMathOperation);
        addVisit("Sub", this::dealWithMathOperation);
        addVisit("Mult", this::dealWithMathOperation);
        addVisit("Div", this::dealWithMathOperation);
        addVisit("ArrayAccess", this::dealWithArrayAccess);
        addVisit("AssignmentVar", this::dealWithAssignmentVar);
        addVisit("AssignmentVarArray", this::dealWithAssignmentVarArray);
        addVisit("And", this::dealWithBooleanOperation);
        addVisit("Not", this::dealWithBooleanOperation);
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

        if (!typeLeft.equals(typeRight))
            secondVisitorHelper.getReportList().add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), "Attempt to do a math operation under operands of different types"));

        if (typeLeft.isArray())
            secondVisitorHelper.getReportList().add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), "Left operand could not be an array pointer. Use the syntax array[index] to access and array"));

        if (typeLeft.isArray())
            secondVisitorHelper.getReportList().add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), "Right operand could not be an array pointer. Use the syntax array[index] to access and array"));
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
            secondVisitorHelper.getReportList().add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), "Try to array access in non array type. You can only perform array access on arrays"));
            return false;
        }

        if (!typeRight.isArray() && !typeRight.getName().equals("int")) {
            secondVisitorHelper.getReportList().add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), "Index in array access must be of type integer"));
            return false;
        }

        return true;
    }

    protected Boolean dealWithBooleanOperation(JmmNode node, SecondVisitorHelper secondVisitorHelper) {

        if (node.getKind().equals("And")) {
            SeekReturnTypeVisitor seekReturnTypeVisitorLeft = new SeekReturnTypeVisitor();
            SeekReturnTypeVisitor seekReturnTypeVisitorRight = new SeekReturnTypeVisitor();

            seekReturnTypeVisitorLeft.visit(node.getChildren().get(0), secondVisitorHelper);
            Type TypeLeft = seekReturnTypeVisitorLeft.getType();

            seekReturnTypeVisitorRight.visit(node.getChildren().get(1), secondVisitorHelper);

            Type TypeRight = seekReturnTypeVisitorRight.getType();

            System.out.println(!TypeLeft.getName().equals("boolean"));
            System.out.println(!TypeRight.getName().equals("boolean"));

            if (TypeLeft == null)
                System.err.println("Left child is null");

            if (TypeRight == null)
                System.err.println("Right child is null");

            else if ((!TypeLeft.getName().equals("boolean")) || (!TypeRight.getName().equals("boolean")))
                System.err.println("One or more elements are not booleans");

        } else {

            SeekReturnTypeVisitor seekReturnTypeVisitorLeft = new SeekReturnTypeVisitor();


            seekReturnTypeVisitorLeft.visit(node.getChildren().get(0), secondVisitorHelper);
            Type TypeLeft = seekReturnTypeVisitorLeft.getType();

            if (TypeLeft == null)
                System.err.println("Left child is null");

            else if ((!TypeLeft.getName().equals("boolean")))
                System.err.println("One or more elements are not booleans");

        }
        return true;
    }

    protected Boolean dealWithAssignmentVar(JmmNode node, SecondVisitorHelper secondVisitorHelper) {

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
            secondVisitorHelper.getReportList().add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), "Try to assign operands of different types"));
            return true;
        }


        return true;
    }

    protected Boolean dealWithAssignmentVarArray(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        return true;
    }


    protected Boolean defaultVisit(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        return true;
    }

}
