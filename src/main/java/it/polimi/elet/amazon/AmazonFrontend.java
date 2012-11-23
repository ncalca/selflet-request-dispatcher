package it.polimi.elet.amazon;

import it.polimi.elet.selflet.istantiator.IVirtualMachineIPManager;
import it.polimi.elet.selflet.istantiator.VirtualMachineIPManager;

import java.io.FileInputStream;
import java.util.Set;
import java.util.Timer;

import org.apache.log4j.Logger;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.google.common.collect.Sets;

/**
 * An implementation for Amazon frontend
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class AmazonFrontend implements IAmazonFrontend {

	private static final Logger LOG = Logger.getLogger(AmazonFrontend.class);

	private static final String AWS_CREDENTIALS = "src/main/resources/AwsCredentials.properties";
	private static final int IP_RETRIEVER_INTERVAL = 20 * 1000;

	private final IVirtualMachineIPManager virtualMachineIPManager = VirtualMachineIPManager.getInstance();

	// singleton instance
	private static IAmazonFrontend instance;

	private final Set<String> activeIPAddresses = Sets.newHashSet();

	private AmazonEC2Client ec2Client;
	private AWSCredentials awsCredentials;

	private AmazonFrontend() {
		initAmazonClient();
		startIpRetriever();
	}

	private void startIpRetriever() {
		AmazonIPRetriever ipRetriever = new AmazonIPRetriever(ec2Client, activeIPAddresses, virtualMachineIPManager);
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(ipRetriever, 0, IP_RETRIEVER_INTERVAL);
	}

	public static IAmazonFrontend getInstance() {
		if (instance == null) {
			instance = new AmazonFrontend();
		}
		return instance;
	}

	@Override
	public int getNumberOfActiveInstances() {
		return getActiveIpAddresses().size();
	}

	@Override
	public Set<String> getActiveIpAddresses() {
		return activeIPAddresses;
	}

	private void initAmazonClient() {

		try {
			awsCredentials = new PropertiesCredentials(new FileInputStream(AWS_CREDENTIALS));
			ec2Client = new AmazonEC2Client(awsCredentials);
			LOG.debug("===========================================");
			LOG.debug("Connected to Amazon AWS!");
			LOG.debug("===========================================");
		} catch (Exception e) {
			LOG.error("Caught login AWS Exception: ", e);
		}

	}

	public AWSCredentials getAWSCredentials() {
		return awsCredentials;
	}

}
