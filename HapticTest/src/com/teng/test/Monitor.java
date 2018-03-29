package com.teng.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import processing.core.PApplet;


//used as a server?
//takes charge of trial sequence and data record

public class Monitor extends PApplet{
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
	int block = 1;    // 1, 2, 3
	
	
	int levels = 0;   //3, 5, 7 (or 9)
	int trial = 0;
	int totalTrials = 0;
	int repetition = 5;
	ArrayList<Integer> trialSequence;
	int target = 0;
	int answer = 0;
	boolean waitingForAnswer = false;
	
	boolean isTrainingMode = true;
	
	//always play a reference first
	boolean isReferencing = true; //not used for now
	boolean blockDone = false;
	
	String promp = "";
	long responseTime = 0;
	long trialStartTime = 0;
	
	float actualTemperature;
	float targetTemperature;
	boolean targetSet = false;
	TimeSeriesPlot seriesPlot;
	
	boolean workingInProgress = false;
	public int rendering = 0;  //0 - nothing, 1 - render, 2 - ready
	
	public static DataStorage dataStorage;
	private Server server;
	
	
	public void settings()
	{
		windowWidth = 1000;
		windowHeight = 750;
		size(windowWidth, windowHeight);
	}
	
	public void setup()
	{
		fill(120, 50, 240);
		rectColor = color(211, 211, 211);
		rectHighlight = color(105, 105, 105);
		
		levels = block * 2 + 1;  // need pilot
		totalTrials = levels * repetition;
		trialSequence = new ArrayList<Integer>();
		ArrayList<Integer> oldSequence = new ArrayList<Integer>();
		
		for(int itr = 1; itr < levels + 1; itr++)
		{
			for(int i = 0; i < repetition; i++)
			{
				oldSequence.add(itr);
			}
		}
		
		trialSequence = randomize(oldSequence);
		
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
			promp = "train mode";
		}
		
		dataStorage = DataStorage.getInstance();
		dataStorage.userId = userId;
		if(sensation == 1)
		{
			dataStorage.sensation = "pressure";
		}else if(sensation == 2)
		{
			dataStorage.sensation = "vibration";
		}else if(sensation == 3)
		{
			dataStorage.sensation = "temperature";
		}
		dataStorage.levels = levels;
		
		seriesPlot = new TimeSeriesPlot(this, windowWidth / 2, 500, windowWidth, 200, 1000, false, false, false);
		
		if(sensation == 1 || sensation == 2)
		{
			seriesPlot.setMinMax(900, 1200, true);
			seriesPlot.setShampen(1000);
		}else if(sensation == 3)
		{
			seriesPlot.setMinMax(15, 50, true);
			seriesPlot.setShampen(100);
		}
		
		
		try {
			server = new Server(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	
	}
	
	public String getSequenceString()
	{
		String sequenceString = "s,";
		sequenceString += ("" + levels + ",");
		sequenceString += ("" + totalTrials + ",");
		sequenceString += ("" + trial + ",");
		for(int itr = 0; itr < trialSequence.size(); itr++)
		{
			sequenceString += ("" + trialSequence.get(itr) + ",");
		}
		sequenceString += "\n";
		return sequenceString;
	}
	
	public void updateRenderStatus(int render)
	{
		rendering = render;
	}
	
	public void draw()
	{
		background(225, 225, 225);
		
		//block
		textSize(48);
		fill(120);
		text("Target " + target, 100, 100);
		
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
		
		
		//timeSeriesPlot.draw();
		seriesPlot.draw();
		
		
		//show target and actual temperature
		{
			String textShown = "" + actualTemperature + " C";
			text(textShown, windowWidth / 2 - textWidth(textShown) /2 , 450); 
		}
		
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
	
	
	public void mousePressed()
	{
		seriesPlot.mousePress(mouseX, mouseY);
	}
	
	public void mouseReleased()
	{
		seriesPlot.mouseRelease(mouseX, mouseY);
	}
	
	public void keyPressed() {
		if (key == 'q') {
			dataStorage.save();
			server.onDestroy();
			exit();
		}else if(key == 's')
		{
			//stop action 
			server.sendMessage("sos,\n");
		}
	}
	
	
	public void releaseWithAccident()
	{
		
	}
	
	
	public static void main(String[] args){
		
		PApplet.main("com.teng.test.Monitor");
	}

}

