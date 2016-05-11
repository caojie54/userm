package com.ztpx.model;

import java.util.Date;
/*
 * ������Ϣ
 * */
public class orderBean {
	
	//�������
	private int id;
	//�û����
	private int userid;
	//�ռ�������
	private String name;
	//�绰����
	private String phone;
	//��ַ
	private String address;
	//����ʱ��
	private Date createTime;

	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getUserid() {
		return userid;
	}


	public void setUserid(int userid) {
		this.userid = userid;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public Date getCreateTime() {
		return createTime;
	}


	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}


}
