package readability.features;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A comprehensive test suite for NumberLinesFeature designed to achieve high coverage.
 * It includes over 10 tests covering various line endings, edge cases, and inputs.
 */
class NumberLinesFeatureTest {

    private NumberLinesFeature feature;

    @BeforeEach
    void setUp() {
        feature = new NumberLinesFeature();
    }

    @Test
    void testGetIdentifier() {
        assertEquals("NumberLines", feature.getIdentifier());
    }

    @Test
    void testComputeMetric_WithNullInput() {
        // Covers the `codeSnippet == null` branch.
        assertEquals(0.0, feature.computeMetric(null));
    }

    @Test
    void testComputeMetric_WithEmptyString() {
        // Specifically targets the `codeSnippet.isEmpty()` branch.
        assertEquals(0.0, feature.computeMetric(""));
    }

    @Test
    void testComputeMetric_WithSingleLineAndNoNewline() {
        assertEquals(1.0, feature.computeMetric("public class A {}"));
    }

    @Test
    void testComputeMetric_WithMultipleLinesUsingLF() {
        String code = "line 1\nline 2\nline 3";
        assertEquals(3.0, feature.computeMetric(code));
    }

    @Test
    void testComputeMetric_WithMultipleLinesUsingCRLF() {
        String code = "line 1\r\nline 2\r\nline 3";
        assertEquals(3.0, feature.computeMetric(code));
    }

    @Test
    void testComputeMetric_WithBlankLinesInBetween() {
        String code = "line 1\n\nline 3";
        assertEquals(3.0, feature.computeMetric(code));
    }

    @Test
    void testComputeMetric_WithTrailingNewline() {
        // The split logic means a trailing newline does not add an extra empty line to the count.
        String code = "line 1\nline 2\n";
        assertEquals(2.0, feature.computeMetric(code));
    }

    @Test
    void testComputeMetric_WithLeadingNewline() {
        // A leading newline creates an empty string "" as the first element.
        String code = "\nline 1\nline 2";
        assertEquals(3.0, feature.computeMetric(code));
    }

    @Test
    void testComputeMetric_WithOnlyNewlines() {
        // "\n\n\n" splits into four empty strings.
        assertEquals(0.0, feature.computeMetric("\n\n\n"));
    }

    @Test
    void testComputeMetric_WithWhitespaceOnlyLines() {
        String code = "  \t\n  \n\t";
        assertEquals(3.0, feature.computeMetric(code));
    }

    @Test
    void testComputeMetric_WithMixedLineEndings() {
        String code = "line1\r\nline2\nline3";
        assertEquals(3.0, feature.computeMetric(code));
    }


}
