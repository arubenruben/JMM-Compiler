package visitors.semantic.helpers.data_helpers;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
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

    public String nameGenerator(String methodName, List<Symbol> parameters){

        StringBuilder returnString = new StringBuilder();

        returnString.append(methodName);

        for(Symbol symbol : parameters){
            returnString.append("_" + symbol.getType().getName());
            if(symbol.getType().isArray()){
                returnString.append("_arr");
            }
        }
        return returnString.toString();
    }
}
