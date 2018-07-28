package Server.ServerImplementation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import Conf.Constants;
import Conf.ServerCenterLocation;

/**
 * 
 * DcmsServerUDPReceiver is the class that serves other servers' requests in
 * form of UDP communication with RECPAKT functionality
 *
 */

public class DcmsServerUDPReceiver extends Thread {
	DatagramSocket serverSocket;
	DatagramPacket receivePacket;
	DatagramPacket sendPacket;
	int udpPortNum;
	ServerCenterLocation location;
	private Logger loggerInstance;
	String recordCount;
	DcmsServerImpl server;
	int c;
	boolean isAlive;

	/**
	 * 
	 * DcmsServerUDPReceiver constructor initializes the UDP socket port number
	 * of each location based on the received location input
	 * 
	 * @param loc
	 *            location of UDP server
	 * @param loggerlogger
	 *            instance of server is used to set the log messages
	 * @param serverImp
	 *            object that holds the corba server instance for communication
	 */

	public DcmsServerUDPReceiver(boolean isAlive, int udpPort, ServerCenterLocation loc, Logger logger,
			DcmsServerImpl serverImp) {
		location = loc;
		loggerInstance = logger;
		this.server = serverImp;
		this.isAlive = isAlive;
		c = 0;
		try {
			serverSocket = new DatagramSocket(udpPort);
			udpPortNum = udpPort;
			logger.log(Level.INFO, loc.toString() + " UDP Server Started");
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public synchronized void run() {
		byte[] receiveData;
		while (isAlive) {
			try {
				receiveData = new byte[1024];
				receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				System.out.println("LOc :: " + location + "1 Received pkt in udp Receiver :: "
						+ new String(receivePacket.getData()));
				String inputPkt = new String(receivePacket.getData()).trim();
				new DcmsServerUDPRequestServer(receivePacket, server, loggerInstance).start();
				loggerInstance.log(Level.INFO, "2 Received in udp receiver " + inputPkt + " from " + location);
			} catch (Exception e) {
			}
		}
	}

	/*
	 * Kills the current UDP receiver by assigning the isAlive flag to false
	 */
	public void killUDPReceiver() {
		isAlive = false;
	}
}
