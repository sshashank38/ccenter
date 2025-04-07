package com.example.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Data implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<PackageData> packages = new ArrayList<>();
	private List<Part> parts = new ArrayList<>();
	private List<DocumentData> documents = new ArrayList<>();

	// Default constructor
	public Data() {
	}

	// Constructor with initialization
	public Data(List<PackageData> packages, List<Part> parts, List<DocumentData> documents) {
		this.packages = (packages != null) ? packages : new ArrayList<>();
		this.parts = (parts != null) ? parts : new ArrayList<>();
		this.documents = (documents != null) ? documents : new ArrayList<>();
	}

	// Getters
	public List<PackageData> getPackages() {
		return packages;
	}

	public List<Part> getParts() {
		return parts;
	}

	public List<DocumentData> getDocuments() {
		return documents;
	}

	// Setters (Ensure Non-null Lists)
	public void setPackages(List<PackageData> packages) {
		this.packages = (packages != null) ? packages : new ArrayList<>();
	}

	public void setParts(List<Part> parts) {
		this.parts = (parts != null) ? parts : new ArrayList<>();
	}

	public void setDocuments(List<DocumentData> documents) {
		this.documents = (documents != null) ? documents : new ArrayList<>();
	}
}
