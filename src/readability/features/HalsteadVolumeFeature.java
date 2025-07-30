package readability.features;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.body.BodyDeclaration;
import readability.utils.OperandVisitor;
import readability.utils.OperatorVisitor;
import readability.utils.Parser;

import java.util.Map;

public class HalsteadVolumeFeature extends FeatureMetric {

    private record HalsteadMetrics(int uniqueOperators, int uniqueOperands, int totalOperators, int totalOperands) {}

    @Override
    public double computeMetric(String codeSnippet) {
        if (codeSnippet == null || codeSnippet.isBlank()) {
            return 0.0;
        }

        BodyDeclaration<?> parsedCode;
        try {
            parsedCode = Parser.parseJavaSnippet(codeSnippet);
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse code snippet", e);
        }

        HalsteadMetrics metrics = collectMetricsFrom(parsedCode);
        return calculateVolume(metrics);
    }

    private HalsteadMetrics collectMetricsFrom(BodyDeclaration<?> ast) {
        OperandVisitor operandVisitor = new OperandVisitor();
        ast.accept(operandVisitor, null);
        Map<String, Integer> operandCounts = operandVisitor.getOperandsPerMethod();
        int uniqueOperands = operandCounts.size();
        int totalOperands = operandCounts.values().stream().mapToInt(Integer::intValue).sum();

        OperatorVisitor operatorVisitor = new OperatorVisitor();
        ast.accept(operatorVisitor, null);
        Map<OperatorVisitor.OperatorType, Integer> operatorCounts = operatorVisitor.getOperatorsPerMethod();
        int uniqueOperators = operatorCounts.size();
        int totalOperators = operatorCounts.values().stream().mapToInt(Integer::intValue).sum();

        return new HalsteadMetrics(uniqueOperators, uniqueOperands, totalOperators, totalOperands);
    }

    private double calculateVolume(HalsteadMetrics metrics) {
        int programLength = metrics.totalOperands() + metrics.totalOperators();
        int vocabularySize = metrics.uniqueOperands() + metrics.uniqueOperators();

        if (vocabularySize <= 1) {
            return 0.0;
        }

        return programLength * (Math.log(vocabularySize) / Math.log(2));
    }

    @Override
    public String getIdentifier() {
        return "HalsteadVolume";
    }
}