// Imports
import java.awt.*; // Fonts and colors
import java.io.*; // Reading files (such as image and audio files)
import javax.sound.sampled.*; // Play sounds


import hsa.Console; // Console
import javax.imageio.ImageIO; // Processing images
import java.text.DecimalFormat; // Rounding decimals
import java.lang.Math; // Math functions


public class Main {
    // The HSA console from the very beginning! Hello from the past!
    static Console c;
   
    // Font and image variables
    static Font customFont; // The custom font to be used to display fancy text
    static Image background1, background2; // The two background images
    static SourceDataLine effectSourceLine, musicSourceLine; // Stores data lines of audio files and acts as a source to the mixer
    static AudioInputStream effectAudioStream, musicAudioStream; // An array of bytes that represents audio input data
    static DataLine.Info effectInfo, musicInfo; // Additional information on the SourceDataLine that represents audio formatting


    // Game variables, values will be set later, except for the arrays
    static long iterations, // The number of frames since the start of the game
		switchCost; // The cost to switch professions


    static int iterationsSinceLastCollect, // The number of frames since the last mine collection
	       currentRegion, // The current region the player is in
	       mineLevel, // The current level of the mine
	       developerMultiplier, // The multiplier for debug mode
	       currentMenu; // The current menu the player is in


    /**
     * 0 - Start menu
     * 1 - Select name menu
     * 2 - Rules menu
     * 3 - Main menu
     * 4 - Population menu
     * 5 - Regions map
    */


    static int[] peopleCounter = {0, 1, 0, 0}, // The number of people in each profession
		 resourceCounter = {0, 0}; // The amount of each resource


    static double secondsPast, // The number of seconds since the start of the game
		  stellarReservesProductionRate, // The rate of stellar reserve production
		  energyProductionRate, // The rate of energy production
		  populationGrowthRate; // The rate of population growth


    static String musicPath, // The path to the background music
		  colonyName; // The name of the colony that the user inputs
   
    static boolean imageNotShown, // A boolean that checks if the main menu image has been shown
		   first, // A boolean that checks if a value is being inputted for the first time
		   debug; // A boolean that checks if debug mode is enabled
   
    // Game constants, value is initially given
    static final long[] mineCollectionTimes = {75, 71, 67, 63, 59, 55, 51, 47, 43, 39, 35}, // The time in ms it takes to collect resources from the mines


		       soldiersNeeded = {0, 0, 0, 0, 0, 0, 0, 500, 1000, 5000, 10000, 12000, 20000, 25000, 33000, 40000, 55000, 60000, 65000}, // The number of soldiers needed to conquer each region


		       reservesNeeded = {0, 0, 50, 300, 1000, 5000, 10000, 50000, 100000, 500000, 1000000, 2000000, 3000000, 4000000, 6000000, 10000000, 20000000, 40000000, 694200000}, // The number of stellar reserves needed to conquer each region


		       populationCaps = {10, 20, 40, 80, 200, 300, 1000, 3000, 10000, 15000, 20000, 30000, 40000, 50000, 60000, 80000, 80000, 1000000}, // The population capacity of each region


		       mineUpdateReservesNeeded = {0, 50, 250, 1000, 3500, 10000, 50000, 350000, 1000000, 5000000, 25000000}, // The number of stellar reserves needed to upgrade the mines


		       mineUpdateEnergyNeeded = {0, 500, 2500, 7500, 30000, 150000, 500000, 2500000, 7500000, 25000000, 150000000}; // The amount of energy needed to upgrade the mines


    static final double[] regionMineMultipliers = {1.0, 1.25, 1.5, 1.75, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0}; // The multiplier for the mine production rate of each region


    static final String[] professionNames = {"Unemployed", "Worker", "Soldier", "Doctor"}, // The names of the professions


			  resourceNames = {"Stellar Reserve", "Energy"}, // The names of the resources


			  regionNames = {"Earth", "Mars", "Asteroid belt", "Jupiter", "Saturn", "Uranus", "Oort cloud", "Planet X", "Proxima Centauri B", "Ross 128b", "Hoth", "SPAS-12", "Gliese x7x", "Groza-S", "Agamar", "Wayland", "SR-25", "Awajiba"}, // The names of the regions


			  mineTierNames = {"Basic", "Advanced", "Thriving", "Industrial", "Global", "Interstellar", "Universal", "Cosmic", "Ethereal", "Omega", "Awajiba"}, // The names of the mine tiers
		       
			  successfullConquerMessages = { // The messages displayed when a region is conquered
			    "",
		   
			    "Your exploratory fleet lands on Mars, unlocking the second pillar of human existence. The red planet, rich with iron oxides, now bows to your dominion. The mineral-laden Martian soil yields to your advanced mining techniques, boosting your resources significantly.",
		   
			    "Your fleet ventures into the asteroid belt, a glittering treasure trove of minerals and resources. Each asteroid, a dormant giant of wealth, now falls under your control. Your mining operations thrive amidst the cosmic debris, transforming the raw ore into invaluable assets.",
		   
			    "Your fleet arrives at Jupiter, the colossal gas giant. Beneath its turbulent clouds and swirling storms lies a realm of untapped potential. With Jupiter under your command, your mining ventures delve deep into its metallic hydrogen core, harvesting unimaginable riches.",
		   
			    "Your fleet navigates through the majestic rings of Saturn, the enigmatic gas giant. The planet's ethereal beauty hides a wealth of resources now under your control. Enhanced mining techniques unlock the secrets of Saturn's rings, yielding a bounty of precious minerals. It's many moons now expand your population capacity and reach.",
		   
			    "Your fleet breaches the icy atmosphere of Uranus, the famous ice giant. This distant world, veiled in mystery, now answers to your might. Your mining operations penetrate its frigid depths, extracting rare elements and compounds to fuel your empire.",
		   
			    "Your fleet reaches the Oort Cloud, a distant frontier of icy planetesimals and celestial wanderers. This cosmic expanse, on the edge of the solar system, now submits to your authority after you conquer it. Your mining fleets capture and harvest the ancient icy bodies, securing vast amounts of resources from the fallen defenders.",
		       
			    "Your fleet uncovers the enigmatic Planet X, shrouded in myth and mystery on the solar system's outermost fringe. The dark and forbidding surface, once guarded by hostile extraterrestrial beings, is now conquered. Your mining operations reveal rare and unknown minerals amidst the ruins of alien fortresses, bolstering your empire's wealth.",
			   
			    "Your fleet ventures to Proxima Centauri B, an alien world in the Alpha Centauri system. This distant planet, bathed in the light of a foreign sun, now falls under your command after a decisive event. Your mines tap into its rich mineral veins, enhancing your resource reserves exponentially from the conquered land.",
			   
			    "Your fleet reaches Ross 128b, a planet orbiting the red dwarf star Ross 128. Amidst its rocky terrain and crimson skies, your dominion extends following several exploratory expansions. Advanced mining technology extracts the planet's abundant resources.",
			   
			    "Your fleet arrives on Hoth, a frozen wasteland in a galaxy far, far away. You conquer the harsh climate and icy surface. Your mines delve deep into the frozen crust, uncovering precious resources buried beneath the perpetual snow and ice, remnants of a bygone alien civilization.",
			   
			    "Your fleet lands on SPAS-12, a planet in a parallel universe, its reality intertwined with your own. After landing on the surface and building reality-anchoring settlements, the planet's alien landscape is now under your control. Your advanced mining operations unearth bizarre and valuable materials, augmenting your empire's wealth.",
			   
			    "Your fleet explores Gliese x7x, a distant world within the vast Gliese system. The planet's unique geological formations now hold untold riches under your dominion. After a mission, your mining operations extract exotic minerals from the ancient alien strongholds, significantly boosting your resource base.",
			   
			    "Your fleet secures Groza-S, a planet in the tempestuous Groza Nebula, a region teeming with volatile storms and alien lifeforms. Amidst its storm-ravaged surface, your control brings order after a diplomatic meeting with the alien species. Your mining technology harnesses the planet's volatile resources, transforming them into invaluable assets for your empire.",
			   
			    "Your fleet arrives on Agamar, a planet steeped in lore within the transdimensional Agamar Cluster. The ancient world, now under your rule after a swift conquest, reveals its hidden treasures. Advanced mining techniques unearth rich deposits of rare minerals and alien artifacts, enhancing your empire's wealth from the mysterious land.",
			   
			    "Your fleet lands on Wayland, a planet existing simultaneously in multiple realities within the Wayland Nexus. The planet's diverse landscapes and hidden caverns, are now yours to explore after a series of dangerous landings. Your mining operations tap into vast mineral reserves, fortifying your resource base while navigating the complexities of its parallel dimensions.",
			   
			    "Your fleet reaches SR-25, a planet orbiting a distant star in the SR-25 Expanse, an area known for its rich history of alien culture. Its rugged terrain and harsh conditions fall under your dominion after a swift landing and recon mission. Your advanced mining technology extracts valuable resources from its core, bolstering your empire's strength.",
			   
			    "Your fleet reaches Awajiba, a planet of legend in the Awajiba Continuum, a realm where the boundaries between realities blur. The planet's ethereal beauty and untold mysteries now bow to your might after you the various time crystals surrounding it. Your mines delve deep into its enchanted crust, unearthing resources of unparalleled value and mystical properties. The whispers of Awajiba's ancient secrets and alien knowledge now serve your empire, marking an ultimate turning point in your cosmic conquest. The black hole at the heart of the Awajiba Continuum beckons, promising untold power and peril for those who dare to venture into its depths."
			},
		       
