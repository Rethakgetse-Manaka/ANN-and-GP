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
        ANN neuralNetwork = new ANN(trainingInputs[0].length, 10, 1, 0.1);
        neuralNetwork.train(trainingInputs, trainingOutputs, 1000, 0.01);

        // Evaluate the neural network on test data
        evaluateModel(neuralNetwork, testingInputs, testingOutputs);
    }

    private static void evaluateModel(ANN neuralNetwork, double[][] testingInputs, double[][] testingOutputs) {
        int correctPredictions = 0;
        for (int i = 0; i < testingInputs.length; i++) {
            double[] output = neuralNetwork.forward(testingInputs[i]);
            int predicted = output[0] >= 0.5 ? 1 : 0; // Assuming binary classification with threshold 0.5
            if (predicted == (int) testingOutputs[i][0]) {
                correctPredictions++;
            }
            System.out.println("Expected: " + (int) testingOutputs[i][0] + ", Predicted: " + predicted);
        }

        double accuracy = (double) correctPredictions / testingInputs.length;
        System.out.println("Accuracy: " + accuracy);
    }
}