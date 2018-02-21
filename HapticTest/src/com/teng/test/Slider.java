package com.teng.test;

import processing.core.*;

public class Slider {
	
	PApplet app;
	
	int swidth = 200; 
	int sheight = 50;    // width and height of bar
	float xpos, ypos;       // x and y position of bar
	float spos, newspos;    // x position of slider
	float sposMin, sposMax; // max and min values of slider
	int loose;              // how loose/heavy
	boolean over;           // is the mouse over the slider?
	boolean locked;
	float ratio;

	
	public Slider(PApplet ap, int cx, int cy)
	{
		app = ap;
		xpos = cx;
		ypos = cy;
		
		int widthtoheight = swidth - sheight;
	    ratio = (float)swidth / (float)widthtoheight;
	    spos = xpos + swidth/2 - sheight/2;
	    newspos = spos;
	    sposMin = xpos;
	    sposMax = xpos + swidth - sheight;
	    loose = 16;
	}
	
	
	void update() {
	    if (overEvent()) {
	      over = true;
	    } else {
	      over = false;
	    }
	    
//	    if (mousePressed && over) {
//	      locked = true;
//	    }
//	    if (!mousePressed) {
//	      locked = false;
//	    }
//	    
//	    if (locked) {
//	      newspos = constrain(mouseX-sheight/2, sposMin, sposMax);
//	    }
//	    
//	    if (Math.abs(newspos - spos) > 1) {
//	      spos = spos + (newspos-spos)/loose;
//	    }
	}
	
	
	float constrain(float val, float minv, float maxv) {
	    return Math.min(Math.max(val, minv), maxv);
	}

	boolean overEvent() {
//	    if (mouseX > xpos && mouseX < xpos+swidth &&
//	       mouseY > ypos && mouseY < ypos+sheight) {
//	      return true;
//	    } else {
//	      return false;
//	    }
		
		return true;
	}

	void draw() {
	    app.noStroke();
	    app.fill(204);
	    app.rect(xpos, ypos, swidth, sheight);
	    if (over || locked) {
	      app.fill(0, 0, 0);
	    } else {
	      app.fill(102, 102, 102);
	    }
	    app.rect(spos, ypos, sheight, sheight);
	}

	float getPos() {
	    // Convert spos to be values between
	    // 0 and the total width of the scrollbar
	    return spos * ratio;
	}
	
	
}
