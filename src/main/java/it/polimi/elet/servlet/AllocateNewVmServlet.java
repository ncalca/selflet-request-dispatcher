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

	private static final Logger LOG = Logger
			.getLogger(AllocateNewVmServlet.class);

	private static final long serialVersionUID = 1L;

	private static final ISelfletIstantiator selfletIstantiator = SelfletIstantiator
			.getInstance();
	private static final VirtualMachineIPManager virtualMachineIpGenerator = new VirtualMachineIPManager();

	private static final String NEW_SELFLET = "new_selflet";
	private static final String NEW_DISPATCHER = "new_dispatcher";
	// private static final String RESET = "reset";
	private static final String TEMPLATE = "template";

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String numberOfSelflets = nullToEmpty(request.getParameter(NEW_SELFLET));
		String template = nullToEmpty(request.getParameter(TEMPLATE));
		String newDispatcher = nullToEmpty(request.getParameter(NEW_DISPATCHER));

		if (!numberOfSelflets.isEmpty()) {
			int numberOfSelfletsInt = Integer.parseInt(numberOfSelflets);
			selfletIstantiator.instantiateMultipleSelflets(numberOfSelfletsInt,
					template);
		}

		if (!newDispatcher.isEmpty()) {
			allocateBrokerAndDispatcher();
		}

		response.sendRedirect(PageNames.INDEX);
	}

	private String allocateBrokerAndDispatcher() {
		return selfletIstantiator.istantiateBrokerAndDispatcher();
	}

}
