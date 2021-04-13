import org.junit.Test;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.specs.util.SpecsIo;

public class SemanticTest {
    public void testFile(String filePath, boolean mustFail) {
        JmmSemanticsResult result;
        String code;
        code = SpecsIo.read("test/" + filePath);
        result = TestUtils.analyse(TestUtils.parse(code));

        if (result.getReports().size() > 0) {
            System.out.println("Test file name: " + filePath.split("/")[filePath.split("/").length - 1]);
            Utils.printReports(result.getReports());
        }

        if (mustFail) {
            TestUtils.mustFail(result.getReports());
        } else {
            TestUtils.noErrors(result.getReports());
        }
    }

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
        testFile("fixtures/public/Lazysort.jmm", true);
    }

    @Test
    public void testLife() {
        testFile("fixtures/public/Life.jmm", false);
    }

    @Test
    public void testMonteCarloPi() {
        testFile("fixtures/public/MonteCarloPi.jmm", false);
    }


    /*TODO:Allow method overload
    @Test
    public void testQuickSort() {
        testFile("fixtures/public/QuickSort.jmm", true);
    }

     */

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
        testFile("fixtures/public/fail/semantic/var_undef.jmm", false);
    }

    @Test
    public void testNotAssignedVariable() {
        testFile("fixtures/public/fail/semantic/varNotInit.jmm", false);
    }

    //Custom Tests
    @Test
    public void testDifferentOperandTypes() {
        testFile("custom/semantic/type_verification/test_op_same_type.jmm", true);
    }

    @Test
    public void testDirectArrayOperations() {
        testFile("custom/semantic/type_verification/test_direct_array_ops.jmm", true);
    }

    @Test
    public void testDirectIntegerArrayAccess() {
        testFile("custom/semantic/type_verification/test_array_access.jmm", true);
    }

    @Test
    public void testArrayIndex1() {
        testFile("custom/semantic/type_verification/test_array_access_index.jmm", true);
    }

    @Test
    public void testAssigmentOk() {
        testFile("custom/semantic/type_verification/test_assignment_ok.jmm", false);
    }

    @Test
    public void testAssigmentFail() {
        testFile("custom/semantic/type_verification/test_assignment_fail.jmm", true);
    }

    @Test
    public void testTarget1() {
        testFile("custom/semantic/method_verification/test_existence_target.jmm", true);
    }

    @Test
    public void testTarget2() {
        testFile("custom/semantic/method_verification/test_existence_target_with_super.jmm", false);
    }
    @Test
    public void testTargetWithImport() {
        testFile("custom/semantic/method_verification/test_existence_import.jmm", false);
    }
    @Test
    public void testMethodParameters() {
        testFile("custom/semantic/method_verification/test_parameters_number.jmm", true);
    }
    @Test
    public void testNumberParameters() {
        testFile("custom/semantic/method_verification/test_number_parameters.jmm", true);
    }
}
