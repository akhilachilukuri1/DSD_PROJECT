package Server.ServerImplementation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class DcmsServerMultiCastReceiver extends Thread {
	MulticastSocket multicastsocket;
	InetAddress address;

	public DcmsServerMultiCastReceiver() {
		try {
			multicastsocket = new MulticastSocket(6789);
			address = InetAddress.getByName("224.0.0.1");
			multicastsocket.joinGroup(address);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}

	public void run() {
		try {
			while (true) {
				byte[] mydata = new byte[100];
				DatagramPacket packet = new DatagramPacket(mydata, mydata.length);
				multicastsocket.receive(packet);
				System.out.println("Received data " + new String(packet.getData()));
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
