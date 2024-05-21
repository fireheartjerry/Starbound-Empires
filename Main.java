import hsa.Console;
import java.awt.*;
import java.awt.Font;

public class Main {
    static Console c; // HSA Console object

    public static void displayStartingScreen() {
        c.clear();
        c.println("Welcome to");
        c.println("Press any key to start the game.");
        
        c.getChar();
    }

    public static void main(String[] args) {
        c = new Console(30, 135, 18, "Starbound Empires"); // Initialize the console

        // Set the text color and background color to space themed
        c.setTextBackgroundColor(Color.BLACK);
        c.setTextColor(Color.WHITE);
        
        // Set the font to Orbitron
        Font orbitronFont = new Font("Orbitron", Font.PLAIN, 12);
        c.setFont(orbitronFont);
        
        // Initialize the game variables for material resources. Energy starts in terajoules, and stellar reserves are in tons.
        long stellar_reserves, energy;
        long stellar_reserves_production_rate, energy_production_rate, energy_consumption_rate;

        // Initialize the game variables for the human resources
        long population, population_growth_rate, soldiers, workers, researchers;

        displayStartingScreen();

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
