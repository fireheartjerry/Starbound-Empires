import hsa.Console;
import java.awt.Font;
import java.awt.Color;
import java.awt.FontFormatException;
import java.awt.Image;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Main {
    static Console c; // HSA Console

    /************************ HELPER FUNCTIONS ************************/
    public static void displayGraphicalText(String message, Font font, Color col, int x, int y) {
        c.setFont(font);
        c.setColor(col);
        c.drawString(message, x, y);
    }

    public static void displayBackgroundImage(String path) {
        Image picture = null;
        try {
            picture = ImageIO.read(new File(path));
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

    public static boolean randomEvent(double chance) {
        return Math.random() < chance;
    }

    public static void clearRow(int row) {
        c.setCursor(row, 1);
        c.print(repeat(" ", c.getMaxColumns()));
    }


    public static void playBackgroundMusic() {
        Thread musicThread = new Thread(new Runnable() {
            public void run() {
                AudioInputStream audioStream = null;
                Clip audioClip = null;
                try {
                    File audioFile = new File(musicPath);
                    if (!audioFile.exists()) {
                        System.err.println("Audio file not found: " + musicPath);
                        return;
                    }

                    // Get an audio input stream from the file
                    audioStream = AudioSystem.getAudioInputStream(audioFile);

                    // Get the audio format
                    AudioFormat audioFormat = audioStream.getFormat();

                    // Get a data line info object for the SourceDataLine
                    DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

                    // Get a SourceDataLine
                    SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(info);
                    sourceLine.open(audioFormat);
                    sourceLine.start();

                    // Buffer for reading the audio data
                    byte[] buffer = new byte[4096];
                    int bytesRead = 0;

                    // Continuously read and write audio data
                    while (running) {
                        while ((bytesRead = audioStream.read(buffer, 0, buffer.length)) != -1) {
                            sourceLine.write(buffer, 0, bytesRead);
                        }
                        audioStream = AudioSystem.getAudioInputStream(audioFile);
                    }

                } catch (UnsupportedAudioFileException e) {
                    System.err.println("The specified audio file format is not supported.");
                    e.printStackTrace();
                } catch (LineUnavailableException e) {
                    System.err.println("Audio line for playing back is unavailable.");
                    e.printStackTrace();
                } catch (IOException e) {
                    System.err.println("Error playing the audio file.");
                    e.printStackTrace();
                } finally {
                    try {
                        if (audioStream != null) {
                            audioStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        musicThread.start();
    }

    // Mimicking an enum for the game states
    public static final class GameState {
        public static final GameState MAINMENU = new GameState(0);
        public static final GameState DASHBOARD = new GameState(1);
        public static final GameState POPULATION = new GameState(2);
        public static final GameState CLEARED_SCREEN = new GameState(3);
        public static final GameState PLANETMAP = new GameState(4);

        private final int currentState;

        private GameState(int currentState) {
            this.currentState = currentState;
        }

        public int state() {
            return currentState;
        }
    }

    public static final class Region {
        public String name;
        public long required_stellar_reserves;
        public long population_capacity;
        public long soldiers_needed;

        public Region(String name, long required_stellar_reserves, long population_capacity, long soldiers_needed) {
            this.name = name;
            this.required_stellar_reserves = required_stellar_reserves;
            this.population_capacity = population_capacity;
            this.soldiers_needed = soldiers_needed;
        }
    }

    // Game variables
    static long stellar_reserves, energy, population, soldiers, workers, doctors, unemployed, population_capacity, iterations, required_energy, number_of_people;
    static int current_region;
    static String colony_name;
    static Font customFont;
    static Region[] regions = {
        new Region("Earth", 0, 10, 0),
        new Region("Mars", 50, 20, 0),
        new Region("The Asteroid Belt", 300, 40, 0),
        new Region("Jupiter", 1000, 80, 0),
        new Region("Saturn", 5000, 200, 0),
        new Region("Uranus", 10000, 300, 0),
        new Region("The Oort Cloud", 50000, 1000, 500),
        new Region("Planet X", 100000, 3000, 1000),
        new Region("Proxima Centauri B", 500000, 10000, 5000),
        new Region("Ross 128B", 1000000, 15000, 10000),
        new Region("Hoth", 2000000, 20000, 12000),
        new Region("Teth", 3000000, 30000, 20000),
        new Region("Gliese x7x", 4000000, 40000, 25000),
        new Region("Groza-S", 6000000, 50000, 33000),
        new Region("Agamar", 10000000, 60000, 40000),
        new Region("Wayland", 20000000, 80000, 55000),
        new Region("SR-25", 40000000, 80000, 60000),
        new Region("Awajiba", 450000000, 80000, 100000),
    };

    // Game rates
    static double stellar_reserves_production_rate, energy_production_rate, population_growth_rate, energy_consumption_rate;

    // Passively detecting key presses
    static char currentKeyPressed;
    static volatile GameState previousGameState, currentGameState;
    static double seconds_past;
    static long maxWidth, maxWidth2, numSwitch, switchCost, newSwitchCost;
    static boolean switching, choseFirst, choseSecond, jobInputComplete, workersAvailable, doctorsAvailable, soldiersAvailable, conqueringPlanet;
    static String firstSwitch, secondSwitch, musicPath = "Assets/bg5.wav";
    static Runnable jobSwitchRunnable, keyListenerRunnable;
    static String[] professions = {"Unemployed", "Workers", "Doctors", "Soldiers"};
    static Clip audioClip;
    static AudioInputStream audioStream;
    static boolean running = true;

    public static void displayStartingScreen() {
        c.clear();

        // Include a nice background image
        displayBackgroundImage("Assets/stars.jpg");

        // Write the static messages (they do not require a loop)
        displayGraphicalText("Welcome To", new Font("Consolas", Font.BOLD, 60), Color.GREEN, 445, 80);
        displayGraphicalText("Starbound", customFont.deriveFont(150f), Color.CYAN, 100, 250);
        displayGraphicalText("Empires", customFont.deriveFont(150f), Color.CYAN, 245, 430);
        displayGraphicalText("Press Any Key to Start", new Font("OCR A Extended", Font.BOLD, 45), Color.YELLOW, 310, 525);
        displayGraphicalText("Brought to you by Jerry Li and Jerry Chen", new Font("OCR A Extended", Font.PLAIN, 25), Color.GREEN, 315, 600);

        c.getChar();

        // Remove the starting screen in a cool way using a for loop and a delay
        for (int i = 0; i < 29; i++) {
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
        population_capacity = 10;
        current_region = 0;

        seconds_past = 0f;
        iterations = 0;
        currentGameState = GameState.MAINMENU;
        previousGameState = GameState.CLEARED_SCREEN;
        switching = false;
        choseFirst = false;
        choseSecond = false;
        conqueringPlanet = false;
        switchCost = 600;

        currentKeyPressed = 0;
        jobInputComplete = false;

        workersAvailable = false;
        doctorsAvailable = false;
        soldiersAvailable = false;
    }

    public static void updateGameVariables() {
        stellar_reserves_production_rate = workers * 0.1;
        energy_production_rate = workers * 5 + 5; // gigajoules
        population_growth_rate = doctors * 0.1 + 0.1; // people per second

        if (iterations % 100 == 0) {
            stellar_reserves += stellar_reserves_production_rate*10;
            // Population only grows if there is enough energy
            if (energy >= population*200 && population < population_capacity) {
                population += population_growth_rate*10;
                if (population > population_capacity)
                    population = population_capacity;
            } unemployed = population - workers - doctors - soldiers;
        }

        if (iterations % 2 == 0)
            energy += energy_production_rate / 5;
    }

    public static void initializeGame() {
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

        displayBackgroundImage("Assets/stars.jpg");
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
        
        else if ((currentKeyPressed == 'C' || currentKeyPressed == 'c') && currentGameState != GameState.PLANETMAP)
            currentGameState = GameState.PLANETMAP;
    }

    public static void displayHeader(String headerTitle, GameState newGameState) {
        displayGraphicalText(colony_name, customFont.deriveFont(50f), Color.CYAN, 10, 45);
        displayGraphicalText("----------------- " + headerTitle + " -----------------", customFont.deriveFont(35f), Color.GREEN, 10, 85);
        previousGameState = currentGameState;
        currentGameState = newGameState;
    }

    public static void callGameStates() {
        if (currentGameState == GameState.CLEARED_SCREEN) {
            c.clear();
            currentGameState = previousGameState;
            previousGameState = GameState.CLEARED_SCREEN;
        }

        if (currentGameState == GameState.MAINMENU) {
            if (currentGameState != previousGameState || previousGameState == GameState.CLEARED_SCREEN) {
                displayBackgroundImage("Assets/planet.png");
                displayHeader("MAIN MENU", GameState.MAINMENU);
                displayGraphicalText("It is your job to maximize resources and conquer all the planets!", new Font("Consolas", Font.PLAIN, 20), Color.YELLOW, 10, 150);
                displayGraphicalText("Press D to view the dashboard.", new Font("Consolas", Font.PLAIN, 20), Color.YELLOW, 10, 185);
                displayGraphicalText("Press P to manage your population.", new Font("Consolas", Font.PLAIN, 20), Color.YELLOW, 10, 220);
                displayGraphicalText("Press C to conquer new planets.", new Font("Consolas", Font.PLAIN, 20), Color.YELLOW, 10, 255);
                displayGraphicalText("Press M to return to the main menu at any time.", new Font("Consolas", Font.PLAIN, 20), Color.YELLOW, 10, 310);
            }
        }

        else if (currentGameState == GameState.DASHBOARD) {
            if (currentGameState != previousGameState || previousGameState == GameState.CLEARED_SCREEN) {
                c.clear();
                displayHeader("DASHBOARD", GameState.DASHBOARD);
                displayGraphicalText("Press M to return to the main menu at any time.", new Font("Consolas", Font.PLAIN, 20), Color.YELLOW, 10, 110);
            }

            dashboard();
        }

        else if (currentGameState == GameState.POPULATION) {
            if (currentGameState != previousGameState || previousGameState == GameState.CLEARED_SCREEN) {
                c.clear();
                displayHeader("POPULATION", GameState.POPULATION);
                displayGraphicalText("Press M to return to the main menu at any time.", new Font("Consolas", Font.PLAIN, 20), Color.YELLOW, 10, 110);
            }
    
            population();
        }

        else if (currentGameState == GameState.PLANETMAP) {
            if (currentGameState != previousGameState || previousGameState == GameState.CLEARED_SCREEN) {
                c.clear();
                displayHeader("PLANET MAP", GameState.PLANETMAP);
                displayGraphicalText("Press M to return to the main menu at any time.", new Font("Consolas", Font.PLAIN, 20), Color.YELLOW, 10, 110);
            }
    
            planet_map();
        }
    }

    public static void dashboard() {
        c.setCursor(6, 1);
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
        c.print("Stellar Reserves Production Rate: ");
        c.print(stellar_reserves_production_rate, 2, 1);
        c.println(" MT/s");
        c.println("Energy Production Rate: " + energy_production_rate + " GJ/s");
        c.print("Population Growth Rate: ");
        c.print(population_growth_rate, 2, 1);
        c.println(" people/s");        

        c.print("_________________________________________________________\n\nTime: ");
        c.print(seconds_past, 2, 1);
        c.println(" s\n");
    }

    public static void population() {
        c.setCursor(6, 1);

        if (currentKeyPressed == 'U' || currentKeyPressed == 'u') {
            switching = true;
            currentKeyPressed = 0;
        }

        // Print descriptoin
        c.println("You can switch professions to manage your colony. Each switch costs " + switchCost + " GJ/person switched. The cost increases exponentially for each person switched. Choose between Workers (energy and stellar reserve production), Doctors (population growth), Soldiers (planet conquering), and Unemployed. Population growth requires a set amount of energy.\n");
        
        c.print("Time: ");
        c.print(seconds_past, 2, 1);
        c.println(" s");
        c.println("Energy: " + energy + " GJ");
        c.println("Energy Production Rate: " + energy_production_rate + " GJ/s\n");

        c.println("Population: " + population);
        c.println("Unemployed: " + unemployed);
        c.println("Workers: " + workers);
        c.println("Doctors: " + doctors);
        c.println("Soldiers: " + soldiers);
        c.println("\nPopulation Capacity: " + population_capacity);
        c.println("Population Growth Rate: " + population_growth_rate + " people/s\n");

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
                    currentKeyPressed = 0;
                    choseFirst = true;
                    currentGameState = GameState.CLEARED_SCREEN;
                    previousGameState = GameState.POPULATION;
                } else if (currentKeyPressed == '5') {
                    switching = false;
                    choseFirst = false;
                    choseSecond = false;
                    currentGameState = GameState.CLEARED_SCREEN;
                    previousGameState = GameState.POPULATION;
                } else if (currentKeyPressed != 0) {
                    c.println("Invalid input. Please try again.");
                    currentKeyPressed = 0;
                }
            }
            
            else if (!choseSecond) {
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
                } else if (currentKeyPressed == '4') {
                    switching = false;
                    choseFirst = false;
                    choseSecond = false;
                    currentGameState = GameState.CLEARED_SCREEN;
                    previousGameState = GameState.POPULATION;
                } else if (currentKeyPressed != 0) {
                    c.println("Invalid input. Please try again.");
                    currentKeyPressed = 0;
                }
            }
            
            else {
                c.print("Enter the number of people to switch: ");
                numSwitch = c.readLong();
                required_energy = 0;
                newSwitchCost = switchCost;

                // Calculate the required energy, and update the switch cost due to exponential growth
                for (int i = 0; i < numSwitch; ++i) {
                    required_energy += newSwitchCost;
                    newSwitchCost = (int) ((double) newSwitchCost * 1.25);
                }
                
                if (firstSwitch.equals("Workers"))
                    number_of_people = workers;
                else if (firstSwitch.equals("Doctors"))
                    number_of_people = doctors;
                else if (firstSwitch.equals("Soldiers"))
                    number_of_people = soldiers;
                else if (firstSwitch.equals("Unemployed"))
                    number_of_people = unemployed;

                if (required_energy > energy) {
                    c.println("You do not have enough energy to switch " + numSwitch + " people. Press any key to continue.");
                    c.getChar();
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
                    }
                    
                    else if (firstSwitch.equals("Doctors")) {
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
                    }
                    
                    else if (firstSwitch.equals("Soldiers")) {
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
                    }
                    
                    else if (firstSwitch.equals("Unemployed")) {
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
                    
                    energy -= required_energy;
                    c.println("Successfully switched " + numSwitch + " " + firstSwitch + " to " + secondSwitch + ". Press any key to continue.");
                    c.getChar();
                    switching = false;
                    choseFirst = false;
                    choseSecond = false;
                    currentGameState = GameState.CLEARED_SCREEN;
                    previousGameState = GameState.POPULATION;
                    switchCost = newSwitchCost;
                }
            }
        }
        
        else
            c.println("Press U to switch professions (" + switchCost + " GJ/person switched)");
    }

    public static void planet_map() {
        c.setCursor(6, 1);
        c.println("- You can conquer new planets to expand your empire. Each planet increases your current population capacity and requires a certain amount of stellar reserves to conquer.\n- As you progress, you will also need soldiers to conquer more challenging planets.\n- Be prepared for random events that may affect your conquests.\n- Your final goal is to conquer the legendary Awajiba planet. Good luck!\n");

        if (currentKeyPressed == 'B' || currentKeyPressed == 'b') {
            conqueringPlanet = true;
            currentKeyPressed = 0;
        }

        c.println("Current Region: " + regions[current_region].name);
        c.println("Population Capacity: " + regions[current_region].population_capacity + "\n");

        c.println("Stellar Reserves: " + stellar_reserves + " MT");
        c.println("Soldiers: " + soldiers + "\n");

        if (current_region < 17) {
            Region next_region = regions[current_region+1];
            c.println("Next Region: " + next_region.name);
            c.println("Stellar Reserves Required: " + next_region.required_stellar_reserves + " MT");
            c.println("Soldiers Needed: " + next_region.soldiers_needed + "\n");

            if (stellar_reserves >= next_region.required_stellar_reserves && soldiers >= next_region.soldiers_needed) {
                if (conqueringPlanet) {
                    long reserves_payment = next_region.required_stellar_reserves;
                    long soldiers_payment = next_region.soldiers_needed;

                    clearRow(c.getRow());
                    c.setCursor(c.getRow()-1, 1);
                    c.print("Press any character to prepare your soldiers.");
                    c.getChar();
                    clearRow(c.getRow());
                    c.setCursor(c.getRow()-1, 1);

                    boolean caughtOffGuard = randomEvent(0.15);
                    boolean underestimated = randomEvent(0.35);

                    try {
                        int start = (int) (Math.random() * 6) + 3;
                        for (int i = start*10; i >= 1; i--) {
                            c.print("Troops land in " + (int) i/10 + "...");
                            Thread.sleep(80);
                            ++iterations;
                            c.setCursor(c.getRow(), 1);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Random events
                    c.setCursor(c.getRow(), 1);
                    if (caughtOffGuard) {
                        c.println("The alien forces on " + next_region.name + " were caught off guard by your powerful military strategy! You have conquered the planet with ease, and the amounts needed are decreased.");
                        reserves_payment -= reserves_payment * (Math.random() * 0.2 + 0.1);
                        soldiers_payment -= soldiers_payment * (Math.random() * 0.2 + 0.1);
                    } else if (underestimated) {
                        c.println("The alien forces on " + next_region.name + " were underestimated, and a bloody battle ensues! The amounts needed are increased.");
                        reserves_payment += reserves_payment * (Math.random() * 0.2 + 0.1);
                        soldiers_payment += soldiers_payment * (Math.random() * 0.2 + 0.1);
                    } else {
                        c.println("The alien forces on " + next_region.name + " have put up some resistance, but you have conquered the planet!");
                    }

                    c.println();
                    try {
                        int start = (int) (Math.random() * 8) + 3;
                        for (int i = start*10; i >= 1; i--) {
                            c.print("Finishing battle in " + (int) i/10 + "...");
                            Thread.sleep(80);
                            ++iterations;
                            c.setCursor(c.getRow(), 1);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // If we don't have enough stellar reserves or soldiers, we can't conquer the planet
                    if (underestimated && (stellar_reserves < reserves_payment || soldiers < soldiers_payment)) {
                        c.println("After the amounts needed were increased, you do not have enough resources to conquer " + next_region.name + ". Your soldiers and population have been decimated, and your stellar reserves and energy have been emptied.");

                        // Set these to zero
                        stellar_reserves = 0;
                        energy = 0;
                        soldiers = 0;
                        unemployed = 0;

                        // Decrease workers and doctors by a random large percentage
                        workers -= workers * (Math.random() * 0.5 + 0.2);
                        doctors -= doctors * (Math.random() * 0.5 + 0.2);
                    } else {
                        c.println("You have successfully conquered " + next_region.name + "! Press any key to continue.");
                        c.getChar();
                        stellar_reserves -= reserves_payment;
                        soldiers -= soldiers_payment;
                        population_capacity += next_region.population_capacity;
                        ++current_region;
                    }

                    conqueringPlanet = false;
                    currentGameState = GameState.CLEARED_SCREEN;
                } else {
                    c.print("You have enough resources to conquer the next planet. Press B to begin conquering " + next_region.name + ".");
                }

            } else {
                c.println("You need " + (next_region.required_stellar_reserves - stellar_reserves) + " MT more stellar reserves and " + (next_region.soldiers_needed - soldiers) + " more soldiers to conquer " + next_region.name + ".");
            }
        }

        else {
            c.println("You have conquered all the planets! Congratulations on this extremely difficult task, you have won the game!");
        }
    }

    public static void main(String[] args) {
        playBackgroundMusic();
        c = new Console(29, 115, 18, "Starbound Empires"); // Initialize the console
        initializeGame();

        // Keeping the main thread alive to keep the application running
        while (running) {
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

        try {
            audioClip.stop();
            audioClip.close();
            audioStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
