package it.polimi.elet.selflet.message;

import it.polimi.elet.selflet.id.ISelfLetID;

import java.util.Set;

import com.google.common.collect.Sets;

/**
 * A class to manage all known neighbors by the dispatcher
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>~
 * */
public class SelfletNeighbors implements ISelfletNeighbors {

	private final static SelfletNeighbors instance = new SelfletNeighbors();

	private final Set<ISelfLetID> neighbors;

	private SelfletNeighbors() {
		neighbors = Sets.newCopyOnWriteArraySet();
		// private
	}

	public static ISelfletNeighbors getInstance() {
		return instance;
	}

	public Set<ISelfLetID> getNeighbors() {
		return Sets.newHashSet(neighbors);
	}

	public void addNeighbor(ISelfLetID neighbor) {
		neighbors.add(neighbor);
	}

	public void removeNeighbor(ISelfLetID neighbor) {
		neighbors.remove(neighbor);
	}

}