			failedConquerMessages = { // The messages displayed when a region is failed to be conquered
			    "",
		       
			    "Your exploratory fleet lands on Mars, but the harsh Martian environment and unknown alien threats prove insurmountable. The red planet remains unconquered, its mineral riches beyond your grasp. Your fleet retreats, bearing the scars of a failed mission.",
		       
			    "Your fleet ventures into the asteroid belt, but the treacherous conditions and unpredictable trajectories of asteroids thwart your efforts. The glittering trove of minerals remains out of reach as your mining operations falter and withdraw.",
		       
			    "Your fleet arrives at Jupiter, but the colossal gas giant's extreme weather and unseen dangers defeat your attempts. The potential riches within its core elude you as your fleet is forced to abandon the mission.",
		       
			    "Your fleet navigates through the rings of Saturn, but the planet's mysterious beauty conceals perilous threats. Toxic fumes and absolute zero temperatures overwhelm your fleet, leaving Saturn's resources untouched and your forces in retreat.",
		       
			    "Your fleet breaches the icy atmosphere of Uranus, only to face overwhelming challenges from the planet's harsh climate and sharp diamond rain. The mission fails, and your fleet withdraws, unable to tap into its rare elements.",
		       
			    "Your fleet reaches the Oort Cloud, but the hypersonic metears destroy your spaceships. The icy bodies and their resources remain under the control of the guardians as your fleet retreats.",
		       
			    "Your fleet uncovers the enigmatic Planet X, but the endless volcanos and volatile geological activity prove too formidable. The exploratory missions end in failure, and your mining operations never commence. Planet X remains a distant and unreachable myth.",
		       
			    "Your fleet ventures to Proxima Centauri B, but they run out of fuel halfway through. Due to an extremely silly mistake in calculations (21-9 = 11), your fleet withdraws, leaving the planet's mineral veins untapped.",
		       
			    "Your fleet reaches Ross 128b, but the lack of gravity throws off the landings. Amidst the rocky terrain and crimson skies, your spaceships crash, leaving the planet's resources unused.",
		       
			    "Your fleet arrives on Hoth, but the extreme climate and absolute zero temperatures thwart your conquest. The frozen wasteland remains unconquered, its precious resources buried beneath perpetual snow and ice, beyond your reach.",
		       
			    "Your fleet lands on SPAS-12, but the planet's various wormholes teleport your fleet back to Hoth. The alien landscape remains unconquered as your fleet looks at Hoth in confusion, unable to comprehend what happened.",
		       
			    "Your fleet explores Gliese x7x, but the radiation belts corrupt your computers. The unique geological formations and their untold riches remain untouched as your fleet withdraws.",
		       
			    "Your fleet secures Groza-S, but the volatile storms and mini black-holes prove too much. The tempestuous planet's resources remain inaccessible as your forces retreat.",
		       
			    "Your fleet arrives on Agamar, but the landing site was badly picked and the spaceships fell into a neutron star. Your advanced mining techniques falter and the remnants your fleet retreat.",
		       
			    "Your fleet lands on Wayland, but the quasars and pulsars melt your spaceships due to a lack of heat-resistant material. The diverse landscapes and hidden caverns remain unexplored as your fleet retreats, unable to tap into its vast mineral reserves.",
		       
			    "Your fleet reaches SR-25, but the relativistic 7.62mm rocks pierce the hulls of your fleet. The planet's natural phenomena and harsh conditions remain unyielding as your advanced mining technology is rendered useless.",
		       
			    "Your fleet reaches Awajiba, but the infamous black hole sucks in a large portion of your fleet. Your scientists watch in despair as the ships approach nothingness, and the survivors are all teleported back to SR-25."
			};


    /************************ HELPER FUNCTIONS ************************/
    // Function that displays graphical text on the console, with the message, font, color, x position, and y position as parameters
    public static void displayGraphicalText(String message, Font font, Color fontColor, int xPos, int yPos) {
	c.setFont(font); // Set the font to the custom font
	c.setColor(fontColor); // Set the color to the specified color
	c.drawString(message, xPos, yPos); // Draw the graphical text on the console to the specified position
    } // end of displayGraphicalText


    // Function that pauses the program for a variable amount milliseconds, used for delaying the program execution for various purposes such as framerate and slow printing
    public static void sleep(int sleepTimeMs) {
	// Try catch block to catch any exceptions that may occur when the thread is sleeping
	try {
	    Thread.sleep(sleepTimeMs); // Pause the program for the specified amount of milliseconds
	} catch (InterruptedException error) {
	    error.printStackTrace();
	}
    } // end of sleep


