package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import Conf.Constants;
import Conf.ServerCenterLocation;
import Server.UDPRequestServer;

/**
 * 
 * ServerUDP is the class that serves other servers' requests
 * in form of UDP communication
 * with RECPAKT functionality
 *
 */

public class ServerUDP extends Thread {
	DatagramSocket serverSocket;
	DatagramPacket receivePacket;
	DatagramPacket sendPacket;
	int udpPortNum;
	ServerCenterLocation location;
	Logger loggerInstance;
	String recordCount;
	DcmsServerImpl server;
	int c;

	/**
	 * 
	 * ServerUDP constructor initializes the UDP socket port number of each location
	 * based on the received location input
	 * 
	 * @param loc
	 *            location of UDP server
	 * @param loggerlogger
	 *            instance of server is used to set the log messages
	 * @param serverImp
	 *            object that holds the corba server instance for communication
	 */

	public ServerUDP(ServerCenterLocation loc, Logger logger,
			DcmsServerImpl serverImp) {
		location = loc;
		loggerInstance = logger;
		this.server = serverImp;
		c = 0;
		try {
			
			/*Switch case to create the datagram packet based on the location port number
			 * and a logger to log the current event
			 * */
			
			switch (loc) {
			case MTL:
				serverSocket = new DatagramSocket(Constants.UDP_PORT_NUM_MTL);
				udpPortNum = Constants.UDP_PORT_NUM_MTL;
				logger.log(Level.INFO, "MTL UDP Server Started");
				break;
			case LVL:
				serverSocket = new DatagramSocket(Constants.UDP_PORT_NUM_LVL);
				udpPortNum = Constants.UDP_PORT_NUM_LVL;
				logger.log(Level.INFO, "LVL UDP Server Started");
				break;
			case DDO:
				serverSocket = new DatagramSocket(Constants.UDP_PORT_NUM_DDO);
				udpPortNum = Constants.UDP_PORT_NUM_DDO;
				logger.log(Level.INFO, "DDO UDP Server Started");
				break;
			}

		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
	}

	/**
	 * A UDP server thread is issued that continuously/periodically keeps listening
	 * for any request or incoming data packet.
	 * Calls UDPRequestServer based on the received data.
	 */

	@Override
	public void run() {
		byte[] receiveData;
		while (true) {
			try {
				receiveData = new byte[1024];
				receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				System.out.println(
						"Received pkt :: " + new String(receivePacket.getData()));
				String inputPkt = new String(receivePacket.getData()).trim();
				new UDPRequestServer(receivePacket, server).start();
				loggerInstance.log(Level.INFO,
						"Received " + inputPkt + " from " + location);
			} catch (Exception e) {
			}
		}
	}
}
