package Server.ServerFrontEnd;

import DcmsApp.*;
import java.util.*;
import Conf.Constants;
import Conf.LogManager;
import Conf.ServerCenterLocation;
import Conf.ServerOperations;

import java.net.*;
import Models.Record;
import Server.ServerImplementation.*;

/**
 * 
 * DcmsServerImpl class includes all the server operations' implementations,
 * implements all the methods in the IDL interface Performs the necessary
 * operations and returns the result/acknowledgement back to the Client.
 *
 */

public class DcmsServerFE extends DcmsPOA {
	LogManager logManager;
	String IPaddress;
	public HashMap<String, List<Record>> recordsMap;
	int studentCount = 0;
	int teacherCount = 0;
	String recordsCount;
	String location;
	Integer requestId;
	HashMap<Integer, String> requestBuffer;
	ArrayList<TransferReqToCurrentServer> requests;
	public static HashMap<Integer,TransferResponseToFE> responses;
	public static ArrayList<String> receivedResponses;
	public static HashMap<String, DcmsServerImpl> primaryServerMap,replica1ServerMap;
	DcmsServerMultiCastReceiver primaryReceiver, replica1Receiver;
	DcmsServerReplicaResponseReceiver replicaResponseReceiver;
	public static HashMap<Integer, HashMap<String, DcmsServerImpl>> centralRepository;
	DcmsServerImpl s1,s2,s3;
	static boolean s1_MTL_sender_isAlive = true;
	static boolean s2_MTL_sender_isAlive = true;
	static boolean s3_MTL_sender_isAlive = true;
	static boolean s1_LVL_sender_isAlive = true;
	static boolean s2_LVL_sender_isAlive = true;
	static boolean s3_LVL_sender_isAlive = true;
	static boolean s1_DDO_sender_isAlive = true;
	static boolean s2_DDO_sender_isAlive = true;
	static boolean s3_DDO_sender_isAlive = true;

	static int TIME_OUT = 6000;
	static int LEADER_ID = 100;
	static int S1_ID = 1;
	static int S2_ID = 2;
	static int S3_ID = 3 ;
	static Object mapAccessor = new Object();
	static HashMap<String, Integer> currentIds = new HashMap<>();
	public static HashMap<String, Boolean> server_leader_status = new HashMap<>();
	public static HashMap<String, Long> server_last_updated_time = new HashMap<>();
	int s1_MTL_receive_port = 5431;
	int s2_MTL_receive_port = 5432;
	int s3_MTL_receive_port = 5433;
	int s1_LVL_receive_port = 5441;
	int s2_LVL_receive_port = 5442;
	int s3_LVL_receive_port = 5443;
	int s1_DDO_receive_port = 5451;
	int s2_DDO_receive_port = 5452;
	int s3_DDO_receive_port = 5453;
	//int s2_receive_port = 6543;
	//int s3_receive_port = 3332;
	String MTLserverName1 = "MTL1";
	String MTLserverName2 = "MTL2";
	String MTLserverName3 = "MTL3";
	String LVLserverName1 = "LVL1";
	String LVLserverName2 = "LVL2";
	String LVLserverName3 = "LVL3";
	String DDOserverName1 = "DDO1";
	String DDOserverName2 = "DDO2";
	String DDOserverName3 = "DDO3";

	/*
	 * DcmsServerImpl Constructor to initializes the variables used for the
	 * implementation
	 * 
	 * @param loc The server location for which the server implementation should be
	 * initialized
	 */
	public DcmsServerFE() {
		recordsMap = new HashMap<>();
		requests = new ArrayList<>();
		responses = new HashMap<>();
		requestBuffer = new HashMap<>();
		receivedResponses = new ArrayList<>();
		UDPReceiverFromFE udpReceiverFromFE = new UDPReceiverFromFE(requests);
		udpReceiverFromFE.start();
		UDPResponseReceiver udpResponse = new UDPResponseReceiver(responses);
		udpResponse.start();
		centralRepository = new HashMap<>();
		primaryServerMap = new HashMap<>();
		replica1ServerMap = new HashMap<>();
		requestId = 0;
		server_leader_status.put(MTLserverName1, true);
		server_leader_status.put(LVLserverName1, true);
		server_leader_status.put(DDOserverName1, true);
		server_leader_status.put(MTLserverName2, false);
		server_leader_status.put(LVLserverName2, false);
		server_leader_status.put(DDOserverName2, false);

		currentIds.put(MTLserverName1, LEADER_ID);
		currentIds.put(MTLserverName2, S2_ID);
		//currentIds.put(MTLserverName3, S3_ID);
		currentIds.put(LVLserverName1, LEADER_ID);
		currentIds.put(LVLserverName2, S2_ID);
		//currentIds.put(LVLserverName3, S3_ID);
		currentIds.put(DDOserverName1, LEADER_ID);
		currentIds.put(DDOserverName2, S2_ID);
		//currentIds.put(DDOserverName3, S3_ID);

		server_last_updated_time.put(MTLserverName1, System.nanoTime() / 1000000);
		server_last_updated_time.put(MTLserverName2, System.nanoTime() / 1000000);
		server_last_updated_time.put(MTLserverName3, System.nanoTime() / 1000000);
		server_last_updated_time.put(LVLserverName1, System.nanoTime() / 1000000);
		server_last_updated_time.put(LVLserverName2, System.nanoTime() / 1000000);
		server_last_updated_time.put(LVLserverName3, System.nanoTime() / 1000000);
		server_last_updated_time.put(DDOserverName1, System.nanoTime() / 1000000);
		server_last_updated_time.put(DDOserverName2, System.nanoTime() / 1000000);
		server_last_updated_time.put(DDOserverName3, System.nanoTime() / 1000000);
		init();
	}

