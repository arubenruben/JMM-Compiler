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

        if (TypeLeft == null){
            System.err.println("Left child is null");
            return true;
        }

        if (TypeRight == null){
            System.err.println("Right child is null");
            return true;
        }

        else if (!TypeLeft.equals(TypeRight))
            System.err.println("Not equals");

        System.out.println("Left"+TypeLeft.toString());
        System.out.println("Right"+TypeRight.toString());


        return true;
    }


    protected Boolean defaultVisit(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        return true;
    }

}
