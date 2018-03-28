package com.teng.test;

import processing.core.*;

public class Slider {
	
	PApplet app;
	TemperatureTest test;
	
	
	int swidth = 1000; 
	int sheight = 50;    // width and height of bar
	float xpos, ypos;       // x and y position of bar
	float spos, newspos;    // x position of slider
	float sposMin, sposMax; // max and min values of slider
	int loose;              // how loose/heavy
	boolean over;           // is the mouse over the slider?
	boolean locked;
	float ratio;

	float realValueMin;
	float realValueMax;
	float realValue;
	
	boolean prevLocked = false;
	int index = 0;
	
	public Slider(PApplet ap, int cx, int cy, float min, float max)
	{
		app = ap;
		
		xpos = cx;
		ypos = cy;
		
		int widthtoheight = swidth - sheight;
	    ratio = (float)swidth / (float)widthtoheight;
	    spos = 0; //xpos + swidth/2 - sheight/2;
	    newspos = spos;
	    sposMin = xpos;
	    sposMax = xpos + swidth - sheight;
	    loose = 1;
	    
	    realValueMin = min;
	    realValueMax = max;
	    
	}
	
	
	public Slider(TemperatureTest ap, int cx, int cy, float min, float max, int _index)
	{
		test = ap;
		index = _index;
		xpos = cx;
		ypos = cy;
		
		int widthtoheight = swidth - sheight;
	    ratio = (float)swidth / (float)widthtoheight;
	    spos = xpos + swidth/2 - sheight/2;
	    newspos = spos;
	    sposMin = xpos;
	    sposMax = xpos + swidth - sheight;
	    loose = 1;
	    
	    realValueMin = min;
	    realValueMax = max;
	    
	}
	
	
	void update(int mouseX, int mouseY) {
	    if (overEvent(mouseX, mouseY)) {
	      over = true;
	    } else {
	      over = false;
	    }
	    
	    if (test.mousePressed && over) {
	      locked = true;
	    }
	    if (!test.mousePressed) {
	      locked = false;
	      
	      if(prevLocked == true)
	      {
	    	  //update
	    	  if(index == 1)
	    	  {
	    		  test.controller.updateP(getRealValue());
	    	  }
	    	  else if(index == 2)
	    	  {
	    		  test.controller.updateI(getRealValue());
	    	  }
	    	  else if(index == 3)
	    	  {
	    		  test.controller.updateD(getRealValue());
	    	  }
	      }
	    }
	    
	    
	    
	    
	    prevLocked  = locked;
	    
	    
	    if (locked) {
	      newspos = constrain(mouseX-sheight/2, sposMin, sposMax);
	    }
	    
	    
	    
	    
	    if (Math.abs(newspos - spos) > 1) {
	      spos = spos + (newspos-spos)/loose;
	    }
	}
	
	
	float constrain(float val, float minv, float maxv) {
	    return Math.min(Math.max(val, minv), maxv);
	}

	boolean overEvent(int mouseX, int mouseY) {
	    if (mouseX > xpos && mouseX < xpos+swidth &&
	       mouseY > ypos && mouseY < ypos+sheight) {
	      return true;
	    } else {
	      return false;
	    }
	}

	void draw() {
	    test.noStroke();
	    test.fill(204);
	    test.rect(xpos, ypos, swidth, sheight);
	    if (over || locked) {
	      test.fill(0, 0, 0);
	    } else {
	      test.fill(102, 102, 102);
	    }
	    test.rect(spos, ypos, sheight, sheight);
	    
	    test.fill(120);
	    String sposValue = "" + getRealValue();
	    test.textSize(24);
	    test.text(sposValue, spos, ypos);
	}

	float getPos() {
	    // Convert spos to be values between
	    // 0 and the total width of the scrollbar
	    return spos * ratio;
	}
	
	public float getRealValue()
	{
		float zoom = (float)swidth / (float)(realValueMax - realValueMin);
		float result = getPos() / zoom;
		return Float.parseFloat(String.format("%.3f", result));
	}
	
	
	
	
	
}