    // Function that prints a string character by character with a delay of 50ms, for added suspense and user experience
    public static void slowPrint(String message) {
	// Loop through each character in the message
	for (int i = 0; i < message.length(); i++) {
	    c.print(message.charAt(i)); // Print the character
	    sleep(50); // Pause program for 50ms
	}
    } // end of slowPrint


    // A helper function that repeats a string s n times, used for dynamic printing of dashes
    public static String repeat (String repeatStr, long repetitions) {
	String result = ""; // Initialize an empty string
	for (int i = 0 ; i < repetitions; i++)
	    result += repeatStr; // Append the string s to the result n times


	// Return the result string
	return result;
    } // end of repeat


    // A helper function that reads an integer from the user, and ensures that it is within the range [lowerBound, upperBound], with proper invalid checks
    public static int inputNumber (int lowerBound, int upperBound) {
	int inputNum; // Initialize the input number variable
	// INPUT and PROCESSING model
	do { // Loop until a valid input is received, we use a do-while loop to ensure that the loop runs at least once
	    // Try to read an integer from the user, and catch any exceptions that may occur (these exceptions are thrown when the user does not input an integer)
	    try {
		// Read an integer from the user and check if it is within the specified range
		inputNum = c.readInt();
		if (inputNum < lowerBound || inputNum > upperBound)
		    c.println("Invalid input. Must be between " + lowerBound + " and " + upperBound);
	    } catch (NumberFormatException error) {
		// Notify the user that their input is invalid
		c.println("Invalid input. Must be an integer.");
		inputNum = lowerBound - 1;
	    }
	} while (inputNum < lowerBound || inputNum > upperBound);


	// Return the valid input number
	return inputNum;
    } // end of inputNumber


    // Function that handles generic audio errors by displaying an error message and exiting the program
    public static void handleAudioError() {
	c.clear(); // Clear the console
	// Display an error message to the user, informing them of the audio error and how to resolve it, this would be an OUTPUT model
	c.println("There was an error while playing the audio. It is recommended to exit the program and ensure that the following audio files are located in the Assets folder:\n- menuswitch.wav\n- success.wav\n- mine.wav\n\nAlternatively, you can continue the program but audio won't be played, and this error message will keep popping up.\n\nPress any key to continue the program.");
	c.getChar(); // Wait for the user to press a key before continuing the program
    } // end of handleAudioError


    /************************ GAME FUNCTIONS ************************/
    // Basic function for playing an audio with path soundPath
    public static void playGameEffect(String soundPath) {
	// Declare variables for the audio stream, data line, and volume control
	FloatControl volumeControl;


	// Close the previous audio stream and data line if they exist by setting them to null
	effectAudioStream = null;
	effectSourceLine = null;
	effectInfo = null;
	volumeControl = null;


	// Load the audio file, create a data line, and set the volume, with proper error handling
	try {
	    // Load the audio file and get the audio stream and data line information
	    effectAudioStream = AudioSystem.getAudioInputStream(new File(soundPath));
	    effectInfo = new DataLine.Info(SourceDataLine.class, effectAudioStream.getFormat());


	    // Intialize source lines
	    effectSourceLine = (SourceDataLine) AudioSystem.getLine(effectInfo);
	    effectSourceLine.open(effectAudioStream.getFormat());


	    // Change voume
	    volumeControl = (FloatControl) effectSourceLine.getControl(FloatControl.Type.MASTER_GAIN);
	    volumeControl.setValue(-25.0f);
	} catch (UnsupportedAudioFileException e) {
	    handleAudioError();
	} catch (LineUnavailableException e) {
	    handleAudioError();
	} catch (IOException e) {
	    handleAudioError();
	}


	// Play the audio file in a new thread to prevent program execution blocking
	new Thread(new Runnable() {
	    public void run() {
		try {
		    // Initialize variables for reading the audio file and writing to the data line
		    int bytesRead = 0;
		    effectSourceLine.start();
		    byte[] buffer = new byte[4096];


		    // Read the audio file and write it to the data line
		    while (bytesRead != -1) {
			// Read the audio file and write it to the data line, stopping when the audio file has finished playing
			bytesRead = effectAudioStream.read(buffer, 0, buffer.length);
			if (bytesRead >= 0)
			    effectSourceLine.write(buffer, 0, bytesRead);
		    }


		    // Close the audio stream and data line after the audio file has finished playing to prevent memory leaks
		    effectSourceLine.drain();
		    effectSourceLine.close();
		    effectAudioStream.close();
		} catch (IOException error) {
		    handleAudioError();
		}
	    }
	}).start();
    } // end of playGameEffect


    // Play the background music, this may look similar to playGameEffect() at first, but due to the background music needing looping, while effects do not, so modifications are needed, therefore a seperate function is required. The contents are mostly the same, but an additional while loop and logic is added. Note that the user cannot specify the audio path as well
    public static void playBackgroundMusic() {
	// Declare variables for the audio stream, data line, and volume control
	FloatControl volumeControl;


	// Close the previous audio stream and data line if they exist
	musicAudioStream = null;
	musicSourceLine = null;
	musicInfo = null;
	volumeControl = null;


	// Load the audio file, create a data line, and set the volume, with proper error handling
	try {
	    // Load the audio file and get the audio stream and data line information
	    musicAudioStream = AudioSystem.getAudioInputStream(new File("Assets/soundtrack.wav"));
	    musicInfo = new DataLine.Info(SourceDataLine.class, musicAudioStream.getFormat());


	    // Initialize source lines
	    musicSourceLine = (SourceDataLine) AudioSystem.getLine(musicInfo);
	    musicSourceLine.open(musicAudioStream.getFormat());


	    // Change volume
	    volumeControl = (FloatControl) musicSourceLine.getControl(FloatControl.Type.MASTER_GAIN);
	    volumeControl.setValue(-7.65f);
	} catch (UnsupportedAudioFileException e) {
	    handleAudioError();
	} catch (LineUnavailableException e) {
	    handleAudioError();
	} catch (IOException e) {
	    handleAudioError();
	}


	// Play the audio file in a new thread to prevent program execution blocking
	new Thread(new Runnable() {
	    public void run() {
		try {
		    int bytesRead = 0;
		    musicSourceLine.start();
		    byte[] buffer = new byte[4096];


		    // Here is the additional while loop that checks if the audio file has finished playing, and if so, restarts it
		    while (true) {
			// Read the audio file and write it to the data line
			bytesRead = musicAudioStream.read(buffer, 0, buffer.length);
			if (bytesRead >= 0)
			    musicSourceLine.write(buffer, 0, bytesRead);
			else {
			    // If the audio file has finished playing, restart it by closing the data line and audio stream, and then reopening them
			    musicAudioStream = AudioSystem.getAudioInputStream(new File("Assets/soundtrack.wav"));
			    musicSourceLine.drain();
			    musicSourceLine.close();
			    musicSourceLine = (SourceDataLine) AudioSystem.getLine(musicInfo);
			    musicSourceLine.open(musicAudioStream.getFormat());
			    musicSourceLine.start();
			}
		    }
		} catch (IOException e) {
		    handleAudioError();
		} catch (LineUnavailableException e) {
		    handleAudioError();
		} catch (UnsupportedAudioFileException e) {
		    handleAudioError();
		}
	    }
	}).start();
    } // end of playBackgroundMusic


