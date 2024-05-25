import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Random;

public class GP {
    public int populationSize = 100;
    public int maxGenerations = 50;
    public int maxTreeDepth = 3;
    public int offSpringDepth = 6;
    public int mutationDepth = 6;
    public double crossoverRate;
    public double mutationRate;
    List<String> terminals = Arrays.asList("cap-diameter", "cap-shape", "gill-attachment", "gill-color", "stem-height",
            "stem-width", "stem-color", "season");
    List<String> functions = Arrays.asList("+", "-", "*", "/");
    public ArrayList<double[]> dataset = new ArrayList<>();
    ArrayList<Node> population = new ArrayList<>();
    public String[] attributeValues;
    public Random random;
    public double[] fitness;

    public GP(double mR, double cR, String fileName, long seed) {
        this.mutationRate = mR;
        this.crossoverRate = cR;
        this.random = new Random(seed);
        readFile(fileName);
        fitness = new double[dataset.size()];
    }

    public void readFile(String fileName) {
        try {
            Scanner sc = new Scanner(new File(fileName));
            String line = sc.nextLine();
            String[] attributes = line.split(",");
            attributeValues = attributes;
            while (sc.hasNextLine()) {
                line = sc.nextLine();
                String[] values = line.split(",");
                double[] vals = new double[values.length];
                for (int index = 0; index < values.length; index++) {
                    vals[index] = Double.parseDouble(values[index]);
                }
                dataset.add(vals);
            }
            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("File was not found.");
        }

        normalizeData();
    }

    public void normalizeData() {
        int attributesMinusClass = attributeValues.length - 1;
        double[] minValues = new double[attributesMinusClass];
        double[] maxValues = new double[attributesMinusClass];

        for (int i = 0; i < attributesMinusClass; i++) {
            minValues[i] = Double.MAX_VALUE;
            maxValues[i] = Double.MIN_VALUE;
        }

        for (double[] instance : dataset) {
            for (int i = 0; i < attributesMinusClass; i++) {
                if (instance[i] < minValues[i]) {
                    minValues[i] = instance[i];
                }

                if (instance[i] > maxValues[i]) {
                    maxValues[i] = instance[i];
                }
            }
        }

        for (double[] instance : dataset) {
            for (int index = 0; index < attributesMinusClass; index++) {
                if (index == 3) {
                    instance[index] = 0;
                } else {
                    instance[index] = (instance[index] - minValues[index]) / (maxValues[index] - minValues[index]);
                }

            }
        }
    }

    public void generateInitialPopulation() {
        for (int i = 0; i < populationSize; i++) {
            Node tree = generateTree(maxTreeDepth);
            population.add(tree);
        }
    }

    public Node generateTree(int depth) {
        if (depth <= 1) {
            return new Node(selectRandomTerminal());
        } else {
            Node node = new Node(selectRandomOperator());
            node.left = generateTree(depth - 1);
            node.right = generateTree(depth - 1);
            return node;
        }
    }

    public String selectRandomTerminal() {
        return terminals.get(random.nextInt(terminals.size()));
    }

    public String selectRandomOperator() {
        return functions.get(random.nextInt(functions.size()));
    }

    public double fitnessFunction(Node tree) {
        int correctPredictions = 0;
        for (double[] instance : dataset) {
            double predictedClass = evaluateTree(tree, instance);
            if (predictedClass == instance[instance.length - 1]) {
                correctPredictions++;
            }
        }
        return (double) correctPredictions / dataset.size();
    }

    public double evaluateTree(Node root, double[] instance) {
        if (root == null)
            return 0.0;

        if (isTerminal(root.value)) {
            int attributeIndex = terminals.indexOf(root.value);
            return instance[attributeIndex];
        } else {
            double leftValue = evaluateTree(root.left, instance);
            double rightValue = evaluateTree(root.right, instance);
            return applyOperator(root.value, leftValue, rightValue);
        }
    }

    public boolean isTerminal(String value) {
        return terminals.contains(value);
    }

