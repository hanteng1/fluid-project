package com.teng.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import processing.core.PApplet;

public class PressureTest extends PApplet{
	int windowWidth, windowHeight;
	int rectColor, rectHighlight;
	
	ArrayList<Integer> rectXs;
	int rectY = 0;
	ArrayList<Integer> mouseTriggered;
	int  rectWidth, rectHeight;
	int rectOverIndex = -1;
	
	//****************************//
	int sensation = 1;   
	//****************************//
	int userId = 1;
	//****************************//
	int block = 0;    // 1, 2, 3
	
	int levels = 0;   //3, 5, 7 (or 9)
	public int trial = 0;
	int totalTrials = 0;
	ArrayList<Integer> trialSequence;
	ArrayList<Integer> trainSequence;
	int trainTrial = 0;
	int target = 0;
	int answer = 0;
	boolean waitingForAnswer = false;
	public boolean isTrialSequenceSet;
	
	boolean isTrainingMode = true;
	
	//always play a reference first
	boolean isReferencing = true;
	boolean blockDone = false;
	
	String promp = "";
	long responseTime = 0;
	long trialStartTime = 0;
	
	public static DataStorage dataStorage;
	
	//ring
	//serial - for pump and valves
	static SerialPort serialPort_One;
	static InputStream serialInput_One;
	static BufferedReader input_One;
	static OutputStream serialOutput_One;
	static String portName_One;
	
	//sensor
	static SerialPort serialPort_Two;
	static InputStream serialInput_Two;
	static BufferedReader input_Two;
	static OutputStream serialOutput_Two;
	static String portName_Two;
	
	TimeSeriesPlot pressurePlot;
	
	//to control pressure
	HapticController controller;
	
	boolean workingInProgress = false;
	public int rendering = 0;  //0 - nothing, 1 - render, 2 - ready
	
	
	public Client client;
	
	
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
		
		trialSequence = new ArrayList<Integer>();
		
		//ring
		configurePort_One("COM10");
		connectPort_One();
		delay(2000);

		configurePort_Two("COM8");
		connectPort_Two();
		delay(2000);
		
		pressurePlot = new TimeSeriesPlot(this, windowWidth /2, 760, windowWidth, 200, 500, false, false, false);
		pressurePlot.setShampen(1000);
		pressurePlot.setMinMax(900, 1200, true);
	
		//client
		client = new Client("10.142.197.9", 9090, this);	
		delay(1000);
		
		
		//waiting for setup of trial sequence
		while(isTrialSequenceSet == false)
		{
			
		}
		println("sequence set");
		
		rectWidth = windowWidth /( 2 * levels + 1);
		rectHeight = 50;
		rectXs = new ArrayList<Integer>();
		
		for(int itr = 0; itr < levels; itr++)
		{
			rectXs.add((2 * itr + 1) * rectWidth );
		}
		rectY = 300;
		
		mouseTriggered = new ArrayList<Integer>();
		for(int itr = 0; itr < levels; itr++)
		{
			mouseTriggered.add(0);
		}
		
		
		trainSequence = new ArrayList<Integer>();
		for(int itr = 1; itr < levels + 1; itr++)
		{
			trainSequence.add(itr);
		}
		for(int itr = 1; itr < levels + 1; itr++)
		{
			trainSequence.add(itr);
		}
		for(int itr = 1; itr < levels + 1; itr++)
		{
			trainSequence.add(itr);
		}
		
		controller = new HapticController(this);
		
		delay(1000);
		
		dataStorage = DataStorage.getInstance();
		dataStorage.userId = userId;
		dataStorage.sensation = "pressure";
		dataStorage.levels = levels;
		
		//read the current progress
		if(trial == 0)
		{
			isTrainingMode = true;
			if(isTrainingMode)
			{
				promp = "Train mode, press SPACE to the next";
			}
		}else
		{
			trial -= 1;
			isTrainingMode = false;
			if(isTrainingMode)
			{
				promp = "Press SPACE to next";
			}
		}
		
