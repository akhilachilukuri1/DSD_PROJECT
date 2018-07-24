package Server.ServerImplementation;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import Conf.Constants;
import java.util.Arrays;
import Conf.ServerOperations;
import Server.ServerFrontEnd.DcmsServerFE;

public class DcmsServerReplicaRequestProcessor extends Thread {
	
		String currentOperationData;
		DcmsServerImpl server;
		String response;
		public DcmsServerReplicaRequestProcessor(String operationData) {
			this.currentOperationData = operationData;
			this.server = null;
			response = null;
		}
		public synchronized void run() {
			String[] dataArr; 
			String[] dataToBeSent = this.currentOperationData.trim().split(Constants.RECEIVED_DATA_SEPERATOR);
			System.out.println(">>>>>>>>>>>>>>>>>"+this.currentOperationData.trim());
			ServerOperations oprn = ServerOperations.valueOf(dataToBeSent[0]);
			String requestId = dataToBeSent[dataToBeSent.length-1];
			System.out.println("Currently serving request with id :: "+ requestId);
			switch (oprn) {
			case CREATE_T_RECORD:
				this.server = chooseServer(dataToBeSent[1]);
				dataArr = Arrays.copyOfRange(dataToBeSent, 3, dataToBeSent.length);
				String teacherData = String.join(Constants.RECEIVED_DATA_SEPERATOR,dataArr);
				System.out.println(">>>>>>>>>>>>>>>>>"+dataToBeSent[0]+" "+dataToBeSent[1]);
				System.out.println("==============Sending data :: "+teacherData);
				response = this.server.createTRecord(dataToBeSent[2], teacherData);
				sendReply(response);
				break;
			case CREATE_S_RECORD:
				this.server = chooseServer(dataToBeSent[1]);
				dataArr = Arrays.copyOfRange(dataToBeSent, 3, dataToBeSent.length);
				String studentData = String.join(Constants.RECEIVED_DATA_SEPERATOR,dataArr);
				response = this.server.createSRecord(dataToBeSent[2], studentData);
				sendReply(response);
				break;
			case GET_REC_COUNT:
				this.server = chooseServer(dataToBeSent[1]);
				response = this.server.getRecordCount(dataToBeSent[2]+Constants.RECEIVED_DATA_SEPERATOR+dataToBeSent[3]);
				sendReply(response);
				break;
			case EDIT_RECORD:
				this.server = chooseServer(dataToBeSent[1]);
				String newdata=dataToBeSent[5]+Constants.RECEIVED_DATA_SEPERATOR+dataToBeSent[6];
				response = this.server.editRecord(dataToBeSent[2], dataToBeSent[3], dataToBeSent[4],  newdata);
				sendReply(response);
				break;
			case TRANSFER_RECORD:
				this.server = chooseServer(dataToBeSent[1]);
				String newdata1=dataToBeSent[4]+Constants.RECEIVED_DATA_SEPERATOR+dataToBeSent[5];
				response = this.server.transferRecord(dataToBeSent[2], dataToBeSent[3], newdata1);
				sendReply(response);
				break;
			}
		}
		public String getResponse() {
			return response;
		}
		private synchronized DcmsServerImpl chooseServer(String loc) {
			return DcmsServerFE.centralRepository.get(Constants.REPLICA1_SERVER_ID).get(loc);
		}
		
		private void sendReply(String response) {
			DatagramSocket ds;
			try {
				ds = new DatagramSocket();
				byte[] dataBytes = response.getBytes();
				DatagramPacket dp = new DatagramPacket(dataBytes, dataBytes.length,
						InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()),
						Constants.CURRENT_PRIMARY_PORT_FOR_REPLICAS);
				ds.send(dp);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
}


	
	

