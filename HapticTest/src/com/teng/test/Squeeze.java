package com.teng.test;

import java.io.OutputStream;

import processing.core.*;

public class Squeeze {
	
	PApplet app;
	HapticTest instance;
	Vibration vibration;
	
	public Squeeze(HapticTest _instance)
	{
		instance = _instance;
		vibration = new Vibration(20);
	}
	
	public void sendNotification()
	{

		//add water 2 seconds
		new AddWater(1000).start();
	}
	
	public void detectSqueeze()
	{
		
	}
	
	
	public void startVibration()
	{
		vibration = new Vibration(50);
		vibration.start();
		
	}
	
	
	public void startVibration(int frequency)
	{
		int duration = (int)(500 / frequency);
		vibration = new Vibration(duration);
		vibration.start();
		
	}
	
	public void stopVirbation()
	{
		vibration.stopWorking();
	}
	
	public void setVibration(float frequency)
	{
		if(vibration != null)
		{
			int duration = (int)(500 / frequency);
			vibration.setDuration(duration); 
		}
	}
	
	class AddWater extends Thread{
		
		int duration;
		
		public AddWater(int _duration)
		{
			duration = _duration;
		}
		
		public void run()
		{
			
			try {
				instance.serialOutput_One.write('j');
			} catch (Exception ex) {
				return;
			}
			
			instance.mouseTriggered[5] = 0;
			
			instance.delay(200);
			
			try {
				instance.serialOutput_One.write('w');
			} catch (Exception ex) {
				return;
			}
			
			instance.pumpOneSpeed = 255;
			
			instance.delay(duration);
			
			try {
				instance.serialOutput_One.write('t');
			} catch (Exception ex) {
				return;
			}
			
			instance.pumpOneSpeed = 0;
			
		}
	}
	
	
	
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
				try {
					instance.serialOutput_One.write('j');
				} catch (Exception ex) {
					return;
				}
				instance.mouseTriggered[5] = 0;
				
				instance.delay(duration);
				
				try {
					instance.serialOutput_One.write('h');
				} catch (Exception ex) {
					return;
				}
				instance.mouseTriggered[5] = 1;
				
				instance.delay(duration);
			}
		}
		
		
	}
}


