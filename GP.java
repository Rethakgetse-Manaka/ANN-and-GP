import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class GP {
    public int populationSize = 100;
    public int maxGenerations = 50;
    public int maxTreeDepth = 3;
    public int offSpringDepth = 6;
    public int mutationDepth = 6;
    public double crossoverRate;
    public double mutationRate;
    public double bestFitness;
    private ArrayList<Node> BestParents;
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
        fitness = new double[populationSize];
        BestParents = new ArrayList<>();
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
                    instance[index] = (instance[index] - minValues[index]) / (maxValues[index] -
                            minValues[index]);
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
        for (int i = 0; i < tournamentSize; i++) {
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

        // Perform evolution over generations
        for (int generation = 0; generation < maxGenerations; generation++) {
            ArrayList<Node> BestParents = selection((int) Math.ceil(populationSize / 5));
            ArrayList<Node> newPopulation = new ArrayList<>();

            // Selection, crossover, and mutation
            while (newPopulation.size() < populationSize) {
                Node parent1 = selectParent(BestParents);
                Node parent2 = selectParent(BestParents);

                // Crossover
                Node[] offspring = crossover(parent1, parent2);

                // Mutation
                offspring[0] = mutate(offspring[0], mutationDepth);
                offspring[1] = mutate(offspring[1], mutationDepth);

                newPopulation.add(offspring[0]);
                newPopulation.add(offspring[1]);
            }

            // Replace the old population with the new population
            population = newPopulation;

            // Evaluate new population
            for (int index = 0; index < populationSize; index++) {
                fitness[index] = fitnessFunction(population.get(index));
                System.out.println("Generation " + (generation + 1) + ", Tree " + (index + 1) + ": " + fitness[index]);
            }
        }
        // Find the best individual in the final population
        Node bestIndividual = null;
        double bestFitness = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < populationSize; i++) {
            if (fitness[i] > bestFitness) {
                bestFitness = fitness[i];
                bestIndividual = population.get(i);
            }
        }
        System.out.println("Best individual: , Fitness: " + bestFitness);
    }

    public Node selectParent(ArrayList<Node> bestParents) {
        int tournamentSize = 10; // Define the tournament size
        Node bestIndividual = null;
        double bestFitness = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < tournamentSize; i++) {
            int randomIndex = random.nextInt(bestParents.size());
            Node individual = bestParents.get(randomIndex);
            double individualFitness = fitness[population.indexOf(individual)]; // Get fitness using population index

            if (individualFitness > bestFitness) {
                bestFitness = individualFitness;
                bestIndividual = individual;
            }
        }

        return bestIndividual;
    }

    public double calculateAccuracy() {
        double totalFitness = 0.0;

        // Sum the fitness of all individuals in the population
        for (int i = 0; i < populationSize; i++) {
            totalFitness += fitness[i];
        }

        // Calculate the average fitness
        double averageFitness = totalFitness / populationSize;
        return averageFitness;
    }

    public Node[] crossover(Node parent1, Node parent2) {
        Node[] offspring = new Node[2];
        offspring[0] = copyTree(parent1);
        offspring[1] = copyTree(parent2);

        if (random.nextDouble() < crossoverRate) {
            Node subTree1 = getRandomSubTree(offspring[0]);
            Node subTree2 = getRandomSubTree(offspring[1]);

            replaceSubTree(offspring[0], subTree1, subTree2);
            replaceSubTree(offspring[1], subTree2, subTree1);
        }

        return offspring;
    }

    public Node copyTree(Node tree) {
        if (tree == null) {
            return null;
        }
        Node copy = new Node(tree.value);
        copy.left = copyTree(tree.left);
        copy.right = copyTree(tree.right);
        return copy;
    }

    public Node getRandomSubTree(Node tree) {
        if (tree == null) {
            return null;
        }
        if (random.nextDouble() < 0.5 || (tree.left == null && tree.right == null)) {
            return tree;
        }
        if (random.nextDouble() < 0.5 && tree.left != null) {
            return getRandomSubTree(tree.left);
        } else if (tree.right != null) {
            return getRandomSubTree(tree.right);
        } else {
            return tree;
        }
    }

    public void replaceSubTree(Node parent, Node oldSubTree, Node newSubTree) {
        if (parent == null) {
            return;
        }

        if (parent.left == oldSubTree) {
            parent.left = newSubTree;
        } else if (parent.right == oldSubTree) {
            parent.right = newSubTree;
        } else {
            replaceSubTree(parent.left, oldSubTree, newSubTree);
            replaceSubTree(parent.right, oldSubTree, newSubTree);
        }
    }

    public Node mutate(Node tree, int depth) {
        if (depth <= 1) {
            return generateTree(mutationDepth);
        } else if (tree == null) {
            return null;
        } else {
            Node copy = copyTree(tree);
            if (random.nextDouble() < mutationRate) {
                Node subTree = getRandomSubTree(copy);
                if (subTree != null) {
                    subTree = generateTree(mutationDepth);
                }
            }
            copy.left = mutate(copy.left, depth - 1);
            copy.right = mutate(copy.right, depth - 1);
            return copy;
        }
    }
}