    // Function that returns a string representing a rounded double to one decimal place, using a DecimalFormat object
    public static String getRoundedDouble(double value) {
	// Create a DecimalFormat object that rounds to one decimal place
	DecimalFormat formatter = new DecimalFormat("0.0");


	// Return the rounded double as a string
	return formatter.format(value);
    } // end of getRoundedDouble


    // Function is used to display the header of each menu. This is an OUTPUT method
    public static void displayHeader(String headerTitle, String subheading) {
	// Display the colony name and header title
	displayGraphicalText(colonyName, customFont.deriveFont(50f), Color.GREEN, 10, 45);
	displayGraphicalText("----------------- " + headerTitle + " -----------------", customFont.deriveFont(35f), Color.CYAN, 10, 85);


	// Display the subheading
	displayGraphicalText(subheading, new Font("Consolas", Font.PLAIN, 20), Color.YELLOW, 10, 110);
    } // end of displayHeader


    // This is the first thing that users will see, the starting screen. It displays the game title, the developers, and a prompt to start the game. This is an OUTPUT method
    public static void displayStartingScreen() {
	// Clear the console
	c.clear();


	// Include a nice background image
	c.drawImage(background1, 0, 0, null);


	// Write the welcome message
	displayGraphicalText("Welcome To", new Font ("Consolas", Font.BOLD, 60), Color.GREEN, 445, 80);
	displayGraphicalText("Starbound", customFont.deriveFont (150f), Color.CYAN, 100, 250);
	displayGraphicalText("Empires", customFont.deriveFont (150f), Color.CYAN, 245, 430);


	// Tell the user to press any key to start
	displayGraphicalText("Press Any Key to Start", new Font ("OCR A Extended", Font.BOLD, 45), Color.YELLOW, 310, 525);


	// Display the developers of the game (Jerry Li and Jerry Chen)
	displayGraphicalText("Brought to you by Jerry Li and Jerry Chen", new Font ("OCR A Extended", Font.PLAIN, 25), Color.GREEN, 315, 600);
    } // end of displayStartingScreen


    // This is the rules screen, which displays the rules of the game. It is displayed after the name and debug menu. This is an OUTPUT method
    public static void displayRules() {
	// Draw the background image
	c.drawImage(background1, 0, 0, null);


	// Write the rules of the game
	displayGraphicalText(colonyName, customFont.deriveFont(50f), Color.CYAN, 10, 45);
	displayGraphicalText("Instructions:", new Font ("OCR A Extended", Font.BOLD, 30), Color.YELLOW, 10, 100);
	displayGraphicalText("1. This is an idle game where you manage your space empire's resources.", new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 135);
	displayGraphicalText("2. The goal is to conquer new planets until you reach the famed Awajiba planet.", new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 170);
	displayGraphicalText("3. You have human resources (workers, soldiers, doctors) and ", new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 205);
	displayGraphicalText("   materials (energy, stellar reserves) to help you conquer planets.", new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 240);
	displayGraphicalText("   a. Conquering planets requires stellar reserves and soldiers.", new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 275);
	displayGraphicalText("   b. To create these stellar reserves, you will use your workers", new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 310);
	displayGraphicalText("      which generate stellar reserves as well as energy.", new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 345);
	displayGraphicalText("   c. Doctors increase the rate of population growth.", new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 380);
	displayGraphicalText("   d. Population starts out as unemployed, and if you want to turn them", new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 415);
	displayGraphicalText("      into soldiers or workers, you will need energy.", new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 450);
	displayGraphicalText("4. If you are short on resources, you can head over to your mines and collect stellar reserves and energy as well.", new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 485);
	displayGraphicalText("5. Each action is performed on a seperate menu. Nagivate through the menus", new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 520);
	displayGraphicalText("   by pressing the keys listed in the menu headings.", new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 555);


	// Tell the user to press any key to continue
	displayGraphicalText("Press any key to continue", new Font ("OCR A Extended", Font.BOLD, 25), Color.YELLOW, 10, 600);
    } // end of displayRules


    // Initialize the game variables and set the console theme to match the game. This function is called at the start of the program. This is a PROCESSING method
    public static void initializeGame() {
	// Set the text color, background color, and font for the console to match our game theme
	c.setTextBackgroundColor(Color.BLACK);
	c.setTextColor(Color.WHITE);
	c.setColor(Color.BLACK);


	// Load the custom font
	customFont = null;


	// Load the custom font from the Assets folder, with proper error handling
	try {
	    // Load the custom font from the Assets folder
	    String fontPath = "Assets/gamefont.ttf";
	    File fontFile = new File (fontPath);


	    // Create a FileInputStream object to read the font file
	    InputStream fontStream = new FileInputStream (fontFile);


	    // Create a custom font object from the font file
	    customFont = Font.createFont (Font.TRUETYPE_FONT, fontStream);
	} catch (FontFormatException error) {
	    error.printStackTrace ();
	} catch (IOException error) {
	    error.printStackTrace ();
	}


	// Load the background images
	background1 = null;
	background2 = null;
	try {
	    // Load the background images from the Assets folder
	    background1 = ImageIO.read(new File("Assets/stars.jpg"));
	    background2 = ImageIO.read(new File("Assets/planet.png"));
	} catch (IOException error) {
	    error.printStackTrace();
	}


	// Initialize game variables
	iterations = 0; // Initialize the number of iterations to 0
	iterationsSinceLastCollect = 0; // Initialize the number of iterations since the last mine collection to 0
	currentRegion = 0; // Initialize the current region to Earth (0)
	mineLevel = 0; // Initialize the mine level to Basic (0)
	currentMenu = 0; // Initialize the current menu to the starting screen (0)
	switchCost = 600; // Initialize the cost of switching regions to 600 energy
	developerMultiplier = 1; // Initialize the developer multiplier to 1 if debug mode is disabled
	secondsPast = 0.0; // Initialize the number of seconds passed to 0
	musicPath = "Assets/soundtrack.wav"; // Initialize the path to the background music
	imageNotShown = true; // Initialize the image not shown variable to true
    } // end of initializeGame