    public double applyOperator(String operator, double leftValue, double rightValue) {
        switch (operator) {
            case "+":
                return leftValue + rightValue;
            case "-":
                return leftValue - rightValue;
            case "*":
                return leftValue * rightValue;
            case "/":
                // Avoid division by zero
                return rightValue != 0 ? leftValue / rightValue : 0.0;
            default:
                return 0.0;
        }
    }

    public ArrayList<Node> selection(int tournamentSize) {
        ArrayList<Node> selectedParents = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int[] tournamentIndices = new int[tournamentSize];
            for (int j = 0; j < tournamentSize; j++) {
                tournamentIndices[j] = random.nextInt(populationSize);
            }
            double bestFitness = Double.MIN_VALUE;
            Node bestIndividual = null;
            for (int index : tournamentIndices) {
                if (fitness[index] > bestFitness) {
                    bestFitness = fitness[index];
                    bestIndividual = population.get(index);
                }
            }
            selectedParents.add(bestIndividual);
        }
        return selectedParents;
    }

    public String treeToString(Node root) {
        if (root == null)
            return "";
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(root.value);
        sb.append(" ");
        sb.append(treeToString(root.left));
        sb.append(" ");
        sb.append(treeToString(root.right));
        sb.append(")");
        return sb.toString();
    }

    public void evolution() {
        generateInitialPopulation();
        for (int i = 0; i < populationSize; i++) {
            System.out.println("Tree " + (i + 1) + ": " + treeToString(population.get(i)));
        }
        for (int index = 0; index < populationSize; index++) {
            fitness[index] = fitnessFunction(population.get(index));
            System.out.println(fitness[index]);
        }
        // for (int i = 0; i < maxGenerations; i++) {
        // ArrayList<Node> selectedNodes = selection(10);

        // ArrayList<Node> newPopulation = new ArrayList<>();

        // int numCrossovers = (int) (crossoverRate * populationSize);
        // for (int j = 0; j < numCrossovers / 2; j++) {
        // Node parent1 = selectParent();
        // Node parent2 = selectParent();
        // Node[] offspring = crossover(parent1, parent2);
        // newPopulation.add(offspring[0]);
        // newPopulation.add(offspring[1]);
        // }

        // int numMutations = (int) (mutationRate * populationSize);
        // // for (int j = 0; j < numMutations; j++) {
        // // Node parent = selectParent();
        // // Node mutatedOffspring = mutate(parent);
        // // newPopulation.add(mutatedOffspring);
        // // }
        // }
    }

    private Node[] crossover(Node parent1, Node parent2) {
        Node[] offspring = new Node[2];
        int crossoverPoint1 = random.nextInt(maxTreeDepth);
        int leftOrRight1 = random.nextInt(2);
        int leftOrRight2 = random.nextInt(2);
        int crossoverPoint2 = random.nextInt(maxTreeDepth);
        Node subTree1 = getSubTree(parent1, crossoverPoint1, leftOrRight1);
        Node subTree2 = getSubTree(parent2, crossoverPoint2, leftOrRight2);
        offspring[0] = replaceSubTree(parent1, crossoverPoint1, subTree2);
        offspring[1] = replaceSubTree(parent2, crossoverPoint2, subTree1);
        return offspring;
    }

    public Node getSubTree(Node parent2, int crossoverPoint2, int leftOrRight) {
        if (crossoverPoint2 == 0) {
            return parent2;
        } else {
            if (leftOrRight == 0) {
                return getSubTree(parent2.right, crossoverPoint2 - 1, leftOrRight);
            } else {
                return getSubTree(parent2.left, crossoverPoint2 - 1, leftOrRight);
            }

        }
    }

    public Node replaceSubTree(Node parent2, int crossoverPoint2, Node node) {
        if (crossoverPoint2 == 0) {
            return node;
        } else {
            Node newParent = new Node(parent2.value);
            newParent.left = replaceSubTree(parent2.left, crossoverPoint2 - 1, node);
            newParent.right = replaceSubTree(parent2.right, crossoverPoint2 - 1, node);
            return newParent;
        }
    }

    public Node selectParent() {
        return population.get(random.nextInt(populationSize));
    }
}
