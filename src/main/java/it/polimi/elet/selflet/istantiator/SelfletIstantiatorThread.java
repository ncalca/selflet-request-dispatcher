package it.polimi.elet.selflet.istantiator;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import polimi.reds.TCPDispatchingService;

import com.google.common.collect.Sets;

import it.polimi.elet.selflet.configuration.DispatcherConfiguration;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.message.MessageBridge;
import it.polimi.elet.selflet.message.RedsMessage;
import it.polimi.elet.selflet.message.SelfLetMessageTypeEnum;
import it.polimi.elet.selflet.message.SelfLetMsg;

public class SelfletIstantiatorThread extends Thread {

	private static final Logger LOG = Logger
			.getLogger(SelfletIstantiatorThread.class);
	private static final long MINIMUM_TIME_FREE_SLOTS = DispatcherConfiguration.minimumTimeToFreeInstantiationSlots * 1000;
	private static final int SLOTS = DispatcherConfiguration.maxInstantiationSlots;

	private static final String DEFAULT_TEMPLATE = DispatcherConfiguration.defaultProjectTemplate;
	private static final String COMPLETE_TEMPLATE = DispatcherConfiguration.completeProjectTemplate;

	private static volatile long lastSlotsReset = 0;
	private static AtomicInteger availableSlots = new AtomicInteger(SLOTS);

	private final ISelfletIstantiator selfletIstantiator = SelfletIstantiator
			.getInstance();
	private final SelfLetMsg selfletMessage;
	private final TCPDispatchingService dispatchingService;
	private final String selfletTemplate;

	public SelfletIstantiatorThread(TCPDispatchingService dispatchingService,
			SelfLetMsg selfletMessage) {
		this.selfletMessage = selfletMessage;
		this.dispatchingService = dispatchingService;
		this.selfletTemplate = DEFAULT_TEMPLATE;
	}

	@Override
	public void run() {
		// if (recentlyIstantiatedNewSelflet()) {
		// LOG.debug("Skipping selflet istantiation. Already instantiated recently");
		// return;
		// }
		// LOG.debug("Istantiating new selflet");

		if (isTimeToFreeSlots()) {
			lastSlotsReset = System.currentTimeMillis();
			availableSlots.set(SLOTS);
		}

		InstnantiateIfPossible();
	}

	private boolean isTimeToFreeSlots() {
		long now = System.currentTimeMillis();
		long elapsed = (now - lastSlotsReset);
		return elapsed > MINIMUM_TIME_FREE_SLOTS;
	}

	private void InstnantiateIfPossible() {
		try {
			if (availableSlots.getAndDecrement() > 0) {
				selfletIstantiator.istantiateNewSelflet(selfletTemplate);
			}
		} catch (IllegalStateException e) {
			LOG.error("No more IPs available", e);
			availableSlots.incrementAndGet();
		}
	}

	//TO BE DELETED
//	private boolean recentlyIstantiatedNewSelflet() {
//		long now = System.currentTimeMillis();
//		long elapsed = (now - lastSlotsReset);
//		return elapsed < MINIMUM_TIME_FREE_SLOTS;
//	}
//
//	private void replyToSelflet(ISelfLetID newSelfletID) {
//		RedsMessage reply = createReply(selfletMessage.getFrom(), newSelfletID);
//		dispatchingService.reply(reply, selfletMessage.getId());
//	}
//
//	private RedsMessage createReply(ISelfLetID receiver, ISelfLetID newSelfletID) {
//		SelfLetMsg selfletMsg = new SelfLetMsg(MessageBridge.THIS_SELFLET_ID,
//				receiver, SelfLetMessageTypeEnum.ISTANTIATE_NEW_SELFLET_REPLY,
//				newSelfletID);
//		return new RedsMessage(selfletMsg, Sets.newHashSet(receiver.toString()));
//	}

}
