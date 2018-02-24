#include <Wire.h>
#include <Adafruit_MotorShield.h>

Adafruit_MotorShield AFMS = Adafruit_MotorShield(); 
Adafruit_DCMotor *myPeltier = AFMS.getMotor(2);

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);           // set up Serial library at 9600 bps

  AFMS.begin(); //1.6khz

  
  //peltier
//  myPeltier->setSpeed(250);
//  myPeltier->run(FORWARD);
//  Serial.println("cold on");
//
//  delay(20000);
//  // turn off peltier
//  myPeltier->run(RELEASE);

  myPeltier->setSpeed(200);
  myPeltier->run(BACKWARD);
  Serial.println("very hot on");

  delay(20000);
  // turn off peltier
  myPeltier->run(RELEASE);
  Serial.println("stop");
  
}

void loop() {
  // put your main code here, to run repeatedly:
  
  
}
