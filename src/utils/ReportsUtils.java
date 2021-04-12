package utils;

import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

import java.util.List;

public class ReportsUtils {
    private static int numberErrors = 1;
    private static int numberWarnings = 1;

    public static void printReports(List<Report> reports) {
        if (reports.size() > 0) {
            System.out.println("Reports:");
            System.out.println("------------------------------------------------");
            for (Report report : reports)
                System.out.println(report.toString());
        }
    }

    public static Report reportEntryWarning(Stage stage, String message, int line, int col) {

        StringBuilder error = new StringBuilder();

        error.append(Constants.ANSI_YELLOW + "Warning Nº").append(numberWarnings).append(Constants.ANSI_RESET).append("\n");
        error.append(message);
        error.append("\n------------------------------------------------\n");

        numberWarnings++;

        return new Report(ReportType.WARNING, stage, line, col, error.toString());

    }

    public static Report reportEntryError(Stage stage, String message, int line, int col) {
        StringBuilder error = new StringBuilder("\n");
        error.append(Constants.ANSI_RED + "Error Nº").append(numberErrors).append(Constants.ANSI_RESET).append("\n");
        error.append("Line ").append(line).append(" Col ").append(col).append(": ").append(message);
        error.append("\n------------------------------------------------\n");

        numberErrors++;
        return new Report(ReportType.ERROR, stage, line, col, error.toString());
    }
}
