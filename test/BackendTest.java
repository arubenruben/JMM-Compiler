import org.junit.Test;
import pt.up.fe.comp.TestUtils;
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

public class BackendTest {

    @Test
    public void testHelloWorld() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/HelloWorld.jmm"));
        TestUtils.noErrors(result.getReports());

        var output = result.run();
        assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testBinaryOperation() {
        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/specific/test_binary_operation.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("41283", output.trim());
    }

    @Test
    public void testInvokeStatic() {
        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/specific/test_invoke_static.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("Result: 3", output.trim());
    }

    @Test
    public void testArrayAccess() {
        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/specific/test_array_access.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("Result: 10", output.trim());
    }

    @Test
    public void testInvokeVirtual() {
        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/specific/test_invoke_virtual.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("8", output.trim());
    }

    @Test
    public void testIfCondition() {
        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/specific/test_if_condition.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("10\n" +
                "5\n" +
                "0\n" +
                "2\n" +
                "5", output.trim());
    }

    @Test
    public void testSimple() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/Simple.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("30", output.trim());
    }


    @Test
    public void testWhileAndIF() {
        testBackend("fixtures/public/WhileAndIF.jmm", "fixtures/public/WhileAndIF.txt");
    }

    @Test
    public void testSimpleCalculator() {
        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/top/generic/SimpleCalculator.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("11\n" +
                "6\n" +
                "21\n" +
                "2\n" +
                "8\n" +
                "120\n" +
                "15\n" +
                "18\n" +
                "27", output.trim());
    }

    @Test
    public void testMethodOverload() {
        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/top/extras/MethodOverload.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("1\n" +
                "1\n" +
                "0\n" +
                "0", output.trim());
    }


    public void testBackend(String filePath, String expectedPath) {
        var result = TestUtils.backend(SpecsIo.getResource(filePath));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        var expected = SpecsIo.getResource(expectedPath);
        assertEquals(expected, output.trim());
    }

    @Test
    public void testConvertor() {
        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/top/generic/ConvertorUnits.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("10\n" +
                "100\n" +
                "1000\n" +
                "1\n" +
                "1\n" +
                "1", output.trim());
    }

    @Test
    public void testArrays() {
        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/top/generic/ArrayLibrary.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("01234567890123456789\n" +
                "1\n" +
                "012\n" +
                "3\n" +
                "0123456789", output.trim());
    }

    @Test
    public void testOverall1() {
        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/overall/overall1.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("45", output.trim());
    }

    @Test
    public void testOverall2() {
        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/overall/overall2.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("0", output.trim());
    }

    @Test
    public void testOverall3() {
        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/overall/overall3.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run("12\n13");
        assertEquals("25", output.trim());
    }

    @Test
    public void testOverall4() {
        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/overall/overall4.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run("12\n");
        assertEquals("12", output.trim());
    }

    @Test
    public void testOverall5() {
        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/overall/overall5.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("5", output.trim());
    }


}
