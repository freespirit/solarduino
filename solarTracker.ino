#include <Time.h>
#include <math.h>

#define TIME_MSG_LEN  11   // time sync to PC is HEADER followed by Unix time_t as ten ASCII digits
#define TIME_HEADER  'T'   // Header tag for serial time sync message


const int servoAltPin = 9;
const int servoAzPin = 10;

const long J2000 = 2451545.0;
const double siderealDay = 24.0;

const double observerLatitude = 42.67464254549345;
const double observerLongtitude = 23.330283164978027;



void setup() {
  Serial.begin(9600);
}


void loop() {

  if(Serial.available()) {
    processTimeSyncMessage();
  }
  
  if(timeStatus() == timeNotSet)
    Serial.println("waiting for time sync message...");
  else {
    Serial.println("......");
    long startTime = millis();
    sunHorizontalCoordinates();
    long endTime = millis();
    
    Serial.print("Calculations took: ");
    Serial.print(endTime-startTime);
    Serial.println("ms");
  }
  
  delay(4000);
}

void processTimeSyncMessage() {
  while(Serial.available() >= TIME_MSG_LEN) {
    char c = Serial.read();
    Serial.print(c);
    
    if(c == TIME_HEADER) {
      time_t pcTime = 0;
      for(int i=0; i<TIME_MSG_LEN; ++i) {
        c = Serial.read();
        if(c>='0' && c<='9')
          pcTime = 10*pcTime + (c-'0');
      }
      setTime(pcTime);
    }
  }
}




/**
Use the following algorithm to find the position of the Sun in the sky:
http://en.wikipedia.org/wiki/Position_of_the_Sun
(check the link for variable names)
**/
void sunHorizontalCoordinates() {

  long JD = julianDayForCurrentTime();
  double n = JD - J2000;
  double L = 280.460 + 0.9856474*n;
  double g = 357.528 + 0.9856003*n;
  L = scaleFromZero(L, 360.0);
  g = scaleFromZero(g, 360.0);
  
  double g2 = scaleFromZero(2*g, 360.0);
  double lambda = L +
    1.915 * sin( radians(g) ) +
    0.020 * sin( radians(g2) );

  double epsilon = 23.439 - 0.0000004*n;
  double lambdaRads = radians(lambda);
  double epsilonRads = radians(epsilon);

  double RA = atan( cos( epsilonRads ) * tan( lambdaRads ) );
  double Dec = asin( sin( epsilonRads ) * sin( lambdaRads ) );
  
  RA = degrees(RA);
  Dec = degrees(Dec);

  while(RA < 0)
    RA += 360.0;

  RA = scaleFromZero(RA, 360.0);
  
  double GST = 18.697374558 + 24.06570982441908 * daysSinceEpoch();
  GST = scaleFromZero(GST, siderealDay);

  double gstDegs = (GST/siderealDay)*360.0;
  double hourAngle = gstDegs + observerLongtitude - RA;
  Serial.print("gstDegs: ");
  Serial.print(gstDegs);
  Serial.print(" hour angle: ");
  Serial.println(hourAngle);
  while(hourAngle < 0)
    hourAngle += 360.0;
  
  scaleFromZero(hourAngle, 360.0);
  
  
  double haRads = radians(hourAngle);
  double decRads = radians(Dec);
  double latRads = radians(observerLatitude);
  
  double tanAz = sin(haRads) / (cos(haRads) * sin(latRads) - tan(decRads) * cos(latRads));
  double sinAlt = sin(latRads) * sin(decRads) + cos(latRads) * cos(decRads) * cos(haRads);
  double azimuth = degrees( atan(tanAz) );
  double altitude = degrees( asin(sinAlt) );
  
  char coordinates[512];
  sprintf(coordinates, "RA: %g  DEC: %g  AZ: %g  ALT: %g ", RA, Dec, azimuth, altitude);
  Serial.print("JD: ");
  Serial.print(JD);
  Serial.print(" GST: ");
  Serial.print(GST);
  Serial.print(" HA: ");
  Serial.print(hourAngle);
  Serial.print(" RA: ");
  Serial.print(RA);
  Serial.print(" DEC: ");
  Serial.print(Dec);
  Serial.print(" AZ: ");
  Serial.print(azimuth);
  Serial.print(" ALT: ");
  Serial.println(altitude);

  Serial.println(coordinates);
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

double scaleFromZero(double value, double max) {
  int multiples = (int) (value / max);
  return value - multiples*max;
}

//http://playground.arduino.cc/Code/Time - look at the example sketch for how to set the time on the board
double daysSinceEpoch() {
  time_t timeNow = now();
  Serial.println(timeNow);
  tmElements_t tmElements = {0, 0, 14, 0, 1, 1, CalendarYrToTm(2000)};// temporary at 14 o'clock until a proper date/time_zone lib is found
  time_t timeThen = makeTime(tmElements);
  char timeLog[256];
  sprintf(timeLog, "%d-%d-%d %d:%d:%d %d-%d-%d %d:%d:%d",
   day(timeNow), month(timeNow), year(timeNow), hour(timeNow), minute(timeNow), second(timeNow),
   day(timeThen), month(timeThen), year(timeThen), hour(timeThen), minute(timeThen), second(timeThen));
  Serial.println(timeLog);
  
  double days = (timeNow - timeThen) / ((double) SECS_PER_DAY);
  Serial.print("days: ");
  Serial.println(days);
  return days;
}
