package Server.ServerImplementation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import Conf.Constants;

public class DcmsServerMultiCastSender extends Thread {
	MulticastSocket multicastsocket;
	InetAddress address;
	String data;

	public DcmsServerMultiCastSender(String request) {
		try {
			multicastsocket = new MulticastSocket(Constants.MULTICAST_PORT_NUMBER);
			address = InetAddress.getByName(Constants.MULTICAST_IP_ADDRESS);
			multicastsocket.joinGroup(address);
			this.data = request;
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void run() {
		try {
			DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(), address, Constants.MULTICAST_PORT_NUMBER);
			multicastsocket.send(packet);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
