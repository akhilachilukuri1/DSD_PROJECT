package Server.ServerImplementation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


import Conf.Constants;

public class DcmsServerReplicaAcknowledgementSender extends Thread {
	String request;
	DatagramSocket ds;
	public DcmsServerReplicaAcknowledgementSender(String request) {
		request="RECEIVED ACKNOWLEDGEMENT IN PRIMARY :: "+request;
		this.request=request;
	}
	public synchronized void run() {
		try {
			ds = new DatagramSocket();
			byte[] dataBytes = request.getBytes();
			DatagramPacket dp = new DatagramPacket(dataBytes, dataBytes.length,
					InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()),
					Constants.CURRENT_PRIMARY_PORT_FOR_REPLICAS);
			ds.send(dp);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
