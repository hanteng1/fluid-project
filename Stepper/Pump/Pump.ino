int relayPinOne = 10;
int relayPinTwo = 9;
int state = -1;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);  
  pinMode(relayPinOne, OUTPUT);
  pinMode(relayPinTwo, OUTPUT);
}

void loop() {
  // put your main code here, to run repeatedly:
  if(Serial.available()>0){
   state = Serial.read(); 
    switch(state){   
      case 's':  //stop 
      digitalWrite(relayPinTwo, LOW);  
      digitalWrite(relayPinOne, HIGH);
      
      break;

      case 'd':  //left pump
      digitalWrite(relayPinOne, LOW);  
      digitalWrite(relayPinTwo, HIGH);
      break;
    }
  }

  delay(10);
}
