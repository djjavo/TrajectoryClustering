import java.lang.*;
import java.io.*;
import java.util.*;


public class Haversine {

	// Adapted from: https://rosettacode.org/wiki/Haversine_formula#Java

	public final static double R = 6372.8; // In kilometers
	
	public static double toKilometres(double lat1, double lon1, double lat2, double lon2) {
		
		double latDelta = Math.toRadians(lat2 - lat1);
		double longDelta = Math.toRadians(lon2 - lon1);

		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);
 
		double a = Math.pow(Math.sin(latDelta / 2),2) + Math.pow(Math.sin(longDelta / 2),2) * Math.cos(lat1) * Math.cos(lat2);
		double c = 2 * Math.asin(Math.sqrt(a));
		
		return R * c;

	}

	public static double toMetres(double lat1, double lon1, double lat2, double lon2) {

		return (toKilometres(lat1, lon1, lat2, lon2) * 1000);

	}

}