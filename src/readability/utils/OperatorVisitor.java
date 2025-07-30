package readability.utils;

import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.HashMap;
import java.util.Map;

public class OperatorVisitor extends VoidVisitorAdapter<Void> {

    public enum OperatorType {
        ASSIGNMENT,         // x=y
        BINARY,             // x+y
        UNARY,              // -x, ++x
        CONDITIONAL,        // ?
        TYPE_COMPARISON,    // instanceof
    }

    /**
     * Maps operator types to the number of their occurrences in the given code snippet.
     */
    private final Map<OperatorType, Integer> operatorsPerMethod;

    public OperatorVisitor() {
        operatorsPerMethod = new HashMap<>();
    }

    public Map<OperatorType, Integer> getOperatorsPerMethod() {
        return operatorsPerMethod;
    }

    /**
     * Increments the count for a given operator type.
     * @param type The OperatorType to count.
     */
    private void addOperator(OperatorType type) {
        operatorsPerMethod.merge(type, 1, Integer::sum);
    }

    @Override
    public void visit(AssignExpr n, Void arg) {
        super.visit(n, arg);
        addOperator(OperatorType.ASSIGNMENT);
    }

    @Override
    public void visit(VariableDeclarationExpr n, Void arg) {
        // Per description: "count variable declarations as operations that assign a type to a variable."
        // Each declarator (e.g., 'a' in 'int a, b;') is counted as one assignment.
        for (VariableDeclarator vd : n.getVariables()) {
            addOperator(OperatorType.ASSIGNMENT);
        }
        super.visit(n, arg);
    }

    @Override
    public void visit(BinaryExpr n, Void arg) {
        super.visit(n, arg);
        addOperator(OperatorType.BINARY);
    }

    @Override
    public void visit(UnaryExpr n, Void arg) {
        super.visit(n, arg);
        addOperator(OperatorType.UNARY);
    }

    @Override
    public void visit(ConditionalExpr n, Void arg) {
        super.visit(n, arg);
        addOperator(OperatorType.CONDITIONAL);
    }

    @Override
    public void visit(InstanceOfExpr n, Void arg) {
        super.visit(n, arg);
        addOperator(OperatorType.TYPE_COMPARISON);
    }
}