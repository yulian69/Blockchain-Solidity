<%@page import="solidity.model.TransferEvent"%>
<%@page import="java.util.List"%>
<%@page import="solidity.storage.TransferEvents"%>
<%
List<TransferEvent> transferEvents = TransferEvents.getTransferEvents();
%>
<!doctype html>
<html lang="en">
	<head>
		<jsp:include page="header.jsp"/>		
	</head>
	<body style="margin-top:0px">
		<jsp:include page="menu.jsp"/>		
		<table style="margin-top:50px;border-width:0px;width:100%">
			<tr>
				<td align="center"><div><label class="label" style="font-size:18px;color:#555555;font-family:Verdana">TRANSFER TOKEN EVENTS</label></div></td>
			</tr>			
		</table><br>
		<div class="" style="width:95%; margin-left: auto; margin-right: auto;">			
			<table class="table table-bordered table-striped" style="width:900px;margin-left:auto;margin-right:auto;font-family:Verdana;font-size:12px;">
				<thead>
		        	<tr style="background-color:#eeeeee">
		        		<th style="text-align:center">From</th>
						<th style="text-align:center">To</th>
						<th style="text-align:center">Tokens</th>
					</tr>
				</thead>
				<tbody>
				<%
				for (TransferEvent transferEvent : transferEvents) {
				%>
					<tr>
						<td><%= transferEvent.getFrom() %></td>
						<td><%= transferEvent.getTo() %></td>
						<td style="text-align:right"><%= transferEvent.getTokens() %></td>
					</tr>
				<%
				}
				%>				
				</tbody>
			</table>			
		</div>	
	</body>	
</html>