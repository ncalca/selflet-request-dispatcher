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
		
		Logger activeSelfletsLogger = Logger.getLogger("activeSelfletsLogger");
		if(activeSelfletsLogger == null){
			System.err.println("Cannot find active selflets logger");
		}

		setConsoleAppender(rootLogger);
//		setFileAppender(rootLogger);
//		setActiveSelfletsAppender(activeSelfletsLogger);
	}

	private void setFileAppender(Logger rootLogger) {
		FileAppender fileAppender = (FileAppender) rootLogger.getAppender("myFileAppender");

		String logFileName = "./log/output.log";

		fileAppender.setFile(logFileName);
		fileAppender.activateOptions();

	}
	
	private void setActiveSelfletsAppender(Logger activeSelfletsLogger) {
		FileAppender fileAppender = (FileAppender) activeSelfletsLogger.getAppender("activeSelfletsAppender");

		String logFileName = "./log/activeSelflets.log";

		fileAppender.setFile(logFileName);
		fileAppender.activateOptions();

	}

	private void setConsoleAppender(Logger rootLogger) {
		Appender consoleAppender = rootLogger.getAppender("myConsoleAppender");

		if (consoleAppender == null) {
			System.err.println("Cannot find console logger");
			return;
		}
	}

}
