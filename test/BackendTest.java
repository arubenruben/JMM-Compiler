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
        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/test_binary_operation.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("41283", output.trim());
    }

    @Test
    public void testInvokeStatic() {
        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/test_invoke_static.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("Result: 3", output.trim());
    }

    @Test
    public void testArrayAccess() {
        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/test_array_access.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("Result: 10", output.trim());
    }

    @Test
    public void testInvokeVirtual() {
        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/test_invoke_virtual.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("8", output.trim());
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
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/WhileAndIF.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("10\n" +
                "10\n" +
                "10\n" +
                "10\n" +
                "10\n" +
                "10\n" +
                "10\n" +
                "10\n" +
                "10\n" +
                "10", output.trim());
    }

    @Test
    public void testSimpleCalculator() {
        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/top/SimpleCalculator.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("11\n" +
                        "6\n" +
                        "21\n" +
                        "2\n" +
                        "8\n" +
                        "27"
                //        "12015189"
                , output.trim());
    }

    @Test
    public void testMethodOverload() {
        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/top/extras/MethodOverload.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("true\n" +
                "true\n" +
                "false\n" +
                "false", output.trim());
    }

    @Test
    public void testMethodOverload1() {
        var result = TestUtils.backend(SpecsIo.getResource("custom/backend/top/extras/MethodOverload1.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("10\n" +
                "8\n" +
                "true\n" +
                "false", output.trim());
    }

}
