package com.ares.baggugu.dto.app;

import java.io.Serializable;

public class LinkInviteDebtListAppDto implements Serializable {

	private static final long serialVersionUID = -5183960311139294892L;

	/**
	 * 记录标示
	 */
	private int id;

	/**
	 * 用户标示
	 */
	private int userId;
	/**
	 * 用户头像
	 */
	private String logo;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 手机号
	 */
	private String telphone;

	/**
	 * 抢投金额
	 */
	private String qtMoney;

	/**
	 * 定投金额
	 */
	private String dtMoney;
	/**
	 * 定投金额
	 */
	private String hqMoney;

	/**
	 * 收益
	 */
	private String earning;

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

	public String getQtMoney() {
		return qtMoney;
	}

	public void setQtMoney(String qtMoney) {
		this.qtMoney = qtMoney;
	}

	public String getDtMoney() {
		return dtMoney;
	}

	public void setDtMoney(String dtMoney) {
		this.dtMoney = dtMoney;
	}

	public String getEarning() {
		return earning;
	}

	public void setEarning(String earning) {
		this.earning = earning;
	}

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

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getHqMoney() {
		return hqMoney;
	}

	public void setHqMoney(String hqMoney) {
		this.hqMoney = hqMoney;
	}

}
