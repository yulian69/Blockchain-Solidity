package test;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple2;

import rx.Subscription;
import solidity.contracts.SkinCareMarketplace;
import solidity.contracts.SkinCareToken;

public class TestSolidity {
	private static String provider = "http://localhost:8545/";
	private static String privateKey = "0x5bffc8c899b644f4f562cc08cf44a2603475300e14e402db0f14258ceb025d15";
	
	private static String contractTokenAddress = "0x920de0b5576cf2c930d620c39387756b0cf07a26";
	private static String contractMarketplaceAddress = "0x7c8accc8215b6407bc9055107338dad5626ba66f";
	
	public static void main(String[] args) throws Exception {
		//getSellers();
		//getSellerByAddress();
		//addSeller();
		//getBuyerByAddress();
		
		//getJson();
		
		//updateSeller();
		
		//getItems();
		//getEthBalance("0x46dd8bd3e567cde925f51bee0d4f4d799f077344");
		//getEthBalance("0x402104da5ec00e1f7c45c677f5a4fa803715de69");
		
		Web3j web3j = Web3j.build(new HttpService(provider));
		SkinCareToken contract = SkinCareToken.load(contractTokenAddress.substring(2), web3j, Credentials.create("0x0"), new BigInteger("30000000"), BigInteger.valueOf(4600000));
		//contract.getTransferTokensEvents(transactionReceipt);
		
		Subscription subscription = contract.transferEventObservable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST).subscribe( log -> {
			System.out.println("From: " + log.from + ", To: " + log.to + ", Tokens: " + log.tokens.intValue() );
		});
		
		Thread.sleep(10000);
		System.out.println("Unsubscribe");
		
		subscription.unsubscribe();
		System.out.println(subscription.isUnsubscribed());
	}
	
	/*void simpleFilterExample() throws Exception {

        Subscription subscription = web3j.blockObservable(false).subscribe(block -> {
            log.info("Sweet, block number " + block.getBlock().getNumber()
                    + " has just been created");
        }, Throwable::printStackTrace);

        TimeUnit.MINUTES.sleep(2);
        subscription.unsubscribe();
    }
	*/
	public static void getEthBalance(String address) throws InterruptedException, ExecutionException, IOException {
		Web3j web3j = Web3j.build(new HttpService(provider));
		EthGetBalance ethGetBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
		System.out.println(ethGetBalance.getBalance());
	}
	
	public static void getItems() throws Exception {
		Web3j web3j = Web3j.build(new HttpService(provider));
		
		SkinCareMarketplace contract = SkinCareMarketplace.load(contractMarketplaceAddress, web3j, Credentials.create(privateKey), new BigInteger("30000000"), BigInteger.valueOf(4600000));
		Tuple2<byte[], String> tuple2 = contract.getSellerByAddress().send();
		
		contract = SkinCareMarketplace.load(contractMarketplaceAddress, web3j, Credentials.create("0x0"), new BigInteger("30000000"), BigInteger.valueOf(4600000));
		List<byte[]> itemIds = contract.getItems(tuple2.getValue1()).send();
		
		System.out.println(itemIds.size());
	}
	
	public static void updateSeller() throws Exception {	
		System.out.println("Update Seller");
		Web3j web3j = Web3j.build(new HttpService(provider));
		Credentials credentials = Credentials.create(privateKey);
		
		SkinCareMarketplace contract = SkinCareMarketplace.load(contractMarketplaceAddress, web3j, credentials, new BigInteger("30000000"), BigInteger.valueOf(4600000));
		TransactionReceipt txtInfo = contract.updateSeller("Qmdfaw9J733vMTWa5jwkZ8e2hD1EGb6optNLGem6pVGf47").send();
		System.out.println("Transaction sent: " + txtInfo.getTransactionHash()); 
	}
	
	public static void getJson() throws ClientProtocolException, IOException {
		HttpClient httpClient = HttpClientBuilder.create().build();
        
        HttpGet httpGet = new HttpGet("http://localhost:8090/ipfs/" + "Qmdfaw9J733vMTWa5jwkZ8e2hD1EGb6optNLGem6pVGf47");
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity result = response.getEntity();
        
        byte[] bs = new byte[1024];
		InputStream inputStream = result.getContent();
		
		String res = "";
		int read;
		while ((read = inputStream.read(bs)) > 0 ) {
			res += new String(bs,0,read);
		}
		System.out.println(res);
	}
	public static void addSeller() throws Exception {
		System.out.println("Add Seller");
		Web3j web3j = Web3j.build(new HttpService(provider));
		Credentials credentials = Credentials.create(privateKey);
		
		//SkinCareMarketplace contract = SkinCareMarketplace.load(contractMarketplaceAddress, web3j, credentials, getGasLimit(), BigInteger.valueOf(4600000));
		
		String ipfs = "ipfs";
		byte[] bs = new byte[ipfs.length()+1];
		byte[] bs2 = ipfs.getBytes();
		for (int i = 0; i < bs2.length; i++) {
			bs[i] = bs2[i];
		}
		bs[bs.length-1] = 0x1&255;
		
		SkinCareToken contract = SkinCareToken.load(contractTokenAddress, web3j, credentials, new BigInteger("30000000"), BigInteger.valueOf(4600000));
		TransactionReceipt txtInfo = contract.approveAndCall(contractMarketplaceAddress, BigInteger.valueOf(20000), bs).send();
		System.out.println("Transaction sent: " + txtInfo.getTransactionHash()); 
	}
	
	public static void getSellerByAddress() throws Exception {
		System.out.println("Get Seller By Address");
		Web3j web3j = Web3j.build(new HttpService(provider));
		Credentials credentials = Credentials.create(privateKey);
				
		SkinCareMarketplace contract = SkinCareMarketplace.load(contractMarketplaceAddress, web3j, credentials, new BigInteger("30000000"), BigInteger.valueOf(4600000));
		Tuple2<byte[], String> tuple2 = contract.getSellerByAddress().send();
		System.out.println(tuple2.getValue2()); 
	}
	
	public static void getBuyerByAddress() throws Exception {
		System.out.println("Get Buyer By Address");
		Web3j web3j = Web3j.build(new HttpService(provider));
		Credentials credentials = Credentials.create(privateKey);
				
		SkinCareMarketplace contract = SkinCareMarketplace.load(contractMarketplaceAddress, web3j, credentials, new BigInteger("30000000"), BigInteger.valueOf(4600000));
		contract.getBuyerByAddress().send();
		//System.out.println(tuple2.getValue2()); 
	}
	
	public static void getSellers() throws Exception {
		System.out.println("Get Sellers");
		Web3j web3j = Web3j.build(new HttpService(provider));
		Credentials credentials = Credentials.create(privateKey);
				
		SkinCareMarketplace contract = SkinCareMarketplace.load(contractMarketplaceAddress, web3j, credentials, new BigInteger("30000000"), BigInteger.valueOf(4600000));
		List<byte[]> sellers = contract.getSellers().send();
		System.out.println(sellers.size()); 
	}
	
	public static void getAllItems() throws Exception {
		System.out.println("Get All Items");
		Web3j web3j = Web3j.build(new HttpService(provider));
		Credentials credentials = Credentials.create(privateKey);
				
		SkinCareMarketplace contract = SkinCareMarketplace.load(contractMarketplaceAddress, web3j, credentials, new BigInteger("30000000"), BigInteger.valueOf(4600000));
		List<byte[]> sellers = contract.getSellers().send();
		System.out.println(sellers.size()); 
	}
}
