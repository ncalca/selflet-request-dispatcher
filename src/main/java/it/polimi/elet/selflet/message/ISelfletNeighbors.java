package it.polimi.elet.selflet.message;

import java.util.Set;

import it.polimi.elet.selflet.id.ISelfLetID;

/**
 * Main interface that contains information about the neighbors
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public interface ISelfletNeighbors {

	/**
	 * Adds a neighbor to the list of neighbors
	 * */
	void addNeighbor(ISelfLetID from);

	/**
	 * Removes a neighbor from the list of known neighbor
	 * */
	void removeNeighbor(ISelfLetID key);

	/**
	 * Retrieves the set of known neighbors
	 * */
	Set<ISelfLetID> getNeighbors();
}
