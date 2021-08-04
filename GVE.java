import java.io.*;
import java.lang.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.*;

/**
 * Java implementation of the Gradient-based Visit Extractor.
 * See: https://www.sciencedirect.com/science/article/pii/S0022000015001312
 * @author James Van Hinsbergh
 * @version 1.1
 */

public class GVE {

	String DATA_DIRECTORY;
	boolean OUTPUT_ENABLED = false;

	int numPoints;
	double alpha;
	double beta;
	int maxTime;

	ArrayList<Point> visit = new ArrayList<Point>();
	ArrayList<Point> buffer = new ArrayList<Point>();

	public GVE(int numPoints, double alpha, double beta, int maxTime) {
		Dataset.readPointsFromCSV();
		if (OUTPUT_ENABLED) System.out.println(
			Helpers.infoMessage +
			"Number of input points: " +
			Dataset.points.size()
		);

		this.numPoints = numPoints;
		this.alpha = alpha;
		this.beta = beta;
		this.maxTime = maxTime;

		visit.add(Dataset.points.get(0));
		buffer.add(Dataset.points.get(0));
	}

	public static void main(String[] args) throws Exception {
		Configuration.load(args[0], args[1]);

		if (!Configuration.CLUSTERER_USED.equals("gve")) {
			throw new Exception(
				"The GVE clusterer has not been set as the active clusterer in the configuration file"
			);
		}

		GVE cluster = new GVE(
			Configuration.NUM_POINTS,
			Configuration.ALPHA,
			Configuration.BETA,
			Configuration.MAX_TIME
		);

		cluster.OUTPUT_ENABLED = Configuration.OUTPUT_ENABLED;

		cluster.runCluster();

		if (Configuration.MERGE_TIME != 0) Helpers.mergeVisits(
			Configuration.MERGE_TIME
		);
		Dataset.writeVisitsToCSV();

		if (cluster.OUTPUT_ENABLED) System.out.println(
			Helpers.successMessage +
			"Number of clusters produced: " +
			Dataset.visits.size()
		);
	}

	public void runCluster() throws Exception {
		if (!(beta > numPoints)) {
			throw new Exception();
		}

		for (int i = 0; i < Dataset.points.size(); i++) {
			process(Dataset.points.get(i));
		}

		// Finish the last visit
		if (
			!visit.isEmpty() &&
			Helpers.timeBetween(visit.get(0), visit.get(visit.size() - 1)) > 0
		) {
			Dataset.visits.add(visit);
			if (OUTPUT_ENABLED) System.out.println(
				Helpers.infoMessage +
				"Visit " +
				Dataset.visits.size() +
				" added: comprised of " +
				visit.size() +
				" distinct points."
			);
		}
	}

	private void process(Point point) {
		buffer.add(point);

		if (buffer.size() > numPoints) {
			buffer.remove(0);
		}

		if (
			Helpers.timeBetween(visit.get(visit.size() - 1), point) > maxTime ||
			movingAway(visit, buffer)
		) {
			if (
				Helpers.timeBetween(visit.get(0), visit.get(visit.size() - 1)) >
				0
			) {
				Dataset.visits.add(visit);
				if (OUTPUT_ENABLED) System.out.println(
					Helpers.infoMessage +
					"Visit " +
					Dataset.visits.size() +
					" added: comprised of " +
					visit.size() +
					" distinct points."
				);
			} else if (Configuration.KEEP_POINTS) {
				Dataset.noise.add(visit);
				if (OUTPUT_ENABLED) System.out.println(
					Helpers.infoMessage +
					"Noise " +
					Dataset.noise.size() +
					" added: comprised of " +
					visit.size() +
					" distinct points."
				);
			}

			visit = new ArrayList<Point>();
			visit.add(point);

			buffer.clear();
			buffer.add(point);
		} else {
			visit.add(point);
		}
	}

	private boolean movingAway(
		ArrayList<Point> visit,
		ArrayList<Point> buffer
	) {
		return gradient(visit, buffer) > threshold(alpha, beta, buffer.size());
	}

	private double gradient(ArrayList<Point> visit, ArrayList<Point> buffer) {
		double topLeft = 0;
		double timeSum = 0;
		double timeSquaredSum = 0;
		double distanceSum = 0;

		for (int i = 0; i < buffer.size(); i++) {
			timeSum += Helpers.timeBetween(buffer.get(0), buffer.get(i));
			timeSquaredSum +=
				Math.pow(Helpers.timeBetween(buffer.get(0), buffer.get(i)), 2);
			distanceSum +=
				Helpers.distanceBetween(Helpers.centroid(visit), buffer.get(i));
			topLeft +=
				(
					Helpers.timeBetween(buffer.get(0), buffer.get(i)) *
					Helpers.distanceBetween(
						Helpers.centroid(visit),
						buffer.get(i)
					)
				);
		}

		double top = (buffer.size() * topLeft) - (timeSum * distanceSum);
		double bottom = buffer.size() * timeSquaredSum - Math.pow(timeSum, 2);

		return top / bottom;
	}

	private double threshold(double alpha, double beta, int bufferLength) {
		return -1.0 * Math.log(bufferLength * (1.0 / beta)) * alpha;
	}
}
