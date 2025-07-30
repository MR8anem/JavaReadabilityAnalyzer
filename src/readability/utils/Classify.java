package readability.utils;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.Logistic;
import weka.core.Instances;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import weka.core.converters.CSVLoader;

public class Classify {

    /**
     * Loads the {@link Instances} dataset by parsing the CSV file specified via the cli.
     *
     * @param data the CSV file to load.
     * @return the {@link Instances} dataset ready to be classified.
     * @throws IOException if the CSV file specified via the cli could not be loaded.
     */
    public static Instances loadDataset(File data) throws IOException {
        CSVLoader loader = new CSVLoader();
        loader.setSource(new File(data.toString()));
        Instances dataset = loader.getDataSet();

        // --- THIS IS THE FIX ---
        // The class index must be set here, right after loading the data.
        // This assumes the class attribute is the last one in the CSV file.
        dataset.setClassIndex(dataset.numAttributes() - 1);
        // --- END OF FIX ---

        System.out.println("=== Dataset loaded from CSV ===");
        System.out.println(dataset.toSummaryString());
        return  dataset;
    }

    /**
     * Trains and evaluates the "logistic" classifier on the given dataset.
     * For the evaluation, we apply a 10-fold cross-validation using a start seed with a value of 1.
     *
     * @param dataset The dataset to train and evaluate the logistic classifier on.
     * @return the evaluation object hosting the evaluation results.
     * @throws Exception if the classifier could not be generated successfully.
     */
    public static Evaluation trainAndEvaluate(Instances dataset) throws Exception {
        // This line is no longer needed here as it's now handled in loadDataset.
        // dataset.setClassIndex(dataset.numAttributes() - 1);

        Logistic logistic = new Logistic();
        Evaluation eval = new Evaluation(dataset);
        eval.crossValidateModel(logistic, dataset, 10, new Random(1));

        return eval;
    }
}