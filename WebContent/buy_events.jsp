<%@page import="java.util.Date"%>
<%@page import="solidity.model.BuyItemEvent"%>
<%@page import="solidity.storage.BuyItemEvents"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.List"%>
<%
List<BuyItemEvent> buyItemEvents = BuyItemEvents.getBuyItemEvents();
SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
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
				<td align="center"><div><label class="label" style="font-size:18px;color:#555555;font-family:Verdana">BUY ITEM EVENTS</label></div></td>
			</tr>			
		</table><br>
		<div class="" style="width:95%; margin-left: auto; margin-right: auto;">			
			<table class="table table-bordered table-striped" style="width:1800px;margin-left:auto;margin-right:auto;font-family:Verdana;font-size:12px;">
				<thead>
		        	<tr style="background-color:#eeeeee">
		        		<th style="text-align:center">BuyerId</th>
						<th style="text-align:center">SellerId</th>
						<th style="text-align:center">ItemId</th>
						<th style="text-align:center">Quantity</th>
						<th style="text-align:center">Tokens</th>
						<th style="text-align:center; width:150px">Date</th>
					</tr>
				</thead>
				<tbody>
				<%
				for (BuyItemEvent buyItemEvent : buyItemEvents) {					
				%>
					<tr>
						<td><%= buyItemEvent.getBuyerId() %></td>
						<td><%= buyItemEvent.getSellerId() %></td>
						<td><%= buyItemEvent.getItemId() %></td>
						<td style="text-align:right"><%= buyItemEvent.getQuantity() %></td>
						<td style="text-align:right"><%= buyItemEvent.getAmount() %></td>
						<td style="text-align:center"><%= dateFormat.format(new Date(buyItemEvent.getTimestamp()*1000)) %></td>
					</tr>
				<%
				}
				%>				
				</tbody>
			</table>			
		</div>	
	</body>	
</html>