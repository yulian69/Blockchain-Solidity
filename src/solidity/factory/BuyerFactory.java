package solidity.factory;

import java.math.BigInteger;

import org.bouncycastle.util.encoders.Hex;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;

import com.google.gson.Gson;

import solidity.contracts.SkinCareMarketplace;
import solidity.contracts.SkinCareToken;
import solidity.model.Account;
import solidity.model.Application;
import solidity.model.Buyer;

/**
 * @author Yulian Yordanov
 * Created: Jul 12, 2018
 */
public class BuyerFactory {
	private static Gson gson = new Gson();
	
	public static boolean isBuyer(Account account) {
		try {
			SkinCareMarketplace contract = SkinCareMarketplace.load(Application.getContractMarketplaceAddress(), Application.getWeb3j(), Credentials.create(account.getPrivateKey()), new BigInteger("30000000"), BigInteger.valueOf(4600000));
			contract.getBuyerByAddress().send();
			return true;
		} catch (Exception e) {
		}
		return false;	
	}
	
	public static byte[] getBuyerId(Account account) {
		try {
			SkinCareMarketplace contract = SkinCareMarketplace.load(Application.getContractMarketplaceAddress(), Application.getWeb3j(), Credentials.create(account.getPrivateKey()), new BigInteger("30000000"), BigInteger.valueOf(4600000));
			Tuple2<byte[], String> tuple2 = contract.getBuyerByAddress().send();
			return tuple2.getValue1();
		} catch (Exception e) {
		}
		return new byte[0];	
	}
	
	public static void registerAndBuy(String itemId, String ipfs, int tokens, Account account) throws Exception {
		SkinCareToken contract = SkinCareToken.load(Application.getContractTokenAddress(), Application.getWeb3j(), Credentials.create("0x0"), Application.getGasLimit(), BigInteger.valueOf(460000));
		BigInteger balance = contract.balanceOf(account.getAddress()).send();
		
		BigInteger biTokens = BigInteger.valueOf(tokens);
		
		if ( balance.compareTo(biTokens) < 0 ) {
			throw new Exception("Insufficient tokens.");
		}
		
		byte[] bs = new byte[32 + ipfs.length() + 1];
		byte[] bs1 = Hex.decode(itemId);
		byte[] bs2 = ipfs.getBytes();
		for (int i = 0; i < bs1.length; i++) {
			bs[i] = bs1[i];
		}
		for (int i = 0; i < bs2.length; i++) {
			bs[i+32] = bs2[i];
		}
		bs[bs.length-1] = 0x2&255;
		
		contract = SkinCareToken.load(Application.getContractTokenAddress(), Application.getWeb3j(), Credentials.create(account.getPrivateKey()), new BigInteger("30000000"), BigInteger.valueOf(4600000));
		TransactionReceipt txtInfo = contract.approveAndCall(Application.getContractMarketplaceAddress(), BigInteger.valueOf(tokens), bs).send();
		System.out.println("Transaction sent: " + txtInfo.getTransactionHash()); 
	}
	
	public static void buy(String itemId, int tokens, Account account) throws Exception {
		SkinCareToken contract = SkinCareToken.load(Application.getContractTokenAddress(), Application.getWeb3j(), Credentials.create("0x0"), Application.getGasLimit(), BigInteger.valueOf(460000));
		BigInteger balance = contract.balanceOf(account.getAddress()).send();
		
		BigInteger biTokens = BigInteger.valueOf(tokens);
		
		if ( balance.compareTo(biTokens) < 0 ) {
			throw new Exception("Insufficient tokens.");
		}
		
		byte[] bs = new byte[33];
		byte[] bs1 = Hex.decode(itemId);
		for (int i = 0; i < bs1.length; i++) {
			bs[i] = bs1[i];
		}
		bs[bs.length-1] = 0x3&255;
		
		contract = SkinCareToken.load(Application.getContractTokenAddress(), Application.getWeb3j(), Credentials.create(account.getPrivateKey()), new BigInteger("30000000"), BigInteger.valueOf(4600000));
		TransactionReceipt txtInfo = contract.approveAndCall(Application.getContractMarketplaceAddress(), BigInteger.valueOf(tokens), bs).send();
		System.out.println("Transaction sent: " + txtInfo.getTransactionHash()); 
	}
	
	public static Buyer getBuyer(Account account) {
		Buyer buyer = new Buyer();
		try {
			SkinCareMarketplace contract = SkinCareMarketplace.load(Application.getContractMarketplaceAddress(), Application.getWeb3j(), Credentials.create(account.getPrivateKey()), new BigInteger("30000000"), BigInteger.valueOf(4600000));
			Tuple2<byte[], String> tuple2 = contract.getBuyerByAddress().send();
			String ipfs = tuple2.getValue2();
						
			String json = IpfsFactory.getJson(ipfs);
						
			buyer = gson.fromJson(json, Buyer.class);
		} catch (Exception e) {}
		
		return buyer;
	}
	
	public static void updateBuyer(String ipfs, String privateKey) throws Exception {		
		SkinCareMarketplace contract = SkinCareMarketplace.load(Application.getContractMarketplaceAddress(), Application.getWeb3j(), Credentials.create(privateKey), new BigInteger("30000000"), BigInteger.valueOf(4600000));
		TransactionReceipt txtInfo = contract.updateBuyer(ipfs).send();
		System.out.println("Transaction sent: " + txtInfo.getTransactionHash()); 
	}
}
