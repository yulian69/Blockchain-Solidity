package solidity.deploy;

import java.math.BigInteger;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import solidity.contracts.SkinCareMarketplace;
import solidity.contracts.SkinCareToken;

/**
 * @author Yulian Yordanov
 * Created: Jul 12, 2018
 */

public class Deploy {
	//private static String provider = "https://ropsten.infura.io/hjYOEvj1Io7oPrzu5t0p";
	private static String provider = "http://localhost:8545/";
	private static String privateKey = "0xa0ca691b2af337eaee2d6f347e62aadab2e79130332c61839c9f8537c14fb016";
	
	public static void main(String[] args) throws Exception {
		Web3j web3j = Web3j.build(new HttpService(provider));
		Credentials credentials = Credentials.create(privateKey);
		
		System.out.println("Deploying token contract");
		SkinCareToken skinCareToken = SkinCareToken.deploy(web3j, credentials, BigInteger.valueOf(300000000), BigInteger.valueOf(5000000)).send();
		String tokenAddress = skinCareToken.getContractAddress();
		System.out.println("Token contract deployed: " + tokenAddress);
		
		System.out.println();

		System.out.println("Deploying token contract");
		SkinCareMarketplace skinCareMarketplace = SkinCareMarketplace.deploy(web3j, credentials, BigInteger.valueOf(300000000), BigInteger.valueOf(5000000), tokenAddress).send();
		String marketplaceAddress = skinCareMarketplace.getContractAddress();
		System.out.println("Marketplace contract deployed: " + marketplaceAddress);
	}
}
