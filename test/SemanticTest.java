import org.junit.Test;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.specs.util.SpecsIo;

public class SemanticTest {
    public void testFile(String filePath, boolean mustFail) {
        JmmSemanticsResult result;
        String code;
        code = SpecsIo.read("test/" + filePath);
        result = TestUtils.analyse(TestUtils.parse(code));

        if (result.getReports().size() > 0) {
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
        testFile("fixtures/public/FindMaximum.jmm", false);
    }

    @Test
    public void testHelloWorld() {
        testFile("fixtures/public/HelloWorld.jmm", false);
    }

    @Test
    public void testLazySort() {
        testFile("fixtures/public/Lazysort.jmm", false);
    }

    @Test
    public void testLife() {
        testFile("fixtures/public/Life.jmm", false);
    }

    @Test
    public void testMonteCarloPi() {
        testFile("fixtures/public/MonteCarloPi.jmm", false);
    }

    @Test
    public void testQuickSort() {
        testFile("fixtures/public/QuickSort.jmm", false);
    }

    @Test
    public void testSimple() {
        testFile("fixtures/public/Simple.jmm", false);
    }

    @Test
    public void testTicTacToe() {
        testFile("fixtures/public/TicTacToe.jmm", false);
    }

    @Test
    public void testWhileAndIF() {
        testFile("fixtures/public/WhileAndIF.jmm", false);
    }

    @Test
    public void testArrayIndex() {
        testFile("fixtures/public/fail/semantic/arr_index_not_int.jmm", true);
    }

    @Test
    public void testArrayNewIndex() {
        testFile("fixtures/public/fail/semantic/arr_size_not_int.jmm", true);
    }

    @Test
    public void testArrayBadArguments() {
        testFile("fixtures/public/fail/semantic/badArguments.jmm", true);
    }

    @Test
    public void testArrayBadOperands() {
        testFile("fixtures/public/fail/semantic/binop_incomp.jmm", true);
    }

    @Test
    public void testArrayNotFoundMethod() {
        testFile("fixtures/public/fail/semantic/funcNotFound.jmm", true);
    }

    @Test
    public void testIllegalLength() {
        testFile("fixtures/public/fail/semantic/simple_length.jmm", true);
    }

    @Test
    public void testIllegalAssignment() {
        testFile("fixtures/public/fail/semantic/var_exp_incomp.jmm", true);
    }

    @Test
    public void testIllegalAssignment2() {
        testFile("fixtures/public/fail/semantic/var_lit_incomp.jmm", true);
    }

    @Test
    public void testNotDefineVarAssignment() {
        testFile("fixtures/public/fail/semantic/var_undef.jmm", true);
    }

    @Test
    public void testNotAssignedVariable() {
        testFile("fixtures/public/fail/semantic/varNotInit.jmm", true);
    }


}
