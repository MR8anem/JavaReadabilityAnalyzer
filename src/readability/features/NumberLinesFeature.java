
package readability.features;

public class NumberLinesFeature extends FeatureMetric {

    /**
     * Computes the number of lines of the given code snippet.
     * Since we are interested in determining the readability of a code snippet, this also includes comments.
     *
     * @return source code lines of the given code snippet.
     */
    @Override
    public double computeMetric(String codeSnippet) {
        if (codeSnippet == null || codeSnippet.isEmpty()) {
            return 0;
        }
        String[] lines = codeSnippet.split("\\r?\\n");
        return lines.length;

    }

    @Override
    public String getIdentifier() {
        return "NumberLines";
    }
}
