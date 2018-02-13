/* 
This is a test sketch for the Adafruit assembled Motor Shield for Arduino v2
It won't work with v1.x motor shields! Only for the v2's with built in PWM
control

For use with the Adafruit Motor Shield v2 
---->  http://www.adafruit.com/products/1438
*/

#include <Wire.h>
#include <Adafruit_MotorShield.h>
#include <Servo.h>

// Create the motor shield object with the default I2C address
Adafruit_MotorShield AFMS = Adafruit_MotorShield(); 
// Or, create it with a different I2C address (say for stacking)
// Adafruit_MotorShield AFMS = Adafruit_MotorShield(0x61); 

// Select which 'port' M1, M2, M3 or M4. In this case, M1
Adafruit_DCMotor *myMotor = AFMS.getMotor(1);
// You can also make another motor on port M2
//Adafruit_DCMotor *myOtherMotor = AFMS.getMotor(2);

int state = -1;
int vStep = 5;
int curVelocity = 0;

//servo motor
Servo myservo;
int servoPos = 0;
int servoStep = 5;
int servoDelayUnit = 15;

void setup() {
  Serial.begin(9600);           // set up Serial library at 9600 bps
  Serial.println("Adafruit Motorshield v2 - DC Motor test!");

  AFMS.begin();  // create with the default frequency 1.6KHz
  //AFMS.begin(1000);  // OR with a different frequency, say 1KHz
  
  // Set the speed to start, from 0 (off) to 255 (max speed)
  //initialize 
  myMotor->setSpeed(255);
  myMotor->run(FORWARD);
  // turn on motor
  myMotor->run(RELEASE);


  //servo
  myservo.attach(9);
  servoPos = 50;
  myservo.write(servoPos);
  delay(servoDelayUnit * servoPos);
}

void loop() {
  
//
//  myMotor->run(BACKWARD);
//
//  delay(5000);
//
//  myMotor->run(RELEASE);
//
//  delay(2000);

  
//  uint8_t i;
//  
//  Serial.print("tick");
//
//  myMotor->run(FORWARD);
//  for (i=0; i<255; i++) {
//    myMotor->setSpeed(i);  
//    delay(10);
//  }
//  for (i=255; i!=0; i--) {
//    myMotor->setSpeed(i);  
//    delay(10);
//  }
//  
//  Serial.print("tock");
//
//  myMotor->run(BACKWARD);
//  for (i=0; i<255; i++) {
//    myMotor->setSpeed(i);  
//    delay(10);
//  }
//  for (i=255; i!=0; i--) {
//    myMotor->setSpeed(i);  
//    delay(10);
//  }
//
//  Serial.print("tech");
//  myMotor->run(RELEASE);
//  delay(1000);





  if(Serial.available()>0){
    state = Serial.read();          

    switch(state){                   // different state to switch  
      
      //////////////pump////////////
      case 's':  //stop   
      stopPumping();
      break;

      case 'l':  //left pump
      leftPump();
      break;

      case 'r':  //right pump
      rightPump();
      break;

      case 'i': //increase speed
      faster();
      break;

      case 'd': //decrease speed
      slower();
      break;


      /////////////servo motor////////////
      case 'o':  //pos - 5
      moveDown();
      break;

      case 'p':  //pos + 5
      moveUp();
      break;
      
      
    }
  }
}


void stopPumping()
{
  myMotor->run(RELEASE);
  curVelocity = 0;
}

void leftPump()
{
  myMotor->setSpeed(255);
  curVelocity = 255;
  myMotor->run(BACKWARD);
}

void rightPump()
{
  myMotor->setSpeed(255);
  curVelocity = 255;
  myMotor->run(FORWARD);
}

void faster()
{
  if(curVelocity + vStep <= 255)
  {
    curVelocity += vStep;
    myMotor->setSpeed(curVelocity);
  }
}

void slower()
{
  if(curVelocity - vStep >= 0)
  {
    curVelocity -= vStep;
    myMotor->setSpeed(curVelocity);
  }
}

void moveUp()
{
  servoPos += servoStep;
  myservo.write(servoPos);
  delay(servoDelayUnit * servoStep);
}

void moveDown()
{
  servoPos -= servoStep;
  myservo.write(servoPos);
  delay(servoDelayUnit * servoStep);
}



