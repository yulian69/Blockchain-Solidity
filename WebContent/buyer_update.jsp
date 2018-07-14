<%@page import="solidity.model.Buyer"%>
<%@page import="solidity.factory.BuyerFactory"%>
<%@page import="solidity.model.Seller"%>
<%@page import="solidity.factory.SellerFactory"%>
<%@page import="org.web3j.crypto.Credentials"%>
<%@page import="solidity.model.Application"%>
<%@page import="solidity.contracts.SkinCareToken"%>
<%@page import="solidity.model.Account"%>
<%
	session.setAttribute("page", "buyer_update.jsp");

	Account account = (Account)session.getAttribute("account");
	if ( account == null ) {
		response.sendRedirect("./login.jsp");
		return;
	}	
	
	boolean isBuyer = BuyerFactory.isBuyer(account);
	if ( !isBuyer ) {
		response.sendRedirect("./home.jsp");
		return;
	}
	
	Buyer buyer = BuyerFactory.getBuyer(account);
%>
<!doctype html>
<html lang="en">
	<head>
		<jsp:include page="header.jsp"/>		
	</head>
	<body style="margin-top:0px">
		<jsp:include page="menu.jsp"/>		
		<br><br>
		<form id="form">
		<div class="" style="width:1800px; margin-left: auto; margin-right: auto; font-size:12px;font-family:Verdana">			
			<table style="border-width:0px; margin-left: auto; margin-right: auto;font-family:Verdana;width:800px">
			<tr>
				<td>
					<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title" style="font-weight:bold">UPDATE BUYER</h3>
					</div>
					<div class="panel-body">	
					
					<div style="height:30px;color:#ff0000;width: 100%;text-align:center" id="error"></div>
					<table style="margin: 0 auto;font-family:Verdana">
						<tbody>
							<tr>
								<td align="right"><label class="label" style="font-size:12px;color:#555555">Name:</label></td><td><input type="text" value="<%=buyer.getName() %>" class="form-control" id="name" name="name" style="width:400px;height:30px;font-size:12px"></td>
							</tr>
							<tr>
								<td align="right"><label class="label" style="font-size:12px;color:#555555">Address:</label></td><td><input type="text" value="<%=buyer.getAddress() %>" id="address" name="address" class="form-control" style="width:400px;height:30px;font-size:12px"></td>
							</tr>
							<tr>
								<td align="right"><label class="label" style="font-size:12px;color:#555555">City:</label></td><td><input type="text" value="<%=buyer.getCity() %>" id="city" name="city" class="form-control" style="width:400px;height:30px;font-size:12px"></td>
							</tr>
							<tr>
								<td align="right"><label class="label" style="font-size:12px;color:#555555">Zip / Postal Code:</label></td><td><input type="text" value="<%=buyer.getZip() %>" id="zip" name="zip" class="form-control" style="width:400px;height:30px;font-size:12px"></td>
							</tr>
							<tr>
								<td align="right"><label class="label" style="font-size:12px;color:#555555">Country:</label></td><td><input type="text" value="<%=buyer.getCountry() %>" id="country" name="country" class="form-control" style="width:400px;height:30px;font-size:12px"></td>
							</tr>
							<tr>
								<td align="right"><label class="label" style="font-size:12px;color:#555555">State:</label></td><td><input type="text" value="<%=buyer.getState() %>" id="state" name="state" class="form-control" style="width:400px;height:30px;font-size:12px"></td>
							</tr>
							<tr>
								<td align="right"><label class="label" style="font-size:12px;color:#555555">Email:</label></td><td><input type="text" value="<%=buyer.getEmail() %>" id="email" name="email" class="form-control" style="width:400px;height:30px;font-size:12px"></td>
							</tr>
							<tr>
								<td align="right"><label class="label" style="font-size:12px;color:#555555">Phone:</label></td><td><input type="text" value="<%=buyer.getPhone() %>" id="phone" name="phone" class="form-control" style="width:400px;height:30px;font-size:12px"></td>
							</tr>					
						</tbody>
					</table>
					<div style="height:40px"></div>
					<div style="width: 100%;text-align:center"><input type="button" value="UPDATE BUYER" id="action-button" class="btn btn-primary" style="width:180px"> <a href="home.jsp" class="btn btn-primary" style="width:180px">CANCEL</a></div>
					<div style="height:10px"></div>
					</div>
				</div>
				</td>
			</tr>			
		</table>		
		</div>
		<input type="hidden" name="action" value="buyer_update">
		</form>
	</body>
	<script src="./js/utils.js"></script>
	<script type="text/javascript">
		$("#action-button").click(function() {
			var returnValues = ["ok"];
			var redirectUrls = ["home.jsp"];
			post("BuyerActions",returnValues,redirectUrls);
		});
	</script>
</html>