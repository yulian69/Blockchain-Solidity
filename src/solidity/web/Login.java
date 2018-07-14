package solidity.web;

import java.io.IOException;
import java.math.BigInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;

import solidity.factory.ParametersFactory;
import solidity.model.Account;
import solidity.model.Parameters;
import solidity.util.Utils;


public class Login extends HttpServlet {			
	private static final long serialVersionUID = -2814008615114754854L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			
			Parameters parameters = ParametersFactory.getInstanse(request);
			String privateKey = parameters.getValueNotNull("privateKey");
			
			if ( !WalletUtils.isValidPrivateKey(privateKey) ) {
				Utils.printTextError(response, "Invalid Private Key.");
				return;
			}
			
			if ( privateKey.startsWith("0x") ) {
				privateKey = privateKey.substring(2);
			}
			
			BigInteger privateKeyInBT = new BigInteger(privateKey, 16);
	        ECKeyPair aPair = ECKeyPair.create(privateKeyInBT);
	        BigInteger publicKeyInBT = aPair.getPublicKey();

	        String sPublickeyInHex = publicKeyInBT.toString(16);
			String address = Keys.getAddress(sPublickeyInHex);
			
			Account account = new Account();
			account.setAddress("0x"+address);
			account.setPrivateKey("0x"+privateKey);
			
			request.getSession().setAttribute("account", account);
			
			String page = (String)request.getSession().getAttribute("page");
			if ( page == null ) {
				page = "home.jsp";
			}
			Utils.printText(response, page);
		} catch (Exception e) {
			Utils.printTextError(response, "Error: " + e.getMessage());
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}		
}
