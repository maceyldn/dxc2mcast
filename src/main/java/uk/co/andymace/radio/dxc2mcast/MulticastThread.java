package uk.co.andymace.radio.dxc2mcast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Enumeration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MulticastThread implements DXClientListener{

	private DatagramSocket socket;
	private InetAddress address;
	private int port;
	private NetworkInterface ni;
	
	private static final Logger logger = LogManager.getLogger(MulticastThread.class);
	

	public MulticastThread(InetAddress multicastAddress, int multicastPort, InetAddress sourceip) {
		
		try {
			address = multicastAddress;
			port = multicastPort;
			ni = NetworkInterface.getByInetAddress(sourceip);
			
			if (ni == null)
			{
				logger.fatal("Cannot bind to network interface");
				System.exit(0);
			}

			
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	
	
	public void changeHappenend(DXSpot dxs) {
		logger.info(dxs);
		
		try {
			broadcast(dxs.PCmessage, address, port);
			
		} catch (IOException e) {
			logger.info("failed to send UDP packet");
			e.printStackTrace();
		}
		
	}
	
    public void broadcast(String broadcastMessage, InetAddress group, int port) throws IOException {
    	
    	 MulticastSocket s = new MulticastSocket(5001);
    	 s.setNetworkInterface(ni);
    	 s.joinGroup(group);
    	 DatagramPacket hi = new DatagramPacket(broadcastMessage.getBytes(), broadcastMessage.length(), group, 6789);
    	 s.send(hi);
    	
    	}

	
}
