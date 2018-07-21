package Server.ServerFrontEnd;
import java.util.Arrays;

import Conf.Constants;
import Conf.ServerOperations;
import Conf.ServerCenterLocation;
import Server.PrimaryServerImplementation.DcmsServerImpl;
import Server.ServerFrontEnd.DcmsServerFE;
public class TransferReqToCurrentServer extends Thread {
	String currentOperationData;
	DcmsServerImpl server;
	public TransferReqToCurrentServer(byte[] operationData) {
		this.currentOperationData = new String(operationData);
		this.server = null;
	}
	public void run() {
		String[] dataArr; 
		String[] dataToBeSent = this.currentOperationData.trim().split(Constants.RECEIVED_DATA_SEPERATOR);
		ServerOperations oprn = ServerOperations.valueOf(dataToBeSent[0]);
		switch (oprn) {
		case CREATE_T_RECORD:
			this.server = chooseServer(dataToBeSent[1].toUpperCase());
			dataArr = Arrays.copyOfRange(dataToBeSent, 3, dataToBeSent.length);
			String teacherData = String.join(Constants.RECEIVED_DATA_SEPERATOR,dataArr);
			this.server.createTRecord(dataToBeSent[2], teacherData);
			break;
		case CREATE_S_RECORD:
			this.server = chooseServer(dataToBeSent[1].toUpperCase());
			dataArr = Arrays.copyOfRange(dataToBeSent, 3, dataToBeSent.length);
			String studentData = String.join(Constants.RECEIVED_DATA_SEPERATOR,dataArr);
			this.server.createSRecord(dataToBeSent[2], studentData);
			break;
		case GET_REC_COUNT:
			this.server = chooseServer(dataToBeSent[1].toUpperCase());
			this.server.getRecordCount(dataToBeSent[2]);
			break;
		case EDIT_RECORD:
			this.server = chooseServer(dataToBeSent[1].toUpperCase());
			this.server.editRecord(dataToBeSent[2], dataToBeSent[3], dataToBeSent[4],  dataToBeSent[5]);
			break;
		case TRANSFER_RECORD:
			this.server = chooseServer(dataToBeSent[1].toUpperCase());
			this.server.transferRecord(dataToBeSent[2], dataToBeSent[3], dataToBeSent[4]);
			break;
		}
	}
	
	private synchronized DcmsServerImpl chooseServer(String loc) {
		return DcmsServerFE.serverMap.get(loc);
	}
}
