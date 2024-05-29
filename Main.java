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

    public static String repeat(String s, long n) {
        String result = "";
        for (int i = 0; i < n; i++)
            result += s;
        return result;
    }

    public static void printPadRight(String s, int n) {
        c.print(s + repeat(" ", n - s.length()));
    }

    // Game variables
    static long stellar_reserves, energy, population, soldiers, workers, doctors, unemployed, population_capacity, iterations, required_energy, number_of_people;
    static String colony_name;
    static Font customFont;

    // Game rates
    static double stellar_reserves_production_rate, energy_production_rate, population_growth_rate, energy_consumption_rate;

    // Mimicking an enum for the game states
    public static final class GameState {
        public static final GameState MAINMENU = new GameState(0);
        public static final GameState DASHBOARD = new GameState(1);
        public static final GameState POPULATION = new GameState(2);
        public static final GameState CLEARED_SCREEN = new GameState(3);
        private final int currentState;

        private GameState(int currentState) {
            this.currentState = currentState;
        }

        public int state() {
            return currentState;
        }
    }

    // Passively detecting key presses
    static char currentKeyPressed;
    static volatile GameState previousGameState, currentGameState;
    static double seconds_past;
    static long maxWidth, maxWidth2, numSwitch;
    static boolean switching, choseFirst, choseSecond, jobInputComplete, workersAvailable, doctorsAvailable, soldiersAvailable;
    static String firstSwitch, secondSwitch;
    static Runnable jobSwitchRunnable, keyListenerRunnable;
    static String[] professions = {"Unemployed", "Workers", "Doctors", "Soldiers"};

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
        for (int i = 0; i < 28; i++) {
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
        displayGraphicalText("Instructions:", new Font("OCR A Extended", Font.BOLD, 30), Color.YELLOW, 10, 100);
        displayGraphicalText("1. You are the leader of a colony in the Starbound Empires universe. You start on Earth.", new Font("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 135);
        displayGraphicalText("2. You must manage your material resources: stellar reserves and energy.", new Font("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 170);
        displayGraphicalText("3. You must also manage your human resources: soldiers, workers, and doctors.", new Font("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 205);
        displayGraphicalText("4. Soldiers will help you conquer new planets.", new Font("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 240);
        displayGraphicalText("5. Workers will increase your production of material resources.", new Font("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 275);
        displayGraphicalText("6. Doctors will increase your population growth.", new Font("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 310);
        displayGraphicalText("7. The game will become more familiar as you play.", new Font("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 345);
        displayGraphicalText("Press any key to continue...", new Font("OCR A Extended", Font.BOLD, 25), Color.YELLOW, 10, 380);
        c.getChar();
        for (int i = 0; i < 28; i++) {
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
        unemployed = 0;
        workers = 1;
        doctors = 0;
        soldiers = 0;

        seconds_past = 0f;
        iterations = 0;
        currentGameState = GameState.MAINMENU;
        previousGameState = GameState.CLEARED_SCREEN;
        switching = false;
        choseFirst = false;
        choseSecond = false;

        currentKeyPressed = 0;
        jobInputComplete = false;

        workersAvailable = false;
        doctorsAvailable = false;
        soldiersAvailable = false;
    }

    public static void updateGameVariables() {
        stellar_reserves_production_rate = workers * 0.1;
        energy_production_rate = workers * 10; // gigajoules
        population_growth_rate = doctors * 0.1 + 0.1; // people per second

        if (iterations % 100 == 0) {
            stellar_reserves += stellar_reserves_production_rate*10;
            population += population_growth_rate*10;
            unemployed = population - workers - doctors - soldiers;
        }

        energy += energy_production_rate / 10;
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
        } while (colony_name.length() > 30 || colony_name.length() < 1);

        displayBackgroundImage();
        displayGraphicalText(colony_name, customFont.deriveFont(50f), Color.CYAN, 10, 45);
        displayRules();
        displayGraphicalText(colony_name, customFont.deriveFont(50f), Color.CYAN, 10, 45);
        setupGameVariables();

        c.setCursor(6, 1);
    }

    public static void updateGameStates() {
        if ((currentKeyPressed == 'M' || currentKeyPressed == 'm') && currentGameState != GameState.MAINMENU)
            currentGameState = GameState.MAINMENU;

        else if ((currentKeyPressed == 'D' || currentKeyPressed == 'd') && currentGameState != GameState.DASHBOARD)
            currentGameState = GameState.DASHBOARD;

        else if ((currentKeyPressed == 'P' || currentKeyPressed == 'p') && currentGameState != GameState.POPULATION)
            currentGameState = GameState.POPULATION;
    }

    public static void callGameStates() {
        if (currentGameState == GameState.CLEARED_SCREEN) {
            c.clear();
            currentGameState = previousGameState;
            previousGameState = GameState.CLEARED_SCREEN;
        }

        if (currentGameState == GameState.MAINMENU) {
            if (currentGameState != previousGameState || previousGameState == GameState.CLEARED_SCREEN) {
                c.clear();
                displayGraphicalText(colony_name, customFont.deriveFont(50f), Color.CYAN, 10, 45);
                displayGraphicalText("----------------- MAIN MENU -----------------", customFont.deriveFont(35f), Color.GREEN, 10, 85);
                previousGameState = currentGameState;
                currentGameState = GameState.MAINMENU;
            }

            main_menu();
        }

        else if (currentGameState == GameState.DASHBOARD) {
            if (currentGameState != previousGameState || previousGameState == GameState.CLEARED_SCREEN) {
                c.clear();
                displayGraphicalText(colony_name, customFont.deriveFont(50f), Color.CYAN, 10, 45);
                displayGraphicalText("----------------- DASHBOARD -----------------", customFont.deriveFont(35f), Color.GREEN, 10, 85);
                previousGameState = currentGameState;
                currentGameState = GameState.DASHBOARD;
            }

            dashboard();
        }

        else if (currentGameState == GameState.POPULATION) {
            if (currentGameState != previousGameState || previousGameState == GameState.CLEARED_SCREEN) {
                c.clear();
                displayGraphicalText(colony_name, customFont.deriveFont(50f), Color.CYAN, 10, 45);
                displayGraphicalText("----------------- POPULATION -----------------", customFont.deriveFont(35f), Color.GREEN, 10, 85);
                previousGameState = currentGameState;
                currentGameState = GameState.POPULATION;
            }
    
            population();
        }
    }

    public static void main_menu() {
        c.setCursor(5, 1);
        c.println("Press D for Dashboard");
        c.println("Press P for Population Management");

        c.println();
        displayValues();
    }

    public static void dashboard() {
        c.setCursor(5, 1);
        displayValues();
    }

    public static void population() {
        c.setCursor(5, 1);

        if (currentKeyPressed == 'U' || currentKeyPressed == 'u')
            switching = true;

        c.println("Population: " + population);
        c.println("Unemployed: " + unemployed);
        c.println("Workers: " + workers);
        c.println("Doctors: " + doctors);
        c.println("Soldiers: " + soldiers);
        c.println();
        if (switching) {
            if (!choseFirst) {
                c.println("Choose a profession to switch from:");
                c.println("Press 1 for Unemployed");
                c.println("Press 2 for Workers");
                c.println("Press 3 for Doctors");
                c.println("Press 4 for Soldiers");
                c.println("Press 5 to cancel");
                if (currentKeyPressed == '1' || currentKeyPressed == '2' || currentKeyPressed == '3' || currentKeyPressed == '4') {
                    firstSwitch = professions[currentKeyPressed-'0'-1];
                    choseFirst = true;
                } else if (currentKeyPressed == '5') {
                    switching = false;
                    choseFirst = false;
                    choseSecond = false;
                    currentGameState = GameState.CLEARED_SCREEN;
                    previousGameState = GameState.POPULATION;
                } else {
                    c.println("Invalid input. Please try again.");
                    currentKeyPressed = 0;
                }
            } else if (!choseSecond) {
                c.println("Choose a profession to switch to (you're switching from \'" + firstSwitch +"\'):");
                c.println("Press 1 for Workers");
                c.println("Press 2 for Doctors");
                c.println("Press 3 for Soldiers");
                c.println("Press 4 to cancel");
                if (currentKeyPressed == '4')
                    switching = false;
                else if (currentKeyPressed == '1' || currentKeyPressed == '2' || currentKeyPressed == '3') {
                    secondSwitch = professions[currentKeyPressed-'0'];
                    if (secondSwitch.equals(firstSwitch)) {
                        c.println("You cannot switch to the same profession.");
                        currentKeyPressed = 0;
                    } else {
                        choseSecond = true;
                        currentGameState = GameState.CLEARED_SCREEN;
                        previousGameState = GameState.POPULATION;
                    }
                } else {
                    c.println("Invalid input. Please try again.");
                    currentKeyPressed = 0;
                }
            } else {
                c.println("Enter the number of people to switch: ");
                numSwitch = c.readLong();
                required_energy = numSwitch * 1000;
                if (firstSwitch.equals("Workers"))
                    number_of_people = workers;
                else if (firstSwitch.equals("Doctors"))
                    number_of_people = doctors;
                else if (firstSwitch.equals("Soldiers"))
                    number_of_people = soldiers;
                else if (firstSwitch.equals("Unemployed"))
                    number_of_people = unemployed;
                if (required_energy > energy) {
                    c.println("You do not have enough energy to switch " + numSwitch + " people.");
                    switching = false;
                    choseFirst = false;
                    choseSecond = false;
                    currentGameState = GameState.CLEARED_SCREEN;
                    previousGameState = GameState.POPULATION;
                } else if (numSwitch > number_of_people) {
                    c.println(numSwitch + " is more than the number of " + firstSwitch + " you have.");
                    switching = false;
                    choseFirst = false;
                    choseSecond = false;
                    currentGameState = GameState.CLEARED_SCREEN;
                    previousGameState = GameState.POPULATION;
                } else {
                    if (firstSwitch.equals("Workers")) {
                        if (secondSwitch.equals("Doctors")) {
                            workers -= numSwitch;
                            doctors += numSwitch;
                        } else if (secondSwitch.equals("Soldiers")) {
                            workers -= numSwitch;
                            soldiers += numSwitch;
                        } else if (secondSwitch.equals("Unemployed")) {
                            workers -= numSwitch;
                            unemployed += numSwitch;
                        }
                    } else if (firstSwitch.equals("Doctors")) {
                        if (secondSwitch.equals("Workers")) {
                            doctors -= numSwitch;
                            workers += numSwitch;
                        } else if (secondSwitch.equals("Soldiers")) {
                            doctors -= numSwitch;
                            soldiers += numSwitch;
                        } else if (secondSwitch.equals("Unemployed")) {
                            doctors -= numSwitch;
                            unemployed += numSwitch;
                        }
                    } else if (firstSwitch.equals("Soldiers")) {
                        if (secondSwitch.equals("Workers")) {
                            soldiers -= numSwitch;
                            workers += numSwitch;
                        } else if (secondSwitch.equals("Doctors")) {
                            soldiers -= numSwitch;
                            doctors += numSwitch;
                        } else if (secondSwitch.equals("Unemployed")) {
                            soldiers -= numSwitch;
                            unemployed += numSwitch;
                        }
                    } else if (firstSwitch.equals("Unemployed")) {
                        if (secondSwitch.equals("Workers")) {
                            unemployed -= numSwitch;
                            workers += numSwitch;
                        } else if (secondSwitch.equals("Doctors")) {
                            unemployed -= numSwitch;
                            doctors += numSwitch;
                        } else if (secondSwitch.equals("Soldiers")) {
                            unemployed -= numSwitch;
                            soldiers += numSwitch;
                        }
                    }
                }
            }
        } else
            c.println("Press U to switch professions (1 TJ/switch)");
    }

    public static void displayValues() {
        // Calculating max width so we can display better
        maxWidth = 0;

        String output_lines[] = {
            "Stellar Reserves: " + stellar_reserves + " MT",
            "Energy: " + energy + " GJ",
            "Population: " + population,
            "Unemployed: " + unemployed,
            "Workers: " + workers,
            "Doctors: " + doctors,
            "Soldiers: " + soldiers,
            "Population Capacity: " + population_capacity,
            "Stellar Reserves Production Rate: " + stellar_reserves_production_rate + " MT/s",
            "Energy Production Rate: " + energy_production_rate + " GJ/s",
            "Population Growth Rate: " + population_growth_rate + " people/s"
        };

        for (int i = 0; i < output_lines.length; i++)
            maxWidth = Math.max(maxWidth, output_lines[i].length());

        // Construct the dash string using an algorithm that dynamically adjusts to the max width
        String dashes = repeat("-", Math.max(5, (maxWidth-20+maxWidth%2)/2)+1);

        c.println(dashes + " MATERIAL RESOURCES " + dashes);
        c.println("Stellar Reserves: " + stellar_reserves + " MT");
        c.println("Energy: " + energy + " GJ\n");

        c.println(dashes + "-- HUMAN RESOURCES -" + dashes);
        c.println("Population: " + population);
        c.println("Unemployed: " + unemployed);
        c.println("Workers: " + workers);
        c.println("Soldiers: " + soldiers);
        c.println("Doctors: " + doctors + "\n");

        c.println(dashes + "-- LIMITS & RATES --" + dashes);
        c.println("Population Capacity: " + population_capacity);
        c.println("Stellar Reserves Production Rate: " + stellar_reserves_production_rate + " MT/s");
        c.println("Energy Production Rate: " + energy_production_rate + " GJ/s");

        c.print("_________________________________________________________\n\nTime: ");
        c.print(seconds_past, 2, 1);
        c.println(" s\n");
    }

    public static void main(String[] args) {
        c = new Console(28, 115, 18, "Starbound Empires"); // Initialize the console
        intializeGame();

        // Keeping the main thread alive to keep the application running
        while (true) {
            if (c.isCharAvail())
                currentKeyPressed = c.getChar();
            updateGameVariables();
            updateGameStates();
            callGameStates();

            try {
                Thread.sleep(80);
                seconds_past += 0.1;
                ++iterations;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
