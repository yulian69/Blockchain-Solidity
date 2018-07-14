package solidity.model;

/**
 * @author Yulian Yordanov
 * Created: Jul 11, 2018
 */
public class Account {
	private String privateKey;
	private String address;
		
	public String getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
}
