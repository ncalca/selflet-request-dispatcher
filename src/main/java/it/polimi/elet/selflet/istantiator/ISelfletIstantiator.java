package it.polimi.elet.selflet.istantiator;

/**
 * Interface to perform operations that istantiate a new selflet
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface ISelfletIstantiator {

	/**
	 * Creates a new selflet
	 * @param template 
	 * 
	 * @return the selflet ID associated with the new selflet
	 * */
	AllocatedSelflet istantiateNewSelflet(String template);

	/**
	 * Creates a new broker and dispatcher
	 * 
	 * @return the IP address of the broker
	 * */
	String istantiateBrokerAndDispatcher();

	void resetAllInstances();

}
