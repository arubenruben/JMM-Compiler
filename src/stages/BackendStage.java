package stages;

import org.specs.comp.ollir.*;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.jasmin.JasminBackend;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Copyright 2021 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

public class BackendStage implements JasminBackend {

    private SymbolTable symbolTable;

    @Override
    public JasminResult toJasmin(OllirResult ollirResult) {
        symbolTable = ollirResult.getSymbolTable();
        ClassUnit ollirClass = ollirResult.getOllirClass();
        try {

            // Example of what you can do with the OLLIR class
            ollirClass.checkMethodLabels(); // check the use of labels in the OLLIR loaded
            ollirClass.buildCFGs(); // build the CFG of each method
            ollirClass.outputCFGs(); // output to .dot files the CFGs, one per method
            ollirClass.buildVarTables(); // build the table of variables for each method
            ollirClass.show(); // print to console main information about the input OLLIR

            // Convert the OLLIR to a String containing the equivalent Jasmin code
            String jasminCode = dealWithClass(ollirClass); // Convert node ...

            // More reports from this stage
            List<Report> reports = new ArrayList<>();

            return new JasminResult(ollirResult, jasminCode, reports);

        } catch (OllirErrorException e) {
            return new JasminResult(ollirClass.getClassName(), null,
                    Arrays.asList(Report.newError(Stage.GENERATION, -1, -1, "Exception during Jasmin generation", e)));
        }

    }

    private String dealWithClass(ClassUnit classUnit){
        StringBuilder stringBuilder = new StringBuilder();

        // Deal with class declaration
        stringBuilder.append(".class ");

        // access-spec
        stringBuilder.append(dealWithAccessSpec(classUnit.getClassAccessModifier(), classUnit.isStaticClass(), classUnit.isFinalClass()));

        // class name
        stringBuilder.append(classUnit.getClassName()).append("\n");

        // Deal with super
        stringBuilder.append(Objects.requireNonNullElse(symbolTable.getSuper(), ".super java.lang.object")).append("\n");

        stringBuilder.append("\n");

        // Deal with class field declaration
        for(Field field : classUnit.getFields()){
            stringBuilder.append(dealWithClassField(field)).append("\n");
        }

        stringBuilder.append("\n");

        // Deal with methods
        for (Method method: classUnit.getMethods()){
            stringBuilder.append(dealWithMethod(method)).append("\n\n");
        }

        return stringBuilder.toString();
    }

    private String dealWithClassField(Field field){
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(".field ");

        // <access-spec>
        stringBuilder.append(dealWithAccessSpec(field.getFieldAccessModifier(), field.isStaticField(), field.isFinalField()));

        // <field-name>
        stringBuilder.append(field.getFieldName()).append(" ");

        // <descriptor>
        stringBuilder.append(dealWithType(field.getFieldType()));

        return stringBuilder.toString();
    }


    private String dealWithMethod(Method method){
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(".method ");

        // access-spec
        stringBuilder.append(dealWithAccessSpec(method.getMethodAccessModifier(), method.isStaticMethod(), method.isFinalMethod()));

        // method-spec (name(parameters)returnType)
        if(method.isConstructMethod()){
            stringBuilder.append("<init>");
        }
        else {
            stringBuilder.append(method.getMethodName());
        }

        stringBuilder.append("(");

        // method parameters
        for(Element element : method.getParams()){
            stringBuilder.append(dealWithType(element.getType())).append(";");
        }

        stringBuilder.append(")").append(dealWithType(method.getReturnType())).append("\n");

        // limits
        stringBuilder.append(".limit stack 99\n").append(".limit locals 99\n");

        // instructions
        for(Instruction instruction : method.getInstructions()){
            stringBuilder.append(dealWithInstruction(instruction)).append("\n");
        }

        // method end
        stringBuilder.append(".end method");

        return stringBuilder.toString();
    }


    private String dealWithInstruction(Instruction instruction){
        StringBuilder stringBuilder = new StringBuilder();

        switch (instruction.getInstType()){

            case ASSIGN -> {

            }
            case CALL -> {
            }
            case GOTO -> {
            }
            case BRANCH -> {
            }
            case RETURN -> {
                stringBuilder.append("return");
            }
            case PUTFIELD -> {
            }
            case GETFIELD -> {
            }
            case UNARYOPER -> {
            }
            case BINARYOPER -> {
            }
            case NOPER -> {
            }
        }

        return stringBuilder.toString();
    }



    private String dealWithAccessSpec(AccessModifiers modifier, boolean staticField, boolean finalField){
        StringBuilder stringBuilder = new StringBuilder();

        switch (modifier) {
            case PUBLIC -> stringBuilder.append("public ");
            case DEFAULT, PRIVATE -> stringBuilder.append("private ");
            case PROTECTED -> stringBuilder.append("protected ");
        }

        if(staticField)
            stringBuilder.append("static ");

        if(finalField)
            stringBuilder.append("final ");

        return stringBuilder.toString();
    }

    private String dealWithType(Type type){
        StringBuilder stringBuilder = new StringBuilder();
        switch (type.getTypeOfElement()){
            case ARRAYREF -> {
                ArrayType arrayType = (ArrayType) type;
                stringBuilder.append("[");

                if(arrayType.getTypeOfElements() == ElementType.STRING){
                    stringBuilder.append("LJava/Lang/String");
                }
                else{
                    stringBuilder.append("I");
                }
            }
            case OBJECTREF -> {
                ClassType classType = (ClassType) type;

                for(String string : symbolTable.getImports()) {
                    List<String> tokens = Arrays.asList(string.split("\\.").clone());

                    if (!tokens.contains(classType.getName()))
                        continue;

                    stringBuilder.append(string);
                    break;
                }
            }
            case CLASS -> {
            }
            case STRING -> {
                stringBuilder.append("LJava/Lang/String");
            }
            case INT32 -> {
                stringBuilder.append("I");
            }
            case BOOLEAN -> {
                stringBuilder.append("Z");
            }
            case VOID -> {
                stringBuilder.append("V");
            }
        }
        return stringBuilder.toString();
    }



}
