package visitors.ollir;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

import java.util.ArrayList;
import java.util.List;

public class SeekStatementsVisitor extends PreorderJmmVisitor<Boolean, Boolean> {
    private final List<JmmNode> nodes;

    public SeekStatementsVisitor() {
        this.nodes = new ArrayList<>();
        addVisit("If", this::dealWithStatement);
        addVisit("While", this::dealWithStatement);
        addVisit("Assignment", this::dealWithStatement);
        setDefaultVisit(this::setDefaultVisit);
    }

    private Boolean dealWithStatement(JmmNode node, Boolean aBoolean) {
        nodes.add(node);
        return true;
    }

    private Boolean setDefaultVisit(JmmNode node, Boolean aBoolean) {
        return true;
    }

    public List<JmmNode> getNodes() {
        return nodes;
    }
}
