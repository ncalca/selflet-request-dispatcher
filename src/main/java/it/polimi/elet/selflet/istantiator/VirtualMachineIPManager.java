package it.polimi.elet.selflet.istantiator;

import it.polimi.elet.selflet.id.ISelfLetID;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Manages the set of IP addresses which are available for new selflet nodes
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class VirtualMachineIPManager implements IVirtualMachineIPManager {

	private static final Logger LOG = Logger.getLogger(VirtualMachineIPManager.class);

	private static VirtualMachineIPManager instance = null;

	private String DISPATCHER = "DISPATCHER";
	private String EMPTY = "";

	private final Set<String> availableIPAddresses;
	private final Set<String> takenIPAddresses;

	// (IP, SELFLETID) -> TODO replace with better typing
	private final Map<String, String> ipToSelfletIDs;
	private final Map<String, ISelfLetID> ipToActiveSelfLets;

	private String brokerAddress = "";

	public VirtualMachineIPManager() {
		takenIPAddresses = Sets.newCopyOnWriteArraySet();
		availableIPAddresses = Sets.newCopyOnWriteArraySet();
		ipToSelfletIDs = Maps.newConcurrentMap();
		ipToActiveSelfLets = Maps.newConcurrentMap();
	}

	public static IVirtualMachineIPManager getInstance() {
		if (instance == null) {
			instance = new VirtualMachineIPManager();
		}
		return instance;
	}

	@Override
	public String getNewIpAddress() {

		if (availableIPAddresses.isEmpty()) {
			throw new IllegalStateException("Trying to get a new ip address but there are no available ones");
		}

		String newIP = availableIPAddresses.iterator().next();
		availableIPAddresses.remove(newIP);
		takenIPAddresses.add(newIP);

		return newIP;
	}

	public void addNewIp(String newIp) {

		if (takenIPAddresses.contains(newIp)) {
			return;
		}

		synchronized (availableIPAddresses) {
			availableIPAddresses.add(newIp);
		}
	}

	public void resetInstances() {
		availableIPAddresses.addAll(takenIPAddresses);
		takenIPAddresses.clear();
		ipToSelfletIDs.clear();
		ipToActiveSelfLets.clear();
		this.brokerAddress = "";
	}

	@Override
	public void addKnownIPAddresses(Set<String> activeIPAddresses) {
		for (String ipAddress : activeIPAddresses) {
			addNewIp(ipAddress);
		}
	}

	@Override
	public void setDispatcherIpAddress(String ipAddress) {
		brokerAddress = ipAddress;
		takenIPAddresses.add(ipAddress);
		availableIPAddresses.remove(ipAddress);
	}

	@Override
	public boolean isDispatcher(String ipAddress) {
		return brokerAddress.equals(ipAddress);
	}

	@Override
	public String getDispatcherIpAddress() {
		return brokerAddress;
	}

	@Override
	public String getContentOfVM(String ipAddress) {

		if (isDispatcher(ipAddress)) {
			return DISPATCHER;
		}

		if (ipToSelfletIDs.containsKey(ipAddress)) {
			return ipToSelfletIDs.get(ipAddress);
		}

		return EMPTY;
	}
	
	public Set<ISelfLetID> getActiveSelfLets(){
		return Sets.newHashSet(ipToActiveSelfLets.values()) ;
	}

	@Override
	public void setVmToSelfletBinding(String ipAddress, ISelfLetID selfletID) {
		ipToActiveSelfLets.put(ipAddress, selfletID);
		setVmToSelfletBinding(ipAddress, selfletID.toString());
	}

	@Override
	public void setVmToSelfletBinding(String vmIPAddress, String selfletID) {
		ipToSelfletIDs.put(vmIPAddress, selfletID);

	}

	@Override
	public String getIPAddressOfSelflet(ISelfLetID selfletId) {
		BiMap<String, String> idToIpMap = HashBiMap.create(ipToSelfletIDs).inverse();

		if (idToIpMap.containsKey(selfletId)) {
			return idToIpMap.get(selfletId);
		}

		return "Not found";
	}

	@Override
	public boolean isDispatcherSet() {
		return !brokerAddress.isEmpty();
	}

	@Override
	public Set<String> getAllIPAddresses() {
		return Sets.newHashSet(Sets.union(availableIPAddresses, takenIPAddresses));
	}

	@Override
	public Set<String> getAvailableIPs() {
		return Sets.newHashSet(availableIPAddresses);
	}

	@Override
	public void freeIPOfSelflet(ISelfLetID selfletToBeRemoved) {
		for (Entry<String, String> entry : ipToSelfletIDs.entrySet()) {
			String ipAddress = entry.getKey();
			String selfletId = entry.getValue();
			if (selfletToBeRemoved.toString().equals(selfletId)) {
				LOG.info("Freeing IP of selflet: " + selfletToBeRemoved + ". Was at " + ipAddress);
				ipToSelfletIDs.remove(ipAddress);
				ipToActiveSelfLets.remove(ipAddress);
				availableIPAddresses.add(ipAddress);
				takenIPAddresses.remove(ipAddress);
			}
		}
	}
}
