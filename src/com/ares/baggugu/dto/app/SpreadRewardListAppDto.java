package com.ares.baggugu.dto.app;

import java.io.Serializable;

public class SpreadRewardListAppDto implements Serializable {

	private static final long serialVersionUID = 8342205402840809377L;
	private int id;
	/**
	 * 达到要求金额
	 */
	private String requireMoney;
	/**
	 * 奖励金额
	 */
	private String money;
	/**
	 * 状态 a未达到要求 b可领取 d申请中 e已领取
	 */
	private char status;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRequireMoney() {
		return requireMoney;
	}

	public void setRequireMoney(String requireMoney) {
		this.requireMoney = requireMoney;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public char getStatus() {
		return status;
	}

	public void setStatus(char status) {
		this.status = status;
	}

}
