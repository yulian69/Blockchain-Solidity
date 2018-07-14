package solidity.factory;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bouncycastle.util.encoders.Hex;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple3;

import com.google.gson.Gson;

import solidity.contracts.SkinCareMarketplace;
import solidity.contracts.SkinCareToken;
import solidity.model.Account;
import solidity.model.Application;
import solidity.model.Item;
import solidity.model.ItemDetail;
import solidity.model.Seller;

/**
 * @author Yulian Yordanov
 * Created: Jul 12, 2018
 */
public class SellerFactory {
	private static Gson gson = new Gson();
	
	public static boolean isSeller(Account account) {
		try {
			SkinCareMarketplace contract = SkinCareMarketplace.load(Application.getContractMarketplaceAddress(), Application.getWeb3j(), Credentials.create(account.getPrivateKey()), new BigInteger("30000000"), BigInteger.valueOf(4600000));
			contract.getSellerByAddress().send();
			return true;
		} catch (Exception e) {	
			//e.printStackTrace();
		}
		return false;		
	}
	
	public static Map<String, String> getSellers() {
		try {
			Map<String, String> map = new TreeMap<String, String>();
			
			SkinCareMarketplace contract = SkinCareMarketplace.load(Application.getContractMarketplaceAddress(), Application.getWeb3j(), Credentials.create("0x0"), new BigInteger("30000000"), BigInteger.valueOf(4600000));
			List<byte[]> sellers = contract.getSellers().send();
			
			for(int i = 0; i < sellers.size(); i++) {
				String ipfs = getSellerById(sellers.get(i));
				Seller seller = gson.fromJson(IpfsFactory.getJson(ipfs), Seller.class);
				map.put(Hex.toHexString(sellers.get(i)), seller.getTradeName());
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new TreeMap<String, String>();
	}
	
	public static ItemDetail[] getItemDetails(String sellerId) {
		try {
			SkinCareMarketplace contract = SkinCareMarketplace.load(Application.getContractMarketplaceAddress(), Application.getWeb3j(), Credentials.create("0x0"), new BigInteger("30000000"), BigInteger.valueOf(4600000));
			List<byte[]> itemIds = contract.getItems(Hex.decode(sellerId)).send();
						
			ItemDetail[] itemDetails = new ItemDetail[itemIds.size()];
			for(int i = 0; i < itemIds.size(); i++) {
				ItemDetail itemDetail = getItemDetail(itemIds.get(i));
				
				itemDetails[i] = itemDetail;
			}
			return itemDetails;
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return new ItemDetail[]{};
	}
	
	public static ItemDetail[] getItemDetails(Account account) {
		try {
			SkinCareMarketplace contract = SkinCareMarketplace.load(Application.getContractMarketplaceAddress(), Application.getWeb3j(), Credentials.create(account.getPrivateKey()), new BigInteger("30000000"), BigInteger.valueOf(4600000));
			Tuple2<byte[], String> tuple2 = contract.getSellerByAddress().send();
			
			contract = SkinCareMarketplace.load(Application.getContractMarketplaceAddress(), Application.getWeb3j(), Credentials.create("0x0"), new BigInteger("30000000"), BigInteger.valueOf(4600000));
			List<byte[]> itemIds = contract.getItems(tuple2.getValue1()).send();
						
			ItemDetail[] itemDetails = new ItemDetail[itemIds.size()];
			for(int i = 0; i < itemIds.size(); i++) {
				ItemDetail itemDetail = getItemDetail(itemIds.get(i));
				
				itemDetails[i] = itemDetail;
			}
			return itemDetails;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ItemDetail[]{};
	}
	
	public static ItemDetail getItemDetail(byte[] itemId) throws Exception {	
		SkinCareMarketplace contract = SkinCareMarketplace.load(Application.getContractMarketplaceAddress(), Application.getWeb3j(), Credentials.create("0x0"), new BigInteger("30000000"), BigInteger.valueOf(4600000));
		Tuple3<String, BigInteger, BigInteger> tuple3 = contract.getItem(itemId).send();
		
		ItemDetail itemDetail= new ItemDetail();
		
		itemDetail.setItemId(Hex.toHexString(itemId));
		itemDetail.setIpfs(tuple3.getValue1());
		itemDetail.setPrice(tuple3.getValue2().intValue());
		itemDetail.setQantity(tuple3.getValue3().intValue());
		
		Item item = gson.fromJson(IpfsFactory.getJson(itemDetail.getIpfs()), Item.class);
		itemDetail.setItem(item);
		
		return itemDetail;		
	}
	
	
	
	public static String getSellerById(byte[] sellerId) throws Exception {		
		SkinCareMarketplace contract = SkinCareMarketplace.load(Application.getContractMarketplaceAddress(), Application.getWeb3j(), Credentials.create("0x0"), new BigInteger("30000000"), BigInteger.valueOf(4600000));
		Tuple2<String, String> tuple2 = contract.getSellerById(sellerId).send();			
		return tuple2.getValue2();		
	}
	
	public static void addSeller(String ipfs, Account account) throws Exception {
		SkinCareToken contract = SkinCareToken.load(Application.getContractTokenAddress(), Application.getWeb3j(), Credentials.create("0x0"), Application.getGasLimit(), BigInteger.valueOf(460000));
		BigInteger balance = contract.balanceOf(account.getAddress()).send();
		
		BigInteger biTokens = BigInteger.valueOf(20000);
		
		if ( balance.compareTo(biTokens) < 0 ) {
			throw new Exception("Insufficient tokens.");
		}
		
		byte[] bs = new byte[ipfs.length() + 1];
		byte[] bs2 = ipfs.getBytes(); 
		for (int i = 0; i < bs2.length; i++) {
			bs[i] = bs2[i];
		}
		bs[bs.length-1] = 0x1&255;
		
		contract = SkinCareToken.load(Application.getContractTokenAddress(), Application.getWeb3j(), Credentials.create(account.getPrivateKey()), new BigInteger("30000000"), BigInteger.valueOf(4600000));
		TransactionReceipt txtInfo = contract.approveAndCall(Application.getContractMarketplaceAddress(), BigInteger.valueOf(20000), bs).send();
		System.out.println("Transaction sent: " + txtInfo.getTransactionHash()); 
	}
	
	public static void updateSeller(String ipfs, String privateKey) throws Exception {		
		SkinCareMarketplace contract = SkinCareMarketplace.load(Application.getContractMarketplaceAddress(), Application.getWeb3j(), Credentials.create(privateKey), new BigInteger("30000000"), BigInteger.valueOf(4600000));
		TransactionReceipt txtInfo = contract.updateSeller(ipfs).send();
		System.out.println("Transaction sent: " + txtInfo.getTransactionHash()); 
	}
	
	public static Seller getSeller(Account account) {
		Seller seller = new Seller();
		try {
			SkinCareMarketplace contract = SkinCareMarketplace.load(Application.getContractMarketplaceAddress(), Application.getWeb3j(), Credentials.create(account.getPrivateKey()), new BigInteger("30000000"), BigInteger.valueOf(4600000));
			Tuple2<byte[], String> tuple2 = contract.getSellerByAddress().send();
			String ipfs = tuple2.getValue2();
						
			String json = IpfsFactory.getJson(ipfs);
						
			seller = gson.fromJson(json, Seller.class);
		} catch (Exception e) {}
		
		return seller;
	}
	
	public static void addItem(String ipfs, int price, int quantity, String privateKey) throws Exception {		
		SkinCareMarketplace contract = SkinCareMarketplace.load(Application.getContractMarketplaceAddress(), Application.getWeb3j(), Credentials.create(privateKey), new BigInteger("30000000"), BigInteger.valueOf(4600000));
		TransactionReceipt txtInfo = contract.addItem(ipfs, BigInteger.valueOf(price), BigInteger.valueOf(quantity)).send();
		System.out.println("Transaction sent: " + txtInfo.getTransactionHash()); 
	}
	
	public static void updateItem(byte[] itemId, String ipfs, int price, int quantity, String privateKey) throws Exception {		
		SkinCareMarketplace contract = SkinCareMarketplace.load(Application.getContractMarketplaceAddress(), Application.getWeb3j(), Credentials.create(privateKey), new BigInteger("30000000"), BigInteger.valueOf(4600000));
		TransactionReceipt txtInfo = contract.updateItem(itemId, ipfs, BigInteger.valueOf(price), BigInteger.valueOf(quantity)).send();
		System.out.println("Transaction sent: " + txtInfo.getTransactionHash()); 
	}
}
