package Conf;



public class Constants {
	
	public static int UDP_PORT_NUM_MTL = 9999;
	public static int UDP_PORT_NUM_LVL = 1111;
	public static int UDP_PORT_NUM_DDO = 7777;
	public static int TOTAL_SERVERS_COUNT = 3;
	
	public static String CURRENT_SERVER_IP = "localhost";
	public static int CURRENT_SERVER_UDP_PORT = 3333;
	
	public static String FRONT_END_IP =  "localhost";
	public static int FRONT_END_UDP_PORT = 4444;
	
	public static int MAX_RETRY_ATTEMPT = 10;
	public static int RETRY_TIME = 10;
	
	public static String RECEIVED_DATA_SEPERATOR = ",";
	public static String RESPONSE_DATA_SEPERATOR = "_";
	public static String PROJECT_DIR = System.getProperty("user.dir");
	public static String LOG_DIR = PROJECT_DIR + "\\Logs\\";

}