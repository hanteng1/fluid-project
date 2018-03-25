package com.teng.test;

import java.util.ArrayList;

import com.teng.test.Squeeze.AddWater;

public class HapticController {
	
	PressureTest pressureTest;
	public int pressureLevel = 0;
	public float pressureLow = 200.0f;
	public float pressureHigh = 1000.0f;
	public int pressureRange;
	public ArrayList<Integer> pressureLevels;
	
	VibrationTest vibrationTest;
	TemperatureTest temperatureTest;
	
	
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
			while(working)
			{
//				try {
//					vibrationTest.serialOutput_One.write('j');
//				} catch (Exception ex) {
//					return;
//				}
//				vibrationTest.delay(duration);
//				
//				try {
//					vibrationTest.serialOutput_One.write('h');
//				} catch (Exception ex) {
//					return;
//				}
//				vibrationTest.delay(duration);
			}
		}
		
		
	}
}
