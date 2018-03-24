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
	ArrayList<Float> plotDataTwo;
	
	boolean drawFilter = false;
	
	float yMax = 0;
	float yMin = 0;
	float yHeight = 0;
	
	float average = 0;
	float shampen = 2;
	float lastValue = 0;
	float lastValueTwo = 0;
	float valueTwoOffset = 62.9f;
	float lastFilteredValue = 0;
	boolean  firstFrame = true;
	boolean firstFrameTwo = true;
	
	boolean getReady = false;
	int readyValue = 0;
	int releaseValue = 0;
	
	int  colorValue = 150;
	
	//fft to test the frequency
	public RealDoubleFFT mRealFFT;
	int fftBins;
	double scale;
	private final static float MEAN_MAX = 2000;   // Maximum signal value
	ArrayList<Float> fftData;
	int mainFrequncy = 0;
	
	//testing
	boolean isTesting = false;
	int logCount = 0;
	long timeStamp = 0;
	
	
	boolean isFilter;
	boolean isFFT;
	boolean isDataTwo;
	
	public TimeSeriesPlot(PApplet ap, float cx, float cy, float cw, float ch, int sz,
			boolean applyFilter, boolean applyFFT, boolean applyDataTwo)
	{
		app = ap;
		centerX = cx;
		centerY = cy;
		plotWidth = cw;
		plotHeight = ch;
		plotPointSize = sz;
		isFilter = applyFilter;
		isFFT = applyFFT;
		isDataTwo = applyDataTwo;
		plotSegWidth = plotWidth / plotPointSize;
		
			
		yMax = 1150.0f;
		yMin = 900.0f;
		yHeight = yMax - yMin;
		
		plotData = new ArrayList<Float>();
		for(int itrd = 0; itrd < plotPointSize; itrd++)
		{
			plotData.add(plotHeight / 2 + centerY );
		}
		
		
		if(isFilter)
		{
			plotDataFiltered = new ArrayList<Float>();
			for(int itrd = 0; itrd < plotPointSize; itrd++)
			{
				plotDataFiltered.add(plotHeight / 2 + centerY );
			}
		}
		
		if(isFFT)
		{
			fftBins = 64;
			mRealFFT = new RealDoubleFFT(fftBins);
			scale =  MEAN_MAX * MEAN_MAX * fftBins * fftBins / 2d;
			
			fftData = new ArrayList<Float>();
		}
		
		if(isDataTwo)
		{
			plotDataTwo = new ArrayList<Float>();
			for(int itrd = 0; itrd < plotPointSize; itrd++)
			{
				plotDataTwo.add(plotHeight / 2 + centerY );
			}
		}
		
	}
	
	public void addValue(float value)
	{
		if(firstFrame)
		{
			firstFrame = false;
		}else
		{
			if(Math.abs(value - lastValue) > shampen)
			{
				value = lastValue;
			}
		}
		
		plotData.remove(0);
		plotData.add(value);
		
		if(isTesting)
		{
			logCount++;
			
			if(timeStamp == 0)
			{
				timeStamp = System.currentTimeMillis();
			}else
			{
				long curTime = System.currentTimeMillis();
				if( curTime - timeStamp >= 1000)
				{
					app.println("" + logCount);
					logCount = 0;
					timeStamp = curTime;
				}
			}
			
			
//			fftData.add(value);
//			if(fftData.size() == fftBins)
//			{
//				//perform a fft
//				double[] fftResults = freq(fftData);
//				
//				//return a main frequency, 64hz sampling rate, 1hz / bin
//				mainFrequncy = getFrequency(fftResults);
//			
//				fftData.clear();
//			}
			
		}
		
		
		
		if(isFilter)
		{
			average = average + 0.05f*(value-average);
			plotDataFiltered.remove(0);
			plotDataFiltered.add(average);
			lastFilteredValue = average;
		}
		
		lastValue = value;
		
	}
	
	
	public void addValueTwo(float value)
	{
		float offettedValue = value + valueTwoOffset;
		if(firstFrameTwo)
		{
			firstFrameTwo = false;
		}else
		{
			if(Math.abs(offettedValue - lastValueTwo) > shampen)
			{
				offettedValue = lastValueTwo;
			}
		}
		
		plotDataTwo.remove(0);
		plotDataTwo.add(offettedValue);
		
		lastValueTwo = offettedValue;
	}
	
	public void setValueTwo()
	{
		//average over the last 100
		float avgOne = 0;
		float avgTwo = 0;
		for(int itr = 0; itr < 100; itr++)
		{
			avgOne += plotData.get(plotPointSize - 1 - itr);
			avgTwo += plotDataTwo.get(plotPointSize - 1 - itr);
		}
		
		avgOne = avgOne / 100.0f;
		avgTwo = avgTwo / 100.0f;
		
		valueTwoOffset = avgOne - avgTwo;
		valueTwoOffset = Float.parseFloat(String.format("%.1f", valueTwoOffset));
		
		app.println("valueoffset " + valueTwoOffset);
	}
	
	
	public float getLastValue()
	{
//		if(plotDataFiltered.size() > 2)
//		{
//			return plotDataFiltered.get(plotDataFiltered.size() - 1);
//		}
//		return 0;
		
		return lastValue;
	}
	
	public float getLastFilteredValue()
	{
		return lastFilteredValue;
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
	
	
	public boolean mouseEffect(int mousex, int mousey)
	{
		//centerX - plotWidth / 2, centerY, plotWidth, plotHeight);
		
		if((mousex >= (centerX - plotWidth / 2)) && (mousex < (centerX + plotWidth / 2)) &&
				(mousey >= centerY) && (mousey <= (centerY + plotHeight)) )
		{
			return true;
		}
			
		return false;
	}
	
	public void mousePress(int mousex, int mousey)
	{
		if(mouseEffect(mousex, mousey))
		{
			getReady = true;
			//yOnAxisOne = centerY + plotHeight - (valueOne - yMin) * plotHeight / yHeight
			readyValue = (int) ((centerY + plotHeight - mousey) / (plotHeight / yHeight) + yMin);
			
			colorValue = 120;
			
			//app.println("readyValue " + readyValue);
		}
	}
	
	public void mouseRelease(int mousex, int mousey)
	{
		if(getReady == true)
		{
			releaseValue = (int) ((centerY + plotHeight - mousey) / (plotHeight / yHeight) + yMin);
			//app.println("releaseValue " + releaseValue);
			if(Math.abs(releaseValue - readyValue) > 2)
			{
				//made an effective selection 
				if(releaseValue > readyValue)
				{
					setMinMax(readyValue, releaseValue);
				}else
				{
					setMinMax(releaseValue, readyValue);
				}
			}else
			{
				//set to default
				setMinMax(0, 1000);
			}
			
			readyValue = 0;
			releaseValue = 0;
			getReady = false;
			colorValue = 150;
			
			
		}
	}
	
	public double[] freq(ArrayList<Float> data)  //size of ac should equal to fftBins
	{
		double[] result = new double[fftBins/2];
		if(data.size() != fftBins)
		{
			return result;
		}
		
		double[] fftData = new double[fftBins];

		for(int itra = 0; itra < fftBins; itra++)
		{
			fftData[itra] = data.get(itra);
		}
		
		mRealFFT.ft(fftData);
		
		//convert to db
		convertToDb(fftData, scale);
		
		for(int itr = 0; itr < fftBins/2; itr++)
		{
			result[itr] = fftData[itr];
		}

		return result;
	}
	
	public int getFrequency(double[] data)
	{
		if(data.length != fftBins/2)
		{
			return -2;
		}
		
		int result = -1;
		double maxValue = 0;
		for(int itr = 1; itr < fftBins/2; itr++)
		{
			if(Math.abs(data[itr]) > maxValue)
			{
				result = itr;
				maxValue = Math.abs(data[itr]);
			}
		}
		
		return result;
	}
	
	public double[] convertToDb(double[] data, double maxSquared) {
	    data[0] = db2(data[0], 0.0, maxSquared);
	    int j = 1;
	    for (int i=1; i < data.length - 1; i+=2, j++) {
	      data[j] = db2(data[i], data[i+1], maxSquared);
	    }
	    data[j] = data[0];
	    return data;
	}
	
	private double db2(double r, double i, double maxSquared) {
	    return 5.0 * Math.log10((r * r + i * i) / maxSquared);
	}
	
	
	public void draw()
	{
		
		app.fill(colorValue);
		app.noStroke();
		app.rect(centerX - plotWidth / 2, centerY, plotWidth, plotHeight);
		
		//min and max values
		app.textSize(32);
		app.fill(200, 100, 100);
		app.text("" + yMin, 100, centerY + plotHeight );
		app.text("" + yMax, 100, centerY + 30);
		
		
		//show frequency
		if(isTesting)
		{
			//app.text("" + mainFrequncy + " Hz", 900, centerY + 30);
			
			
		}
		
		app.text("" + lastValue + "", 800, centerY + 30);
		
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
		
		
		if(isDataTwo)
		{
			app.stroke(100, 100, 200);
			app.fill(100, 100, 200);
			app.text("" + lastValueTwo + "", 800, centerY + 80);
			
			for(int itrd = 0; itrd < plotDataTwo.size() - 1 ; itrd++)
			{
				float valueOne = plotDataTwo.get(itrd);
				float yOnAxisOne = centerY + plotHeight - (valueOne - yMin) * plotHeight / yHeight;
				float xOnAxisOne = plotSegWidth * itrd;
				
				float valueTwo = plotDataTwo.get(itrd + 1);
				float yOnAxisTwo = centerY + plotHeight - (valueTwo - yMin) * plotHeight / yHeight;
				float xOnAxisTwo = plotSegWidth * (itrd + 1);
				
				app.line(xOnAxisOne, yOnAxisOne, xOnAxisTwo, yOnAxisTwo);
			}
			
		}
		
		
		if(drawFilter)
		{
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
			
		}
		
		app.noStroke();
	}
		

}
