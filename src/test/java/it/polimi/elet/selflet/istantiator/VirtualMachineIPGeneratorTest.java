package it.polimi.elet.selflet.istantiator;

import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;

import static junit.framework.Assert.*;

public class VirtualMachineIPGeneratorTest {

	private VirtualMachineIPManager virtualMachineIPGenerator;

	@Test
	public void getAllAvailableIPs() {
		Set<String> ips = Sets.newHashSet("192.168.1.1", "192.168.1.2", "192.168.1.3");
		virtualMachineIPGenerator = new VirtualMachineIPManager();
		virtualMachineIPGenerator.addKnownIPAddresses(ips);
		Set<String> receivedIPAddresses = Sets.newHashSet();
		receivedIPAddresses.add(virtualMachineIPGenerator.getNewIpAddress());
		receivedIPAddresses.add(virtualMachineIPGenerator.getNewIpAddress());
		receivedIPAddresses.add(virtualMachineIPGenerator.getNewIpAddress());

		assertEquals(3, receivedIPAddresses.size());
	}

	@Test(expected = IllegalStateException.class)
	public void getIPsWhenThereAreNoAvailableIPs() {
		Set<String> ips = Sets.newHashSet();
		virtualMachineIPGenerator = new VirtualMachineIPManager();
		virtualMachineIPGenerator.addKnownIPAddresses(ips);

		Set<String> receivedIPAddresses = Sets.newHashSet();
		receivedIPAddresses.add(virtualMachineIPGenerator.getNewIpAddress());
	}

}
