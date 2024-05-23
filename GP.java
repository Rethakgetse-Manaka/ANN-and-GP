// file: GP.java

import java.util.*;

public class GP {
    private int populationSize;
    private int generations;
    private List<GPNode> population;

    public GP(int populationSize, int generations) {
        this.populationSize = populationSize;
        this.generations = generations;
        this.population = new ArrayList<>();
    }

    public void initializePopulation() {
        for (int i = 0; i < populationSize; i++) {
            GPNode individual = new GPNode();
            individual.randomInitialization();
            population.add(individual);
        }
    }

    public void evolve() {
        for (int generation = 0; generation < generations; generation++) {
            evaluateFitness();
            List<GPNode> newPopulation = new ArrayList<>();
            while (newPopulation.size() < populationSize) {
                GPNode parent1 = selectParent();
                GPNode parent2 = selectParent();
                GPNode offspring = crossover(parent1, parent2);
                offspring.mutate();
                newPopulation.add(offspring);
            }
            population = newPopulation;
            System.out.println("Generation " + generation + " complete. Training accuracy: " + getBestFitness());
        }
    }

    private void evaluateFitness() {
        for (GPNode individual : population) {
            individual.evaluateFitness();
        }
    }

    private GPNode selectParent() {
        Random rand = new Random();
        return population.get(rand.nextInt(population.size()));
    }

    private GPNode crossover(GPNode parent1, GPNode parent2) {
        GPNode offspring = new GPNode();
        offspring.crossover(parent1, parent2);
        return offspring;
    }

    private double getBestFitness() {
        double bestFitness = Double.NEGATIVE_INFINITY;
        for (GPNode individual : population) {
            if (individual.getFitness() > bestFitness) {
                bestFitness = individual.getFitness();
            }
        }
        return bestFitness;
    }

    public double getTestingAccuracy() {
        // Implement the testing accuracy calculation here
        return 0.0;
    }

    public static void main(String[] args) {
        GP gp = new GP(100, 50);
        gp.initializePopulation();
        gp.evolve();
        System.out.println("Final testing accuracy: " + gp.getTestingAccuracy());
    }
}
