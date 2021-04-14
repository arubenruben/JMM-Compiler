package visitors.helpers.data_helpers;

import pt.up.fe.comp.jmm.report.Report;
import symbols.SymbolTableIml;

import java.util.List;

public class VisitorOllirDataHelper extends VisitorDataHelper {
    private String code;

    public VisitorOllirDataHelper(SymbolTableIml symbolTableIml, List<Report> reportList, String code) {
        super(symbolTableIml, reportList);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
