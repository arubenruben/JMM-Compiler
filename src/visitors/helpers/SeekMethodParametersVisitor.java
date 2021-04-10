package visitors.helpers;

import visitors.helpers.data_helpers.SecondVisitorHelper;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

import java.util.ArrayList;
import java.util.List;

public class SeekMethodParametersVisitor extends PreorderJmmVisitor<SecondVisitorHelper, Boolean> {
    private final List<Type> parameters;


    public SeekMethodParametersVisitor() {
        addVisit("Parameters", this::dealWithParameters);
        this.parameters = new ArrayList<>();
    }

    protected Boolean dealWithParameters(JmmNode node, SecondVisitorHelper secondVisitorHelper) {

        SeekReturnTypeVisitor seekReturnTypeVisitor = new SeekReturnTypeVisitor();

        for (JmmNode childNode : node.getChildren()) {

            seekReturnTypeVisitor.visit(childNode, secondVisitorHelper);
            Type type = seekReturnTypeVisitor.getType();

            if (type != null)
                parameters.add(seekReturnTypeVisitor.getType());
        }

        return true;
    }

    protected Boolean defaultVisit(JmmNode node, SecondVisitorHelper secondVisitorHelper) {
        return true;
    }

    public List<Type> getParameters() {
        return parameters;
    }
}
