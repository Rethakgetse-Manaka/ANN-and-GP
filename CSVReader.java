import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {

    public static List<double[]> readCSV(String filePath) {
        List<double[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Skip the header line
            if ((line = br.readLine()) != null) {
                System.out.println("Header: " + line);
            }
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                String[] values = line.split(",");
                double[] doubleValues = new double[values.length];
                for (int i = 0; i < values.length; i++) {
                    doubleValues[i] = Double.parseDouble(values[i]);
                }
                data.add(doubleValues);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
