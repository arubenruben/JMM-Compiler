package visitors.helpers.data_helpers;

import pt.up.fe.comp.jmm.report.Report;
import symbols.SymbolTableIml;

import java.util.List;

public class VisitorDataHelper {
    private final SymbolTableIml symbolTableIml;
    private final List<Report> reportList;

    public VisitorDataHelper(SymbolTableIml symbolTableIml, List<Report> reportList) {
        this.symbolTableIml = symbolTableIml;
        this.reportList = reportList;
    }

    public SymbolTableIml getSymbolTableIml() {
        return symbolTableIml;
    }

    public List<Report> getReportList() {
        return reportList;
    }
}
