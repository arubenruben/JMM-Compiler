import Visitors.FirstVisitor;
import pt.up.fe.comp.MainAnalysis;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.JmmParser;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.analysis.JmmAnalysis;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.ast.JmmNodeImpl;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.ast.examples.ExamplePreorderVisitor;
import pt.up.fe.comp.jmm.ast.examples.ExampleVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.specs.util.SpecsIo;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;



public class Main implements JmmParser, JmmAnalysis {

    public JmmParserResult parse(String jmmCode) {
        //TODO: Check if this is only a error given out by the idea
        List<Report> reports = new ArrayList<>();
        List<String> fileLines = new ArrayList<>();

        Scanner s = new Scanner(jmmCode);
        while (s.hasNext()) {
            fileLines.add(s.nextLine());
        }

        try {
            Parser parser = new Parser(new StringReader(jmmCode));
            parser.setFileLines(fileLines);
            parser.setReports(reports);
            SimpleNode root = null; // returns reference to root node
            root = parser.Program();
            return new JmmParserResult(root, reports);
        }
        catch (ParseException e) {
            reports.add(new Report(ReportType.ERROR, Stage.SYNTATIC, e.currentToken.next.beginLine, e.currentToken.next.beginColumn, Utils.reportEntryError(e, TestUtils.getNumReports(reports, ReportType.ERROR) + 1, fileLines)));
            return new JmmParserResult(null, reports);
        }
        catch (RuntimeException ignored){
            return new JmmParserResult(null, reports);
        }
    }

    public static void writeToFile(String content, String path){
        try {
            FileWriter myWriter = new FileWriter(path);
            myWriter.write(content);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred writing to file");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Executing with args: " + Arrays.toString(args));
        if (args[0].contains("fail")) {
            throw new RuntimeException("It's supposed to fail");
        }

        String code = null;
        code = SpecsIo.read(args[0]);
        JmmParserResult result = null;
        Main main = new Main();
        result = main.parse(code);

        JmmNode node = result.getRootNode();
      /*  PreorderJmmVisitor preorderJmmVisitor = new ExamplePreorderVisitor("Integer",  "value");
        if(node != null)
            System.out.println(preorderJmmVisitor.visit(node, ""));

        Utils.printReports(result.getReports());*/
        var analysis = new Main();

        analysis.semanticAnalysis(result);


        if(result.getRootNode() != null)
            writeToFile(result.toJson(), "results/ast.txt");

    }


    public JmmSemanticsResult semanticAnalysis(JmmParserResult parserResult) {

        SymbolTable symbolTable = new SymbolTableIml();

        if (TestUtils.getNumReports(parserResult.getReports(), ReportType.ERROR) > 0) {
            return null;
        }
        if (parserResult.getRootNode() == null) {
            return null;
        }

        // Convert Simple node to JmmNodeIml
        JmmNode node = parserResult.getRootNode().sanitize();

        System.out.println("VISITOR");
        FirstVisitor visitor = new FirstVisitor("ImportPath", "value", symbolTable);
        System.out.println(visitor.visit(node, symbolTable));

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
}

