package com.example.model;

public class FileModel {
	private int key;
	private String name;
	private String path;
	private String username;
	private long size=0;
	
	public FileModel(){}
	
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long l) {
		this.size = l;
	}
	
	
}
