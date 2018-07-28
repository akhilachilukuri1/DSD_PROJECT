package Server.ServerImplementation;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/*
 * Heart beat sender class which keeps sending to the other servers
 * in the servers list
 */
public class HeartBeatSender extends Thread {
	int port1,port2;
	String name;
	DatagramSocket ds;
	public HeartBeatSender(DatagramSocket ds, String name, int port1, int port2) {
		this.port1 = port1;
		this.port2 = port2;
		this.name = name;
		this.ds = ds;
	}
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		byte[] dataBytes = name.getBytes();
		DatagramPacket dp;
		try {
			dp = new DatagramPacket(dataBytes, dataBytes.length,
					InetAddress.getByName("localhost"),port1);
			ds.send(dp);
			dp = new DatagramPacket(dataBytes, dataBytes.length,
					InetAddress.getByName("localhost"),port2);
			ds.send(dp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
