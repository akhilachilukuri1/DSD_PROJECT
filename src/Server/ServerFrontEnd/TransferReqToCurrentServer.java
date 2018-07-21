package Server.ServerFrontEnd;
import java.util.Arrays;

import Conf.Constants;
import Conf.ServerOperations;
import Conf.ServerCenterLocation;
import Server.PrimaryServerImplementation.DcmsServerImpl;
public class TransferReqToCurrentServer extends Thread {
	String currentOperationData;
	DcmsServerImpl server;
	public TransferReqToCurrentServer(byte[] operationData) {
		this.currentOperationData = new String(operationData);
		this.server = null;
	}
	public void run() {
		String[] dataToBeSent = this.currentOperationData.split(Constants.RECEIVED_DATA_SEPERATOR);
		ServerOperations oprn = ServerOperations.valueOf(dataToBeSent[0]);
		switch (oprn) {
		case CREATE_T_RECORD:
			this.server = chooseServer(dataToBeSent[1].toUpperCase());
			String[] newArr = Arrays.copyOfRange(dataToBeSent, 3, dataToBeSent.length);
			String teacherData = String.join(Constants.RECEIVED_DATA_SEPERATOR,newArr);
			this.server.createTRecord(dataToBeSent[2], teacherData);
			break;
		case CREATE_S_RECORD:
			break;
		case GET_REC_COUNT:
			break;
		case EDIT_RECORD:
			break;
		case TRANSFER_RECORD:
			break;
		}
	}
	
	private synchronized DcmsServerImpl chooseServer(String loc) {
		switch (loc) {
		case "MTL":
			DcmsServerImpl mtlServer = new DcmsServerImpl(ServerCenterLocation.MTL);
			return mtlServer;
		case "LVL":
			DcmsServerImpl lvlServer = new DcmsServerImpl(ServerCenterLocation.LVL);
			return lvlServer;
		case "DDO":
			DcmsServerImpl ddoServer = new DcmsServerImpl(ServerCenterLocation.DDO);
			return ddoServer;
		}
		return null;
	}
}
