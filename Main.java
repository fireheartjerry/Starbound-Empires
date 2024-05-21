import hsa.Console;

public class Main {
    static Console c; // HSA Console object



    public static void main(String[] args) {
        c = new Console(); // Initialize the console
        
        // Initialize the game variables for material resources. Energy starts in terajoules, and stellar reserves are in tons.
        long stellar_reserves, energy;
        long stellar_reserves_production_rate, energy_production_rate, energy_consumption_rate;

        // Initialize the game variables for the human resources
        long population, population_growth_rate, soldiers, workers, researchers;

        // Keeping the main thread alive to keep the application running
        while (true) {
            // Update the cookies count


            // Calculate the remaining time to sleep to maintain the frame rate
            try {
                Thread.sleep(1000);
                c.setCursor(1, 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
