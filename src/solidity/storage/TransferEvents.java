package solidity.storage;

import java.util.ArrayList;
import java.util.List;

import solidity.model.TransferEvent;

/**
 * @author Yulian Yordanov
 * Created: Jul 13, 2018
 */
public class TransferEvents {
	private static List<TransferEvent> transferEvents = new ArrayList<>();

	public static void addTransferEvent(TransferEvent transferEvent) {
		transferEvents.add(transferEvent);
	}
	
	public static List<TransferEvent> getTransferEvents() {
		return transferEvents;
	}	
}
