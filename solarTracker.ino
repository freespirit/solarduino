#include <Time.h>

const int servoAltPin = 9;
const int servoAzPin = 10;



void setup() {
  Serial.begin(9600);
}


void loop() {

  long startTime = millis();
  sunHorizontalCoordinates();
  long endTime = millis();
  
  Serial.print("Calculations took: ");
  Serial.print(endTime-startTime);
  Serial.println("ms");
}


/**
Use this to find the position of the Sun in the sky:
http://en.wikipedia.org/wiki/Position_of_the_Sun
**/
void sunHorizontalCoordinates() {

  //  1. calculate the Sun's position in the ecliptic coordinate system,
  long JD = julianDayForCurrentTime();
  long n = JD - 2451545;
  float L = 280.460f + 0.9856474f*n;//the mean longtitude of the Sun
  float g = 357.528f + 0.9856003f*n;//The mean anomaly of the Sun

  int times360 = L / 360;
  L = L - times360*360.0f;
  times360 = g/360;
  g = g - times360*360.0f;
  
  float lambda = L + 1.915*sin(g) + 0.020*sin(2*g); // ecliptic longitude of the Sun


  //  2. convert to the equatorial coordinate system, and
  float epsilon = 23.439 - 0.0000004*n;//approximation of obliquity of the ecliptic
  float RA = atan( cos(epsilon) * tan(lambda) );
  float Dec = asin( sin(epsilon) * sin(lambda) );


  //  3. convert to the horizontal coordinate system, for the observer's local time and position.

  
  Serial.print("JD: ");
  Serial.print(JD);
  Serial.print(" RA: ");
  Serial.print(RA);
  Serial.print(" DEC: ");
  Serial.print(DEC);
  Serial.print(" ALT: ");
//  Serial.print(ALT);
  Serial.print(" AZ: ");
//  Serial.println(AZ);
}


/*
 Use
 http://en.wikipedia.org/wiki/Julian_day#Converting_Julian_or_Gregorian_calendar_date_to_Julian_Day_Number
 to find the Julian Date for the current time
*/
long julianDayForCurrentTime() { 
  int a = (14-month())/12;
  int y = year() + 4800 - a;
  int m = month() +12*a - 3;
  
  return day() + (153*m + 2)/5 + 365l*y + y/4 - y/100 + y/400 - 32045;
}
