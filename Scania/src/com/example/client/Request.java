package com.example.client;

import java.io.Serializable;

import java.io.Serializable;

public class Request implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int option;
    private String fileKey;
    private boolean flag;

    // Constructor matching the expected usage
    public Request(int option) {
        this.option = option;
    }

    public Request(int option, String fileKey, boolean flag) {
        this.option = option;
        this.fileKey = fileKey;
        this.flag = flag;
    }

    public int getOption() {
        return option;
    }

    public String getFileKey() {
        return fileKey;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
