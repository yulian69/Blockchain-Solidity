<%@page import="org.bouncycastle.util.encoders.Hex"%>
<%@page import="solidity.util.Utils"%>
<%@page import="solidity.model.Application"%>
<%@page import="solidity.model.Parameters"%>
<%@page import="solidity.model.ItemDetail"%>
<%@page import="solidity.factory.ParametersFactory"%>
<%@page import="java.util.Map"%>
<%@page import="solidity.factory.SellerFactory"%>
<%@page import="solidity.model.Account"%>
<%			
	Parameters parameters = ParametersFactory.getInstanse(request);	
	String sellerId = parameters.getValueNotNull("sellerId");
	String itemId = parameters.getValueNotNull("itemId");
	
	ItemDetail itemDetail = SellerFactory.getItemDetail(Hex.decode(itemId));
	
	session.setAttribute("page", "item_buy.jsp?itemId="+itemId+"&sellerId="+sellerId);	
	
	//System.out.println(session.getAttribute("page"));
%>
<!doctype html>
<html lang="en">
	<head>
		<jsp:include page="header.jsp"/>		
	</head>
	<body style="margin-top:0px">
		<jsp:include page="menu.jsp"/>		
		<form id="form">
			<table style="border-width:0px; width:800px; margin: 0 auto;">
				<tr>
					<td><img src="<%=Application.getIpfsHTTPUrl() + itemDetail.getItem().getIpfs()%>"></td>
					<td>
						<table style="border-width:0px; margin-left: auto; margin-right: auto;font-family:Verdana;font-size:14px; width:300px">
							<tr>
								<td style="padding-bottom: 15px; padding-top: 55px; font-weight:bold"><%=itemDetail.getItem().getName()%></td>
							</tr>										
							<tr>
								<td style="padding-bottom: 15px;"><%=itemDetail.getItem().getDescription()%></td>
							</tr>
							<tr>
								<td style="padding-bottom: 5px;">Size: <%=itemDetail.getItem().getSize()%></td>
							</tr>
							<tr>
								<td style="padding-bottom: 5px;">In Stock: <%=itemDetail.getQantity()%></td>
							</tr>
							<tr>
								<td style="padding-bottom: 5px;">Price: SCT <%=Utils.formatAmount(itemDetail.getPrice()/100.0)%></td>
							</tr>
							<tr>
								<td style="padding-bottom: 20px;">Qty: <select name="qty"><option value="1">1</option><option value="2">2</option><option value="3">3</option><option value="4">4</option><option value="5">5</option><option value="6">6</option><option value="7">7</option><option value="8">8</option><option value="9">9</option></select></td>
							</tr>
							
							<tr>
								<td style="padding-bottom: 5px;"><input type="button" id="action-button" value="BUY" class="btn btn-primary btn-sm" style="width:125px"> <a href="home.jsp?sellerId=<%=sellerId %>" class="btn btn-primary btn-sm" style="width:125px">CANCEL</a></td>
							</tr>
						</table>
					</td>
				</tr>										
			</table>
			<input type="hidden" name="action" value="item_buy">
			<input type="hidden" name="itemId" value="<%=itemDetail.getItemId()%>">
		</form>
	</body>	
	<script src="./js/utils.js"></script>
	<script type="text/javascript">
		$("#action-button").click(function() {
			var returnValues = ["ok","not_registered"];
			var redirectUrls = ["home.jsp?sellerId=<%=sellerId%>","buyer_add.jsp?itemId=<%=itemId%>&sellerId=<%=sellerId%>"];
			post("BuyerActions",returnValues,redirectUrls);
		});
	</script>
</html>