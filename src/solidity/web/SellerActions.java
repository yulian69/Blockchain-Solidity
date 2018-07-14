package solidity.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.util.encoders.Hex;

import com.google.gson.Gson;

import solidity.factory.IpfsFactory;
import solidity.factory.ParametersFactory;
import solidity.factory.SellerFactory;
import solidity.model.Account;
import solidity.model.Application;
import solidity.model.Item;
import solidity.model.MultipartParameters;
import solidity.model.Parameters;
import solidity.model.Seller;
import solidity.util.Utils;

public class SellerActions extends HttpServlet {			
	private static final long serialVersionUID = -2814008615114754854L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			Account account = (Account)request.getSession().getAttribute("account");
			if ( account == null ) {
				Utils.printText(response, "invalid_session");
				return;
			}
			
			if ( request.getContentType() != null && request.getContentType().indexOf("multipart/form-data") >= 0 ) {
				MultipartParameters parameters = ParametersFactory.getInstanseMultiPart(request);
				String action = parameters.getValueNotNull("action");
							
				switch (action) {			
					case "item_add":
						itemAdd(parameters, response, account);
						return;
					case "item_update":
						itemUpdate(parameters, response, account);
						return;
				}
				Utils.printTextError(response, "Invalid Data.");
				return;
			}
			
			Parameters parameters = ParametersFactory.getInstanse(request);
			String action = parameters.getValueNotNull("action");
			
			switch (action) {			
				case "seller_add":
					sellerAdd(parameters, response, account);
					return;
				case "seller_update":
					sellerUpdate(parameters, response, account);
					return;
			}
			
