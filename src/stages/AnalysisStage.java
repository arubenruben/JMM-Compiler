package stages;

import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.analysis.JmmAnalysis;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.ast.AJmmVisitor;
import pt.up.fe.comp.jmm.ast.examples.ExamplePostorderVisitor;
import pt.up.fe.comp.jmm.ast.examples.ExamplePreorderVisitor;
import pt.up.fe.comp.jmm.ast.examples.ExamplePrintVariables;
import pt.up.fe.comp.jmm.ast.examples.ExampleVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;
import symbols.SymbolTableIml;
import visitors.FirstVisitor;
import visitors.SecondVisitor;
import visitors.ThirdVisitor;
import visitors.helpers.data_helpers.SecondVisitorHelper;
import visitors.helpers.data_helpers.VisitorDataHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
            secondVisitor.visit(symbolTable.getNodeMap().get(methodName), new SecondVisitorHelper(methodName, symbolTable, reportList));
        }
        for (String methodName : symbolTable.getMethodsHashmap().keySet()) {
            AJmmVisitor<SecondVisitorHelper, Boolean> thirdVisitor = new ThirdVisitor();
            thirdVisitor.visit(symbolTable.getNodeMap().get(methodName), new SecondVisitorHelper(methodName, symbolTable, reportList));
        }

        return new JmmSemanticsResult(node, symbolTable, reportList);

    }

}
