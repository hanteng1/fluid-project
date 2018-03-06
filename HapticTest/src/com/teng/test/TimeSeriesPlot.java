package com.teng.test;

import java.util.ArrayList;

import processing.core.*;

public class TimeSeriesPlot {
	
	PApplet app;
	
	float centerX = 0;
	float centerY = 0;
	
	float plotWidth = 0;
	float plotHeight = 0;
	int plotPointSize = 0;
	float plotSegWidth =0;
	ArrayList<Float> plotData;
	ArrayList<Float> plotDataFiltered;
	
	float yMax = 0;
	float yMin = 0;
	float yHeight = 0;
	
	float average = 0;
	
	float shampen = 2;
	float lastValue = 0;
	boolean  firstFrame = true;
	
	public TimeSeriesPlot(PApplet ap, float cx, float cy, float cw, float ch, int sz)
	{
		app = ap;
		centerX = cx;
		centerY = cy;
		plotWidth = cw;
		plotHeight = ch;
		plotPointSize = sz;
		plotSegWidth = plotWidth / plotPointSize;
		
			
		yMax = 1150.0f;
		yMin = 900.0f;
		yHeight = yMax - yMin;
		
		plotData = new ArrayList<Float>();
		for(int itrd = 0; itrd < plotPointSize; itrd++)
		{
			plotData.add(plotHeight / 2 + centerY );
		}
		
		plotDataFiltered = new ArrayList<Float>();
		for(int itrd = 0; itrd < plotPointSize; itrd++)
		{
			plotDataFiltered.add(plotHeight / 2 + centerY );
		}
	}
	
	public void addValue(float value)
	{
		
		//if(value < 700 || value > 1500)
		//{
		//	return;
		//}
		
		
		if(!firstFrame)
		{
			if(Math.abs(value - lastValue) > shampen)
			{
				value = lastValue;
			}
		}
		
		
		plotData.remove(0);
		plotData.add(value);
			
		average = average + 0.05f*(value-average);
		plotDataFiltered.remove(0);
		plotDataFiltered.add(average);
		
		lastValue = value;
		firstFrame = false;
		
	}
	
	public float getLastValue()
	{
		if(plotDataFiltered.size() > 2)
		{
			return plotDataFiltered.get(plotDataFiltered.size() - 1);
		}
		return 0;
	}
	
	
	public void setMinMax(float min, float max)
	{
		yMin = min;
		yMax = max;
		yHeight = max - min;
	}
	
	public void setShampen(float _shampen)
	{
		shampen = _shampen;
	}
	
	public void draw()
	{
		
		app.fill(150, 150, 150);
		app.noStroke();
		app.rect(centerX - plotWidth / 2, centerY, plotWidth, plotHeight);
		
		app.stroke(200, 100, 100);
		app.noFill();
		app.strokeWeight(3);
		
		for(int itrd = 0; itrd < plotData.size() - 1 ; itrd++)
		{
			float valueOne = plotData.get(itrd);
			float yOnAxisOne = centerY + plotHeight - (valueOne - yMin) * plotHeight / yHeight;
			float xOnAxisOne = plotSegWidth * itrd;
			
			float valueTwo = plotData.get(itrd + 1);
			float yOnAxisTwo = centerY + plotHeight - (valueTwo - yMin) * plotHeight / yHeight;
			float xOnAxisTwo = plotSegWidth * (itrd + 1);
			
			app.line(xOnAxisOne, yOnAxisOne, xOnAxisTwo, yOnAxisTwo);
		}
		
		app.stroke(100, 100, 200);
		app.noFill();
		app.strokeWeight(3);
		
		for(int itrd = 0; itrd < plotData.size() - 1 ; itrd++)
		{
			float valueOne = plotDataFiltered.get(itrd);
			float yOnAxisOne = centerY + plotHeight - (valueOne - yMin) * plotHeight / yHeight;
			float xOnAxisOne = plotSegWidth * itrd;
			
			float valueTwo = plotDataFiltered.get(itrd + 1);
			float yOnAxisTwo = centerY + plotHeight - (valueTwo - yMin) * plotHeight / yHeight;
			float xOnAxisTwo = plotSegWidth * (itrd + 1);
			
			app.line(xOnAxisOne, yOnAxisOne, xOnAxisTwo, yOnAxisTwo);
		}
		
		app.noStroke();
	}
		

}
