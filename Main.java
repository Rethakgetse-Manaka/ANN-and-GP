import java.text.DecimalFormat;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Read training data
        List<double[]> trainingData = CSVReader.readCSV("mushroom_train.csv");
        double[][] trainingInputs = new double[trainingData.size()][trainingData.get(0).length - 1];
        double[][] trainingOutputs = new double[trainingData.size()][1];

        for (int i = 0; i < trainingData.size(); i++) {
            double[] row = trainingData.get(i);
            System.arraycopy(row, 0, trainingInputs[i], 0, row.length - 1);
            trainingOutputs[i][0] = row[row.length - 1];
        }

        // Read testing data
        List<double[]> testingData = CSVReader.readCSV("mushroom_test.csv");
        double[][] testingInputs = new double[testingData.size()][testingData.get(0).length - 1];
        double[][] testingOutputs = new double[testingData.size()][1];

        for (int i = 0; i < testingData.size(); i++) {
            double[] row = testingData.get(i);
            System.arraycopy(row, 0, testingInputs[i], 0, row.length - 1);
            testingOutputs[i][0] = row[row.length - 1];
        }

        // Create and train the neural network
        int inputSize = trainingInputs[0].length;
        int hiddenSize = 50;
        int outputSize = 1;
        double learningRate = 0.00009;
        double targetError = 0.0001;
        int epochs = 400;
        ANN neuralNetwork = new ANN(inputSize, hiddenSize, outputSize, learningRate);
        neuralNetwork.train(trainingInputs, trainingOutputs, epochs, targetError);
        // neuralNetwork.printWeights();
        // Evaluate the neural network on test data
        evaluateModel(neuralNetwork, testingInputs, testingOutputs);
    }

    private static void evaluateModel(ANN nn, double[][] testingInputs, double[][] testingOutputs) {
        DecimalFormat df = new DecimalFormat("#.###");
        int truePositives = 0;
        int trueNegatives = 0;
        int falsePositives = 0;
        int falseNegatives = 0;

        for (int i = 0; i < testingInputs.length; i++) {
            double[] output = nn.forward(testingInputs[i]);
            int predicted = output[0] >= 0.5 ? 1 : 0; // Assuming binary classification with threshold 0.5
            int actual = (int) testingOutputs[i][0];

            if (predicted == 1 && actual == 1) {
                truePositives++;
            } else if (predicted == 0 && actual == 0) {
                trueNegatives++;
            } else if (predicted == 1 && actual == 0) {
                falsePositives++;
            } else if (predicted == 0 && actual == 1) {
                falseNegatives++;
            }

            System.out.println("Expected: " + actual + ", Predicted: " + predicted);
        }

        // Calculate metrics
        double accuracy = (double) (truePositives + trueNegatives) / testingInputs.length;
        double sensitivity = (double) truePositives / (truePositives + falseNegatives);
        double specificity = (double) trueNegatives / (trueNegatives + falsePositives);
        double precision = (double) truePositives / (truePositives + falsePositives);
        double fMeasure = 2 * ((precision * sensitivity) / (precision + sensitivity));

        // Display metrics
        System.out.println("Accuracy: " + df.format(accuracy * 100) + "%");
        System.out.println("Sensitivity (Recall): " + df.format(sensitivity));
        System.out.println("Specificity: " + df.format(specificity));
        System.out.println("Precision: " + df.format(precision));
        System.out.println("F-Measure: " + df.format(fMeasure));
    }
}