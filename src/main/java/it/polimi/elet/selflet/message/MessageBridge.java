package it.polimi.elet.selflet.message;

import it.polimi.elet.selflet.configuration.DispatcherConfiguration;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.id.SelfLetID;
import it.polimi.elet.thread.ThreadPool;

import java.net.ConnectException;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableSet;
import static it.polimi.elet.selflet.message.SelfLetMessageTypeEnum.*;

import polimi.reds.Filter;
import polimi.reds.TCPDispatchingService;

public class MessageBridge {

	private static final Logger LOG = Logger.getLogger(MessageBridge.class);

	public static final ISelfLetID THIS_SELFLET_ID = new SelfLetID(DispatcherConfiguration.dispatcherID);

	private static final String redsAddress = DispatcherConfiguration.redsAddress;
	private static final int port = DispatcherConfiguration.redsPort;

	private static MessageBridge singleton;
	private static TCPDispatchingService dispatchingService;

	private MessageBridge() {
	}

	public static MessageBridge getInstance() {
		if (singleton == null) {
			singleton = new MessageBridge();
		}
		return singleton;
	}

	public static void init() {

		dispatchingService = new TCPDispatchingService(redsAddress, port);

		try {
			dispatchingService.open();
		} catch (ConnectException e) {
			LOG.error("Error while connecting to reds.", e);
		}

		if (dispatchingService.isOpened()) {
			LOG.debug("Connected to reds");
		} else {
			LOG.error("Not connected to reds");
			return;
		}

		subscribeToActiveMessages();
		startMsgDispatcher();
	}

	private static void startMsgDispatcher() {
		MessageDispatchingThread dispatchingThread = new MessageDispatchingThread(dispatchingService);
		ThreadPool.submitGenericJob(dispatchingThread);
	}

	private static void subscribeToActiveMessages() {
		Filter typeFilter = new RedsMessageTypeFilter(ImmutableSet.of(ALIVE_SELFLET, NODE_STATE, ISTANTIATE_NEW_SELFLET, REMOVE_SELFLET));
		dispatchingService.subscribe(typeFilter);
	}

	public void publish(RedsMessage redsMessage) {
		if (!dispatchingService.isOpened()) {
			throw new IllegalStateException("Dispatching service is closed");
		}
		// LOG.debug("Sending " + redsMessage);
		dispatchingService.publish(redsMessage);
	}

	public void publish(List<RedsMessage> messages) {
		for (RedsMessage redsMessage : messages) {
			publish(redsMessage);
		}
	}

}
