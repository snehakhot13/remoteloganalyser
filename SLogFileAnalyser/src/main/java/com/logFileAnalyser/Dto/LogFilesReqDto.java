package com.logFileAnalyser.Dto;

public class LogFilesReqDto {
	
	
	private String searchInput;
	private String[] serverName;
	private String[] date;
	private String[] email;
	
	public String getSearchInput() {
		return searchInput;
	}
	public void setSearchInput(String searchInput) {
		this.searchInput = searchInput;
	}
	public String[] getServerName() {
		return serverName;
	}
	public void setServerName(String[] serverName) {
		this.serverName = serverName;
	}
	public String[] getDate() {
		return date;
	}
	public void setDate(String[] date) {
		this.date = date;
	}
	public String[] getEmail() {
		return email;
	}
	public void setEmail(String[] email) {
		this.email = email;
	}
	
	

}
