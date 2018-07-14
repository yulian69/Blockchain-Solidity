package solidity.model;

import java.util.Map;
import java.util.TreeMap;

public class MultipartParameters {
	private Map<String, MultipartParameter> map = new TreeMap<>();

	public Map<String, MultipartParameter> getMap() {
		return map;
	}

	public void setMap(Map<String, MultipartParameter> map) {
		this.map = map;
	}
	
	public void addValue(String name, MultipartParameter value) {
		map.put(name, value);
	}
	
	public String getValue(String name) {
		MultipartParameter multipartParameter = map.get(name);
		if ( multipartParameter == null ) {
			return null;
		}
		return multipartParameter.getValue();
	}
	
	public String getValueNotNull(String name) {
		MultipartParameter multipartParameter = map.get(name);
		if ( multipartParameter == null ) {
			return "";
		}
		if ( multipartParameter.getValue() == null ) {
			return "";
		}
		return multipartParameter.getValue();
	}
	
	public boolean getValueAsBool(String name) {
		try {
			return Boolean.parseBoolean(map.get(name).getValue());
		} catch (Exception e) {}
		return false;
	}
	
	public long getValueAsLong(String name) {
		try {
			return Long.parseLong(map.get(name).getValue().replaceAll(",", ""));
		} catch (Exception e) {}
		return 0L;
	}
	
	public int getValueAsInt(String name) {
		try {
			return Integer.parseInt(map.get(name).getValue().replaceAll(",", ""));
		} catch (Exception e) {}
		return 0;
	}
	
	public double getValueAsDouble(String name) {
		try {
			return Double.parseDouble(map.get(name).getValue().replaceAll(",", ""));
		} catch (Exception e) {}
		return 0.0;
	}
	
	public String getFileName(String name) {
		MultipartParameter multipartParameter = map.get(name);
		if ( multipartParameter == null ) {
			return null;
		}
		if (multipartParameter.getFileName() == null) {
			return "";
		}
		return multipartParameter.getFileName();
	}
	
	public String getStorageFileName(String name) {
		MultipartParameter multipartParameter = map.get(name);
		if ( multipartParameter == null ) {
			return null;
		}
		if (multipartParameter.getStorageFileName() == null) {
			return "";
		}
		return multipartParameter.getStorageFileName();
	}
}
