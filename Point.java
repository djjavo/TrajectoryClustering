import java.util.ArrayList;
import java.util.Objects;

public class Point {

	double latitude;
	double longitude;
	double timestamp;
	ArrayList<String> additionalData;
	int cluster;

	/**
	 * Initialises a newly created Point object without a timestamp.
	 *
	 * @param latitude the latitude of the GPS data
	 * @param longitude the longitude of the GPS data
	 */

	public Point(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Point(double latitude, double longitude, double timestamp) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.timestamp = timestamp;
	}

	public Point(
		double latitude,
		double longitude,
		double timestamp,
		ArrayList<String> additionalData
	) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.timestamp = timestamp;
		this.additionalData = additionalData;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public double getLongitude() {
		return this.longitude;
	}

	public double getTimestamp() {
		return this.timestamp;
	}

	public ArrayList<String> getAdditionalData() {
		return this.additionalData;
	}

	public String printAdditionalData() {
		String dataString = "";
		for (String s : additionalData) {
			dataString += ", " + s;
		}
		return dataString;
	}

	public int getCluster() {
		return this.cluster;
	}

	public void setCluster(int cluster) {
		this.cluster = cluster;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;

		if (!(o instanceof Point)) {
			return false;
		}

		Point point = (Point) o;
		return (timestamp == point.timestamp);
	}

	@Override
	public int hashCode() {
		return Objects.hash(latitude, longitude);
	}
}
