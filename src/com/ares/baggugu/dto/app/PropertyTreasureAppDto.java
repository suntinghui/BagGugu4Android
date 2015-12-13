package com.ares.baggugu.dto.app;

import java.io.Serializable;

/**
 * 物业宝详情
 * 
 * @author sunshuai
 *
 */
public class PropertyTreasureAppDto implements Serializable {

	private static final long serialVersionUID = 3719378079488800704L;

	/**
	 * 业主地址
	 */
	private String address;
	/**
	 * 保障金总金额
	 */
	private String totalMoney;
	/**
	 * 收益率
	 */
	private String rate;
	/**
	 * 累计收益
	 */
	private String earnings;
	/**
	 * 已支付保障金
	 */
	private String paidMoney;
	/**
	 * 还需支付保障金
	 */
	private String payMoney;
	/**
	 * 账号可用余额
	 */
	private String surplusMoney;
	/**
	 * 银行卡支付金额
	 */
	private String bankPayMoney;
	/**
	 * 债券标示
	 */
	private int debtId;
	/**
	 * 收益到账提示
	 */
	private String earningDayStr;
	/**
	 * 公司名字
	 */
	private String companyName;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(String totalMoney) {
		this.totalMoney = totalMoney;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getEarnings() {
		return earnings;
	}

	public void setEarnings(String earnings) {
		this.earnings = earnings;
	}

	public String getPaidMoney() {
		return paidMoney;
	}

	public void setPaidMoney(String paidMoney) {
		this.paidMoney = paidMoney;
	}

	public String getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(String payMoney) {
		this.payMoney = payMoney;
	}

	public String getSurplusMoney() {
		return surplusMoney;
	}

	public void setSurplusMoney(String surplusMoney) {
		this.surplusMoney = surplusMoney;
	}

	public String getBankPayMoney() {
		return bankPayMoney;
	}

	public void setBankPayMoney(String bankPayMoney) {
		this.bankPayMoney = bankPayMoney;
	}

	public int getDebtId() {
		return debtId;
	}

	public void setDebtId(int debtId) {
		this.debtId = debtId;
	}

	public String getEarningDayStr() {
		return earningDayStr;
	}

	public void setEarningDayStr(String earningDayStr) {
		this.earningDayStr = earningDayStr;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

}
