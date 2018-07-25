package Server.ServerImplementation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import Server.ServerFrontEnd.DcmsServerFE;

public class Receiver extends Thread {

	DatagramSocket ds = null;
	String name;
	boolean isAlive;
	Object mapAccessor;
	public Receiver(boolean isAlive, String name, int port) {
		try {
			this.isAlive = isAlive;
			this.name = name;
			System.out.println(name+"listening in :: "+port);
			ds = new DatagramSocket(port);
			mapAccessor = new Object();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		byte[] data = new byte[100];
		while(getStatus()) {
			try {
				DatagramPacket dp = new DatagramPacket(data, data.length);
				ds.receive(dp);
				synchronized (mapAccessor) {
					//System.out.println("with time "+System.nanoTime()/1000000+"In "+this.name+" Received data "+new String(dp.getData()));
					DcmsServerFE.server_last_updated_time.put(name, System.nanoTime()/1000000);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setStatus(boolean value) {
		isAlive = value;
	}
	
	private boolean getStatus() {
		return isAlive;
	}
}
