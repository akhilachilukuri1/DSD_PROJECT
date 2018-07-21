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
		UDPReceiverFromFE udpReceiverFromFE = new UDPReceiverFromFE();
		udpReceiverFromFE.start();
		serverMap = new HashMap<>();
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
		teacher = ServerOperations.CREATE_T_RECORD + Constants.RECEIVED_DATA_SEPERATOR
				+ getServerLoc(managerID)+Constants.RECEIVED_DATA_SEPERATOR+managerID+
				Constants.RECEIVED_DATA_SEPERATOR+teacher;
		sendRequestToServer(teacher);
		return "...processing";

	}
	
	private String getServerLoc(String managerID) {
	       return managerID.substring(0,3);
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
		student = ServerOperations.CREATE_S_RECORD + Constants.RECEIVED_DATA_SEPERATOR
				+ getServerLoc(managerID)+Constants.RECEIVED_DATA_SEPERATOR+managerID+
				Constants.RECEIVED_DATA_SEPERATOR+student;
		sendRequestToServer(student);
		return "...processing";
	}

	/**
	 * Invokes record count request on MTL/LVL/DDO server to get record count from
	 * all the servers Creates UDPRequest Provider objects for each request and
	 * creates separate thread for each request. And makes sure each thread is
	 * complete and returns the result
	 */

	@Override
	public String getRecordCount(String managerID) {
		String req = ServerOperations.GET_REC_COUNT + 
				Constants.RECEIVED_DATA_SEPERATOR+getServerLoc(managerID)+
				Constants.RECEIVED_DATA_SEPERATOR+managerID;
		sendRequestToServer(req);
		return "...processing";
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
	public String editRecord(String managerID, String recordID, String fieldname,
			String newvalue) {
		String editData = ServerOperations.EDIT_RECORD + 
				Constants.RECEIVED_DATA_SEPERATOR + getServerLoc(managerID)+
				Constants.RECEIVED_DATA_SEPERATOR+managerID+
				Constants.RECEIVED_DATA_SEPERATOR+recordID+
				Constants.RECEIVED_DATA_SEPERATOR+fieldname+
				Constants.RECEIVED_DATA_SEPERATOR+newvalue;
		sendRequestToServer(editData);
		return "...processing";
	}

	/**
	 * Performs the transfer record to the remoteCenterServer by sending the
	 * appropriate packet to the DcmsServerUDPRequestProvider thread Creates UDPRequest
	 * Provider objects for each request and creates separate thread for each
	 * request. And makes sure each thread is complete and returns the result
	 * 
	 * @param managerID
	 *            gets the managerID
	 * @param recordID
	 *            gets the recordID to be edited
	 * @param remoteCenterServerName
	 *            gets the location to transfer the recordID from the client
	 */
	public String transferRecord(String managerID, String recordID,
			String remoteCenterServerName) {
		String req = ServerOperations.TRANSFER_RECORD + 
				Constants.RECEIVED_DATA_SEPERATOR+getServerLoc(managerID)+
				Constants.RECEIVED_DATA_SEPERATOR+managerID+
				Constants.RECEIVED_DATA_SEPERATOR+recordID+
				Constants.RECEIVED_DATA_SEPERATOR+remoteCenterServerName;
		sendRequestToServer(req);
		return "...processing";	}
	
	public void sendRequestToServer(String data) {
		 try {
			DatagramSocket ds = new DatagramSocket();
			byte[] dataBytes = data.getBytes();
			DatagramPacket dp = new DatagramPacket(dataBytes, dataBytes.length, InetAddress.getByName(Constants.CURRENT_SERVER_IP),
			         Constants.CURRENT_SERVER_UDP_PORT);
			ds.send(dp);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}