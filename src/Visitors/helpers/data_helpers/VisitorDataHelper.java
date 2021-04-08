package Visitors.helpers.data_helpers;

import Symbols.SymbolTableIml;
import pt.up.fe.comp.jmm.report.Report;

import java.util.List;

public abstract class VisitorDataHelper {
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
