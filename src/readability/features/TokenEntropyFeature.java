package readability.features;

import com.github.javaparser.JavaToken;
import com.github.javaparser.ParseException;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.body.BodyDeclaration;
import readability.utils.Parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TokenEntropyFeature extends FeatureMetric {

    @Override
    public double computeMetric(String codeSnippet) {
        // Guard clause for invalid input - easy to test.
        if (codeSnippet == null || codeSnippet.isBlank()) {
            return 0.0;
        }

        Map<String, Integer> freqMap = calculateTokenFrequencies(codeSnippet);

        return calculateEntropyFromFrequencies(freqMap);
    }


    private Map<String, Integer> calculateTokenFrequencies(String codeSnippet) {
        BodyDeclaration<?> parsedCode;
        try {
            parsedCode = Parser.parseJavaSnippet(codeSnippet);
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse code snippet", e);
        }

        Optional<TokenRange> tokenRange = parsedCode.getTokenRange();
        if (tokenRange.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Integer> freqMap = new HashMap<>();
        for (JavaToken token : tokenRange.get()) {
            String text = token.getText();
            freqMap.merge(text, 1, Integer::sum);
        }
        return freqMap;
    }


    private double calculateEntropyFromFrequencies(Map<String, Integer> freqMap) {
        if (freqMap.isEmpty()) {
            return 0.0;
        }

        int totalTokens = freqMap.values().stream().mapToInt(Integer::intValue).sum();
        if (totalTokens == 0) {
            return 0.0;
        }

        double entropy = 0.0;
        for (Integer freq : freqMap.values()) {
            double p = (double) freq / totalTokens;
            if (p > 0) {
                entropy += p * (Math.log(p) / Math.log(2));
            }
        }
        return -entropy;
    }

    @Override
    public String getIdentifier() {
        return "TokenEntropy";
    }
}