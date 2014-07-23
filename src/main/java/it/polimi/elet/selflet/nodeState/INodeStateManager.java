package it.polimi.elet.selflet.nodeState;

import java.util.List;

import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.negotiation.nodeState.INodeState;

/**
 * Interface containing the main operations to deal with node states retrieved
 * by the dispatching tier
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface INodeStateManager {

	/**
	 * Returns all the node states
	 * */
	List<INodeState> getStates();

	/**
	 * Adds a received state in the manager
	 * */
	void addState(INodeState content);

	/**
	 * Retrieves the node state for the given selflet
	 * */
	INodeState getNodeState(ISelfLetID selfletID);

	/**
	 * Returns true if the manager has information about the given selflet
	 * */
	boolean haveStateOfSelflet(ISelfLetID selflet);

	/**
	 * Removes states which are old (more than a given threshold)
	 * */
	void cleanOldStates();

	/**
	 * Removes information about the given selflet
	 * */
	void removeNodeStateOfNeighbor(ISelfLetID selflet);

	/**
	 * Returns a selflet that offers the given service
	 * */
	ISelfLetID getRandomSelfletHavingService(String serviceName);
	
	/**
	 * check if the neighborhood of the selflet is full
	 * 
	 */
	boolean isNeighborhoodFull(ISelfLetID selflet);

}