    // Displays the values for various variables such as resources, population, and production rates. This is displayed for the main menu. This is an OUTPUT method
    public static void displayValues() {
	// Calculating max width so we can display better
	int maxWidth;


	// Construct the output lines dynamically using an array and the game variables
	String outputLines[] = {
	    "Stellar Reserves [MT]: " + resourceCounter[0],
	    "Energy [GJ]: " + resourceCounter[1],
	    "Population: " + (peopleCounter[0] + peopleCounter[1] + peopleCounter[2] + peopleCounter[3]),
	    "Unemployed: " + peopleCounter[0],
	    "Workers: " + peopleCounter[1],
	    "Soldiers: " + peopleCounter[2],
	    "Doctor: " + peopleCounter[3],
	    "Population Capacity: " + populationCaps [currentRegion],
	    "Stellar Reserves Production Rate [MT/s]: " + getRoundedDouble(stellarReservesProductionRate),
	    "Energy Production Rate [GJ/s]: " + energyProductionRate,
	    "Population Growth Rate [people/s]: " + populationGrowthRate
	};


	// Dynamically calculate the max width of the output lines
	maxWidth = 0;
	for (int i = 0 ; i < outputLines.length ; i++)
	    maxWidth = Math.max (maxWidth, outputLines [i].length ());


	// Construct the dash string using an algorithm that dynamically adjusts to the max width
	String dashes = repeat ("-", Math.max (5, (maxWidth - 20 + maxWidth % 2) / 2) + 1);
	displayGraphicalText(dashes + " MATERIAL RESOURCES " + dashes, new Font ("Consolas", Font.BOLD, 20), Color.YELLOW, 10, 150);
	displayGraphicalText(outputLines[0], new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 180);
	displayGraphicalText(outputLines[1], new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 210);


	// Display the human resources section (population and professions)
	displayGraphicalText(dashes + "-- HUMAN RESOURCES --" + dashes, new Font ("Consolas", Font.BOLD, 20), Color.YELLOW, 10, 240);
	displayGraphicalText(outputLines[2], new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 270);
	displayGraphicalText(outputLines[3], new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 300);
	displayGraphicalText(outputLines[4], new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 330);
	displayGraphicalText(outputLines[5], new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 360);
	displayGraphicalText(outputLines[6], new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 390);


	// Display the limits and rates section (population cap, production rates)
	displayGraphicalText(dashes + "-- LIMITS & RATES --" + dashes, new Font ("Consolas", Font.BOLD, 20), Color.YELLOW, 10, 420);
	displayGraphicalText(outputLines[7], new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 450);
	displayGraphicalText(outputLines[8], new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 480);
	displayGraphicalText(outputLines[9], new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 510);
	displayGraphicalText(outputLines[10], new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 540);


	// Display the time passed
	displayGraphicalText("________________________________________________", new Font ("Consolas", Font.PLAIN, 20), Color.GREEN, 10, 570);
	displayGraphicalText("Time [S]: ", new Font ("Consolas", Font.PLAIN, 20), Color.WHITE, 10, 610);
	displayGraphicalText(getRoundedDouble(secondsPast), new Font ("Consolas", Font.BOLD, 20), Color.BLACK, 115, 611);
    } // end of displayValues


    // The function that displays the starting screen.
    public static void startMenu() {
	// Display the actual starting screen
	displayStartingScreen();
	c.getChar();
	currentMenu = 1; // progress to the next screen


	// Remove the starting screen in a cool way using a for loop and a delay
	for (int i = 0 ; i < 29 ; i++) {
	    c.println();
	    sleep(15); // Pause for 15ms
	}


	// Reset the cursor position
	c.setCursor (1, 1);
    } // end of startMenu


    // The function that displays the name menu, where the user can input their colony name and choose whether to enable debug mode
    public static void nameMenu() {
	// Play the menu switch sound effect
	// Auditory OUTPUT section
	playGameEffect("Assets/menuswitch.wav");


	// Display the name menu, and get the user's input for the colony name and debug mode
	// INPUT Section: prompts the user to input their colony name
	slowPrint("What is your colony name (max 30 characters)? ");
	colonyName = c.readLine(); // Reads the colony name input


	// Ensure that the colony name is between 1 and 30 characters, using proper invalid checking. This while loop only runs if the original input is invalid
	// INPUT Section Continued
	while (colonyName.length () > 30 || colonyName.length () < 1) {
	    // Clear the console and ask the user to input a valid colony name
	    c.clear();
	    c.print ("[INVALID NAME] What is your colony name (max 30 characters)? ");
	    colonyName = c.readLine();
	}


	// Clear the console and play the menu switch sound effect
	c.clear();
	// Auditory OUTPUT section
	playGameEffect("Assets/menuswitch.wav");
	char debugChoice;
       
	// Ask the user if they want to enable debug mode, and set the developer multiplier and debug flag accordingly. This is a different INPUT Section for debug mode
	c.print("Debug mode speeds up the game and gives you special multipliers, so that a developer/teacher can play through the game faster. Would you like to enable debug mode (y/n)? ");
	debugChoice = c.getChar();


	// Ensure that the user inputs a valid choice (y or n), using proper invalid checking. This while loop only runs if the original input is invalid
	// INPUT Section Continued
	while (debugChoice != 'y' && debugChoice != 'n') {
	    c.clear();
	    c.print("[INVALID CHOICE] Would you like to enable debug mode? (y/n) ");
	    debugChoice = c.getChar();
	}


	// Clear the console
	c.clear();


	// If the user chooses to enable debug mode, set the developer multiplier and debug flag accordingly
	// PROCESSING Section: sets the developer multiplier and debug flag based on the user's choice
	if (debugChoice == 'y') {
	    developerMultiplier = 5;
	    debug = true;
	    // OUTPUT Section, notifies the user that debug mode is enabled
	    slowPrint("Debug mode enabled. You have significantly increased framerate and developer multipliers. Press any key to continue.");
	    c.getChar();
	}


	// Progress to the next screen (rules menu)
	currentMenu = 2;
    } // end of nameMenu


    // The function that displays the rules menu, which displays detailed instructions on how to play the game
    public static void rulesMenu () {
	// Play the menu switch sound effect
	// Auditory OUTPUT section
	playGameEffect("Assets/menuswitch.wav");


	// Display the rules menu
	displayRules();


	// Wait for the user to press a key before transitioning
	c.getChar();


	// Clear console using another transition
	for (int i = 0 ; i < 29 ; i++) {
	    c.println();
	    sleep(15); // Pause for 15ms
	}


	// Reset the cursor position
	c.setCursor (1, 1);


	// Progress to the next screen (main menu)
	currentMenu = 3;
    } // end of rulesMenu


