<%@page import="solidity.factory.BuyerFactory"%>
<%@page import="solidity.factory.SellerFactory"%>
<%@page import="solidity.model.Account"%>
<%
	String address = "Anonymous";
	boolean isSeller = false;
	boolean isBuyer = false;
	Account account = (Account)session.getAttribute("account");
	if ( account != null ) {
		address = account.getAddress();
		isSeller = SellerFactory.isSeller(account);
		isBuyer = BuyerFactory.isBuyer(account);
	} 
	
	String disabled = "";
	if ( !isBuyer ) {
		disabled = "disabled";
	}
%>
<nav class="navbar navbar-default">
			<div class="container-fluid">
				<div class="navbar-header">      
			  		<span class="navbar-brand" style="font-family:Verdana;font-size:11px;"><%= address %></span>
				</div>
				<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1" style="font-family:Verdana;font-size:13px;">
					<ul class="nav navbar-nav">	        
						<li class="dropdown">
							<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Token <span class="caret"></span></a>
							<ul class="dropdown-menu" style="font-family:Verdana;font-size:13px;">
								<li><a href="token.jsp">Administration</a></li>														
							</ul>
						</li>
					</ul>					
					<ul class="nav navbar-nav">	        
						<li class="dropdown">
							<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Marketplace <span class="caret"></span></a>
							<ul class="dropdown-menu" style="font-family:Verdana;font-size:13px;">
								<li><a href="marketplace.jsp">Administration</a></li>														
							</ul>
						</li>
					</ul>
					<ul class="nav navbar-nav">	        
						<li class="dropdown">
							<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Seller <span class="caret"></span></a>
							<ul class="dropdown-menu" style="font-family:Verdana;font-size:13px;">
							<%
							if ( isSeller ) {
							%>
								<li><a href="item_add.jsp">Add Item</a></li>
								<li role="separator" class="divider"></li>			
								<li><a href="items.jsp">Update Item</a></li>
								<li role="separator" class="divider"></li>
								<li><a href="seller_update.jsp">Update Account</a></li>											
							<%
							} else {
							%>	
							<li><a href="seller_add.jsp">Create Account</a></li>		
							<%
							}
							%>
							</ul>
						</li>						
					</ul>
					<ul class="nav navbar-nav">	        
						<li class="dropdown">
							<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Buyer <span class="caret"></span></a>
							<ul class="dropdown-menu" style="font-family:Verdana;font-size:13px;">
								<li class="<%=disabled %>"><a href="<%=(isBuyer ? "buyer_update.jsp" : "#") %>">Update Account</a></li>	
																			
							</ul>
						</li>						
					</ul>
					<ul class="nav navbar-nav">	        
						<li class="dropdown">
							<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Events <span class="caret"></span></a>
							<ul class="dropdown-menu" style="font-family:Verdana;font-size:13px;">
								<li><a href="token_events.jsp">Transfer Token Events</a></li>														
								<li role="separator" class="divider"></li>	
								<li><a href="buy_events.jsp">Buy Item Events</a></li>														
							</ul>
						</li>
					</ul>	
						
					<ul class="nav navbar-nav navbar-right">
						<li><a href="home.jsp"><span class="glyphicon glyphicon-home" aria-hidden="true"></span> Home</a></li>
						<%
						if ( session.getAttribute("account") != null ) {
						%>
						<li><a href="logout.jsp"><span class="glyphicon glyphicon-log-out" aria-hidden="true"></span> Logout</a></li>
						<%
						} else {
						%>
						<li><a href="login.jsp"><span class="glyphicon glyphicon-log-in" aria-hidden="true"></span> Login</a></li>
						<%
						}						
						%>
					</ul>
				</div><!-- /.navbar-collapse -->
			</div><!-- /.container-fluid -->
		</nav>	
