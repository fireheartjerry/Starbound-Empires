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
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.text.DecimalFormat;
import java.lang.Math;

public class test
{
    static Console c; // HSA Console
    static String[] professionNames = {"Unemployed", "Worker", "Soldier", "Doctor"};
    static String[] resourceNames = {"Stellar Reserve", "Energy"};
    static String[] regionNames = {"Earth", "Mars", "Asteroid belt", "Jupiter", "Saturn", "Uranus", "Oort cloud", "Planet X", "Proxima Centauri B", "Ross 128b", "Hoth", "SPAS-12", "Gliese x7x", "Groza-S", "Agamar", "Wayland", "SR-25", "Awajiba"};
    static int[] soldiersNeeded = {0, 0, 0, 0, 0, 0, 500, 1000, 5000, 10000, 12000, 20000, 25000, 33000, 40000, 55000, 60000, 65000};
    static int[] reservesNeeded = {0, 50, 300, 1000, 5000, 10000, 50000, 100000, 500000, 1000000, 2000000, 3000000, 4000000, 6000000, 10000000, 20000000, 40000000, 694200000};
    static int[] populationCaps = {10, 20, 40, 80, 200, 300, 1000, 3000, 10000, 15000, 20000, 30000, 40000, 50000, 60000, 80000, 80000, 80000};
    static int currentRegion = 0;
    static int[] peopleCounter = {0, 1, 0, 0};
    static int[] resourceCounter = {0, 0, 0};
    static double seconds_past = 0;
    static int iterations = 0;
	static String musicPath = "Assets/bg6.wav";
	static boolean running = true, image_not_shown = true;

    // Game variables
    static String colony_name;
    static Font customFont;

    // Game rates
    static double stellar_reserves_production_rate, energy_production_rate, population_growth_rate;
	static long switchCost = 600;

    /**
     * 0 - Start menu
     * 1 - Select name menu
     * 2 - Rules menu
     * 3 - Main menu
     * 4 - Population menu
     * 5 - Regions map
    */
    static int currentMenu = 0;

    /************************ HELPER FUNCTIONS ************************/
    public static int min (int a, int b)
    {
	if (a < b)
	    return a;
	else
	    return b;
    }

