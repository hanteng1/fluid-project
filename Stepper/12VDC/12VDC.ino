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
//Adafruit_MotorShield AFMS = Adafruit_MotorShield(); 
// Or, create it with a different I2C address (say for stacking)
// Adafruit_MotorShield AFMS = Adafruit_MotorShield(0x61); 

// Select which 'port' M1, M2, M3 or M4. In this case, M1
//Adafruit_DCMotor *myMotor = AFMS.getMotor(1);
// You can also make another motor on port M2
//Adafruit_DCMotor *myPeltier = AFMS.getMotor(2);

//int state = -1;
//int vStep = 5;
//int curVelocity = 0;

//servo motor
//Servo myservo;
//int servoPos = 0;
//int servoStep = 5;
//int servoDelayUnit = 15;

int state = -1;

//valve 1
int solenoid1Pin = 4; 
int solenoid2Pin = 5;
int solenoid3Pin = 6;

int pumpTwo = 9;
int pumpOne = 3;

int pumpOneSpeed = 0;  //0 - 255 , cold
int pumpTwoSpeed = 0;  //hot
int maxSpeed = 255;

void setup() {
  Serial.begin(9600);           // set up Serial library at 9600 bps
  //Serial.println("Adafruit Motorshield v2 - DC Motor test!");

//  AFMS.begin();  // create with the default frequency 1.6KHz
  //AFMS.begin(1000);  // OR with a different frequency, say 1KHz
  
  // Set the speed to start, from 0 (off) to 255 (max speed)
  //initialize 
//  myMotor->setSpeed(255);
//  myMotor->run(FORWARD);
//  // turn on motor
//  myMotor->run(RELEASE);

  pinMode(pumpOne, OUTPUT);
  pinMode(pumpTwo, OUTPUT);
  
  
  //servo
//  myservo.attach(9);
//  servoPos = 0;
//  myservo.write(servoPos);
//  delay(servoDelayUnit * servoPos);


  pinMode(solenoid1Pin, OUTPUT);           //Sets the pin as an 
  pinMode(solenoid2Pin, OUTPUT);
  pinMode(solenoid3Pin, OUTPUT);

  
}

void loop() {

  analogWrite(pumpOne, pumpOneSpeed);
  analogWrite(pumpTwo, pumpTwoSpeed);

  //wait for input
  if(Serial.available()>0){
    state = Serial.read();          
    switch(state){                   // different state to switch  
      
      //////////////pump////////////
//      case 's':  //stop   
//      stopPumping();
//      break;
//
//      case 'l':  //left pump
//      leftPump();
//      break;
//
//      case 'r':  //right pump
//      rightPump();
//      break;
//
//      case 'i': //increase speed
//      faster();
//      break;
//
//      case 'd': //decrease speed
//      slower();
//      break;
//
//
//      /////////////servo motor////////////
//      case 'o':  //pos - 5
//      moveDown();
//      break;
//
//      case 'p':  //pos + 5
//      moveUp();
//      break;
//
//
//      case 'z':  //pos = 0
//      moveZero();
//      break;
//
//      case 'm':  //pos = 180
//      moveMax();
//      break;

/////////////////////////valves////////////////////
      case 'v':
      close1Valve();
      break;

      case 'b':
      open1Valve();
      break;

      case 'f':
      close2Valve();
      break;

      case 'g':
      open2Valve();
      break;

      case 'h':
      close3Valve();
      break;

      case 'j':
      open3Valve();
      break;

/////////////////////////pumps////////////////////////////
      case 'w':
      coldWater();
      break;

      case 'e':
      hotWater();
      break;

      case 'r':
      pumpOn();
      break;

      case 't':
      pumpOff();
      break;

//      case 'y':
//      increaseTemperature();
//      break;
//
//      case 'u':
//      decreaseTemperature();
//      break;
//
//      case 't':
//      noTemperature();
//      break;
      
    }
  }

  delay(10); 
}


//set speed
void coldWater()
{
  pumpOneSpeed += 5;
  if(pumpOneSpeed > maxSpeed)
  {
    pumpOneSpeed = maxSpeed;
  }
  pumpTwoSpeed = maxSpeed - pumpOneSpeed;
}

void hotWater()
{
  pumpOneSpeed -= 5;
  if(pumpOneSpeed < 0)
  {
    pumpOneSpeed = 0;
  }
  pumpTwoSpeed = maxSpeed - pumpOneSpeed;
}

void pumpOn()
{
  pumpOneSpeed = 127;
  pumpTwoSpeed = 127;
}

void pumpOff()
{
  pumpOneSpeed = 0;
  pumpTwoSpeed = 0;
}



//void stopPumping()
//{
//  myMotor->run(RELEASE);
//  curVelocity = 0;
//}
//
//void leftPump()
//{
//  myMotor->setSpeed(255);
//  curVelocity = 255;
//  myMotor->run(BACKWARD);
//}
//
//void rightPump()
//{
//  myMotor->setSpeed(255);
//  curVelocity = 255;
//  myMotor->run(FORWARD);
//}
//
//void faster()
//{
//  if(curVelocity + vStep <= 255)
//  {
//    curVelocity += vStep;
//    myMotor->setSpeed(curVelocity);
//  }
//}
//
//void slower()
//{
//  if(curVelocity - vStep >= 0)
//  {
//    curVelocity -= vStep;
//    myMotor->setSpeed(curVelocity);
//  }
//}
//
//void moveUp()
//{
//  if(servoPos + servoStep <= 180)
//  {
//    servoPos += servoStep;
//    myservo.write(servoPos);
//    delay(servoDelayUnit * servoStep);
//  } 
//}
//
//void moveDown()
//{
//  if(servoPos - servoStep >= 0)
//  {
//    servoPos -= servoStep;
//    myservo.write(servoPos);
//    delay(servoDelayUnit * servoStep);
//  }
//}
//
//void moveZero()
//{
//  servoPos = 0;
//  myservo.write(servoPos);
//  delay(1000);
//}
//
//
//
//void moveMax()
//{
//  servoPos = 180;
//  myservo.write(servoPos);
//  delay(1000);
//}

void close1Valve()
{
  digitalWrite(solenoid1Pin, HIGH);    //Switch Solenoid ON
}

void open1Valve()
{
  digitalWrite(solenoid1Pin, LOW);     //Switch Solenoid OFF
}

void close2Valve()
{
  digitalWrite(solenoid2Pin, HIGH);    //Switch Solenoid ON
}

void open2Valve()
{
  digitalWrite(solenoid2Pin, LOW);     //Switch Solenoid OFF
}

void close3Valve()
{
  digitalWrite(solenoid3Pin, HIGH);    //Switch Solenoid ON
}

void open3Valve()
{
  digitalWrite(solenoid3Pin, LOW);     //Switch Solenoid OFF
}


double sealevel(double P, double A)
{
  return(P/pow(1-(A/44330.0),5.255));
}


double altitude(double P, double P0)
{
  return(44330.0*(1-pow(P/P0,1/5.255)));
}


//void increaseTemperature(){
//  myPeltier->run(RELEASE);
//  myPeltier->setSpeed(100);
//  myPeltier->run(BACKWARD);
//}
//
//void decreaseTemperature(){
//   myPeltier->run(RELEASE);
//   myPeltier->setSpeed(100);
//   myPeltier->run(FORWARD);
//}
//
//void noTemperature()
//{
//  myPeltier->run(RELEASE);
//}




