package readability.features;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * A comprehensive test suite for TokenEntropyFeature to achieve high coverage.
 * It uses reflection to test private calculation methods with boundary cases.
 */
class TokenEntropyFeatureTest {

    private TokenEntropyFeature feature;

    @BeforeEach
    void setUp() {
        feature = new TokenEntropyFeature();
    }

    private double invokeCalculateEntropy(Map<String, Integer> freqMap) throws Exception {
        Method method = TokenEntropyFeature.class.getDeclaredMethod("calculateEntropyFromFrequencies", Map.class);
        method.setAccessible(true);
        return (double) method.invoke(feature, freqMap);
    }

    private Map<String, Integer> invokeCalculateFrequencies(String code) throws Exception {
        Method method = TokenEntropyFeature.class.getDeclaredMethod("calculateTokenFrequencies", String.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Integer> result = (Map<String, Integer>) method.invoke(feature, code);
        return result;
    }

    @Test
    void testGetIdentifier() {
        assertEquals("TokenEntropy", feature.getIdentifier());
    }

    @Test
    void testComputeMetric_WithNullInput() {
        assertEquals(0.0, feature.computeMetric(null));
    }

    @Test
    void testComputeMetric_WithBlankInput() {
        assertEquals(0.0, feature.computeMetric("  \t\n "));
    }

    @Test
    void testComputeMetric_WithInvalidCode() {
        assertThrows(RuntimeException.class, () -> feature.computeMetric("void m() { int a = ; }"));
    }


    @Test
    void testCalculateEntropy_PerfectUncertainty() throws Exception {
        Map<String, Integer> frequencies = Map.of("a", 1, "b", 1);
        assertEquals(1.0, invokeCalculateEntropy(frequencies), 0.001);
    }

    @Test
    void testCalculateEntropy_ZeroUncertainty() throws Exception {
        Map<String, Integer> frequencies = Map.of("a", 10);
        assertEquals(0.0, invokeCalculateEntropy(frequencies), 0.0001);
    }

    @Test
    void testCalculateEntropy_EmptyMap() throws Exception {
        assertEquals(0.0, invokeCalculateEntropy(Map.of()));
    }

    @Test
    void testCalculateEntropy_TotalTokensIsZero() throws Exception {
        Map<String, Integer> frequencies = Map.of("a", 0, "b", 0);
        assertEquals(0.0, invokeCalculateEntropy(frequencies));
    }

    @Test
    void testCalculateEntropy_SkewedProbability() throws Exception {
        Map<String, Integer> frequencies = Map.of("a", 9, "b", 1);
        assertEquals(0.469, invokeCalculateEntropy(frequencies), 0.001);
    }

    @Test
    void testComputeMetric_HappyPath() {
        String code = "void a(){}";
        assertEquals(2.807, feature.computeMetric(code), 0.001);
    }

    @Test
    void testCalculateFrequencies_ForSimpleSnippet() throws Exception {
        Map<String, Integer> freqMap = invokeCalculateFrequencies("int a;");
        assertEquals(4, freqMap.size()); // "int", " ", "a", ";"
        assertEquals(1, freqMap.get("int"));
    }
}
