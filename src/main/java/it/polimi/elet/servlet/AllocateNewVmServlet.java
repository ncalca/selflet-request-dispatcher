package it.polimi.elet.servlet;

import static com.google.common.base.Strings.nullToEmpty;
import it.polimi.elet.selflet.istantiator.AllocatedSelflet;
import it.polimi.elet.selflet.istantiator.ISelfletIstantiator;
import it.polimi.elet.selflet.istantiator.SelfletIstantiator;
import it.polimi.elet.selflet.istantiator.VirtualMachineIPManager;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;

/**
 * This Servlet is used to allocate a new VM after a get request. It is used
 * only to allocate the first VM from the web interface
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class AllocateNewVmServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(AllocateNewVmServlet.class);

	private static final long serialVersionUID = 1L;

	private static final ISelfletIstantiator selfletIstantiator = SelfletIstantiator.getInstance();
	private static final VirtualMachineIPManager virtualMachineIpGenerator = new VirtualMachineIPManager();

	private static final String NEW_SELFLET = "new_selflet";
	private static final String NEW_DISPATCHER = "new_dispatcher";
	private static final String RESET = "reset";
	private static final String TEMPLATE = "template";
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String numberOfSelflets = nullToEmpty(request.getParameter(NEW_SELFLET));
		String template = nullToEmpty(request.getParameter(TEMPLATE));
		String newDispatcher = nullToEmpty(request.getParameter(NEW_DISPATCHER));
		String reset = nullToEmpty(request.getParameter(RESET));

		if (!reset.isEmpty()) {
			resetInstances();
		}

		if (!numberOfSelflets.isEmpty()) {
			List<AllocatedSelflet> ids = newSelflets(numberOfSelflets,template);
			if (ids.size() > 1) {
				throw new IllegalStateException("Multiple serlflet istantiation not implemented yet!");
			}

//			AllocatedSelflet allocatedSelflet = ids.get(0);
//			String locationPrefix = "http://" + virtualMachineIpGenerator.getDispatcherIpAddress() + ":8080/";
//			String location = locationPrefix + ASSOCIATION_SERVLET + "?" + IPAssociationServlet.VM + "=" + allocatedSelflet.getIpAddress() + "&"
//					+ IPAssociationServlet.SELFLET_ID + "=" + allocatedSelflet.getSelfletID().toString();
//			performHttpRequestTo(location);
		}

		if (!newDispatcher.isEmpty()) {
			// String ipAddressForDispatcher =
			allocateBrokerAndDispatcher();
			// String location = "http://" + ipAddressForDispatcher + ":8080/" +
			// ASSOCIATION_SERVLET + "?" + IPAssociationServlet.DISPATCHER + "="
			// + ipAddressForDispatcher;
			// performHttpRequestTo(location);
		}

		response.sendRedirect(PageNames.INDEX);
	}

	private void performHttpRequestTo(String location) {
		// URL url;
		// try {
		// url = new URL(location);
		// InputStream stream = url.openStream();
		// stream.close();
		// } catch (MalformedURLException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
//		try {
//			Runtime.getRuntime().exec("wget " + location + " > /dev/null");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	private void resetInstances() {
		virtualMachineIpGenerator.resetInstances();
		selfletIstantiator.resetAllInstances();
	}

	private String allocateBrokerAndDispatcher() {
		return selfletIstantiator.istantiateBrokerAndDispatcher();
	}

	private List<AllocatedSelflet> newSelflets(String numberOfSelflets, String template) {
		int number = 0;
		try {
			number = Integer.valueOf(numberOfSelflets);
		} catch (NumberFormatException e) {
			LOG.error(e);
		}
		return allocateVMs(number,template);
	}

	private List<AllocatedSelflet> allocateVMs(int number, String template) {
		List<AllocatedSelflet> allocatedSelflets = Lists.newArrayList();
		for (int i = 0; i < number; i++) {
			AllocatedSelflet allocatedSelflet = selfletIstantiator.istantiateNewSelflet(template);
			allocatedSelflets.add(allocatedSelflet);
		}
		return allocatedSelflets;
	}
}
