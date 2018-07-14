package solidity.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.text.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Utils {
	public static final long PASSWORD_EXPIRATION_TIME = 90*24*60*60*1000L; //90 days
	private static String specialSymbols = "!@#$%^&*()";
		
	public static String escapeHTML(String string) {
		return StringEscapeUtils.escapeHtml4(string);
	}
    
    public static String escapeXML(String string) {
		return StringEscapeUtils.escapeXml11(string);
	}
	
	public static void printResponse(HttpServletResponse response, String text) {
		try {
			PrintWriter writer = response.getWriter();		
			
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");		
			
			writer.write(text);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}	
	
	public static Document getDocument() throws ParserConfigurationException {
		return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();		
	}
	
	public static void print(HttpServletResponse response, String xml) throws IOException {
		PrintWriter writer = response.getWriter();		
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");			
		writer.write(xml);
		writer.flush();
		writer.close();
	}
	
	public static void print(HttpServletResponse response, Document document) throws IOException, TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		String xml = getXML(document);	
		print(response, xml);
		PrintWriter writer = response.getWriter();		
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");			
		writer.write(xml);
		writer.flush();
		writer.close();
	}
	
	public static String getXML(Document document) throws TransformerFactoryConfigurationError,	TransformerConfigurationException, TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer trans = tf.newTransformer();
		StringWriter strWtr = new StringWriter();
		StreamResult strResult = new StreamResult(strWtr);
		trans.transform(new DOMSource(document.getDocumentElement()), strResult);
		
		return strResult.getWriter().toString();
	}
	
	public static void printXML(HttpServletResponse response, String xml) throws IOException {
		PrintWriter writer = response.getWriter();		
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");		
		writer.write(xml);
		writer.flush();
		writer.close();
	}
		
	public static void printText(HttpServletResponse response, String text) throws IOException {
		PrintWriter writer = response.getWriter();		
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");		
		writer.write(text);
		writer.flush();
		writer.close();
	}
	
	public static void printHtml(HttpServletResponse response, String html) throws IOException {
		PrintWriter writer = response.getWriter();		
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");				
		writer.write(html);
		writer.flush();
		writer.close();
	}
	
	public static void setError(Document document, Element elErrors, String id, String msg) {
		Element elError = document.createElement("error");
		elError.setAttribute("id", id);
		elError.setAttribute("text", msg);
		elErrors.appendChild(elError);
	}
	
	public static boolean printErrors(HttpServletResponse response, Document document, Element elErrors) throws TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException, IOException {
		if ( elErrors.getChildNodes().getLength() > 0 ) {
			Element elResponse = document.createElement("response");
			elResponse.setAttribute("success", "false");
			elResponse.appendChild(elErrors);
			document.appendChild(elResponse);
			String xml = getXML(document);
			print(response, xml);
			return true;
		}
		return false;
	}
		
	public static void printResponse(HttpServletResponse response, Document document, String text) throws TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException, IOException {
		Element elResponse = document.createElement("response");
		elResponse.setAttribute("success", "true");
		elResponse.appendChild(document.createTextNode(text));
		document.appendChild(elResponse);
		String xml = getXML(document);	
		print(response, xml);
	}
	
	public static void printTextError(HttpServletResponse response, String error) throws IOException {
		response.sendError(400, error);
	}
	
	public static void export(HttpServletResponse response, String text, String filename) throws IOException {
		response.setHeader("Content-Disposition", "attachment; filename=" + filename);

		DataOutputStream outputStream = new DataOutputStream(response.getOutputStream());
		outputStream.write(text.getBytes());
		outputStream.close();
	}
	
	public static String formatAmount(double amount, int fractionDigits) {
    	NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
    	numberFormat.setMaximumFractionDigits(fractionDigits);
    	numberFormat.setMinimumFractionDigits(fractionDigits);
    	
		return numberFormat.format(amount);
	}
	 
    public static String formatAmount(double amount) {
    	return formatAmount(amount, 2);
	}
    
    public static String formatNumber(double amount) {
    	NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
    	numberFormat.setMaximumFractionDigits(0);
    	numberFormat.setMinimumFractionDigits(0);
    	
		return numberFormat.format(amount);
	}
    
    public static String formatCC(String cardNumber) {
    	if ( cardNumber.length() >= 10 ) {
    		cardNumber = cardNumber.substring(0,6) + getAsterisks(cardNumber.length()-10) + cardNumber.substring(cardNumber.length()-4);
    	}
    	
		return cardNumber;
	}
    
    private static String getAsterisks(int len) {
    	String s = "";
    	for (int i = 0; i < len; i++) {
    		s += "*";
    	}
    	return s;
    }
    
    public static String getErrorMessage(String errorMessage, String value) {
    	return errorMessage.replaceAll("_1_", value);
	}
    
    public static boolean validateEmail(String email) {
    	Pattern pattern = Pattern.compile("^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,3})$");
		return pattern.matcher(email.toLowerCase()).matches();
    }
    
    public static boolean validateSwift(String swift) {
    	Pattern pattern = Pattern.compile("^([a-zA-Z]){4}([a-zA-Z]){2}([0-9a-zA-Z]){2}([0-9a-zA-Z]{3})?$");
		return pattern.matcher(swift.toLowerCase()).matches();
    }
    
    public static boolean validatePhone(String phone) {
    	try {
	    	if ( phone.startsWith("+") ) {
	    		phone = phone.substring(1);
	    	}
			return Long.parseLong(phone.replaceAll(" ", "").replaceAll("-", "")) > 0;
    	} catch (Exception e) {
			return false;
		}
    }
    
    public static String generatePassword() {
    	return generatePassword(16);
    }
    
    public static String generatePassword(int len) {
		StringBuffer password = new StringBuffer();
		StringBuffer symbols = new StringBuffer();
		for (int i = 0; i < 26; i++) {
			char c = (char)('a'+i);
			symbols.append(c);
		}
		for (int i = 0; i < 26; i++) {
			char c = (char)('A'+i);
			symbols.append(c);
		}
		for (int i = 0; i < 10; i++) {
			char c = (char)('0'+i);
			symbols.append(c);
		}
		symbols.append("!@#$%^&*()");
		
		for (int i = 0; i < 10; i++) {
			char c = (char)('0'+i);
			symbols.append(c);
		}
		Random random = new Random();
		for (int i = 0; i < len; i++) {
			password.append(symbols.charAt(random.nextInt(symbols.length())));
		}		
		return password.toString();
	}
    
    
    public static Calendar getUTCDate() {
    	Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MILLISECOND, -calendar.getTimeZone().getOffset(calendar.getTimeInMillis()));
		return calendar;
    }
    

	public static String encrypt(String text, String key) {
        try {
			SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);

			return Base64.encodeBase64String(cipher.doFinal(text.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 
        return "";
    }
	
	public static String decrypt(String encrypted, String key) {
        try {
			SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);

			return new String(cipher.doFinal(Base64.decodeBase64(encrypted)),StandardCharsets.UTF_8);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
        return "";
    }
	
	public static boolean validatePassword(String password) {
		if ( password.length() < 7 ) {
			return false;
		}
		
		boolean hasLowerCase = false;
		boolean hasUpperCase = false;
		boolean hasDigit = false;
		boolean hasSpecialSybmol = false;
		
		for (int i = 0; i < password.length(); i++) {
			char ch = password.charAt(i);
			if ( ch >= 'a' && ch <= 'z' ) {
				hasLowerCase = true;
			} else if ( ch >= 'A' && ch <= 'Z' ) {
				hasUpperCase = true;
			} else if ( ch >= '0' && ch <= '9' ) {
				hasDigit = true;
			} else if ( specialSymbols.indexOf(ch) >= 0 ) {
				hasSpecialSybmol = true;
			}
		}
		
		return hasLowerCase&hasUpperCase&hasDigit&hasSpecialSybmol;
	}
	
	public static Timestamp addDays(Timestamp timestamp, int days) {
		return addDays(timestamp, days, 0);
	}
	
	public static Date addDays(Date date, int days) {
		return addDays(date, days, 0);
	}

	public static Timestamp addDays(Timestamp timestamp, int days, int miliseconds) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp.getTime());
		calendar.add(Calendar.DATE, days);
		calendar.add(Calendar.MILLISECOND, miliseconds);
		return new Timestamp(calendar.getTimeInMillis());
	}
	
	public static Date addDays(Date date, int days, int miliseconds) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date.getTime());
		calendar.add(Calendar.DATE, days);
		calendar.add(Calendar.MILLISECOND, miliseconds);
		return new Date(calendar.getTimeInMillis());
	}
	
	private static String digestEncode(String string, String salt, String algorithm) {
        try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			md.update(string.getBytes());
			byte[] bytes = (salt != null) ? md.digest(salt.getBytes()) : md.digest();
			        
			StringBuffer buff = new StringBuffer();        
			for ( int i = 0; i < bytes.length; i++ ) {   
				buff.append(String.format("%02x", bytes[i]));	
			}
			return buff.toString();
		} catch (NoSuchAlgorithmException e) {
			return "";
		}        
    }
	
	private static String digestEncode(String string, String algorithm) {
        return digestEncode(string, null, algorithm);  
    }
	
	public static String MD5encode(String string) {
        return digestEncode(string, "MD5");
    }
	
	public static String SHA256encode(String string) {
        return digestEncode(string, "SHA-256");
    }
	
	public static String SHA512encode(String string, String salt) {
        return digestEncode(string, salt, "SHA-512");
    }
	
	private static boolean validateIP(String ip) {
		String[] items = ip.split("\\.");
		if ( items.length != 4 ) {
			return false;
		}
		
		for (String item : items) {
			try {
				int number = Integer.parseInt(item);
				if ( number < 0 || number > 255 ) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}
	
	public static String getRemoteAddr(HttpServletRequest request) {
		String ip = request.getRemoteAddr();
		if ( request.getHeader("X-Forwarded-For") != null ) {
			ip = request.getHeader("X-Forwarded-For").split("\\,")[0].trim();
			if ( !validateIP(ip) ) {
				ip = request.getRemoteAddr();
			}
		}
        return ip;
    }
	
	public static String generateAuthSecret() {
		byte[] bs = new byte[40];		
		new Random().nextBytes(bs);
		return new Base32().encodeAsString(bs);
	}
	
	public static String getAuthCode(String secret) {		
		long counter = new Date().getTime() / 30000;

		Key key = new SecretKeySpec(new Base32().decode(secret), "HmacSHA1");
  
		Mac mac = null;
		try {
			mac = Mac.getInstance("HmacSHA1");
			mac.init(key);
		} catch (InvalidKeyException | NoSuchAlgorithmException e) {}
  
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.putLong(0, counter);

		byte[] hmac = mac.doFinal(buffer.array());
		int offset = hmac[hmac.length-1] & 0x0f;

		for (int i = 0; i < 4; i++) {
		   buffer.put(i, hmac[i+offset]);
		}
		
		String code = "" + (buffer.getInt(0) & 0x7fffffff) % 1_000_000;		
		while ( code.length() < 6 ) {
			code = "0" + code;
		}
		
		return code;
	}
	
}
