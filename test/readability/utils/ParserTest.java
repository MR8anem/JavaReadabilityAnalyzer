package readability.utils;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.body.BodyDeclaration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Test
    void testParseJavaSnippet_WithValidCode() {
        String validSnippet = "public void myMethod() { int x = 1; }";

        assertDoesNotThrow(() -> {
            BodyDeclaration<?> result = Parser.parseJavaSnippet(validSnippet);
            assertNotNull(result, "The parsed result for valid code should not be null.");
        });
    }

    @Test
    void testParseJavaSnippet_WithInvalidCode() {
        String invalidSnippet = "public void myMethod() { int x = ; }";

        assertThrows(ParseException.class, () -> {
            Parser.parseJavaSnippet(invalidSnippet);
        }, "A ParseException should be thrown for syntactically incorrect code.");
    }

    @Test
    void testParseJavaSnippet_WithEmptyCode() {
        String emptySnippet = "   ";

        assertThrows(ParseException.class, () -> {
            Parser.parseJavaSnippet(emptySnippet);
        }, "A ParseException should be thrown for an empty code snippet.");
    }
}