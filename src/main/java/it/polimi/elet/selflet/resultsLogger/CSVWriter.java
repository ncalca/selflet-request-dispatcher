package it.polimi.elet.selflet.resultsLogger;

import it.polimi.elet.selflet.configuration.DispatcherConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Receives an object representing data gathered in a time instant and writes it
 * in the CSV file
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class CSVWriter {

	private static final Logger LOG = Logger.getLogger(CSVWriter.class);

	private static final char CSV_SEPARATOR = '\t';
	private static final char END_LINE = '\n';

	private static final String EPOCH_HEADER = "epoch";
	private static final String ACTIVE_SEFLETS_HEADER = "activeSelflets";
	private static final String UTILIZATION_HEADER = "utilization";
	private static final String UTILIZATION_UPPER_BOUND_HEADER = "upperBound";

	private final List<String> monitoredServices;

	private int currentTimeInterval = 0;
	private FileWriter fileWriter;

	public CSVWriter(List<String> monitoredServices) {
		this.monitoredServices = monitoredServices;
		openLogFile();
		writeHeader();
	}

	private void writeHeader() {
		writeField(EPOCH_HEADER);
		writeField(ACTIVE_SEFLETS_HEADER);
		writeField(UTILIZATION_HEADER);
		writeField(UTILIZATION_UPPER_BOUND_HEADER);

		for (String service : monitoredServices) {
			writeField(service);
		}
		
		endLine();
	}

	private void openLogFile() {
		String filePath = DispatcherConfiguration.csvFilePath + "_" + new Date().getTime() + ".csv";
		File file = new File(filePath);
		try {
			this.fileWriter = new FileWriter(file);
		} catch (IOException e) {
			LOG.error(e);
			throw new IllegalStateException("Cannot open results file: " + DispatcherConfiguration.csvFilePath);
		}
	}

	public void writeResultEntry(ResultEntry resultEntry) {
		writeField(++currentTimeInterval);
		writeField(resultEntry.getActiveSelflets());
		writeField(resultEntry.getAverageUtilization());
		writeField(resultEntry.getAverageUtilizationUpperBound());
		for (String service : resultEntry.getMonitoredServices()) {
			writeField(resultEntry.getResponseTime(service));
		}
		endLine();
	}

	private void writeField(double doubleField) {
		writeField(String.valueOf(doubleField));
	}

	private void writeField(int intField) {
		writeField(String.valueOf(intField));
	}

	private void writeField(String field) {
		try {
			fileWriter.write(field + CSV_SEPARATOR);
		} catch (IOException e) {
			LOG.error(e);
		}
	}

	private void endLine() {
		try {
			fileWriter.write(END_LINE);
			fileWriter.flush();
		} catch (IOException e) {
			LOG.error(e);
		}

	}
}
