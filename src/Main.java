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
        Parser parser = new Parser(new StringReader(jmmCode));
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

        InputStream in = null;

        try {
            in = new FileInputStream(args[0]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Main main = new Main();

        String code = "package file;\n" +
                "import io;\n" +
                "\n" +
                "class Fac {\n" +
                "    public int ComputeFac(int num) {\n" +
                "        int num_aux;\n" +
                "        if (num < 1)\n" +
                "            num_aux = 1;\n" +
                "        else\n" +
                "            num_aux = num * (this.ComputeFac(num - 1));\n" +
                "        return num_aux;\n" +
                "    }\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        io.println(new Fac().ComputeFac(10)); //assuming the existence\n" +
                "        // of the classfile io.class\n" +
                "        /*\n" +
                "            adsaskdaskjdkljq2wleqkwljdq String []]@¹£¹@£12\n" +
                "        */\n" +
                "    }\n" +
                "}";

        main.parse(code);
    }




}