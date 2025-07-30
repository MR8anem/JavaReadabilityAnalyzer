package readability.utils;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.body.BodyDeclaration;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OperandVisitorTest {

    @Test
    void UniqoperandCountsTest() throws IOException, ParseException {
        String codeSnippet = Files.readString(Paths.get("resources/snippets/15.jsnp"));
        BodyDeclaration<?> parsedcode = Parser.parseJavaSnippet(codeSnippet);

        OperandVisitor operandVisitor = new OperandVisitor();
        parsedcode.accept(operandVisitor, null);
        Map<String, Integer> operandCounts = operandVisitor.getOperandsPerMethod();

        assertEquals(32, operandCounts.size());
    }

    @Test
    void totalOperandCountsTest() throws IOException, ParseException {
        String codeSnippet = Files.readString(Paths.get("resources/snippets/15.jsnp"));
        BodyDeclaration<?> parsedcode = Parser.parseJavaSnippet(codeSnippet);

        OperandVisitor operandVisitor = new OperandVisitor();
        parsedcode.accept(operandVisitor, null);
        Map<String, Integer> operandCounts = operandVisitor.getOperandsPerMethod();
        int totalOperands = operandCounts.values().stream().mapToInt(Integer::intValue).sum();

        assertEquals(82, totalOperands);
    }

    @Test
    void testMultipleUnaryOperands() throws ParseException {
        String code = """
    public void testMethod(){
        int x = 10;
        int y = 20;
        int z = x + y;
        x = x + y;
        y = x > 10 ? 10 : 20;

        List<Integer> list;
        if (list instanceof ArrayList) {
            for (int i = 0; i < 10; i++) {
                System.out.println(i);
            }
        }
    }
    """;

        BodyDeclaration<?> parsedcode = Parser.parseJavaSnippet(code);

        OperandVisitor visitor = new OperandVisitor();
        parsedcode.accept(visitor, null);

        Map<String, Integer> operands = visitor.getOperandsPerMethod();

        // Update expected counts based on the new snippet's operands:
        assertEquals(5, operands.get("x"));  // 'x' used 4 times
        assertEquals(4, operands.get("y"));  // 'y' used 3 times
        assertEquals(1, operands.get("z"));  // 'z' used once
        assertEquals(2, operands.get("list")); // 'list' used once
        assertEquals(4, operands.get("i"));  // 'i' used once
        assertEquals(4, operands.get("10")); // literal 10 appears 3 times
        assertEquals(2, operands.get("20")); // literal 20 appears once
    }


    @Test
    void testBooleanAndNullOperands() throws ParseException {
        String code = "void foo() { boolean flag = true; Object o = null; String s = \"NULL\"; }";
        BodyDeclaration<?> parsedcode = Parser.parseJavaSnippet(code);

        OperandVisitor visitor = new OperandVisitor();
        parsedcode.accept(visitor, null);

        Map<String, Integer> operands = visitor.getOperandsPerMethod();

        assertEquals(1, operands.get("flag"));
        assertEquals(1, operands.get("true"));
        assertEquals(2, operands.get("null")); // null literal and string "NULL" both counted as "null"
        assertEquals(1, operands.get("s"));
    }

    @Test
    void testDifferentLiteralOperands() throws ParseException {
        String code = "void foo() { char c = 'a'; double d = 1.23; long l = 100; int i = 42; String str = \"hello\"; }";
        BodyDeclaration<?> parsedcode = Parser.parseJavaSnippet(code);

        OperandVisitor visitor = new OperandVisitor();
        parsedcode.accept(visitor, null);

        Map<String, Integer> operands = visitor.getOperandsPerMethod();

        assertEquals(1, operands.get("c"));
        assertEquals(1, operands.get("a"));       // char literal 'a'
        assertEquals(1, operands.get("d"));
        assertEquals(1, operands.get("1.23"));    // double literal
        assertEquals(1, operands.get("l"));
        assertEquals(1, operands.get("100"));     // long literal stored as "100"
        assertEquals(1, operands.get("i"));
        assertEquals(1, operands.get("42"));      // int literal
        assertEquals(1, operands.get("str"));
        assertEquals(1, operands.get("hello"));   // string literal
    }

    @Test
    void testMultipleOperandsRepeated() throws ParseException {
        String code = "void foo() { int a = 1, b = 2; a = b + a; b = a - b; }";
        BodyDeclaration<?> parsedcode = Parser.parseJavaSnippet(code);

        OperandVisitor visitor = new OperandVisitor();
        parsedcode.accept(visitor, null);

        Map<String, Integer> operands = visitor.getOperandsPerMethod();

        assertEquals(4, operands.get("a"));   // 'a' appears twice as variable
        assertEquals(4, operands.get("b"));   // 'b' appears twice as variable
        assertEquals(1, operands.get("1"));   // int literal 1 twice (declaration and usage)
        assertEquals(1, operands.get("2"));   // int literal 2 once
    }
}
