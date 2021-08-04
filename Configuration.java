import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Configuration {

	private static Properties appConfig;
	private static FileInputStream input;

	public static boolean DEBUG;
	public static boolean OUTPUT_ENABLED;

	public static String DATA_PATH;
	public static boolean DATA_HEADER;

	public static String CLUSTERER_USED;

	public static boolean KEEP_POINTS;

	public static double ALPHA;
	public static double BETA;
	public static int NUM_POINTS;
	public static int MAX_TIME;

	public static double DISTANCE_THRESHOLD;

	public static int MERGE_TIME;

	public static void load(String dataPath, String configFile) {
		try {
			appConfig = new Properties();
			input = new FileInputStream(configFile);

			appConfig.load(input);

			DEBUG = Boolean.parseBoolean((String) appConfig.get("debug"));
			OUTPUT_ENABLED =
				Boolean.parseBoolean((String) appConfig.get("output.enabled"));

			DATA_PATH = dataPath;

			if (DEBUG) System.out.println(
				"[CONFIG]\tConfiguration file: " + configFile
			);

			processSettings();

			CLUSTERER_USED = (String) appConfig.get("clusterer.used");
			if (CLUSTERER_USED.equals("gve")) {
				processGVE();
			} else if (CLUSTERER_USED.equals("sta")) {
				processSTA();
			}

			input.close();

			printSummary();
		} catch (IOException e) {
			System.out.println("Could not load configuration file.");
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}

	public static void processSettings() {
		DATA_HEADER =
			Boolean.parseBoolean((String) appConfig.get("data.header"));
		MERGE_TIME = Integer.parseInt((String) appConfig.get("merge.time"));
		KEEP_POINTS =
			Boolean.parseBoolean((String) appConfig.get("keep.points"));
	}

	public static void processGVE() {
		ALPHA = Double.parseDouble((String) appConfig.get("alpha"));
		BETA = Double.parseDouble((String) appConfig.get("beta"));
		NUM_POINTS = Integer.parseInt((String) appConfig.get("num.points.gve"));
		MAX_TIME = Integer.parseInt((String) appConfig.get("t.max"));
	}

	public static void processSTA() {
		DISTANCE_THRESHOLD =
			Double.parseDouble((String) appConfig.get("distance.threshold"));
		NUM_POINTS = Integer.parseInt((String) appConfig.get("num.points.sta"));
	}

	public static void printSummary() {
		if (DEBUG) {
			System.out.println("[CONFIG]\tnum_points: " + NUM_POINTS);
			System.out.println("[CONFIG]\talpha: " + ALPHA);
			System.out.println("[CONFIG]\tbeta: " + BETA);
			System.out.println("[CONFIG]\tt_max: " + MAX_TIME);

			System.out.println("[CONFIG]\tMerge threshold: " + MERGE_TIME);

			System.out.println("[CONFIG]\tData header: " + DATA_HEADER);
			System.out.println("[CONFIG]\tOutput enabled: " + OUTPUT_ENABLED);
		}
	}
}
