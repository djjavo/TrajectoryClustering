import java.util.ArrayList;

public class Helpers {

	static String colourReset = (char) 27 + "[0m";
	static String colourInfo = (char) 27 + "[0;34m";
	static String colourError = (char) 27 + "[0;31m";
	static String colourSuccess = (char) 27 + "[0;32m";

	static String infoMessage = colourInfo + "[INFO]   " + colourReset + "\t";
	static String errorMessage = colourError + "[ERROR]  " + colourReset + "\t";
	static String successMessage =
		colourSuccess + "[SUCCESS]" + colourReset + "\t";

	public static Point centroid(ArrayList<Point> arr) {
		double sum = 0;
		double meanLat = 0;
		double meanLong = 0;

		for (int i = 0; i < arr.size(); i++) {
			sum += arr.get(i).getLatitude();
		}
		meanLat = sum / arr.size();

		sum = 0;
		for (int i = 0; i < arr.size(); i++) {
			sum += arr.get(i).getLongitude();
		}
		meanLong = sum / arr.size();

		return new Point(meanLat, meanLong);
	}

	public static double distanceBetween(Point p1, Point p2) {
		return Haversine.toMetres(
			p1.getLatitude(),
			p1.getLongitude(),
			p2.getLatitude(),
			p2.getLongitude()
		);
	}

	public static double timeBetween(Point p1, Point p2) {
		return p2.getTimestamp() - p1.getTimestamp();
	}

	public static double absoluteTimeBetween(Point p1, Point p2) {
		return Math.abs(p1.getTimestamp() - p2.getTimestamp());
	}

	public static void mergeVisits(int threshold) {
		// Go through every visit
		for (int i = 1; i < Dataset.visits.size();) {
			ArrayList<Point> previousVisit = Dataset.visits.get(i - 1);
			ArrayList<Point> thisVisit = Dataset.visits.get(i);

			double timeDifference =
				thisVisit.get(0).getTimestamp() -
				previousVisit.get(previousVisit.size() - 1).getTimestamp();

			if (timeDifference < threshold) {
				// combine
				ArrayList<Point> combinedVisit = new ArrayList<Point>(
					previousVisit
				);

				// convert noise to visit
				for (int j = 1; j < Dataset.noise.size();) {
					ArrayList<Point> thisNoise = Dataset.noise.get(j);
					if (
						thisNoise.get(0).getTimestamp() >
						previousVisit
							.get(previousVisit.size() - 1)
							.getTimestamp() &&
						thisNoise.get(thisNoise.size() - 1).getTimestamp() <
						thisVisit.get(0).getTimestamp()
					) {
						combinedVisit.addAll(thisNoise);
						Dataset.noise.remove(j);
					} else if (
						thisNoise.get(0).getTimestamp() >
						previousVisit
							.get(previousVisit.size() - 1)
							.getTimestamp()
					) {
						break;
					} else {
						j++;
					}
				}

				combinedVisit.addAll(thisVisit);

				Dataset.visits.set(i - 1, combinedVisit);

				Dataset.visits.remove(i);

				if (Configuration.OUTPUT_ENABLED) System.out.println(
					infoMessage +
					"Merged visit " +
					(i - 1) +
					" and " +
					i +
					" together due to a time difference of " +
					timeDifference
				);
			} else {
				i++;
			}
		}
	}
}
