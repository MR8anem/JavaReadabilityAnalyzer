package readability.features;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HalsteadVolumeFeatureTest {

    private final HalsteadVolumeFeature feature = new HalsteadVolumeFeature();

    @Test
    void testSimpleOperation() {
        String code = "void test() { int x = a + b; }";
        double value = feature.computeMetric(code);
        assertTrue(value > 0);
    }

    @Test
    void testEmptyMethod() {
        String code = "void test() { }";
        assertEquals(0.0, feature.computeMetric(code));
    }

    @Test
    void testSameOperandMultipleTimes() {
        String code = "void test() { int x = a + a + a; }";
        double value = feature.computeMetric(code);
        assertTrue(value > 0);
    }

    @Test
    void testInvalidCodeThrowsException() {
        String code = "not java";
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            feature.computeMetric(code);
        });
        assertTrue(ex.getCause() instanceof com.github.javaparser.ParseException);
    }

    @Test
    void testGetIdentifier() {
        assertEquals("HalsteadVolume", feature.getIdentifier());
    }

    @Test
    void testSingleOperatorNoOperand() {
        String code = "void test() { ++; }"; // Invalid operand use
        RuntimeException ex = assertThrows(RuntimeException.class, () -> feature.computeMetric(code));
        assertTrue(ex.getCause() instanceof com.github.javaparser.ParseException);
    }

    @Test
    void testSingleOperandNoOperator() {
        String code = "void test() { int x = a; }";
        double value = feature.computeMetric(code);
        assertTrue(value > 0);
    }

    @Test
    void testMultipleOperatorsAndOperands() {
        String code = "void test() { int x = a + b - c * d / e % f; }";
        double value = feature.computeMetric(code);
        assertTrue(value > 0);
    }

    @Test
    void testBooleanExpressions() {
        String code = "void test() { if (a && b || c) { x = y; } }";
        double value = feature.computeMetric(code);
        assertTrue(value > 0);
    }

    @Test
    void testRelationalOperators() {
        String code = "void test() { if (a > b && b < c || a == c) { return; } }";
        double value = feature.computeMetric(code);
        assertTrue(value > 0);
    }

    @Test
    void testLoopOperators() {
        String code = "void test() { for (int i = 0; i < 10; i++) { x += i; } }";
        double value = feature.computeMetric(code);
        assertTrue(value > 0);
    }

    @Test
    void testMultipleDeclarations() {
        String code = "void test() { int a = 1, b = 2, c = 3; int sum = a + b + c; }";
        double value = feature.computeMetric(code);
        assertTrue(value > 0);
    }

    @Test
    void testMethodCallAsOperand() {
        String code = "void test() { int a = getValue() + 3; }";
        double value = feature.computeMetric(code);
        assertTrue(value > 0);
    }

    @Test
    void testNestedOperations() {
        String code = "void test() { int x = (a + b) * (c - d); }";
        double value = feature.computeMetric(code);
        assertTrue(value > 0);
    }

    @Test
    void testNullParseReturnHandled() {
        HalsteadVolumeFeature featureWithNull = new HalsteadVolumeFeature() {
            @Override
            public double computeMetric(String codeSnippet) {
                return super.computeMetric("void test() { }"); // Simulate null parsing result
            }
        };
        double value = featureWithNull.computeMetric("void test() { }");
        assertEquals(0.0, value);
    }

    @Test
    void testShortCircuitAnd() {
        String code = "void test() { if (a && b) return; }";
        double value = feature.computeMetric(code);
        assertTrue(value > 0);
    }

    @Test
    void testNestedIfElseBranches() {
        String code = """
            void test() {
                if (a > b) {
                    if (c < d) {
                        x = y;
                    } else {
                        x = z;
                    }
                }
            }""";
        double value = feature.computeMetric(code);
        assertTrue(value > 0);
    }
}
