package com.teng.test;

import processing.core.*;

public class Slider {
	
	PApplet app;
	Monitor monitor;
	
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
	
	
	public Slider(Monitor ap, int cx, int cy, float min, float max, int _index)
	{
		monitor = ap;
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
	    
	    if (monitor.mousePressed && over) {
	      locked = true;
	    }
	    if (!monitor.mousePressed) {
	      locked = false;
	      
	      if(prevLocked == true)
	      {
	    	  //update
	    	  if(index == 1)
	    	  {
	    		  String msg = "pid,p,";
	    		  msg += "" + getRealValue() + ",";
	    		  msg += "\n";
	    		  monitor.server.sendMessage(msg);
	    		  
	    	  }
	    	  else if(index == 2)
	    	  {
	    		  String msg = "pid,i,";
	    		  msg += "" + getRealValue() + ",";
	    		  msg += "\n";
	    		  monitor.server.sendMessage(msg);
	    	  }
	    	  else if(index == 3)
	    	  {
	    		  String msg = "pid,d,";
	    		  msg += "" + getRealValue() + ",";
	    		  msg += "\n";
	    		  monitor.server.sendMessage(msg);
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
		monitor.noStroke();
		monitor.fill(204);
		monitor.rect(xpos, ypos, swidth, sheight);
	    if (over || locked) {
	    	monitor.fill(0, 0, 0);
	    } else {
	    	monitor.fill(102, 102, 102);
	    }
	    monitor.rect(spos, ypos, sheight, sheight);
	    
	    monitor.fill(120);
	    String sposValue = "" + getRealValue();
	    monitor.textSize(24);
	    monitor.text(sposValue, spos, ypos);
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
		return Float.parseFloat(String.format("%.4f", result));
	}
	
	
	
	
	
}
