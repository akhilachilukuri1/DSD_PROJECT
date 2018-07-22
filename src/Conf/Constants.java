package Conf;

public class Constants {

	public static int TOTAL_SERVERS_COUNT = 3;

	public static String CURRENT_SERVER_IP = "localhost";
	public static int CURRENT_SERVER_UDP_PORT = 3333;

	public static String FRONT_END_IP = "localhost";
	public static int FRONT_END_UDP_PORT = 4444;

	public static int MULTICAST_PORT_NUMBER = 6789;
	public static String MULTICAST_IP_ADDRESS = "224.0.0.1";
	public static int MAX_RETRY_ATTEMPT = 10;
	public static int RETRY_TIME = 100;

	public static String RECEIVED_DATA_SEPERATOR = ",";
	public static String RESPONSE_DATA_SEPERATOR = "_";
	public static String PROJECT_DIR = System.getProperty("user.dir");
	public static String LOG_DIR = PROJECT_DIR + "\\Logs\\";

	/*
	 * public static final int PORT_FRONT_END_TO_LEADER = 1231; public static final
	 * int PORT_LEADER_TO_BACKUPS = 1232; public static final int
	 * PORT_BACKUPS_TO_LEADER = 1233;
	 */
	public static final int PORT_HEART_BEAT = 1234;
	public static final int PORT_ELECTION = 1235;
	public static final int PORT_NEW_LEADER = 1236;

	public static final int COEFFICIENT = 10;

	public static final String MESSAGE = "Election message";
	public static final String RESPONSE = "Election response";
	public static final String ANNOUNCEMENT = "%s,%s";
	public static final int ANSWER_TIMEOUT = 500;
	public static final int ELECTION_DELAY = 500;
	public static final int ELECTION_TIMEOUT = 500;

	public static final int HEART_BEAT_DELAY = 5000;
	public static final int HEART_BEAT_TIMEOUT = 6000;

	public enum REPLICAS {
		KAMAL(1), KEN_RO(2), MINH(3);

		private final int coefficient;

		REPLICAS(int coefficient) {
			this.coefficient = coefficient;
		}

		public int getCoefficient() {
			return coefficient;
		}

	}

	public enum SERVER_ID {
		MTL, LVL, DDO
	}

	public enum KAMAL_SERVER_ID {
		KM_MTL, KM_LVL, KM_DDO
	}

	public enum KENRO_SERVER_ID {
		KR_MTL, KR_LVL, KR_DDO
	}

}