#include <SparkFun_MS5803_I2C.h>

MS5803 sensor(ADDRESS_HIGH);

//Create variables to store results
double pressure_abs, pressure_relative, altitude_delta, pressure_baseline;
double base_altitude = 1655.0; 

void setup() {
  // put your setup code here, to run once:
 Serial.begin(9600);
    //Retrieve calibration constants for conversion math.
    sensor.reset();
    sensor.begin();
    
    pressure_baseline = sensor.getPressure(ADC_4096);
}

void loop() {
  // put your main code here, to run repeatedly:
   pressure_abs = sensor.getPressure(ADC_4096);
   pressure_relative = sealevel(pressure_abs, base_altitude);
   altitude_delta = altitude(pressure_abs , pressure_baseline);
    Serial.println(altitude_delta);
   delay(1000);
}


 double sealevel(double P, double A)
{
  return(P/pow(1-(A/44330.0),5.255));
}


double altitude(double P, double P0)
{
  return(44330.0*(1-pow(P/P0,1/5.255)));
}
