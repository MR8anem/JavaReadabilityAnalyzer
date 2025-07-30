package readability.features;

import com.github.javaparser.ast.body.BodyDeclaration;
import readability.utils.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * A comprehensive test suite for CyclomaticComplexityFeature to achieve high coverage.
 * It uses reflection to test private methods and covers all branches and edge cases.
 */
class CyclomaticComplexityFeatureTest {

    private CyclomaticComplexityFeature feature;

    @BeforeEach
    void setUp() {
        feature = new CyclomaticComplexityFeature();
    }

    // Helper method to test the private countDecisionPoints method using reflection.
    private int invokeCountDecisionPoints(String code) throws Exception {
        BodyDeclaration<?> ast = Parser.parseJavaSnippet(code);
        Method method = CyclomaticComplexityFeature.class.getDeclaredMethod("countDecisionPoints", BodyDeclaration.class);
        method.setAccessible(true);
        return (int) method.invoke(feature, ast);
    }

    @Test
    void testGetIdentifier() {
        assertEquals("CyclomaticComplexity", feature.getIdentifier());
    }

    @Test
    void testComputeMetric_ForNullInput() {
        assertEquals(1.0, feature.computeMetric(null));
    }

    @Test
    void testComputeMetric_ForBlankInput() {
        assertEquals(1.0, feature.computeMetric("   \t\n "));
    }

    @Test
    void testComputeMetric_ForEmptyInput() {
        assertEquals(1.0, feature.computeMetric(""));
    }

    @Test
    void testComputeMetric_ForInvalidCode() {
        assertThrows(RuntimeException.class, () -> feature.computeMetric("void m() { int a = ; }"));
    }

    @Test
    void testComputeMetric_NoDecisionPoints() {
        assertEquals(1.0, feature.computeMetric("void m() { int x = 1; }"));
    }

    // --- Specific tests for each decision point to kill mutants ---

    @Test
    void testCountDecisionPoints_IfStatement() throws Exception {
        assertEquals(1, invokeCountDecisionPoints("void m() { if(true) {} }"));
    }

    @Test
    void testCountDecisionPoints_ForStatement() throws Exception {
        assertEquals(1, invokeCountDecisionPoints("void m() { for(int i=0; i<1; i++) {} }"));
    }

    @Test
    void testCountDecisionPoints_WhileStatement() throws Exception {
        assertEquals(1, invokeCountDecisionPoints("void m() { while(true) {} }"));
    }

    @Test
    void testCountDecisionPoints_CatchClause() throws Exception {
        assertEquals(1, invokeCountDecisionPoints("void m() { try {} catch(Exception e) {} }"));
    }

    @Test
    void testCountDecisionPoints_ConditionalExpression() throws Exception {
        assertEquals(1, invokeCountDecisionPoints("void m() { int x = true ? 1 : 0; }"));
    }

    @Test
    void testCountDecisionPoints_LogicalAnd() throws Exception {
        assertEquals(2, invokeCountDecisionPoints("void m() { if(a && b) {} }"));
    }

    @Test
    void testCountDecisionPoints_LogicalOr() throws Exception {
        assertEquals(2, invokeCountDecisionPoints("void m() { if(a || b) {} }"));
    }

    @Test
    void testComputeMetric_CombinedComplexity() {
        String code = "void m() { if(a || b) { while(c) {} } }";
        assertEquals(4.0, feature.computeMetric(code));
    }
}
