package Server.ServerMain;

import DcmsApp.*;
import org.omg.CosNaming.*;
import java.io.File;
import java.io.IOException;
import Conf.Constants;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;
import Server.ServerFrontEnd.DcmsServerFE;
import Conf.ServerCenterLocation;

/**
 * DcmsServer class creates the CORBA server instance for the current project
 * and establishes the initial set of communication between the client module
 * and the server module for performing various operations
 */
public class DcmsServerMain {
	static Dcms fehref;

	static {
		try {
			Runtime.getRuntime()
					.exec("orbd -ORBInitialPort 1050 -ORBInitialHost localhost");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("CORBA Service Started!");
	}

	/** 
	 * In order to store the events and actions taking place this function 
	 * creates and initializes the log directories in the server side One log
	 * directory per location to separate the events
	 */
	private static void init() {
		new File(Constants.LOG_DIR + "ServerFE").mkdir();
		
		new File(Constants.LOG_DIR + "PRIMARY_SERVER").mkdir();
		new File(Constants.LOG_DIR + "PRIMARY_SERVER"+ "\\"+ServerCenterLocation.MTL.toString()).mkdir();
		new File(Constants.LOG_DIR + "PRIMARY_SERVER"+ "\\"+ServerCenterLocation.LVL.toString()).mkdir();
		new File(Constants.LOG_DIR + "PRIMARY_SERVER"+ "\\"+ServerCenterLocation.DDO.toString()).mkdir();

		new File(Constants.LOG_DIR + "REPLICA1_SERVER").mkdir();
		new File(Constants.LOG_DIR + "REPLICA1_SERVER"+ "\\"+ServerCenterLocation.MTL.toString()).mkdir();
		new File(Constants.LOG_DIR + "REPLICA1_SERVER"+ "\\"+ServerCenterLocation.LVL.toString()).mkdir();
		new File(Constants.LOG_DIR + "REPLICA1_SERVER"+ "\\"+ServerCenterLocation.DDO.toString()).mkdir();
		
		new File(Constants.LOG_DIR + "REPLICA2_SERVER").mkdir();
		new File(Constants.LOG_DIR + "REPLICA2_SERVER"+ "\\"+ServerCenterLocation.MTL.toString()).mkdir();
		new File(Constants.LOG_DIR + "REPLICA2_SERVER"+ "\\"+ServerCenterLocation.LVL.toString()).mkdir();
		new File(Constants.LOG_DIR + "REPLICA2_SERVER"+ "\\"+ServerCenterLocation.DDO.toString()).mkdir();
		
	}

	/**
	 * Server's main method to initialize and start the server instances Creates the
	 * orbd objects and performs the naming service
	 * Bind the Corba objects to
	 * establish connection to the client module 
	 * 
	 * @param args[] - port number and IP address Corba server starts listening the
	 * given port number and IP address
	 */
	public static void main(String args[]) {
		try {

			init();
			/*Initialize the ORB service with the respective arguments*/
			
			ORB orb = ORB.init(args, null);
			
			/*Initialize and Activate the root POA Manager
			*POA - Portable Object Adapter*/
			
			POA rootpoa = POAHelper
					.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();
			
			/*As per the process implementations, create the Java object instances
			*For the client, create servants to do the work*/
			
			DcmsServerFE serverFE = new DcmsServerFE();

			/*
			 * A CORBA object reference is a handler for a particular 
			 * CORBA object implemented by a server. 
			 * A CORBA object reference	will keep 
			 * track of the already used CORBA object references	
			 * of a method on the object.
			 */			
			
			/*Create the corba object references, given the server process implementation - java objects 	
			 *get object reference from the servant*/
			
			/*@param mtlServer - servant for which corba object reference is to be obtained
			* @return - mtlRef - object reference associated with the servant.
			**/
			
			org.omg.CORBA.Object feRef = rootpoa.servant_to_reference(serverFE);
			
			/*narrow - helper method of idl, which casts the corba object reference to local 
			programming language object.*/
			
			/*Cast the corba object references to java objects
			*mtlhref,lvlhref and ddohref are of type Dcms signature file
			*created by the idl compiler*/
			
			fehref = DcmsHelper.narrow(feRef);

			/*CORBA NAMING SERVICE*/
			
			org.omg.CORBA.Object objRef = orb
					.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			/*Create Naming directory entries, which the client would use to resolve*/
			
			NameComponent fePath[] = ncRef.to_name("FE");

			/*Rebind will bind the object reference to the given path*/
			
			ncRef.rebind(fePath, fehref);
			System.out.println("DCMS Servers ready and waiting ...");
			orb.run();
		}

		catch (Exception e) {
			System.err.println("Exception in Server Main:: " + e);
			e.printStackTrace(System.out);
		}

	}
}