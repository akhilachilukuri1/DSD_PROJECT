package Server.ServerImplementation;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import Conf.Constants;
import Conf.LogManager;
import Conf.ServerCenterLocation;
import Server.ServerFrontEnd.TransferResponseToFE;

public class DcmsServerReplicaResponseReceiver extends Thread{

	DatagramSocket serverSocket;
	DatagramPacket receivePacket;
	DatagramPacket sendPacket;
	int udpPortNum;
	ServerCenterLocation location;
	Logger loggerInstance;
	String recordCount;
	HashMap<Integer, TransferResponseToFE> responses;
	int c;
	
	public DcmsServerReplicaResponseReceiver(LogManager logManager) {
			try {
				serverSocket = new DatagramSocket(Constants.CURRENT_PRIMARY_PORT_FOR_REPLICAS);
			} catch (SocketException e) {
				System.out.println(e.getMessage());
			}
	}
	@Override
	public synchronized void run() {
		byte[] receiveData;
		while (true) {
			try {
				receiveData = new byte[1024];
				receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				byte[] receivedData = receivePacket.getData();
				String inputPkt = new String(receivedData).trim();
				if(inputPkt.contains("ACKNOWLEDGEMENT"))
				{
					System.out.println(new String(receivedData));		
					loggerInstance.log(Level.INFO, inputPkt + " from " + location);
				}
				else
				{
				System.out.println(
						"Received response packet in PRIMARY:: " + new String(receivedData));		
				loggerInstance.log(Level.INFO,
						"Received " + inputPkt + " from " + location);
				}
			} catch (Exception e) {
		 
			}
		}
	}
}
