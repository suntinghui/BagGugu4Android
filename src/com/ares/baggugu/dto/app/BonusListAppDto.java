package com.ares.baggugu.dto.app;

import java.io.Serializable;

public class BonusListAppDto implements Serializable {

	private static final long serialVersionUID = -3841090980747635133L;

	/**
	 * 记录标示
	 */
	private int id;

	/**
	 * 用户头像
	 */
	private String logo;

	/**
	 * 用户名称
	 */
	private String name;

	/**
	 * 金额
	 */
	private String money;

	/**
	 * 是否领取
	 */
	private boolean receive;
	/**
	 * 是否可以领取
	 */
	private boolean activation;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public boolean isReceive() {
		return receive;
	}

	public void setReceive(boolean receive) {
		this.receive = receive;
	}

	public boolean isActivation() {
		return activation;
	}

	public void setActivation(boolean activation) {
		this.activation = activation;
	}

}
