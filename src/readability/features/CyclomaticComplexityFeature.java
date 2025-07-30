package readability.features;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.body.BodyDeclaration;
import readability.utils.CyclomaticComplexityVisitor;
import readability.utils.Parser;

import java.util.concurrent.atomic.AtomicInteger;

public class CyclomaticComplexityFeature extends FeatureMetric {

    /**
     * The base cyclomatic complexity for any code block is 1.
     */
    private static final double BASE_COMPLEXITY = 1.0;

    @Override
    public double computeMetric(String codeSnippet) {
        // Add a guard clause for null or empty snippets.
        // A blank snippet has a single path, so its complexity is 1.
        if (codeSnippet == null || codeSnippet.isBlank()) {
            return BASE_COMPLEXITY;
        }

        BodyDeclaration<?> ast;
        try {
            ast = Parser.parseJavaSnippet(codeSnippet);
        } catch (ParseException e) {
            // Re-throw as a runtime exception to simplify the method signature.
            throw new RuntimeException("Failed to parse code snippet", e);
        }

        // Call the helper method to get the number of decision points.
        int decisionPoints = countDecisionPoints(ast);

        // The final complexity is 1 (for the base path) + the number of decision points.
        return BASE_COMPLEXITY + decisionPoints;
    }

    /**
     * This private helper method isolates the visitor logic.
     * It is now testable on its own.
     * @param ast The Abstract Syntax Tree to visit.
     * @return The number of decision points found by the visitor.
     */
    private int countDecisionPoints(BodyDeclaration<?> ast) {
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        // The counter starts at 0 because the visitor only counts decision points.
        AtomicInteger decisionPointCounter = new AtomicInteger(0);
        ast.accept(visitor, decisionPointCounter);
        return decisionPointCounter.get();
    }

    @Override
    public String getIdentifier() {
        return "CyclomaticComplexity";
    }
}