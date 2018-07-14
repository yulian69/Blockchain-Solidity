package solidity.model;

/**
 * @author Yulian Yordanov
 * Created: Dec 7, 2017
 */
public class MultipartParameter {
	private String fieldName;
	private String fileName;
	private String storageFileName;
	private String value;	
	private boolean formField;
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public boolean isFormField() {
		return formField;
	}
	public void setFormField(boolean formField) {
		this.formField = formField;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getStorageFileName() {
		return storageFileName;
	}
	public void setStorageFileName(String storageFileName) {
		this.storageFileName = storageFileName;
	}
}
