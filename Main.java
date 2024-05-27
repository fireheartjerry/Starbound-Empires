import hsa.Console;

import java.awt.Font;
import java.awt.Color;
import java.awt.FontFormatException;
import java.awt.Image;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;

import javax.imageio.ImageIO;
import java.lang.Math;

import java.util.List;
import java.util.ArrayList;

public class Main {
    static Console c; // HSA Console

    /************************ HELPER FUNCTIONS ************************/
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

    public static String repeat(String s, int n) {
        String result = "";
        for (int i = 0; i < n; i++)
            result += s;
        return result;
    }

    public static void printPadRight(String s, int n) {
        c.print(s + repeat(" ", n - s.length()));
    }

    // Game variables
    static long stellar_reserves, energy, population, soldiers, workers, doctors, unemployed, population_capacity, iterations;
    static String colony_name;
    static Font customFont;

    // Game rates
    static double stellar_reserves_production_rate, energy_production_rate, population_growth_rate, energy_consumption_rate;

    // Mimicking an enum for the game states
    public static final class GameState {
        public static final GameState MAINMENU = new GameState(0);
        public static final GameState DASHBOARD = new GameState(1);
        private final int currentState;

        private GameState(int currentState) {
            this.currentState = currentState;
        }

        public int state() {
            return currentState;
        }
    }

    // Passively detecting key presses
    static char currentKeyPressed = 0;
    static volatile GameState previousGameState, currentGameState;
    static double seconds_past;
    static int maxWidth;

    public static void displayStartingScreen() {
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
        for (int i = 0; i < 27; i++) {
            c.println();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        c.setCursor(1, 1);
    }

    public static void setupGameVariables() {
        // We start with 0 stellar reserves and energy
        stellar_reserves = 0;
        energy = 0;

        // We start with 1 population, and just 1 worker
        population = 1;
        workers = 1;
        doctors = 0;
        soldiers = 0;

        seconds_past = 0f;
        iterations = 0;
        currentGameState = GameState.MAINMENU;
    }

    public static void updateGameVariables() {
        stellar_reserves_production_rate = workers * 0.1;
        energy_production_rate = workers * 5; // gigajoules
        population_growth_rate = doctors * 0.1;

        if (iterations % 100 == 0) {
            stellar_reserves += stellar_reserves_production_rate*10;
            energy += energy_production_rate*10;
            population += population_growth_rate*10;
            unemployed = population - workers - doctors - soldiers;
        }
    }

    public static void intializeGame() {
        // Set the text color, background color, and font for the console to match our game theme
        c.setTextBackgroundColor(Color.BLACK);
        c.setTextColor(Color.WHITE);

        customFont = null;

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

        displayStartingScreen();

        do {
            c.print("What is your colony name (max 30 characters)? ");
            colony_name = c.readLine();
            c.clear();
        } while (colony_name.length() > 30 && colony_name.length() < 1);

        displayBackgroundImage();
        displayGraphicalText(colony_name, customFont.deriveFont(50f), Color.CYAN, 10, 45);
        displayRules();
        displayGraphicalText(colony_name, customFont.deriveFont(50f), Color.CYAN, 10, 45);
        setupGameVariables();

        c.setCursor(3, 1);
    }

    public static void main_menu() {
        if (previousGameState != GameState.MAINMENU) {
            c.clear();
            displayGraphicalText(colony_name, customFont.deriveFont(50f), Color.CYAN, 10, 45);
            displayGraphicalText("----------------- MAIN MENU -----------------", customFont.deriveFont(35f), Color.GREEN, 10, 85);
            previousGameState = currentGameState;
        }

        c.setCursor(6, 1);
        c.println("Press D for Dashboard\n");
    }

    public static void dashboard() {
        // Only display the hub title if we are not already in the hub
        if (previousGameState != GameState.DASHBOARD) {
            c.clear();
            displayGraphicalText(colony_name, customFont.deriveFont(50f), Color.CYAN, 10, 45);
            displayGraphicalText("----------------- DASHBOARD -----------------", customFont.deriveFont(35f), Color.GREEN, 10, 85);
            previousGameState = currentGameState;
        }

        c.setCursor(6, 1);
    }

    public static void main(String[] args) {
        c = new Console(27, 115, 18, "Starbound Empires"); // Initialize the console
        intializeGame();

        Runnable keyListenerRunnable = new Runnable() {
            public void run() {
                // Replace `c.getChar()` with the appropriate method to get the character
                while (true) {
                    currentKeyPressed = c.getChar();
                }
            }
        };

        // Create a thread with the Runnable
        Thread keyListenerThread = new Thread(keyListenerRunnable);

        // Allow the thread to exit when the program ends
        keyListenerThread.setDaemon(true);

        // Start the thread
        keyListenerThread.start();

        // Keeping the main thread alive to keep the application running
        while (true) {
            updateGameVariables();
            if (currentGameState == GameState.MAINMENU) {
                main_menu();                
            }

            else if (currentGameState == GameState.DASHBOARD) {
                dashboard();
            }

            if (currentKeyPressed == 'D' || currentKeyPressed == 'd')
                currentGameState = GameState.DASHBOARD;

            c.print("_________________________________________________________\n\nTime: ");
            c.print(seconds_past, 2, 1);
            c.println(" s\n");

            // Calculating max width so we can display better
            maxWidth = 0;

            List output_lines = new ArrayList();

            output_lines.add("Stellar Reserves (MT): " + stellar_reserves);
            output_lines.add("Energy (GJ): " + energy);
            output_lines.add("Population: " + population);
            output_lines.add("Unemployed: " + unemployed);
            output_lines.add("Workers: " + workers);
            output_lines.add("Doctors: " + doctors);
            output_lines.add("Soldiers: " + soldiers);
            output_lines.add("Population Capacity: " + population_capacity);

            for (int i = 0; i < output_lines.size(); i++)
                maxWidth = Math.max(maxWidth, output_lines.get(i).toString().length());

            // Construct the dash string using an algorithm that dynamically adjusts to the max width
            String dashes = repeat("-", Math.max(5, (maxWidth-20+maxWidth%2)/2)+1);

            c.println("|" + dashes + " MATERIAL RESOURCES " + dashes + "|");
            for (int i = 0; i < 2; i++) {
                printPadRight("| " + output_lines.get(i), Math.max(33, maxWidth+3-output_lines.get(i).toString().length()));
                c.println("|");
            }

            c.println("|" + dashes + "-- HUMAN RESOURCES -" + dashes + "|");
            for (int i = 2; i < 8; i++) {
                printPadRight("| " + output_lines.get(i), Math.max(33, maxWidth+3-output_lines.get(i).toString().length()));
                c.println("|");                
            }
            c.println("|" + repeat("-", Math.max(32, maxWidth+5)) + "|");

            try {
                Thread.sleep(100);
                seconds_past += 0.1;
                ++iterations;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
