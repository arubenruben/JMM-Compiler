import pt.up.fe.comp.jmm.report.Report;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;



public class Utils {
	
	public static InputStream toInputStream(String text) {
        try {
            return new ByteArrayInputStream(text.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static void printReports(List<Report> reports){
        if (reports.size() > 0) {
            System.out.println("Reports:");
            System.out.println("------------------------------------------------");
            for (Report report : reports)
                System.out.println(report.toString());
        }
    }

    public static String reportEntryWarning(String message, int numberOfSyntaxWarnings){
        String error = "\n";
        error += Constants.ANSI_YELLOW + "Warning Nº" + numberOfSyntaxWarnings + Constants.ANSI_RESET + "\n";
        error += message;
        error += "\n------------------------------------------------";
        return error;
    }

    public static String reportEntryError(ParseException e, long numberOfSyntaxErrors, List<String> fileLines){
        String errorType = "Syntax";
        String error = "\n";
        error += Constants.ANSI_RED + "Error Nº" + numberOfSyntaxErrors + Constants.ANSI_RESET + "\n";
        String line = "";
        String indicator = "";

        if(!fileLines.isEmpty()){
            line = fileLines.get(e.currentToken.next.beginLine - 1);
            for(int i = 0; i < e.currentToken.next.beginColumn - 1; i++) {
                if (line.charAt(i) == '\t')
                    indicator += "\t";
                else
                    indicator += " ";
            }
            error += line + "\n";
            indicator += Constants.ANSI_RED + "^" + Constants.ANSI_RESET + "\n";
            error += indicator;
        }

        error += "Found: " + e.currentToken.next + "\n";
        error += "Expected: ";
        for (int [] expectedTokenArray : e.expectedTokenSequences){
            for(int expectedToken : expectedTokenArray){
                error += e.tokenImage[expectedToken] + ",";
            }
        }

        error += "\n------------------------------------------------";
        return error;
    }

}