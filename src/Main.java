import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.JmmParser;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.jasmin.JasminBackend;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.JmmOptimization;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.specs.util.SpecsIo;
import stages.AnalysisStage;
import stages.BackendStage;
import stages.OptimizationStage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class Main implements JmmParser {

    @Override
    public JmmParserResult parse(String jmmCode) {
        //TODO: Check if this is only a error given out by the idea
        List<Report> reports = new ArrayList<>();
        List<String> fileLines = new ArrayList<>();

        Scanner s = new Scanner(jmmCode);
        while (s.hasNext()) {
            fileLines.add(s.nextLine());
        }

        try {
            SimpleNode root; // returns reference to root node
            Parser parser = new Parser(new StringReader(jmmCode));
            parser.setFileLines(fileLines);
            parser.setReports(reports);
            root = parser.Program();
            return new JmmParserResult(root, reports);
        } catch (ParseException e) {
            reports.add(new Report(ReportType.ERROR, Stage.SYNTATIC, e.currentToken.next.beginLine, e.currentToken.next.beginColumn, Utils.reportEntryError(e, TestUtils.getNumReports(reports, ReportType.ERROR) + 1, fileLines)));
            return new JmmParserResult(null, reports);
        } catch (RuntimeException ignored) {
            return new JmmParserResult(null, reports);
        }
    }


    public static void main(String[] args) {
        String code;
        JmmParserResult parserResult;

        if (args[0].contains("fail"))
            throw new RuntimeException("It's supposed to fail");

        System.out.println("Executing with args: " + Arrays.toString(args));

        code = SpecsIo.read(args[0]);

        parserResult = new Main().parse(code);

        Utils.printReports(parserResult.getReports());

        if (parserResult.getRootNode() != null)
            writeToFile(parserResult.toJson(), "results/ast.txt");

        if (parserResult.getRootNode() == null){
            return;
        }


        if (TestUtils.getNumReports(parserResult.getReports(), ReportType.ERROR) > 0)
            return;

        JmmSemanticsResult semanticsResults = new AnalysisStage().semanticAnalysis(parserResult);

       /* Utils.printSymbolTable(semanticsResults.getSymbolTable());
        Utils.printReports(semanticsResults.getReports());*/

        // It is expected that the Optimize class can be instantiated without arguments
        JmmOptimization optimization = new OptimizationStage();

        OllirResult ollirResult = optimization.toOllir(semanticsResults);


        //System.out.println(ollirResult);

        JasminBackend backend = new BackendStage();

        JasminResult jasminResult = backend.toJasmin(ollirResult);

        //System.out.println(jasminResult.getJasminCode());
    }


    public static void writeToFile(String content, String path) {
        try {
            FileWriter myWriter = new FileWriter(path);
            myWriter.write(content);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred writing to file");
            e.printStackTrace();
        }
    }
}

