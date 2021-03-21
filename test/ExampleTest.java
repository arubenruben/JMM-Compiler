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
    public void testFile(String filePath, Stage stage, boolean mustFail){
        JmmParserResult result = null;
        String code = "";
        code = SpecsIo.read("test/" + filePath);
        result = TestUtils.parse(code);

        if(result.getReports().size() > 0) {
            System.out.println("\n\nTest file name: " + filePath.split("/")[filePath.split("/").length - 1]);
            Utils.printReports(result.getReports());
        }

        if (mustFail) {
            TestUtils.mustFail(result.getReports());
        } else {
            TestUtils.noErrors(result.getReports());
        }
    }

    // Test out if the parser succeeds in parsing the files
    @Test
    public void testFindMaximum() {
        testFile("fixtures/public/FindMaximum.jmm", Stage.SYNTATIC, false);
    }

    @Test
    public void testHelloWorld() {
        testFile("fixtures/public/HelloWorld.jmm", Stage.SYNTATIC, false);
    }

    @Test
    public void testLazySort() {
        testFile("fixtures/public/Lazysort.jmm", Stage.SYNTATIC, false);
    }

    @Test
    public void testLife() {
        testFile("fixtures/public/Life.jmm", Stage.SYNTATIC, false);
    }

    @Test
    public void testMonteCarloPi() {
        testFile("fixtures/public/MonteCarloPi.jmm", Stage.SYNTATIC, false);
    }

    @Test
    public void testQuickSort() {
        testFile("fixtures/public/QuickSort.jmm", Stage.SYNTATIC, false);
    }

    @Test
    public void testSimple() {
        testFile("fixtures/public/Simple.jmm", Stage.SYNTATIC, false);
    }

    @Test
    public void testTicTacToe(){
        testFile("fixtures/public/TicTacToe.jmm", Stage.SYNTATIC, false);
    }

    @Test
    public void testWhileAndIF(){
        testFile("fixtures/public/WhileAndIF.jmm", Stage.SYNTATIC, false);
    }

    @Test
    public void testBlowUp(){
        testFile("fixtures/public/fail/syntactical/BlowUp.jmm",  Stage.SYNTATIC, true);
    }

    @Test
    public void testCompleteWhileTest(){
        testFile("fixtures/public/fail/syntactical/CompleteWhileTest.jmm",  Stage.SYNTATIC, true);
    }

    @Test
    public void testLengthError(){
        testFile("fixtures/public/fail/syntactical/LengthError.jmm",  Stage.SYNTATIC, true);
    }

    @Test
    public void testMissingRightPar(){
        testFile("fixtures/public/fail/syntactical/MissingRightPar.jmm",  Stage.SYNTATIC, true);
    }

    @Test
    public void testMultipleSequential(){
        testFile("fixtures/public/fail/syntactical/MultipleSequential.jmm",  Stage.SYNTATIC, true);
    }

    @Test
    public void testNestedLoop(){
        testFile("fixtures/public/fail/syntactical/NestedLoop.jmm", Stage.SYNTATIC,true);
    }

}
