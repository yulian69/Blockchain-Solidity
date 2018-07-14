package solidity.factory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.Gson;

import solidity.model.Application;

/**
 * @author Yulian Yordanov
 * Created: Jul 12, 2018
 */
public class IpfsFactory {
	public static String addFile(String filename) throws IOException {
		HttpClient httpClient = HttpClientBuilder.create().build();
        
        HttpEntity entity = MultipartEntityBuilder
        	    .create()
        	    .addBinaryBody("file", new File(filename), ContentType.create("application/octet-stream"), filename)
        	    .build();
        
        HttpPost httpPost = new HttpPost(Application.getIpfsAPIUrl());
        httpPost.setEntity(entity);
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity result = response.getEntity();
                
        return getHash(result);
	}
	
	public static String addJson(String json) throws IOException {
		HttpClient httpClient = HttpClientBuilder.create().build();
        
        HttpEntity entity = MultipartEntityBuilder
        	    .create()
        	    .addTextBody("json", json, ContentType.create("application/json"))
        	    .build();
        
        HttpPost httpPost = new HttpPost(Application.getIpfsAPIUrl());
        httpPost.setEntity(entity);
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity result = response.getEntity();
        
        return getHash(result);
	}
	
	public static String getJson(String ipfs) throws IOException {
		HttpClient httpClient = HttpClientBuilder.create().build();
                
        HttpGet httpGet = new HttpGet(Application.getIpfsHTTPUrl() + ipfs);
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity result = response.getEntity();
        
        return getResult(result);
	}

	private static String getHash(HttpEntity result) throws IOException {
		byte[] bs = new byte[1024];
		InputStream inputStream = result.getContent();
		
		String res = "";
		int read;
		while ((read = inputStream.read(bs)) > 0 ) {
			res += new String(bs,0,read);
		}
		
		String[] lines = res.split("\n");
		
		Gson gson = new Gson();
		Map<String, String> map = gson.fromJson(lines[0], HashMap.class);
		return map.get("Hash");
	}
	
	private static String getResult(HttpEntity result) throws IOException {
		byte[] bs = new byte[1024];
		InputStream inputStream = result.getContent();
		
		String res = "";
		int read;
		while ((read = inputStream.read(bs)) > 0 ) {
			res += new String(bs,0,read);
		}
		return res;
	}
}
