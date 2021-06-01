import org.junit.Test;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.specs.util.SpecsIo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    public void testIinc() {
        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/specific/test_iinc.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("1", output.trim());
    }

    @Test
    public void testLife() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/Simple.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("30", output.trim());
    }


    @Test
    public void testIfCondition() {
        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/specific/test_if_condition.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("10" + System.lineSeparator() +
                "5" + System.lineSeparator() +
                "0" + System.lineSeparator() +
                "2" + System.lineSeparator() +
                "5", output.trim());
    }

    @Test
    public void tesClassFields() {
        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/specific/test_class_fields.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("5", output.trim());
    }

    @Test
    public void testExtends() {
        TestUtils.backend(SpecsIo.getResource("custom/backend/specific/test_extends_1.jmm")).compile(new File("tmp"));

        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/specific/test_extends_2.jmm"));
        TestUtils.noErrors(result.getReports());
        final String classPath = TestUtils.getLibsClasspath();
        final List<String> classPathFinal = new ArrayList<>();
        classPathFinal.add(classPath);
        classPathFinal.add("./tmp");
        var output = result.run(new ArrayList<>(), classPathFinal, "");
        assertEquals("12" + System.lineSeparator() +
                "16", output.trim());
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
        assertEquals("11" + System.lineSeparator() +
                "6" + System.lineSeparator() +
                "21" + System.lineSeparator() +
                "2" + System.lineSeparator() +
                "8" + System.lineSeparator() +
                "120" + System.lineSeparator() +
                "15" + System.lineSeparator() +
                "18" + System.lineSeparator() +
                "27", output.trim());
    }

    @Test
    public void testMethodOverload() {
        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/top/extras/MethodOverload.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("1" + System.lineSeparator() +
                "1" + System.lineSeparator() +
                "0" + System.lineSeparator() +
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
        assertEquals("10" + System.lineSeparator() +
                "100" + System.lineSeparator() +
                "1000" + System.lineSeparator() +
                "1" + System.lineSeparator() +
                "1" + System.lineSeparator() +
                "1", output.trim());
    }

    @Test
    public void testArrays() {
        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/top/generic/ArrayLibrary.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("01234567890123456789" + System.lineSeparator() +
                "1" + System.lineSeparator() +
                "012" + System.lineSeparator() +
                "3" + System.lineSeparator() +
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
        var output = result.run("12" + System.lineSeparator() + "13");
        assertEquals("25", output.trim());
    }

    @Test
    public void testOverall4() {
        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/overall/overall4.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run("12" + System.lineSeparator());
        assertEquals("12", output.trim());
    }

    @Test
    public void testOverall5() {
        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/overall/overall5.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("3" + System.lineSeparator() +
                "3" + System.lineSeparator() +
                "3" + System.lineSeparator() +
                "3" + System.lineSeparator() +
                "3", output.trim());
    }
}
