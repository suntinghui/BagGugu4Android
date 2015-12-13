package com.ares.baggugu.dto.app;

import java.io.Serializable;

public class MonthAddRateListAppDto implements Serializable {

	private static final long serialVersionUID = 8851094447009985550L;
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
	 * 用户名称
	 */
	private String name;
	/**
	 * 是否送过红包
	 */
	private boolean isBonus;
	/**
	 * 是否使用了
	 */
	private boolean isUsed;

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

	public boolean isBonus() {
		return isBonus;
	}

	public void setBonus(boolean isBonus) {
		this.isBonus = isBonus;
	}

	public boolean isUsed() {
		return isUsed;
	}

	public void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}
