import java.io.*;
import java.lang.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.*;

/**
 * Java implementation of STA extraction algorithm.
 * See: https://ieeexplore.ieee.org/document/5702238
 * @author James Van Hinsbergh
 * @version 1.0
 */

public class STA {

	String DATA_DIRECTORY;

	int numberOfPointsInBuffer;
	double distanceThreshold;
	int numberDelay;

	Point previous;

	ArrayList<Point> buffer = new ArrayList<Point>();

	ArrayList<Point> sta = new ArrayList<Point>();

	ArrayList<Double> sdist = new ArrayList<Double>();

	public static void main(String[] args) throws Exception {
		Configuration.load(args[0], args[1]);

		if (!Configuration.CLUSTERER_USED.equals("sta")) {
			throw new Exception(
				"The STA clusterer has not been set as the active clusterer in the configuration file"
			);
		}

		STA cluster = new STA(
			Configuration.DISTANCE_THRESHOLD,
			Configuration.NUM_POINTS
		);
		cluster.runCluster();

		if (Configuration.MERGE_TIME != 0) Helpers.mergeVisits(
			Configuration.MERGE_TIME
		);
		Dataset.writeVisitsToCSV();

		System.out.println(Dataset.visits.size());

		if (Configuration.OUTPUT_ENABLED) System.out.println(
			Helpers.successMessage +
			"Number of clusters produced: " +
			Dataset.visits.size()
		);
	}

	public STA(double distanceThreshold, int numberOfPointsInBuffer) {
		Dataset.readPointsFromCSV();

		this.distanceThreshold = distanceThreshold;
		this.numberOfPointsInBuffer = numberOfPointsInBuffer;
		numberDelay = numberOfPointsInBuffer / 2;

		for (int i = 0; i < numberOfPointsInBuffer; i++) {
			buffer.add(Dataset.points.get(i));
		}

		for (int i = 0; i < numberOfPointsInBuffer - 1; i++) {
			ArrayList<Point> temp1 = new ArrayList<Point>(
				buffer.subList(0, i + 1)
			);
			ArrayList<Point> temp2 = new ArrayList<Point>(
				buffer.subList(0, i + 2)
			);

			sdist.add(
				Helpers.distanceBetween(
					Helpers.centroid(temp1),
					Helpers.centroid(temp2)
				)
			);
		}

		for (int i = 0; i < numberDelay - 1; i++) {
			sdist.remove(0);
		}

		previous = Helpers.centroid(buffer);
	}

	public void runCluster() {
		for (int i = numberOfPointsInBuffer; i < Dataset.points.size(); i++) {
			process(Dataset.points.get(i));
		}

		if (sta.size() > numberOfPointsInBuffer) {
			Dataset.visits.add(sta);
		}
	}

	public void process(Point point) {
		buffer.add(point);

		Point candidate = buffer.get(numberOfPointsInBuffer / 2);

		Point current = Helpers.centroid(buffer);
		sdist.add(Helpers.distanceBetween(previous, current));

		if (weightedMovingAverage(sdist) < distanceThreshold) {
			sta.add(candidate);
		} else {
			if (sta.size() > numberOfPointsInBuffer) {
				Dataset.visits.add(sta);
			} else if (Configuration.KEEP_POINTS) {
				Dataset.noise.add(sta);
				if (Configuration.OUTPUT_ENABLED) System.out.println(
					Helpers.infoMessage +
					"Noise " +
					Dataset.noise.size() +
					" added: comprised of " +
					sta.size() +
					" distinct points."
				);
			}
			sta = new ArrayList<Point>();
			sta.add(candidate);
		}

		previous = current;
		sdist.remove(0);
		buffer.remove(0);
	}

	private double weightedMovingAverage(ArrayList<Double> sdist) {
		int t = sdist.size() - numberDelay;

		double top = 0;

		for (int i = 0; i < numberDelay; i++) {
			top +=
				(
					(numberDelay - i) *
					(
						sdist.get(Math.floorMod(t + i, sdist.size())) +
						sdist.get(Math.floorMod(t - i, sdist.size()))
					)
				);
		}

		double bottom = numberDelay * (numberDelay + 1);

		return (top / bottom);
	}
}
