package Visitors;

import Visitors.helpers.SeekReturnTypeVisitor;
import Visitors.helpers.data_helpers.SecondVisitorHelper;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;


public class SecondVisitor extends PreorderJmmVisitor<SecondVisitorHelper, Boolean> {

    public SecondVisitor() {
        addVisit("Add", this::dealWithMathOperation);
        addVisit("Sub", this::dealWithMathOperation);
        addVisit("Mult", this::dealWithMathOperation);
        addVisit("Div", this::dealWithMathOperation);

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


        //TODO:WHY???? NULL ON VISIT??'
        seekReturnTypeVisitorLeft.visit(node.getChildren().get(0), secondVisitorHelper);
        Type TypeLeft = seekReturnTypeVisitorLeft.getType();

        seekReturnTypeVisitorRight.visit(node.getChildren().get(1), secondVisitorHelper);

        Type TypeRight = seekReturnTypeVisitorRight.getType();

        if (TypeLeft == null)
            System.err.println("Left child is null");

        if (TypeRight == null)
            System.err.println("Right child is null");

        else if (!TypeLeft.equals(TypeRight))
            System.err.println("Not equals");


        return true;
    }

    protected Boolean dealWithBooleanOperation(JmmNode node, SecondVisitorHelper secondVisitorHelper) {

        SeekReturnTypeVisitor seekReturnTypeVisitorLeft = new SeekReturnTypeVisitor();
        SeekReturnTypeVisitor seekReturnTypeVisitorRight = new SeekReturnTypeVisitor();


        seekReturnTypeVisitorLeft.visit(node.getChildren().get(0), secondVisitorHelper);
        Type TypeLeft = seekReturnTypeVisitorLeft.getType();

        seekReturnTypeVisitorRight.visit(node.getChildren().get(1), secondVisitorHelper);

        Type TypeRight = seekReturnTypeVisitorRight.getType();

        if (node.getKind().equals("And"))
        {

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

            if (TypeLeft == null)
                System.err.println("Left child is null");

            if (TypeRight == null)
                System.err.println("Right child is null");

            else if ((!TypeLeft.getName().equals("int")) || (!TypeRight.getName().equals("int")))
                System.err.println("The *less* operation is not being used between integers");

        }

        return true;
    }


    protected Boolean dealWithConditionOperation(JmmNode node, SecondVisitorHelper secondVisitorHelper) {

        JmmNode conditionNode = node.getChildren().get(0).getChildren().get(0);
        String nodeType = conditionNode.getKind();

        if(nodeType.equals("Boolean")){
            return true;
        }
        else if(nodeType.equals("And") || nodeType.equals("Not") || nodeType.equals("Less")){
            dealWithBooleanOperation(conditionNode,secondVisitorHelper);
        }
        else if(nodeType.equals("Add") || nodeType.equals("Sub") || nodeType.equals("Mult") || nodeType.equals("Div")){
            System.err.println("If or While statement is not a boolean");
        }



        return true;
    }



    protected Boolean defaultVisit(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        return true;
    }

}
