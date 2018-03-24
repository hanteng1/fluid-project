package com.teng.test;

import java.util.ArrayList;

import com.teng.test.Squeeze.AddWater;

public class HapticController {
	
	PressureTest pressureTest;
	public int pressureLevel = 0;
	public int pressureLow = 300;
	public int pressureHigh = 1000;
	public int pressureRange;
	public ArrayList<Integer> pressureLevels;
	
	VibrationTest vibrationTest;
	TemperatureTest temperatureTest;
	
	
	public HapticController(PressureTest test)
	{
		pressureTest = test;
		pressureRange = pressureHigh - pressureLow;
		pressureLevels = new ArrayList<Integer>();
		
		int levels = pressureTest.levels;
		int unitPressure = (int)(pressureRange / (levels - 1));
		
		for(int itr = 0; itr < levels; itr++)
		{
			pressureLevels.add(pressureLow + itr * unitPressure);
			
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
			new AddWater(level * 100).start();
			pressureLevel = level;
			
			//pressureTest.println("level: " + pressureLevel);
		}
		
	}
	
	public void releasePressure()
	{
		pressureTest.valveOpen();
		pressureLevel = 0;
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
			pressureTest.rendering = 1;
			
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
			
			pressureTest.rendering = 2;
			
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
