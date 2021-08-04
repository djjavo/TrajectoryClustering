import java.io.*;
import java.lang.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.*;

public class Dataset {

	static ArrayList<ArrayList<Point>> noise;
	static ArrayList<ArrayList<Point>> visits;
	static ArrayList<Point> points;

	static String outputHeader = "";

	public static void readPointsFromCSV() {
		noise = new ArrayList<ArrayList<Point>>();
		visits = new ArrayList<ArrayList<Point>>();
		points = new ArrayList<Point>();

		boolean firstPass = true;

		Path pathToFile = Paths.get(
			Configuration.DATA_PATH + "trajectories.csv"
		);

		try (
			BufferedReader br = Files.newBufferedReader(
				pathToFile,
				StandardCharsets.US_ASCII
			)
		) {
			String line = br.readLine();

			while (line != null) {
				String[] attributes = line.split(",");

				if (firstPass && Configuration.DATA_HEADER) {
					firstPass = false;
					outputHeader = line;
				} else {
					ArrayList<String> additionalData = new ArrayList<String>();

					for (int i = 3; i < attributes.length; i++) {
						additionalData.add(attributes[i]);
					}

					Point point = new Point(
						Double.parseDouble(attributes[1]),
						Double.parseDouble(attributes[2]),
						Double.parseDouble(attributes[0]),
						additionalData
					);

					points.add(point);
				}

				line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeVisitsToCSV() {
		try {
			PrintWriter pw = new PrintWriter(
				new FileWriter(Configuration.DATA_PATH + "clusters.csv")
			);

			if (Configuration.DATA_HEADER) {
				pw.printf("cluster,%s%n", outputHeader);
			}

			int i = 0;

			for (ArrayList<Point> visit : visits) {
				for (Point p : visit) {
					pw.printf(
						"%d, %f, %f, %f%s %n",
						i,
						p.getTimestamp(),
						p.getLatitude(),
						p.getLongitude(),
						p.printAdditionalData()
					);
				}

				i++;
			}

			if (Configuration.KEEP_POINTS) {
				for (ArrayList<Point> visit : noise) {
					for (Point p : visit) {
						pw.printf(
							"-1, %f, %f, %f%s %n",
							p.getTimestamp(),
							p.getLatitude(),
							p.getLongitude(),
							p.printAdditionalData()
						);
					}
				}
			}

			pw.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
