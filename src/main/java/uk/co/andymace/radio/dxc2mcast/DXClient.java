package uk.co.andymace.radio.dxc2mcast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.co.andymace.radio.dxc2mcast.utils.Utils;

public class DXClient {
	
	    private Socket socket;
	    private BufferedReader in;
	    private PrintWriter out;
	    
	    public String mycall = "MB7MUX";
	    public String cluster_version = "5400";
	    public String software_version = "0.94";
	    public int hops = 15;
		private PCSTATE state;	
	    
		private ArrayList<DXClientListener> listenerList;
		
		private static final Logger logger = LogManager.getLogger(DXClient.class);
		
	
	public DXClient(String call, String serverAddress, int port) throws Exception {

		mycall = call;
		listenerList = new ArrayList<DXClientListener>();
		state = PCSTATE.idle;
		
        // Setup networking
        socket = new Socket(serverAddress, port);
        
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        if (socket.isConnected())
        {
        	//logger.info("Socket Connected");
        	logger.info("Connected to: " + serverAddress + ":" + port);
        	logger.info("Logging in");
        	state = PCSTATE.connected;
        }
        
        
    }
	
	public void addListener(DXClientListener listener)
	{
		listenerList.add(listener);
	}
	
	private void announceNewDXSpot(DXSpot dxs)
	{
		for (DXClientListener listener : listenerList)
		{
			listener.changeHappenend(dxs);
		}
	}
		
	public void login() throws IOException {
			out.write(mycall + "\n");
			out.flush();
	}

	public void processPCData() throws IOException {
		String response;
		
		while (true)
		{
			response = in.readLine();
			//logger.info(response);
			if (response.startsWith("PC11"))
			{
				process_dx_spot(response, false);
			}
			else if (response.startsWith("PC61"))
			{
				//logger.info(response);
				process_dx_spot(response, true);
			}
			else if (response.startsWith("PC18"))
			{
				if (state == PCSTATE.connected);
					do_init();
				//do_init() if ($state eq 'connected');
			}
			else if (response.startsWith("PC16")) {
				//logger.info("Ignoring PC16");
			}
			else if (response.startsWith("PC17")) {
				//logger.info("Ignoring PC17");
			}
			else if (response.startsWith("PC19")) {
				//logger.info("Ignoring PC19");
			}
			else if (response.startsWith("PC20")) {
				//logger.info("Ignoring PC20");
			}
			else if (response.startsWith("PC21")) {
				//logger.info("Ignoring PC21");
			}
			else if (response.startsWith("PC22")) {
				if (state == PCSTATE.myinitdone) {
					state = PCSTATE.initcomplete;
					logger.info("Handshake Complete, Logged in and Protocol init complete...");
				}
			}
			else if (response.startsWith("PC23")) {
				//process_wwc_info();
			}
			else if (response.startsWith("PC34")) {
				//process_remote_command();
			}
			else if (response.startsWith("PC39")) {
				logger.info("Disconnecting on PC39");
				socket.close();
				System.exit(0);
			}
			else if (response.startsWith("PC51")) {
				process_ping_request(response);
			}
			else 
			{
				//logger.info("Unknown PC: " + response);
			}
		}
		
	}

	
	
	
	private void process_dx_spot(String response, boolean PC61) {
		
		//logger.info(response);
		//my ($opcode, $freq, $spotted_call, $date, $time, $comment, $fullspotter, $cluster) = split /\^/;
		String[] dxspot = response.split("\\^");
		
		DXSpot dxs = new DXSpot();
		dxs.opcode = dxspot[0];
		dxs.freq = dxspot[1];
		dxs.spotted_call = dxspot[2];
		dxs.date = dxspot[3];
		dxs.time = dxspot[4];
		dxs.comment = dxspot[5];
		dxs.fullspotter = dxspot[6];
		dxs.cluster = dxspot[7];
		dxs.PCmessage = response;
		
		if (dxs.fullspotter.contains("-"))
		{
		 dxs.spotter_call = dxs.fullspotter.split("-")[0];
		 dxs.spottersid = dxs.fullspotter.split("-")[1];
		} else {
			dxs.spotter_call = dxs.fullspotter;
		}
		
		dxs.extended = PC61;
				
		if (dxs.extended)
		{
			dxs.spotter_ip = dxs.cluster = dxspot[8];
			dxs.hops = dxs.cluster = dxspot[9];
			dxs.spotter_host = Utils.getHostName(dxs.spotter_ip);
			
		}
		
		
		announceNewDXSpot(dxs);
		//logger.info(dxs);
	}

	private void process_ping_request(String response) {
		
		//my ($opcode, $tocluster, $fromcluster, $pingflag) = split /\^/;
		String[] s = response.split("\\^");
		
		String opcode = s[0], tocluster = s[1], fromcluster = s[2];
		int pingflag = Integer.parseInt(s[3]);
				
		
		if (tocluster.equalsIgnoreCase(mycall))
		{
			logger.info("HEARTBEAT " + s[2]);
			pingflag ^= 1;
			String pc51 = "PC51^"+fromcluster+"^"+mycall+"^"+pingflag+"^~";
			sendpc(pc51);
		}
		
	}

	private void do_init() {
		logger.info("Sending initialisations...");
		sendPC19();
		sendPC20();
		state = PCSTATE.myinitdone;
		
	}

	

	private void sendPC20() {
		String pc20 = "PC20^~";
		sendpc(pc20);
		
	}

	

	private void sendPC19() {
		String pc19 = "PC19^0^"+mycall+"^0^"+cluster_version+"^H"+hops+"^";
		sendpc(pc19);
	}
	
	private void sendpc(String pc) {
		//logger.info("Sending: " + pc);
		out.print(pc + "\n");
		out.flush();
		
	}

	
}
