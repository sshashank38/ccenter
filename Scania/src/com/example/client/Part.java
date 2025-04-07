package com.example.client;

import java.io.Serializable;
import java.util.Map;

public class Part implements Serializable {
	private static final long serialVersionUID = 1L; // ✅ Add serialVersionUID
	private String id;
	private String name;
	private String state;
	private String partNumber;
	private String subtype;
    private String revision;
    private String created;
    private String modified;
    private String type;
    
	public String getSubtype() {
		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
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

	public void setType(String type) {
		this.type = type;
	}

	private Map<String, String> attributes;

	// Getters
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getState() {
		return state;
	}

	public String getPartNumber() {
		return partNumber;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	// Setters
	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
}
