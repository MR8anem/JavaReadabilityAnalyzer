package readability.utils;

import readability.features.FeatureMetric;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Preprocess {

    private static final double TRUTH_THRESHOLD = 3.6;

    /**
     * The main public method. It now coordinates calls to helper methods.
     */
    public static void collectCSVBody(Path sourceDir, File truth, StringBuilder csv, List<FeatureMetric> featureMetrics) throws IOException {
        List<File> sortedFiles = findAndSortSnippetFiles(sourceDir);
        if (sortedFiles.isEmpty()) {
            System.err.println("Warning: No .jsnp files found in source directory: " + sourceDir);
            return;
        }

        String[] truthMeanScores = loadTruthScores(truth);
        if (truthMeanScores == null) {
            System.err.println("Error: 'Mean' row not found in ground truth file: " + truth.getAbsolutePath());
            return;
        }

        for (File file : sortedFiles) {
            processSingleFile(file, truthMeanScores, featureMetrics)
                    .ifPresent(csvRow -> csv.append(csvRow).append(System.lineSeparator()));
        }
    }

    /**
     * Finds, filters, and sorts the .jsnp files in a directory.
     * This method's logic can now be tested in isolation.
     */
    private static List<File> findAndSortSnippetFiles(Path sourceDir) {
        File[] listOfFiles = sourceDir.toFile().listFiles((dir, name) -> name.toLowerCase().endsWith(".jsnp"));

        if (listOfFiles == null || listOfFiles.length == 0) {
            return Collections.emptyList();
        }

        return Arrays.stream(listOfFiles)
                .sorted((f1, f2) -> {
                    try {
                        String name1 = f1.getName().substring(0, f1.getName().lastIndexOf('.'));
                        String name2 = f2.getName().substring(0, f2.getName().lastIndexOf('.'));
                        int num1 = Integer.parseInt(name1);
                        int num2 = Integer.parseInt(name2);
                        return Integer.compare(num1, num2);
                    } catch (NumberFormatException e) {
                        return f1.getName().compareTo(f2.getName());
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Loads the "Mean" scores row from the ground truth CSV file.
     * This method's logic can now be tested in isolation.
     */
    private static String[] loadTruthScores(File truthFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(truthFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().startsWith("Mean")) {
                    return line.split(",");
                }
            }
        }
        return null;
    }

    /**
     * Contains the core logic for processing one file. Returns an Optional<String>
     * containing the CSV row, or empty if the file should be skipped.
     * This method is now highly testable without file system interaction.
     */
    private static Optional<String> processSingleFile(File file, String[] truthMeanScores, List<FeatureMetric> featureMetrics) {
        try {
            String fileName = file.getName();
            int fileNumber;
            try {
                fileNumber = Integer.parseInt(fileName.substring(0, fileName.lastIndexOf('.')));
            } catch (NumberFormatException e) {
                System.err.println("Error parsing file number from " + fileName + ". Skipping.");
                return Optional.empty();
            }

            if (fileNumber <= 0 || fileNumber >= truthMeanScores.length) {
                System.err.println("Warning: No truth score found for snippet " + fileName + ". Skipping.");
                return Optional.empty();
            }

            double meanScoreValue;
            try {
                meanScoreValue = Double.parseDouble(truthMeanScores[fileNumber].trim());
            } catch (NumberFormatException e) {
                System.err.println("Error parsing truth score '" + truthMeanScores[fileNumber] + "' for " + fileName + ". Skipping.");
                return Optional.empty();
            }

            String codeSnippet = Files.readString(file.toPath());
            String truthSymbol = (meanScoreValue >= TRUTH_THRESHOLD) ? "Y" : "N";

            StringBuilder csvRow = new StringBuilder();
            csvRow.append(fileName);

            for (FeatureMetric featureMetric : featureMetrics) {
                csvRow.append(",").append(String.format("%.2f", featureMetric.computeMetric(codeSnippet)));
            }
            csvRow.append(",").append(truthSymbol);

            return Optional.of(csvRow.toString());

        } catch (IOException e) {
            System.err.println("Error reading snippet file " + file.getName() + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error computing metrics for file " + file.getName() + ": " + e.getMessage());
        }
        return Optional.empty();
    }
}