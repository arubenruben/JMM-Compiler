package stages;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.analysis.JmmAnalysis;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.ast.AJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import symbols.SymbolTableIml;
import visitors.semantic.FirstVisitor;
import visitors.semantic.SecondVisitor;
import visitors.semantic.ThirdVisitor;
import visitors.semantic.helpers.data_helpers.SecondVisitorHelper;
import visitors.semantic.helpers.data_helpers.VisitorDataHelper;

import java.util.ArrayList;
import java.util.List;

public class AnalysisStage implements JmmAnalysis {

    @Override
    public JmmSemanticsResult semanticAnalysis(JmmParserResult parserResult) {

        // Convert Simple node to JmmNodeIml
        JmmNode node = parserResult.getRootNode().sanitize();

        SymbolTableIml symbolTable = new SymbolTableIml();
        List<Report> reportList = new ArrayList<>(parserResult.getReports());

        AJmmVisitor<VisitorDataHelper, Boolean> firstVisitor = new FirstVisitor();
        firstVisitor.visit(node, new VisitorDataHelper(symbolTable, reportList));

        for (String methodName : symbolTable.getMethodsHashmap().keySet()) {
            AJmmVisitor<SecondVisitorHelper, Boolean> secondVisitor = new SecondVisitor();
            secondVisitor.visit(symbolTable.getMethodsHashmap().get(methodName).getNode(), new SecondVisitorHelper(methodName, symbolTable, reportList));
        }
        for (String methodName : symbolTable.getMethodsHashmap().keySet()) {
            AJmmVisitor<SecondVisitorHelper, Boolean> thirdVisitor = new ThirdVisitor();
            thirdVisitor.visit(symbolTable.getMethodsHashmap().get(methodName).getNode(), new SecondVisitorHelper(methodName, symbolTable, reportList));
        }
        return new JmmSemanticsResult(node, symbolTable, reportList);

    }

}
