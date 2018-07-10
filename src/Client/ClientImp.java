package Client;

import java.io.File;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import DcmsApp.Dcms;
import DcmsApp.DcmsHelper;
import Conf.LogManager;
import Conf.*;
/*Implementation of Client class*/

public class ClientImp {
	LogManager logManager = null;
	Dcms serverLoc = null;
	static NamingContextExt ncRef = null;

	/**
	 * 
	 * creates the client instance with
	 * 
	 * @param args
	 *            gets the port number and IP address and creates the ORB object
	 *            with it.
	 * @param location
	 *            gets the location of the client,based on the location the
	 *            appropriate server instance is called to perform the operation
	 *            requested by the manager.
	 * @param ManagerID
	 *            creates the log file with the managerID.
	 */
	ClientImp(String[] args, ServerCenterLocation location, String ManagerID) {
		try {
			//Initialize the ORB service with the given input arguments
			//Host name and port number
			ORB orb = ORB.init(args, null);
			//Resolve the naming context reference to know which path to look into
			org.omg.CORBA.Object objRef = orb
					.resolve_initial_references("NameService");
			//Cast the naming context corba object reference to java object
			ncRef = NamingContextExtHelper.narrow(objRef);

			//Resolve the name and get the object reference to invoke servant methods
			if (location == ServerCenterLocation.MTL) {
				serverLoc = DcmsHelper.narrow(ncRef.resolve_str("MTL"));
			} else if (location == ServerCenterLocation.LVL) {
				serverLoc = DcmsHelper.narrow(ncRef.resolve_str("LVL"));
			} else if (location == ServerCenterLocation.DDO) {
				serverLoc = DcmsHelper.narrow(ncRef.resolve_str("DDO"));
			}
			boolean mgrID = new File(Constants.LOG_DIR + ManagerID).mkdir();
			logManager = new LogManager(ManagerID);
		} catch (Exception e) {
			System.out.println("ERROR : " + e);
			e.printStackTrace(System.out);
		}
	}

	/**
	 * If the teacher record is created, It Displays the record ID of the teacher
	 * record created on the server with the values given by the manager. If the
	 * teacher record is not created it displays the message.
	 * 
	 * @param managerID
	 *            gets the managerID
	 * @param teacherField
	 *            values of the teacher attribute concatenated by the comma and are
	 *            sent to the server
	 * 
	 */
	public String createTRecord(String managerID, String teacherField) {
		logManager.logger.log(Level.INFO,
				"Initiating T record object creation request");
		String result = "";
		String teacherID = "";
		teacherID = serverLoc.createTRecord(managerID, teacherField);
		if (teacherID != null)
			result = "Teacher record is created and assigned with " + teacherID;
		else
			result = "Teacher record is not created";
		logManager.logger.log(Level.INFO, result);
		return result;
	}

	/**
	 * If the student record is created, It Displays the record ID of the student
	 * record created on the server with the values given by the manager. If the
	 * teacher record is not created it displays the message.
	 * 
	 * @param managerID
	 *            gets the managerID
	 * @param studentFields
	 *            values of the student attribute concatenated by the comma and are
	 *            sent to the server
	 * 
	 */
	public String createSRecord(String managerID, String studentFields) {
		logManager.logger.log(Level.INFO,
				"Initiating S record object creation request");
		String result = "";
		String studentID = "";
		studentID = serverLoc.createSRecord(managerID, studentFields);
		if (studentID != null)
			result = "student record is created and assigned with " + studentID;
		else
			result = "student record is not created";
		logManager.logger.log(Level.INFO, result);
		return result;
	}

	/**
	 *
	 *
	 * invokes record count on the corresponding server to get record count on all
	 * the servers
	 * 
	 */
	public String getRecordCounts() {
		String count = "";
		logManager.logger.log(Level.INFO, "Initiating record count request");
		count = serverLoc.getRecordCount();
		logManager.logger.log(Level.INFO, "received....count as follows");
		logManager.logger.log(Level.INFO, count);
		return count;
	}

	/**
	 * invokes edit record on the server return the appropriate message
	 * 
	 * @param managerID
	 *            gets the managerID
	 * @param recordID
	 *            gets the recordID to be edited
	 * @param location
	 *            gets the location to transfer the recordID
	 */
	public String transferRecord(String ManagerID, String recordID,
			String location) {
		String message = "";
		logManager.logger.log(Level.INFO, "Initiating the record transfer request");
		message = serverLoc.transferRecord(ManagerID, recordID, location);
		System.out.println(message);
		logManager.logger.log(Level.INFO, message);
		return message;
	}

	/**
	 * invokes edit record on the server return the appropriate message
	 * 
	 * @param managerID
	 *            gets the managerID
	 * @param recordID
	 *            gets the recordID to be edited
	 * @param fieldname
	 *            gets the fieldname to be edited for the given recordID
	 * @param newvalue
	 *            gets the newvalue to be replaced to the given fieldname
	 */
	public String editRecord(String managerID, String recordID, String fieldname,
			String newvalue) {
		String message = "";
		logManager.logger.log(Level.INFO,
				managerID + "has Initiated the record edit request for " + recordID);
		message = serverLoc.editRecord(managerID, recordID, fieldname, newvalue);
		// System.out.println(message);
		logManager.logger.log(Level.INFO, message);
		return message;
	}
}