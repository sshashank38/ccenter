package com.example.client;

import java.io.Serializable;

public class Response implements Serializable {
	private static final long serialVersionUID = 1L;
	private Object data;
	private String error;

	// Default Constructor
	public Response() {
	}

	// Constructor for quick initialization
	public Response(Object data, String error) {
		this.data = data;
		this.error = error;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		if (error != null) {
			return "Response{error='" + error + "'}";
		}
		return "Response{data=\n" + data + "\n}";
	}
}
