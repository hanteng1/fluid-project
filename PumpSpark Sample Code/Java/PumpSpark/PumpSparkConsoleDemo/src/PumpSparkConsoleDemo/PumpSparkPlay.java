package PumpSparkConsoleDemo;

import PumpSpark.PumpSparkManager;
import processing.core.PApplet;

public class PumpSparkPlay extends PApplet {
	
	PumpSparkManager pumpSpark;
	
	
	public void settings()
	{
		size(300, 300);
	}
	
	public void setup()
	{
		fill(120, 50, 240);
		pumpSpark = new PumpSparkManager();
		pumpSpark.configurePort("COM7");	
		// Open the serial port
		pumpSpark.connectPort();
	}
	
	public void draw()
	{
		//pumpSpark.actuatePump((byte)0, (byte)254);
		//delay(5000);
		//pumpSpark.actuatePump((byte)0, (byte)0);
		//delay(5000);
	}
	
	
	public void keyPressed() {
		if (key == 'q') {
			pumpSpark.actuatePump((byte)0, (byte)0);
			pumpSpark.actuatePump((byte)1, (byte)0);
			delay(100);
			exit();
		}else if(key == 'b')
		{
			pumpSpark.actuatePump((byte)0, (byte)254);
			pumpSpark.actuatePump((byte)1, (byte)254);
			pumpSpark.actuatePump((byte)2, (byte)254);
		}else if(key == 'e')
		{
			pumpSpark.actuatePump((byte)0, (byte)0);
			pumpSpark.actuatePump((byte)1, (byte)0);
			pumpSpark.actuatePump((byte)2, (byte)0);
		}
	}
	
	
	
	public static void main(String[] args){
			
		PApplet.main("PumpSparkConsoleDemo.PumpSparkPlay");
		
	}
}





