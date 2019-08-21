package uk.co.andymace.radio.dxc2mcast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.airlift.command.Command;
import io.airlift.command.Option;
import io.airlift.command.SingleCommand;

@Command(name = "app", description = "app")
public class App {
	
	private static final Logger logger = LogManager.getLogger(DXClient.class);
	
	@Option(name = {"--config"}, title="Configfile", description = "(default: config.properties)")
	private static String configFilename = "config.properties";

	private static myProperties appProperties;
	
	public App() 
	{
	// needed by airline
	}
	public static void main(String[] args) throws Exception {
		   
		logger.info("***********************************");
		logger.info("*    DX Cluster 2 Multicast       *");
		logger.info("*  Andy Mace / M0MUX 15/02/2019   *");
		logger.info("***********************************");
		
		SingleCommand.singleCommand(App.class).parse(args);
    	
    	ArrayList<String> requiredProperties = new ArrayList<String>(Arrays.asList("appName", "sourceaddress", "multicastaddress", "multicastport", "clustercall", "clusteraddress", "clusterport"));
    	appProperties = new myProperties(configFilename, requiredProperties);

    	logger.info("App Name is : ["+ appProperties.getProperty("appName") +"]");
    	//Logger.info("Listening on: ["+ appProperties.getProperty("localIPAddress") + ":" + appProperties.getProperty("listeningPort")+ "]");
    	logger.info("====================================================");

    	
		logger.info("Setting up Multicast Backend");
		
		int multicastPort = appProperties.getIntProperty("multicastport");
		InetAddress multicastAddress = InetAddress.getByName(appProperties.getProperty("multicastaddress"));
		InetAddress sourceAddress = InetAddress.getByName(appProperties.getProperty("sourceaddress"));
		MulticastThread mcast = new MulticastThread(multicastAddress, multicastPort, sourceAddress);
		
		logger.info("Setting up DXClient");
		DXClient dxc = new DXClient(appProperties.getProperty("clustercall"), appProperties.getProperty("clusteraddress"), appProperties.getIntProperty("clusterport"));
		dxc.addListener(mcast);
		dxc.login();
		dxc.processPCData();
		   
		  
	}

	

}