    // The main menu function, which displays the main menu of the game, showing the user's resources, population, and production rates
    public static void mainMenu () {
	// There are 2 cases where we clear the console and display the background image. The first case is every 100 frames (10 seconds), and the second case is when we switch back from another menu, and in this case, imageNotShown would be true. In both cases the background image needs to be redrawn
	if (iterations % 100 == 0 || imageNotShown) {
	    // Clear the console and display the background image
	    c.clear();
	    c.drawImage(background2, 0, 0, null);


	    // Reset imageNotShown to false
	    imageNotShown = false;
	} else {
	    // This rectangle is for the time passed, it will be cleared and redrawn every iteration, we use log functions to make the rectangle grow linearly to the number of digits in secondsPast
	    c.clearRect(112, 593, 40 + 15*((int) (Math.log(secondsPast)/Math.log(10))), 23);
	}


	// Display the main menu header and resource/population values
	displayHeader("Main Menu", "[P] - Population Menu | [R] - Region Map | [M] - View Mines");
	displayValues();


	// If the user presses a key, check if they pressed P, R, or M, and progress to the corresponding menu. The c.isCharAvail() function is used to determine if a key has been pressed, but without blocking the program execution.
	// INPUT + PROCESSING section
	if (c.isCharAvail()) {
	    char pressed = c.getChar(); // INPUT section to detect the keypress
	    // For each below case, we clear the console, play the menu switch sound effect, and progress to the menu that corresponds to the key pressed
	    // PROCESSING Section to redirect to different menus
	    if (pressed == 'p') {
		// Clear the console, play the menu switch sound effect, and progress to the population menu
		c.clear();
		// Auditory OUTPUT section
		playGameEffect("Assets/menuswitch.wav");
		currentMenu = 4;
		pressed = 0;
	    } else if (pressed == 'r') {
		// Clear the console, play the menu switch sound effect, and progress to the region map menu
		c.clear();
		// Auditory OUTPUT section
		playGameEffect("Assets/menuswitch.wav");
		currentMenu = 5;
		pressed = 0;
	    } else if (pressed == 'm') {
		// Clear the console, play the menu switch sound effect, and progress to the mine menu
		c.clear();
		// Auditory OUTPUT section
		playGameEffect("Assets/menuswitch.wav");
		currentMenu = 6;
		pressed = 0;
	    }
	}
    } // end of mainMenu


    // The population menu function, which displays the population of the colony, the energy, and the energy production rate. It also gives the user the option to switch professions
    public static void populationMenu () {
	// Reset the cursor position
	c.setCursor(6, 1);


	// Display the population menu header, with the option to go back to the main menu and switch professions
	displayHeader("Population Menu", "[Z] - Back | [S] - Switch Professions (costs " + switchCost + " GJ per person)");


	// Displays several relevant values, such as the population of each profession, the total population, the population cap, the energy, the energy needed to switch workers, the energy production rate, and the time passed.
	c.println("Population Menu\n");
	for (int i = 0; i < 4; i++)
	    c.println(professionNames[i] + ": " + peopleCounter[i]);
	c.println("Total Population: " + (peopleCounter[0] + peopleCounter[1] + peopleCounter[2] + peopleCounter[3]));
	c.println("Population cap: " + populationCaps[currentRegion]);
	c.println("\nEnergy: " + resourceCounter[1] + " GJ");
	c.println("Energy needed to switch workers: " + switchCost + "GJ"); // in case they didn't see the heading that says "costs x GJ per person"
	c.println("Energy Production Rate: " + energyProductionRate + " GJ/s");
	c.println("Time Passed: " + getRoundedDouble(secondsPast) + "s");
	c.println("\n");


	// If the user presses Z, they will go back to the main menu. If they press S, they will switch professions. The c.isCharAvail() function is used to determine if a key has been pressed, but without blocking the program execution.
	if (c.isCharAvail()) {
	    char pressed = c.getChar(); // INPUT section to detect the keypress


	    // For each below case, we clear the console, play the menu switch sound effect, and progress to the menu that corresponds to the key pressed.
	    if (pressed == 'z') {
		// Clear the console, play the menu switch sound effect, and progress to the main menu
		imageNotShown = true; // Note that we set imageNotShown to true, so that the background image is redrawn when we go back to the main menu
		// Auditory OUTPUT section
		playGameEffect("Assets/menuswitch.wav");
		c.clear();
		currentMenu = 3;
	    } else if (pressed == 's') {
		// PROCESSING Section for profession switching
		if (resourceCounter[1] < switchCost)
		    c.println("You do not have enough energy to switch professions.");
		else {
		    // Initialize local variables for the profession switcher.
		    int switchFrom, switchTo, numSwitch;


		    // Display the profession switcher header and prompt the user to enter the profession to switch from and to.
		    c.println ("Profession Switcher");
		    c.println ("Enter profession to switch from: [0] - Unemployed | [1] - Worker | [2] - Soldier | [3] - Doctor");
		    switchFrom = inputNumber (0, 3);


		    // Prompt the user to enter the profession to switch to, and calculate the number of people to switch.
		    c.println ("Enter profession to switch to: [0] - Unemployed | [1] - Worker | [2] - Soldier | [3] - Doctor");
		    switchTo = inputNumber (0, 3);


		    // Prompt the user to enter the number of people to switch, and calculate the cost of switching.
		    c.println ("Enter how many people to switch: ");


		    /*
		    At most, you can switch all your people, or you can switch until you have no energy left
		    thus, we must use a min function to see which one we are constrained by either you are constrained by the total amount of people available to switch, i.e peopleCounter[switchFrom] or you are constrained by the amount of energy you have, i.e. resourceCounter[1] / switchCost
		    amount of energy / cost of switching one person
		    also, the cost of switching increases each time you switch by 50 GJ
		    */


		    // Calculate the number of people to switch, and increase the switch cost by 50 GJ per switch
		    numSwitch = inputNumber (0, Math.min (peopleCounter [switchFrom], (int) (resourceCounter [1] / switchCost)));
		    switchCost += 50; // increase by 50 GJ per switch


		    // Perform the switch
		    peopleCounter [switchFrom] -= numSwitch;
		    peopleCounter [switchTo] += numSwitch;
		    resourceCounter [1] -= switchCost * numSwitch;
		   
		    // Display the result of the switch and play the success sound effect
		    // Auditory OUTPUT section
		    playGameEffect("Assets/success.wav");
		    // OUTPUT Section, displays the success message
		    c.println("Successfully switched " + numSwitch + " " + professionNames[switchFrom] + " to " + professionNames[switchTo] + ". Press any key to continue");


		    // Wait for the user to press a key before continuing
		    c.getChar();
		    c.clear(); // We clear the console after a profession switch
		}
	    }
	}
    } // end of populationMenu


