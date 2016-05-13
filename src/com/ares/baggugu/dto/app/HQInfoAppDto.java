package com.ares.baggugu.dto.app;

import java.io.Serializable;

public class HQInfoAppDto implements Serializable {

	private static final long serialVersionUID = 3312186903548162174L;
	/**
	 * 年收益率
	 */
	private String rate;
	/**
	 * 正在反息人数
	 */
	private int userCount;
	/**
	 * 可售金额
	 */
	private String surplusMoney;
	/**
	 * 总金额
	 */
	private String totalMoney;
	/**
	 * 超过银行多少倍
	 */
	private int bank;
	/**
	 * 活期剩余或定期已完成
	 */
	@Deprecated
	private String surplusMoneyStr;
	/**
	 * 完成金额
	 */
	private String totalSoldMoneyStr;

	private String remark;

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public int getUserCount() {
		return userCount;
	}

	public void setUserCount(int userCount) {
		this.userCount = userCount;
	}

	public String getSurplusMoney() {
		return surplusMoney;
	}

	public void setSurplusMoney(String surplusMoney) {
		this.surplusMoney = surplusMoney;
	}

	public String getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(String totalMoney) {
		this.totalMoney = totalMoney;
	}

	public int getBank() {
		return bank;
	}

	public void setBank(int bank) {
		this.bank = bank;
	}

	public String getSurplusMoneyStr() {
		return surplusMoneyStr;
	}

	public void setSurplusMoneyStr(String surplusMoneyStr) {
		this.surplusMoneyStr = surplusMoneyStr;
	}

	public String getTotalSoldMoneyStr() {
		return totalSoldMoneyStr;
	}

	public void setTotalSoldMoneyStr(String totalSoldMoneyStr) {
		this.totalSoldMoneyStr = totalSoldMoneyStr;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
