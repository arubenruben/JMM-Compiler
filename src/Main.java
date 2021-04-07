import Symbols.SymbolTableIml;
import Visitors.FirstVisitor;
import Visitors.SecondVisitor;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.JmmParser;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.analysis.JmmAnalysis;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.ast.AJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.specs.util.SpecsIo;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class Main implements JmmParser, JmmAnalysis {


    public static void main(String[] args) {
        String code;
        Main main = new Main();
        JmmParserResult parserResult;

        if (args[0].contains("fail"))
            throw new RuntimeException("It's supposed to fail");


        System.out.println("Executing with args: " + Arrays.toString(args));

        code = SpecsIo.read(args[0]);

        parserResult = main.parse(code);

        if (parserResult.getRootNode() != null)
            writeToFile(parserResult.toJson(), "results/ast.txt");

        JmmSemanticsResult semanticAnalysis = main.semanticAnalysis(parserResult);
    /*
        if (node != null)
            System.out.println(preorderJmmVisitor.visit(node, ""));

        Utils.printReports(result.getReports());



        if (result.getRootNode() != null)
            writeToFile(result.toJson(), "results/ast.txt");


     */
    }


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

    public JmmSemanticsResult semanticAnalysis(JmmParserResult parserResult) {

        SymbolTableIml symbolTable = new SymbolTableIml();

        // Convert Simple node to JmmNodeIml
        JmmNode node = parserResult.getRootNode().sanitize();

        //TODO:???-Ruben
        if (TestUtils.getNumReports(parserResult.getReports(), ReportType.ERROR) > 0)
            return null;
        //TODO:???-Ruben
        if (parserResult.getRootNode() == null)
            return null;


        AJmmVisitor<SymbolTableIml, Boolean> firstVisitor = new FirstVisitor();
        firstVisitor.visit(node, symbolTable);

        for (String methodName : symbolTable.getMethodsHashmap().keySet()) {
            AJmmVisitor<SymbolTableIml, Boolean> secondVisitor = new SecondVisitor(methodName);
            secondVisitor.visit(node, symbolTable);
        }


        // ExampleVisitor visitor = new ExampleVisitor("VarDeclaration", "value");
        // System.out.println(visitor.visit(node, ""));

       /* System.out.println("PREORDER VISITOR");
        var preOrderVisitor = new ExamplePreorderVisitor("Identifier", "id");
        System.out.println(preOrderVisitor.visit(node, ""));

        System.out.println("POSTORDER VISITOR");
        var postOrderVisitor = new ExamplePostorderVisitor();
        var kindCount = new HashMap<String, Integer>();
        postOrderVisitor.visit(node, kindCount);
        System.out.println("Kinds count: " + kindCount);*/

        // No Symbol Table being calculated yet
        return new JmmSemanticsResult(node, symbolTable, parserResult.getReports());

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

