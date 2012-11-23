package it.polimi.elet.selflet.resultsLogger;

import static it.polimi.elet.selflet.negotiation.nodeState.NodeStateGenericDataEnum.THROUGHPUT;
import static it.polimi.elet.selflet.negotiation.nodeState.NodeStateGenericDataEnum.RESPONSE_TIME;
import it.polimi.elet.selflet.configuration.DispatcherConfiguration;
import it.polimi.elet.selflet.negotiation.nodeState.INodeState;
import it.polimi.elet.selflet.negotiation.nodeState.NodeStateGenericDataEnum;
import it.polimi.elet.selflet.nodeState.INodeStateManager;
import it.polimi.elet.selflet.nodeState.NodeStateManager;
import it.polimi.elet.selflet.threadUtilities.IPeriodicTask;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;

/**
 * Periodic task that reads value gathered from other selflets and sends them to
 * the CSV logger.
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class ResultsLogger extends TimerTask implements IPeriodicTask {

	private static final Logger LOG = Logger.getLogger(ResultsLogger.class);

	private static final int SLEEP_TIME = DispatcherConfiguration.resultLoggerPeriodInSec * 1000;

	private final INodeStateManager nodeStateManager = NodeStateManager.getInstance();
	private final CSVWriter csvWriter;

	private List<String> monitoredServices;

	public ResultsLogger() {
		loadMonitoredServices();
		csvWriter = new CSVWriter(monitoredServices);
	}

	private void loadMonitoredServices() {
		String SEPARATOR = ";";
		String[] monitoredServicesArray = DispatcherConfiguration.monitoredServices.split(SEPARATOR);
		monitoredServices = Lists.newArrayList(monitoredServicesArray);
	}

	@Override
	public void run() {
		try {
			List<INodeState> states = nodeStateManager.getStates();
			ResultEntry resultEntry = createResultEntry(states);
			csvWriter.writeResultEntry(resultEntry);
			LOG.debug("Saving data to file: " + resultEntry);
		} catch (Exception e) {
			LOG.error(e);
		}
	}

	private ResultEntry createResultEntry(List<INodeState> states) {
		ResultEntry resultEntry = new ResultEntry();
		resultEntry.setActiveSelflets(states.size());
		resultEntry.setAverageUtilization(computeAverageUtilization(states));
		resultEntry.setMonitoredServices(monitoredServices);
		setResponseTimes(resultEntry, states);
		return resultEntry;
	}

	private double computeAverageUtilization(List<INodeState> states) {
		double sum = 0;
		int count = 0;

		for (INodeState nodeState : states) {
			sum += nodeState.getUtilization();
			count++;
		}

		if (count == 0) {
			return 0;
		}

		return sum / count;
	}


	private void setResponseTimes(ResultEntry resultEntry, List<INodeState> states) {
		for (String service : monitoredServices) {
			double avgResponseTime = computeAverageResponseTimeForService(service, states);
			resultEntry.setResponseTime(service, avgResponseTime);
		}
	}

	private double computeAverageResponseTimeForService(String service, List<INodeState> states) {
		double weightedSum = 0;
		double totalCompletionRate = 0;

		for (INodeState iNodeState : states) {

			if (!iNodeState.getAvailableServices().contains(service))
				continue;

			double responseTime = extractResponseTimeForService(service, iNodeState);
			double completionRate = extractCompletionRateForService(service, iNodeState);

			weightedSum += responseTime * completionRate;
			totalCompletionRate += completionRate;
		}

		if (totalCompletionRate == 0) {
			return 0;
		}

		double averageResponseTime = weightedSum / totalCompletionRate;
		return averageResponseTime;
	}

	private double extractCompletionRateForService(String service, INodeState iNodeState) {
		return extractDataOfTypeForService(THROUGHPUT, service, iNodeState);
	}

	private double extractResponseTimeForService(String service, INodeState iNodeState) {
		return extractDataOfTypeForService(RESPONSE_TIME, service, iNodeState);
	}

	private double extractDataOfTypeForService(NodeStateGenericDataEnum dataType, String service, INodeState iNodeState) {

		Map<String, Serializable> genericData = iNodeState.getGenericData();
		for (Entry<String, Serializable> entry : genericData.entrySet()) {
			String key = entry.getKey();
			if (key.contains(dataType.toString()) && key.contains(service)) {

				Serializable value = entry.getValue();
				return convertToDouble(value);
			}
		}

		return 0;
	}

	private double convertToDouble(Serializable value) {

		if (value instanceof Double) {
			return (Double) value;
		} else if (value instanceof Long) {
			return new Double((Long) value);
		} else if (value instanceof Integer) {
			return new Double((Integer) value);
		}

		throw new IllegalStateException("Value: " + value + " cannot be forced to a double");
	}

	@Override
	public int periodInMsec() {
		return SLEEP_TIME;
	}

}
