package it.polimi.elet.amazon;

import it.polimi.elet.selflet.istantiator.IVirtualMachineIPManager;

import java.util.List;
import java.util.Set;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.google.common.collect.Sets;

/**
 * Periodically queries amazon to retrive all active ip address of instances
 * 
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class AmazonIPRetriever extends TimerTask {

	private static final Logger LOG = Logger.getLogger(AmazonIPRetriever.class);

	private final AmazonEC2Client ec2;
	private final Set<String> activeIPAddresses;
	private final IVirtualMachineIPManager virtualMachineIPManager;

	public AmazonIPRetriever(AmazonEC2Client ec2, Set<String> activeIPAddresses, IVirtualMachineIPManager virtualMachineIPManager) {
		this.ec2 = ec2;
		this.activeIPAddresses = activeIPAddresses;
		this.virtualMachineIPManager = virtualMachineIPManager;
	}

	@Override
	public void run() {
		LOG.debug("Retrieving Amazon IPs");
		Set<String> newAddresses = getActiveIPAddresses();
		activeIPAddresses.clear();
		activeIPAddresses.addAll(newAddresses);
		virtualMachineIPManager.addKnownIPAddresses(newAddresses);
	}

	private Set<String> getActiveIPAddresses() {
		Set<String> addresses = Sets.newHashSet();
		DescribeInstancesResult instancesDescription = ec2.describeInstances();
		List<Reservation> reservations = instancesDescription.getReservations();

		for (Reservation reservation : reservations) {
			List<Instance> instances = reservation.getInstances();
			for (Instance instance : instances) {
				if (instance != null && instance.getPublicIpAddress() != null) {
					addresses.add(instance.getPublicIpAddress());
				}
			}
		}

		return addresses;
	}

}
