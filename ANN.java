import java.util.Random;

public class ANN {
    private int inputSize;
    private int hiddenSize;
    private int outputSize;
    private double[][] weightsInputHidden;
    private double[][] weightsHiddenOutput;
    private double[] hiddenLayer;
    private double[] outputLayer;
    private double learningRate;
    private Random random = new Random();

    public ANN(int inputSize, int hiddenSize, int outputSize, double learningRate) {
        this.inputSize = inputSize;
        this.hiddenSize = hiddenSize;
        this.outputSize = outputSize;
        this.learningRate = learningRate;

        // Initialize weights with random values
        weightsInputHidden = new double[inputSize][hiddenSize];
        weightsHiddenOutput = new double[hiddenSize][outputSize];
        initializeWeights(weightsInputHidden);
        initializeWeights(weightsHiddenOutput);

        hiddenLayer = new double[hiddenSize];
        outputLayer = new double[outputSize];
        System.out.println("ANN created");
    }

    private void initializeWeights(double[][] weights) {
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                weights[i][j] = (random.nextDouble() * 2 - 1); // random values between -1 and 1
            }
        }
    }

    public double[] forward(double[] inputs) {
        // Compute hidden layer activations
        for (int j = 0; j < hiddenSize; j++) {
            hiddenLayer[j] = 0;
            for (int i = 0; i < inputSize; i++) {
                hiddenLayer[j] += inputs[i] * weightsInputHidden[i][j];
            }
            hiddenLayer[j] = sigmoid(hiddenLayer[j]);
        }

        // Compute output layer activations
        for (int k = 0; k < outputSize; k++) {
            outputLayer[k] = 0;
            for (int j = 0; j < hiddenSize; j++) {
                outputLayer[k] += hiddenLayer[j] * weightsHiddenOutput[j][k];
            }
            outputLayer[k] = sigmoid(outputLayer[k]);
        }

        return outputLayer;
    }

    private double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    public void backward(double[] inputs, double[] expectedOutputs) {
        // Calculate output layer errors and deltas
        double[] outputErrors = new double[outputSize];
        double[] outputDeltas = new double[outputSize];
        for (int k = 0; k < outputSize; k++) {
            outputErrors[k] = expectedOutputs[k] - outputLayer[k];
            outputDeltas[k] = outputErrors[k] * sigmoidDerivative(outputLayer[k]);
        }

        // Calculate hidden layer errors and deltas
        double[] hiddenErrors = new double[hiddenSize];
        double[] hiddenDeltas = new double[hiddenSize];
        for (int j = 0; j < hiddenSize; j++) {
            hiddenErrors[j] = 0;
            for (int k = 0; k < outputSize; k++) {
                hiddenErrors[j] += outputDeltas[k] * weightsHiddenOutput[j][k];
            }
            hiddenDeltas[j] = hiddenErrors[j] * sigmoidDerivative(hiddenLayer[j]);
        }

        // Update weights from hidden to output layer
        for (int j = 0; j < hiddenSize; j++) {
            for (int k = 0; k < outputSize; k++) {
                weightsHiddenOutput[j][k] += learningRate * outputDeltas[k] * hiddenLayer[j];
            }
        }

        // Update weights from input to hidden layer
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                weightsInputHidden[i][j] += learningRate * hiddenDeltas[j] * inputs[i];
            }
        }
    }

    private double sigmoidDerivative(double x) {
        return x * (1 - x);
    }

    public void train(double[][] trainingInputs, double[][] trainingOutputs, int epochs, double targetError) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            double totalError = 0;
            for (int i = 0; i < trainingInputs.length; i++) {
                double[] outputs = forward(trainingInputs[i]);
                backward(trainingInputs[i], trainingOutputs[i]);
                totalError += calculateError(outputs, trainingOutputs[i]);
            }
            totalError /= trainingInputs.length;
            System.out.println("Epoch " + epoch + " - Error: " + totalError);

            if (totalError < targetError) {
                break;
            }
        }
    }

    private double calculateError(double[] outputs, double[] expectedOutputs) {
        double error = 0;
        for (int i = 0; i < outputs.length; i++) {
            error += Math.pow(expectedOutputs[i] - outputs[i], 2);
        }
        return error / 2;
    }
}