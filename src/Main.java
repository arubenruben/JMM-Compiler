import pt.up.fe.comp.jmm.JmmParser;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.report.Report;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;


public class Main implements JmmParser {

    static String content = "";
    static JmmParserResult result = null;

    public JmmParserResult parse(String jmmCode) {
        Parser parser = new Parser(new ByteArrayInputStream(jmmCode.getBytes()));
        SimpleNode root = null; // returns reference to root node
        try {
            root = parser.Program();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        root.dump(""); // prints the tree on the screen

        return new JmmParserResult(root, new ArrayList<Report>());
    }

    public static void main(String[] args) {
        System.out.println("Executing with args: " + Arrays.toString(args));
        if (args[0].contains("fail")) {
            throw new RuntimeException("It's supposed to fail");
        }

        String code = null;
        try {
            code = Files.readString(Paths.get("file/" + args[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Main main = new Main();
        main.parse(code);
    }




}