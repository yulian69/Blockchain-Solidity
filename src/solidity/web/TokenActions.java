package solidity.web;

import java.io.IOException;
import java.math.BigInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import solidity.contracts.SkinCareToken;
import solidity.factory.ParametersFactory;
import solidity.model.Account;
import solidity.model.Application;
import solidity.model.Parameters;
import solidity.util.Utils;

public class TokenActions extends HttpServlet {			
	private static final long serialVersionUID = -2814008615114754854L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
						
			if ( request.getContentType() != null && request.getContentType().indexOf("multipart/form-data") >= 0 ) {
				//MultipartParameters parameters = ParametersFactory.getInstanseMultiPart(request);
						
				
				//String action = parameters.getValueNotNull("action");
								
				return;
			}
			
			Account account = (Account)request.getSession().getAttribute("account");
			String privateKey = (account != null) ? account.getPrivateKey() : "0x0";
			
			Parameters parameters = ParametersFactory.getInstanse(request);
			String action = parameters.getValueNotNull("action");
			
			switch (action) {			
				case "totalSupply":
					getTotalSupply(response, privateKey);
					return;
				case "balance":
					getBalance(parameters, response, privateKey);
					return;		
				case "owner":
					getOwner(response, privateKey);
					return;
				case "transfer":
					transfer(parameters, response, privateKey, account.getAddress());
					return;
				case "transferOwnership":
					transferOwnership(parameters, response, privateKey);
					return;
			}
			
			Utils.printTextError(response, "Invalid Data.");
		} catch (Exception e) {
			try {
				response.sendRedirect("error.jsp");
			} catch (IOException e1) {}
		}
	}
	
	private void transfer(Parameters parameters, HttpServletResponse response, String privateKey, String address) throws IOException {
		try {
			String addressTo = parameters.getValueNotNull("txtAddressTo");
			if ( !WalletUtils.isValidAddress(addressTo)) {
				throw new Exception("Invalid address.");
			}
			long tokens;
			try {
				tokens = Long.parseLong(parameters.getValue("txtTransTokens"));
			} catch (Exception e) {
				throw new Exception("Invalid token value.");
			}
			
			SkinCareToken contract = SkinCareToken.load(Application.getContractTokenAddress(), Application.getWeb3j(), Credentials.create(privateKey), Application.getGasLimit(), BigInteger.valueOf(460000));
			BigInteger balance = contract.balanceOf(address).send();
			
			BigInteger biTokens = BigInteger.valueOf(tokens);
			
			if ( balance.compareTo(biTokens) < 0 ) {
				throw new Exception("Insufficient tokens.");
			}
					
			contract = SkinCareToken.load(Application.getContractTokenAddress(), Application.getWeb3j(), Credentials.create(privateKey), Application.getGasLimit(), BigInteger.valueOf(4600000));
			contract.transfer(addressTo, biTokens).send();
			
			Utils.printText(response, "ok");
		} catch (Exception e) {
			Utils.printTextError(response, e.getMessage());
		}
	}
	
	private void transferOwnership(Parameters parameters, HttpServletResponse response, String privateKey) throws IOException {
		try {
			String address = parameters.getValueNotNull("txtAddressNewOwner");
			if ( !WalletUtils.isValidAddress(address)) {
				throw new Exception("Invalid address.");
			}
								
			SkinCareToken contract = SkinCareToken.load(Application.getContractTokenAddress(), Application.getWeb3j(), Credentials.create(privateKey), Application.getGasLimit(), BigInteger.valueOf(4600000));
			contract.transferOwnership(address).send();
			
			Utils.printText(response, "ok");
		} catch (Exception e) {
			Utils.printTextError(response, e.getMessage());
		}
	}
	
	private void getTotalSupply(HttpServletResponse response, String privateKey) throws IOException {
		try {
			SkinCareToken contract = SkinCareToken.load(Application.getContractTokenAddress(), Application.getWeb3j(), Credentials.create(privateKey), Application.getGasLimit(), BigInteger.valueOf(4600000));
			BigInteger totalSupply  = contract.totalSupply().send();
			Utils.printText(response, totalSupply.toString(10));			
		} catch (Exception e) {
			Utils.printTextError(response, e.getMessage());
		}
	}
	
	private void getBalance(Parameters parameters, HttpServletResponse response, String privateKey) throws IOException {
		try {
			String address = parameters.getValueNotNull("txtTokenOwner");
			if ( !WalletUtils.isValidAddress(address)) {
				throw new Exception("Invalid address.");
			}
			
			SkinCareToken contract = SkinCareToken.load(Application.getContractTokenAddress(), Application.getWeb3j(), Credentials.create(privateKey), Application.getGasLimit(), BigInteger.valueOf(460000));
			BigInteger balance = contract.balanceOf(address).send();
			
			Utils.printText(response, balance.toString(10));
		} catch (Exception e) {
			Utils.printTextError(response, e.getMessage());
		}
	}
	
	private void getOwner(HttpServletResponse response, String privateKey) throws IOException {
		try {
			SkinCareToken contract = SkinCareToken.load(Application.getContractTokenAddress(), Application.getWeb3j(), Credentials.create(privateKey), Application.getGasLimit(), BigInteger.valueOf(4600000));
			String owner = contract.owner().send();
			Utils.printText(response, owner);			
		} catch (Exception e) {
			Utils.printTextError(response, e.getMessage());
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}		
}
