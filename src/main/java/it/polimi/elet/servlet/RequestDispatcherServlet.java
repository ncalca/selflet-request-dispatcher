package it.polimi.elet.servlet;

import it.polimi.elet.selflet.exceptions.NotFoundException;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.id.SelfLetID;
import it.polimi.elet.selflet.message.ISelfletNeighbors;
import it.polimi.elet.selflet.message.MessageBridge;
import it.polimi.elet.selflet.message.RedsMessage;
import it.polimi.elet.selflet.message.SelfLetMessageTypeEnum;
import it.polimi.elet.selflet.message.SelfLetMsg;
import it.polimi.elet.selflet.message.SelfletNeighbors;
import it.polimi.elet.selflet.negotiation.ServiceExecutionParameter;
import it.polimi.elet.selflet.nodeState.INodeStateManager;
import it.polimi.elet.selflet.nodeState.NodeStateManager;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import static com.google.common.base.Strings.*;

@SuppressWarnings("serial")
public class RequestDispatcherServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(RequestDispatcherServlet.class);

	private static final MessageBridge messageBridge = MessageBridge.getInstance();
	private static final INodeStateManager nodeStateManager = NodeStateManager.getInstance();

	private static final String RECEIVER = "receiver";
	private static final String SERVICE = "service";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String receiver = nullToEmpty(request.getParameter(RECEIVER));
		String serviceName = nullToEmpty(request.getParameter(SERVICE));

		sendRequestToReceiver(receiver, serviceName);
		// response.sendRedirect(INDEX);
	}

	private void sendRequestToReceiver(String receiver, String serviceName) {

		if (serviceName.isEmpty()) {
			return;
		}

		if (receiver.equalsIgnoreCase("ALL")) {
			sendToAllSelflets(serviceName);
		} else {
			try {
				ISelfLetID receiverID = getReceiver(receiver, serviceName);
				LOG.debug("Dispatching request for service " + serviceName + " to selflet " + receiverID);
				sendToSelflet(receiverID, serviceName);
			} catch (NotFoundException e) {
				LOG.error("Cannot find receiver for service " + serviceName);
			}
		}
	}

	private ISelfLetID getReceiver(String receiver, String serviceName) {

		if (!receiver.isEmpty()) {
			return new SelfLetID(receiver);
		}

		return nodeStateManager.getRandomSelfletHavingService(serviceName);
	}

	private void sendToSelflet(ISelfLetID receiver, String serviceName) {
		RedsMessage message = prepareMessage(receiver, serviceName);
		messageBridge.publish(message);
	}

	private void sendToAllSelflets(String serviceName) {
		ISelfletNeighbors selfletNeighbors = SelfletNeighbors.getInstance();
		Set<ISelfLetID> neighbors = selfletNeighbors.getNeighbors();
		List<RedsMessage> messages = prepareMessages(neighbors, serviceName);
		messageBridge.publish(messages);
	}

	private List<RedsMessage> prepareMessages(Set<ISelfLetID> neighbors, String serviceName) {
		List<RedsMessage> redsMessages = Lists.newArrayList();
		for (ISelfLetID neighbor : neighbors) {
			RedsMessage message = prepareMessage(neighbor, serviceName);
			redsMessages.add(message);
		}
		return redsMessages;
	}

	private RedsMessage prepareMessage(ISelfLetID neighbor, String serviceName) {
		ISelfLetID receiverSelfLetID = neighbor;
		Map<String, Object> parameters = Maps.newHashMap();
		ServiceExecutionParameter selfletExecutionParameters = new ServiceExecutionParameter(serviceName, parameters);
		SelfLetMsg selfletMsg = new SelfLetMsg(MessageBridge.THIS_SELFLET_ID, receiverSelfLetID, SelfLetMessageTypeEnum.EXECUTE_ACHIEVABLE_SERVICE,
				selfletExecutionParameters);
		Set<String> recipients = Sets.newHashSet(receiverSelfLetID.toString());
		return new RedsMessage(selfletMsg, recipients);
	}
}