	public void init() {
		try {
		ArrayList<Integer> replicas =new ArrayList<>();
		replicas.add(Constants.REPLICA1_SERVER_ID);
		boolean isPrimary = true;
		primaryReceiver = new DcmsServerMultiCastReceiver(isPrimary);
		primaryReceiver.start();
		
		replicaResponseReceiver = new DcmsServerReplicaResponseReceiver();
		replicaResponseReceiver.start();
		DatagramSocket socket1 = new DatagramSocket();
		DcmsServerImpl primaryMtlServer = new DcmsServerImpl(Constants.PRIMARY_SERVER_ID,isPrimary, ServerCenterLocation.MTL,9999,socket1, s1_MTL_sender_isAlive, MTLserverName1, s1_MTL_receive_port, s2_MTL_receive_port,0,replicas);
		DcmsServerImpl primaryLvlServer = new DcmsServerImpl(Constants.PRIMARY_SERVER_ID,isPrimary, ServerCenterLocation.LVL,7777,socket1, s1_LVL_sender_isAlive, LVLserverName1, s1_LVL_receive_port, s2_LVL_receive_port,0,replicas);
		DcmsServerImpl primaryDdoServer = new DcmsServerImpl(Constants.PRIMARY_SERVER_ID,isPrimary, ServerCenterLocation.DDO,6666,socket1, s1_DDO_sender_isAlive, DDOserverName1, s1_DDO_receive_port, s2_DDO_receive_port,0,replicas);
		primaryServerMap.put("MTL", primaryMtlServer);
		primaryServerMap.put("LVL", primaryLvlServer);
		primaryServerMap.put("DDO", primaryDdoServer);

		replica1Receiver = new DcmsServerMultiCastReceiver(false);
		replica1Receiver.start();
		DatagramSocket socket2 = new DatagramSocket();
		DcmsServerImpl replica1MtlServer = new DcmsServerImpl(Constants.REPLICA1_SERVER_ID,false, ServerCenterLocation.MTL,5555,socket2, s2_MTL_sender_isAlive, MTLserverName2, s2_MTL_receive_port, s1_MTL_receive_port,0,replicas);
		DcmsServerImpl replica1LvlServer = new DcmsServerImpl(Constants.REPLICA1_SERVER_ID,false, ServerCenterLocation.LVL,4444,socket2, s2_LVL_sender_isAlive, LVLserverName2, s2_LVL_receive_port, s1_LVL_receive_port,0,replicas);
		DcmsServerImpl replica1DdoServer = new DcmsServerImpl(Constants.REPLICA1_SERVER_ID,false, ServerCenterLocation.DDO,2222,socket2, s2_DDO_sender_isAlive, DDOserverName2, s2_DDO_receive_port, s1_DDO_receive_port,0,replicas);
		replica1ServerMap.put("MTL", replica1MtlServer);
		replica1ServerMap.put("LVL", replica1LvlServer);
		replica1ServerMap.put("DDO", replica1DdoServer);
		
		centralRepository.put(Constants.PRIMARY_SERVER_ID, primaryServerMap);
		centralRepository.put(Constants.REPLICA1_SERVER_ID, replica1ServerMap);
		
			
			Thread thread1 = new Thread() {
				public void run() {
					while (getStatus(MTLserverName1)) {
						primaryMtlServer.send();
					}
				}
			};
			Thread thread2 = new Thread() {
				public void run() {
					while (getStatus(MTLserverName2)) {
						replica1MtlServer.send();
					}
				}
			};
			Thread thread3 = new Thread() {
				public void run() {
					while (getStatus(LVLserverName1)) {
						primaryLvlServer.send();
					}
				}
			};
			Thread thread4 = new Thread() {
				public void run() {
					while (getStatus(LVLserverName2)) {
						replica1LvlServer.send();
					}
				}
			};
			Thread thread5 = new Thread() {
				public void run() {
					while (getStatus(DDOserverName1)) {
						primaryDdoServer.send();
					}
				}
			};
			Thread thread6 = new Thread() {
				public void run() {
					while (getStatus(DDOserverName2)) {
						replica1DdoServer.send();
					}
				}
			};
			thread1.start();
			thread2.start();
			thread3.start();
			thread4.start();
			thread5.start();
			thread6.start();

			Thread statusChecker = new Thread() {
				public void run() {
					while (true) {
						checkServerStatus("MTL1");
						checkServerStatus("MTL2");
						checkServerStatus("LVL1");
						checkServerStatus("LVL2");
						checkServerStatus("DDO1");
						checkServerStatus("DDO2");
					}
				}
			};

			statusChecker.start();

			try {
				Thread.sleep(10000);
				//setStatus();
				//primaryMtlServer.receiver.setStatus(false);
				//primaryLvlServer.receiver.setStatus(false);
				//replica1DdoServer.receiver.setStatus(false);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {

		}
	}

	/**
	 * Once the teacher record is created, createTRRecord function returns the
	 * record ID of the teacher record created to the client
	 * 
	 * @param managerID
	 *            gets the managerID
	 * @param teacherField
	 *            values of the teacher attribute concatenated by the comma which
	 *            are received from the client
	 * 
	 */

	@Override
	public String createTRecord(String managerID, String teacher) {
		teacher = ServerOperations.CREATE_T_RECORD + 
				Constants.RECEIVED_DATA_SEPERATOR + getServerLoc(managerID)
				+ Constants.RECEIVED_DATA_SEPERATOR + managerID + 
				Constants.RECEIVED_DATA_SEPERATOR + teacher;
		return sendRequestToServer(teacher);
	}

	private String getServerLoc(String managerID) {
		return managerID.substring(0, 3);
	}

	/**
	 * Once the student record is created, the function createSRecord returns the
	 * record ID of the student record created to the client
	 * 
	 * @param managerID
	 *            gets the managerID
	 * @param studentFields
	 *            values of the student attribute concatenated by the comma which
	 *            are received the client
	 * 
	 */

	@Override
	public String createSRecord(String managerID, String student) {
		student = ServerOperations.CREATE_S_RECORD + Constants.RECEIVED_DATA_SEPERATOR + getServerLoc(managerID)
				+ Constants.RECEIVED_DATA_SEPERATOR + managerID + Constants.RECEIVED_DATA_SEPERATOR + student;
		return sendRequestToServer(student);
	}

	/**
	 * Invokes record count request on MTL/LVL/DDO server to get record count from
	 * all the servers Creates UDPRequest Provider objects for each request and
	 * creates separate thread for each request. And makes sure each thread is
	 * complete and returns the result
	 */

	@Override
	public String getRecordCount(String managerID) {
		String req = ServerOperations.GET_REC_COUNT + Constants.RECEIVED_DATA_SEPERATOR + getServerLoc(managerID)
				+ Constants.RECEIVED_DATA_SEPERATOR + managerID;
		return sendRequestToServer(req);
	}

	/**
	 * The edit record function performs the edit operation on the server and
	 * returns the appropriate message
	 * 
	 * @param managerID
	 *            gets the managerID
	 * @param recordID
	 *            gets the recordID to be edited
	 * @param fieldname
	 *            gets the fieldname to be edited for the given recordID
	 * @param newvalue
	 *            gets the newvalue to be replaced to the given fieldname from the
	 *            client
	 */

	@Override
	public String editRecord(String managerID, String recordID, String fieldname, String newvalue) {
		String editData = ServerOperations.EDIT_RECORD + Constants.RECEIVED_DATA_SEPERATOR + getServerLoc(managerID)
				+ Constants.RECEIVED_DATA_SEPERATOR + managerID + Constants.RECEIVED_DATA_SEPERATOR + recordID
				+ Constants.RECEIVED_DATA_SEPERATOR + fieldname + Constants.RECEIVED_DATA_SEPERATOR + newvalue;
		return sendRequestToServer(editData);
	}

	/**
	 * Performs the transfer record to the remoteCenterServer by sending the
	 * appropriate packet to the DcmsServerUDPRequestProvider thread Creates
	 * UDPRequest Provider objects for each request and creates separate thread for
	 * each request. And makes sure each thread is complete and returns the result
	 * 
	 * @param managerID
	 *            gets the managerID
	 * @param recordID
	 *            gets the recordID to be edited
	 * @param remoteCenterServerName
	 *            gets the location to transfer the recordID from the client
	 */
	public String transferRecord(String managerID, String recordID, String remoteCenterServerName) {
		String req = ServerOperations.TRANSFER_RECORD + Constants.RECEIVED_DATA_SEPERATOR + getServerLoc(managerID)
				+ Constants.RECEIVED_DATA_SEPERATOR + managerID + Constants.RECEIVED_DATA_SEPERATOR + recordID
				+ Constants.RECEIVED_DATA_SEPERATOR + remoteCenterServerName;
		return sendRequestToServer(req);
	}

	public String sendRequestToServer(String data) {
		try {
			requestId += 1;
			DatagramSocket ds = new DatagramSocket();
			data = data + Constants.RECEIVED_DATA_SEPERATOR+ Integer.toString(requestId);
			byte[] dataBytes = data.getBytes();
			DatagramPacket dp = new DatagramPacket(dataBytes, dataBytes.length,
					InetAddress.getByName(Constants.CURRENT_SERVER_IP)
					, Constants.CURRENT_SERVER_UDP_PORT);
			ds.send(dp);
			System.out.println("Adding request to request buffer with req id..."+requestId);
			requestBuffer.put(requestId, data);
			//waitForAck();
			System.out.println("Waiting for acknowledgement from current server...");
			Thread.sleep(Constants.RETRY_TIME);
			return getResponse(requestId);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return e.getMessage();
		}
	}

	public String getResponse(Integer requestId) {
		try {
			responses.get(requestId).join();
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());		
		}
		requestBuffer.remove(requestId);
		return responses.get(requestId).getResponse();
	}

