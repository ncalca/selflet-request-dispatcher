package it.polimi.elet.selflet.istantiator;

import it.polimi.elet.selflet.id.ISelfLetID;

import java.util.Set;

/**
 * Interface for virtual machine manager. It manages all know ips and the
 * association between ip and selflet IDs
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IVirtualMachineIPManager {

	/**
	 * Returns a new IP address from the set of the available ones
	 * */
	String getNewIpAddress();

	/**
	 * Sets the IP address used by the request dispatcher
	 * */
	void setDispatcherIpAddress(String ipAddress);

	String getDispatcherIpAddress();

	/**
	 * Resets the association between vms and selflet
	 * */
	void resetInstances();

	/**
	 * Returns true if the broker and the request dispatcher are installed in
	 * the VM at the given ipaddress
	 * */
	boolean isDispatcher(String ipAddress);

	//
	/**
	 * Adds the set of ip address to the set of known ip addresses
	 * */
	void addKnownIPAddresses(Set<String> activeIPAddresses);

	/**
	 * Returns the content of the virtual machine at the given ip
	 * 
	 * Content can be:
	 * <ul>
	 * <li>DISPATCHER</li>
	 * <li>SelfledID</li>
	 * <li>empty string</li>
	 * </ul>
	 * */
	String getContentOfVM(String ipAddress);

	/**
	 * Returns the set of SelfLetsID of current active SelfLets
	 * 
	 * @return set of SelfLetsID of current active SelfLets
	 */
	public Set<ISelfLetID> getActiveSelfLets();

	void setVmToSelfletBinding(String vmIPAddress, String selfletID);

	void setVmToSelfletBinding(String ipAddress, ISelfLetID newSelfletID);

	/**
	 * Returns the IP address associated with the given selflet ID
	 * */
	String getIPAddressOfSelflet(ISelfLetID selfletId);

	/**
	 * Returns true if the dispatcher is already set
	 * */
	boolean isDispatcherSet();

	/**
	 * Returns all known IP addresses
	 * */
	Set<String> getAllIPAddresses();

	Set<String> getAvailableIPs();

	void freeIPOfSelflet(ISelfLetID selfletToBeRemoved);
	
	/**
	 * Set the ip address used by jmeter
	 */
	void setJmeterIpAddress(String ipAddress);
	
	/**
	 * Return true if this IP address is associated to Jmeter
	 */
	boolean isJmeter(String ipAddress);

}
