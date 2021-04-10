package visitors.helpers.data_helpers;

import pt.up.fe.comp.jmm.report.Report;
import symbols.MethodSymbol;
import symbols.SymbolTableIml;

import java.util.List;

public class MethodBodyDataHelper extends VisitorDataHelper {
    private final MethodSymbol methodSymbol;

    public MethodBodyDataHelper(MethodSymbol methodSymbol, SymbolTableIml symbolTableIml, List<Report> reportList) {
        super(symbolTableIml, reportList);
        this.methodSymbol = methodSymbol;
    }

    public MethodSymbol getMethodSymbol() {
        return methodSymbol;
    }
}
