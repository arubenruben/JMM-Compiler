package Visitors.helpers;

import Symbols.MethodSymbol;
import Visitors.helpers.data_helpers.SecondVisitorHelper;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

public class SeekTypesVisitor extends PreorderJmmVisitor<SecondVisitorHelper, Type> {
    private Type typeReturned;

    public SeekTypesVisitor() {
        setDefaultVisit(this::defaultVisit);
    }

    protected Type defaultVisit(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        return null;
    }


}
