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

        if (TypeLeft == null) {
            System.err.println("Left child is null");
            return true;
        }

        if (TypeRight == null) {
            System.err.println("Right child is null");
            return true;
        } else if (!TypeLeft.equals(TypeRight))
            System.err.println("Not equals");

        return true;
    }

    protected Boolean dealWithBooleanOperation(JmmNode node, SecondVisitorHelper secondVisitorHelper) {

        if (node.getKind().equals("And"))
        {
            SeekReturnTypeVisitor seekReturnTypeVisitorLeft = new SeekReturnTypeVisitor();
            SeekReturnTypeVisitor seekReturnTypeVisitorRight = new SeekReturnTypeVisitor();

            //TODO:WHY???? NULL ON VISIT??'
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

        }
        else {

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


    protected Boolean defaultVisit(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        return true;
    }

}
