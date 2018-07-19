package Server;

import DcmsApp.*;
import java.util.*;
import java.util.logging.Level;
import Conf.LogManager;
import Models.Record;

/**
 * 
 * DcmsServerImpl class includes all the server operations' implementations,
 * implements all the methods in the IDL interface Performs the necessary
 * operations and returns the result/acknowledgement back to the Client.
 *
 */

class DcmsServerFE extends DcmsPOA {
	LogManager logManager;
	ServerUDP serverUDP;
	String IPaddress;
	public HashMap<String, List<Record>> recordsMap;
	int studentCount = 0;
	int teacherCount = 0;
	String recordsCount;
	String location;

	/*
	 * DcmsServerImpl Constructor to initializes the variables used for the
	 * implementation
	 * 
	 * @param loc The server location for which the server implementation should be
	 * initialized
	 */
	public DcmsServerFE() {
		recordsMap = new HashMap<>();
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
		return "CT";

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
		return "CS";
	}

	/**
	 * Invokes record count request on MTL/LVL/DDO server to get record count from
	 * all the servers Creates UDPRequest Provider objects for each request and
	 * creates separate thread for each request. And makes sure each thread is
	 * complete and returns the result
	 */

	@Override
	public String getRecordCount() {
		return "0";
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
		logManager.logger.log(Level.INFO, "Record edit successful");
		return "Operation not performed!";
	}

	/**
	 * Performs the transfer record to the remoteCenterServer by sending the
	 * appropriate packet to the UDPRequestProvider thread Creates UDPRequest
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
	public String transferRecord(String ManagerID, String recordID,
			String remoteCenterServerName) {
		return "Transfer record operation unsuccessful!";
	}
}