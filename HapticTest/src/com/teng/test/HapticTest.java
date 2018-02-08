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
	
	int mouseTriggered = -1;
	
	//serial
	static SerialPort serialPort;
	static InputStream serialInput;
	static OutputStream serialOutput;
	static String portName;
	
	boolean threading = false;
	
	int pumpVelocity = 0;  // 0 - 255
	int velocityStep = 5;
	
	public void settings()
	{
		windowWidth = 1000;
		windowHeight = 1000;
		size(windowWidth, windowHeight);
	}
	
	public void setup()
	{
		fill(120, 50, 240);
		rectColor = color(211, 211, 211);
		rectHighlight = color(105, 105, 105);
		
		rectWidth = (int) (windowWidth * 0.8);
		rectX = (windowWidth - rectWidth) / 2;
		//5 buttons
		numButtons = 5;
		rectXs = new int[] {rectX, rectX, rectX, rectX, rectX};
		rectHeight = 100;
		rectYs = new int[] {(int)(rectHeight * 1.5), (int)(rectHeight * 3.0), (int)(rectHeight * 4.5), (int)(rectHeight * 6.0), (int)(rectHeight * 7.5)};
	
		buttonTexts = new String[] {"Left Vibration", "Right Vibration", "x", "x", "x"};
		
		configurePort("COM8");
		connectPort();
		
	}
	
	public void draw()
	{
		
		background(225, 225, 225);
		
		update(mouseX, mouseY);
		
		for(int itrr = 0; itrr < 2; itrr++)
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
		
		
		//show the velocity
		textSize(64);
		String showText = "" + pumpVelocity;
		text(showText, windowWidth / 2 - textWidth(showText) / 2, windowHeight * 4 / 5);
		
		
		
		
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
			threading = false;
			//task stop 
			try {
				serialOutput.write('s');
				pumpVelocity = 0;
			} catch (Exception ex) {
				return;
			}
		}else
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
			case 1:  // right vibrating
				try {
					serialOutput.write('r');
					pumpVelocity = 255;
				} catch (Exception ex) {
					return;
				}
				break;
			case 2:  //weight
				
				break;
			case 3:
				
				break;
			case 4:
				
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
			disconnectPort();
			exit();
		}else if(key == CODED)
		{
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
	
	
	
	public static void main(String[] args){
		
		PApplet.main("com.teng.test.HapticTest");
		
	}
}
