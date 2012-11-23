package it.polimi.elet.amazon;

import java.util.Set;

import com.amazonaws.auth.AWSCredentials;

/**
 * Interface for Amazon main operation and data
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface IAmazonFrontend {

	/**
	 * Returns the credentials used for amazon
	 * */
	AWSCredentials getAWSCredentials();

	/**
	 * Returns the number of active EC2 instances
	 * */
	int getNumberOfActiveInstances();

	/**
	 * Returns the set of IPs corresponding to active virtual machines
	 * */
	Set<String> getActiveIpAddresses();

}
