package readability.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import weka.classifiers.Evaluation;
import weka.core.Instances;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Classify utility class.
 */
class ClassifyTest {

    @TempDir
    Path tempDir;

    private File testCsv;

    /**
     * This method runs before each test to create a fresh, temporary CSV file.
     * It now creates 10 instances to support 10-fold cross-validation.
     */
    @BeforeEach
    void setUp() throws IOException {
        testCsv = tempDir.resolve("test_data.csv").toFile();
        try (FileWriter writer = new FileWriter(testCsv)) {
            // Write the header
            writer.write("feature1,feature2,class\n");
            // Write 10 rows of data
            writer.write("1.0,2.5,Y\n");
            writer.write("3.5,4.0,N\n");
            writer.write("1.1,2.6,Y\n");
            writer.write("3.6,4.1,N\n");
            writer.write("1.2,2.7,Y\n");
            writer.write("3.7,4.2,N\n");
            writer.write("1.3,2.8,Y\n");
            writer.write("3.8,4.3,N\n");
            writer.write("1.4,2.9,Y\n");
            writer.write("3.9,4.4,N\n");
        }
    }

    @Test
    void testLoadDataset() throws IOException {
        // Act
        Instances dataset = Classify.loadDataset(testCsv);

        // Assert
        assertNotNull(dataset, "The returned dataset should not be null.");
        assertEquals(10, dataset.size(), "Dataset should contain 10 instances.");
        assertEquals(3, dataset.numAttributes(), "Dataset should have 3 attributes.");
        assertEquals(dataset.numAttributes() - 1, dataset.classIndex(), "The class index was not set correctly.");
    }

    @Test
    void testLoadDataset_ThrowsExceptionForNonexistentFile() {
        // Arrange
        File nonExistentFile = new File("non_existent_file.csv");

        // Act & Assert
        assertThrows(IOException.class, () -> Classify.loadDataset(nonExistentFile));
    }

    /**
     * This test will now pass because the dataset has enough instances (10)
     * for a 10-fold cross-validation.
     */
    @Test
    void testTrainAndEvaluate() throws Exception {
        // Arrange
        Instances dataset = Classify.loadDataset(testCsv);

        // Act
        Evaluation eval = Classify.trainAndEvaluate(dataset);

        // Assert
        assertNotNull(eval, "The Evaluation object should not be null.");
        assertEquals(dataset.size(), eval.numInstances(), "Evaluation should have been run on all instances.");
    }
}