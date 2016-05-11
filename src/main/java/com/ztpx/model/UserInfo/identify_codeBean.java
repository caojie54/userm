package com.ztpx.model.UserInfo;

public class identify_codeBean {
	
	//验证码编号
	private int id;
	//用户编号
	private int userId;
	//验证码内容
	private String identify_code;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getIdentify_code() {
		return identify_code;
	}
	public void setIdentify_code(String identify_code) {
		this.identify_code = identify_code;
	}

	
}
