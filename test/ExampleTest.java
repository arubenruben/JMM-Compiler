import org.junit.Test;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.specs.util.SpecsIo;

import static org.junit.Assert.*;

public class ExampleTest {

    enum Stage
    {
        Lexical, Syntatic, Semantic
    }

    /**
     * Function meant to test out if a certain file must fail or not the jjt parser
     * @param filePath name of the file
     * @param stage the stage at which the parse must fail
     */
    public boolean testFile(String filePath, Stage stage){

        try {
            String code = "";
            code = SpecsIo.read("test/" + filePath);
                TestUtils.parse(code);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    // Test out if the parser succeeds in parsing the files

    @Test
    public void testFindMaximum() {
        assertTrue(testFile("fixtures/public/FindMaximum.jmm", Stage.Syntatic));
    }

    @Test
    public void testHelloWorld() {
        assertTrue(testFile("fixtures/public/HelloWorld.jmm", Stage.Syntatic));
    }

    @Test
    public void testLazySort() {
        assertTrue(testFile("fixtures/public/Lazysort.jmm", Stage.Syntatic));
    }

    @Test
    public void testLife() {
        assertTrue(testFile("fixtures/public/Life.jmm", Stage.Syntatic));
    }

    @Test
    public void testMonteCarloPi() {
        assertTrue(testFile("fixtures/public/MonteCarloPi.jmm", Stage.Syntatic));
    }

    @Test
    public void testQuickSort() {
        assertTrue(testFile("fixtures/public/QuickSort.jmm", Stage.Syntatic));
    }

    @Test
    public void testSimple() {
        assertTrue(testFile("fixtures/public/Simple.jmm", Stage.Syntatic));
    }

    @Test
    public void testTicTacToe(){
        assertTrue(testFile("fixtures/public/TicTacToe.jmm", Stage.Syntatic));
    }

    @Test
    public void testWhileAndIF(){
        assertTrue(testFile("fixtures/public/WhileAndIF.jmm", Stage.Syntatic));
    }

    // Test out if the parser fails in parsing the files

    //TODO: Falar se isto tem ou não de ser erro por causa do error recovery
    @Test
    public void testBlowUp(){
        assertFalse(testFile("fixtures/public/fail/syntactical/BlowUp.jmm",  Stage.Syntatic));
    }

    @Test
    public void testCompleteWhileTest(){
        assertFalse(testFile("fixtures/public/fail/syntactical/CompleteWhileTest.jmm",  Stage.Syntatic));
    }

    @Test
    public void testLengthError(){
        assertFalse(testFile("fixtures/public/fail/syntactical/LengthError.jmm",  Stage.Syntatic));
    }

    @Test
    public void testMissingRightPar(){
        assertFalse(testFile("fixtures/public/fail/syntactical/MissingRightPar.jmm",  Stage.Syntatic));
    }

    @Test
    public void testMultipleSequential(){
        assertFalse(testFile("fixtures/public/fail/syntactical/MultipleSequential.jmm",  Stage.Syntatic));
    }

    @Test
    public void testNestedLoop(){
        assertFalse(testFile("fixtures/public/fail/syntactical/NestedLoop.jmm", Stage.Syntatic));
    }

}
