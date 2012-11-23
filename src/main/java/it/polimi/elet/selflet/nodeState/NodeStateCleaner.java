package it.polimi.elet.selflet.nodeState;

import it.polimi.elet.selflet.configuration.DispatcherConfiguration;
import it.polimi.elet.selflet.threadUtilities.IPeriodicTask;

import java.util.TimerTask;

/**
 * A timer task that periodically invokes the node state manager clean on states
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class NodeStateCleaner extends TimerTask implements IPeriodicTask {

	private final INodeStateManager nodeStateManager = NodeStateManager.getInstance();

	public static final int SLEEP_TIME = DispatcherConfiguration.nodeStateCleaningPeriod * 1000;

	@Override
	public void run() {
		nodeStateManager.cleanOldStates();
	}

	@Override
	public int periodInMsec() {
		return SLEEP_TIME;
	}

}
