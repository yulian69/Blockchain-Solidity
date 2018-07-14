package solidity.storage;

import java.util.ArrayList;
import java.util.List;

import solidity.model.BuyItemEvent;

/**
 * @author Yulian Yordanov
 * Created: Jul 13, 2018
 */
public class BuyItemEvents {
	private static List<BuyItemEvent> buyItemEvents = new ArrayList<>();

	public static void addBuyItemEvent(BuyItemEvent buyItemEvent) {
		buyItemEvents.add(buyItemEvent);
	}
	
	public static List<BuyItemEvent> getBuyItemEvents() {
		return buyItemEvents;
	}	
}