			Utils.printTextError(response, "Invalid Data.");
		} catch (Exception e) {
			try {
				response.sendRedirect("error.jsp");
			} catch (IOException e1) {}
		}
	}
	
	private void itemAdd(MultipartParameters parameters, HttpServletResponse response, Account account) throws IOException {
		try {				
			String name = parameters.getValueNotNull("name");
			String description = parameters.getValueNotNull("description");
			String size = parameters.getValueNotNull("size");
			int price = parameters.getValueAsInt("price");
			int quantity = parameters.getValueAsInt("quantity");
			String filename = parameters.getFileName("image");
			
			if ( name.length() == 0 ) {
				throw new Exception("Please enter item name.");
			}
			if ( description.length() == 0 ) {
				throw new Exception("Please enter item description.");
			}
			if ( size.length() == 0 ) {
				throw new Exception("Please enter item size.");
			}
			if ( price == 0 ) {
				throw new Exception("Please enter item price.");
			}	
			if ( quantity == 0 ) {
				throw new Exception("Please enter item quantity.");
			}
			if ( filename == null || filename.length() == 0 ) {
				throw new Exception("Please enter item image.");
			}
			
			String ext = "";
			int pos = filename.lastIndexOf('.');
			if (pos > 0) {
				ext = filename.substring(pos+1).toLowerCase();
			}
			
			if ( !ext.equals("jpg") && !ext.equals("jpeg") && !ext.equals("png") ) {
				throw new Exception("Please upload an image file.");
			}
			
			//System.out.println(Application.getUploadDir() + parameters.getStorageFileName("image"));
			
			String imageIpfs = IpfsFactory.addFile(Application.getUploadDir() + parameters.getStorageFileName("image"));
			
			//System.out.println(imageIpfs);
			
			Item item = new Item();			
			item.setName(name);
			item.setDescription(description);
			item.setSize(size);
			item.setIpfs(imageIpfs);
			
			Gson gson = new Gson();
			String json = gson.toJson(item);
			
			String ipfs = IpfsFactory.addJson(json);
			
			SellerFactory.addItem(ipfs, price, quantity, account.getPrivateKey());
			
			Utils.printText(response, "ok");
		} catch (Exception e) {
			Utils.printText(response, e.getMessage());
		}
	}
	
	private void itemUpdate(MultipartParameters parameters, HttpServletResponse response, Account account) throws IOException {
		try {				
			String name = parameters.getValueNotNull("name");
			String description = parameters.getValueNotNull("description");
			String size = parameters.getValueNotNull("size");
			int price = parameters.getValueAsInt("price");
			int quantity = parameters.getValueAsInt("quantity");
			String filename = parameters.getFileName("image");
			String itemId = parameters.getValueNotNull("itemId");
			
			if ( itemId.length() == 0 ) {
				throw new Exception("Invalid Data.");
			}
			
			if ( name.length() == 0 ) {
				throw new Exception("Please enter item name.");
			}
			if ( description.length() == 0 ) {
				throw new Exception("Please enter item description.");
			}
			if ( size.length() == 0 ) {
				throw new Exception("Please enter item size.");
			}
			if ( price == 0 ) {
				throw new Exception("Please enter item price.");
			}	
			if ( quantity == 0 ) {
				throw new Exception("Please enter item quantity.");
			}
			if ( filename == null || filename.length() == 0 ) {
				throw new Exception("Please enter item image.");
			}
			
			String ext = "";
			int pos = filename.lastIndexOf('.');
			if (pos > 0) {
				ext = filename.substring(pos+1).toLowerCase();
			}
			
			if ( !ext.equals("jpg") && !ext.equals("jpeg") && !ext.equals("png") ) {
				throw new Exception("Please upload an image file.");
			}
			
			//System.out.println(Application.getUploadDir() + parameters.getStorageFileName("image"));
			
			String imageIpfs = IpfsFactory.addFile(Application.getUploadDir() + parameters.getStorageFileName("image"));
			
			//System.out.println(imageIpfs);
			
			Item item = new Item();			
			item.setName(name);
			item.setDescription(description);
			item.setSize(size);
			item.setIpfs(imageIpfs);
			
			Gson gson = new Gson();
			String json = gson.toJson(item);
			
			String ipfs = IpfsFactory.addJson(json);
			
			SellerFactory.updateItem(Hex.decode(itemId), ipfs, price, quantity, account.getPrivateKey());
			
			Utils.printText(response, "ok");
		} catch (Exception e) {
			Utils.printText(response, e.getMessage());
		}
	}
	
	private void sellerAdd(Parameters parameters, HttpServletResponse response, Account account) throws IOException {
		try {				
			String tradeName = parameters.getValueNotNull("trade_name");
			String company = parameters.getValueNotNull("company");
			String address = parameters.getValueNotNull("address");
			String city = parameters.getValueNotNull("city");
			String zip = parameters.getValueNotNull("zip");
			String country = parameters.getValueNotNull("country");
			String state = parameters.getValueNotNull("state");
			String email = parameters.getValueNotNull("email");
			String phone = parameters.getValueNotNull("phone");
			
			if ( tradeName.length() == 0 ) {
				throw new Exception("Please enter trade name.");
			}
			if ( company.length() == 0 ) {
				throw new Exception("Please enter company.");
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
			
			Seller seller = new Seller();
			
			seller.setTradeName(tradeName);
			seller.setCompany(company);
			seller.setAddress(address);
			seller.setCity(city);
			seller.setZip(zip);
			seller.setCountry(country);
			seller.setState(state);
			seller.setEmail(email);
			seller.setPhone(phone);
			
			Gson gson = new Gson();
			String json = gson.toJson(seller);
			
			String ipfs = IpfsFactory.addJson(json);
			
			SellerFactory.addSeller(ipfs, account);
			
			Utils.printText(response, "ok");
		} catch (Exception e) {
			Utils.printText(response, e.getMessage());
		}
	}
	
	private void sellerUpdate(Parameters parameters, HttpServletResponse response, Account account) throws IOException {
		try {				
			String tradeName = parameters.getValueNotNull("trade_name");
			String company = parameters.getValueNotNull("company");
			String address = parameters.getValueNotNull("address");
			String city = parameters.getValueNotNull("city");
			String zip = parameters.getValueNotNull("zip");
			String country = parameters.getValueNotNull("country");
			String state = parameters.getValueNotNull("state");
			String email = parameters.getValueNotNull("email");
			String phone = parameters.getValueNotNull("phone");
			
			if ( tradeName.length() == 0 ) {
				throw new Exception("Please enter trade name.");
			}
			if ( company.length() == 0 ) {
				throw new Exception("Please enter company.");
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
			
			Seller seller = new Seller();
			
			seller.setTradeName(tradeName);
			seller.setCompany(company);
			seller.setAddress(address);
			seller.setCity(city);
			seller.setZip(zip);
			seller.setCountry(country);
			seller.setState(state);
			seller.setEmail(email);
			seller.setPhone(phone);
			
			Gson gson = new Gson();
			String json = gson.toJson(seller);
			
			String ipfs = IpfsFactory.addJson(json);
			
			SellerFactory.updateSeller(ipfs, account.getPrivateKey());
			
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
