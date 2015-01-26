package st.coordinates;

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
		times360 = (int) ((2*g) / 360.0);
		double g2 = 2*g - times360;
		
		// ecliptic longitude of the Sun
		double lambda = L +
				1.915*Math.sin( Math.toRadians(g) ) +
				0.020*Math.sin( Math.toRadians(g2) );


		//  2. convert to the equatorial coordinate system, and
		double epsilon = 23.439 - 0.0000004*n;//approximation of obliquity of the ecliptic
		
		double lambdaRads = Math.toRadians(lambda);
		double epsilonRads = Math.toRadians(epsilon);

		double RA = Math.atan(
				Math.cos( epsilonRads ) *
				Math.tan( lambdaRads )
				);
		double Dec = Math.asin(
				Math.sin( epsilonRads ) *
				Math.sin( lambdaRads )
				);
		
		RA = Math.toDegrees(RA);
		Dec = Math.toDegrees(Dec);
		
		while(RA < 0)
			RA += 360.0;
		
		times360 = (int) (RA / 360.0);
		RA -= times360 * 360.0;
		System.out.println("RA: " + RA + " DEC: " + Dec);


		//  3. convert to the horizontal coordinate system, for the observer's local time and position.

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
