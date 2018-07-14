package solidity.web.listener;

import java.math.BigInteger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.bouncycastle.util.encoders.Hex;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;

import solidity.contracts.SkinCareMarketplace;
import solidity.contracts.SkinCareToken;
import solidity.model.Application;
import solidity.model.BuyItemEvent;
import solidity.model.TransferEvent;
import solidity.storage.BuyItemEvents;
import solidity.storage.TransferEvents;


public class ProjectSolidityContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent contextEvent) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent contextEvent) {
		Application.setWeb3j(Web3j.build(new HttpService("http://localhost:8545/")));
		Application.setContractTokenAddress("0x920de0b5576cf2c930d620c39387756b0cf07a26");
		Application.setContractMarketplaceAddress("0x7c8accc8215b6407bc9055107338dad5626ba66f");
		Application.setGasLimit(BigInteger.valueOf(300000000));
		Application.setIpfsAPIUrl("http://localhost:5001/api/v0/add");
		Application.setIpfsHTTPUrl("http://localhost:8090/ipfs/");
		Application.setUploadDir("c:/uploads/");
		
		Web3j web3j = Application.getWeb3j();
		
		SkinCareToken skinCareToken = SkinCareToken.load(Application.getContractTokenAddress().substring(2), web3j, Credentials.create("0x0"), new BigInteger("30000000"), BigInteger.valueOf(4600000));
		skinCareToken.transferEventObservable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST).subscribe(log -> {
			TransferEvent event = new TransferEvent();
			event.setFrom(log.from);
			event.setTo(log.to);
			event.setTokens(log.tokens.intValue());
			TransferEvents.addTransferEvent(event);
		});
		
		SkinCareMarketplace skinCareMarketplace = SkinCareMarketplace.load(Application.getContractMarketplaceAddress().substring(2), web3j, Credentials.create("0x0"), new BigInteger("30000000"), BigInteger.valueOf(4600000));
		skinCareMarketplace.buyItemEventObservable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST).subscribe(log -> {
			BuyItemEvent event = new BuyItemEvent();
			event.setBuyerId(Hex.toHexString(log.buyerId));
			event.setSellerId(Hex.toHexString(log.sellerId));
			event.setItemId(Hex.toHexString(log.itemId));
			event.setQuantity(log.quantity.intValue());
			event.setAmount(log.amount.intValue());
			event.setTimestamp(log.timestamp.longValue());
			BuyItemEvents.addBuyItemEvent(event);
		});
	}
}
