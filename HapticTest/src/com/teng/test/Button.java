package com.teng.test;

import processing.core.*;


public class Button {

	PApplet app;
	
	float centerX = 0;
	float centerY = 0;
	
	float baseX = 0;
	float baseY = 0;
	float baseWidth = 100;
	float baseHeight = 50;
	
	float topX = 0;
	float topY = 0;
	float topWidth = 50;
	float topHeight = 20;
	
	float pressDistance = 0;
	
	public Button(PApplet ap, int cx, int cy)
	{
		app = ap;
		centerX = cx;
		centerY = cy;
		
		baseX = centerX - baseWidth / 2;
		baseY = centerY;
		topX = centerX - topWidth / 2;
		topY = centerY - topHeight;
		
				
		HapticTest.getInstance().switchTask(0, 1);
		HapticTest.getInstance().mouseTriggered[0] = 1;
		
	}
	
	
	public void draw()
	{
		app.fill(255, 128, 0);
		app.noStroke();
		app.rect(baseX, baseY, baseWidth, baseHeight);		

		app.fill(255, 178, 102);
		app.rect(topX, topY, topWidth, topHeight);
	}
	
	public void detectTouch(float lx, float ly, float plx, float ply)
	{
		if( (plx * ply != 0) && (topY <= centerY))
		{
			if(lx > topX && lx < (topX + topWidth))
			{
				if(ly > topY && ply <= topY)
				{
					pressDistance += (ly - topY);
					topY = ly;					
					
					//app.println("press distance: "+ pressDistance);
					mapTouch2Pressure(pressDistance);
				}
			}
		}
	}
	
	public void mapTouch2Pressure(float pressure)
	{
		
	}
	
}
