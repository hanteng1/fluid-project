#include <SparkFun_MS5803_I2C.h>


//pressure
MS5803 pressureSensor(ADDRESS_HIGH);
int temperature = 1;
#define aref_voltage 5.0;


//Create variables to store results
double pressure_abs, pressure_relative, altitude_delta, pressure_baseline;
double base_altitude = 1655.0; 


void setup() {
  Serial.begin(9600);
  // put your setup code here, to run once:
  pressureSensor.reset();
  pressureSensor.begin();  
  pressure_baseline = pressureSensor.getPressure(ADC_4096);


}

void loop() {
  
  //read pressure
   pressure_abs = pressureSensor.getPressure(ADC_4096);
   //pressure_relative = sealevel(pressure_abs, base_altitude);
   //altitude_delta = altitude(pressure_abs , pressure_baseline);
   //send the data to serial
   Serial.println(pressure_abs);

  
  int tempReading = analogRead(temperature);  
 
  // converting that reading to voltage, for 3.3v arduino use 3.3
  float voltage = tempReading * aref_voltage;
  voltage /= 1024.0; 
 
  // print out the voltage
  //Serial.print(voltage); Serial.println(" volts");
 
  // now print out the temperature
  float temperatureC = (voltage - 0.5) * 100;  //converting from 10 mv per degree wit 500 mV offset
                                               //to degrees ((voltage - 500mV) times 100)
  Serial.println(temperatureC); 
  //Serial.println(" degrees C");

  delay(10);
}
