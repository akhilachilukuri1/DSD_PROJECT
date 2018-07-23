package Server.ServerImplementation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class DcmsServerMultiCastReceiver extends Thread {
	MulticastSocket multicastsocket;
	InetAddress address;
	boolean isPrimary;
	public DcmsServerMultiCastReceiver(boolean isPrimary) {
		try {
			multicastsocket = new MulticastSocket(6789);
			address = InetAddress.getByName("224.0.0.1");
			multicastsocket.joinGroup(address);
			this.isPrimary = isPrimary;
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}

	public synchronized void run() {
		try {
			while (true) {
				byte[] mydata = new byte[100];
				DatagramPacket packet = new DatagramPacket(mydata, mydata.length);
				multicastsocket.receive(packet);
				if(!isPrimary) {
					System.out.println("Received data in multicast receiver " + new String(packet.getData()));
					System.out.println("Sent the acknowledgement for the data recevied in replica to primary server " + new String(packet.getData()));
					DcmsServerReplicaAcknowledgementSender ack=new DcmsServerReplicaAcknowledgementSender(new String(packet.getData()));
					ack.start();
					DcmsServerReplicaRequestProcessor req = new DcmsServerReplicaRequestProcessor(new String(packet.getData()));
					req.start();
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
