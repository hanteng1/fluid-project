package com.teng.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import processing.core.PApplet;

public class HapticTest extends PApplet {

	int windowWidth, windowHeight;
	
	//buttons 
	int[] rectXs;
	int[] rectYs;
	int rectX, rectY;
	int numButtons;
	String[] buttonTexts;
	int rectWidth;
	int rectHeight;
	int rectColor, rectHighlight;
	int rectOverIndex = -1;
	
	int[] mouseTriggered;
	
	//serial
	static SerialPort serialPort;
	static InputStream serialInput;
	static OutputStream serialOutput;
	static String portName;
	
	boolean threading = false;
	
	int pumpVelocity = 0;  // 0 - 255
	int velocityStep = 5;
	
	//for leapmotion
	int buttonHeight = 100;
	float buttonY = 700 - 100;
	float leapX = 100;
	float leapY = 500;
	float prev_leapX;
	float prev_leapY;
	
	Leap leap;
	
	//button
	Button button;
	
	//slider
	Slider slider;
	
	
	
	
	public static HapticTest instance;
	public static HapticTest getInstance()
	{
		if(instance == null)
		{
			instance = new HapticTest();
		}
		return instance;
	}
		
	
	public void settings()
	{
		windowWidth = 1000;
		windowHeight = 1000;
		size(windowWidth, windowHeight);
	}
	
	public void setup()
	{
		
		instance = this;
		
		fill(120, 50, 240);
		rectColor = color(211, 211, 211);
		rectHighlight = color(105, 105, 105);
		
		rectWidth = (int) (windowWidth * 0.8);
		rectX = (windowWidth - rectWidth) / 2;
		//5 buttons
		numButtons = 6;
		rectXs = new int[] {rectX, rectX, rectX, rectX, rectX, rectX};
		rectHeight = 50;
		rectYs = new int[] {(int)(rectHeight * 1.5), (int)(rectHeight * 3.0), (int)(rectHeight * 4.5), (int)(rectHeight * 6.0), (int)(rectHeight * 7.5), (int)(rectHeight * 9.0)};
	
		buttonTexts = new String[] {"Left Vibration", "Right Vibration", "Valve Up + 5", "Valve Down - 5", "Valve 0", "Valve 180"};
		
		mouseTriggered = new int[] {0, 0, 0, 0, 0, 0};  //0 - not active, 1 - active
		
		configurePort("COM8");
		connectPort();
		
		delay(1000);
		
		leap = new Leap();
		
		
		delay(1000);
		button = new Button(this, 400, 700);
		thread("initializeButton");
		
		slider = new Slider(this, 600, 700);
		
		
	}
	
	public void draw()
	{
		
		background(225, 225, 225);
		
		update(mouseX, mouseY);
		
		for(int itrr = 0; itrr < 6; itrr++)
		{
			if(mouseTriggered[itrr] == 1)
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
		
		
		//show the velocity
		textSize(64);
		String showText = "" + pumpVelocity;
		text(showText, windowWidth / 2 - textWidth(showText) / 2, windowHeight * 9 / 10);
		
		
		//finger
		leapX = leap.indexTipX * 2 + windowWidth / 2;
		leapY = windowHeight - leap.indexTipY * 2;
		
		stroke(153);
		noFill();
		arc(leapX, leapY - 25, 100, 50, 0, PI);
		
		//new button position
		button.detectTouch(leapX, leapY, prev_leapX, prev_leapY);

		prev_leapX = leapX;
		prev_leapY = leapY;
		
		
		button.draw();
		
		slider.draw();
		
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
			if(mouseTriggered[rectOverIndex] == 1)  //if active
			{
				
				//switch off
				switchTask(rectOverIndex, 0);
				mouseTriggered[rectOverIndex] = 0; 
				
			}else
			{
				mouseTriggered[rectOverIndex] = 1;
				println("action: " + buttonTexts[rectOverIndex]);
				
				//switch on
				switchTask(rectOverIndex, 1);
			}
		}
	}
	
	
	void switchTask(int task, int onoff)
	{
		if(onoff == 0)  //turn off tasks
		{
			threading = false;
			//task stop 
			try {
				serialOutput.write('s');
				pumpVelocity = 0;
			} catch (Exception ex) {
				return;
			}
		}else  //turn on tasks
		{
			switch (task) {
			case 0:  //left vibrating
				try {
					serialOutput.write('l');
					pumpVelocity = 255;
					
				} catch (Exception ex) {
					return;
				}
				break;
			case 1:  // right vibrating, will not be used for now
				try {
					serialOutput.write('r');
					pumpVelocity = 255;
				} catch (Exception ex) {
					return;
				}
				break;
			case 2:  //valve up
				thread("valveUp");				
				
				break;
			case 3: //valve down
				thread("valveDown");
				
				break;
			case 4:  //zero
				thread("valveZero");
				break;
				
			case 5:  //max
				thread("valveMax");
				break;
				
			
			}
		}
		
	}
	
