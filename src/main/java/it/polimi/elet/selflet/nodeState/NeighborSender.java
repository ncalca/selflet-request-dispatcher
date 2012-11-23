package it.polimi.elet.selflet.nodeState;

import static it.polimi.elet.selflet.message.SelfLetMessageTypeEnum.*;
import it.polimi.elet.selflet.collectionUtils.CollectionUtil;
import it.polimi.elet.selflet.configuration.DispatcherConfiguration;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.message.ISelfletNeighbors;
import it.polimi.elet.selflet.message.MessageBridge;
import it.polimi.elet.selflet.message.RedsMessage;
import it.polimi.elet.selflet.message.SelfLetMsg;
import it.polimi.elet.selflet.message.SelfletNeighbors;
import it.polimi.elet.selflet.negotiation.nodeState.INodeState;
import it.polimi.elet.selflet.threadUtilities.IPeriodicTask;

import java.io.Serializable;
import java.util.Set;
import java.util.TimerTask;

import com.google.common.collect.Sets;

/**
 * This class sends a message to every known selflet to update their knowledge
 * about the neighbors
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class NeighborSender extends TimerTask implements IPeriodicTask {

	private static final int MAX_NEIGHBORS_PER_SELFLET = DispatcherConfiguration.maxNeighborsPerSelflet;
	private static final int SLEEP_TIME = DispatcherConfiguration.neighborsUpdaterSleepPeriodInSec * 1000;

	private final MessageBridge messageBridge = MessageBridge.getInstance();
	private final INodeStateManager nodeStateManager = NodeStateManager
			.getInstance();
	private final ISelfletNeighbors selfletNeighbors = SelfletNeighbors
			.getInstance();

	@Override
	public void run() {
		Set<ISelfLetID> knownSelflets = selfletNeighbors.getNeighbors();
		for (ISelfLetID selflet : knownSelflets) {
			sendNeighborMessageToSelfletIfNecessary(selflet);
		}
	}

	private void sendNeighborMessageToSelfletIfNecessary(ISelfLetID selflet) {
		INodeState nodeState = nodeStateManager.getNodeState(selflet);

		if (!nodeStateManager.haveStateOfSelflet(selflet)) {
			return;
		}

		Set<ISelfLetID> neighborsKnownByThisNode = nodeState
				.getKnownNeighbors();

		if (neighborsKnownByThisNode.size() >= MAX_NEIGHBORS_PER_SELFLET) {
			// there are enough neighbors
			return;
		}

		Set<ISelfLetID> neighborsToBeAdded = selectNeighborsToBeAdded(selflet,
				neighborsKnownByThisNode);

		if (neighborsToBeAdded.isEmpty()) {
			// nothing to send
			return;
		}

		RedsMessage neighborMessage = prepareNeighborMessage(selflet,
				neighborsToBeAdded);
		
		messageBridge.publish(neighborMessage);
	}

	private RedsMessage prepareNeighborMessage(ISelfLetID receiver,
			Set<ISelfLetID> neighbors) {
		
		SelfLetMsg selfletMsg = new SelfLetMsg(MessageBridge.THIS_SELFLET_ID,
				receiver, NEIGHBORS, (Serializable) neighbors);
		
		return new RedsMessage(selfletMsg, receiver.getID().toString());
	}

	private Set<ISelfLetID> selectNeighborsToBeAdded(ISelfLetID selflet,
			Set<ISelfLetID> neighborsAlreadyKnown) {
		
		Set<ISelfLetID> allNeighbors = selfletNeighbors.getNeighbors();
		// remove this selflet
		allNeighbors.remove(selflet);
		Set<ISelfLetID> candidateNeighborsToBeAdded = Sets.difference(
				allNeighbors, neighborsAlreadyKnown);
		int numberOfNeighborsToBeAdded = MAX_NEIGHBORS_PER_SELFLET
				- neighborsAlreadyKnown.size();
		return CollectionUtil.extractAtMostNElementsFromSet(
				candidateNeighborsToBeAdded, numberOfNeighborsToBeAdded);
	}

	@Override
	public int periodInMsec() {
		return SLEEP_TIME;
	}

}
