package Server.ServerImplementation;

import DcmsApp.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import Conf.Constants;
import Conf.LogManager;
import Conf.ServerOperations;

import Models.Record;

/**
 * 
 * DcmsServerImpl class includes all the server operations' implementations,
 * implements all the methods in the IDL interface Performs the necessary
 * operations and returns the result/acknowledgement back to the Client.
 *
 */

public class DcmsServerPrepareReplicasRequest extends DcmsPOA {
	LogManager logManager;
	Logger logger;
	String IPaddress;
	public HashMap<String, List<Record>> recordsMap;
	int studentCount = 0;
	int teacherCount = 0;
	String recordsCount;
	String location;
	Integer requestId;
	HashMap<Integer, String> requestBuffer;
	Integer replicaID;

	/*
	 * DcmsServerImpl Constructor to initializes the variables used for the
	 * implementation
	 * 
	 * @param loc The server location for which the server implementation should
	 * be initialized
	 */
	public DcmsServerPrepareReplicasRequest(Integer replicaID, Logger logger) {
		recordsMap = new HashMap<>();
		requestBuffer = new HashMap<>();
		requestId = 0;
		this.replicaID = replicaID;
		this.logger = logger;
	}

	/**
	 * Once the teacher record is created, createTRRecord function returns the
	 * record ID of the teacher record created to the client
	 * 
	 * @param managerID
	 *            gets the managerID
	 * @param teacherField
	 *            values of the teacher attribute concatenated by the comma
	 *            which are received from the client
	 * 
	 */

	private void sendMulticastRequest(String req) {
		DcmsServerMultiCastSender sender = new DcmsServerMultiCastSender(req, logger);
		sender.start();
	}

	@Override
	public String createTRecord(String managerID, String teacher) {
		teacher = Integer.toString(replicaID) + Constants.RECEIVED_DATA_SEPERATOR + ServerOperations.CREATE_T_RECORD
				+ Constants.RECEIVED_DATA_SEPERATOR + getServerLoc(managerID) + Constants.RECEIVED_DATA_SEPERATOR
				+ managerID + Constants.RECEIVED_DATA_SEPERATOR + teacher;
		logger.log(Level.INFO, "Preparing Multicast request for Create Teacher record : " + teacher);
		sendMulticastRequest(teacher);
		return "";
	}

	private String getServerLoc(String managerID) {
		return managerID.substring(0, 3);
	}

	/**
	 * Once the student record is created, the function createSRecord returns
	 * the record ID of the student record created to the client
	 * 
	 * @param managerID
	 *            gets the managerID
	 * @param studentFields
	 *            values of the student attribute concatenated by the comma
	 *            which are received the client
	 * 
	 */

	@Override
	public String createSRecord(String managerID, String student) {
		student = Integer.toString(replicaID) + Constants.RECEIVED_DATA_SEPERATOR + ServerOperations.CREATE_S_RECORD
				+ Constants.RECEIVED_DATA_SEPERATOR + getServerLoc(managerID) + Constants.RECEIVED_DATA_SEPERATOR
				+ managerID + Constants.RECEIVED_DATA_SEPERATOR + student;
		sendMulticastRequest(student);
		logger.log(Level.INFO, "Preparing Multicast request for Create Student record : " + student);
		return "";
	}

	/**
	 * Invokes record count request on MTL/LVL/DDO server to get record count
	 * from all the servers Creates UDPRequest Provider objects for each request
	 * and creates separate thread for each request. And makes sure each thread
	 * is complete and returns the result
	 */

	@Override
	public String getRecordCount(String manager) {
		String data[] = manager.split(Constants.RECEIVED_DATA_SEPERATOR);
		String req = Integer.toString(replicaID) + Constants.RECEIVED_DATA_SEPERATOR + ServerOperations.GET_REC_COUNT
				+ Constants.RECEIVED_DATA_SEPERATOR + getServerLoc(data[0]) + Constants.RECEIVED_DATA_SEPERATOR
				+ manager;
		sendMulticastRequest(req);
		logger.log(Level.INFO, "Preparing Multicast request for get record Count :" + req);
		return "";
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
	 *            gets the newvalue to be replaced to the given fieldname from
	 *            the client
	 */

	@Override
	public String editRecord(String managerID, String recordID, String fieldname, String newvalue) {
		String editData = Integer.toString(replicaID) + Constants.RECEIVED_DATA_SEPERATOR + ServerOperations.EDIT_RECORD
				+ Constants.RECEIVED_DATA_SEPERATOR + getServerLoc(managerID) + Constants.RECEIVED_DATA_SEPERATOR
				+ managerID + Constants.RECEIVED_DATA_SEPERATOR + recordID + Constants.RECEIVED_DATA_SEPERATOR
				+ fieldname + Constants.RECEIVED_DATA_SEPERATOR + newvalue;
		sendMulticastRequest(editData);
		logger.log(Level.INFO, "Preparing Multicast request for editRecord : " + editData);
		return "";
	}

	/**
	 * Performs the transfer record to the remoteCenterServer by sending the
	 * appropriate packet to the DcmsServerUDPRequestProvider thread Creates
	 * UDPRequest Provider objects for each request and creates separate thread
	 * for each request. And makes sure each thread is complete and returns the
	 * result
	 * 
	 * @param managerID
	 *            gets the managerID
	 * @param recordID
	 *            gets the recordID to be edited
	 * @param remoteCenterServerName
	 *            gets the location to transfer the recordID from the client
	 */
	public String transferRecord(String managerID, String recordID, String remoteCenterServerName) {
		String req = Integer.toString(replicaID) + Constants.RECEIVED_DATA_SEPERATOR + ServerOperations.TRANSFER_RECORD
				+ Constants.RECEIVED_DATA_SEPERATOR + getServerLoc(managerID) + Constants.RECEIVED_DATA_SEPERATOR
				+ managerID + Constants.RECEIVED_DATA_SEPERATOR + recordID + Constants.RECEIVED_DATA_SEPERATOR
				+ remoteCenterServerName;
		sendMulticastRequest(req);
		logger.log(Level.INFO, "Preparing Multicast request for transferRecord : " + req);
		return "";
	}

	@Override
	public String killServer(String location) {
		return null;
	}
}