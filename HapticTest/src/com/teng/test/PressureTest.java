package com.teng.test;

import java.util.ArrayList;
import java.util.Random;

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
	
	boolean isReady = false;
	
	//****************************//
	int sensation = 1;   
	//****************************//
	int userId = 1;
	//****************************//
	int block = 3;    // 1, 2, 3
	
	
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
	boolean isReferencing = true;
	boolean blockDone = false;
	
	String promp = "";
	long responseTime = 0;
	long startTime = 0;
	
	public static DataStorage dataStorage;
	
	
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
		
//		for(int itr = 0; itr < trialSequence.size(); itr++)
//		{
//			print(trialSequence.get(itr) + ",");
//		}
//		println();
		
		
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
			promp = "Train mode, press SPACE to start";
		}
		
		dataStorage = DataStorage.getInstance();
		dataStorage.userId = userId;
		dataStorage.sensation = "pressure";
		dataStorage.levels = levels;
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
		if(isReady)
		{
			fill(0, 200, 0);
		}else
		{
			fill(200, 0, 0);
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
		
	}
	
	
	private void update(int x, int y)
	{
		for(int itrr = 0; itrr < levels; itrr++)
		{
			if(overRect(rectXs.get(itrr), rectY, rectWidth, rectHeight))
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
		if(rectOverIndex >= 0)
		{
			thread("makeChoice");
		}
		
	}
	
	public void makeChoice()
	{
		mouseTriggered.set(rectOverIndex, 1);
		answer = rectOverIndex + 1;
		
		//record/update the duration
		//responseTime = System.currentTimeMillis() - startTime;
		
		promp = "Press SPACE to next";
		waitingForAnswer = false;
	}
	
	
	public void keyPressed() {
		if (key == 'q') {
			dataStorage.save();
			exit();
		}else if(key == ' ')
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
						
						//go to next
						nextTrial();
					}	
				}
			}
			
		}else
		{
			String inpuText = "" + key;
			
			if(waitingForAnswer || rectOverIndex >= 0)
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
	
	public void nextTrial()
	{
		
		if(trial <= (totalTrials - 1))
		{
			target = trialSequence.get(trial);
			println("target: " + target);
			trial++;
			
			waitingForAnswer = true;
			if(rectOverIndex > -1)
			{
				mouseTriggered.set(rectOverIndex, 0);
				rectOverIndex = -1;
			}
			
			//render the target
			
			
			
			promp = "waiting for your answer ...";
			
		}else
		{
			//block finish
			blockDone = true;
		}
	}
	
	
	public static void main(String[] args){
		
		PApplet.main("com.teng.test.PressureTest");
		
	}
	
}
