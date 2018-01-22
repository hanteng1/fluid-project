package PumpSparkConsoleDemo;

import PumpSpark.PumpSparkManager;
import processing.core.PApplet;

public class PumpSparkPlay extends PApplet {
	
	PumpSparkManager pumpSpark;
	
	private boolean oneOn = false;
	private boolean twoOn = false;
	private boolean threeOn = false;
	
	//buttons 
	int[] rectXs;
	int[] rectYs;
	int rectX, rectY;
	int numButtons;
	int rectWidth;
	int rectHeight;
	int rectColor, rectHighlight;
	int rectOverIndex = -1;
	
	int windowWidth, windowHeight;
	int mouseTriggered = -1;
	
	String[] buttonTexts;
	
	boolean threading = false;
	
	public void settings()
	{
		windowWidth = 1000;
		windowHeight = 1000;
		size(windowWidth, windowHeight);
	}
	
	public void setup()
	{
		fill(120, 50, 240);
		pumpSpark = new PumpSparkManager();
		pumpSpark.configurePort("COM7");	
		// Open the serial port
		pumpSpark.connectPort();
		
		rectColor = color(211, 211, 211);
		rectHighlight = color(105, 105, 105);
		
		rectWidth = (int) (windowWidth * 0.8);
		rectX = (windowWidth - rectWidth) / 2;
		//5 buttons
		numButtons = 5;
		rectXs = new int[] {rectX, rectX, rectX, rectX, rectX};
		rectHeight = 100;
		rectYs = new int[] {(int)(rectHeight * 1.5), (int)(rectHeight * 3.0), (int)(rectHeight * 4.5), (int)(rectHeight * 6.0), (int)(rectHeight * 7.5)};
		
		buttonTexts = new String[] {"Beating", "Vibration", "x", "x", "x"};
	}
	
	public void draw()
	{
		update(mouseX, mouseY);
		
		for(int itrr = 0; itrr < numButtons; itrr++)
		{
			if(mouseTriggered == itrr)
			{
				fill(rectHighlight);
			}else
			{
				fill(rectColor);
			}
			
			rect(rectXs[itrr], rectYs[itrr], rectWidth, rectHeight);
			
			textSize(32);
			fill(0, 102, 153);
			
			text(buttonTexts[itrr], rectXs[itrr] + rectWidth / 2 - textWidth(buttonTexts[itrr]) / 2, rectYs[itrr] + 2 * rectHeight / 3); 
			
		}
	}
	
	private void update(int x, int y)
	{
		for(int itrr = 0; itrr < numButtons; itrr++)
		{
			if(overRect(rectXs[itrr], rectYs[itrr], rectWidth, rectHeight))
			{
				rectOverIndex = itrr;
				return;
			}
		}
		
		rectOverIndex = -1;
	}
	
	private boolean overRect(int x, int y, int width, int height)
	{
		if (mouseX >= x && mouseX <= x+width && 
			      mouseY >= y && mouseY <= y+height) {
		    return true;
		} 
		else {
			return false;
		}
	}
	
	public void mousePressed()
	{
		if(rectOverIndex >= 0)
		{
			if(mouseTriggered >= 0)
			{
				if(mouseTriggered == rectOverIndex)
				{
					//switch off
					switchTask(mouseTriggered, 0);
					
					
					mouseTriggered = -1; 
					
				}
				
			}else
			{
				mouseTriggered = rectOverIndex;
				println("action: " + buttonTexts[mouseTriggered]);
				
				//switch on
				switchTask(mouseTriggered, 1);
			}
		}
	}
	
	
	private void switchTask(int task, int onoff)
	{
		if(onoff == 0)
		{
			
			//clear threads
			threading = false;
			
			
			if(oneOn == true || twoOn == true || threeOn == true)
			{
				//turn off
				pumpSpark.actuatePump((byte)0, (byte)0);
				pumpSpark.actuatePump((byte)1, (byte)0);
				pumpSpark.actuatePump((byte)2, (byte)0);
				
				oneOn = false;
				twoOn = false;
				threeOn = false;
				println("pump 1 is off");
				println("pump 2 is off");
				println("pump 3 is off");
			}
			
			
			
		}else
		{
			switch (task) {
				case 0:  //beating
					thread("beating");
					break;
				case 1:  // vibrating
					pumpSpark.actuatePump((byte)0, (byte)254);
					pumpSpark.actuatePump((byte)1, (byte)254);
					pumpSpark.actuatePump((byte)2, (byte)254);
					
					oneOn = true;
					twoOn = true;
					threeOn = true;
					println("pump 1 is on");
					println("pump 2 is on");
					println("pump 3 is on");
					break;
				case 2:
					
					break;
				case 3:
					
					break;
				case 4:
					
					break;
					
				
			}
		}
					
	}
	
	
	public void beating()
	{
		println("threading beating starts");
		threading = true;
		while(threading)
		{
			pumpSpark.actuatePump((byte)0, (byte)254);
			pumpSpark.actuatePump((byte)1, (byte)254);
			pumpSpark.actuatePump((byte)2, (byte)254);
			println("pump 1 is on");
			println("pump 2 is on");
			println("pump 3 is on");
			oneOn = true;
			twoOn = true;
			threeOn = true;
			println("2 seconds");
			delay(2000);
			
			
			pumpSpark.actuatePump((byte)0, (byte)0);
			pumpSpark.actuatePump((byte)1, (byte)0);
			pumpSpark.actuatePump((byte)2, (byte)0);
			println("pump 1 is off");
			println("pump 2 is off");
			println("pump 3 is off");
			oneOn = false;
			twoOn = false;
			threeOn = false;
			println("6 seconds");
			delay(6000);
			
		}
		
		println("threading beating end");
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
			pumpSpark.actuatePump((byte)0, (byte)254);
			pumpSpark.actuatePump((byte)1, (byte)254);
			pumpSpark.actuatePump((byte)2, (byte)254);
			println("pump 1 is on");
			println("pump 2 is on");
			println("pump 3 is on");
			
			delay(2000);
			println("2 seconds");
			
			pumpSpark.actuatePump((byte)0, (byte)0);
			pumpSpark.actuatePump((byte)1, (byte)0);
			pumpSpark.actuatePump((byte)2, (byte)0);
			println("pump 1 is off");
			println("pump 2 is off");
			println("pump 3 is off");
		}
		
		
		
	}
	
	
	
	public static void main(String[] args){
			
		PApplet.main("PumpSparkConsoleDemo.PumpSparkPlay");
		
	}
}





