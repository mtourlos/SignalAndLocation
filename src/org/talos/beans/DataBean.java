package org.talos.beans;

public class DataBean {
	String timeStamp;
	String user;
	String operator;
	String networkType;
	String cinr;
	String latitude;
	String longitude;
	
	
	public DataBean() {
		super();
	}
	
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getNetworkType(){
		return networkType;
	}
	public void setNetworkType(String networkType){
		this.networkType = networkType;
	}
	public String getCinr() {
		return cinr;
	}
	public void setCinr(String cinr) {
		this.cinr = cinr;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	

}
