package it.polimi.elet.selflet.initializer;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

/**
 * Initializes the logger
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class LoggerInitializer {

	/**
	 * Initializes the logger
	 * */
	public void init() {
		Logger rootLogger = Logger.getRootLogger();

		if (rootLogger == null) {
			System.err.println("Cannot find root logger");
			return;
		}

		setConsoleAppender(rootLogger);
		setFileAppender(rootLogger);
	}

	private void setFileAppender(Logger rootLogger) {
		FileAppender fileAppender = (FileAppender) rootLogger.getAppender("myFileAppender");

		String logFileName = "./log/output.log";

		fileAppender.setFile(logFileName);
		fileAppender.activateOptions();

		// LOG.addAppender(fileAppender);
		// LOG.addAppender(consoleAppender);
	}

	private void setConsoleAppender(Logger rootLogger) {
		Appender consoleAppender = rootLogger.getAppender("myConsoleAppender");

		if (consoleAppender == null) {
			System.err.println("Cannot find console logger");
			return;
		}
	}

}
