package it.polimi.elet.selflet.message;

import it.polimi.elet.selflet.configuration.DispatcherConfiguration;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.istantiator.IVirtualMachineIPManager;
import it.polimi.elet.selflet.istantiator.VirtualMachineIPManager;

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

	IVirtualMachineIPManager virtualMachineIPManager;

	private SelfletNeighbors() {
		neighbors = Sets.newCopyOnWriteArraySet();
		virtualMachineIPManager = VirtualMachineIPManager.getInstance();
		// private
	}

	public static ISelfletNeighbors getInstance() {
		return instance;
	}

	public Set<ISelfLetID> getNeighbors() {
		if (!DispatcherConfiguration.isLocal)
			checkNeighborsConsistency();
		return Sets.newHashSet(neighbors);
	}

	public void addNeighbor(ISelfLetID neighbor) {
		neighbors.add(neighbor);
	}

	public void removeNeighbor(ISelfLetID neighbor) {
		neighbors.remove(neighbor);
	}

	private void checkNeighborsConsistency() {
		Set<ISelfLetID> activeSelfLets = virtualMachineIPManager
				.getActiveSelfLets();

		for (ISelfLetID neighbor : neighbors) {
			if (!activeSelfLets.contains(neighbor))
				removeNeighbor(neighbor);
		}

	}

}
