package com.ztpx.model.UserInfo;

import java.util.Date;

public class UserInfoBean {
	
	 //用户编号
	 private int id;
	 //用户名
	 private String username;
	 //密码
	 private String password;
	 //昵称
	 private String nickname;
	 //邮箱
	 private String email;
	 //性别
	 private int gender;
	 //出生年月
	 private String birthday;
	 //手机号
	 private String telNum;
	 //收货地址
	 private String shopping_address;
	 //可用状态
	 private int active;
	 //是否在线
	 private int isOnline;
	 //常用地
	 private String commonLocation;
	 
	 
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getTelNum() {
		return telNum;
	}
	public void setTelNum(String telNum) {
		this.telNum = telNum;
	}
	public String getShopping_address() {
		return shopping_address;
	}
	public void setShopping_address(String shopping_address) {
		this.shopping_address = shopping_address;
	}
	public int getActive() {
		return active;
	}
	public void setActive(int active) {
		this.active = active;
	}
	public int getIsOnline() {
		return isOnline;
	}
	public void setIsOnline(int isOnline) {
		this.isOnline = isOnline;
	}
	public String getCommonLocation() {
		return commonLocation;
	}
	public void setCommonLocation(String commonLocation) {
		this.commonLocation = commonLocation;
	}
	 
	 
}
