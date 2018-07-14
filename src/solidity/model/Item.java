package solidity.model;

/**
 * @author Yulian Yordanov
 * Created: Jul 12, 2018
 */
public class Item {
	private String name;
	private String description;
	private String size;
	private String ipfs;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getIpfs() {
		return ipfs;
	}
	public void setIpfs(String ipfs) {
		this.ipfs = ipfs;
	}	
}
