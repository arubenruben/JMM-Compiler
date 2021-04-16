package stages;

import org.specs.comp.ollir.*;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.jasmin.JasminBackend;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.Stage;

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
    private ClassUnit ollirClass;
    @Override
    public JasminResult toJasmin(OllirResult ollirResult) {
        symbolTable = ollirResult.getSymbolTable();
        ollirClass = ollirResult.getOllirClass();
        try {

            // Example of what you can do with the OLLIR class
            ollirClass.checkMethodLabels(); // check the use of labels in the OLLIR loaded
            ollirClass.buildCFGs(); // build the CFG of each method
            ollirClass.outputCFGs(); // output to .dot files the CFGs, one per method
            ollirClass.buildVarTables(); // build the table of variables for each method
            ollirClass.show(); // print to console main information about the input OLLIR

            // Convert the OLLIR to a String containing the equivalent Jasmin code
            String jasminCode = dealWithClass(); // Convert node ...

            // More reports from this stage
            List<Report> reports = new ArrayList<>();

            return new JasminResult(ollirResult, jasminCode, reports);

        } catch (OllirErrorException e) {
            return new JasminResult(ollirClass.getClassName(), null,
                    Arrays.asList(Report.newError(Stage.GENERATION, -1, -1, "Exception during Jasmin generation", e)));
        }

    }

    // Function to deal with a class

    private String dealWithClass(){
        StringBuilder stringBuilder = new StringBuilder();

        // Deal with class declaration
        stringBuilder.append(".class ");

        // access-spec
        stringBuilder.append(dealWithAccessSpec(ollirClass.getClassAccessModifier(), ollirClass.isStaticClass(), ollirClass.isFinalClass()));

        // class name
        stringBuilder.append(ollirClass.getClassName()).append("\n");

        // Deal with super
        stringBuilder.append(Objects.requireNonNullElse(symbolTable.getSuper(), ".super java.lang.object")).append("\n");

        stringBuilder.append("\n");

        // Deal with class field declaration
        for(Field field : ollirClass.getFields()){
            stringBuilder.append(dealWithClassField(field)).append("\n");
        }

        stringBuilder.append("\n");

        // Deal with methods
        for (Method method: ollirClass.getMethods()){
            if(method.isConstructMethod()){
                stringBuilder.append(dealWithConstructorMethod()).append("\n\n");
                continue;
            }
            stringBuilder.append(dealWithMethod(method)).append("\n\n");
        }

        return stringBuilder.toString();
    }

    // Function to deal with the class fields

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

    // Functions to deal with a class method's declaration

    private String dealWithConstructorMethod(){
        return """
                .method public <init>()V
                    aload_0
                    invokespecial java/lang/Object/<init>()V
                    return
                .end method""";
     }

    private String dealWithMethod(Method method){
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(".method ");

        // access-spec
        stringBuilder.append(dealWithAccessSpec(method.getMethodAccessModifier(), method.isStaticMethod(), method.isFinalMethod()));

        // method-spec (name(parameters)returnType)
        stringBuilder.append(method.getMethodName());
        stringBuilder.append("(");

        // method parameters
        for(Element element : method.getParams()){
            stringBuilder.append(dealWithType(element.getType())).append(";");
        }

        stringBuilder.append(")").append(dealWithType(method.getReturnType())).append("\n");

        // limits
        stringBuilder.append("\t.limit stack 99\n").append("\t.limit locals 99\n\n");

        // Deal with method instructions
        for(Instruction instruction : method.getInstructions()){
            stringBuilder.append(dealWithInstruction(method, instruction)).append("\n");
        }

        // method end
        stringBuilder.append(".end method");

        return stringBuilder.toString();
    }

    // Functions to deal with each type of instructions

    private String dealWithInstruction(Method method, Instruction instruction){
        StringBuilder stringBuilder = new StringBuilder();

        switch (instruction.getInstType()){

            case ASSIGN -> {
                AssignInstruction assignInstruction = (AssignInstruction) instruction;
                stringBuilder.append(dealWithAssignInstruction(method, assignInstruction));
            }
            case CALL -> {
                CallInstruction callInstruction = (CallInstruction) instruction;
                stringBuilder.append(dealWithCallInstruction(method, callInstruction));
            }
            case GOTO -> {
            }
            case BRANCH -> {
            }
            case RETURN -> {
                ReturnInstruction returnInstruction = (ReturnInstruction) instruction;
                stringBuilder.append(dealWithReturnInstruction(method, returnInstruction));
            }
            case PUTFIELD -> {
            }
            case GETFIELD -> {
            }
            case UNARYOPER -> {
                UnaryOpInstruction unaryOpInstruction = (UnaryOpInstruction) instruction;
                stringBuilder.append(dealWithUnaryOpInstruction(method, unaryOpInstruction));
            }
            case BINARYOPER -> {
                BinaryOpInstruction binaryOpInstruction = (BinaryOpInstruction) instruction;
                stringBuilder.append(dealWithBinaryOpInstruction(method, binaryOpInstruction));
            }
            case NOPER -> {
                SingleOpInstruction singleOpInstruction = (SingleOpInstruction) instruction;
                stringBuilder.append(dealWithSingleOpInstruction(method, singleOpInstruction));
            }
        }

        return stringBuilder.toString();
    }

    private String dealWithAssignInstruction(Method method, AssignInstruction assignInstruction){
        StringBuilder stringBuilder = new StringBuilder();

        Instruction rhs = assignInstruction.getRhs();

        switch (rhs.getInstType()) {
            case CALL -> {
                CallInstruction callInstruction = (CallInstruction) rhs;
                stringBuilder.append(dealWithCallInstruction(method, callInstruction));
            }
            case NOPER -> {
                SingleOpInstruction singleOpInstruction = (SingleOpInstruction) rhs;
                stringBuilder.append(dealWithSingleOpInstruction(method, singleOpInstruction));
            }
            case BINARYOPER -> {
                BinaryOpInstruction binaryOpInstruction = (BinaryOpInstruction) rhs;
                stringBuilder.append(dealWithBinaryOpInstruction(method, binaryOpInstruction));
            }
        }

        // store call value
        Operand dest = (Operand) assignInstruction.getDest();
        stringBuilder.append("\t").append(dealWithStoreLoadReturnType(dest.getType()));
        stringBuilder.append("store_").append(getVarVirtualRegister(method, dest.getName())).append("\n");

        return stringBuilder.toString();
    }

    private String dealWithCallInstruction(Method method, CallInstruction callInstruction){
        StringBuilder stringBuilder = new StringBuilder();

        Operand firstArg = (Operand) callInstruction.getFirstArg();
        int virtualRegister = getVarVirtualRegister(method, firstArg.getName());

        CallType callType = OllirAccesser.getCallInvocation(callInstruction);

        switch (callType){
            case invokestatic, invokevirtual -> {

                // push into the stack the reference of the object
                if(callType == CallType.invokevirtual)
                    stringBuilder.append("\taload_").append(virtualRegister).append("\n");

                // push into the stack the method parameters
                for(Element parameterElement : callInstruction.getListOfOperands()){
                    stringBuilder.append(dealWithElementPush(method, parameterElement));
                }

                // Type of method invocation
                stringBuilder.append("\t").append(OllirAccesser.getCallInvocation(callInstruction).toString()).append(" ");

                // function name
                stringBuilder.append(dealWithType(firstArg.getType())).append(".");

                // for some reason the function comes with as a string like ""functionName""
                LiteralElement secondArg = (LiteralElement) callInstruction.getSecondArg();
                stringBuilder.append(stripChars(secondArg.getLiteral(), "\"")).append("(");

                // function parameters
                for(Element element : callInstruction.getListOfOperands()){
                    stringBuilder.append(dealWithType(element.getType())).append(";");
                }

                stringBuilder.append(")");

                // function return type
                stringBuilder.append(dealWithType(callInstruction.getReturnType())).append("\n");
            }
            case invokespecial -> {

                // load value
                stringBuilder.append("\taload_").append(virtualRegister).append("\n");

                // dup value
                stringBuilder.append("\tdup\n");

                // Type of method invocation
                stringBuilder.append("\t").append(OllirAccesser.getCallInvocation(callInstruction).toString()).append(" ");

                // function name
                LiteralElement secondArg = (LiteralElement) callInstruction.getSecondArg();
                stringBuilder.append(secondArg.getLiteral().toString()).append("(");

                stringBuilder.append(")");

                // function return type
                stringBuilder.append(dealWithType(callInstruction.getReturnType())).append("\n");

                // store value
                stringBuilder.append("\tastore_").append(getVarVirtualRegister(method, firstArg.getName())).append("\n");
            }
            case NEW -> {
                if(OllirAccesser.getCallInvocation(callInstruction) == CallType.NEW) {

                    Operand newOperand = (Operand) callInstruction.getFirstArg();

                    stringBuilder.append("\tnew ").append(newOperand.getName()).append("\n");
                }
            }
            case arraylength -> {
            }
            case ldc -> {
            }
        }
        return stringBuilder.toString();
    }

    //TODO
    private String dealWithGotoInstruction(Method method, GotoInstruction gotoInstruction){
        return "";
    }

    //TODO
    private String dealWithCondBranchInstruction(Method method, CondBranchInstruction condBranchInstruction){
        return "";
    }

    private String dealWithReturnInstruction(Method method, ReturnInstruction returnInstruction){
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(dealWithElementPush(method, returnInstruction.getOperand()));

        stringBuilder.append("\t").append(dealWithStoreLoadReturnType(returnInstruction.getOperand().getType()));
        stringBuilder.append("return\n");
        return stringBuilder.toString();
    }

    //TODO
    private String dealWithPutFieldInstruction(Method method, PutFieldInstruction putFieldInstruction){
        return "";
    }

    //TODO
    private String dealWithGetFieldInstruction(Method method, GetFieldInstruction getFieldInstruction){
        return "";
    }

    //TODO
    private String dealWithUnaryOpInstruction(Method method, UnaryOpInstruction unaryOpInstruction){
        return "";
    }

    private String dealWithBinaryOpInstruction(Method method, BinaryOpInstruction binaryOpInstruction){
        return "";
    }

    private String dealWithSingleOpInstruction(Method method, SingleOpInstruction instruction){
        return dealWithElementPush(method, instruction.getSingleOperand());
    }

    private String dealWithElementPush(Method method, Element element){
        StringBuilder stringBuilder = new StringBuilder();

        if(element.isLiteral()){
            LiteralElement parameterLiteral = (LiteralElement) element;
            stringBuilder.append("\ticonst_").append(parameterLiteral.getLiteral()).append("\n");
            return stringBuilder.toString();
        }

        Operand parameterOperand = (Operand) element;
        stringBuilder.append("\t").append(dealWithStoreLoadReturnType(parameterOperand.getType()));
        stringBuilder.append("load_").append(getVarVirtualRegister(method, parameterOperand.getName())).append("\n");
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

    private String dealWithStoreLoadReturnType(Type type){
        switch (type.getTypeOfElement()){
            case INT32, BOOLEAN -> {
                return "i";
            }
            case ARRAYREF, OBJECTREF, CLASS, THIS, STRING -> {
                return "a";
            }
        }
        return "a";
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

    // Auxiliary functions to get information from the var table of each method

    private int getVarVirtualRegister(Method method, String var){
        return OllirAccesser.getVarTable(method).get(var).getVirtualReg();
    }

    private VarScope getVarScope(Method method, String var){
        return OllirAccesser.getVarTable(method).get(var).getScope();
    }
    
    private Type getVarType(Method method, String var){
        return OllirAccesser.getVarTable(method).get(var).getVarType();
    }

    // Auxiliary functions string operations

    public static String stripChars(String input, String strip) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (strip.indexOf(c) == -1) {
                result.append(c);
            }
        }
        return result.toString();
    }

}
