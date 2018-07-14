package solidity.model;

import java.math.BigInteger;

import org.web3j.protocol.Web3j;

/**
 * @author Yulian Yordanov
 * Created: Jul 11, 2018
 */
public class Application {
	private static Web3j web3j;
	private static String contractTokenAddress;
	private static String contractMarketplaceAddress;
	private static BigInteger gasLimit;
	private static String ipfsAPIUrl;
	private static String ipfsHTTPUrl;
	private static String uploadDir;
	
	public static Web3j getWeb3j() {
		return web3j;
	}
	public static void setWeb3j(Web3j web3j) {
		Application.web3j = web3j;
	}
	public static String getContractTokenAddress() {
		return contractTokenAddress;
	}
	public static void setContractTokenAddress(String contractTokenAddress) {
		Application.contractTokenAddress = contractTokenAddress;
	}
	public static String getContractMarketplaceAddress() {
		return contractMarketplaceAddress;
	}
	public static void setContractMarketplaceAddress(String contractMarketplaceAddress) {
		Application.contractMarketplaceAddress = contractMarketplaceAddress;
	}
	public static BigInteger getGasLimit() {
		return gasLimit;
	}
	public static void setGasLimit(BigInteger gasLimit) {
		Application.gasLimit = gasLimit;
	}
	public static String getIpfsAPIUrl() {
		return ipfsAPIUrl;
	}
	public static String getIpfsHTTPUrl() {
		return ipfsHTTPUrl;
	}
	public static void setIpfsHTTPUrl(String ipfsHTTPUrl) {
		Application.ipfsHTTPUrl = ipfsHTTPUrl;
	}
	public static void setIpfsAPIUrl(String ipfsAPIUrl) {
		Application.ipfsAPIUrl = ipfsAPIUrl;
	}
	public static String getUploadDir() {
		return uploadDir;
	}
	public static void setUploadDir(String uploadDir) {
		Application.uploadDir = uploadDir;
	}	
}
