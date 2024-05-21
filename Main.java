import hsa.Console;

public class Main {
    static Console c; // HSA Console object

    public static void main(String[] args) {
        c = new Console(); // Initialize the console
        
        // Initalize variables
        

        // Keeping the main thread alive to keep the application running
        while (true) {
            // Update the cookies count
            cookies += increment;

            // Calculate the time taken for the frame update
            long cookiesPerSecond = increment;
            c.print("Steel: " + cookies + "\nYou are producing " + cookiesPerSecond + " steel per second\n");

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