    public static void displayGraphicalText (String message, Font font, Color col, int x, int y)
    {
	c.setFont (font);
	c.setColor (col);
	c.drawString (message, x, y);
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

    public static String repeat (String s, long n)
    {
	String result = "";
	for (int i = 0 ; i < n ; i++)
	    result += s;
	return result;
    }

    public static void printPadRight (String s, int n)
    {
	c.print (s + repeat (" ", n - s.length ()));
    }

    public static int inputNumber (int low, int high)
    {
	int num;

	do
	{
	    num = c.readInt ();
	    if (num < low || num > high)
	    {
		c.println ("Invalid input. Must be between " + low + " and " + high);
	    }
	}
	while (num < low || num > high);
	return num;
    }

    public static boolean randomEvent(double chance) {
        return Math.random() < chance;
    }

    public static void playBackgroundMusic() {
        Thread musicThread = new Thread(new Runnable() {
            public void run() {
                AudioInputStream audioStream = null;
                try {
                    File audioFile = new File(musicPath);
                    if (!audioFile.exists()) {
                        System.err.println("Audio file not found: " + musicPath);
                        return;
                    }

                    audioStream = AudioSystem.getAudioInputStream(audioFile);
                    AudioFormat audioFormat = audioStream.getFormat();
                    DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
                    SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(info);
                    sourceLine.open(audioFormat);
                    FloatControl volumeControl = (FloatControl) sourceLine.getControl(FloatControl.Type.MASTER_GAIN);
                    volumeControl.setValue(-20.0f);
                    sourceLine.start();

                    byte[] buffer = new byte[4096];
                    int bytesRead = 0;

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

    public static void playGameSound(String soundPath) {
        AudioInputStream audioStream = null;
        try {
            File audioFile = new File(soundPath);
            if (!audioFile.exists()) {
                System.err.println("Audio file not found: " + soundPath);
                return;
            }

			audioStream = AudioSystem.getAudioInputStream(audioFile);
			AudioFormat audioFormat = audioStream.getFormat();
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
			SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(info);
			sourceLine.open(audioFormat);
			FloatControl volumeControl = (FloatControl) sourceLine.getControl(FloatControl.Type.MASTER_GAIN);
			volumeControl.setValue(-20.0f);
			sourceLine.start();

            // Buffer for reading the audio data
            byte[] buffer = new byte[4096];
            int bytesRead = 0;

            // Continuously read and write audio data
            while ((bytesRead = audioStream.read(buffer, 0, buffer.length)) != -1) {
                sourceLine.write(buffer, 0, bytesRead);
            }
            audioStream = AudioSystem.getAudioInputStream(audioFile);
            

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

	public static void playMenuSwitch() {
		new Thread(new Runnable() {
			public void run() {
				playGameSound("Assets/menuswitch.wav");
			}
		}).start();
	}
    
	public static String getRoundedDouble(double value) {
        DecimalFormat df = new DecimalFormat("0.0");
        String seconds_past_display = df.format(value);
		return seconds_past_display;
	}

    public static void displayHeader(String headerTitle, String subheading) {
	displayGraphicalText(colony_name, customFont.deriveFont(50f), Color.GREEN, 10, 45);
	displayGraphicalText("----------------- " + headerTitle + " -----------------", customFont.deriveFont(35f), Color.CYAN, 10, 85);
	displayGraphicalText(subheading, new Font("Consolas", Font.PLAIN, 20), Color.YELLOW, 10, 110);
    }

    public static void displayStartingScreen () {
	c.clear ();

	// Include a nice background image
	displayBackgroundImage ("Assets/stars.jpg");

	// Write the static messages (they do not require a loop)
	displayGraphicalText ("Welcome To", new Font ("Consolas", Font.BOLD, 60), Color.GREEN, 445, 80);
	displayGraphicalText ("Starbound", customFont.deriveFont (150f), Color.CYAN, 100, 250);
	displayGraphicalText ("Empires", customFont.deriveFont (150f), Color.CYAN, 245, 430);
	displayGraphicalText ("Press Any Key to Start", new Font ("OCR A Extended", Font.BOLD, 45), Color.YELLOW, 310, 525);
	displayGraphicalText ("Brought to you by Jerry Li and Jerry Chen", new Font ("OCR A Extended", Font.PLAIN, 25), Color.GREEN, 315, 600);
    }

    public static void displayRules ()
    {
    displayBackgroundImage("Assets/stars.jpg");
    displayGraphicalText(colony_name, customFont.deriveFont(50f), Color.CYAN, 10, 45);
	displayGraphicalText ("Instructions:", new Font ("OCR A Extended", Font.BOLD, 30), Color.YELLOW, 10, 100);
	displayGraphicalText ("1. You are the leader of a colony in the Starbound Empires universe. You start on Earth.", new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 135);
	displayGraphicalText ("2. You must manage your material resources: stellar reserves and energy. Resources update every 10 seconds.", new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 170);
	displayGraphicalText ("3. You must also manage your human resources: soldiers, workers, and doctors.", new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 205);
	displayGraphicalText ("4. Soldiers will help you conquer new regions.", new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 240);
	displayGraphicalText ("5. Workers will increase your production of material resources.", new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 275);
	displayGraphicalText ("6. Doctors will increase your population growth.", new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 310);
	displayGraphicalText ("7. The game will become more familiar as you play.", new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 345);
	displayGraphicalText ("Press any key to continue...", new Font ("OCR A Extended", Font.BOLD, 25), Color.YELLOW, 10, 380);
    }

    public static void initializeGame ()
    {
	// Set the text color, background color, and font for the console to match our game theme
	c.setTextBackgroundColor (Color.BLACK);
	c.setTextColor (Color.WHITE);
	c.setColor(Color.BLACK);

	customFont = null;

	try
	{
	    String fontPath = "Assets/gamefont.ttf";
	    File fontFile = new File (fontPath);
	    InputStream fontStream = new FileInputStream (fontFile);
	    customFont = Font.createFont (Font.TRUETYPE_FONT, fontStream);
	}
	catch (FontFormatException e)
	{
	    e.printStackTrace ();
	    System.err.println ("The font file is not in the correct format. Please ensure that it is a .ttf file in the Assets folder.");
	}
	catch (IOException e)
	{
	    e.printStackTrace ();
	    System.err.println ("There was an I/O error when reading the font file. Please ensure that it is a .ttf file in the Assets folder.");
	}
    }

    public static void displayValues ()
    {
	// Calculating max width so we can display better
	int maxWidth;
	maxWidth = 0;

	String output_lines[] = {
	    "Stellar Reserves [MT]: " + resourceCounter [0],
	    "Energy [GJ]: " + resourceCounter [1],
	    "Population: " + peopleCounter [0] + peopleCounter [1] + peopleCounter [2] + peopleCounter [3],
	    "Unemployed: " + peopleCounter [0],
	    "Workers: " + peopleCounter [1],
	    "Soldiers: " + peopleCounter [2],
	    "Doctor: " + peopleCounter [3],
	    "Population Capacity: " + populationCaps [currentRegion],
	    "Stellar Reserves Production Rate [MT/s]: " + stellar_reserves_production_rate,
	    "Energy Production Rate [GJ/s]: " + energy_production_rate,
	    "Population Growth Rate [people/s]: " + population_growth_rate
	    };

	for (int i = 0 ; i < output_lines.length ; i++)
	    maxWidth = Math.max (maxWidth, output_lines [i].length ());

	// Construct the dash string using an algorithm that dynamically adjusts to the max width
	String dashes = repeat ("-", Math.max (5, (maxWidth - 20 + maxWidth % 2) / 2) + 1);
	displayGraphicalText(dashes + " MATERIAL RESOURCES " + dashes, new Font ("Consolas", Font.BOLD, 20), Color.YELLOW, 10, 150);
	displayGraphicalText(output_lines[0], new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 180);
	displayGraphicalText(output_lines[1], new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 210);

	displayGraphicalText(dashes + "-- HUMAN RESOURCES --" + dashes, new Font ("Consolas", Font.BOLD, 20), Color.YELLOW, 10, 240);
	displayGraphicalText(output_lines[2], new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 270);
	displayGraphicalText(output_lines[3], new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 300);
	displayGraphicalText(output_lines[4], new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 330);
	displayGraphicalText(output_lines[5], new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 360);
	displayGraphicalText(output_lines[6], new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 390);

	displayGraphicalText(dashes + "-- LIMITS & RATES --" + dashes, new Font ("Consolas", Font.BOLD, 20), Color.YELLOW, 10, 420);
	displayGraphicalText(output_lines[7], new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 450);
	displayGraphicalText(output_lines[8], new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 480);
	displayGraphicalText(output_lines[9], new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 510);
	displayGraphicalText(output_lines[10], new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 540);

	displayGraphicalText("________________________________________________", new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 570);
	displayGraphicalText("Time [S]: ", new Font ("Consolas", Font.PLAIN, 20), Color.WHITE, 10, 610);
	displayGraphicalText(getRoundedDouble(seconds_past), new Font ("Consolas", Font.BOLD, 20), Color.BLACK, 115, 611);
    }

    public static void displayPopulation ()
    {
	for (int i = 0 ; i < 4 ; i++)
	{
	    c.println (professionNames [i] + ": " + peopleCounter [i]);
	}
    }

    public static void startMenu ()
    {
	displayStartingScreen ();
	c.getChar ();
	currentMenu = 1; // progress to the next screen
	// Remove the starting screen in a cool way using a for loop and a delay
	for (int i = 0 ; i < 29 ; i++)
	{
	    c.println ();
	    try
	    {
		Thread.sleep (15);
	    }
	    catch (InterruptedException e)
	    {
		e.printStackTrace ();
	    }
	}

	c.setCursor (1, 1);
    }

    public static void nameMenu () {
		playMenuSwitch();
	do
	{
	    c.print ("What is your colony name (max 30 characters)? ");
	    colony_name = c.readLine ();
	    c.clear ();
	}
	while (colony_name.length () > 30 || colony_name.length () < 1);
	currentMenu = 2;
    }

    public static void rulesMenu ()
    {
		playMenuSwitch();
	displayRules ();
	c.getChar ();
	// clear console
	for (int i = 0 ; i < 29 ; i++)
	{
	    c.println ();
	    try
	    {
		Thread.sleep (10);
	    }
	    catch (InterruptedException e)
	    {
		e.printStackTrace ();
	    }
	}
	c.setCursor (1, 1);
	currentMenu = 3;
    }

    public static void mainMenu ()
    {
	if (iterations % 100 == 0 || image_not_shown) {
		c.clear();
		displayBackgroundImage("Assets/planet_test.png");
		image_not_shown = false;
	} else {
		c.clearRect(112, 593, 40 + 10*((int) (Math.log(seconds_past)/Math.log(10))), 23);
	}
	displayHeader("Main Menu", "[P] - Population Menu | [M] - Region Map");
	
	displayValues ();

	if (c.isCharAvail ())
	{
	    char pressed = c.getChar ();
	    if (pressed == 'p')
	    {
			c.clear();
			playMenuSwitch();
		currentMenu = 4;
	    }
	    else if (pressed == 'm')
	    {
			c.clear();
			playMenuSwitch();
		currentMenu = 5;
		pressed = 0;
	    }
	}
    }

    public static void populationMenu ()
    {
	c.setCursor (6, 1);
	displayHeader("Population Menu", "[Z] - Back | [S] - Switch Professions (costs " + switchCost + " GJ per person)");

	c.println ("Population Menu\n");
	displayPopulation ();
	c.println("\nEnergy: " + resourceCounter [1] + " GJ");
	c.println("Energy Production Rate: " + energy_production_rate + " GJ/s");
	c.println("\n");

	if (c.isCharAvail ())
	{
	    char pressed = c.getChar ();
	    if (pressed == 'z')
	    {
			image_not_shown = true;
			playMenuSwitch();
			c.clear();
		currentMenu = 3;
	    }
	    else if (pressed == 's')
	    {
		int switchFrom, switchTo, numSwitch;

		c.println ("Profession Switcher");
		c.println ("Enter profession to switch from: [0] - Unemployed | [1] - Worker | [2] - Soldier | [3] - Doctor");
		switchFrom = inputNumber (0, 3);

		c.println ("Enter profession to switch to: [0] - Unemployed | [1] - Worker | [2] - Soldier | [3] - Doctor");
		switchTo = inputNumber (0, 3);

		c.println ("Enter how many people to switch: ");
		// at most, you can switch all your people, or you can switch until you have no energy left
		// thus, we must use a min function to see which one we are constrained by
		// we calculate the max number of switches we can do with the exponential cost function
		int max_energy_switches = 0;
		long temp_switch_cost = switchCost, temp_energy = resourceCounter [1];
		while (temp_energy > temp_switch_cost) {
			++max_energy_switches;
			temp_energy -= temp_switch_cost;
			temp_switch_cost *= (long) ((double) temp_switch_cost * 1.25);
		}

		numSwitch = inputNumber (0, min (peopleCounter [switchFrom], max_energy_switches));

		long required_cost = 0;
		for (int i = 0; i < numSwitch; i++) {
			required_cost += switchCost;
			switchCost *= (long) ((double) switchCost * 1.25);
		}

		// perform the switch
		peopleCounter [switchFrom] -= numSwitch;
		peopleCounter [switchTo] += numSwitch;
		resourceCounter [1] -= required_cost;
	    }
	}
    }

    public static void regionMap ()
    {
	boolean canProgress;

	c.setCursor (6, 1);
	displayHeader("Map", "[Z] - back");

	c.println ("Regions Map");
	c.println ("Current Region: " + regionNames [currentRegion]);
	c.println ("Population Capacity: " + populationCaps [currentRegion]);

	if (soldiersNeeded [currentRegion + 1] > peopleCounter [2] || reservesNeeded [currentRegion + 1] > resourceCounter [0] || currentRegion == regionNames.length - 1)
	    canProgress = false;
	else
	    canProgress = true;

	c.println ();

	if (currentRegion != regionNames.length - 1)
	{ // there are still more regions to conquer
	    c.println ("Next Region: " + regionNames [currentRegion + 1]);
		c.println ("Reserves Needed: " + reservesNeeded [currentRegion + 1] + " MT");
		c.println ("Soldiers Needed: " + soldiersNeeded [currentRegion + 1] + " soldiers");

	    if (canProgress)
		c.println ("Press [C] to conquer the next region");
	    else
		c.println ("You do not have enough resources/people to conquer the next region.");
	}
	else
	{
	    c.println ("There are no more regions to conquer. You have beaten the game!");
	}
	if (c.isCharAvail ())
	{
	    char pressed = c.getChar ();
	    if (pressed == 'z')
	    {
			image_not_shown = true;
			playMenuSwitch();
			c.clear();
		currentMenu = 3;
	    }
	    else if (pressed == 'c' && canProgress)
	    {
			long reserves_payment = reservesNeeded [currentRegion + 1];
			long soldiers_payment = soldiersNeeded [currentRegion + 1];

			boolean caughtOffGuard = randomEvent(0.25);
			boolean underestimated = randomEvent(0.35);

			double adjustmentFactor = Math.random() * 0.2 + 0.1;
			double adjustment;
			if (caughtOffGuard || underestimated) {
				String outcome;
				if (caughtOffGuard)
					outcome = "caught off guard by your powerful military strategy! You have conquered the region with ease, and the amounts needed are decreased.";
				else
					outcome = "underestimated, and a bloody battle ensues! The amounts needed are increased.";
				c.println("The alien forces on " + regionNames [currentRegion + 1] + " were " + outcome);
				if (caughtOffGuard)
					adjustment = -adjustmentFactor;
				else
					adjustment = adjustmentFactor;
				reserves_payment += reserves_payment * adjustment;
				soldiers_payment += soldiers_payment * adjustment;
			} else {
				c.println("The alien forces on " + regionNames [currentRegion + 1] + " have put up some resistance, but you have conquered the region!");
			}
			
			if (underestimated && (resourceCounter [0] < reserves_payment || peopleCounter[2] < soldiers_payment)) {
				c.println("After the amounts needed were increased, you do not have enough resources to conquer " + regionNames [currentRegion + 1] + ". Your soldiers and population have been decimated, and your stellar reserves and energy have been emptied.");

				// Set these to zero
				resourceCounter [0] = 0;
				resourceCounter [1] = 0;
				peopleCounter[2] = 0;
				peopleCounter[0] = 0;

				// Decrease workers and doctors by a random large percentage
				peopleCounter[1] -= peopleCounter[1] * (Math.random() * 0.5 + 0.2);
				peopleCounter[3] -= peopleCounter[3] * (Math.random() * 0.5 + 0.2);
			} else {
				c.println("You have successfully conquered " + regionNames [currentRegion + 1] + "! Press any key to continue.");
				c.getChar();
				resourceCounter [0] -= reserves_payment;
				peopleCounter[2] -= soldiers_payment;
				++currentRegion;
			}

		currentRegion++;
		resourceCounter [0] -= reservesNeeded [currentRegion];
		peopleCounter [2] -= soldiersNeeded [currentRegion];

		c.println ("You have conquered " + regionNames [currentRegion]);
		c.println ("Press any key to continue");
		c.getChar ();
	    }
	}
    }


    // tick functions
    // ticks are called periodically and they update the game
    // short ticks happen every 0.1s
    // long ticks happen every 10s
    public static void shortTick ()
    {
	stellar_reserves_production_rate = peopleCounter [1] * 0.1 + 0.1; // workers * 0.1 = rate
	energy_production_rate = peopleCounter [1] * 5; // workers * 5 = GJ/s
	population_growth_rate = peopleCounter [3] * 0.1 + 0.1; // base rate of 0.1 plus 0.1 for every doctor
    }


    public static void longTick ()
    {
	int finalPopulation;

	resourceCounter [0] += stellar_reserves_production_rate * 10;
	peopleCounter [0] += population_growth_rate * 10;
	resourceCounter [1] += energy_production_rate * 10;
	finalPopulation = peopleCounter [0] + peopleCounter [1] + peopleCounter [2] + peopleCounter [3];
	if (finalPopulation > populationCaps [currentRegion])
	{
	    peopleCounter [0] -= finalPopulation - populationCaps [currentRegion]; // adjust to population cap
	}
    }


    public static void main (String[] args)
    {
		playBackgroundMusic();
	c = new Console (29, 115, 18, "Starbound Empires"); // Initialize the console
	initializeGame ();

	// Keeping the main thread alive to keep the application running
	while (running)
	{
	    shortTick ();
	    if (iterations % 100 == 0)
		longTick (); // every 100 iterations, i.e 10s, a long tick is called
	    if (currentMenu == 0)
		startMenu ();
	    else if (currentMenu == 1)
		nameMenu ();
	    else if (currentMenu == 2)
		rulesMenu ();
	    else if (currentMenu == 3) {
			mainMenu ();
		}
	    else if (currentMenu == 4)
		populationMenu ();
	    else if (currentMenu == 5)
		regionMap ();
	    try
	    {
		Thread.sleep (80);
		seconds_past += 0.1;
		iterations++;
	    }
	    catch (InterruptedException e)
	    {
		e.printStackTrace ();
	    }
	}
    }
}
