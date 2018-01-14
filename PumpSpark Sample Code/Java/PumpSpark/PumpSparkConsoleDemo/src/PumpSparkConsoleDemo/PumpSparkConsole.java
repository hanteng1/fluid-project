package PumpSparkConsoleDemo;

import PumpSpark.PumpSparkManager;

public class PumpSparkConsole {
	public static void main(String[] args) throws Exception {
		// Instantiate a new PumpSparkManager
		PumpSparkManager pumpSpark = new PumpSparkManager();
		
		// Configure serial port and specify COM port
		pumpSpark.configurePort("COM7");
		
		// Open the serial port
		pumpSpark.connectPort();
		
		// Looping through 10x times
		for (int i = 0; i < 30; i++) {
            // Output message to console
            System.out.println("Actuate pump 0 at flow 254");

            // Actuating pump 0 at max value of 254 for 5 seconds            
            pumpSpark.actuatePump((byte)0, (byte)254);
            Thread.sleep(5000);

            // Output message to console
            System.out.println("Actuate pump 0 at flow 0");

            // Actuating pump 0 at min value of 0 for 5 seconds
            pumpSpark.actuatePump((byte)0, (byte)0);
            Thread.sleep(5000);			
		}
		
		pumpSpark.disconnectPort();
	}
}
