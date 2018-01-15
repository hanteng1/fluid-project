package PumpSparkConsoleDemo;

import PumpSpark.PumpSparkManager;
import processing.core.PApplet;

public class PumpSparkPlay extends PApplet {
	
	PumpSparkManager pumpSpark;
	
	private boolean oneOn = false;
	private boolean twoOn = false;
	private boolean threeOn = false;
	
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
			pumpSpark.actuatePump((byte)2, (byte)0);
		
			oneOn = false;
			twoOn = false;
			threeOn = false;
			println("pump 1 is off");
			println("pump 2 is off");
			println("pump 3 is off");
			
			delay(100);
			pumpSpark.disconnectPort();
			exit();
		}else if(key == 'b')
		{
			pumpSpark.actuatePump((byte)0, (byte)254);
			pumpSpark.actuatePump((byte)1, (byte)254);
			pumpSpark.actuatePump((byte)2, (byte)254);
			
			oneOn = true;
			twoOn = true;
			threeOn = true;
			println("pump 1 is on");
			println("pump 2 is on");
			println("pump 3 is on");
			
		}else if(key == '1') {
			if(oneOn)
			{
				pumpSpark.actuatePump((byte)0, (byte)0);
				println("pump 1 is off");
			}else
			{
				pumpSpark.actuatePump((byte)0, (byte)254);
				println("pump 1 is on");
			}
			oneOn = !oneOn;
		}else if(key == '2')
		{
			if(twoOn)
			{
				pumpSpark.actuatePump((byte)1, (byte)0);
				println("pump 2 is off");
			}else
			{
				pumpSpark.actuatePump((byte)1, (byte)254);
				println("pump 2 is on");
			}
			twoOn = !twoOn;
		}else if(key == '3')
		{
			if(threeOn)
			{
				pumpSpark.actuatePump((byte)2, (byte)0);
				println("pump 3 is off");
			}else
			{
				pumpSpark.actuatePump((byte)2, (byte)254);
				println("pump 1 is on");
			}
			threeOn = !threeOn;
		}
		else if(key == 'e')
		{
			pumpSpark.actuatePump((byte)0, (byte)0);
			pumpSpark.actuatePump((byte)1, (byte)0);
			pumpSpark.actuatePump((byte)2, (byte)0);
			
			oneOn = false;
			twoOn = false;
			threeOn = false;
			println("pump 1 is off");
			println("pump 2 is off");
			println("pump 3 is off");
		}else if(key == 'f')
		{
			pumpSpark.actuatePump((byte)3, (byte)254);
			
			delay(2000);
			
			pumpSpark.actuatePump((byte)3, (byte)0);
		}
		
		
		
	}
	
	
	
	public static void main(String[] args){
			
		PApplet.main("PumpSparkConsoleDemo.PumpSparkPlay");
		
	}
}





