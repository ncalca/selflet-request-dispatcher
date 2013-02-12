package it.polimi.elet.selflet.initializer;

import it.polimi.elet.selflet.configuration.ConfigurationException;
import it.polimi.elet.selflet.configuration.DispatcherConfiguration;
import it.polimi.elet.selflet.message.MessageBridge;
import it.polimi.elet.selflet.nodeState.NeighborSender;
import it.polimi.elet.selflet.nodeState.NodeStateCleaner;
import it.polimi.elet.selflet.resultsLogger.ResultsLogger;
import it.polimi.elet.selflet.threadUtilities.PeriodicThreadStarter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

/**
 * Initializer of the servlet container
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class ContextInitializer implements ServletContextListener {

	private static final Logger LOG = Logger.getLogger(ContextInitializer.class);

	private static final String CONFIGURATION_PARAMETERS_FILE = "src/main/resources/dispatcher.conf";
	private static final int INITIAL_DELAY = 5 * 1000;

	private PeriodicThreadStarter periodicThreadStarter;

	public void contextDestroyed(ServletContextEvent arg0) {
		LOG.debug("Closed");
		periodicThreadStarter.stop();
	}

	public void contextInitialized(ServletContextEvent arg0) {
		LOG.debug("Initializing context");
		loadConfigurationParameters();
		LoggerInitializer loggerInitalizer = new LoggerInitializer();
		loggerInitalizer.init();
		MessageBridge.init();

		periodicThreadStarter = new PeriodicThreadStarter(INITIAL_DELAY);

		periodicThreadStarter.addPeriodicTask(new NodeStateCleaner());
		periodicThreadStarter.addPeriodicTask(new NeighborSender());
		periodicThreadStarter.addPeriodicTask(new ResultsLogger());

		periodicThreadStarter.start();
		LOG.debug("Context initialized");
	}

	private void loadConfigurationParameters() {
		try {
			DispatcherConfiguration.getSingleton().loadFromFile(CONFIGURATION_PARAMETERS_FILE);
		} catch (ConfigurationException e) {
			LOG.error(e);
		}

	}

}
