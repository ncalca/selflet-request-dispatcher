package it.polimi.elet.selflet.istantiator;

import it.polimi.elet.selflet.id.ISelfLetID;

/**
 * Simple storage class that holds an IP and the selflet ID that is deployed on
 * that VM
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class AllocatedSelflet {

	private final String ipAddress;

	private final ISelfLetID selfletID;

	public AllocatedSelflet(String ipAddress, ISelfLetID selfletID) {
		this.ipAddress = ipAddress;
		this.selfletID = selfletID;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public ISelfLetID getSelfletID() {
		return selfletID;
	}

}
