#include <Wire.h>
#include <Adafruit_MotorShield.h>

//Adafruit_MotorShield AFMS = Adafruit_MotorShield(); 
//Adafruit_DCMotor *myPeltier = AFMS.getMotor(2);

int peltier = 3;
int sinOut = 240;
int state = -1;

void setup() {
//  // put your setup code here, to run once:
  Serial.begin(9600);           // set up Serial library at 9600 bps
//
//  AFMS.begin(); //1.6khz
//
//  
//  //peltier
////  myPeltier->setSpeed(250);
////  myPeltier->run(FORWARD);
////  Serial.println("cold on");
////
////  delay(20000);
////  // turn off peltier
////  myPeltier->run(RELEASE);
//
//  myPeltier->setSpeed(200);
//  myPeltier->run(BACKWARD);
//  Serial.println("very hot on");
//
//  delay(20000);
//  // turn off peltier
//  myPeltier->run(RELEASE);
//  Serial.println("stop");
  
  
  pinMode(peltier, OUTPUT);
  
  
}

void loop() {

// for(int i = 0; i<360; i++){ 
//    //convert 0-360 angle to radian (needed for sin function) 
//    float rad = DEG_TO_RAD * i; 
//    //calculate sin of angle as number between 0 and 255 
//    int sinOut = constrain((sin(rad) * 128) + 128, 0, 255); 
//
//    Serial.println(sinOut);
//    analogWrite(peltier, sinOut);
//    delay(20); 
//  }

   if(Serial.available()>0){
    state = Serial.read();   
     switch(state){ 
      case 's':  //stop   
       sinOut++;
       if(sinOut >255)
       {
        sinOut = 255;
       }
        Serial.println(sinOut);
       
       analogWrite(peltier, sinOut);
      break;

      case 'd':  //stop   
       sinOut--;
       if(sinOut < 0)
       {
       sinOut = 0;
       }

        Serial.println(sinOut);
       analogWrite(peltier, sinOut);
      break;
     }
   }
  
}