    // The region map function, which displays the current region, the population capacity, the next region, the reserves and soldiers needed to conquer it, and the option to conquer the next region
    public static void regionMap () {
	// Set the cursor position
	c.setCursor (6, 1);


	// Display the region map header, with the option to go back to the main menu and conquer the next region
	displayHeader("Region Map", "[Z] - Back | [C] - Conquer Next Region");


	// Initialize the canProgress variable
	boolean canProgress;


	// Display the region map information, including the current region, the population capacity, the next region, the reserves and soldiers needed to conquer it, and the option to conquer the next region
	c.println("Current Region: " + regionNames [currentRegion]);
	c.println("Population Capacity: " + populationCaps [currentRegion]);


	// Check if the player has enough resources and people to progress to the next region
	if (soldiersNeeded [currentRegion + 1] > peopleCounter[2] || reservesNeeded [currentRegion + 1] > resourceCounter[0] || currentRegion == regionNames.length - 1)
	    canProgress = false;
	else
	    canProgress = true;
	c.println();


	// If there are still regions to conquer, display the next region, the reserves and soldiers needed to conquer it, and the option to conquer the next region. If there aren't more regions to conquer, display a message indicating that the player has beaten the game
	if (currentRegion != regionNames.length - 1) {
	    c.println("Next Region: " + regionNames [currentRegion + 1]);
	    c.println("Reserves Needed: " + reservesNeeded [currentRegion + 1] + " MT");
	    c.println("Soldiers Needed: " + soldiersNeeded [currentRegion + 1] + " soldiers\n");
	    if (canProgress)
		c.println("Press [C] to conquer the next region");
	    else
		c.println("You do not have enough resources/people to conquer the next region.");
	} else
	    c.println("There are no more regions to conquer. You have beaten the game!");
       
	// If the user presses Z, they will go back to the main menu. If they press C, they will conquer the next region. The c.isCharAvail() function is used to determine if a key has been pressed, but without blocking the program execution
	// INPUT + PROCESSING Section
	if (c.isCharAvail()) {
	    char pressed = c.getChar(); // INPUT section to detect the keypress


	    // PROCESSING Section to return to main menu
	    if (pressed == 'z') {
		// Clear the console, play the menu switch sound effect, and progress to the main menu
		imageNotShown = true; // Note that we set imageNotShown to true, so that the background image is redrawn when we go back to the main menu
		// Auditory OUTPUT section
		playGameEffect("Assets/menuswitch.wav");
		c.clear();
		currentMenu = 3;
	    }
	   
	    // If the user presses C, they will conquer the next region, and the game will check if the player has enough resources and people to progress to the next region
	    // PROCESSING Section to conquer the next region
	    else if (pressed == 'c' && canProgress) {
		// Increment the current region by 1
		++currentRegion;


		// Random events for conquering regions
		if (Math.random () < 0.10) {
		    // 10% chance for the alien forces to be caught off guard
		    double reductionFactor = 0.9 - Math.random () * 0.4;


		    // Display the "not as dangerous" message
		    // OUTPUT Section, notfies users of the result
		    slowPrint("The region was not as dangerous as your scientists predicted. All costs are reduced.\n");


		    // Reduce the resources and professions
		    resourceCounter[0] -= reservesNeeded[currentRegion] * reductionFactor;
		    peopleCounter[2] -= soldiersNeeded[currentRegion] * reductionFactor;
		} else if (Math.random () < 0.15) {
		    // 15% chance for the alien forces to be underestimated
		    double increaseFactor = 1.1 + Math.random () * 0.4;


		    // Display the underestimated message
		    // OUTPUT Section, notfies users of the result
		    slowPrint("The harsh conditions were underestimated and your ships have difficulty operating. All costs are increased.\n");


		    // Decrease the resources and professions
		    resourceCounter[0] -= reservesNeeded[currentRegion] * increaseFactor;
		    peopleCounter[2] -= soldiersNeeded[currentRegion] * increaseFactor;
		} else {
		    // Normal conquering
		    resourceCounter[0] -= reservesNeeded[currentRegion];
		    peopleCounter[2] -= soldiersNeeded[currentRegion];
		}


		// Ask the user to press any key to continue
		c.print("Press any key to continue.");
		c.getChar();
	       
		// The user has been defeated if the region was underestimated, and the user cannot satisfy the increased resources
		if (resourceCounter[0] < 0 || peopleCounter[2] < 0) {
		    // Reduce the resources and professions if the player loses
		    double reductionFactor = 0.4 + Math.random () * 0.4;
		    resourceCounter[0] = 0;
		    resourceCounter[1] = 0;
		    peopleCounter[0] = 0;
		    peopleCounter[1] *= reductionFactor;
		    peopleCounter[2] = 0;
		    peopleCounter[3] *= reductionFactor;


		    // Display the failed conquest
		    c.clear();
		    slowPrint(failedConquerMessages[currentRegion] + "\n\n");
		    sleep(1000); // Pause for 1 second


		    // Notify the user that they have suffered a crushing defeat
		    // OUTPUT Section, notfies users of the result
		    slowPrint("You did not have enough soldiers or resources and your fleet is heavily damaged. You suffer a crushing defeat.");


		    // Tell the user to press any key to continue
		    c.print("Press any key to continue.");
		    c.getChar();
		    c.clear();


		    // Decrement currentRegion, since it was incremented earlier
		    --currentRegion;
		} else {
		    // Display the successful conquest
		    c.clear();
		    slowPrint(successfullConquerMessages[currentRegion] + "\n\n");
		    sleep(1000); // Pause for 1 second


		    // Notify the user that they have successfully conquered the region, and play the success sound effect
		    // Auditory OUTPUT section
		    playGameEffect("Assets/success.wav");
		    // OUTPUT Section, notfies users of the result
		    slowPrint("You have successfully conquered " + regionNames [currentRegion] + "!\n");


		    // Tell the user to press any key to continue
		    c.print("Press any key to continue.");
		    c.getChar();
		    c.clear();
		}
	    }
	}
    } // End of regionMap function


