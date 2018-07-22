package Server.ServerFrontEnd;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

import Conf.Constants;
import Conf.ServerOperations;
import Conf.ServerCenterLocation;
import Server.ServerFrontEnd.DcmsServerFE;
import Server.ServerImplementation.DcmsServerImpl;
public class TransferReqToCurrentServer extends Thread {
	String currentOperationData;
	DcmsServerImpl server;
	String response;
	public TransferReqToCurrentServer(byte[] operationData) {
		this.currentOperationData = new String(operationData);
		this.server = null;
		response = null;
	}
	public void run() {
		String[] dataArr; 
		String[] dataToBeSent = this.currentOperationData.trim().split(Constants.RECEIVED_DATA_SEPERATOR);
		ServerOperations oprn = ServerOperations.valueOf(dataToBeSent[0]);
		String requestId = dataToBeSent[dataToBeSent.length-1];
		System.out.println("Currently serving request with id :: "+ requestId);
		switch (oprn) {
		case CREATE_T_RECORD:
			this.server = chooseServer(dataToBeSent[1].toUpperCase());
			dataArr = Arrays.copyOfRange(dataToBeSent, 3, dataToBeSent.length-1);
			String teacherData = String.join(Constants.RECEIVED_DATA_SEPERATOR,dataArr);
			response = this.server.createTRecord(dataToBeSent[2], teacherData);
			sendReply(requestId, response);
			break;
		case CREATE_S_RECORD:
			this.server = chooseServer(dataToBeSent[1].toUpperCase());
			dataArr = Arrays.copyOfRange(dataToBeSent, 3, dataToBeSent.length-1);
			String studentData = String.join(Constants.RECEIVED_DATA_SEPERATOR,dataArr);
			response = this.server.createSRecord(dataToBeSent[2], studentData);
			sendReply(requestId, response);
			break;
		case GET_REC_COUNT:
			this.server = chooseServer(dataToBeSent[1].toUpperCase());
			response = this.server.getRecordCount(dataToBeSent[2]);
			sendReply(requestId, response);
			break;
		case EDIT_RECORD:
			this.server = chooseServer(dataToBeSent[1].toUpperCase());
			response = this.server.editRecord(dataToBeSent[2], dataToBeSent[3], dataToBeSent[4],  dataToBeSent[5]);
			sendReply(requestId, response);
			break;
		case TRANSFER_RECORD:
			this.server = chooseServer(dataToBeSent[1].toUpperCase());
			response = this.server.transferRecord(dataToBeSent[2], dataToBeSent[3], dataToBeSent[4]);
			sendReply(requestId, response);
			break;
		}
	}
	public String getResponse() {
		return response;
	}
	private synchronized DcmsServerImpl chooseServer(String loc) {
		return DcmsServerFE.primarServerMap.get(loc);
	}
	
	private void sendReply(String requestId, String response) {
		DatagramSocket ds;
		try {
			ds = new DatagramSocket();
			response = response + Constants.RESPONSE_DATA_SEPERATOR + requestId; 
			byte[] dataBytes = response.getBytes();
			DatagramPacket dp = new DatagramPacket(dataBytes, dataBytes.length,
					InetAddress.getByName(Constants.FRONT_END_IP), Constants.FRONT_END_UDP_PORT);
			ds.send(dp);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
