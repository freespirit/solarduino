package st.solartracker.coordinates;

import java.lang.Math;
import java.util.Calendar;

public class Coordinates {
	
	public static void main(String[] args) {
		Coordinates c = new Coordinates();
		
		c.sunHorizontalCoordinates();
	}
	
	void sunHorizontalCoordinates() {
		//  1. calculate the Sun's position in the ecliptic coordinate system,
		long JD = julianDayForCurrentTime();
		System.out.println("JD: " + JD);
		long n = JD - 2451545;
		double L = 280.460 + 0.9856474*n;//the mean longtitude of the Sun
		double g = 357.528 + 0.9856003*n;//The mean anomaly of the Sun
		
		int times360 = (int) (L / 360.0);
		L = L - times360*360.0;
		times360 = (int) (g / 360.0);
		g = g - times360*360.0;
		
		double lambda = L + 1.915*Math.sin(g) + 0.020*Math.sin(2*g); // ecliptic longitude of the Sun


		//  2. convert to the equatorial coordinate system, and
		double epsilon = 23.439 - 0.0000004*n;//approximation of obliquity of the ecliptic
		double RA = Math.atan( Math.cos(epsilon) * Math.tan(lambda) );
		double Dec = Math.asin( Math.sin(epsilon) * Math.sin(lambda) );
		System.out.println("RA: " + RA + " DEC: " + Dec);


		//  3. convert to the horizontal coordinate system, for the observer's local time and position.

	  
	  
//	  Serial.print("JD: ");
//	  Serial.print(JD);
//	  Serial.print(" RA: ");
//	  Serial.print(RA);
//	  Serial.print(" DEC: ");
//	  Serial.print(DEC);
//	  Serial.print(" ALT: ");
//	//  Serial.print(ALT);
//	  Serial.print(" AZ: ");
//	//  Serial.println(AZ);
	}


	/*
	 Use
	 http://en.wikipedia.org/wiki/Julian_day#Converting_Julian_or_Gregorian_calendar_date_to_Julian_Day_Number
	 to find the Julian Date for the current time
	*/
	long julianDayForCurrentTime() {
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		int day = now.get(Calendar.DAY_OF_MONTH);
//		System.out.println(day + "." + month + "." + year);
		
		int a = (14-month)/12;
		int y = year + 4800 - a;
		int m = month +12*a - 3;
	  
		return day + (153*m + 2)/5 + 365l*y + y/4 - y/100 + y/400 - 32045;
	}
}
