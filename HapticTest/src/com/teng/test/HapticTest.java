package com.teng.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import processing.core.PApplet;

public class HapticTest extends PApplet{

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
	
	
	//serial - for finger pressure
	static SerialPort serialPort_Three;
	static InputStream serialInput_Three;
	static BufferedReader input_Three;
	static OutputStream serialOutput_Three;
	static String portName_Three;

	
	//serial - for finger pressure
	static SerialPort serialPort_Four;
	static InputStream serialInput_Four;
	static BufferedReader input_Four;
	static OutputStream serialOutput_Four;
	static String portName_Four;
	
	
	
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
	float filteredTemperatureValue;
	float targetTemperature;
	float actualTarget;
	boolean targetSet = false;
	TimeSeriesPlot temperateSeriesPlot;
	
	
	//finger pressure
	TimeSeriesPlot fingerPressurePlot;
	
	//vibration
	int vibFrequency = 0;
	
	
	//pid controller for temperature
	MiniPID miniPID; 
	double pidOutput  = 0;
	double prevPidOutput = 0;
	
	float hotTemp = 60;
	float coldTemp = 25;
	

	float alpha = 0.0f;
	float delta = 0.0f;
	
	
	// 0 - stop, 1 - fast hot, 2 - slow hot, 3 - fast cold, 4 - slow cold
	int pumpState = 0;
	int pumpOneSpeed = 0;
	int pumpTwoSpeed = 0;
	
	//for testing with text input
	String inputText = "";
	int maxSpeed = 250;
	float changePoint = 0.5f;
	
	
	//demo squeeze
	Squeeze squeeze;
	
	
	//for offline calibration
		ArrayList<Float> groundValues;
		ArrayList<Float> observedValues;
	DataStorage dataStorage;
	
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
		numButtons = 16;
		rectXs = new int[] {100, 100, 100, 600, 600, 600, 900, 900, 350,
			100, 400, 100, 400, 100, 400, 800	
		};
		rectWidth = new int[] {200, 200, 300, 300, 300, 300, 100, 100, 100,
			200, 200, 200, 200, 200, 200, 200	
		};
		rectHeight = 50;
		rectYs = new int[] {(int)(rectHeight * 3.8), (int)(rectHeight * 5.2), (int)(rectHeight * 1.0), //cold hot on
				(int)(rectHeight * 0.2), (int)(rectHeight * 1.3), (int)(rectHeight * 2.4), //valves
				(int)(rectHeight * 3.8), (int)(rectHeight * 5.2),  // + 10 -10
				(int)(rectHeight * 3.8),  /// <<< >>>
				(int)(rectHeight * 8.8), (int)(rectHeight * 8.8), 
				(int)(rectHeight * 10.3), (int)(rectHeight * 10.3),
				(int)(rectHeight * 13.8), (int)(rectHeight * 13.8),
				(int)(rectHeight * 8.8)
		};
	
		
		//render when 1
		buttonTexts = new String[] {"Cold", "Hot", "On", "Valve 1 : Open", "Valve 2 : Open", "Valve 3: Open", "+10", "-10", ">>>", 
				"Amplitude up", "Amplitude down", "Frequency up", "Frequency down", "Pressure up", "Pressure down", "Vib On"
		
		};
		
		//render when 0
		buttonTextsBackup = new String[] {"Cold", "Hot", "Off", "Valve 1 : Close", "Valve 2 : Close", "Valve 3: Close", "+10", "-10", "<<<",
				"Amplitude up", "Amplitude down", "Frequency up", "Frequency down", "Pressure up", "Pressure down", "Vib Off"
				
		};
		
		mouseTriggered = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};  //0 - not active, 1 - active
		
		//control
		configurePort_One("COM10");
		connectPort_One();
			
		delay(1000);
		
		//sensor
		configurePort_Two("COM8");
		connectPort_Two();
		
		delay(1000);
//		
//		
//		//finger pressure
//		configurePort_Three("COM13");
//		connectPort_Three();
//		delay(1000);
//		
//		
//		//sensor for evaluation
//		configurePort_Four("COM4");
//		connectPort_Four();
//		delay(1000);
//		
//		
//		
		leap = new Leap();
