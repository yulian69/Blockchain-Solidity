<html lang="en">
	<head>
		<jsp:include page="header.jsp"/>
	</head>
	<style>	
		@media (min-width: 800px) {
		  .container {
		    max-width: 800px;
		  }
		}			
		.jumbotron {
		  text-align: center;
		  border-bottom: 1px solid #e5e5e5;
		  background-color: #eeeeee;
		  font-family:Verdana;
		}
	</style>
	<body>	
		<jsp:include page="menu.jsp"/>		
		<br><br>
		<div style="height:10%"></div>
		<form id="form">
			<div class="container">
				<div class="jumbotron">
				<div><label class="label" style="font-size:18px;color:#555555;">ACCOUNT LOGIN</label></div>
					<div style="height:10px;color:#ff0000"></div>
					<div style="height:30px;color:#ff0000" id="error"></div>
					<table style="margin: 0 auto">
						<tbody>
							<tr>
								<td><label class="label" style="font-size:12px;color:#555555">Private Key:</label></td><td><input type="password" autocomplete="new-password"class="form-control" id="privateKey" name="privateKey" style="width:470px;font-size:12px;" placeholder="Private Key"></td>
							</tr>
						</tbody>
					</table>
					<div style="height:40px"></div>
					<input type="button" value="LOGIN" id="login" class="btn btn-primary" style="width:150px">
				</div>
			</div>			
		</form>		
	</body>
	<script src="./js/utils.js"></script>
	<script>
	$("#login").click(function() {
		showOverlay();
		$.post("Login",$("#form").serialize())
			.done(function(data) {
				location.href = data;
			})
			.fail(function(xhr) {
				var res = parseError(xhr.responseText).trim();
				alert("Error: " + res);
				$("#overlay").remove();
			});		
	});
	</script>
</html>