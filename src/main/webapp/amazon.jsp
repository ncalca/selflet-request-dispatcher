<%@page import="it.polimi.elet.selflet.template.TemplateManager"%>
<%@page import="it.polimi.elet.selflet.template.ITemplateManager"%>
<%@ page
	import="it.polimi.elet.selflet.istantiator.IVirtualMachineIPManager"%>
<%@ page
	import="it.polimi.elet.selflet.istantiator.VirtualMachineIPManager"%>
<%@ page import="it.polimi.elet.amazon.*"%>
<%@ page import="java.util.*"%>

<HTML>
<head>
<script type="text/JavaScript">
<!--
	function timedRefresh(timeoutPeriod) {
		setTimeout("location.reload(true);", timeoutPeriod);
	}
//   -->
</script>
</head>
<body onload="JavaScript:timedRefresh(30000);">

	<h1>Amazon status</h1>
	<hr>

	<%
		IVirtualMachineIPManager virtualMachineIPManager = VirtualMachineIPManager
				.getInstance();
		//		ISelfletIstantiator selfletIstantiator = SelfletIstantiator.getInstance();
		IAmazonFrontend amazonFrontend = AmazonFrontend.getInstance();
		Set<String> activeIPAddresses = amazonFrontend
				.getActiveIpAddresses();
	%>

	<p>
		<b>Number of active instances:</b>
		<%=amazonFrontend.getNumberOfActiveInstances()%></p>

	<p>
		<a href="allocateNew?new_dispatcher=1">Allocate new dispatcher</a>
	</p>
	<hr>
	<b>Selflet allocation</b>
	<br>
	<p><form action="allocateNew?">
		template: <select name="template">
			<%
				ITemplateManager templateManager = TemplateManager.getInstance();
				List<String> templates = templateManager.getTemplates();
				for (String template : templates) {
			%>
			<option value=<%=template%>><%=template%></option>
			<%
				}
			%>
		</select> Number of selflets: <input type="text" name="new_selflet" value="1">
		<input type="submit" value="Submit" /></form>
	</p>
	<p>
		<b>Jmeter start</b><br>
	<form action="startJmeter?">
		<%
			String dispIpAddr = virtualMachineIPManager
					.getDispatcherIpAddress();
		%>
		Dispatcher IP address:<input type="text" name="disp_ip_addr"
			value=<%=dispIpAddr%>><%=dispIpAddr%>
		<input type="submit" value="Start" />
	</form>
	</p>
	<hr>
	<p>
		<b><a href="allocateNew?reset=1">Reset instances</a></b>
	</p>
	<p>
		<b><a href="/logs">Selflets Logs</a></b>
	</p>
	<hr>
	<ol>
		<%
			String ipAddresses = "";
			for (String ipAddress : activeIPAddresses) {
				String contents = virtualMachineIPManager
						.getContentOfVM(ipAddress);
		%>
		<li>
			<p>
				<b><%=ipAddress%> -> <%=contents%></b>
				<%
					if (contents.equals("DISPATCHER")) {
				%>
				<b><a href="http://<%=ipAddress%>:8080">Dashboard</a></b>
				<%
					} else if (contents.equals("JMETER")) {
				%>
				sending load to
				<%=dispIpAddr%>
				<%
					} else {
				%>
				<b><a
					href="retrieveLogs?ipAddressesList=<%=ipAddress%>&getLogs=true">Get
						logs</a></b> <b><a
					href="retrieveLogs?ipAddressesList=<%=ipAddress%>&clearLogs=true">Clear
						logs</a></b>
			</p> <%
 	}
 %>
		</li>
	</ol>
	<p>
		<b><a
			href="retrieveLogs?ipAddressesList=<%=ipAddresses%>&getLogs=true">Get
				all logs</a></b>
	</p>
	<p>
		<b><a
			href="retrieveLogs?ipAddressesList=<%=ipAddresses%>&clearLogs=true">Clear
				all logs</a></b>
	</p>
	</p>
	<%
		}
	%>

</BODY>
</HTML>