//		
//		
//		delay(1000);
//		button = new Button(this, 400, 700);
//		//thread("initializeButton");
//		
//		slider = new Slider(this, 600, 700);
//		
		timeSeriesPlot = new TimeSeriesPlot(this, windowWidth /2, 760, windowWidth, 200, 500, false, false, true);
		timeSeriesPlot.setShampen(1000);
		timeSeriesPlot.setMinMax(900, 1200, true);
		
		//timeSeriesPlot.isTesting = true;
		
		temperateSeriesPlot = new TimeSeriesPlot(this, windowWidth / 2, 500, windowWidth, 200, 500, true, false, false);
		temperateSeriesPlot.setMinMax(20, 25, true);
		temperateSeriesPlot.setShampen(2);
		
		
		fingerPressurePlot = new TimeSeriesPlot(this, windowWidth / 2, 1000, windowWidth, 200, 500, false, false, false);
		fingerPressurePlot.setShampen(200);
		fingerPressurePlot.setMinMax(0, 1000, true);
		//fingerPressurePlot.isTesting = true;
		
//		
//		//open at least one valve
//		mouseTriggered[3] = 1;
//		println("action: " + buttonTexts[3]);
//		//valve 1 open
//		switchTask(3, 1);
//		
//		delay(200);
//		mouseTriggered[4] = 0;
//		println("action: " + buttonTextsBackup[4]);
//		//valve 2 close
//		switchTask(4, 0);
		
				
		delay(200);
		//valve 3 open
		mouseTriggered[5] = 1;
		println("action: " + buttonTexts[5]);
		switchTask(5, 1);
		
		
		//initialize pid controller
		//output should be a ratio: portion of hot water flow speed in the total speed
		//if total speed is 255
		//output: 1 - hot 255, cold 0 ; 0.5 - hot 127 cold 127; 0 - hot 0 cold 255
		miniPID = new MiniPID(0.1, 0.0, 0.2);  // no integral part
		miniPID.setOutputLimits(10);
		//miniPID.setMaxIOutput(2);
		//miniPID.setOutputRampRate(3);
		//miniPID.setOutputFilter(.3);
		miniPID.setSetpointRange(40);
		
		
		delay(200);
		//set to clockwise by default
		thread("setClockWise");
		
		//demo squeeze
		squeeze = new Squeeze(this);
		
		
		//thread("setValueTwoOffset");
		groundValues = new ArrayList<Float>();
		observedValues= new ArrayList<Float>();
		
		dataStorage = DataStorage.getInstance();
		
		
	}
	
	
	public void setValueTwoOffset()
	{
		delay(7000);
		timeSeriesPlot.setValueTwo();
	}
	
	public void draw()
	{
		
		background(225, 225, 225);
		
		update(mouseX, mouseY);
		
		
		//temperature zone
		noStroke();
		fill(200);
		rect(0, (int)(rectHeight * 3.5),  windowWidth, 150);
		
		textSize(36);
		fill(120);
		text("Temperature", 20, (int)(rectHeight * 3.0));
		
		//vibration zone 
		noStroke();
		fill(200);
		rect(0, (int)(rectHeight * 8.5),  windowWidth, 150);
		
		textSize(36);
		fill(120);
		text("Vibration", 20, (int)(rectHeight * 8.0));
		
		
		
		//pressure zone
		noStroke();
		fill(200);
		rect(0, (int)(rectHeight * 13.5),  windowWidth, 320);
		
		textSize(36);
		fill(120);
		text("Pressure", 20, (int)(rectHeight * 13.0));
		
		
		for(int itrr = 0; itrr < numButtons; itrr++)
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
			
			if(itrr == 0)
			{
				if(mouseTriggered[itrr] == 1)
				{
					text(buttonTexts[itrr] + " " + pumpOneSpeed, rectXs[itrr] + rectWidth[itrr] / 2 - textWidth(buttonTexts[itrr]) / 2, rectYs[itrr] + 2 * rectHeight / 3); 
				}else
				{
					text(buttonTextsBackup[itrr] + " " + pumpOneSpeed, rectXs[itrr] + rectWidth[itrr] / 2 - textWidth(buttonTexts[itrr]) / 2, rectYs[itrr] + 2 * rectHeight / 3); 
				}
			}else if(itrr == 1)
			{
				if(mouseTriggered[itrr] == 1)
				{
					text(buttonTexts[itrr] + " " + pumpTwoSpeed, rectXs[itrr] + rectWidth[itrr] / 2 - textWidth(buttonTexts[itrr]) / 2, rectYs[itrr] + 2 * rectHeight / 3); 
				}else
				{
					text(buttonTextsBackup[itrr] + " " + pumpTwoSpeed, rectXs[itrr] + rectWidth[itrr] / 2 - textWidth(buttonTexts[itrr]) / 2, rectYs[itrr] + 2 * rectHeight / 3); 
				}
			}else{
				if(mouseTriggered[itrr] == 1)
				{
					text(buttonTexts[itrr], rectXs[itrr] + rectWidth[itrr] / 2 - textWidth(buttonTexts[itrr]) / 2, rectYs[itrr] + 2 * rectHeight / 3); 
				}else
				{
					text(buttonTextsBackup[itrr], rectXs[itrr] + rectWidth[itrr] / 2 - textWidth(buttonTexts[itrr]) / 2, rectYs[itrr] + 2 * rectHeight / 3); 
				}
			}
			
		}
		
		
		//show the velocity
		textSize(64);
		String showText = "" + pumpVelocity;
		text(showText, windowWidth / 2 - textWidth(showText) / 2, windowHeight * 9 / 10);
		
						
