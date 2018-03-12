int solenoidPin = 10;   


void setup() {
  // put your setup code here, to run once:
  pinMode(solenoidPin, OUTPUT);           //Sets the pin as an output
}

void loop() {
  // put your main code here, to run repeatedly:
  digitalWrite(solenoidPin, HIGH);    //Switch Solenoid ON
  delay(100);                      //Wait 1 Second
 digitalWrite(solenoidPin, LOW);     //Switch Solenoid OFF
  delay(100);                      //Wait 1 Second
}
