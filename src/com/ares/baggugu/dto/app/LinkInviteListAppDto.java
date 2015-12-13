package com.ares.baggugu.dto.app;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnore;

public class LinkInviteListAppDto implements Serializable {

	private static final long serialVersionUID = 3434132769728637875L;

	private int id;

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
	 * 是否已领取积分
	 */
	private boolean receiveIntegral;

	/**
	 * 积分额度
	 */
	private String integral;

	/**
	 * 是否已领取红包
	 */
	private boolean receiveBonus;

	/**
	 * 红包额度
	 */
	private String bonus;
	/**
	 * 是否登录过
	 */
	private boolean login;

	/**
	 * 是否已注册
	 */
	@JsonIgnore
	private boolean regist;

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

	public boolean isReceiveIntegral() {
		return receiveIntegral;
	}

	public void setReceiveIntegral(boolean receiveIntegral) {
		this.receiveIntegral = receiveIntegral;
	}

	public String getIntegral() {
		return integral;
	}

	public void setIntegral(String integral) {
		this.integral = integral;
	}

	public boolean isReceiveBonus() {
		return receiveBonus;
	}

	public void setReceiveBonus(boolean receiveBonus) {
		this.receiveBonus = receiveBonus;
	}

	public String getBonus() {
		return bonus;
	}

	public void setBonus(String bonus) {
		this.bonus = bonus;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public boolean isRegist() {
		return regist;
	}

	public void setRegist(boolean regist) {
		this.regist = regist;
	}

	public boolean isLogin() {
		return login;
	}

	public void setLogin(boolean login) {
		this.login = login;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
