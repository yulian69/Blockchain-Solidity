package solidity.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.util.encoders.Hex;

import com.google.gson.Gson;

import solidity.factory.BuyerFactory;
import solidity.factory.IpfsFactory;
import solidity.factory.ParametersFactory;
import solidity.factory.SellerFactory;
import solidity.model.Account;
import solidity.model.Buyer;
import solidity.model.ItemDetail;
import solidity.model.Parameters;
import solidity.util.Utils;

public class BuyerActions extends HttpServlet {			
	private static final long serialVersionUID = -6027193029045277471L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			Account account = (Account)request.getSession().getAttribute("account");
			if ( account == null ) {
				Utils.printText(response, "invalid_session");
				return;
			}
			
			Parameters parameters = ParametersFactory.getInstanse(request);
			String action = parameters.getValueNotNull("action");
			
			switch (action) {			
				case "buyer_add":
					addBuyer(parameters, request, response, account);
					return;
				case "item_buy":					
					buy(parameters, request, response, account);
					return;
				case "buyer_update":					
					updateBuyer(parameters, response, account);
					return;
			}
			
			Utils.printTextError(response, "Invalid Data.");
		} catch (Exception e) {
			try {
				response.sendRedirect("error.jsp");
			} catch (IOException e1) {}
		}
	}
		
	private void buy(Parameters parameters, HttpServletRequest request, HttpServletResponse response, Account account) throws IOException {
		try {	
			byte[] buyerId = BuyerFactory.getBuyerId(account);
			Buyer buyer = (Buyer)request.getSession().getAttribute("buyer");
			if ( buyerId.length == 0 && buyer == null ) {
				Utils.printText(response, "not_registered");
				return;
			}
			
			String itemId = parameters.getValueNotNull("itemId");
			
			int qty = 0;
			try {
				qty = parameters.getValueAsInt("qty");
				if ( qty <= 0 ) {
					throw new Exception("Invalid data.");
				}
			} catch (Exception e) {
				throw new Exception("Invalid data.");
			}
			
			ItemDetail itemDetail = SellerFactory.getItemDetail(Hex.decode(itemId));
			if ( qty > itemDetail.getQantity() ) {
				throw new Exception("Insufficient ite qantity.");
			}
			
			int tokens = qty*itemDetail.getPrice();
			
			if ( buyerId.length == 32 ) {
				BuyerFactory.buy(itemId, tokens, account);
			} else {			
				Gson gson = new Gson();
				String json = gson.toJson(buyer);				
				String ipfs = IpfsFactory.addJson(json);				
				BuyerFactory.registerAndBuy(itemId, ipfs, tokens, account);				
				request.getSession().removeAttribute("buyer");
			}
			
			Utils.printText(response, "ok");
		} catch (Exception e) {
			Utils.printText(response, e.getMessage());
		}
	}
		
	
	private void addBuyer(Parameters parameters, HttpServletRequest request, HttpServletResponse response, Account account) throws IOException {
		try {				
			String name = parameters.getValueNotNull("name");
			String address = parameters.getValueNotNull("address");
			String city = parameters.getValueNotNull("city");
			String zip = parameters.getValueNotNull("zip");
			String country = parameters.getValueNotNull("country");
			String state = parameters.getValueNotNull("state");
			String email = parameters.getValueNotNull("email");
			String phone = parameters.getValueNotNull("phone");
			
			if ( name.length() == 0 ) {
				throw new Exception("Please enter your name.");
			}
			if ( address.length() == 0 ) {
				throw new Exception("Please enter address.");
			}	
			if ( city.length() == 0 ) {
				throw new Exception("Please enter city.");
			}
			if ( zip.length() == 0 ) {
				throw new Exception("Please enter zip.");
			}
			if ( country.length() == 0 ) {
				throw new Exception("Please enter country.");
			}
			if ( email.length() == 0 ) {
				throw new Exception("Please enter email.");
			}
			if ( phone.length() == 0 ) {
				throw new Exception("Please enter phone.");
			}	
			
			if ( !Utils.validateEmail(email)) {
				throw new Exception("Please enter valid email.");
			}
			
			Buyer buyer = new Buyer();
			
			buyer.setName(name);
			buyer.setAddress(address);
			buyer.setCity(city);
			buyer.setZip(zip);
			buyer.setCountry(country);
			buyer.setState(state);
			buyer.setEmail(email);
			buyer.setPhone(phone);
			
			request.getSession().setAttribute("buyer", buyer);
			
			Utils.printText(response, "ok");
		} catch (Exception e) {
			Utils.printText(response, e.getMessage());
		}
	}
	
	private void updateBuyer(Parameters parameters, HttpServletResponse response, Account account) throws IOException {
		try {				
			String name = parameters.getValueNotNull("name");
			String address = parameters.getValueNotNull("address");
			String city = parameters.getValueNotNull("city");
			String zip = parameters.getValueNotNull("zip");
			String country = parameters.getValueNotNull("country");
			String state = parameters.getValueNotNull("state");
			String email = parameters.getValueNotNull("email");
			String phone = parameters.getValueNotNull("phone");
			
			if ( name.length() == 0 ) {
				throw new Exception("Please enter your name.");
			}
			if ( address.length() == 0 ) {
				throw new Exception("Please enter address.");
			}	
			if ( city.length() == 0 ) {
				throw new Exception("Please enter city.");
			}
			if ( zip.length() == 0 ) {
				throw new Exception("Please enter zip.");
			}
			if ( country.length() == 0 ) {
				throw new Exception("Please enter country.");
			}
			if ( email.length() == 0 ) {
				throw new Exception("Please enter email.");
			}
			if ( phone.length() == 0 ) {
				throw new Exception("Please enter phone.");
			}	
			
			if ( !Utils.validateEmail(email)) {
				throw new Exception("Please enter valid email.");
			}
			
			Buyer buyer = new Buyer();
			
			buyer.setName(name);
			buyer.setAddress(address);
			buyer.setCity(city);
			buyer.setZip(zip);
			buyer.setCountry(country);
			buyer.setState(state);
			buyer.setEmail(email);
			buyer.setPhone(phone);
			
			Gson gson = new Gson();
			String json = gson.toJson(buyer);
			
			String ipfs = IpfsFactory.addJson(json);
			
			BuyerFactory.updateBuyer(ipfs, account.getPrivateKey());
			
			Utils.printText(response, "ok");
		} catch (Exception e) {
			Utils.printText(response, e.getMessage());
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}	
}