//		//finger
//		leapX = leap.indexTipX * 2 + windowWidth / 2;
//		leapY = windowHeight - leap.indexTipY * 2;
//		
//		stroke(153);
//		noFill();
//		arc(leapX, leapY - 25, 100, 50, 0, PI);
//		
//		//new button position
//		button.detectTouch(leapX, leapY, prev_leapX, prev_leapY);
//
//		prev_leapX = leapX;
//		prev_leapY = leapY;
//		
//		
//		button.draw();
//		
//		slider.draw();
		
		timeSeriesPlot.draw();
		
		fingerPressurePlot.draw();
		
		//draw temperature
		//show the velocity
		textSize(64);
		String temperatureText = "" + filteredTemperatureValue + " C";  //now the resolution is 0.2
		text(temperatureText, 350, 300);
		
		//show hot and cold 
		textSize(32);
		text(coldTemp, 20, 250);
		text(hotTemp, 20, 300);

		if(targetSet == false)
		{
			targetTemperature =  (int) filteredTemperatureValue;
		}
		
		String targetTemperatureText = "" + targetTemperature + " C";
		text(targetTemperatureText, 700, 260);
		
		//temperateSeriesPlot.draw();
		
		//show the virbation frequency
		textSize(48);
		String frequencyText = "" + vibFrequency + " Hz"; 
		text(frequencyText, 800, 550);
		
		
		if(mouseTriggered[2] == 1)
		{
			//AdjustTemperature(targetTemperature);
		}
		
		
		//draw teting text
		textSize(24);
		text("input text: " + inputText, 700, 300);
		
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
				//println("action: " + buttonTexts[rectOverIndex]);
				
				//switch on
				switchTask(rectOverIndex, 1);
			}
		}
		
		timeSeriesPlot.mousePress(mouseX, mouseY);
		fingerPressurePlot.mousePress(mouseX, mouseY);
	}
	
	public void mouseReleased()
	{
		timeSeriesPlot.mouseRelease(mouseX, mouseY);
		fingerPressurePlot.mouseRelease(mouseX, mouseY);
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
				
			case 6:
				break;
			case 7:
				break;
			case 8:
				thread("setAntiClockWise");
				break;
			
			case 9:
				break;
			case 10:
				break;
			case 11:
				break;
			case 12:
				break;
			case 13:
				break;
			case 14:
				break;
			case 15:  //vibration off
				thread("stopVibration");
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
					
			case 6:
				thread("increaseTen");
				mouseTriggered[2] = 1;
				break;
			case 7:
				thread("decreaseTen");
				mouseTriggered[2] = 1;
				break;
				
			case 8:
				thread("setClockWise");
				break;
				
				
			case 9:
				thread("increaseVibAmplitude");
				break;
			case 10:
				thread("decreaseVibAmplitude");
				break;
			case 11:
				thread("increaseVibFrequency");
				break;
			case 12:
				thread("decreaseVibFrequency");
				break;
			case 13:
				thread("increasePressure");
				break;
			case 14:
				thread("decreasePressure");
				break;
			case 15:  //vibration on
				thread("startVibration");
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
	
	void configurePort_Three(String _portName)
	{
		portName_Three = _portName;
	}
	
	void configurePort_Four(String _portName)
	{
		portName_Four = _portName;
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
							115200, 
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
							115200, 
							SerialPort.DATABITS_8,
							SerialPort.STOPBITS_1, 
							SerialPort.PARITY_NONE);

					serialInput_Two = serialPort_Two.getInputStream();
					input_Two = new BufferedReader(new InputStreamReader(serialInput_Two));				
					serialOutput_Two = serialPort_Two.getOutputStream();
					
					serialPort_Two.addEventListener(new SerialListener(2, this));
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
	
	
	void connectPort_Three() {
		try {
			CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName_Three);
			if (portIdentifier.isCurrentlyOwned()) {
				System.out.println("Error: Port is currently in use");
			} else {
				CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
				
				if (commPort instanceof SerialPort) {
					serialPort_Three = (SerialPort) commPort;

					// Set appropriate properties (do not change these)
					serialPort_Three.setSerialPortParams(
							115200, 
							SerialPort.DATABITS_8,
							SerialPort.STOPBITS_1, 
							SerialPort.PARITY_NONE);

					serialInput_Three = serialPort_Three.getInputStream();
					input_Three = new BufferedReader(new InputStreamReader(serialInput_Three));				
					serialOutput_Three = serialPort_Three.getOutputStream();
					
					serialPort_Three.addEventListener(new SerialListener(3,this));
			        serialPort_Three.notifyOnDataAvailable(true);
			      
					System.out.println("Connected to port: " + portName_Three);
				} else {
					System.out.println("Error: Only serial ports are handled by this example.");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
	}
	
	
	void connectPort_Four() {
		try {
			CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName_Four);
			if (portIdentifier.isCurrentlyOwned()) {
				System.out.println("Error: Port is currently in use");
			} else {
				CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
				
				if (commPort instanceof SerialPort) {
					serialPort_Four = (SerialPort) commPort;

					// Set appropriate properties (do not change these)
					serialPort_Four.setSerialPortParams(
							115200, 
							SerialPort.DATABITS_8,
							SerialPort.STOPBITS_1, 
							SerialPort.PARITY_NONE);

					serialInput_Four = serialPort_Four.getInputStream();
					input_Four = new BufferedReader(new InputStreamReader(serialInput_Four));				
					serialOutput_Four = serialPort_Four.getOutputStream();
					
					serialPort_Four.addEventListener(new SerialListener(4, this));
			        serialPort_Four.notifyOnDataAvailable(true);
			      
					System.out.println("Connected to port: " + portName_Four);
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
		
		delay(100);
		
//		try {
//			System.out.println("Disconnecting from port: " + portName_Three);
//			serialInput_Three.close();
//			serialOutput_Three.close();
//			serialPort_Three.close();
//			
//		} catch (Exception ex){
//			ex.printStackTrace();
//		}
//		
//		delay(100);
//		try {
//			System.out.println("Disconnecting from port: " + portName_Four);
//			serialInput_Four.close();
//			serialOutput_Four.close();
//			serialPort_Four.close();
//			
//		} catch (Exception ex){
//			ex.printStackTrace();
//		}
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
			//leap.end();
			exit();
			
			
		}else if(key == 's')
		{
			thread("setValueTwoOffset");
			
		}else if(key =='d')
		{
			squeeze.startVibration();
		}else if(key == 'f')
		{
			//squeeze.stopVirbation();
			temperatureworking = false;
		}else if(key == 'u')
		{
			coldTemp -= 1;
		}else if(key == 'i')
		{
			coldTemp += 1;
		}else if(key == 'j')
		{
			hotTemp -= 1;
		}else if(key == 'k')
		{
			hotTemp += 1;
		}
		
		
		
		else if(key == CODED)
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
		}else if(key == ENTER || key == RETURN)
		{
			
			if(inputText != null && inputText.length() > 0)
			{
				
				float inputValue = Float.parseFloat(inputText);
				
				if(inputValue > 0 && inputValue < 100)
				{
//					targetSet = true;
//					targetTemperature = inputValue;
//					
//					if(targetTemperature > filteredTemperatureValue)
//					{
//						actualTarget = targetTemperature + 0;
//					}else
//					{
//						actualTarget = targetTemperature - 0;
//					}
//					
//					temperatureworking = true;
//					thread("temperatureThread");
//					
//					mouseTriggered[2] = 1;
					
			
				}
				
				
				//calculateWater(inputValue);
				
//				if(inputValue > 0)
//				{
//					inputText = Float.toString(Math.abs(inputValue)) + 'z';
//				}else
//				{
//					inputText = Float.toString(Math.abs(inputValue)) + 'x';
//				}
				
				
//				///to test////
//				float inputValue = Float.parseFloat(inputText);
//				if(inputValue >= 0 && inputValue <= 1)
//				{
//					testWater(inputValue);
//					inputText = Float.toString(Math.abs(inputValue)) + 'c';
//					try {
//						serialOutput_One.write(inputText.getBytes());  //full speed hot
//					} catch (Exception ex) {
//						return;
//					}
//					mouseTriggered[2] = 1;
//				}
//				
//				////////////
				
			}
			
			
			inputText = "";
		}else if(key == BACKSPACE)
		{
			 if (inputText != null && inputText.length() > 0) {
				 inputText = inputText.substring(0, inputText.length() - 1);
			  }
		}
		else
		{
			inputText = inputText + key;
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
		
		delay(1000);
		targetSet = false;
				
		try {
			serialOutput_One.write('t');
		} catch (Exception ex) {
			return;
		}
		
		pumpOneSpeed = 0;
		pumpTwoSpeed = 0;
		
		alpha = 0.0f;
		delta = 0;
		
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
		
		pumpOneSpeed = 250;
		
		mouseTriggered[0] = 0; 
		mouseTriggered[2] = 1;
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
		
		pumpTwoSpeed = 250;
		
		mouseTriggered[1] = 0; 
		mouseTriggered[2] = 1;
		threading = false;
	}
	
	//only for ui
	public void increaseTen()
	{
		threading = true;
		delay(100);
		
		targetSet = true;
		targetTemperature += 10;
		
		mouseTriggered[6] = 0; 
		threading = false;
	}
	
	public void decreaseTen()
	{
		threading = true;
		delay(100);
		
		targetSet = true;
		targetTemperature -= 10;
		
		mouseTriggered[7] = 0; 
		threading = false;
	}
	
	
	public void setClockWise()
	{
		threading = true;
		
		//some delay to protect?
		
		try {
			serialOutput_One.write('i');
		} catch (Exception ex) {
			return;
		}
		
		mouseTriggered[8] = 1; 
		threading = false;
	}
	
	public void setAntiClockWise()
	{
		threading = true;
		try {
			serialOutput_One.write('o');
		} catch (Exception ex) {
			return;
		}
		
		mouseTriggered[8] = 0; 
		threading = false;
	}
	
	
	
	
	boolean temperatureworking = false;
	
	public void temperatureThread()
	{
		while(temperatureworking)
		{
			AdjustTemperature(actualTarget);
			delay(500);
		}
		
		pumpOff();
	}
	
	
	//for pump to work
	//run every 0.5 seconds
	public void AdjustTemperature(float target) 
	{
		float change = target - filteredTemperatureValue;
		
		if(Math.abs(change) >= 0.1)
		{
			//the output is desired change in the next step
			pidOutput = miniPID.getOutput(filteredTemperatureValue, target);
		
			
//			if(pidOutput == prevPidOutput)
//			{
//				//do nothing
//			}else
			{
				if(pidOutput >= 0)
				{
//					if(pidOutput >= 10.0f)
//					{
//						pidOutput = 9.99f;
//					}
					
					String valueTwoDecial = String.format("%.2f", Math.abs(pidOutput));
					String valuetosend = valueTwoDecial + "z";
					float temp = Float.parseFloat(valueTwoDecial);
					calculateWater(temp);
					
					println("pid " + temp);
					try {
						serialOutput_One.write(valuetosend.getBytes());  //full speed hot
					} catch (Exception ex) {
						return;
					}
				}else
				{
//					if(pidOutput <= -10.0f)
//					{
//						pidOutput = -9.99f;
//					}
					
					String valueTwoDecial = String.format("%.2f", Math.abs(pidOutput));
					String valuetosend = valueTwoDecial + "x";
					float temp = Float.parseFloat(valueTwoDecial) * (-1);
					calculateWater(temp);
					
					println("pid " + temp);
					try {
						serialOutput_One.write(valuetosend.getBytes());  //full speed hot
					} catch (Exception ex) {
						return;
					}
				}
			}
			
			prevPidOutput = pidOutput;
			
		}

	}
	
	
	
	
//	void calculateWater(float ratio)
//	{
//	
//	  changePoint = 0.7f;
//		
//	  if(ratio > changePoint)
//	  {
//	    //full speed
//	    pumpTwoSpeed = 250;
//	    pumpOneSpeed = 0;
//	  }else if(ratio <= changePoint && ratio >= 0)
//	  {
//	    //90 - 250 reduce speed and balance the cold and hot
//	    int totalSpeed = 100;// (int)(250 - (1.5 - ratio) * 50);  // 250 - 200
//
//	    // 0 - 90/90
//	    float pTwo = (float)((changePoint + ratio) / (2 * changePoint));  // 100 - 50
//	    float pOne = (float)((changePoint - ratio) / (2 * changePoint));  //0 - 50
//	    pumpTwoSpeed = (int)(60 + pTwo * totalSpeed);
//	    pumpOneSpeed = (int)(60 + pOne * totalSpeed);
//	  }else if(ratio < 0 && ratio >= (changePoint * (-1)))
//	  {
//	    //90 - 250 reduce speed and balance the cold and hot
//	    int totalSpeed = 100; // (int)(250 - (1.5 + ratio) * 50);  // 250 - 200
//
//	    // 0 - 90/90
//	    float pTwo = (float)((changePoint + ratio) / (2 * changePoint));
//	    float pOne = (float)((changePoint - ratio) / (2 * changePoint));
//	    pumpTwoSpeed = (int)(60 + pTwo * totalSpeed);
//	    pumpOneSpeed = (int)(60 + pOne * totalSpeed);
//	  }else if(ratio < (changePoint * (-1)))
//	  {
//	    pumpTwoSpeed = 0;
//	    pumpOneSpeed = 250;
//	  }
//	}
	
	

	float lastpid = 0;
	boolean ispiding = false;

	void calculateWater(float pidvalue)
	{
		
		if(pidvalue > 0)
		{
			alpha = 1.0f + pidvalue * 5.0f;
			 
			  if(alpha > 3.5f)
			  {
				  alpha = 3.5f;
			  }
			  
			  println("alpha: " + alpha);
			  
			  //println("alpha: " + alpha);
			  pumpOneSpeed = 70;
			  pumpTwoSpeed = (int) ( alpha * pumpOneSpeed);
		}else
		{
			alpha = 1.0f + pidvalue * (-5.0f);
			 
			  if(alpha > 3.5f)
			  {
				  alpha = 3.5f;
			  }
			  
			  println("alpha: " + alpha);
			  
			  //println("alpha: " + alpha);
			  pumpTwoSpeed = 70;
			  pumpOneSpeed = (int) ( alpha * pumpTwoSpeed);
		}
	
		
	  
	  
	  
	}
		
	
	
	void testWater(float ratio)
	{
		pumpTwoSpeed = (int)(ratio * maxSpeed);
		pumpOneSpeed = (int)((1 - ratio) * maxSpeed);
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
	
	
	//vibration start
	public void startVibration()
	{
		threading = true;
		
		//valve 1 open
		if(mouseTriggered[3] == 0)
		{
			 valve1Open();
			 mouseTriggered[3] = 1;
			 delay(200);
		}
		
		//valve 3 open
		if(mouseTriggered[5] == 0)
		{
			 valve3Open();
			 mouseTriggered[5] = 1;
			 delay(200);
		}
		
		//cold flow at initial speed
		try {
			serialOutput_One.write('y');
		} catch (Exception ex) {
			return;
		}
		
		pumpOneSpeed = 130;
		mouseTriggered[2] = 1;
		
		delay(1000);
		//set initial vibration frequency
		vibFrequency = 5;
		squeeze.startVibration(vibFrequency);
		
		threading = false;
		
		
	}
	
	public void stopVibration()
	{
		threading = true;
		
		//valve 1 open
		if(mouseTriggered[3] == 0)
		{
			 valve1Open();
			 mouseTriggered[3] = 1;
			 delay(200);
		}
		
		//valve 3 open
		if(mouseTriggered[5] == 0)
		{
			 valve3Open();
			 mouseTriggered[5] = 1;
			 delay(200);
		}
		
		//flow stop
		try {
			serialOutput_One.write('t');
		} catch (Exception ex) {
			return;
		}
		
		pumpOneSpeed = 0;
		mouseTriggered[2] = 0;
		
		
		delay(200);
		//set initial vibration frequency
		squeeze.stopVirbation();
		vibFrequency = 0;
		
		threading = false;	
	}
	
	public void increaseVibFrequency()
	{
		threading = true;
		delay(100);
		vibFrequency += 2;
		squeeze.setVibration(vibFrequency);
		mouseTriggered[11] = 0;
		threading = false;
	}
	
	public void decreaseVibFrequency()
	{
		threading = true;
		delay(100);
		vibFrequency -= 2;
		squeeze.setVibration(vibFrequency);
		mouseTriggered[12] = 0;
		threading = false;
	}
	
	public void increaseVibAmplitude()
	{
		threading = true;
		delay(100);
		
		pumpOneSpeed += 5;
		
		if(pumpOneSpeed > 250)
		{
			pumpOneSpeed = 250;
		}else
		{
			try {
				serialOutput_One.write('l');
			} catch (Exception ex) {
				return;
			}
		}
		
		
		mouseTriggered[9] = 0;
		threading = false;
	}
	
	public void decreaseVibAmplitude()
	{
		threading = true;
		delay(100);
		
		pumpOneSpeed -= 5;
		
		if(pumpOneSpeed < 0)
		{
			pumpOneSpeed = 0;
		}else
		{
			try {
				serialOutput_One.write('k');
			} catch (Exception ex) {
				return;
			}
		}
		
		mouseTriggered[10] = 0;
		threading = false;
	}
	
	
	public void increasePressure()
	{
		threading = true;
		delay(100);
		
		squeeze.sendNotification();
		
		mouseTriggered[13] = 0;
		threading = false;
	}
	
	public void decreasePressure()
	{
		threading = true;
		delay(100);
		
		squeeze.releaseNotification();
		
		mouseTriggered[14] = 0;
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
		//valveMax();
		
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

class SerialListener implements SerialPortEventListener
{

	public int index;
	//public BufferedReader input;
	//public TimeSeriesPlot plot;
	
	HapticTest instance;
	
	
	public SerialListener(int _index, HapticTest _instance)
	{
		index = _index;
		instance = _instance;
	}
	
	@Override
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		// TODO Auto-generated method stub
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
		    try {
		        String inputLine=null;
		        
		        if(index == 2)
		        {
		        	 if(instance.input_Two.ready()) {
				        	inputLine = instance.input_Two.readLine();
				        	try {
				            	float readValue = Float.parseFloat(inputLine);         	
	
				            	
				            	//instance.timeSeriesPlot.addValue(readValue);
				            	
			            		if(readValue > 100) {
				            		instance.timeSeriesPlot.addValue(readValue);
				            	}
				            	else
				            	{
				            		//System.out.println("temperature: " + readValue)
				            		instance.temperatureValue = readValue;
				            		instance.temperateSeriesPlot.addValue(instance.temperatureValue);
				            		instance.filteredTemperatureValue = Float.parseFloat(String.format("%.1f", Math.abs( instance.temperateSeriesPlot.getLastFilteredValue())));
				            		
				            	}
				            	
				            }catch(Exception ex)
				            {
				            	return;
				            }
				     }
		        }else if(index == 3)
		        {
		        	 if(instance.input_Three.ready()) {
				        	inputLine = instance.input_Three.readLine();
				        	try {
				            	float readValue = Float.parseFloat(inputLine);         	
				            	instance.fingerPressurePlot.addValue(readValue);
				            
				            }catch(Exception ex)
				            {
				            	return;
				            }
				     }
		        }else if(index == 4)
		        {
		        	 if(instance.input_Four.ready()) {
		        		 inputLine = instance.input_Four.readLine();
				        	try {
				            	float readValue = Float.parseFloat(inputLine);         	
				            	instance.timeSeriesPlot.addValueTwo(readValue);
				            
				            }catch(Exception ex)
				            {
				            	return;
				            }
		        	 }
		        }

		    } catch (Exception e) {
		        System.err.println(e.toString());
		    }
		 }
		// Ignore all the other eventTypes, but you should consider the other ones.
	}
	
}
