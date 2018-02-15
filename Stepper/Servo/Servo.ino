#include <Servo.h>

Servo myservo;
int pos = 0;
int i = 0;

void setup() {
  // put your setup code here, to run once:
  //Serial.begin(9600);  
  myservo.attach(9);
  myservo.write(30);
  delay(15 * 30);

}

void loop() {
  // put your main code here, to run repeatedly:

 
     for (pos = 30; pos <= 80; pos += 1) { // goes from 0 degrees to 180 degrees
      // in steps of 1 degree
      myservo.write(pos);              // tell servo to go to position in variable 'pos'
      delay(15);                       // waits 15ms for the servo to reach the position
    }

    delay(1000);
    
    for (pos = 80; pos >= 30; pos -= 1) { // goes from 180 degrees to 0 degrees
      myservo.write(pos);              // tell servo to go to position in variable 'pos'
      delay(15);                       // waits 15ms for the servo to reach the position
    }


  
  
 
}