	private void waitForAck() {
		int counter = 0;
		while (true) {
			if (counter >= Constants.MAX_RETRY_ATTEMPT) {
				break;
			} else {
				try {
					System.out.println("Waiting");
					Thread.sleep(Constants.RETRY_TIME);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				//sendRequestToServer(requestBuffer.get(requestId));
				counter++;
			}
		}
	}
	private static void setStatus() {
		s1_MTL_sender_isAlive = false;
		//s1_LVL_sender_isAlive = false;
		//s2_DDO_sender_isAlive = false;
	}

	private static synchronized void checkServerStatus(String serverName) {
		synchronized (mapAccessor) {
			long currentTime = System.nanoTime() / 1000000;
			if(server_last_updated_time.containsKey(serverName)) {
				if (currentTime - server_last_updated_time.get(serverName) > TIME_OUT) {
					//System.out.println(serverName + "Failed");
					if(server_leader_status.get(serverName)) {
						System.out.println(serverName+ " Leader Failed Found!!!");
						electNewLeader(serverName);
					}
				}
			}
		}
	}

	private static void electNewLeader(String oldLeader) {
		server_leader_status.remove(oldLeader);
		server_last_updated_time.remove(oldLeader);
		currentIds.remove(oldLeader);
		String loc=oldLeader.substring(0, 3);
		Map.Entry<String, Integer> maxEntry = null;
		for (Map.Entry<String, Integer> entry : currentIds.entrySet())
		{
			if(entry.getKey().contains(loc))
			{
		    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
		    {
		        maxEntry = entry;
		    }
		}
		}
		server_leader_status.put(maxEntry.getKey(),true);
		currentIds.put(maxEntry.getKey(), LEADER_ID);
		System.out.println("++++Elected new leader :: "+maxEntry.getKey()+" in the location"+loc);
	}
	private static boolean getStatus(String name) {
		if (name.equals("MTL1")) {
			return s1_MTL_sender_isAlive;
		} else if (name.equals("MTL2")) {
			return s2_MTL_sender_isAlive;
		} else if (name.equals("MTL3")) {
			return s3_MTL_sender_isAlive;
		}else if (name.equals("LVL1")) {
				return s1_LVL_sender_isAlive;
			} else if (name.equals("LVL2")) {
				return s2_LVL_sender_isAlive;
			} else if (name.equals("LVL3")) {
				return s3_LVL_sender_isAlive;
			}else if (name.equals("DDO1")) {
					return s1_DDO_sender_isAlive;
				} else if (name.equals("DDO2")) {
					return s2_DDO_sender_isAlive;
				} else if (name.equals("DDO3")) {
					return s3_DDO_sender_isAlive;
				}
		return false;
	}
}