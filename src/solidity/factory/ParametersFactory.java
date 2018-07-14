package solidity.factory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import solidity.model.MultipartParameter;
import solidity.model.MultipartParameters;
import solidity.model.Parameters;

public class ParametersFactory {
	private final static int MAX_LEN = 64;
	private final static int BUFF_SIZE = 50*1024; //50K
	
	public static Parameters getInstanse(HttpServletRequest httpServletRequest) {
		Parameters parameters = new Parameters();
		
		try {
			String request;
			String queryString = (httpServletRequest.getQueryString() != null) ? httpServletRequest.getQueryString() : "";
			byte[] buffer = new byte[BUFF_SIZE];
			int read = httpServletRequest.getInputStream().read(buffer);
			if ( read >= 0 ) {
				request = new String(buffer,0,read,StandardCharsets.UTF_8) + "&" + queryString;
			} else {
				request = queryString;
			}
			
			String[] items = request.split("\\&");
			for (String item : items) {
				int pos = item.indexOf('=');
				if ( pos > 0 ) {
					String name = URLDecoder.decode(item.substring(0, pos), StandardCharsets.UTF_8.name()).trim();
					String value = URLDecoder.decode(item.substring(pos+1), StandardCharsets.UTF_8.name()).trim();
					if ( name.length() > MAX_LEN ) {
						continue;
					}
					int limit = 256;
					if ( value.length() > limit ) {
						value = value.substring(0, limit);
					}
					parameters.addValue(name, value);
				} else {
					String name = URLDecoder.decode(item, StandardCharsets.UTF_8.name()).trim();
					if ( name.length() > MAX_LEN ) {
						continue;
					}
					parameters.addValue(name, "");
				}
			}
		} catch (IOException e) {}
    	
    	return parameters;
    }
	
	public static MultipartParameters getInstanseMultiPart(HttpServletRequest httpServletRequest) {
		MultipartParameters parameters = new MultipartParameters();
		
		long time = System.currentTimeMillis();
		int index = 1;
		
		ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
		try {
			List<FileItem> formItems = upload.parseRequest(httpServletRequest);
			for (FileItem formItem : formItems) {
				if ( formItem.isFormField() ) {
					String name = formItem.getFieldName().trim();
					String value = formItem.getString().trim();
					if ( name.length() > MAX_LEN ) {
						continue;
					}
					int limit = 256;
					if ( value.length() > limit ) {
						value = value.substring(0, limit);
					}
					MultipartParameter multipartParameter = new MultipartParameter();
					multipartParameter.setFieldName(name);
					multipartParameter.setValue(value);
					multipartParameter.setFormField(true);
					parameters.addValue(multipartParameter.getFieldName(), multipartParameter);
				} else {
					String name = formItem.getFieldName().trim();
					String fileName = formItem.getName().trim();
					String storageFileName = time + "-" + index++;
					saveFile(formItem, storageFileName);
					
					MultipartParameter multipartParameter = new MultipartParameter();
					multipartParameter.setFieldName(name);
					multipartParameter.setFileName(fileName);
					multipartParameter.setStorageFileName(storageFileName);
					multipartParameter.setFormField(false);
					
					parameters.addValue(multipartParameter.getFieldName(), multipartParameter);
				}
			}
		} catch (FileUploadException e) {}
    	
    	return parameters;
    }
	
	private static void saveFile(FileItem formItem, String storageFileName) {
		try {
			FileOutputStream outputStream = new FileOutputStream("c:/uploads/" + storageFileName);
			byte[] bs = new byte[1024];
			int read = 0;
			InputStream inputStream = formItem.getInputStream();
			while ( (read = inputStream.read(bs)) > 0 ) {
				outputStream.write(bs, 0, read);
			}
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public static String getParamsToString(Parameters parameters) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();		
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);		
		objectOutputStream.writeObject(parameters.getMap());		
		return Base64.encodeBase64String(byteArrayOutputStream.toByteArray());
	}
	
	public static Parameters getStringToParams(String string) throws IOException, ClassNotFoundException {
		ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(Base64.decodeBase64(string)));	
		Parameters parameters = new Parameters();
		parameters.setMap((Map<String, String>)objectInputStream.readObject());
		return parameters;
	}
}
