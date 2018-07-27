package Server.ServerImplementation;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import Conf.ServerCenterLocation;

import Conf.Constants;
import Models.Record;

public class DcmsServerUDPRequestProvider extends Thread {
	private String recordCount = "";
	private String transferResult = "";
	private DcmsServerImpl server;
	private String requestType;
	private Record recordForTransfer;

	/**
	 * DcmsServerUDPRequestProvider handles the incoming messages call (Get Rec ,
	 * Trans Rec) and based on the incoming call 
	 * Routes the packet to the necessary location 
	 * to retreive the data
	 * 
	 * @param server
	 *            serverimpl class instance
	 * @param requestType
	 *            UDP request type - transfer/record count
	 * @param recordForTransfer
	 *            The record to be transferred
	 * 
	 */
	public DcmsServerUDPRequestProvider(DcmsServerImpl server, String requestType,
			Record recordForTransfer) throws IOException {
		this.server = server;
		this.requestType = requestType;
		this.recordForTransfer = recordForTransfer;
	}

	public String getRemoteRecordCount() {
		return recordCount;
	}

	public String getTransferResult() {
		return transferResult;
	}

	/**
	 * UDP Server thread that handles the incoming packets 
	 * Routes the packet to the
	 * respective server address based on the
	 * required functionality
	 */

	@Override
	public void run() {
		DatagramSocket socket = null;
		try {
			System.out.println("Req type :: "+requestType);
			switch (requestType) {
			case "GET_RECORD_COUNT":
				socket = new DatagramSocket();
				byte[] data = "GET_RECORD_COUNT".getBytes();
				System.out.println("data in udp req provider :: "+new String(data));
				System.out.println("port here :: "+server.locUDPPort);
				/*Create a datagram packet for the respective server address.*/
				DatagramPacket packet = new DatagramPacket(data, data.length,
						InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()),
						server.locUDPPort);
				socket.send(packet);
				data = new byte[100];
				socket.receive(new DatagramPacket(data, data.length));
				recordCount = server.location + " " + new String(data);
				break;
			case "TRANSFER_RECORD":
				socket = new DatagramSocket();
				byte[] data1 = ("TRANSFER_RECORD" + "#"
						+ recordForTransfer.toString()).getBytes();
				
				/*Create a datagram packet for the respective server address.*/
				
				DatagramPacket packet1 = new DatagramPacket(data1, data1.length,
						InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()),
						server.locUDPPort);
				socket.send(packet1);
				data1 = new byte[100];
				socket.receive(new DatagramPacket(data1, data1.length));
				transferResult = new String(data1);
				//System.out.println("============="+transferResult);
				break;
			}
		} catch (Exception e) {
			System.out.println("Exception :::::::::::::::::::::"+e.getMessage());
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
	}

}