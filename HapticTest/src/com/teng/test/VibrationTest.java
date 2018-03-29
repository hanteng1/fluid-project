package com.teng.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import processing.core.PApplet;

public class VibrationTest extends PApplet{
	int windowWidth, windowHeight;
	int rectColor, rectHighlight;
	
	ArrayList<Integer> rectXs;
	int rectY = 0;
	ArrayList<Integer> mouseTriggered;
	int  rectWidth, rectHeight;
	int rectOverIndex = -1;
	
	//****************************//
	int sensation = 2;   
	//****************************//
	int userId = 1;
	//****************************//
	int block = 2;    // 1, 2, 3
	
	
	int levels = 0;   //3, 5, 7 (or 9)
	int trial = 0;
	int totalTrials = 0;
	ArrayList<Integer> trialSequence;
	public boolean isTrialSequenceSet;
	int target = 0;
	int answer = 0;
	boolean waitingForAnswer = false;
	
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
	
	//to control pressure
	HapticController controller;
	
	boolean workingInProgress = false;
	public int rendering = 0;  //0 - nothing, 1 - render, 2 - ready
	
	
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
		
		ArrayList<Integer> oldSequence = new ArrayList<Integer>();
		
		
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
		
		if(isTrainingMode)
		{
			promp = "Train mode, press 123.. to try, or SPACE to start";
		}
		
		dataStorage = DataStorage.getInstance();
		dataStorage.userId = userId;
		dataStorage.sensation = "vibration";
		dataStorage.levels = levels;
		

		//ring
		configurePort_One("COM10");
		connectPort_One();
		delay(2000);
		//set to clockwise by default
		
		controller = new HapticController(this);
		
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
			serialOutput_One.write('y');  //slow speed
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
		delay(5000);
		stopWater();
		workingInProgress = false;
	}
	
	public void draw()
	{
		background(225, 225, 225);
		
		//block
		textSize(48);
		fill(120);
		text("Block " + block, 100, 100);
		
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
	
	public ArrayList<Integer> randomize(ArrayList<Integer> data)
	{
		ArrayList<Integer> newData = new ArrayList<Integer>();
		int size = data.size();
		Random rand = new Random();
		
		for(int i = 0; i < size; i++)
		{
			//pick a random number from sequence
			int pick = rand.nextInt(data.size());
			newData.add(data.get(pick));
			data.remove(pick);
		}
		
		return newData;
		
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
	
	public void makePractice()
	{
		//render a target
		mouseTriggered.set(rectOverIndex, 1);
		renderNext(target);
	}
	
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
			
			exit();
		}else if(key == ' ')
		{
			if(workingInProgress && trial > 0)
			{
				isTrainingMode = true;
				workingInProgress = false;
				promp = "Train mode, press 123... to try, or SPACE to start";
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
						isTrainingMode = false;
						//start
						nextTrial();
					}else
					{
						if(waitingForAnswer == false)
						{
							//record the data
							DataStorage.AddSample(trial, sensation, levels, target, answer, responseTime, answer == target ? 1 : 0);
							
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
			}
			
			
		}else
		{
			
			if(workingInProgress)
			{
				return;
			}else
			{
				String inpuText = "" + key;
				
				if(isTrainingMode)
				{
					if(rendering > 0)
					{
						return;
					}else
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
							rectOverIndex = inputValue - 1 ;
							target = inputValue;
							makePractice();
						}
					}
					
					
				}else
				{
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
						}
					}
				}
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
		controller.startVibration(targetIndex);
		promp = "rendering...";
	}
	
	public void releaseRender()
	{
		controller.stopVibration();
	}
	
	public void scheduleTaskReady()
	{
		rendering = 2;
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
	
	public void releaseWithSpace()
	{
		promp = "releasing...";
		releaseRender();
		rendering = 1;
		delay(2000);
		rendering = 0;
		mouseTriggered.set(rectOverIndex, 0);
		rectOverIndex = -1;
		
		if(isTrainingMode)
		{
			promp = "Train mode, press 123... to try, or SPACE to start";
		}else
		{
			promp = "Press SPACE to next";
		}
	}
	
	
	public static void main(String[] args){
		
		PApplet.main("com.teng.test.VibrationTest");
	}
	
}
