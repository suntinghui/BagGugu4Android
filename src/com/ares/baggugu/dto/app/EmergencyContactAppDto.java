package com.ares.baggugu.dto.app;

import java.io.Serializable;

public class EmergencyContactAppDto implements Serializable {

	private static final long serialVersionUID = -3055670987818734549L;
	/**
	 * 标识
	 */
	private int id;
	/**
	 * 通讯录名称
	 */
	private String realname;
	/**
	 * 手机号
	 */
	private String telphone;
	/**
	 * 关系
	 */
	private String relationship;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getTelphone() {
		return telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

}
