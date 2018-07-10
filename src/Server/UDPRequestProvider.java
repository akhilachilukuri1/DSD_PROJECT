package Server;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import Conf.ServerCenterLocation;

import Conf.Constants;
import Models.Record;

public class UDPRequestProvider extends Thread {
	private static final String MTL = null;
	private static final String LVL = null;
	private static final String DDO = null;
	private String recordCount, transferResult;
	private Logger logger;
	private DcmsServerImpl server;
	private String requestType;
	private Record recordForTransfer;

	/**
	 * UDPRequestProvider handles the UDP message call and transfers the necessary
	 * record
	 * 
	 * @param server
	 *            serverimpl class instance
	 * @param requestType
	 *            UDP request type - transfer/record count
	 * @param recordForTransfer
	 *            The record to be transferred
	 * 
	 */
	public UDPRequestProvider(DcmsServerImpl server, String requestType,
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
	 * UDP Server thread that handles the incoming packets Routes the packet to the
	 * respective server address
	 */

	@Override
	public void run() {
		DatagramSocket socket = null;
		try {
			switch (requestType) {
			case "GET_RECORD_COUNT":
				socket = new DatagramSocket();
				byte[] data = "GET_RECORD_COUNT".getBytes();
				DatagramPacket packet = new DatagramPacket(data, data.length,
						InetAddress.getByName(server.IPaddress),
						server.serverUDP.udpPortNum);
				socket.send(packet);
				data = new byte[100];
				socket.receive(new DatagramPacket(data, data.length));
				recordCount = server.location + " " + new String(data);
				break;
			case "TRANSFER_RECORD":
				socket = new DatagramSocket();
				byte[] data1 = ("TRANSFER_RECORD" + "#"
						+ recordForTransfer.toString()).getBytes();
				DatagramPacket packet1 = new DatagramPacket(data1, data1.length,
						InetAddress.getByName(server.IPaddress),
						server.serverUDP.udpPortNum);
				socket.send(packet1);
				data1 = new byte[100];
				socket.receive(new DatagramPacket(data1, data1.length));
				transferResult = new String(data1);
				break;
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage());
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
	}

}