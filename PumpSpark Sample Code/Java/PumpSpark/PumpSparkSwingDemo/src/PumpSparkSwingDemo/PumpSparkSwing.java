package PumpSparkSwingDemo;

import PumpSpark.PumpSparkManager;

import java.awt.event.*;
import javax.swing.*;

public class PumpSparkSwing implements ActionListener  {
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				PumpSparkSwing demo = new PumpSparkSwing();
				demo.createAndShowGUI();
			}
		});
	}
	
	public void createAndShowGUI() {
		JFrame frame = new JFrame("PumpSpark");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton button = new JButton("Test pump!");
		button.addActionListener(this);      

		frame.getContentPane().add(button);
		
		frame.pack();
		frame.setVisible(true);
	}
			
	public void actionPerformed(ActionEvent e)
	{
		// Instantiate a new PumpSparkManager
		PumpSparkManager pumpSpark = new PumpSparkManager();
		
		// Configure serial port and specify COM port
		pumpSpark.configurePort("COM4");
		
		// Open the serial port
		pumpSpark.connectPort();
		
		try {
			// Looping through 10x times
			for (int i = 0; i < 10; i++) {
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
		} catch (Exception ex) { }
		
		pumpSpark.disconnectPort();
	}
}