	void configurePort(String _portName) {
		portName = _portName;
	}
	
	void connectPort() {
		try {
			CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
			if (portIdentifier.isCurrentlyOwned()) {
				System.out.println("Error: Port is currently in use");
			} else {
				CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
				
				if (commPort instanceof SerialPort) {
					serialPort = (SerialPort) commPort;

					// Set appropriate properties (do not change these)
					serialPort.setSerialPortParams(
							9600, 
							SerialPort.DATABITS_8,
							SerialPort.STOPBITS_1, 
							SerialPort.PARITY_NONE);

					serialInput = serialPort.getInputStream();
					serialOutput = serialPort.getOutputStream();
					System.out.println("Connected to port: " + portName);
				} else {
					System.out.println("Error: Only serial ports are handled by this example.");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
	}
	
	void disconnectPort() {
		try {
			System.out.println("Disconnecting from port: " + portName);
			serialInput.close();
			serialOutput.close();
			serialPort.close();
			
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	public void keyPressed() {
		if (key == 'q') {
			
			valveZero();
			
			//stop pop and reset valve
			try {
				serialOutput.write('s');
				pumpVelocity = 0;
			} catch (Exception ex) {
				return;
			}
		
			
			disconnectPort();
			leap.end();
			exit();
		}else if(key == CODED)
		{
			//try not using these
			if(keyCode == LEFT)
			{
				thread("leftPump");
			}else if(keyCode == RIGHT)
			{
				thread("rightPump");
			}else if(keyCode == UP)
			{
				if(pumpVelocity + velocityStep <= 255)
				{
					pumpVelocity += velocityStep;
					
					thread("fasterVelocity");
					
				}
			}else if(keyCode == DOWN)
			{
				if(pumpVelocity - velocityStep >= 0)
				{
					pumpVelocity -= velocityStep;
					
					thread("slowerVelocity");
				}
			}
		}
		

	}
	
	
	public void leftPump()
	{
		threading = true;
		
		try {
			serialOutput.write('l');
		} catch (Exception ex) {
			return;
		}
		
		delay(1000);
		
		try {
			serialOutput.write('s');
		} catch (Exception ex) {
			return;
		}
		
		threading = false;
		
	}
	
	
	public void rightPump()
	{
		threading = true;
		
		try {
			serialOutput.write('r');
		} catch (Exception ex) {
			return;
		}
		
		delay(1000);
		
		try {
			serialOutput.write('s');
		} catch (Exception ex) {
			return;
		}
		
		threading = false;
		
	}
	
	public void fasterVelocity()
	{
		threading = true;
		
		try {
			serialOutput.write('i');
		}catch(Exception ex){
			return;
		}
		
		threading = false;
	}
	
	public void slowerVelocity()
	{
		threading = true;
		
		try {
			serialOutput.write('d');
		}catch(Exception ex){
			return;
		}
		
		threading = false;
	}
	
	
	public void valveUp()
	{
		threading = true;
		//give some delay
		delay(100);
		
		try {
			serialOutput.write('p');
		} catch (Exception ex) {
			return;
		}

		mouseTriggered[2] = -1; 
		threading = false;
	}
	
	public void valveDown()
	{
		threading = true;
		//give some delay
		delay(100);
		
		try {
			serialOutput.write('o');
		} catch (Exception ex) {
			return;
		}
		
		mouseTriggered[3] = -1; 
		threading = false;
	}
	
	public void valveZero()
	{
		threading = true;
		//give some delay
		delay(100);
		
		try {
			serialOutput.write('z');
		} catch (Exception ex) {
			return;
		}
		
		mouseTriggered[4] = -1; 
		threading = false;
	}
	
	public void valveMax()
	{
		threading = true;
		//give some delay
		delay(100);
				
		try {
			serialOutput.write('m');
		} catch (Exception ex) {
			return;
		}
		
		mouseTriggered[5] = -1; 
		threading = false;
	}
	
	
	public void initializeButton()
	{
		valveMax();
		
		//switch on the flow
		switchTask(0, 1);
		mouseTriggered[0] = 1;
		
		
		//schedule a stop in 2 secs
		sheduleStop(3000);
	}
	
	
	public void sheduleStop(int duration)
	{
		delay(duration);
		
		switchTask(0, 0);
		mouseTriggered[0] = 0;
	}
	
	
	
	public static void main(String[] args){
		
		PApplet.main("com.teng.test.HapticTest");
		
	}
}
