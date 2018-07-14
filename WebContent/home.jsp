<%@page import="solidity.util.Utils"%>
<%@page import="solidity.model.Application"%>
<%@page import="solidity.model.Parameters"%>
<%@page import="solidity.model.ItemDetail"%>
<%@page import="solidity.factory.ParametersFactory"%>
<%@page import="java.util.Map"%>
<%@page import="solidity.factory.SellerFactory"%>
<%@page import="solidity.model.Account"%>
<%		
	session.setAttribute("page", "home.jsp");
	Map<String, String> sellers = SellerFactory.getSellers();
	
	Parameters parameters = ParametersFactory.getInstanse(request);
	
	ItemDetail[] itemDetails = SellerFactory.getItemDetails(parameters.getValueNotNull("sellerId"));
	int rows = itemDetails.length/3;
	if ( itemDetails.length % 3 > 0 ) {
		rows++;
	}
	int counter = 0;
%>
<!doctype html>
<html lang="en">
	<head>
		<jsp:include page="header.jsp"/>		
	</head>
	<body style="margin-top:0px">
		<jsp:include page="menu.jsp"/>		
		
			<table style="border-width:0px; margin-left: auto; margin-right: auto;font-family:Verdana; width: 100%">
				<tr>
					<td style="width:300px;vertical-align:top;">
						<div class="list-group">
						<%
						for (String sellerId : sellers.keySet()) {
						%>
						  <a href="home.jsp?sellerId=<%=sellerId %>" class="list-group-item list-group-item-action"><%=sellers.get(sellerId) %> </a>						  
						<%
						}
						%>
						</div>
					</td>
					<td>
					<table style="border-width:0px; margin-left: auto; margin-right: auto;font-family:Verdana; width: 100%">
							<%
							for (int i = 0; i < rows; i ++) {
							%>				
							<tr>
							<%
							for (int j = 0; j < 3; j++) {
								if (counter < itemDetails.length) {
							%>
								<td>
									<table style="border-width:0px; width:520px">
										<tr>
											<td><img style="height:300px" src="<%=Application.getIpfsHTTPUrl() + itemDetails[counter].getItem().getIpfs()%>"></td>
											<td>
												<table style="border-width:0px; margin-left: auto; margin-right: auto;font-family:Verdana;font-size:12px">
													<tr>
														<td style="padding-bottom: 15px; padding-top: 55px; font-weight:bold"><%=itemDetails[counter].getItem().getName()%></td>
													</tr>										
													<tr>
														<td style="padding-bottom: 15px;"><%=itemDetails[counter].getItem().getDescription()%></td>
													</tr>
													<tr>
														<td style="padding-bottom: 5px;">Size: <%=itemDetails[counter].getItem().getSize()%></td>
													</tr>
													<tr>
														<td style="padding-bottom: 5px;">In Stock: <%=itemDetails[counter].getQantity()%></td>
													</tr>
													<tr>
														<td style="padding-bottom: 5px;">Price: SCT <%=Utils.formatAmount(itemDetails[counter].getPrice()/100.0)%></td>
													</tr>
													<tr>
														<td style="padding-bottom: 5px;">
															<form method="post" action="item_buy.jsp">
															<input type="hidden" name="itemId" value="<%=itemDetails[counter].getItemId()%>">
															<input type="hidden" name="sellerId" value="<%=parameters.getValueNotNull("sellerId")%>">
															<input type="submit" value="PREVIEW" class="btn btn-primary btn-sm" style="width:125px">
															</form>												
														</td>
													</tr>
												</table>
											</td>
										</tr>
										
									</table>
								</td>
							<%
								} else {
							%>		
								<td><table style="border-width:0px; width:520px"><tr><td>&nbsp;</td></tr></table></td>
							<%
								}
								counter++;
							}
							%>
							</tr>
							<%
							}
							%>
						</table>
					</td>
				</tr>
			</table>
		
	</body>	
</html>