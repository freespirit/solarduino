package st.coordinates;

import java.lang.Math;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Coordinates {
	public static final double J2000 = 2451545.0;
	public static final double observerLatitude = 42.67464254549345;
	public static final double observerLongtitude = 23.330283164978027;
	public static final double siderealDay = 24.0;//23.9344699;//hours
	public static final long MILLIS_IN_DAY = 24l * 60l * 60l * 1000l;
	
	
	
	public static void main(String[] args) {
		Coordinates c = new Coordinates();
		
		c.sunHorizontalCoordinates();
	}
	
	void sunHorizontalCoordinates() {
		//  1. calculate the Sun's position in the ecliptic coordinate system,
		long JD = julianDayForCurrentTime();
		System.out.println("JD: " + JD);
		double n = JD - J2000;// julian days since Epoch 
		double L = 280.460 + 0.9856474*n;//the mean longtitude of the Sun
		double g = 357.528 + 0.9856003*n;//The mean anomaly of the Sun
		
		L = scaleFromZero(L, 360.0);
		g = scaleFromZero(g, 360.0);
		double g2 = scaleFromZero(2*g, 360.0);
		
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
		
		RA = scaleFromZero(RA, 360.0);
		System.out.println("RA: " + RA + " DEC: " + Dec);


		
		//  3. convert to the horizontal coordinate system, for the observer's local time and position.
		// NOTE: Azimuth is measured from the south point
		
		//Greenwich Sidereal Time, based on http://en.wikipedia.org/wiki/Sidereal_time
		double GST = 18.697374558 + 24.06570982441908 * daysSinceEpoch();
		GST = scaleFromZero(GST, siderealDay);
		
		double gstDegs = (GST/siderealDay)*360.0;
		
		double hourAngle = gstDegs + observerLongtitude - RA;
		while(hourAngle < 0)
			hourAngle += 360.0;
		scaleFromZero(hourAngle, 360.0);
		System.out.println("GST: " + GST + "(degs: " + gstDegs + ")" + " hour angle: " + hourAngle);

		
		
		double tanAz =
				sin(hourAngle) /
				(cos(hourAngle) * sin(observerLatitude) - tan(Dec) * cos(observerLatitude));
		double sinAlt =
				sin(observerLatitude) * sin(Dec) +
				cos(observerLatitude) * cos(Dec) * cos(hourAngle);
		
		double azimuth = Math.toDegrees( Math.atan(tanAz) );
		double altitude = Math.toDegrees( Math.asin(sinAlt) );
		
		System.out.println("AZ: " + azimuth + " ALT: " + altitude);
				
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
		System.out.println(now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND));
		
		int a = (14-month)/12;
		int y = year + 4800 - a;
		int m = month +12*a - 3;
	  
		return day + (153*m + 2)/5 + 365*y + y/4 - y/100 + y/400 - 32045;
	}
	
	double scaleFromZero(double value, double max) {
		int multiples = (int) (value / max);
		return value - multiples*max;
	}
	
	double sin(double angle) {
		return Math.sin( Math.toRadians(angle) );
	}
	
	double cos(double angle) {
		return Math.cos( Math.toRadians(angle) );
	}
	
	double tan(double angle) {
		return Math.tan( Math.toRadians(angle) );
	}
	
	double daysSinceEpoch() {
		Calendar now = Calendar.getInstance();
		Calendar then = Calendar.getInstance();
		then.setTimeZone(TimeZone.getTimeZone("GMT"));
		then.set(2000, 0, 1, 12, 0, 0);
		
		double days = (now.getTimeInMillis() - then.getTimeInMillis()) / ((double) MILLIS_IN_DAY);
		System.out.println(now.getTime().toString());
		System.out.println(new Date(then.getTimeInMillis()).toString());
		System.out.println(new Date(then.getTimeInMillis() + (long)(days * (double)MILLIS_IN_DAY)).toString() + " days: " + days); 
		return days;
	}
}
