package readability.utils;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.body.BodyDeclaration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the OperatorVisitor class.
 */
class OperatorVisitorTest {


    // 1. Declare the variable that will be shared across tests.
    private BodyDeclaration<?> parsedcode;

    // 2. Create a setup method to initialize the variable.
    // This method will run before each @Test.
    @BeforeEach
    void setUp() throws IOException, ParseException {

        String codeSnippet = Files.readString(Paths.get("resources/snippets/15.jsnp"));
        parsedcode = Parser.parseJavaSnippet(codeSnippet);
    }



    @Test
    void getOperatorsPerMethod() {
        OperatorVisitor operatorVisitor = new OperatorVisitor();
        parsedcode.accept(operatorVisitor, null);
        Map<OperatorVisitor.OperatorType, Integer> operatorCounts = operatorVisitor.getOperatorsPerMethod();
        assertEquals(3, operatorCounts.size());
        assertEquals(6, operatorCounts.get(OperatorVisitor.OperatorType.ASSIGNMENT));
        assertEquals(null, operatorCounts.get(OperatorVisitor.OperatorType.CONDITIONAL));
        assertEquals(2, operatorCounts.get(OperatorVisitor.OperatorType.UNARY));
        assertEquals(8, operatorCounts.get(OperatorVisitor.OperatorType.BINARY));

    }

    @Test
    void testTypeComparisonOperator() throws Exception {
        String code = "class A { void f(Object o) { boolean b = o instanceof String; } }";
        BodyDeclaration<?> snippet = Parser.parseJavaSnippet(code);
        OperatorVisitor visitor = new OperatorVisitor();
        snippet.accept(visitor, null);
        Map<OperatorVisitor.OperatorType, Integer> ops = visitor.getOperatorsPerMethod();
        assertEquals(1, ops.get(OperatorVisitor.OperatorType.TYPE_COMPARISON));
    }

    @Test
    void testConditionalOperator() throws Exception {
        String code = "class A { void f() { int x = true ? 1 : 2; } }";
        BodyDeclaration<?> snippet = Parser.parseJavaSnippet(code);
        OperatorVisitor visitor = new OperatorVisitor();
        snippet.accept(visitor, null);
        Map<OperatorVisitor.OperatorType, Integer> ops = visitor.getOperatorsPerMethod();
        assertEquals(1, ops.get(OperatorVisitor.OperatorType.CONDITIONAL));
    }





    @Test
    void testMultipleBinaryOperators() throws Exception {
        String code = "class A { void f() { int x = 1 + 2 - 3 * 4 / 5; } }";
        BodyDeclaration<?> snippet = Parser.parseJavaSnippet(code);
        OperatorVisitor visitor = new OperatorVisitor();
        snippet.accept(visitor, null);
        Map<OperatorVisitor.OperatorType, Integer> ops = visitor.getOperatorsPerMethod();
        assertEquals(4, ops.get(OperatorVisitor.OperatorType.BINARY));
    }

    @Test
    void testAssignmentsAndVariableDeclarations() throws Exception {
        String code = "class A { void f() { int a = 1, b = 2; a = 3; b = 4; } }";
        BodyDeclaration<?> snippet = Parser.parseJavaSnippet(code);
        OperatorVisitor visitor = new OperatorVisitor();
        snippet.accept(visitor, null);
        Map<OperatorVisitor.OperatorType, Integer> ops = visitor.getOperatorsPerMethod();
        assertEquals(4, ops.get(OperatorVisitor.OperatorType.ASSIGNMENT));
    }

    @Test
    void testEmptyMethod() throws Exception {
        String code = "class A { void f() { } }";
        BodyDeclaration<?> snippet = Parser.parseJavaSnippet(code);
        OperatorVisitor visitor = new OperatorVisitor();
        snippet.accept(visitor, null);
        Map<OperatorVisitor.OperatorType, Integer> ops = visitor.getOperatorsPerMethod();
        assertTrue(ops.isEmpty() || ops.values().stream().allMatch(i -> i == 0));
    }



}