package it.polimi.elet.servlet;

import it.polimi.elet.selflet.istantiator.ISelfletIstantiator;
import it.polimi.elet.selflet.istantiator.SelfletIstantiator;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet is only used to reset all existing instances
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class DeploySelfletsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final ISelfletIstantiator selfletIstantiator = SelfletIstantiator.getInstance();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		selfletIstantiator.resetAllInstances();
		resp.sendRedirect(PageNames.INDEX);
	}
}
