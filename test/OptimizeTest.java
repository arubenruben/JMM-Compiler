import org.junit.Test;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.specs.util.SpecsIo;

import static org.junit.Assert.assertEquals;

/**
 * Copyright 2021 SPeCS.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

public class OptimizeTest {
    public void testFile(String filePath, boolean mustFail) {
        OllirResult result;

        result = TestUtils.optimize(SpecsIo.getResource(filePath));

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
    public void testHelloWorld() {
        testFile("fixtures/public/HelloWorld.jmm", false);
    }

    @Test
    public void testSimple() {
        testFile("fixtures/public/Simple.jmm", false);
    }

    @Test
    public void testFig1() {
        testFile("custom/optimization/Fig1.jmm", false);
    }

    @Test
    public void testFig2() {
        testFile("custom/optimization/Fig2.jmm", false);
    }

    @Test
    public void testFig3() {
        testFile("custom/optimization/Fig3.jmm", false);
    }

    @Test
    public void testFig4() {
        testFile("custom/optimization/Fig4.jmm", false);
    }

    @Test
    public void testFig5() {
        testFile("custom/optimization/Fig5.jmm", false);
    }

    @Test
    public void testFindMaximum() {
        testFile("custom/optimization/FindMaximum.jmm", false);
    }

    @Test
    public void testLife() {
        testFile("custom/optimization/LifeCustom.jmm", false);
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
    public void testTicTacToe() {
        testFile("custom/optimization/TicTacToe.jmm", false);
    }

    @Test
    public void testWhileAndIF() {
        testFile("fixtures/public/WhileAndIF.jmm", false);
    }


    //Custom Tests
    @Test
    public void testAssignmentOk() {
        testFile("custom/optimization/AssignmentTest.jmm", false);
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
    public void testConditionWithArrayOK() {
        testFile("custom/semantic/type_verification/test_condition_with_array_ok.jmm", false);
    }


    @Test
    public void testConditionPrecedence() {
        testFile("custom/semantic/type_verification/test_condition_precedence.jmm", false);
    }
    
    @Test
    public void testLoopUnrolling() {
        var result = TestUtils.optimize(SpecsIo.getResource("custom/optimization/test_loop_unrolling.jmm"), true);
        var output = TestUtils.backend(result).run();
        assertEquals("50", output.trim());
    }

    @Test
    public void testConstantFolding() {
        var result = TestUtils.optimize(SpecsIo.getResource("custom/optimization/test_constant_propagation.jmm"), true);
        var output = TestUtils.backend(result).run();
        assertEquals("0", output.trim());
    }

    @Test
    public void testExpressionSimplification() {
        var result = TestUtils.optimize(SpecsIo.getResource("custom/optimization/test_expression_simplification.jmm"), true);
        var output = TestUtils.backend(result).run();
        assertEquals("0", output.trim());
    }
}
