package com.ares.baggugu.dto.app;

import java.io.Serializable;

public class PhonebookAppDto implements Serializable {

	private static final long serialVersionUID = -8834012610889078804L;

	/**
	 * 名字
	 */
	private String name;

	/**
	 * 手机号
	 */
	private String telphone;
	/**
	 * 是否已邀请
	 */
	private boolean invite;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTelphone() {
		return telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	public boolean isInvite() {
		return invite;
	}

	public void setInvite(boolean invite) {
		this.invite = invite;
	}

}
