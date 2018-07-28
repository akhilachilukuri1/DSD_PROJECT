package Server.ServerFrontEnd;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import Conf.Constants;
import Conf.ServerCenterLocation;

public class UDPResponseReceiver extends Thread {
	
	DatagramSocket serverSocket;
	DatagramPacket receivePacket;
	DatagramPacket sendPacket;
	int udpPortNum;
	ServerCenterLocation location;
	Logger loggerInstance;
	String recordCount;
	HashMap<Integer, TransferResponseToFE> responses;
	int c;
	public UDPResponseReceiver(HashMap<Integer,TransferResponseToFE> responses) {
		try {
			this.responses = responses;
			serverSocket = new DatagramSocket(Constants.FRONT_END_UDP_PORT);
		} catch (SocketException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * This thread is initialized by the FrontEnd server 
	 * in order to receive the responses from the servers and replicas
	 * and the data is added to the responses HashMap
	 */
	
	
	@Override
	public void run() {
		byte[] receiveData;
		while (true) {
			try {
				receiveData = new byte[1024];
				receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				byte[] receivedData = receivePacket.getData();
				System.out.println(
						"Received response packet :: " + new String(receivedData));
				String inputPkt = new String(receivedData).trim();
				System.out.println("Returned response...."+inputPkt);
				String[] data = inputPkt.split(Constants.RESPONSE_DATA_SEPERATOR);
				TransferResponseToFE transferResponse = new TransferResponseToFE(data[0]);
				transferResponse.start();
				responses.put(Integer.parseInt(data[1]), transferResponse);				
				loggerInstance.log(Level.INFO,
						"Received " + inputPkt + " from " + location);
			} catch (Exception e) {
		 
			}
		}
	}
}
