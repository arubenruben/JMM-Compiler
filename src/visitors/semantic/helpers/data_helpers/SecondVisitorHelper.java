package visitors.semantic.helpers.data_helpers;

import pt.up.fe.comp.jmm.report.Report;
import symbols.SymbolTableIml;

import java.util.List;

public class SecondVisitorHelper extends VisitorDataHelper {

    private final String currentMethodName;

    public SecondVisitorHelper(String currentMethodName, SymbolTableIml symbolTableIml, List<Report> reportList) {
        super(symbolTableIml, reportList);
        this.currentMethodName = currentMethodName;
    }

    public String getCurrentMethodName() {
        return currentMethodName;
    }
}

