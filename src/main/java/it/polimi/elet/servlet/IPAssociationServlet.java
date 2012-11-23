package it.polimi.elet.servlet;

import it.polimi.elet.selflet.istantiator.IVirtualMachineIPManager;
import it.polimi.elet.selflet.istantiator.VirtualMachineIPManager;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import static com.google.common.base.Strings.*;
import static it.polimi.elet.servlet.PageNames.*;

public class IPAssociationServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(IPAssociationServlet.class);

	private static final long serialVersionUID = 1L;

	public final static String DISPATCHER = "dispatcher";
	public final static String VM = "vm";
	public final static String SELFLET_ID = "selfletID";

	private static final IVirtualMachineIPManager virtualMachineIPManager = VirtualMachineIPManager.getInstance();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String dispatcherIpAddress = req.getParameter(DISPATCHER);
		setDispatcherIpAddress(dispatcherIpAddress);

		String vmIPAddress = req.getParameter(VM);
		String selfletID = req.getParameter(SELFLET_ID);
		setVMBinding(vmIPAddress, selfletID);

		resp.sendRedirect(INDEX);
	}

	private void setVMBinding(String theVmIPAddress, String theSelfletID) {
		String vmIPAddress = nullToEmpty(theVmIPAddress);
		String selfletID = nullToEmpty(theSelfletID);

		if (vmIPAddress.isEmpty() || selfletID.isEmpty()) {
			return;
		}
		LOG.debug("Setting VM binding: " + vmIPAddress + "->" + selfletID);
		virtualMachineIPManager.setVmToSelfletBinding(vmIPAddress, selfletID);
	}

	private void setDispatcherIpAddress(String dispatcherIpAddress) {
		String ipAddress = nullToEmpty(dispatcherIpAddress);
		if (ipAddress.isEmpty()) {
			return;
		}

		LOG.debug("Setting dispatcher address: " + ipAddress);
		virtualMachineIPManager.setDispatcherIpAddress(ipAddress);

	}

}
