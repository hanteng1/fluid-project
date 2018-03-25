package com.teng.test;

import java.util.ArrayList;

import com.teng.test.Squeeze.AddWater;
import com.teng.test.Squeeze.Vibration;

public class HapticController {
	
	PressureTest pressureTest;
	float pressureLow = 200.0f;
	float pressureHigh = 1000.0f;
	ArrayList<Integer> pressureLevels;
	
	VibrationTest vibrationTest;
	float vibrationLow = 1.0f;
	float vibrationHigh = 40.0f;
	ArrayList<Integer> vibrationLevels;
	Vibration vibration;
	
	TemperatureTest temperatureTest;
	float temperatureLow = 15.0f;
	float temperatureHigh = 35.0f;
	ArrayList<Integer> temperatureLevels;
	
	public float levelFactor = 0;
	
	public HapticController(PressureTest test)
	{
		pressureTest = test;
		pressureLevels = new ArrayList<Integer>();
		
		int levels = pressureTest.levels;
		levelFactor = (float) Math.pow(pressureHigh / pressureLow, 1.0/ (levels - 1));
		//pressureTest.println(levelFactor);
		for(int itr = 0; itr < levels; itr++)
		{
			pressureLevels.add((int) (pressureLow * Math.pow(levelFactor, itr)));
			//pressureTest.println("" + itr + " : " + pressureLevels.get(itr));
		}
	}
	
	public HapticController(VibrationTest test)
	{
		vibrationTest = test;
		vibrationLevels = new ArrayList<Integer>();
		
		int levels = vibrationTest.levels;
		levelFactor = (float) Math.pow(vibrationHigh / vibrationLow, 1.0/ (levels - 1));
		vibrationTest.println(levelFactor);
		for(int itr = 0; itr < levels; itr++)
		{
			vibrationLevels.add((int) (vibrationLow * Math.pow(levelFactor, itr)));
			
			if(itr > 0)
			{
				if(vibrationLevels.get(itr) == vibrationLevels.get(itr - 1))
				{
					vibrationLevels.set(itr, vibrationLevels.get(itr) + 1);
				}
			}
			
			//vibrationTest.println("" + itr + " : " + vibrationLevels.get(itr));
		}
		
		
	}
	
	public HapticController(TemperatureTest test)
	{
		temperatureTest = test;
	}
	
	public void addPressure(int level)
	{
		if(level > 0 && level < 10)
		{
			new AddWater(pressureLevels.get(level - 1)).start();
			
			//pressureTest.println("render " + level + " : " +  pressureLevels.get(level - 1));
		}
		
	}
	
	public void releasePressure()
	{
		pressureTest.valveOpen();
		//pressureTest.println("level: " + pressureLevel);
	}
	
	
	public void startVibration(int level)
	{
		if(level > 0 && level < 10)
		{
			int duration = (int)(500 / vibrationLevels.get(level - 1));
			vibration = new Vibration(duration);
			vibration.start();
		}
	}
	
	public void stopVibration()
	{
		vibration.stopWorking();
	}
	
	//used only for pressureTest
	class AddWater extends Thread{
		
		int duration;
		
		public AddWater(int _duration)
		{
			duration = _duration;
		}
		
		public void run()
		{	
			//close the value
			try {
				pressureTest.serialOutput_One.write('j');
			} catch (Exception ex) {
				return;
			}
			pressureTest.delay(200);
			
			//run the water
			try {
				pressureTest.serialOutput_One.write('w');
			} catch (Exception ex) {
				return;
			}
			pressureTest.delay(duration);
			
			try {
				pressureTest.serialOutput_One.write('t');
			} catch (Exception ex) {
				return;
			}
			
		}
	}
	
	
	//used only for vibration test
	class Vibration extends Thread
	{
		int duration; 
		boolean working = true;
		
		public Vibration(int _duration)
		{
			duration = _duration;
		}
		
		public void stopWorking()
		{
			working = false;
		}
		
		public void setDuration(int _duration)
		{
			duration = _duration;
		}
		
		public void run()
		{
			
			vibrationTest.valveOpen();
			vibrationTest.delay(200);
			vibrationTest.runWater();
			vibrationTest.delay(2000);
			
			vibrationTest.scheduleTaskReady();
			
			while(working)
			{
				vibrationTest.valveClose();
				vibrationTest.delay(duration);
				
				vibrationTest.valveOpen();
				vibrationTest.delay(duration);
			}
			
			vibrationTest.stopWater();
		}
		
		
	}
}
