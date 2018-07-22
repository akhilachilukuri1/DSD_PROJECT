package Server.ServerFrontEnd;

import DcmsApp.*;
import java.util.*;
import java.util.logging.Level;

import Conf.Constants;
import Conf.LogManager;
import Conf.ServerCenterLocation;
import Conf.ServerOperations;

import java.net.*;
import Models.Record;
import Server.PrimaryServerImplementation.DcmsServerImpl;

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
	public static HashMap<String, DcmsServerImpl> serverMap;

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
		serverMap = new HashMap<>();
		requestId = 0;
		init();
	}

	public void init() {
		DcmsServerImpl mtlServer = new DcmsServerImpl(ServerCenterLocation.MTL);
		DcmsServerImpl lvlServer = new DcmsServerImpl(ServerCenterLocation.LVL);
		DcmsServerImpl ddoServer = new DcmsServerImpl(ServerCenterLocation.DDO);
		serverMap.put("MTL", mtlServer);
		serverMap.put("LVL", lvlServer);
		serverMap.put("DDO", ddoServer);
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
}