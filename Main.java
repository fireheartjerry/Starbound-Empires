import hsa.Console;

public class Main {
    static Console c; // HSA Console object
    static final int FRAME_RATE = 1; // Target frame rate in FPS (frames per second)
    static final int FRAME_PERIOD = 1000 / FRAME_RATE; // Frame period in milliseconds

    public static void main(String[] args) {
        c = new Console(); // Initialize the console
        long cookies = 0;
        long increment = 17;

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
