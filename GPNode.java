// file: GPNode.java

import java.util.*;

public class GPNode {
    private double fitness;
    private String expression;

    public GPNode() {
        this.fitness = 0.0;
        this.expression = "";
    }

    public void randomInitialization() {
        // Initialize the GPNode with a random arithmetic expression
        Random rand = new Random();
        char[] operators = { '+', '-', '*', '/' };
        int numTerms = rand.nextInt(5) + 1; // Number of terms in the expression

        StringBuilder exprBuilder = new StringBuilder();
        for (int i = 0; i < numTerms; i++) {
            exprBuilder.append(rand.nextInt(10)); // Add a random number
            if (i < numTerms - 1) {
                exprBuilder.append(operators[rand.nextInt(operators.length)]); // Add a random operator
            }
        }
        this.expression = exprBuilder.toString();
    }

    public void evaluateFitness() {
        // Evaluate the fitness of the GPNode based on its expression
        // This is a placeholder implementation; replace with actual fitness calculation
        this.fitness = new Random().nextDouble();
    }

    public void crossover(GPNode parent1, GPNode parent2) {
        // Implement the crossover logic between two parent GPNodes
        // This is a placeholder implementation; replace with actual crossover logic
        this.expression = parent1.expression.substring(0, parent1.expression.length() / 2) +
                parent2.expression.substring(parent2.expression.length() / 2);
    }

    public void mutate() {
        // Implement the mutation logic for the GPNode
        // This is a placeholder implementation; replace with actual mutation logic
        Random rand = new Random();
        char[] operators = { '+', '-', '*', '/' };
        int mutationPoint = rand.nextInt(expression.length());
        char[] exprChars = expression.toCharArray();
        if (Character.isDigit(exprChars[mutationPoint])) {
            exprChars[mutationPoint] = (char) ('0' + rand.nextInt(10));
        } else {
            exprChars[mutationPoint] = operators[rand.nextInt(operators.length)];
        }
        this.expression = new String(exprChars);
    }

    public double getFitness() {
        return this.fitness;
    }

    public String getExpression() {
        return this.expression;
    }
}
