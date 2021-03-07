
import pt.up.fe.comp.jmm.JmmParser;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.report.Report;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Arrays;
import java.util.ArrayList;

public class Main implements JmmParser {

	static String content = "";
	static JmmParserResult result = null;

	public JmmParserResult parse(String jmmCode) {
		
		try {
		    Parser parser = new Parser(new StringReader(jmmCode));
    		SimpleNode root = parser.Program(); // returns reference to root node
            	
    		root.dump(""); // prints the tree on the screen


    	
    		return new JmmParserResult(root, new ArrayList<Report>());
		} catch(ParseException e) {
			throw new RuntimeException("Error while parsing", e);
		}

	}

    public static void main(String[] args) {
        System.out.println("Executing with args: " + Arrays.toString(args));
        if (args[0].contains("fail")) {
            throw new RuntimeException("It's supposed to fail");
        }

        Main main = new Main();

		try {
			 main.parse(Files.readString(Paths.get(args[0])));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }


}