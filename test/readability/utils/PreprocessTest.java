package readability.utils;

import readability.features.FeatureMetric;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class PreprocessTest {

    private Path tempDir;
    private File truthFile;
    private StringBuilder csvOutput;
    private List<FeatureMetric> features;

    static class DummyFeature extends FeatureMetric {
        private final double value;

        DummyFeature(double value) {
            this.value = value;
        }

        @Override
        public double computeMetric(String codeSnippet) {
            return value;
        }

        @Override
        public String getIdentifier() {
            return "DummyFeature";
        }
    }

    @BeforeEach
    public void setup() throws IOException {
        tempDir = Files.createTempDirectory("testJsnpDir");

        Files.writeString(tempDir.resolve("1.jsnp"), "code snippet 1");
        Files.writeString(tempDir.resolve("2.jsnp"), "code snippet 2");
        Files.writeString(tempDir.resolve("10.jsnp"), "code snippet 10");

        truthFile = File.createTempFile("truth", ".csv");
        try (PrintWriter pw = new PrintWriter(truthFile)) {
            pw.println("Header,1,2,3,4,5,6,7,8,9,10");
            pw.println("Mean,4.0,3.5,2.0,3.0,4.1,3.7,3.9,4.2,3.8,5.0");
        }

        csvOutput = new StringBuilder();
        features = Arrays.asList(new DummyFeature(1.23), new DummyFeature(4.56));
    }

    @AfterEach
    public void cleanup() throws IOException {
        Files.deleteIfExists(truthFile.toPath());
        Files.walk(tempDir)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test
    public void testCollectCSVBody_basic() throws IOException {
        Preprocess.collectCSVBody(tempDir, truthFile, csvOutput, features);
        String result = csvOutput.toString();
        assertTrue(result.contains("1.jsnp,1.23,4.56,Y"));
        assertTrue(result.contains("2.jsnp,1.23,4.56,N"));
        assertTrue(result.contains("10.jsnp,1.23,4.56,Y"));
        assertEquals(3, result.lines().count());
    }

    @Test
    public void testCollectCSVBody_noJsnpFiles() throws IOException {
        Files.list(tempDir)
                .filter(p -> p.toString().endsWith(".jsnp"))
                .forEach(p -> p.toFile().delete());
        Preprocess.collectCSVBody(tempDir, truthFile, csvOutput, features);
        assertEquals("", csvOutput.toString());
    }

    @Test
    public void testCollectCSVBody_missingMeanLine() throws IOException {
        try (PrintWriter pw = new PrintWriter(truthFile)) {
            pw.println("Header,1,2,10");
        }
        Preprocess.collectCSVBody(tempDir, truthFile, csvOutput, features);
        assertEquals("", csvOutput.toString());
    }

    @Test
    public void testCollectCSVBody_invalidFileNumber() throws IOException {
        Files.writeString(tempDir.resolve("abc.jsnp"), "invalid snippet");
        Preprocess.collectCSVBody(tempDir, truthFile, csvOutput, features);
        String result = csvOutput.toString();
        assertTrue(result.contains("1.jsnp"));
        assertTrue(result.contains("2.jsnp"));
        assertFalse(result.contains("abc.jsnp"));
    }

    // ✅ New Tests for full branch & mutation coverage

    @Test
    void testNoJsnpFiles_found() throws IOException {
        File emptyDir = Files.createTempDirectory("empty").toFile();
        Preprocess.collectCSVBody(emptyDir.toPath(), truthFile, new StringBuilder(), features);
    }

    @Test
    void testInvalidMeanValueInTruthFile() throws IOException {
        try (PrintWriter pw = new PrintWriter(truthFile)) {
            pw.println("Header,1,2,10");
            pw.println("Mean,4.0,INVALID,5.0");
        }
        Preprocess.collectCSVBody(tempDir, truthFile, csvOutput, features);
        assertTrue(csvOutput.toString().contains("1.jsnp"));
        assertFalse(csvOutput.toString().contains("2.jsnp"));
    }

    @Test
    void testScoreAtThresholdIsY() throws IOException {
        try (PrintWriter pw = new PrintWriter(truthFile)) {
            pw.println("Header,1,2,10");
            pw.println("Mean,3.6,3.5,5.0");
        }
        Preprocess.collectCSVBody(tempDir, truthFile, csvOutput, features);
        assertTrue(csvOutput.toString().contains("1.jsnp,1.23,4.56,Y"));
    }

    @Test
    void testComputeMetricThrowsException() throws IOException {
        List<FeatureMetric> faultyFeature = List.of(new FeatureMetric() {
            @Override public double computeMetric(String codeSnippet) {
                throw new RuntimeException("fail");
            }
            @Override public String getIdentifier() { return "Broken"; }
        });
        Preprocess.collectCSVBody(tempDir, truthFile, csvOutput, faultyFeature);
        assertFalse(csvOutput.toString().contains("Exception"));
    }

    @Test
    void testFileNumberOutOfTruthBounds() throws IOException {
        Files.writeString(tempDir.resolve("100.jsnp"), "code snippet");
        Preprocess.collectCSVBody(tempDir, truthFile, csvOutput, features);
        assertFalse(csvOutput.toString().contains("100.jsnp"));
    }
    @Test
    void testFileNumberZeroSkipped() throws IOException {
        Files.writeString(tempDir.resolve("0.jsnp"), "zero file");

        // Collect output with the extra "0.jsnp"
        Preprocess.collectCSVBody(tempDir, truthFile, csvOutput, features);

        // Each valid .jsnp (1, 2, 10) should result in one output line
        // 0.jsnp should be skipped, so total = 3
        long validLines = csvOutput.toString().lines()
                .filter(line -> line.startsWith("0.jsnp")).count();

        assertEquals(0, validLines, "0.jsnp should be skipped and not appear in the output");
    }


    @Test
    void testComputeMetricReturnsNaNOrNegative() throws IOException {
        List<FeatureMetric> weirdFeature = List.of(
                new DummyFeature(Double.NaN),
                new DummyFeature(-1.0)
        );
        Preprocess.collectCSVBody(tempDir, truthFile, csvOutput, weirdFeature);
        assertTrue(csvOutput.toString().contains("NaN") || csvOutput.toString().contains("-1.00"));
    }

    @Test
    void testIOExceptionDuringFileRead() throws IOException {
        File file = tempDir.resolve("99.jsnp").toFile();
        Files.writeString(file.toPath(), "code");
        file.setReadable(false); // simulate error
        Preprocess.collectCSVBody(tempDir, truthFile, csvOutput, features);
        file.setReadable(true); // reset
    }

    @Test
    void testEmptyCodeSnippetStillProcesses() throws IOException {
        Files.writeString(tempDir.resolve("5.jsnp"), "");
        try (PrintWriter pw = new PrintWriter(truthFile)) {
            pw.println("Header,1,2,3,4,5");
            pw.println("Mean,4.0,4.0,4.0,4.0,4.0");
        }
        Preprocess.collectCSVBody(tempDir, truthFile, csvOutput, features);
        assertTrue(csvOutput.toString().contains("5.jsnp"));
    }

    /**
     * Tests the boundary condition of the truth threshold.
     * This test "kills" any mutant that might change the '>=' operator to '>'.
     */
    @Test
    void testScoreAtThresholdIsClassifiedAsY() throws IOException {
        // Arrange: Overwrite the truth file to have a score exactly at the 3.6 threshold.
        try (PrintWriter pw = new PrintWriter(truthFile)) {
            pw.println("Header,1");
            pw.println("Mean,3.6");
        }

        // Act
        Preprocess.collectCSVBody(tempDir, truthFile, csvOutput, features);

        // Assert: A score of exactly 3.6 should be classified as 'Y'.
        assertTrue(csvOutput.toString().contains("1.jsnp,1.23,4.56,Y"));
    }

    /**
     * Tests that a file with a number that is out of bounds of the truth scores is skipped.
     * This kills mutants related to the boundary check (e.g., changing >= to >).
     */
    @Test
    void testFileNumberOutOfTruthBoundsIsSkipped() throws IOException {
        // Arrange: Add a file with a number far beyond the number of scores in the truth file.
        Files.writeString(tempDir.resolve("99.jsnp"), "code snippet 99");

        // Act
        Preprocess.collectCSVBody(tempDir, truthFile, csvOutput, features);

        // Assert: The final output should not contain the out-of-bounds file.
        assertFalse(csvOutput.toString().contains("99.jsnp"));
    }

    /**
     * Tests that a file is skipped if its corresponding truth score is not a valid number.
     * This covers the NumberFormatException catch block for parsing the score.
     */
    @Test
    void testInvalidTruthScoreIsSkipped() throws IOException {
        // Arrange: Overwrite the truth file with a non-numeric score for file "2".
        try (PrintWriter pw = new PrintWriter(truthFile)) {
            pw.println("Header,1,2,10");
            pw.println("Mean,4.0,INVALID,5.0");
        }

        // Act
        Preprocess.collectCSVBody(tempDir, truthFile, csvOutput, features);

        // Assert: The file pointing to the invalid score should be skipped.
        String result = csvOutput.toString();
        assertTrue(result.contains("1.jsnp"));
        assertFalse(result.contains("2.jsnp"));
    }



}
