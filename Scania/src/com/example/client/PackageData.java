package com.example.client;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class PackageData implements Serializable {
	private static final long serialVersionUID = 1L; // ✅ Add serialVersionUID
	private String id;
	private String type;
	private String subtype;
	private String created;
	private String modified;
	private String state;
//	private List<String> includedDocuments;
//	private List<String> includedParts;
	private Map<String, String> attributes;

	// Getters
	public String getId() {
		return id;
	}

	public String getSubtype() {
		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getModified() {
		return modified;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}

	public String getType() {
		return type;
	}

	public String getState() {
		return state;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	// Setters
	public void setId(String id) {
		this.id = id;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public void setType(String type) {
		this.type = type;
		
	}
//	public List<String> getIncludedDocuments() {
//	return includedDocuments;
//}
//
//public List<String> getIncludedParts() {
//	return includedParts;
//}
//	public void setIncludedDocuments(List<String> includedDocuments) {
//	this.includedDocuments = includedDocuments;
//}
//
//public void setIncludedParts(List<String> includedParts) {
//	this.includedParts = includedParts;
//}
}
