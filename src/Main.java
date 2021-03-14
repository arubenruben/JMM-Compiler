import com.google.gson.JsonObject;
import pt.up.fe.comp.jmm.JmmParser;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.specs.util.SpecsIo;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class Main implements JmmParser {

    static String content = "";
    static JmmParserResult result = null;
    List<String> fileLines = new ArrayList<>();

    public JmmParserResult parse(String jmmCode) {
        //TODO: Check if this is only a error given out by the idea
        Parser parser = new Parser(new StringReader(jmmCode));
        parser.setFileLines(jmmCode);

        SimpleNode root = null; // returns reference to root node

        try {
            root = parser.Program();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        root.dump(""); // prints the tree on the screen

        return new JmmParserResult(root, new ArrayList<Report>());
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
        writeToFile(result.getRootNode().toJson(), "results/ast.txt");
    }


}