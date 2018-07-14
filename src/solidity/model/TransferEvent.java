package solidity.model;

/**
 * @author Yulian Yordanov
 * Created: Jul 13, 2018
 */
public class TransferEvent {
	private String from;
	private String to;
	private int tokens;
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public int getTokens() {
		return tokens;
	}
	public void setTokens(int tokens) {
		this.tokens = tokens;
	}
}
