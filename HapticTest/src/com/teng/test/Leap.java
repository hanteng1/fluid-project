package com.teng.test;

import java.io.IOException;
import java.lang.Math;
import com.leapmotion.leap.*;
import com.leapmotion.leap.Finger.Type;



public class Leap {
	
	public LeapListener listener;
    public Controller controller;
    
    public float indexTipX;
    public float indexTipY;
    
    public static Leap instance;
    public static Leap getInstance()
    {
    	if(instance == null)
    	{
    		instance = new Leap();
    	}
    	
    	return instance;
    }
    
    public Leap() {
    	
    	instance = this;
    	
    	listener = new LeapListener();
        controller = new Controller();
        
        controller.addListener(listener);
       
    }
    
    public void end()
    {
    	controller.removeListener(listener);
    }
    
    
}


class LeapListener extends Listener {
    public void onInit(Controller controller) {
        System.out.println("Initialized");
    }

    public void onConnect(Controller controller) {
        System.out.println("Connected");
    }

    public void onDisconnect(Controller controller) {
        //Note: not dispatched when running in a debugger.
        System.out.println("Disconnected");
    }

    public void onExit(Controller controller) {
        System.out.println("Exited");
    }

    public void onFrame(Controller controller) {
        // Get the most recent frame and report some basic information
        Frame frame = controller.frame();
//        System.out.println("Frame id: " + frame.id()
//                         + ", timestamp: " + frame.timestamp()
//                         + ", hands: " + frame.hands().count()
//                         + ", fingers: " + frame.fingers().count());

        //Get hands
        for(Hand hand : frame.hands()) {
//            String handType = hand.isLeft() ? "Left hand" : "Right hand";
//            System.out.println("  " + handType + ", id: " + hand.id()
//                             + ", palm position: " + hand.palmPosition());

            // Get the hand's normal vector and direction
//            Vector normal = hand.palmNormal();
//            Vector direction = hand.direction();

            // Calculate the hand's pitch, roll, and yaw angles
//            System.out.println("  pitch: " + Math.toDegrees(direction.pitch()) + " degrees, "
//                             + "roll: " + Math.toDegrees(normal.roll()) + " degrees, "
//                             + "yaw: " + Math.toDegrees(direction.yaw()) + " degrees");

//            // Get arm bone
//            Arm arm = hand.arm();
//            System.out.println("  Arm direction: " + arm.direction()
//                             + ", wrist position: " + arm.wristPosition()
//                             + ", elbow position: " + arm.elbowPosition());

            // Get fingers
        	
        	if(hand.isRight())
        	{
                for (Finger finger : hand.fingers()) {
                	
                	if(finger.type() == Type.TYPE_INDEX)
                	{
                		Leap.getInstance().indexTipX = finger.tipPosition().getX();
                		Leap.getInstance().indexTipY = finger.tipPosition().getY();
                	}
                	
                	
//                  System.out.println("    " + finger.type() + ", id: " + finger.id()
//                                   + ", length: " + finger.length()
//                                   + "mm, width: " + finger.width() + "mm");
  //
//                  //Get Bones
//                  for(Bone.Type boneType : Bone.Type.values()) {
//                      Bone bone = finger.bone(boneType);
//                      System.out.println("      " + bone.type()
//                                       + " bone, start: " + bone.prevJoint()
//                                       + ", end: " + bone.nextJoint()
//                                       + ", direction: " + bone.direction());
//                  }
              }
        	}
        	

        }

        if (!frame.hands().isEmpty()) {
            //System.out.println();
        }
    }

}
