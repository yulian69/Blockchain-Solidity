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
		
<!-- This is a very simple parallax effect achieved by simple CSS 3 multiple backgrounds, made by http://twitter.com/msurguy -->

<div class="container">
    <div class="row vertical-offset-100">
    	<div class="col-md-7 col-md-offset-2">
    		<div class="panel panel-default">
			  	<div class="panel-heading">
			    	<h3 class="panel-title">Please sign in</h3>
			 	</div>
			  	<div class="panel-body">
			    	<form id="form">
                    <fieldset>
			    	  	<div class="form-group">
			    			<input class="form-control" placeholder="Private Key" name="privateKey" type="password" value="" >
			    		</div>
			    		
			    		<input class="btn btn-lg btn-success btn-block" type="button" value="Login" id="login">
			    	</fieldset>
			      	</form>
			    </div>
			</div>
		</div>
	</div>
</div>
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