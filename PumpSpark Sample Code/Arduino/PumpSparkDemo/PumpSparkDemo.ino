/*** 
 * PumpSpark Demo
 * Microsoft Research
 * UIST 2013 Student Innovation Contest
 ***/

#include <SoftwareSerial.h>
SoftwareSerial pumpSerial(4, 5); // RX, TX

// Setup function
void setup()
{
  // Setting up serial monitor for debugging
  Serial.begin(9600);

  // Setting up the Software Serial port running on digital I/O pin
  pumpSerial.begin(9600);

  // Initialize with pump 0 turned off
  actuatePump(0, 0);
}

// Loop function (required)
void loop()
{
  // Example code running through pump values from max to min
  for(byte i = 254; i > 0; i--)
  {
    actuatePump(0, i);
    delay(100);
  }

  // Actuating pump 0 at max value of 254 for 5 seconds
  actuatePump(0,254);
  delay(5000);

  // Turning pump 0 off for 5 seconds
  actuatePump(0,0);
  delay(5000);
}

/*** 
 * Function to actuate the pumps 
 * pump = pump number => {0, 1, 2, 3, 4, 5, 6, 7}
 * value = pump flow value => {min to max} = {0 to 254}  
 ***/

void actuatePump(byte pump, byte value)
{
  // Send start byte = {255} or {0xff}
  // Required every time to actuate pumps
  pumpSerial.write(255);

  // Send pump number = {0, 1, 2, 3, 4, 5, 6, 7}
  pumpSerial.write(pump);

  // Set pump value {min to max} = {0 to 254}
  pumpSerial.write(value);
}

