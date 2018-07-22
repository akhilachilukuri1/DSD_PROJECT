package Server.ServerFrontEnd;
public class TransferResponseToFE extends Thread {
	String response;
	public TransferResponseToFE(String response) {
		this.response = response;
	}
	
	public void run() {
		System.out.println("============="+this.response);
		DcmsServerFE.receivedResponses.add(this.response);
	}	
	
	public String getResponse() {
		return response;
	}
}
