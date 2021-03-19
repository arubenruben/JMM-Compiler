import com.google.gson.JsonObject;
import pt.up.fe.comp.jmm.JmmParser;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.specs.util.SpecsIo;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main implements JmmParser {

    static String content = "";
    static JmmParserResult result = null;
    List<String> fileLines = new ArrayList<>();

    public JmmParserResult parse(String jmmCode) {
        //TODO: Check if this is only a error given out by the idea
        Parser parser = new Parser(new StringReader(jmmCode));
        List<Report> reports = new ArrayList<>();
        parser.setFileLines(jmmCode);
        parser.setReports(reports);
        SimpleNode root = null; // returns reference to root node

        try {
            root = parser.Program();
            root.dump(""); // prints the tree on the screen
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new JmmParserResult(root, reports);
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
        code = SpecsIo.read("testFiles/" + args[0]);

        Main main = new Main();
        JmmParserResult result = main.parse(code);

        if(result.getReports().size() > 0) {
            System.out.println("\n\nReports:");
            for (Report report : result.getReports())
                System.out.println(report.toString());
        }

        writeToFile(result.getRootNode().toJson(), "results/ast.txt");
    }


}