package readability.utils;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.stmt.CatchClause;  // Import retained
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A stateless visitor that calculates cyclomatic complexity.
 * It modifies an AtomicInteger counter that is passed as an argument.
 */
public class CyclomaticComplexityVisitor extends VoidVisitorAdapter<AtomicInteger> {

    @Override
    public void visit(IfStmt n, AtomicInteger counter) {
        counter.incrementAndGet();
        super.visit(n, counter);
    }

    @Override
    public void visit(ForStmt n, AtomicInteger counter) {
        counter.incrementAndGet();
        super.visit(n, counter);
    }

    @Override
    public void visit(ForEachStmt n, AtomicInteger counter) {
        counter.incrementAndGet();
        super.visit(n, counter);
    }

    @Override
    public void visit(WhileStmt n, AtomicInteger counter) {
        counter.incrementAndGet();
        super.visit(n, counter);
    }

    @Override
    public void visit(DoStmt n, AtomicInteger counter) {
        counter.incrementAndGet();
        super.visit(n, counter);
    }

    @Override
    public void visit(CatchClause n, AtomicInteger counter) {
        counter.incrementAndGet();
        super.visit(n, counter);
    }

    // The visit method for BreakStmt has been removed.

    @Override
    public void visit(SwitchEntry n, AtomicInteger counter) {
        if (n.getLabels().size() > 0) {
            counter.addAndGet(n.getLabels().size());
        }
        super.visit(n, counter);
    }

    @Override
    public void visit(ConditionalExpr n, AtomicInteger counter) {
        counter.incrementAndGet();
        super.visit(n, counter);
    }

    @Override
    public void visit(BinaryExpr n, AtomicInteger counter) {
        if (n.getOperator() == BinaryExpr.Operator.AND || n.getOperator() == BinaryExpr.Operator.OR) {
            counter.incrementAndGet();
        }
        super.visit(n, counter);
    }
}