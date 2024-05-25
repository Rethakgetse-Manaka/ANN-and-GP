import java.text.DecimalFormat;

public class Main {
    public static void main(String[] args) {
        DecimalFormat df = new DecimalFormat("#.####");
        // Example parameters
        double mutationRate = 0.1;
        double crossoverRate = 0.4;
        String fileName = "mushroom_test.csv"; // Ensure this file exists and contains your dataset
        long seed = 45;

        // Create the GP object
        GP gp = new GP(mutationRate, crossoverRate, fileName, seed);

        // Run the evolution process
        gp.evolution();

        // Calculate and print accuracy
        double accuracy = gp.calculateAccuracy();
        System.out.println("Overall accuracy of the population: " + df.format(accuracy * 100) + "%");
    }
}
