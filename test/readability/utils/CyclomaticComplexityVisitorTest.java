package readability.utils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class CyclomaticComplexityVisitorTest {

    private final JavaParser parser = new JavaParser();

    private AtomicInteger applyVisitorTo(String code) {
        CompilationUnit cu = parser.parse(code).getResult().orElseThrow();
        AtomicInteger counter = new AtomicInteger(1);  // Cyclomatic starts at 1
        new CyclomaticComplexityVisitor().visit(cu, counter);
        return counter;
    }

    // Your existing tests
    @Test
    public void testIfStmtIncrements() {
        String code = "class A { void f() { if (true) {} } }";
        assertEquals(2, applyVisitorTo(code).get());
    }

    @Test
    public void testForStmtIncrements() {
        String code = "class A { void f() { for (int i = 0; i < 10; i++) {} } }";
        assertEquals(2, applyVisitorTo(code).get());
    }

    @Test
    public void testForEachStmtIncrements() {
        String code = "class A { void f() { for (int i : new int[]{1,2}) {} } }";
        assertEquals(2, applyVisitorTo(code).get());
    }

    @Test
    public void testWhileStmtIncrements() {
        String code = "class A { void f() { while (true) {} } }";
        assertEquals(2, applyVisitorTo(code).get());
    }

    @Test
    public void testDoStmtIncrements() {
        String code = "class A { void f() { do {} while(true); } }";
        assertEquals(2, applyVisitorTo(code).get());
    }

    @Test
    public void testCatchClauseIncrements() {
        String code = "class A { void f() { try {} catch(Exception e) {} } }";
        assertEquals(2, applyVisitorTo(code).get());
    }

    @Test
    public void testSwitchEntryIncrements() {
        String code = "class A { void f() { switch(1) { case 1: break; case 2: break; } } }";
        assertEquals(3, applyVisitorTo(code).get());
    }

    @Test
    public void testConditionalExprIncrements() {
        String code = "class A { void f() { int x = true ? 1 : 2; } }";
        assertEquals(2, applyVisitorTo(code).get());
    }

    @Test
    public void testBinaryExprIncrementsForAndOr() {
        String code = "class A { void f() { if (true && false || true) {} } }";
        assertEquals(4, applyVisitorTo(code).get());
    }

    @Test
    public void testBinaryExprDoesNotIncrementForPlus() {
        String code = "class A { void f() { int x = 1 + 2; } }";
        assertEquals(1, applyVisitorTo(code).get());
    }

    // --- New tests for better coverage ---

    @Test
    public void testNestedIfs() {
        String code = "class A { void f() { if (true) { if(false) {} } } }";
        // if (outer) + if (inner) + base 1 = 3
        assertEquals(3, applyVisitorTo(code).get());
    }

    @Test
    public void testEmptyClass() {
        String code = "class A {}";
        // no methods or conditions, should be 1 base
        assertEquals(1, applyVisitorTo(code).get());
    }

    @Test
    public void testEmptyMethod() {
        String code = "class A { void f() {} }";
        // no conditions, base complexity 1
        assertEquals(1, applyVisitorTo(code).get());
    }

    @Test
    public void testSwitchDefaultEntry() {
        String code = "class A { void f() { switch(1) { default: break; } } }";
        assertEquals(1, applyVisitorTo(code).get()); // only base complexity, default does not increment
    }

    @Test
    public void testBinaryExprWithMixedOperators() {
        String code = "class A { void f() { if (true && false | true ^ false) {} } }";
        // only && increments (bitwise | and ^ do not)
        assertEquals(3, applyVisitorTo(code).get());
    }

    @Test
    public void testTernaryExpressionNested() {
        String code = "class A { void f() { int x = true ? (false ? 1 : 2) : 3; } }";
        // ternary inside ternary: +2 (1 base + outer + inner)
        assertEquals(3, applyVisitorTo(code).get());
    }

    @Test
    public void testTryWithoutCatchOrFinally() {
        String code = "class A { void f() { try { int a = 1; } finally {} } }";
        // try itself doesn't add complexity, no catch
        assertEquals(1, applyVisitorTo(code).get());
    }



}
