package stages;

import org.specs.comp.ollir.*;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.jasmin.JasminBackend;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.Stage;

import javax.print.DocFlavor;
import java.util.*;

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
    private int maxLimitStack = 0;
    private int currentStack = 0;
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

            System.out.println(jasminCode);
            return new JasminResult(ollirResult, jasminCode, reports);

        } catch (OllirErrorException e) {
            return new JasminResult(ollirClass.getClassName(), null,
                    Arrays.asList(Report.newError(Stage.GENERATION, -1, -1, "Exception during Jasmin generation", e)));
        }

    }

    private void AddRemoveFromStack(int number){
        currentStack += number;
        if(currentStack > maxLimitStack){
            maxLimitStack = currentStack;
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
        stringBuilder.append(Objects.requireNonNullElse(symbolTable.getSuper(), ".super java/lang/Object")).append("\n");

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
        stringBuilder.append(dealWithType(field.getFieldType(), true));

        return stringBuilder.toString();
    }

    // Functions to deal with a class method's declaration

    private String dealWithConstructorMethod(){
        return ".method public <init>()V\n" +
               "    aload_0\n" +
               "    invokespecial java/lang/Object/<init>()V\n" +
               "    return\n" +
               ".end method";
     }

    private String dealWithMethod(Method method){
        StringBuilder stringBuilder = new StringBuilder();

        maxLimitStack = 0;
        currentStack = 0;

        stringBuilder.append(".method ");

        // access-spec
        stringBuilder.append(dealWithAccessSpec(method.getMethodAccessModifier(), method.isStaticMethod(), method.isFinalMethod()));

        // method-spec (name(parameters)returnType)
        stringBuilder.append(method.getMethodName());
        stringBuilder.append("(");

        ArrayList<Element> parameters = method.getParams();

        // method parameters
        for(int i = 0; i < parameters.size(); i++){
            stringBuilder.append(dealWithType(parameters.get(i).getType(), true));
        }

        stringBuilder.append(")").append(dealWithType(method.getReturnType(), true)).append("\n");


        StringBuilder instructions = new StringBuilder();

        boolean returnInstruction = false;

        // Deal with method instructions
        for(Instruction instruction : method.getInstructions()){
            instructions.append(dealWithInstruction(method, instruction)).append("\n");
            if(instruction.getInstType() == InstructionType.RETURN)
                returnInstruction = true;
        }


        // limits
        stringBuilder.append("\t.limit stack ").append(maxLimitStack).append("\n");

        int localNumber = method.getVarTable().values().size();

        if(!method.getMethodName().equals("main")){
            localNumber++;
        }

        stringBuilder.append("\t.limit locals ").append(localNumber).append("\n\n");

        stringBuilder.append(instructions);

        //TODO see if we can do this or if have to check if the return type of the method is void
        if(!returnInstruction)
            stringBuilder.append("\treturn\n");


        // method end
        stringBuilder.append(".end method");

        return stringBuilder.toString();
    }

    // Functions to deal with each type of instructions

    private String dealWithInstruction(Method method, Instruction instruction){
        StringBuilder stringBuilder = new StringBuilder();

        for (String label : method.getLabels(instruction)) {
            stringBuilder.append("\t").append(label).append(":\n");
        }

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
                GotoInstruction gotoInstruction = (GotoInstruction) instruction;
                stringBuilder.append(dealWithGotoInstruction(method,gotoInstruction));
            }
            case BRANCH -> {
                CondBranchInstruction condBranchInstruction = (CondBranchInstruction) instruction;
                stringBuilder.append(dealWithCondBranchInstruction(method, condBranchInstruction));
            }
            case RETURN -> {
                ReturnInstruction returnInstruction = (ReturnInstruction) instruction;
                stringBuilder.append(dealWithReturnInstruction(method, returnInstruction));
            }
            case PUTFIELD -> {
                PutFieldInstruction putFieldInstruction = (PutFieldInstruction)  instruction;
                stringBuilder.append(dealWithPutFieldInstruction(method, putFieldInstruction));
            }
            case GETFIELD -> {
                GetFieldInstruction getFieldInstruction = (GetFieldInstruction)  instruction;
                stringBuilder.append(dealWithGetFieldInstruction(method, getFieldInstruction));
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

        try {
            ArrayOperand arrayOperand = (ArrayOperand) assignInstruction.getDest();
            stringBuilder.append(dealWithArrayOperandAssignInstruction(method, arrayOperand, rhs));
        }
        catch (Exception e){
            stringBuilder.append(dealWithInstruction(method, rhs));
            // store call value
            Operand dest = (Operand) assignInstruction.getDest();
            stringBuilder.append(dealWithStoreInstruction(method, dest));
        }
        return stringBuilder.toString();
    }

    private String dealWithArrayOperandAssignInstruction(Method method, ArrayOperand arrayOperand, Instruction rhs){
        StringBuilder stringBuilder = new StringBuilder();

        if (arrayOperand.getIndexOperands().size() > 0) {
            stringBuilder.append("\taload");
            AddRemoveFromStack(1);
            int register = getVarVirtualRegister(method, arrayOperand.getName());
            if(0<= register && register <= 3){
                stringBuilder.append("_");
            }
            else{
                stringBuilder.append(" ");
            }
            stringBuilder.append(register).append("\n");
            stringBuilder.append(dealWithLoadInstruction(method, (Operand) arrayOperand.getIndexOperands().get(0)));
            stringBuilder.append(dealWithInstruction(method, rhs));
            stringBuilder.append("\tiastore\n");
            AddRemoveFromStack(-3);
        }

        return stringBuilder.toString();
    }


    private String dealWithCallInstruction(Method method, CallInstruction callInstruction){
        StringBuilder stringBuilder = new StringBuilder();

        Operand firstArg = (Operand) callInstruction.getFirstArg();
        CallType callType = callInstruction.getInvocationType();
        switch (callType){
            case invokestatic, invokevirtual -> {

                // push into the stack the reference of the object
                if(callType == CallType.invokevirtual)
                    stringBuilder.append(dealWithLoadInstruction(method, firstArg));
                // push into the stack the method parameters
                for(Element parameterElement : callInstruction.getListOfOperands()){
                    stringBuilder.append(dealWithElementPush(method, parameterElement));
                }

                // Type of method invocation
                stringBuilder.append("\t").append(OllirAccesser.getCallInvocation(callInstruction).toString()).append(" ");

                //TODO check if this is the correct wat to do it
                if (callType == CallType.invokestatic) {
                    stringBuilder.append(firstArg.getName()).append(".");
                } else {
                    stringBuilder.append(dealWithType(firstArg.getType(), false)).append(".");
                }

                // function name
                // for some reason the function comes with as a string like ""functionName""
                LiteralElement secondArg = (LiteralElement) callInstruction.getSecondArg();
                stringBuilder.append(stripChars(secondArg.getLiteral(), "\"")).append("(");

                // function parameters
                for(Element element : callInstruction.getListOfOperands()){
                    stringBuilder.append(dealWithType(element.getType(), true));
                }

                stringBuilder.append(")");

                // function return type
                stringBuilder.append(dealWithType(callInstruction.getReturnType(), true)).append("\n");

                // push into the stack the reference of the object
                if(callInstruction.getReturnType().getTypeOfElement() != ElementType.VOID){
                    AddRemoveFromStack(1);
                }

                if(callType == CallType.invokevirtual)
                    AddRemoveFromStack(-1);
                AddRemoveFromStack(-callInstruction.getListOfOperands().size());

            }
            case invokespecial -> {

                // load value
                stringBuilder.append(dealWithLoadInstruction(method, firstArg));

                // dup value
                stringBuilder.append("\tdup\n");
                AddRemoveFromStack(1);

                // Type of method invocation
                stringBuilder.append("\t").append(callInstruction.getInvocationType().toString()).append(" ");

                stringBuilder.append(dealWithType(firstArg.getType(), false)).append(".");
                // function name
                LiteralElement secondArg = (LiteralElement) callInstruction.getSecondArg();
                stringBuilder.append(secondArg.getLiteral().toString().replace("\"", "")).append("(");

                stringBuilder.append(")");

                // function return type
                stringBuilder.append(dealWithType(callInstruction.getReturnType(), true)).append("\n");
                AddRemoveFromStack(-1);

                // store value
                stringBuilder.append(dealWithStoreInstruction(method, firstArg));
            }
            case NEW -> {
                Operand newOperand = (Operand) callInstruction.getFirstArg();
                if (newOperand.getType().getTypeOfElement().equals(ElementType.ARRAYREF)) {
                    stringBuilder.append(dealWithElementPush(method, callInstruction.getListOfOperands().get(0)));
                    stringBuilder.append("\tnewarray int\n");
                } else {
                    stringBuilder.append("\tnew ").append(newOperand.getName()).append("\n");
                    AddRemoveFromStack(1);
                }
            }
            case arraylength -> {
                // load value array
                stringBuilder.append(dealWithLoadInstruction(method, firstArg));
                stringBuilder.append("\tarraylength\n");
            }
            //TODO check if we will have any call instruction of this kind
            case ldc -> {
            }
        }
        return stringBuilder.toString();
    }

    private String dealWithGotoInstruction(Method method, GotoInstruction gotoInstruction){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < currentStack; i++){
            stringBuilder.append("\tpop\n");
        }
        stringBuilder.append("\tgoto " + gotoInstruction.getLabel() + "\n");
        return stringBuilder.toString();
    }

    private String dealWithCondBranchInstruction(Method method, CondBranchInstruction condBranchInstruction){ StringBuilder stringBuilder = new StringBuilder();

        Operation operation = condBranchInstruction.getCondOperation();

        if(operation.getOpType() != OperationType.NOT && operation.getOpType() != OperationType.NOTB){
            stringBuilder.append(dealWithElementPush(method, condBranchInstruction.getLeftOperand()));
        }
        stringBuilder.append(dealWithElementPush(method, condBranchInstruction.getRightOperand()));


        switch (condBranchInstruction.getCondOperation().getOpType()){
            case AND, ANDB -> {
                stringBuilder.append("\tif_icmpeq ");
                AddRemoveFromStack(-2);
            }
            case NOT, NOTB -> {
                stringBuilder.append("\tineg\n");
                stringBuilder.append("\tifeq ");
                AddRemoveFromStack(-1);
            }
            case GTE -> {
                stringBuilder.append("\tif_icmpge ");
                AddRemoveFromStack(-2);
            }
            case LTH -> {
                stringBuilder.append("\tif_icmplt ");
                AddRemoveFromStack(-2);
            }
            case OR, ORB -> {
                stringBuilder.append("\tior\n");
                AddRemoveFromStack(-1);
                stringBuilder.append("\tifne ");
                AddRemoveFromStack(-1);

            }
        }

        stringBuilder.append(condBranchInstruction.getLabel()).append("\n");

        return stringBuilder.toString();
    }

    private String dealWithReturnInstruction(Method method, ReturnInstruction returnInstruction){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(dealWithElementPush(method, returnInstruction.getOperand()));

        stringBuilder.append("\t").append(dealWithStoreLoadReturnType(returnInstruction.getOperand().getType()));
        stringBuilder.append("return\n");
        return stringBuilder.toString();
    }

    private String dealWithPutFieldInstruction(Method method, PutFieldInstruction putFieldInstruction){
        StringBuilder stringBuilder = new StringBuilder();

        Operand firstOperand = (Operand) putFieldInstruction.getFirstOperand();
        Operand secondOperand = (Operand) putFieldInstruction.getSecondOperand();
        Element thirdOperand = (Element) putFieldInstruction.getThirdOperand();

        stringBuilder.append(dealWithElementPush(method, firstOperand));

        if(thirdOperand != null){
            stringBuilder.append(dealWithElementPush(method, thirdOperand));
        }

        stringBuilder.append("\t").append("putfield ").append(secondOperand.getName()).append(" ").append(dealWithType(secondOperand.getType(), true)).append("\n");

        AddRemoveFromStack(+1);

        return stringBuilder.toString();
    }

    private String dealWithGetFieldInstruction(Method method, GetFieldInstruction getFieldInstruction){
        StringBuilder stringBuilder = new StringBuilder();

        Operand firstOperand = (Operand) getFieldInstruction.getFirstOperand();
        Operand secondOperand = (Operand) getFieldInstruction.getSecondOperand();

        stringBuilder.append(dealWithElementPush(method, firstOperand));
        stringBuilder.append("\t").append("getfield ").append(dealWithType(secondOperand.getType(), true)).append(" " + secondOperand.getName()).append("\n");

        return stringBuilder.toString();
    }

    private String dealWithUnaryOpInstruction(Method method, UnaryOpInstruction unaryOpInstruction){
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(dealWithElementPush(method, unaryOpInstruction.getRightOperand()));

        Operation operation = unaryOpInstruction.getUnaryOperation();

        switch (operation.getOpType()){
            case NOT -> {
                stringBuilder.append("\tineg\n");
                AddRemoveFromStack(-1);
            }
        }

        return stringBuilder.toString();
    }

    private String dealWithBinaryOpInstruction(Method method, BinaryOpInstruction binaryOpInstruction){
        StringBuilder stringBuilder = new StringBuilder();

        Operation operation = binaryOpInstruction.getUnaryOperation();

        if(operation.getOpType() != OperationType.NOT && operation.getOpType() != OperationType.NOTB){
            stringBuilder.append(dealWithElementPush(method, binaryOpInstruction.getLeftOperand()));
        }
        stringBuilder.append(dealWithElementPush(method, binaryOpInstruction.getRightOperand()));

        switch (operation.getOpType()) {
            case ADD -> {
                stringBuilder.append("\tiadd\n");
                AddRemoveFromStack(-1);
            }
            case SUB -> {
                stringBuilder.append("\tisub\n");
                AddRemoveFromStack(-1);
            }
            case MUL -> {
                stringBuilder.append("\timul\n");
                AddRemoveFromStack(-1);
            }
            case DIV -> {
                stringBuilder.append("\tidiv\n");
                AddRemoveFromStack(-1);
            }
            case AND, ANDB -> {
                stringBuilder.append("\tiand\n");
                AddRemoveFromStack(-1);
            }
            case NOT, NOTB -> {
                stringBuilder.append("\tineg\n");
            }
            case LTH -> {
                String labelFalse = "LabelFalse" + binaryOpInstruction.getId();
                String labelContinue = "LabelContinue" + binaryOpInstruction.getId();
                stringBuilder.append("\tif_icmpge ").append(labelFalse).append("\n");
                stringBuilder.append("\ticonst_1\n");
                stringBuilder.append("\tgoto ").append(labelContinue).append("\n");
                stringBuilder.append("\t").append(labelFalse).append(":\n");
                stringBuilder.append("\ticonst_0\n");
                stringBuilder.append("\t").append(labelContinue).append(":\n");
                AddRemoveFromStack(-1);
            }
            case OR, ORB -> {
                stringBuilder.append("\tior\n");
                AddRemoveFromStack(-1);
            }
        }


        return stringBuilder.toString();
    }

    private String dealWithSingleOpInstruction(Method method, SingleOpInstruction instruction){
        return dealWithElementPush(method, instruction.getSingleOperand());
    }

    private String dealWithElementPush(Method method, Element element){
        StringBuilder stringBuilder = new StringBuilder();

        try{
            ArrayOperand arrayOperand = (ArrayOperand) element;
            stringBuilder.append("\taload");
            int register = getVarVirtualRegister(method, arrayOperand.getName());
            if(0<= register && register <= 3){
                stringBuilder.append("_");
            }
            else{
                stringBuilder.append(" ");
            }
            stringBuilder.append(register).append("\n");

            //puts the array into the stack (+1)
            AddRemoveFromStack(1);

            stringBuilder.append(dealWithLoadInstruction(method, (Operand) arrayOperand.getIndexOperands().get(0)));
            stringBuilder.append("\tiaload\n");

            // iaload takes 2 elements from the stack (-2) and puts 1 element in stack (+1)
            AddRemoveFromStack(-1);
        }
        catch (Exception ignored){
            if(element.isLiteral()){
                return dealWithLiteralElementPush(method, (LiteralElement) element);
            }
            Operand parameterOperand = (Operand) element;
            stringBuilder.append(dealWithLoadInstruction(method, parameterOperand));
        }

        return stringBuilder.toString();
    }

    private String dealWithLiteralElementPush(Method method, LiteralElement element){

        StringBuilder stringBuilder = new StringBuilder();

        int integer = Integer.parseInt(element.getLiteral());

        if(0 <= integer && integer <= 5 )
            stringBuilder.append("\ticonst_");
        else
            stringBuilder.append("\tldc ");

        stringBuilder.append(integer).append("\n");

        // Basically the same as aload but for integer so it put (+1) in the stack
        AddRemoveFromStack(1);

        return stringBuilder.toString();
    }

    private String dealWithStoreInstruction(Method method, Operand dest){
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("\t").append(dealWithStoreLoadReturnType(dest.getType()));
        stringBuilder.append("store");

        int register = getVarVirtualRegister(method, dest.getName());
        if(0<= register && register <= 3){
            stringBuilder.append("_");
        }
        else{
            stringBuilder.append(" ");
        }
        stringBuilder.append(register).append("\n");

        AddRemoveFromStack(-1);

        return stringBuilder.toString();
    }

    private String dealWithLoadInstruction(Method method, Operand origin){
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("\t").append(dealWithStoreLoadReturnType(origin.getType()));
        stringBuilder.append("load");

        int register = getVarVirtualRegister(method, origin.getName());
        if(0<= register && register <= 3){
            stringBuilder.append("_");
        }
        else{
            stringBuilder.append(" ");
        }
        stringBuilder.append(register).append("\n");

        AddRemoveFromStack(1);

        return stringBuilder.toString();
    }

    private String dealWithAccessSpec(AccessModifiers modifier, boolean staticField, boolean finalField){
        StringBuilder stringBuilder = new StringBuilder();

        switch (modifier) {
            case DEFAULT, PUBLIC -> stringBuilder.append("public ");
            case PRIVATE -> stringBuilder.append("private ");
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
    
    private String dealWithType(Type type, boolean objectRef){
        StringBuilder stringBuilder = new StringBuilder();
        switch (type.getTypeOfElement()){
            case ARRAYREF -> {
                ArrayType arrayType = (ArrayType) type;
                stringBuilder.append("[");

                if(arrayType.getTypeOfElements() == ElementType.STRING){
                    stringBuilder.append("Ljava/lang/String;");
                }
                else{
                    stringBuilder.append("I");
                }
            }
            case CLASS -> {
                ClassType classType = (ClassType) type;
                for(String string : symbolTable.getImports()) {
                    List<String> tokens = Arrays.asList(string.split("\\.").clone());

                    if(tokens.size() == 0)
                        tokens.add(string);

                    if (!tokens.contains(classType.getName()))
                        continue;

                    stringBuilder.append(string).append(";");
                    break;
                }


            }
            case THIS-> {
                ClassType classType = (ClassType) type;
                stringBuilder.append(classType.getName());
            }
            case OBJECTREF -> {
                ClassType classType = (ClassType) type;
                if(objectRef) {
                    stringBuilder.append("L").append(classType.getName()).append(";");
                }
                else{
                    stringBuilder.append(classType.getName());
                }
            }
            case STRING -> {
                stringBuilder.append("LJava/Lang/String;");
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
