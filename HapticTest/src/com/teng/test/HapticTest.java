package com.teng.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import processing.core.PApplet;

public class HapticTest extends PApplet implements SerialPortEventListener{

	int windowWidth, windowHeight;
	
	//buttons 
	int[] rectXs;
	int[] rectYs;
	int rectX, rectY;
	int numButtons;
	String[] buttonTexts;
	String[] buttonTextsBackup;
	int[] rectWidth;
	int rectHeight;
	int rectColor, rectHighlight;
	int rectOverIndex = -1;
	
	int[] mouseTriggered;
	
	//serial - for pump and valves
	static SerialPort serialPort_One;
	static InputStream serialInput_One;
	static BufferedReader input_One;
	static OutputStream serialOutput_One;
	static String portName_One;
	
	//serial - for sensors
	static SerialPort serialPort_Two;
	static InputStream serialInput_Two;
	static BufferedReader input_Two;
	static OutputStream serialOutput_Two;
	static String portName_Two;
	
	
	
	
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
	
	//presure
	float pressureValue;
	TimeSeriesPlot timeSeriesPlot;
	
	//temperature value
	float temperatureValue;
	
	
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
		windowHeight = 1500;
		size(windowWidth, windowHeight);
	}
	
	public void setup()
	{
		
		instance = this;
		
		fill(120, 50, 240);
		rectColor = color(211, 211, 211);
		rectHighlight = color(105, 105, 105);
		
		
		//5 buttons
		numButtons = 6;
		rectXs = new int[] {100, 100, 600, 100, 100, 100, 100};
		rectWidth = new int[] {200, 200, 200, 500, 500, 500, 500};
		rectHeight = 50;
		rectYs = new int[] {(int)(rectHeight * 1.5), (int)(rectHeight * 3.0), (int)(rectHeight * 1.5), (int)(rectHeight * 4.5), (int)(rectHeight * 6.0), (int)(rectHeight * 7.5), (int)(rectHeight * 9.0)};
	
		
		buttonTexts = new String[] {"Cold", "Hot", "On", "Valve 1 : Open", "Valve 2 : Open", "Valve 3: Open"};
		buttonTextsBackup = new String[] {"Cold", "Hot", "Off", "Valve 1 : Close", "Valve 2 : Close", "Valve 3: Close"};
		
		mouseTriggered = new int[] {0, 0, 0, 0, 0, 0};  //0 - not active, 1 - active
		
		//control
		configurePort_One("COM10");
		connectPort_One();
			
		delay(1000);
		
		//sensor
		configurePort_Two("COM8");
		connectPort_Two();
		
		delay(1000);
		
		
		leap = new Leap();
		
		
		delay(1000);
		button = new Button(this, 400, 700);
		//thread("initializeButton");
		
		slider = new Slider(this, 600, 700);
		
		timeSeriesPlot = new TimeSeriesPlot(this, windowWidth /2, 900, windowWidth, 200, 500);
		
//		//open at least one valve
//		mouseTriggered[2] = 1;
//		println("action: " + buttonTexts[2]);
//		//valve 1 open
//		switchTask(2, 1);
//		
//		delay(200);
//		mouseTriggered[3] = 0;
//		println("action: " + buttonTextsBackup[3]);
//		//valve 2 close
//		switchTask(3, 0);
//		
//				
//		delay(200);
//		//valve 3 open
//		mouseTriggered[4] = 1;
//		println("action: " + buttonTexts[4]);
//		switchTask(4, 1);
		
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
			
			rect(rectXs[itrr], rectYs[itrr], rectWidth[itrr], rectHeight);
			
			textSize(32);
			fill(0, 102, 153);
			
			if(mouseTriggered[itrr] == 1)
			{
				text(buttonTexts[itrr], rectXs[itrr] + rectWidth[itrr] / 2 - textWidth(buttonTexts[itrr]) / 2, rectYs[itrr] + 2 * rectHeight / 3); 
			}else
			{
				text(buttonTextsBackup[itrr], rectXs[itrr] + rectWidth[itrr] / 2 - textWidth(buttonTexts[itrr]) / 2, rectYs[itrr] + 2 * rectHeight / 3); 
			}
			
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
		
		timeSeriesPlot.draw();
		
		//draw temperature
		//show the velocity
		textSize(64);
		String temperatureText = "" + temperatureValue + " C";
		text(temperatureText, 80, 750);
		
	}

	
	private void update(int x, int y)
	{
		for(int itrr = 0; itrr < numButtons; itrr++)
		{
			if(overRect(rectXs[itrr], rectYs[itrr], rectWidth[itrr], rectHeight))
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
			
			switch(task) {
//			case 0:
//				threading = false;
//				//task stop 
//				try {
//					serialOutput_One.write('s');
//					pumpVelocity = 0;
//				} catch (Exception ex) {
//					return;
//				}
//				break;
//			case 1:
//				threading = false;
//				//task stop 
//				try {
//					serialOutput_One.write('s');
//					pumpVelocity = 0;
//				} catch (Exception ex) {
//					return;
//				}
//				break;
			
			case 0:
				break;
			case 1:
				break;
			case 2:  //both pump stop
				thread("pumpOff");
				break;
			
			case 3:  //valve 1 to close
				thread("valve1Close");
				thread("valve2Open");
				mouseTriggered[4] = 1;
				break;
				
			case 4:  //valve 2 to close
				thread("valve2Close");
				thread("valve1Open");
				mouseTriggered[3] = 1;
				break;
				
			case 5: //valve 3 to close
				thread("valve3Close");
				break;

			}

		}else  //turn on tasks
		{
			switch (task) {
//			case 0:  //left vibrating
//				try {
//					serialOutput_One.write('l');
//					pumpVelocity = 255;
//					
//				} catch (Exception ex) {
//					return;
//				}
//				break;
//			case 1:  // right vibrating, will not be used for now
//				try {
//					serialOutput_One.write('r');
//					pumpVelocity = 255;
//				} catch (Exception ex) {
//					return;
//				}
//				break;
			case 0:  //cold
				thread("moreCold");
				break;
			case 1:  //hot
				thread("moreHot");
				break;
			case 2:
				thread("pumpOn");
				break;
				
			case 3:  //valve 1 to open
				thread("valve1Open");
				thread("valve2Close");
				mouseTriggered[4] = 0;
				break;
			case 4: //valve 2 open
				thread("valve2Open");
				thread("valve1Close");
				mouseTriggered[3] = 0;
				break;
				
			case 5:  //valve 3 to open
				thread("valve3Open");
				break;
					
				
			
			}
		}
		
	}
	
	void configurePort_One(String _portName) {
		portName_One = _portName;
	}
	
	void configurePort_Two(String _portName)
	{
		portName_Two = _portName;
	}
	
	void connectPort_One() {
		try {
			CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName_One);
			if (portIdentifier.isCurrentlyOwned()) {
				System.out.println("Error: Port is currently in use");
			} else {
				CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
				
				if (commPort instanceof SerialPort) {
					serialPort_One = (SerialPort) commPort;

					// Set appropriate properties (do not change these)
					serialPort_One.setSerialPortParams(
							9600, 
							SerialPort.DATABITS_8,
							SerialPort.STOPBITS_1, 
							SerialPort.PARITY_NONE);

					serialInput_One = serialPort_One.getInputStream();
					//input = new BufferedReader(new InputStreamReader(serialInput));				
					serialOutput_One = serialPort_One.getOutputStream();
					
					//serialPort.addEventListener(this);
			        //serialPort.notifyOnDataAvailable(true);
			      
					System.out.println("Connected to port: " + portName_One);
				} else {
					System.out.println("Error: Only serial ports are handled by this example.");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
	}
	
	void connectPort_Two() {
		try {
			CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName_Two);
			if (portIdentifier.isCurrentlyOwned()) {
				System.out.println("Error: Port is currently in use");
			} else {
				CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
				
				if (commPort instanceof SerialPort) {
					serialPort_Two = (SerialPort) commPort;

					// Set appropriate properties (do not change these)
					serialPort_Two.setSerialPortParams(
							9600, 
							SerialPort.DATABITS_8,
							SerialPort.STOPBITS_1, 
							SerialPort.PARITY_NONE);

					serialInput_Two = serialPort_Two.getInputStream();
					input_Two = new BufferedReader(new InputStreamReader(serialInput_Two));				
					serialOutput_Two = serialPort_Two.getOutputStream();
					
					serialPort_Two.addEventListener(this);
			        serialPort_Two.notifyOnDataAvailable(true);
			      
					System.out.println("Connected to port: " + portName_Two);
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
			System.out.println("Disconnecting from port: " + portName_One);
			serialInput_One.close();
			serialOutput_One.close();
			serialPort_One.close();
			
		} catch (Exception ex){
			ex.printStackTrace();
		}
		
		delay(100);
		
		try {
			System.out.println("Disconnecting from port: " + portName_Two);
			serialInput_Two.close();
			serialOutput_Two.close();
			serialPort_Two.close();
			
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		 if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
		    try {
		        String inputLine=null;
		        if (input_Two.ready()) {
		            inputLine = input_Two.readLine();
		            //System.out.println(inputLine);
		            try {
		            	float readValue = Float.parseFloat(inputLine);
		            	if(readValue > 100) {
		            		timeSeriesPlot.addValue(readValue);
		            	}
		            	else
		            	{
		            		//System.out.println("temperature: " + readValue);
		            		temperatureValue = readValue;
		            	}
		            }catch(Exception ex)
		            {
		            	return;
		            }
		            
		        }

		    } catch (Exception e) {
		        System.err.println(e.toString());
		    }
		 }
		// Ignore all the other eventTypes, but you should consider the other ones.
		}
	
	
	public void keyPressed() {
		if (key == 'q') {
			
			//valveZero();
			
//			//stop pop and reset valve
//			try {
//				serialOutput_One.write('s');
//				pumpVelocity = 0;
//			} catch (Exception ex) {
//				return;
//			}
			
			thread("pumpOff");
		
			delay(1000);
			
			disconnectPort();
			leap.end();
			exit();
			
			
		}else if(key == CODED)
		{
			//try not using these
			if(keyCode == LEFT)
			{
				//thread("leftPump");
			}else if(keyCode == RIGHT)
			{
				//thread("rightPump");
			}else if(keyCode == UP)
			{
//				if(pumpVelocity + velocityStep <= 255)
//				{
//					pumpVelocity += velocityStep;
//					
//					thread("fasterVelocity");
//					
//				}
			}else if(keyCode == DOWN)
			{
//				if(pumpVelocity - velocityStep >= 0)
//				{
//					pumpVelocity -= velocityStep;
//					
//					thread("slowerVelocity");
//				}
			}
		}
		

	}
	
	
//	public void leftPump()
//	{
//		threading = true;
//		
//		try {
//			serialOutput_One.write('l');
//		} catch (Exception ex) {
//			return;
//		}
//		
//		delay(1000);
//		
//		try {
//			serialOutput_One.write('s');
//		} catch (Exception ex) {
//			return;
//		}
//		
//		threading = false;
//		
//	}
//	
//	
//	public void rightPump()
//	{
//		threading = true;
//		
//		try {
//			serialOutput_One.write('r');
//		} catch (Exception ex) {
//			return;
//		}
//		
//		delay(1000);
//		
//		try {
//			serialOutput_One.write('s');
//		} catch (Exception ex) {
//			return;
//		}
//		
//		threading = false;
//		
//	}
//	
//	public void fasterVelocity()
//	{
//		threading = true;
//		
//		try {
//			serialOutput_One.write('i');
//		}catch(Exception ex){
//			return;
//		}
//		
//		threading = false;
//	}
//	
//	public void slowerVelocity()
//	{
//		threading = true;
//		
//		try {
//			serialOutput_One.write('d');
//		}catch(Exception ex){
//			return;
//		}
//		
//		threading = false;
//	}
//	
//	
//	public void valveUp()
//	{
//		threading = true;
//		//give some delay
//		delay(100);
//		
//		try {
//			serialOutput_One.write('p');
//		} catch (Exception ex) {
//			return;
//		}
//
//		mouseTriggered[2] = 0; 
//		threading = false;
//	}
//	
//	public void valveDown()
//	{
//		threading = true;
//		//give some delay
//		delay(100);
//		
//		try {
//			serialOutput_One.write('o');
//		} catch (Exception ex) {
//			return;
//		}
//		
//		mouseTriggered[3] = 0; 
//		threading = false;
//	}
	
	
	public void pumpOn()
	{
		threading = true;
		
		try {
			serialOutput_One.write('r');
		} catch (Exception ex) {
			return;
		}
		
		threading = false;
	}
	
	public void pumpOff()
	{
		threading = true;
		
		try {
			serialOutput_One.write('t');
		} catch (Exception ex) {
			return;
		}
		
		threading = false;
	}
	
	public void moreCold()
	{
		threading = true;
		//give some delay
		delay(100);
		
		try {
			serialOutput_One.write('w');
		} catch (Exception ex) {
			return;
		}
		
		mouseTriggered[0] = 0; 
		threading = false;
	}
	
	public void moreHot()
	{
		threading = true;
		//give some delay
		delay(100);
		
		try {
			serialOutput_One.write('e');
		} catch (Exception ex) {
			return;
		}
		
		mouseTriggered[1] = 0; 
		threading = false;
	}
	
	
	public void valve1Open()
	{
		threading = true;
		
		try {
			serialOutput_One.write('v');
		} catch (Exception ex) {
			return;
		}

		threading = false;
	}
	
	public void valve1Close()
	{
		threading = true;
		
		try {
			serialOutput_One.write('b');
		} catch (Exception ex) {
			return;
		}
		
		threading = false;
	}
	
	public void valve2Open()
	{
		threading = true;
		
		try {
			serialOutput_One.write('f');
		} catch (Exception ex) {
			return;
		}

		threading = false;
	}
	
	public void valve2Close()
	{
		threading = true;
		
		try {
			serialOutput_One.write('g');
		} catch (Exception ex) {
			return;
		}
		
		threading = false;
	}
	
	public void valve3Open()
	{
		threading = true;
		
		try {
			serialOutput_One.write('h');
		} catch (Exception ex) {
			return;
		}

		threading = false;
	}
	
	public void valve3Close()
	{
		threading = true;
		
		try {
			serialOutput_One.write('j');
		} catch (Exception ex) {
			return;
		}
		
		threading = false;
	}
	
//	public void valveZero()
//	{
//		threading = true;
//		//give some delay
//		delay(100);
//		
//		try {
//			serialOutput_One.write('z');
//		} catch (Exception ex) {
//			return;
//		}
//		
//		mouseTriggered[4] = 0; 
//		threading = false;
//	}
//	
//	public void valveMax()
//	{
//		threading = true;
//		//give some delay
//		delay(100);
//				
//		try {
//			serialOutput_One.write('m');
//		} catch (Exception ex) {
//			return;
//		}
//		
//		mouseTriggered[5] = 0; 
//		threading = false;
//	}
//	
//	public void heatOff()
//	{
//		threading = true;
//		
//		try {
//			serialOutput_One.write('u');
//		} catch (Exception ex) {
//			return;
//		}
//		
//		threading = false;
//	}
//	
//	public void heatOn()
//	{
//		threading = true;
//		
//		try {
//			serialOutput_One.write('y');
//		} catch (Exception ex) {
//			return;
//		}
//		
//		threading = false;
//	}
	
	
	public void initializeButton()
	{
		valveMax();
		
		//switch on the flow
		switchTask(0, 1);
		mouseTriggered[0] = 1;
		
		
		//schedule a stop in 2 secs
		sheduleStop(2000);
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
