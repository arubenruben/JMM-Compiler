import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.report.Report;
import utils.Constants;

import java.util.List;

public class Utils {

    public static void printReports(List<Report> reports) {
        if (reports.size() > 0) {
            System.out.println("Reports:");
            System.out.println("------------------------------------------------");
            for (Report report : reports)
                System.out.println(report.toString());
        }
    }

    public static String reportEntryWarning(String message, int numberOfSyntaxWarnings) {
        String error = "\n";
        error += Constants.ANSI_YELLOW + "Warning Nº" + numberOfSyntaxWarnings + Constants.ANSI_RESET + "\n";
        error += message;
        error += "\n------------------------------------------------";
        return error;
    }

    public static String reportEntryError(ParseException e, long numberOfSyntaxErrors, List<String> fileLines) {
        StringBuilder error = new StringBuilder("\n");
        error.append(Constants.ANSI_RED + "Error Nº").append(numberOfSyntaxErrors).append(Constants.ANSI_RESET).append("\n");
        String line;
        StringBuilder indicator = new StringBuilder();

        if (!fileLines.isEmpty()) {
            line = fileLines.get(e.currentToken.next.beginLine - 1);
            for (int i = 0; i < e.currentToken.next.beginColumn - 1; i++) {
                if (line.charAt(i) == '\t')
                    indicator.append("\t");
                else
                    indicator.append(" ");
            }
            error.append(line).append("\n");
            indicator.append(Constants.ANSI_RED + "^" + Constants.ANSI_RESET + "\n");
            error.append(indicator);
        }

        error.append("Found: ").append(e.currentToken.next).append("\n");
        error.append("Expected: ");
        for (int[] expectedTokenArray : e.expectedTokenSequences) {
            for (int expectedToken : expectedTokenArray) {
                error.append(e.tokenImage[expectedToken]).append(",");
            }
        }

        error.append("\n------------------------------------------------");
        return error.toString();
    }

    public static void printSymbolTable(SymbolTable symbolTableIml) {
        System.out.println(symbolTableIml.toString());
    }

}