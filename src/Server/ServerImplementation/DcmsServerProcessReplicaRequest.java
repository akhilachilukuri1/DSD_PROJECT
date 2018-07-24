package Server.ServerImplementation;

import java.util.Arrays;

import Conf.Constants;
import Conf.ServerOperations;
import Server.ServerFrontEnd.DcmsServerFE;

public class DcmsServerProcessReplicaRequest extends Thread {
	
		String currentOperationData;
		DcmsServerImpl server;
		String response;
		public DcmsServerProcessReplicaRequest(byte[] operationData) {
			this.currentOperationData = new String(operationData);
			this.server = null;
			response = null;
		}
		public synchronized void run() {
			String[] dataArr; 
			String[] dataToBeSent = this.currentOperationData.trim().split(Constants.RECEIVED_DATA_SEPERATOR);
			ServerOperations oprn = ServerOperations.valueOf(dataToBeSent[0]);
			String requestId = dataToBeSent[dataToBeSent.length-1];
			System.out.println("Currently serving request with id :: "+ requestId);
			switch (oprn) {
			case CREATE_T_RECORD:
				this.server = chooseServer(dataToBeSent[dataToBeSent.length-1]);
				dataArr = Arrays.copyOfRange(dataToBeSent, 3, dataToBeSent.length-1);
				String teacherData = String.join(Constants.RECEIVED_DATA_SEPERATOR,dataArr);
				response = this.server.createTRecord(dataToBeSent[2], teacherData);
				//sendReply(requestId, response);
				break;
			case CREATE_S_RECORD:
				this.server = chooseServer(dataToBeSent[dataToBeSent.length-1]);
				dataArr = Arrays.copyOfRange(dataToBeSent, 3, dataToBeSent.length-1);
				String studentData = String.join(Constants.RECEIVED_DATA_SEPERATOR,dataArr);
				response = this.server.createSRecord(dataToBeSent[2], studentData);
				//sendReply(requestId, response);
				break;
			case GET_REC_COUNT:
				this.server = chooseServer(dataToBeSent[dataToBeSent.length-1]);
				response = this.server.getRecordCount(dataToBeSent[2]);
				//sendReply(requestId, response);
				break;
			case EDIT_RECORD:
				this.server = chooseServer(dataToBeSent[dataToBeSent.length-1]);
				response = this.server.editRecord(dataToBeSent[2], dataToBeSent[3], dataToBeSent[4],  dataToBeSent[5]);
				//sendReply(requestId, response);
				break;
			case TRANSFER_RECORD:
				this.server = chooseServer(dataToBeSent[dataToBeSent.length-1]);
				response = this.server.transferRecord(dataToBeSent[2], dataToBeSent[3], dataToBeSent[4]);
				//sendReply(requestId, response);
				break;
			}
		}
		public String getResponse() {
			return response;
		}
		private synchronized DcmsServerImpl chooseServer(String loc) {
			return DcmsServerFE.centralRepository.get(Constants.REPLICA1_SERVER_ID).get(loc);
		}
}
