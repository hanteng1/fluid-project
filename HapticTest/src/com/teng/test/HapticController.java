package com.teng.test;

import java.util.ArrayList;

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
	float temperatureLow = 18.0f;
	float temperatureHigh = 40.0f;
	ArrayList<Integer> temperatureLevels;
	double pidOutput  = 0;
	double prevPidOutput = 0;
	MiniPID miniPID; 
	AdjustTemperature adjustTemperature;
	
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
		//vibrationTest.println(levelFactor);
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
		temperatureLevels = new ArrayList<Integer>();
		
		int levels = temperatureTest.levels;
//		levelFactor = (float) Math.pow(temperatureHigh / temperatureLow, 1.0/ (levels - 1));
//		temperatureTest.println(levelFactor);
//		for(int itr = 0; itr < levels; itr++)
//		{
//			temperatureLevels.add((int) (temperatureLow * Math.pow(levelFactor, itr)));
//			
//			if(itr > 0)
//			{
//				if(temperatureLevels.get(itr) == temperatureLevels.get(itr - 1))
//				{
//					temperatureLevels.set(itr, temperatureLevels.get(itr) + 1);
//				}
//			}
//			
//			temperatureTest.println("" + itr + " : " + temperatureLevels.get(itr));
//		}
		
		float segment = (temperatureHigh - temperatureLow) / (levels - 1);
		for(int itr = 0; itr < levels; itr++)
		{
			temperatureLevels.add((int)(temperatureLow + segment* itr));
			temperatureTest.println("" + itr + " : " + temperatureLevels.get(itr));
		}
		
		miniPID = new MiniPID(0.1, 0, 0.2);  // no integral part
		miniPID.setOutputLimits(10);
		//miniPID.setMaxIOutput(2);
		//miniPID.setOutputRampRate(3);
		//miniPID.setOutputFilter(.3);
		miniPID.setSetpointRange(40);
		
	}
	
	
	
	//////////////////////////////////////////////////////////
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
	
	//////////////////////////////////////////////////////////////
	
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
	
	///////////////////////////////////////////////////////////////
	
	public void setTemperature(int level)
	{
		if(level > 0 && level < 10)
		{
			adjustTemperature = new AdjustTemperature(temperatureLevels.get(level - 1));
			adjustTemperature.start();
		}
	}
	
	public void stopTemperature()
	{
		adjustTemperature.stopWorking();
	}
	
	//////////////////////////////////////////////////////////////////
	
	
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
	
	//used for running the temperature
	class AdjustTemperature extends Thread
	{
		float target;
		float actual;
		boolean working = true;
		
		public AdjustTemperature(float _target)
		{
			target = _target;
		}
		
		public void stopWorking()
		{
			working = false;
		}
		
		public void run()
		{
			
			while(working)
			{
				actual = temperatureTest.actualTemperature;
				float change = target - actual;
				
				if(Math.abs(change) <= 1 && temperatureTest.rendering == 1)
				{
					temperatureTest.scheduleTaskReady();
				}
				
				if(Math.abs(change) >= 0.1)
				{
					//the output is desired change in the next step
					pidOutput = miniPID.getOutput(actual, target);
					
					if(pidOutput >= 0)
					{
						
						if(pidOutput >= 10.0f)
						{
							pidOutput = 9.99f;
						}
						String valueTwoDecial = String.format("%.2f", Math.abs(pidOutput));
						String valuetosend = valueTwoDecial + "z";
						float temp = Float.parseFloat(valueTwoDecial);
						
						try {
							temperatureTest.serialOutput_One.write(valuetosend.getBytes());  //full speed hot
						} catch (Exception ex) {
							return;
						}
					}else
					{
						if(pidOutput <= -10.0f)
						{
							pidOutput = -9.99f;
						}
						String valueTwoDecial = String.format("%.2f", Math.abs(pidOutput));
						String valuetosend = valueTwoDecial + "x";
						float temp = Float.parseFloat(valueTwoDecial) * (-1);

						try {
							temperatureTest.serialOutput_One.write(valuetosend.getBytes());  //full speed hot
						} catch (Exception ex) {
							return;
						}
					}
					
					prevPidOutput = pidOutput;
					
				}
				
				temperatureTest.delay(100);
			}
			
			//release
			temperatureTest.stopWater();
			
			
		}
	}
}
