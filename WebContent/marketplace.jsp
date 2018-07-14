<%@page import="java.math.BigInteger"%>
<%@page import="solidity.contracts.SkinCareMarketplace"%>
<%@page import="org.web3j.crypto.Credentials"%>
<%@page import="solidity.model.Application"%>
<%@page import="solidity.contracts.SkinCareToken"%>
<%@page import="solidity.model.Account"%>
<%
	String disabled = "disabled";
	Account account = (Account)session.getAttribute("account");
	
	SkinCareMarketplace contract = SkinCareMarketplace.load(Application.getContractMarketplaceAddress(), Application.getWeb3j(), Credentials.create("0x0"), Application.getGasLimit(), BigInteger.valueOf(4600000));
	if ( account != null ) {
		String owner = "";
		try {				
			owner = contract.owner().send();		
		} catch (Exception e) {}
		if ( owner.equals(account.getAddress()) ) {
			disabled = "";
		}
	}	
	session.setAttribute("page", "marketplace.jsp");
	
			
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
			<table style="border-width:0px; margin-left: auto; margin-right: auto;font-family:Verdana">
			<tr>
				<td>
					<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title" style="font-weight:bold">MARKETPLACE ADMINISTRATION <span style="font-weight:bold; font-size:12px"><%=contract.getContractAddress() %></span></h3>
					</div>
					<div class="panel-body">	
					
					<table>
						<tbody>		
							<tr>
								<td colspan="2" style="padding: 5px"><input type="text" class="form-control" id="txtToken" style="width:100%" value="" readonly></td>								
								<td style="padding: 5px"><input type="button" id="btnTokenAddress" value="TOKEN ADDRESS" class="btn btn-primary btn-sm" style="width:200px"></td>								
							</tr>
							<tr>
								<td style="padding: 5px" colspan="2"><input type="text" class="form-control" name="txtTokenAddressNew" style="width:100%" value="" placeholder="token_address"></td>								
								<td style="padding: 5px"><input type="button" id="btnTransferTokenAddress" value="TRANSFER TOKEN ADDRESS" class="btn btn-primary btn-sm" style="width:200px" <%=disabled %>></td>								
							</tr>
							<tr>
								<td style="padding: 5px"><input type="text" class="form-control" name="txtAddressTo" style="width:500px" value="" placeholder="address_to"></td>								
								<td style="padding: 5px"><input type="text" class="form-control" name="txtTransTokens" style="width:110px;text-align:right" value="" placeholder="tokens"></td>	
								<td style="padding: 5px"><input type="button" id="btnTransferTokens" value="TRANSFER TOKENS" class="btn btn-primary btn-sm" style="width:200px" <%=disabled %>></td>								
							</tr>
							<tr>
								<td style="padding: 5px" colspan="2"><input type="text" class="form-control" id="txtOwner" style="width:100%" value="" readonly></td>	
								<td style="padding: 5px"><input type="button" id="btnOwner" value="MARKETPLACE OWNER" class="btn btn-primary btn-sm" style="width:200px"></td>								
							</tr>
							<tr>
								<td style="padding: 5px" colspan="2"><input type="text" class="form-control" name="txtAddressNewOwner" style="width:100%" value="" placeholder="address_to"></td>								
								<td style="padding: 5px"><input type="button" id="btnTransferOwner" value="TRANSFER OWNER" class="btn btn-primary btn-sm" style="width:200px" <%=disabled %>></td>								
							</tr>
						</tbody>
					</table>
					
					</div>
				</div>
				</td>
			</tr>
			
		</table>
		</div>
		</form>
	</body>
	<script src="./js/utils.js"></script>
	<script type="text/javascript">
	$("#btnTokenAddress").click(function() {
		showOverlay();
		$.post("MarketplaceActions?action=token",$("#form").serialize())
			.done(function(data) {
				$("#overlay").remove();
				$("#txtToken").val(data);
			})
			.fail(function(xhr) {
				var res = parseError(xhr.responseText).trim();
				alert("Error: " + res);
				$("#overlay").remove();
			});
	});

	$("#btnBalance").click(function() {
		showOverlay();
		$.post("TokenActions?action=balance",$("#form").serialize())
			.done(function(data) {
				$("#overlay").remove();
				$("#txtTokens").val(data);
			})
			.fail(function(xhr) {
				var res = parseError(xhr.responseText).trim();
				alert("Error: " + res);
				$("#overlay").remove();
			});
	});

	$("#btnOwner").click(function() {
		showOverlay();
		$.post("MarketplaceActions?action=owner",$("#form").serialize())
			.done(function(data) {
				$("#overlay").remove();
				$("#txtOwner").val(data);
			})
			.fail(function(xhr) {
				var res = parseError(xhr.responseText).trim();
				alert("Error: " + res);
				$("#overlay").remove();
			});		
	});
	
	$("#btnTransferTokenAddress").click(function() {
		showOverlay();
		$.post("MarketplaceActions?action=transferTokenAddress",$("#form").serialize())
			.done(function(data) {
				$("#overlay").remove();
				alert("Tokens address transferred!");				
			})
			.fail(function(xhr) {
				var res = parseError(xhr.responseText).trim();
				alert("Error: " + res);
				$("#overlay").remove();
			});	
	});

	$("#btnTransferTokens").click(function() {
		showOverlay();
		$.post("MarketplaceActions?action=transfer",$("#form").serialize())
			.done(function(data) {
				$("#overlay").remove();
				alert("Tokens transferred!")
			})
			.fail(function(xhr) {
				var res = parseError(xhr.responseText).trim();
				alert("Error: " + res);
				$("#overlay").remove();
			});	
	});
	
	$("#btnTransferOwner").click(function() {
		showOverlay();
		$.post("MarketplaceActions?action=transferOwnership",$("#form").serialize())
			.done(function(data) {
				$("#overlay").remove();
				alert("Ownership transferred!");
				$("#btnTransferOwner").attr('disabled','disabled');
				$("#btnTransferTokenAddress").attr('disabled','disabled');
				$("#btnTransferTokens").attr('disabled','disabled');
			})
			.fail(function(xhr) {
				var res = parseError(xhr.responseText).trim();
				alert("Error: " + res);
				$("#overlay").remove();
			});	
	});
	</script>
</html>