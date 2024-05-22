import hsa.*;

import java.awt.Font;
import java.awt.Color;
import java.awt.FontFormatException;
import java.awt.Image;

import java.io.File;
import java.io.InputStream;

import javax.imageio.ImageIO;

import java.io.IOException;
import java.io.FileInputStream;

public class Main {
    static Console c; // HSA Console object

    static long stellar_reserves, energy;
    static long stellar_reserves_production_rate, energy_production_rate, energy_consumption_rate;

    static long population, population_growth_rate, soldiers, workers, researchers;

    public static void displayGraphicalText(String message, Font font, Color col, int x, int y) {
        c.setFont(font);
        c.setColor(col);
        c.drawString(message, x, y);
    }

    public static void displayBackgroundImage() {
        Image picture = null;
        try {
            picture = ImageIO.read(new File("Assets/stars.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("There was an I/O error when reading the background image file.");
        }
        c.drawImage(picture, 0, 0, null);
    }

    public static void displayStartingScreen(Font customFont) {
        c.clear();

        // Include a nice background image
        displayBackgroundImage();

        // Write the static messages (they do not require a loop)
        displayGraphicalText("Welcome To", new Font("Consolas", Font.BOLD, 60), Color.GREEN, 445, 80);
        displayGraphicalText("Starbound", customFont.deriveFont(150f), Color.CYAN, 100, 250);
        displayGraphicalText("Empires", customFont.deriveFont(150f), Color.CYAN, 245, 430);
        displayGraphicalText("Press Any Key to Start", new Font("OCR A Extended", Font.BOLD, 45), Color.YELLOW, 310, 525);
        displayGraphicalText("Brought to you by Jerry Li and Jerry Chen", new Font("OCR A Extended", Font.PLAIN, 25), Color.GREEN, 315, 600);

        c.getChar();
    }

    

    public static void main(String[] args) {
        c = new Console(27, 115, 18, "Starbound Empires"); // Initialize the console

        // Set the text color, background color, and font for the console to match our game theme
        c.setTextBackgroundColor(Color.BLACK);
        c.setTextColor(Color.WHITE);

        Font customFont = null;

        // Load custom font
        try {
            String fontPath = "Assets/gamefont.ttf";
            File fontFile = new File(fontPath);
            InputStream fontStream = new FileInputStream(fontFile);
            customFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
        } catch (FontFormatException e) {
            e.printStackTrace();
            System.err.println("The font file is not in the correct format.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("There was an I/O error when reading the font file.");
        }

        // Initialize the game variables for material resources. Energy starts in terajoules, and stellar reserves are in tons.
        stellar_reserves = 0;
        energy = 0;

        stellar_reserves_production_rate, energy_production_rate, energy_consumption_rate;

        // Initialize the game variables for the human resources
        long population, population_growth_rate, soldiers, workers, researchers;

        // Initialize some miscellaneous variables
        String colony_name; // We initialize it to a string of 13 a's to ensure that the user will always have to change it

        displayStartingScreen(customFont);

        // Remove the starting screen in a cool way using a for loop and a delay
        for (int i = 0; i < 27; i++) {
            c.println();
            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        do {
            c.clear();
            c.print("What is your colony name (max 30 characters)? ");
            colony_name = c.readLine();
        } while (colony_name.length() > 30 && colony_name.length() < 1);

        c.clear();
        // Output the rules to the user
        displayGraphicalText(colony_name, customFont.deriveFont(50f), Color.CYAN, 10, 45);

        // Output the rules to the user
        displayGraphicalText("Rules:", new Font("OCR A Extended", Font.BOLD, 30), Color.YELLOW, 10, 100);
        displayGraphicalText("1. You are the leader of a colony in the Starbound Empires universe residing on Earth.", new Font("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 135);
        displayGraphicalText("2. You must manage your material resources: stellar reserves and energy.", new Font("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 170);
        displayGraphicalText("3. You must also manage your human resources: soldiers, workers, and doctors.", new Font("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 205);
        displayGraphicalText("4. Soldiers will help you conquer new planets.", new Font("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 240);
        displayGraphicalText("5. Workers will increase your production of material resources.", new Font("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 275);
        displayGraphicalText("6. Doctors will increase your population growth.", new Font("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 310);
        displayGraphicalText("7. The game will become more familiar as you play.", new Font("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 345);
        displayGraphicalText("Press any key to continue...", new Font("OCR A Extended", Font.BOLD, 25), Color.YELLOW, 10, 380);

        c.getChar();
        c.clear();

        // Keeping the main thread alive to keep the application running
        while (true) {
            // Display the hub


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
