package readability.utils;

import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.HashMap;
import java.util.Map;

public class OperandVisitor extends VoidVisitorAdapter<Void> {

    /**
     * Maps operand names to the number of their occurrences in the given code snippet.
     */
    private final Map<String, Integer> operandsPerMethod;

    public OperandVisitor() {
        operandsPerMethod = new HashMap<>();
    }

    public Map<String, Integer> getOperandsPerMethod() {
        return operandsPerMethod;
    }

    /**
     * Increments the count for a given operand name.
     * @param name The string representation of the operand.
     */
    private void addOperand(String name) {
        operandsPerMethod.merge(name, 1, Integer::sum);
    }

    @Override
    public void visit(SimpleName n, Void arg) {
        super.visit(n, arg);
        addOperand(n.getIdentifier());
    }

    @Override
    public void visit(BooleanLiteralExpr n, Void arg) {
        super.visit(n, arg);
        // Per description: "do not distinguish the type of the operand"
        addOperand(String.valueOf(n.getValue()));
    }

    @Override
    public void visit(CharLiteralExpr n, Void arg) {
        super.visit(n, arg);
        addOperand(n.getValue());
    }

    @Override
    public void visit(DoubleLiteralExpr n, Void arg) {
        super.visit(n, arg);
        addOperand(n.getValue());
    }

    @Override
    public void visit(IntegerLiteralExpr n, Void arg) {
        super.visit(n, arg);
        addOperand(n.getValue());
    }

    @Override
    public void visit(LongLiteralExpr n, Void arg) {
        super.visit(n, arg);
        addOperand(n.getValue());
    }

    @Override
    public void visit(StringLiteralExpr n, Void arg) {
        super.visit(n, arg);
        String value = n.getValue();
        // Per description: "do not distinguish between a String having a value of NULL and the null literal"
        if ("NULL".equalsIgnoreCase(value)) {
            addOperand("null");
        } else {
            addOperand(value);
        }
    }

    @Override
    public void visit(NullLiteralExpr n, Void arg) {
        super.visit(n, arg);
        addOperand("null");
    }
}