    // The view mines function, which displays the mine tier, the regional multiplier, the collection cooldown, the resources needed for the next tier, the resources and energy needed to upgrade the mines, the resources and energy available, and the time until the next collection
    public static void viewMines() {
	// Increment the iterations since the last collect
	++iterationsSinceLastCollect;


	// Set the cursor position and display the view mines header
	c.setCursor(6, 1);
	displayHeader("Stellar Reserve Mines", "[Z] - Back | [C] - Collect Mines | [U] - Upgrade Mines");


	// Display a description of the mines
	c.println("Your workers are hard at work mining stellar reserves and producing energy in our top notch mines. You can collect from mines every few seconds, but only when you are on this screen. This cooldown can be reduced by upgrading your mine. Each time you collect, there is a 20% chance of double resources, and a 10% chance of triple resources. If you're lucky, these two may stack!\n");


	// Display the mine tier, the regional multiplier, the collection cooldown, the resources needed for the next tier, the resources and energy needed to upgrade the mines, the resources and energy available, and the time until the next collection
	c.print("Mine Tier ");


	// Display the mine tier and region name, if the mine level is 10, display that the mines are maxed out
	if (mineLevel == 10)
	    c.print("[MAXED OUT]");


	// Displays the rest of the info
	c.print(": ");
	c.println(mineTierNames[mineLevel] + " - [" + regionNames[currentRegion] + "]");
	c.println("Regional Multiplier: " + regionMineMultipliers[currentRegion] + "x");
	c.println("Collection Cooldown: " + getRoundedDouble(mineCollectionTimes[mineLevel]/10.0) + " seconds\n");


	// Tell the user their mine is maxed out if the mine level is 10, otherwise display the resources needed for the next tier and the energy needed for the next tier. Tells the user if they can upgrade the mines or not
	if (mineLevel != 10) {
	    c.println("Reserves Needed For Next Tier: " + mineUpdateReservesNeeded[mineLevel + 1] + " MT");
	    c.println("Energy Needed For Next Tier: " + mineUpdateEnergyNeeded[mineLevel + 1] + " GJ");
	    if (resourceCounter[0] >= mineUpdateReservesNeeded[mineLevel + 1] && resourceCounter[1] >= mineUpdateEnergyNeeded[mineLevel + 1] && mineLevel < 10)
		c.println("You can upgrade your mines! Press [U] to upgrade.");
	    else
	       c.println("You do not have enough resources to upgrade your mines.");
	} else
	    c.println("Mines are maxed out and cannot be upgraded.");


	// Display the resources and energy available
	c.println("\nStellar Reserves: " + resourceCounter[0] + " MT");
	c.println("Energy: " + resourceCounter[1] + " GJ\n");


	// Display the time until the next collection, and if a user can collect, display a message indicating that they can collect now. If they cannot collect, display a message indicating the time until the next collection
	if (iterationsSinceLastCollect >= mineCollectionTimes[mineLevel])
	    c.println("You can collect now! Press [C] to collect from your mines.");
	else    
	    c.println("You can collect from mines in " + getRoundedDouble((mineCollectionTimes[mineLevel]-(double)iterationsSinceLastCollect)/10.0) + " seconds.");


	// If the user presses Z, they will go back to the main menu. If they press C, they will collect from the mines. If they press U, they will upgrade the mines. The c.isCharAvail() function is used to determine if a key has been pressed, but without blocking the program execution
	// INPUT + PROCESSING Section
	if (c.isCharAvail()) {
	    char pressed = c.getChar(); // INPUT section to detect the keypress


	    // PROCESSING Section to return to main menu
	    if (pressed == 'z') {
		// Clear the console, play the menu switch sound effect, and progress to the main menu
		imageNotShown = true; // Note that we set imageNotShown to true, so that the background image is redrawn when we go back to the main menu
		// Auditory OUTPUT section
		playGameEffect("Assets/menuswitch.wav");
		c.clear();
		currentMenu = 3;
	    }
	   
	    // If the user presses C, the game will check if the user can collect from the mines. If they can, the game will increment the resources by their respective values
	    // PROCESSING Section to collect from mines
	    else if (pressed == 'c' && iterationsSinceLastCollect >= mineCollectionTimes[mineLevel]) {
		// Play the mine sound effect, auditory OUTPUT section
		playGameEffect("Assets/mine.wav");


		// Reset the iterations since the last collect
		iterationsSinceLastCollect = 0;


		// Add some randomness to the resources collected by introducing multipliers
		int randomMultiplier = 1;
		if (Math.random() < 0.2)
		    randomMultiplier *= 2;
		if (Math.random() < 0.1)
		    randomMultiplier *= 3;


		// Increment the resources by their respective values
		resourceCounter[0] += 3 + (int) developerMultiplier * // Developer multiplier
						randomMultiplier * // Random multiplier
						regionMineMultipliers[currentRegion] * // Regional multiplier
						(int) (reservesNeeded[currentRegion + 1] *
						(Math.random() + 1.5) / 100);


		resourceCounter[1] += 2 + (int) developerMultiplier * // Developer multiplier
						randomMultiplier * // Random multiplier
						regionMineMultipliers[currentRegion] * // Regional multiplier
						(int) (resourceCounter[1] *
						(Math.random() + 0.5) / 100);
	    }
	   
	    // If the user presses U, they will upgrade the mines, and the game will check if the user can upgrade the mines using several conditions
	    // PROCESSING Section to upgrade mines
	    else if (pressed == 'u' && resourceCounter[0] >= mineUpdateReservesNeeded[mineLevel + 1] && resourceCounter[1] >= mineUpdateEnergyNeeded[mineLevel + 1] && mineLevel < 10) {
		// Play the upgrade sound effect and decrement resources
		resourceCounter[0] -= mineUpdateReservesNeeded[mineLevel + 1];
		resourceCounter[1] -= mineUpdateEnergyNeeded[mineLevel + 1];
		// Auditory OUTPUT section
		playGameEffect("Assets/success.wav");


		// Display the result of the upgrade and increment the mine level
		// OUTPUT Section, displays the success message
		c.print("You have successfully upgraded your mines to tier " + mineTierNames[mineLevel + 1] + "! Press any key to continue.");


		// Wait for the user to press a key before continuing
		c.getChar();
		c.clear();


		// Increment the mine level
		++mineLevel;
	    }
	}
    } // end of viewMines


    /*
    Tick functions
    Ticks are called periodically and they update the game
    Short ticks happen every 0.1s
    Long ticks happen every 10s
    */


    // The short tick function, which updates the stellar reserves production rate, the energy production rate, and the population growth rate every 0.1s
    // PROCESSING method
    public static void shortTick () {
	stellarReservesProductionRate = peopleCounter[1] * 0.1 + 0.1; // workers * 0.1 = rate
	energyProductionRate = peopleCounter[1] * 5; // workers * 5 = GJ/s
	populationGrowthRate = peopleCounter[3] * 0.1 + 0.1; // base rate of 0.1 plus 0.1 for every doctor
    } // end of shortTick


    // The long tick function, which updates the resources every 10s, and checks if the user has exceeded the population cap
    // PROCESSING method
    public static void longTick () {
	int finalPopulation;


	// Update the resources and population, the developer multiplier is used to speed up the game for developers in developer mode.
	resourceCounter[0] += developerMultiplier * stellarReservesProductionRate * 10; // Add the stellar reserves production rate to the reserves, we multiply by 10 because this happens every 10 seconds
	peopleCounter[0] += developerMultiplier * populationGrowthRate * 10; // Add the population growth rate to the unemployed population, we multiply by 10 because this happens every 10 seconds
	resourceCounter[1] += developerMultiplier * energyProductionRate * 10; // Add the energy production rate to the energy, we multiply by 10 because this happens every 10 seconds
	finalPopulation = peopleCounter[0] + peopleCounter[1] + peopleCounter[2] + peopleCounter[3]; // Calculate the total population


	// If the population exceeds the population cap, adjust the population to the population cap
	if (finalPopulation > populationCaps [currentRegion]) {
	    peopleCounter[0] -= finalPopulation - populationCaps [currentRegion]; // adjust to population cap
	}
    } // end of longTick


    // The main function, which initializes the game, displays the appropriate menu, and runs the game loop
    public static void main (String[] args) {
	// Immediately play the background music
	// Auditory OUTPUT section
	playBackgroundMusic();


	// Initialize the console, it has a width of 115 and a height of 29, and the title is "Starbound Empires"
	c = new Console(29, 115, 18, "Starbound Empires"); // Initialize the console


	// Initialize the game
	initializeGame();


	// Keeping the main thread alive to keep the application running, this is our main game loop, with all the elements of the game
	while (true) {
	    shortTick(); // A short tick is called every frame


	    if (iterations % 100 == 0)
		longTick(); // every 100 frames, i.e 10s, a long tick is called


	    // Display the appropriate menu with event handling
	    // PROCESSING Section to display the appropriate menu
	    if (currentMenu == 0)
		startMenu();
	    else if (currentMenu == 1)
		nameMenu();
	    else if (currentMenu == 2)
		rulesMenu();
	    else if (currentMenu == 3)
		mainMenu();
	    else if (currentMenu == 4)
		populationMenu();
	    else if (currentMenu == 5)
		regionMap();
	    else if (currentMenu == 6)
		viewMines();


	    // Sleep for 80ms to keep the game running at a reasonable speed, unless in debug mode
	    if (!debug)
		sleep(80);


	    // Update the time passed and the iterations
	    secondsPast += 0.1;
	    iterations++;
	} // end of while loop
    } // end of main
} // end of class
