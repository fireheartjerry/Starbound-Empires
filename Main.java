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
    static Console c; // HSA Console

    // Game variables
    static long stellar_reserves, energy;
    static long population, soldiers, workers, doctors, population_capacity;
    static long seconds_past;
    static String colony_name;

    // Game rates
    static double stellar_reserves_production_rate, energy_production_rate, population_growth_rate, energy_consumption_rate;

    /**
     * Displays graphical text on the screen.
     *
     * @param message the text to be displayed
     * @param font the font to be used for the text
     * @param col the color of the text
     * @param x the x-coordinate of the text's position
     * @param y the y-coordinate of the text's position
     */
    public static void displayGraphicalText(String message, Font font, Color col, int x, int y) {
        c.setFont(font);
        c.setColor(col);
        c.drawString(message, x, y);
    }

    /**
     * Displays the starting screen of the Starbound Empires game.
     * 
     * @param customFont the custom font to be used for displaying text
     */
    public static void displayStartingScreen(Font customFont) {
        c.clear();

        // Include a nice background image
        Image picture = null;
        try {
            picture = ImageIO.read(new File("Assets/stars.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("There was an I/O error when reading the background image file.");
        }
        c.drawImage(picture, 0, 0, null);

        // Write the static messages (they do not require a loop)
        displayGraphicalText("Welcome To", new Font("Consolas", Font.BOLD, 60), Color.GREEN, 445, 80);
        displayGraphicalText("Starbound", customFont.deriveFont(150f), Color.CYAN, 100, 250);
        displayGraphicalText("Empires", customFont.deriveFont(150f), Color.CYAN, 245, 430);
        displayGraphicalText("Press Any Key to Start", new Font("OCR A Extended", Font.BOLD, 45), Color.YELLOW, 310, 525);
        displayGraphicalText("Brought to you by Jerry Li and Jerry Chen", new Font("OCR A Extended", Font.PLAIN, 25), Color.GREEN, 315, 600);

        c.getChar();

        // Remove the starting screen in a cool way using a for loop and a delay
        for (int i = 0; i < 27; i++) {
            c.println();
            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        c.setCursor(1, 1);
    }

    public static void displayRules() {
        displayGraphicalText("Rules:", new Font("OCR A Extended", Font.BOLD, 30), Color.YELLOW, 10, 100);
        displayGraphicalText("1. You are the leader of a colony in the Starbound Empires universe. You start on Earth.", new Font("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 135);
        displayGraphicalText("2. You must manage your material resources: stellar reserves and energy.", new Font("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 170);
        displayGraphicalText("3. You must also manage your human resources: soldiers, workers, and doctors.", new Font("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 205);
        displayGraphicalText("4. Soldiers will help you conquer new planets.", new Font("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 240);
        displayGraphicalText("5. Workers will increase your production of material resources.", new Font("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 275);
        displayGraphicalText("6. Doctors will increase your population growth.", new Font("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 310);
        displayGraphicalText("7. The game will become more familiar as you play.", new Font("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 345);
        displayGraphicalText("Press any key to continue...", new Font("OCR A Extended", Font.BOLD, 25), Color.YELLOW, 10, 380);
        c.getChar();
        c.clear();
    }

    public static void intializeGame() {
        // Set the text color, background color, and font for the console to match our game theme
        c.setTextBackgroundColor(Color.BLACK);
        c.setTextColor(Color.WHITE);

        Font customFont = null;

        try {
            String fontPath = "Assets/gamefont.ttf";
            File fontFile = new File(fontPath);
            InputStream fontStream = new FileInputStream(fontFile);
            customFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
        } catch (FontFormatException e) {
            e.printStackTrace();
            System.err.println("The font file is not in the correct format. Please ensure that it is a .ttf file in the Assets folder.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("There was an I/O error when reading the font file. Please ensure that it is a .ttf file in the Assets folder.");
        }

        displayStartingScreen(customFont);

        do {
            c.print("What is your colony name (max 30 characters)? ");
            colony_name = c.readLine();
            c.clear();
        } while (colony_name.length() > 30 && colony_name.length() < 1);

        displayGraphicalText(colony_name, customFont.deriveFont(50f), Color.CYAN, 10, 45);
        displayRules();
        displayGraphicalText(colony_name, customFont.deriveFont(50f), Color.CYAN, 10, 45);

        // We start with 0 stellar reserves and energy
        stellar_reserves = 0;
        energy = 0;

        // We start with 1 population, and just 1 worker
        population = 1;
        workers = 1;
        doctors = 0;
        soldiers = 0;

        seconds_past = 0;
    }

    public static void main(String[] args) {
        c = new Console(27, 115, 18, "Starbound Empires"); // Initialize the console
        intializeGame();

        // Keeping the main thread alive to keep the application running
        while (true) {
            // Display the hub
            c.println(seconds_past);

            // Calculate the remaining time to sleep to maintain the frame rate
            try {
                Thread.sleep(1000);
                seconds_past++;
                c.setCursor(1, 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
