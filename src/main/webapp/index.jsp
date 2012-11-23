<%@page import="it.polimi.elet.amazon.IAmazonFrontend"%>
<%@page import="it.polimi.elet.amazon.AmazonFrontend"%>
<%@ page import="it.polimi.elet.selflet.*"%>
<%@ page import="it.polimi.elet.selflet.id.*"%>
<%@ page import="it.polimi.elet.selflet.message.*"%>
<%@ page import="it.polimi.elet.selflet.negotiation.nodeState.*"%>
<%@ page import="it.polimi.elet.selflet.nodeState.*"%>
<%@ page
	import="it.polimi.elet.selflet.istantiator.IVirtualMachineIPManager"%>
<%@ page
	import="it.polimi.elet.selflet.istantiator.VirtualMachineIPManager"%>

<%@ page import="java.util.*"%>
<%@ page import="java.io.Serializable"%>
<%@ page import="java.util.Map.*"%>
<%@ page import="java.util.List.*"%>
<%@ page import="java.net.*"%>

<html>

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
	<h1>Selflet request dispatcher</h1>
	<hr>
	<p>
		<b>Address:</b>
		<%=InetAddress.getLocalHost()%>
	</p>

	<ul>
		<li><a href="amazon.jsp">Amazon Status</a></li>
		<br />
		<li><a href="/results">Results</a></li>
		<br />
		<li><a href="/resetAllInstances">Reset all instances</a></li>
		<br />
	</ul>
	<%
		ISelfletNeighbors selfletNeighbors = SelfletNeighbors.getInstance();
		Set<ISelfLetID> neighbors = selfletNeighbors.getNeighbors();
		IVirtualMachineIPManager virtualMachineIPManager = VirtualMachineIPManager.getInstance();
	%>
	<h2>
		Active selflets:
		<%=neighbors.size()%></h2>
	<h2>
		Available IPs:
		<%=virtualMachineIPManager.getAvailableIPs().size()%></h2>

	<p>
		<%
			for (ISelfLetID selfletID : neighbors) {
		%>
		<%=selfletID%>
		<%
			}
		%>
	</p>

	<h2>Details:</h2>

	<%
		INodeStateManager nodeStateManager = NodeStateManager.getInstance();

		for (ISelfLetID selfletID : neighbors) {
			if (!nodeStateManager.haveStateOfSelflet(selfletID)) {
				return;
			}
			INodeState nodeState = nodeStateManager.getNodeState(selfletID);
	%>
	<hr>
	<h4>
		Selflet ID:
		<%=selfletID%></h4>
	<!-- <h4>IP Address: <%=virtualMachineIPManager.getIPAddressOfSelflet(selfletID)%> </h4> -->
	<b>Neighbors: </b><%=nodeState.getKnownNeighbors()%>
	<br />
	<p>Send service request:</p>

	<%
		for (String service : nodeState.getAvailableServices()) {
	%>

	<a href="/dispatcher?receiver=<%=selfletID%>&service=<%=service%>"><%=service%></a>
	<%
		}
	%>
	<br />
	<br />
	<center>
		<table border="1">
			<tr>
				<th colspan="2">Generic data for Selflet <%=selfletID%>
				</th>
			<tr>
				<th colspan="2">Last update <%=nodeState.getTimeStamp()%>
				</th>
				<%
					Map<String, Serializable> genericData = nodeState.getGenericData();
						List<String> keys = new ArrayList(genericData.keySet());
						Collections.sort(keys);
						for (String key : keys) {
				%>
			
			<tr>
				<td><b> <%=key%>
				</b></td>
				<td><%=genericData.get(key)%></td>
			</tr>

			<%
				}
			%>
		</table>
	</center>
	<br />
	<%
		}
	%>

</BODY>
</HTML>