		//get ready
		thread("getWaterReady");
				
	}
	
	void configurePort_One(String _portName) {
		portName_One = _portName;
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
					serialOutput_One = serialPort_One.getOutputStream();
			      
					System.out.println("Connected to port: " + portName_One);
				} else {
					System.out.println("Error: Only serial ports are handled by this example.");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
	}
	
	void configurePort_Two(String _portName)
	{
		portName_Two = _portName;
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
					
					serialPort_Two.addEventListener(new PressureSerialListener(2, this));
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
	
	public void setClockWise()
	{
		try {
			serialOutput_One.write('i');
		} catch (Exception ex) {
			return;
		}
	}
	
	public void valveOpen()
	{
		try {
			serialOutput_One.write('h');
		} catch (Exception ex) {
			return;
		}
	}
	
	public void valveClose()
	{
		try {
			serialOutput_One.write('j');
		} catch (Exception ex) {
			return;
		}
	
	}
	
	public void runWater()
	{
		try {
			serialOutput_One.write('w');
		} catch (Exception ex) {
			return;
		}
	}
	
	public void stopWater()
	{
		try {
			serialOutput_One.write('t');
		} catch (Exception ex) {
			return;
		}
	}
	
	public void getWaterReady()
	{
		workingInProgress = true;
		setClockWise();
		delay(200);
		valveOpen();
		delay(200);
		
		runWater();
		delay(2000);
		stopWater();
		workingInProgress = false;
	}

	public void draw()
	{
		background(225, 225, 225);
		
		//block
		textSize(48);
//		fill(120);
//		text("Block " + block, 100, 100);
		
		//current trial / total trial
		fill(120);
		text("Trial " + trial + " / " + totalTrials , 400, 100);
		
		//ready?
		if(rendering == 0)
		{
			fill(200, 0, 0);
		}else if(rendering == 1)
		{
			fill(200, 200, 0);
		}else if(rendering == 2)
		{
			fill(0, 200, 0);
		}
		noStroke();
		ellipse(800, 85, 100, 100);
		
		//update(mouseX, mouseY);  //disable mouse select
		
		//choices
		for(int itr = 0; itr < levels; itr++)
		{
			if(mouseTriggered.get(itr) == 1)
			{
				fill(rectHighlight);
			}else
			{
				fill(rectColor);
			}
			
			rect(rectXs.get(itr), rectY, rectWidth, rectHeight);
			
			fill(0, 102, 153);
			textSize(32);
			String textShown = "" + (itr + 1);
			text(textShown, rectXs.get(itr) + rectWidth/ 2 - textWidth(textShown) / 2, rectY + 2 * rectHeight / 3); 
			
		}
		
	
		textSize(24);
		fill(120);
		text(promp, windowWidth/2 - textWidth(promp)/2, 200);
		
		
		//pressure signal
		pressurePlot.draw();
		
		//complete
		if(blockDone)
		{
			fill(200, 200, 200, 200);
			rect(0, 0, windowWidth, windowHeight);
			String textShown = "Block done, press Q to quit";
			fill(120);
			textSize(48);
			text(textShown, windowWidth/ 2 - textWidth(textShown) / 2, windowHeight / 2); 
		}
		
		if(workingInProgress)
		{
			fill(200, 200, 200, 200);
			rect(0, 0, windowWidth, windowHeight);
			String textShown;
			if(trial > 0)
			{
				textShown = "press SPACE to refamiliar... ";
			}else
			{
				textShown = "working...";
			}
			
			fill(120);
			textSize(48);
			text(textShown, windowWidth/ 2 - textWidth(textShown) / 2, windowHeight / 2); 
		}
		
	}
	
	public void makeChoice()
	{
		mouseTriggered.set(rectOverIndex, 1);
		answer = rectOverIndex + 1;
		
		//record/update the duration
		responseTime = System.currentTimeMillis() - trialStartTime;
		promp = "Press SPACE to release";
		waitingForAnswer = false;
	}
	
//	public void makePractice()
//	{
//		//render a target
//		mouseTriggered.set(rectOverIndex, 1);
//		
//		renderNext(target, isTrainingMode);  //render with releasing		
//	}
	
	
	public void keyPressed() {
		if (key == 'q') {
			
			dataStorage.save();
			
			stopWater();
			delay(200);
			
			valveOpen();
			delay(200);
			
			try {
				System.out.println("Disconnecting from port: " + portName_One);
				serialInput_One.close();
				serialOutput_One.close();
				serialPort_One.close();
				
			} catch (Exception ex){
				ex.printStackTrace();
			}
			
			delay(100);
			
			client.onDestroy();
			
			exit();
		}else if(key == 's')
		{
			if(rendering == 1)
			{
				thread("releaseWithAccident");
			}
		}else if(key == ' ')
		{
			if(workingInProgress && trial > 0)
			{
				isTrainingMode = true;
				workingInProgress = false;
				promp = "Train mode, press SPACE to next";
				
				//update the status to server: to the next trian/trial, the status
				//istrainingmode, workinginprogress, trial, target
				String msg = "m,";
				msg += "" + (isTrainingMode == true ? "1" : "0") + ",";
				msg += "" + (workingInProgress == true ? "1" : "0") + ",";
				msg += "" + trial + ",";
				msg += "" + target + ",";
				msg += "" + rectOverIndex + ",";
				msg += "\n";
				client.sendMessage(msg);
				
				return;
			}
			
			
			if(workingInProgress || rendering == 1)
			{
				return;
			}else if(rendering == 2 && waitingForAnswer == false)
			{
				thread("releaseWithSpace");
			}else if(rendering == 0)
			{
				if(blockDone == false)
				{
					if(isTrainingMode)
					{
						
						//render next train
						nextTrain();
						
//						isTrainingMode = false;
//						//start
//						nextTrial();
					}else
					{
						if(waitingForAnswer == false)
						{
							//record the data
							DataStorage.AddSample(trial, sensation, levels, target, answer, responseTime, answer == target ? 1 : 0);
							//send to server the answer for recording
							
							
							answer = 0;
							responseTime = 0;
							
							//go to next
							if(trial % 10 == 0)
							{
								workingInProgress = true;
							}else
							{
								nextTrial();
							}
							
							
						}	
					}
				}
				

				//update the status to server: to the next trian/trial, the status
				//istrainingmode, workinginprogress, trial, target
				String msg = "m,";
				msg += "" + (isTrainingMode == true ? "1" : "0") + ",";
				msg += "" + (workingInProgress == true ? "1" : "0") + ",";
				msg += "" + trial + ",";
				msg += "" + target + ",";
				msg += "" + rectOverIndex + ",";
				msg += "\n";
				client.sendMessage(msg);
			}

			
		}else
		{
			
			if(workingInProgress)
			{
				return;
			}else
			{
				String inpuText = "" + key;
				
//				if(isTrainingMode)
//				{
//					if(rendering > 0)
//					{
//						return;
//					}else
//					{
//						int inputValue = -1;
//						try {
//							inputValue = Integer.parseInt(inpuText);
//						}catch(Exception ex)
//						{
//							return;
//						}
//						
//						if(inputValue > 0 && inputValue < (levels + 1))
//						{			
//							rectOverIndex = inputValue - 1 ;
//							target = inputValue;
//							makePractice();
//						}
//					}
//					
//					
//				}else
				if(!isTrainingMode){
					if((waitingForAnswer || rectOverIndex >= 0) && rendering == 2)
					{
						int inputValue = -1;
						try {
							inputValue = Integer.parseInt(inpuText);
						}catch(Exception ex)
						{
							return;
						}
						
						if(inputValue > 0 && inputValue < (levels + 1))
						{
							if(rectOverIndex >= 0)
							{
								mouseTriggered.set(rectOverIndex, 0);
							}
							rectOverIndex = inputValue - 1 ;
							makeChoice();
							
							//update the status to serve : made a selection
							//the choice
							String msg = "c,";
							msg += "" + inputValue + ",";
							msg += "\n";
							client.sendMessage(msg);
							
							
						}
					}
				}
			}
			
			
			
		}
	}
	
	
	public void nextTrain()
	{	
		if(trial == 0)  //begining
		{
			//begin, all
			if(trainTrial <=  (2 * levels - 1))
			{
				//continue train
				target = trainSequence.get(trainTrial);
				trainTrial ++; 
				
				if(rectOverIndex > -1)
				{
					mouseTriggered.set(rectOverIndex, 0);
					rectOverIndex = -1;
				}
				
				rectOverIndex = target - 1;
				mouseTriggered.set(rectOverIndex, 1);
				
				renderNext(target);
				
			}else
			{
				//start to trail
				nextTrial();
				isTrainingMode = false;
				trainTrial = 0;
			}
			
		}else  //in the middle 
		{
			//middle, just one round 
			if(trainTrial <=  (1 * levels - 1))
			{
				//continue train
				target = trainSequence.get(trainTrial);
				trainTrial ++; 
				
				if(rectOverIndex > -1)
				{
					mouseTriggered.set(rectOverIndex, 0);
					rectOverIndex = -1;
				}
				
				rectOverIndex = target - 1;
				mouseTriggered.set(rectOverIndex, 1);
				
				renderNext(target);
			}else
			{
				//start to trail
				nextTrial();
				isTrainingMode = false;
				trainTrial = 0;
			}
		}
		
	}
	
	
	public void nextTrial()
	{
		if(trial <= (totalTrials - 1))
		{
			target = trialSequence.get(trial);
			//println("target: " + target);
			trial++;
			
			if(rectOverIndex > -1)
			{
				mouseTriggered.set(rectOverIndex, 0);
				rectOverIndex = -1;
			}
			
			//render the target
			renderNext(target);
			
		}else
		{
			//block finish
			blockDone = true;
		}
	}
	
	
	public void renderNext(int targetIndex)
	{
		rendering = 1;
		
		
		String msg = "r,";
		msg += "" + rendering + ",";
		msg += "\n";
		client.sendMessage(msg);
		
		controller.addPressure(targetIndex);
		
		promp = "rendering...";
		
		//thread("scheduleTaskReady");
	}
	
	public void releaseRender()
	{
		controller.releasePressure();
	}
	
	public void scheduleTaskReady()
	{
		//delay(2000);
		rendering = 2;
		
		
		String msg = "r,";
		msg += "" + rendering + ",";
		msg += "\n";
		client.sendMessage(msg);
		
		if(isTrainingMode)
		{
			promp = "press SPACE to release ...";
		}else
		{
			waitingForAnswer = true;
			trialStartTime = System.currentTimeMillis();
			promp = "waiting for your answer ...";
		}
		
	}
	
//	public void scheduleRelease()
//	{
//		delay(5000);  //how long it lasts before releasing
//		releaseRender();
//		rendering = 1;
//		delay(2000);
//		rendering = 0;
//		mouseTriggered.set(rectOverIndex, 0);
//		rectOverIndex = -1;
//		
//	}
	
	public void releaseWithSpace()
	{
		promp = "releasing...";
		releaseRender();
		rendering = 1;
		
		//send the render status to server
		String msg = "r,";
		msg += "" + rendering + ",";
		msg += "\n";
		client.sendMessage(msg);
	
		delay(2000);
		rendering = 0;
		
		msg = "r,";
		msg += "" + rendering + ",";
		msg += "\n";
		client.sendMessage(msg);
		
		if(rectOverIndex > -1)
		{
			mouseTriggered.set(rectOverIndex, 0);
			rectOverIndex = -1;
			
			msg = "m,";
			msg += "" + (isTrainingMode == true ? "1" : "0") + ",";
			msg += "" + (workingInProgress == true ? "1" : "0") + ",";
			msg += "" + trial + ",";
			msg += "" + target + ",";
			msg += "" + rectOverIndex + ",";
			msg += "\n";
			client.sendMessage(msg);
			
		}
		
		
		if(isTrainingMode)
		{
			
			if(trial == 0 && trainTrial ==  (2 * levels))
			{
				promp = "press space to enter test!";
			}else if(trial > 0 && trainTrial ==  (1 * levels))
			{	
				promp = "press space to continue test!";
			}else
			{
				promp = "Train mode, press SPACE to next";
			}
			
		}else
		{
			promp = "Trial mode, Press SPACE to next";
		}
		
		
		
		
		
	}
	
	public void releaseWithAccident()
	{
		promp = "releasing...";
		releaseRender();
		rendering = 1;
		delay(2000);
		rendering = 0;
		if(rectOverIndex >= 0)
		{
			mouseTriggered.set(rectOverIndex, 0);
			rectOverIndex = -1;
		}
	
		if(isTrainingMode)
		{
			promp = "Train mode, press SPACE to next";
		}else
		{
			trial--;
			promp = "Press SPACE to replay the next trial";
		}
	}
	
	
	public static void main(String[] args){
		
		PApplet.main("com.teng.test.PressureTest");
		
	}
	
}


class PressureSerialListener implements SerialPortEventListener
{
	public int index;
	PressureTest instance;
	
	
	public PressureSerialListener(int _index, PressureTest _instance)
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

				            	instance.pressurePlot.addValue(readValue);
				            	instance.client.sendMessage("t" + "," + readValue + "\n");
				            	
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
