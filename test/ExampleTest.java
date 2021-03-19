import org.junit.Test;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.comp.jmm.report.Stage;

import static org.junit.Assert.*;

public class ExampleTest {

    /**
     * Function meant to test out if a certain file must fail or not the jjt parser
     * @param filePath name of the file
     * @param stage the stage at which the parse must fail
     */
    public boolean testFile(String filePath, Stage stage){
        JmmParserResult result = null;
        try {
            String code = "";
            code = SpecsIo.read("test/" + filePath);
            result = TestUtils.parse(code);
        }
        catch(Exception e){
            System.err.println(e.toString());
            return false;
        }
        finally{
            if(result.getReports().size() > 0) {
                System.out.println("\n\nReports:");
                for (Report report : result.getReports())
                    System.out.println(report.toString());
            }
        }

        return result.getReports().size() < 10;
    }

    // Test out if the parser succeeds in parsing the files
    @Test
    public void testFindMaximum() {
        assertTrue(testFile("fixtures/public/FindMaximum.jmm", Stage.SYNTATIC));
    }

    @Test
    public void testHelloWorld() {
        assertTrue(testFile("fixtures/public/HelloWorld.jmm", Stage.SYNTATIC));
    }

    @Test
    public void testLazySort() {
        assertTrue(testFile("fixtures/public/Lazysort.jmm", Stage.SYNTATIC));
    }

    @Test
    public void testLife() {
        assertTrue(testFile("fixtures/public/Life.jmm", Stage.SYNTATIC));
    }

    @Test
    public void testMonteCarloPi() {
        assertTrue(testFile("fixtures/public/MonteCarloPi.jmm", Stage.SYNTATIC));
    }

    @Test
    public void testQuickSort() {
        assertTrue(testFile("fixtures/public/QuickSort.jmm", Stage.SYNTATIC));
    }

    @Test
    public void testSimple() {
        assertTrue(testFile("fixtures/public/Simple.jmm", Stage.SYNTATIC));
    }

    @Test
    public void testTicTacToe(){
        assertTrue(testFile("fixtures/public/TicTacToe.jmm", Stage.SYNTATIC));
    }

    @Test
    public void testWhileAndIF(){
        assertTrue(testFile("fixtures/public/WhileAndIF.jmm", Stage.SYNTATIC));
    }

    @Test
    public void testBlowUp(){
        assertFalse(testFile("fixtures/public/fail/syntactical/BlowUp.jmm",  Stage.SYNTATIC));
    }

    @Test
    public void testCompleteWhileTest(){
        assertFalse(testFile("fixtures/public/fail/syntactical/CompleteWhileTest.jmm",  Stage.SYNTATIC));
    }

    @Test
    public void testLengthError(){
        assertTrue(testFile("fixtures/public/fail/syntactical/LengthError.jmm",  Stage.SYNTATIC));
    }

    @Test
    public void testMissingRightPar(){
        assertTrue(testFile("fixtures/public/fail/syntactical/MissingRightPar.jmm",  Stage.SYNTATIC));
    }

    @Test
    public void testMultipleSequential(){
        assertTrue(testFile("fixtures/public/fail/syntactical/MultipleSequential.jmm",  Stage.SYNTATIC));
    }

    @Test
    public void testNestedLoop(){
        assertTrue(testFile("fixtures/public/fail/syntactical/NestedLoop.jmm", Stage.SYNTATIC));
    }

}
