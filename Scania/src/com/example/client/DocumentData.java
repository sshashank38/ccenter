package com.example.client;

import java.io.Serializable;
import java.util.Map;

public class DocumentData implements Serializable {
	private static final long serialVersionUID = 1L; // ✅ Add serialVersionUID
	private String id;
	private String name;
	private String revision;
	private String created;
	private String modified;
	// private String title;
	private String type;
	private String subtype;
	private String state;
	private String docId;
	private Map<String, String> attributes;

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	// Getters
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getCreated() {
		return created;
	}

	public String getModified() {
		return modified;
	}

	// public String getTitle() { return title; }
	public Map<String, String> getAttributes() {
		return attributes;
	}

	public String getType() { // ✅ Add getter
		return type;
	}

	// Setters
	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}

	// public void setTitle(String title) { this.title = title; }
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public String getSubtype() {
		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public void setType(String type) { // ✅ Add setter
		this.type = type;
	}
}
