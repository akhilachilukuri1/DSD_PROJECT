package Server.ServerImplementation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;

import com.sun.istack.internal.logging.Logger;

import Conf.LogManager;

public class DcmsServerMultiCastReceiver extends Thread {
	MulticastSocket multicastsocket;
	InetAddress address;
	boolean isPrimary;
	LogManager logManager;
	public DcmsServerMultiCastReceiver(boolean isPrimary, LogManager ackManager) {
		try {
			multicastsocket = new MulticastSocket(6789);
			address = InetAddress.getByName("224.0.0.1");
			multicastsocket.joinGroup(address);
			this.isPrimary = isPrimary;
			this.logManager=ackManager;
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
					System.out.println("Received data in multicast heartBeatReceiver " + new String(packet.getData()));
					System.out.println("Sent the acknowledgement for the data recevied in replica to primary server " + new String(packet.getData()));
					//logManager.logger.log(Level.INFO,"Sent the acknowledgement for the data recevied in replica to primary server" );
					DcmsServerReplicaAcknowledgementSender ack=new DcmsServerReplicaAcknowledgementSender(new String(packet.getData()),logManager);
					ack.start();
					DcmsServerReplicaRequestProcessor req = new DcmsServerReplicaRequestProcessor(new String(packet.getData()),logManager);
					req.start();
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
