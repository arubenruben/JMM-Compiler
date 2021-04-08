package Visitors;

import Symbols.SymbolTableIml;
import Visitors.helpers.data_helpers.SecondVisitorHelper;
import Visitors.helpers.SeekTypesVisitor;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

import java.util.ArrayList;
import java.util.List;


public class SecondVisitor extends PreorderJmmVisitor<SecondVisitorHelper, Boolean> {

    public SecondVisitor() {
        addVisit("Add", this::dealWithMathOperation);
        addVisit("Sub", this::dealWithMathOperation);
        addVisit("Mult", this::dealWithMathOperation);
        addVisit("Div", this::dealWithMathOperation);
        setDefaultVisit(this::defaultVisit);
    }

    //TODO:Pass The report List inside
    protected Boolean dealWithMathOperation(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        List<String> list = new ArrayList<>();
        list.add("Integer");
        list.add("Boolean");
        list.add("Identifier");

        //TODO:Operaçoes em arrays
        //list.add()

        SeekTypesVisitor seekTypesVisitor = new SeekTypesVisitor(list.toArray(new String[0]));

        List<Type> typesFound = seekTypesVisitor.visit(node, secondVisitorHelper);

        if (typesFound == null) {
            System.err.println("Identifier found that have not been declared");
            return true;
        }

        for (int i = 0; i < typesFound.size() - 1; i++) {
            if (!typesFound.get(i).getName().equals(typesFound.get(i + 1).getName())) {
                System.err.println("This operands are not the same type");
                return true;
            }
        }
        return true;
    }

    protected Boolean defaultVisit(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        return true;
    }

}
