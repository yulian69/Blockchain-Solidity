<%@page import="org.bouncycastle.util.encoders.Hex"%>
<%@page import="solidity.factory.ParametersFactory"%>
<%@page import="solidity.model.Parameters"%>
<%@page import="solidity.model.ItemDetail"%>
<%@page import="solidity.factory.SellerFactory"%>
<%@page import="org.web3j.crypto.Credentials"%>
<%@page import="solidity.model.Application"%>
<%@page import="solidity.contracts.SkinCareToken"%>
<%@page import="solidity.model.Account"%>
<%
	session.setAttribute("page", "item_update.jsp");

	Account account = (Account)session.getAttribute("account");
	if ( account == null ) {
		response.sendRedirect("./login.jsp");
		return;
	}	
	
	boolean isSeller = SellerFactory.isSeller(account);
	if ( !isSeller ) {
		response.sendRedirect("./seller_add.jsp");
		return;
	}
	
	Parameters parameters = ParametersFactory.getInstanse(request);
	
	ItemDetail itemDetail = SellerFactory.getItemDetail(Hex.decode(parameters.getValueNotNull("itemId")));
	
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
						<h3 class="panel-title" style="font-weight:bold">UPDATE ITEM</h3>
					</div>
					<div class="panel-body">	
					
					<div style="height:30px;color:#ff0000;width: 100%;text-align:center" id="error"></div>
					<table style="margin: 0 auto;font-family:Verdana">
						<tbody>
							<tr>
								<td align="right"><label class="label" style="font-size:12px;color:#555555">Name:</label></td><td><input type="text" value="<%=itemDetail.getItem().getName() %>" class="form-control" id="name" name="name" style="width:400px;height:30px;font-size:12px"></td>
							</tr>
							<tr>
								<td align="right"><label class="label" style="font-size:12px;color:#555555">Description:</label></td><td><textarea id="description" name="description" class="form-control" style="width:400px;height:150px;font-size:12px"><%=itemDetail.getItem().getDescription() %></textarea></td>
							</tr>
							<tr>
								<td align="right"><label class="label" style="font-size:12px;color:#555555">Size:</label></td><td><input type="text" value="<%=itemDetail.getItem().getSize() %>" id="size" name="size" class="form-control" style="width:400px;height:30px;font-size:12px"></td>
							</tr>
							<tr>
								<td align="right"><label class="label" style="font-size:12px;color:#555555">Price:</label></td><td><input type="text" value="<%=itemDetail.getPrice() %>" id="price" name="price" class="form-control" style="width:400px;height:30px;font-size:12px"></td>
							</tr>
							<tr>
								<td align="right"><label class="label" style="font-size:12px;color:#555555">Quantity:</label></td><td><input type="text" value="<%=itemDetail.getQantity() %>" id="quantity" name="quantity" class="form-control" style="width:400px;height:30px;font-size:12px"></td>
							</tr>
							<tr>
								<td align="right"><label class="label" style="font-size:12px;color:#555555">Image:</label></td><td><input type="file" class="form-control" style="width:400px;height:30px;font-size:12px" name="image"></td>
							</tr>	
						</tbody>
					</table>
					<div style="height:40px"></div>
					<div style="width: 100%;text-align:center"><input type="button" value="UPDATE ITEM" id="action-button" class="btn btn-primary" style="width:180px"> <a href="items.jsp" class="btn btn-primary" style="width:180px">CANCEL</a></div>
					<div style="height:10px"></div>
					</div>
				</div>
				</td>
			</tr>			
		</table>		
		</div>
		<input type="hidden" name="action" value="item_update">
		<input type="hidden" name="itemId" value="<%=parameters.getValueNotNull("itemId")%>">
		</form>
	</body>
	<script src="./js/utils.js"></script>
	<script type="text/javascript">
		$("#action-button").click(function() {
			var returnValues = ["ok"];
			var redirectUrls = ["items.jsp"];
			postMultipart("SellerActions",returnValues,redirectUrls);
		});
	</script>
</html>