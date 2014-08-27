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
<body onload="JavaScript:timedRefresh(5000);">

	<h1>Amazon status</h1>
	<hr>

	<%
		IVirtualMachineIPManager virtualMachineIPManager = VirtualMachineIPManager.getInstance();
		//		ISelfletIstantiator selfletIstantiator = SelfletIstantiator.getInstance();
		IAmazonFrontend amazonFrontend = AmazonFrontend.getInstance();
		Set<String> activeIPAddresses = amazonFrontend.getActiveIpAddresses();
	%>

	<p>
		<b>Number of active instances:</b>
		<%=amazonFrontend.getNumberOfActiveInstances()%></p>

	<p>
		<a href="allocateNew?new_dispatcher=1">Allocate new dispatcher</a>
	</p>
	<hr>
	<%
		ITemplateManager templateManager = TemplateManager.getInstance();
		List<String> templates = templateManager.getTemplates();
		for (String template : templates) {
	%>
	<p>
		<a href="allocateNew?new_selflet=1&template=<%=template%>">Add
			selflet:</a> Template<b> <%=template%></b>
	</p>
	<%
		}
	%>
	<hr>
	<p>
		<b><a href="allocateNew?reset=1">Reset instances</a></b>
	</p>
	<hr>
	<ol>
		<%
			for (String ipAddress : activeIPAddresses) {
				String contents = virtualMachineIPManager.getContentOfVM(ipAddress);
		%>
		<li>
			<p>
				<b><%=ipAddress%> -> <%=contents%></b>
				<%
					if (contents.equals("DISPATCHER")) {
				%>
				<b><a href="http://<%=ipAddress%>:8080">Dashboard</a></b>
				<%
					} else {
				%>
				<b><a href="retrieveLogs?ipAddress=<%=ipAddress%>">Logs</a></b>
				<%
					}
				%>
			</p>
		</li>
		<%
			}
		%>
	</ol>

</BODY>
</HTML>
