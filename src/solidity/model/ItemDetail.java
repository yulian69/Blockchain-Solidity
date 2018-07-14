package solidity.model;

/**
 * @author Yulian Yordanov
 * Created: Jul 12, 2018
 */
public class ItemDetail {
	private String itemId;
	private int price;
	private int qantity;
	private Item item;
	private String ipfs;
	
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getQantity() {
		return qantity;
	}
	public void setQantity(int qantity) {
		this.qantity = qantity;
	}
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	public String getIpfs() {
		return ipfs;
	}
	public void setIpfs(String ipfs) {
		this.ipfs = ipfs;
	}	
}
