package visitors.semantic.helpers;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import visitors.semantic.helpers.data_helpers.SecondVisitorHelper;

import java.util.ArrayList;
import java.util.List;

public class SeekMethodParametersVisitor extends PreorderJmmVisitor<SecondVisitorHelper, Boolean> {
    private final List<Type> parameters;


    public SeekMethodParametersVisitor() {
        addVisit("Parameters", this::dealWithParameters);
        this.parameters = new ArrayList<>();
    }

    protected Boolean dealWithParameters(JmmNode node, SecondVisitorHelper secondVisitorHelper) {


        for (JmmNode childNode : node.getChildren()) {

            SeekReturnTypeVisitor seekReturnTypeVisitor = new SeekReturnTypeVisitor();

            seekReturnTypeVisitor.visit(childNode, secondVisitorHelper);
            Type type = seekReturnTypeVisitor.getType();

            if (type == null)
                type = new Type("static", false);

            parameters.add(type);
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
