package it.polimi.elet.selflet.configuration;

/**
 * Class containing all configuration parameters used by the dispacher
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class DispatcherConfiguration extends Configuration {

	private static DispatcherConfiguration singleton;

	public static String username;
	public static String password;
	public static int initialNumberOfSelflets;
	public static String redsAddress;
	public static int redsPort;
	public static String dispatcherID;
	public static long stateMaximumAgeInSec;
	public static int nodeStateCleaningPeriod;
	public static int maxNeighborsPerSelflet;
	public static int neighborsUpdaterSleepPeriodInSec;
	public static int resultLoggerPeriodInSec;
	public static String csvFilePath;
	public static String monitoredServices;
	public static long minimumTimeToFreeInstantiationSlots;
	public static int maxInstantiationSlots;
	public static String defaultProjectTemplate;
	public static String completeProjectTemplate;
	public static String trackName;
	public static boolean isLocal;

	private DispatcherConfiguration() {
		// private constructor
	}

	public static DispatcherConfiguration getSingleton() {
		if (singleton == null) {
			singleton = new DispatcherConfiguration();
		}
		return singleton;
	}

}
