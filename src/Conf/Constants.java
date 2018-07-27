package Conf;

public class Constants {

	public static int TOTAL_SERVERS_COUNT = 3;
	
	public static int PRIMARY_SERVER_ID = 1;
	public static int REPLICA1_SERVER_ID = 2;
	public static int REPLICA2_SERVER_ID = 3;
	public static String CURRENT_SERVER_IP = "localhost";
	public static int CURRENT_SERVER_UDP_PORT = 3333;
	
	public static String FRONT_END_IP =  "localhost";
	public static int FRONT_END_UDP_PORT = 3232;

	public static int MULTICAST_PORT_NUMBER = 6779;
	public static String MULTICAST_IP_ADDRESS = "224.0.0.150";
	
	public static int CURRENT_PRIMARY_PORT_FOR_REPLICAS = 2323;
	
	public static int MAX_RETRY_ATTEMPT = 10;
	public static int RETRY_TIME = 5000;

	public static String RECEIVED_DATA_SEPERATOR = ",";
	public static String RESPONSE_DATA_SEPERATOR = "_";
	public static String PROJECT_DIR = System.getProperty("user.dir");
	public static String LOG_DIR = PROJECT_DIR + "\\Logs\\";

	
	public static final int PORT_FRONT_END_TO_LEADER = 1231; 
	public static final int PORT_LEADER_TO_BACKUPS = 1232; 
	public static final int PORT_BACKUPS_TO_LEADER = 1233;
	 
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
		Replica1(1), Replica2(2), Replica3(3);

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

	public enum Replica1_SERVER_ID {
		KM_MTL, KM_LVL, KM_DDO
	}

	public enum Replica2_SERVER_ID {
		KR_MTL, KR_LVL, KR_DDO
	}

}