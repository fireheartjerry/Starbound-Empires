// File: HSAConsoleFrameRateDemo.java

import hsa.Console;
import java.math.BigDecimal;

public class Framerate {
    static Console c; // HSA Console object
    static final int FRAME_RATE = 30; // Target frame rate in FPS (frames per second)
    static final int FRAME_PERIOD = 1000 / FRAME_RATE; // Frame period in milliseconds

    public static void main(String[] args) {
        c = new Console(); // Initialize the console
        BigDecimal cookies = new BigDecimal("1.0");
        BigDecimal increment = new BigDecimal("1"); // Decimal amount to add
        BigDecimal FRAME_RATE_CALCULATION = new BigDecimal(FRAME_RATE);
        long programStartTime = System.currentTimeMillis(); // Record the start time of the program

        // Keeping the main thread alive to keep the application running
        while (true) {
            long frameStartTime = System.currentTimeMillis(); // Record the start time of the current frame

            // Print the current number of cookies and elapsed time
            long elapsedTimeMillis = System.currentTimeMillis() - programStartTime;
            double elapsedTimeSeconds = elapsedTimeMillis / 1000.0;
            // Update the cookies count
            cookies = cookies.add(increment);

            // Calculate the time taken for the frame update
            long timeTaken = System.currentTimeMillis() - frameStartTime;
            BigDecimal cookiesPerSecond = increment.multiply(new BigDecimal(FRAME_RATE));
            c.print("Cookies: " + cookies + " | Time: " + elapsedTimeSeconds + "s\nYou are producing " + cookiesPerSecond + " cookies per second\n");

            // Calculate the remaining time to sleep to maintain the frame rate
            long sleepTime = FRAME_PERIOD - timeTaken;
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                    c.setCursor(1, 1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Busy-wait to fill the remaining frame time for better precision
            while (System.currentTimeMillis() - frameStartTime < FRAME_PERIOD) {
                // Busy-wait loop
            }
        }
    }
}
