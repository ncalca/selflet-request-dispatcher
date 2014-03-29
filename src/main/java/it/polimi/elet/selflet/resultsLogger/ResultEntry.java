package it.polimi.elet.selflet.resultsLogger;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * A class representing all results for a given period interval
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class ResultEntry {

	private int activeSelflets;
	private List<String> monitoredServices;
	private Map<String, Double> responseTimes = Maps.newHashMap();

	private double averageUtilization;
	private double averageUtilizationUpperBound;

	public List<String> getMonitoredServices() {
		return monitoredServices;
	}

	public void setActiveSelflets(int activeSelflets) {
		this.activeSelflets = activeSelflets;
	}

	public int getActiveSelflets() {
		return activeSelflets;
	}

	public void setMonitoredServices(List<String> monitoredServices) {
		this.monitoredServices = monitoredServices;
	}

	public void setResponseTime(String service, double avgResponseTime) {
		responseTimes.put(service, avgResponseTime);
	}

	public String getResponseTime(String service) {
		return responseTimes.get(service).toString();
	}

	@Override
	public String toString() {
		return "Active: " + activeSelflets + ", monitored: "
				+ monitoredServices + ", responseTimes: " + responseTimes;
	}

	public void setAverageUtilization(double averageUtilization) {
		this.averageUtilization = averageUtilization;
	}

	public double getAverageUtilization() {
		return this.averageUtilization;
	}

	public void setAverageUtilizationUpperBound(
			double averageUtilizationUpperBound) {
		this.averageUtilizationUpperBound = averageUtilizationUpperBound;
	}

	public double getAverageUtilizationUpperBound() {
		return this.averageUtilizationUpperBound;
	}
}
