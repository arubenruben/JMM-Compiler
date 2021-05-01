package stages;

import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ollir.JmmOptimization;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.report.Report;
import symbols.MethodSymbol;
import symbols.SymbolTableIml;

import java.util.ArrayList;
import java.util.List;

public class OptimizationStage implements JmmOptimization {
    private SymbolTableIml symbolTable;


    @Override
    public OllirResult toOllir(JmmSemanticsResult semanticsResult) {
        // Convert the AST to a String containing the equivalent OLLIR code
        symbolTable = (SymbolTableIml) semanticsResult.getSymbolTable();
        String ollirCode = ollirCodeString();

        System.out.println(ollirCode);

        // More reports from this stage
        List<Report> reports = new ArrayList<>();

        // Fac {} must be replaced by ollirCode
        return new OllirResult(semanticsResult, ollirCode, reports);
    }

    private String ollirCodeString() {
        StringBuilder code = new StringBuilder();

        code.append(dealWithClassHeaders());

        for (MethodSymbol method : symbolTable.getMethodsHashmap().values()) {
            code.append(dealWithMethod(method));
            code.append("\n");
        }

        code.append(dealWithFooter());

        return code.toString();
    }

    @Override
    public JmmSemanticsResult optimize(JmmSemanticsResult semanticsResult) {
        // THIS IS JUST FOR CHECKPOINT 3
        return semanticsResult;
    }

    @Override
    public OllirResult optimize(OllirResult ollirResult) {
        // THIS IS JUST FOR CHECKPOINT 3
        return ollirResult;
    }

    //------Deal With Things------

    private String dealWithClassHeaders() {
        StringBuilder code = new StringBuilder();

        code.append(symbolTable.getClassName()).append(" ").append("{").append("\n");
        code.append("\t").append(".construct ").append(symbolTable.getClassName()).append("().V").append("{").append("\n");
        code.append("\t\t").append("invokespecial").append("(this,").append("\"<init>\")").append(".V;").append("\n");
        code.append("\t").append("}");

        code.append("\n");

        return code.toString();
    }

    private String dealWithMethod(MethodSymbol method) {
        StringBuilder code = new StringBuilder();

        code.append("\t").append(dealWithMethodHeader(method));

        code.append(dealWithMethodBody(method));

        code.append("\t").append(dealWithFooter());

        return code.toString();
    }

    private String dealWithMethodBody(MethodSymbol method) {
        StringBuilder code = new StringBuilder();

        return code.toString();
    }

    private String dealWithMethodHeader(MethodSymbol method) {
        StringBuilder code = new StringBuilder();

        code.append(".method public ").append(method.getName()).append("(").append(dealWithMethodHeaderParameters(method.getParameters())).append(")").append(dealWithType(method.getType())).append(" ").append("{");

        code.append("\n");

        return code.toString();

    }

    private String dealWithMethodHeaderParameters(List<Symbol> parameters) {
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < parameters.size(); i++) {
            Symbol parameter = parameters.get(i);

            code.append(dealWithSymbol(parameter));

            if (i != parameters.size() - 1)
                code.append(", ");

        }

        return code.toString();
    }

    private String dealWithSymbol(Symbol symbol) {
        StringBuilder code = new StringBuilder();
        code.append(symbol.getName()).append(dealWithType(symbol.getType()));
        return code.toString();
    }

    private String dealWithType(Type type) {
        StringBuilder code = new StringBuilder();

        switch (type.getName()) {
            case "int" -> code.append(".i32");
            case "boolean" -> code.append(".bool");
            case "void" -> code.append(".V");
            default -> code.append(".").append(type.getName());
        }

        return code.toString();
    }


    private String dealWithFooter() {
        return "}";
    }


}
