package com.zsb.model;

import java.io.Serializable;
import java.util.Map;

/**
 * @date 2016年6月2日
 * @author zhoushanbin
 *
 */
public class MdsParam implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2703536505837450878L;
	private String address;
	
	private String userName;
	private String pwd;
	private String serviceId;
	
	private Map<String,String> conf;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public String calcurateKey(){
		StringBuffer buf = new StringBuffer();
		buf.append(address).append("_");
		buf.append(userName).append("_");
		buf.append(serviceId).append("_");
		return buf.toString();
	}
	public Map<String,String> getConf() {
		return conf;
	}
	public void setConf(Map<String,String> conf) {
		this.conf = conf;
	}
	
}
