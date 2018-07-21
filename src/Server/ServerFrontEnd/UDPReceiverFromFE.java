package Server.ServerFrontEnd;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import Conf.Constants;
import Conf.ServerCenterLocation;

public class UDPReceiverFromFE extends Thread {
	
	DatagramSocket serverSocket;
	DatagramPacket receivePacket;
	DatagramPacket sendPacket;
	int udpPortNum;
	ServerCenterLocation location;
	Logger loggerInstance;
	String recordCount;
	int c;
	public UDPReceiverFromFE() {
		try {
			serverSocket = new DatagramSocket(Constants.CURRENT_SERVER_UDP_PORT);
		} catch (SocketException e) {
			System.out.println(e.getMessage());
		}
	}
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
						"Received pkt :: " + new String(receivedData));
				TransferReqToCurrentServer transferReq = new TransferReqToCurrentServer(receivedData);
				transferReq.start();
				String inputPkt = new String(receivePacket.getData()).trim();
				loggerInstance.log(Level.INFO,
						"Received " + inputPkt + " from " + location);
			} catch (Exception e) {
		 
			}
		}
	}
}
