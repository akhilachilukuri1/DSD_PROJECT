package Conf;



public class Constants {
	
	public static int TOTAL_SERVERS_COUNT = 3;
	
	public static String CURRENT_SERVER_IP = "localhost";
	public static int CURRENT_SERVER_UDP_PORT = 3333;
	
	public static String FRONT_END_IP =  "localhost";
	public static int FRONT_END_UDP_PORT = 4444;

	public static int MULTICAST_PORT_NUMBER = 6789;
	public static String MULTICAST_IP_ADDRESS = "224.0.0.1";
	public static int MAX_RETRY_ATTEMPT = 10;
	public static int RETRY_TIME = 100;
	
	public static String RECEIVED_DATA_SEPERATOR = ",";
	public static String RESPONSE_DATA_SEPERATOR = "_";
	public static String PROJECT_DIR = System.getProperty("user.dir");
	public static String LOG_DIR = PROJECT_DIR + "\\Logs\\";

}