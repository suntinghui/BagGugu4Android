package com.ares.baggugu.dto.app;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 我要提现
 * 
 * @author sunshuai
 * 
 */
public class WithdrawalInfoAppDto implements Serializable {

	private static final long serialVersionUID = -3392531786500222586L;
	/**
	 * 滚动消息
	 */
	private List<String> messages;
	/**
	 * 可提现余额
	 */
	private String surplusMoney;
	/**
	 * 绑定的银行卡信息
	 */
	private Map<String, String> bankInfo;
	/**
	 * 定投本金
	 */
	private String fixedDebtMoney;
	/**
	 * 定投放弃收益
	 */
	private String fixedEarnings;
	/**
	 * 抢投本金
	 */
	private String grabDebtMoney;
	/**
	 * 抢投放弃收益
	 */
	private String grabEarnings;
	/**
	 * 活期金额
	 */
	private String hqMoney;

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	public String getSurplusMoney() {
		return surplusMoney;
	}

	public void setSurplusMoney(String surplusMoney) {
		this.surplusMoney = surplusMoney;
	}

	public Map<String, String> getBankInfo() {
		return bankInfo;
	}

	public void setBankInfo(Map<String, String> bankInfo) {
		this.bankInfo = bankInfo;
	}

	public String getFixedDebtMoney() {
		return fixedDebtMoney;
	}

	public void setFixedDebtMoney(String fixedDebtMoney) {
		this.fixedDebtMoney = fixedDebtMoney;
	}

	public String getFixedEarnings() {
		return fixedEarnings;
	}

	public void setFixedEarnings(String fixedEarnings) {
		this.fixedEarnings = fixedEarnings;
	}

	public String getGrabDebtMoney() {
		return grabDebtMoney;
	}

	public void setGrabDebtMoney(String grabDebtMoney) {
		this.grabDebtMoney = grabDebtMoney;
	}

	public String getGrabEarnings() {
		return grabEarnings;
	}

	public void setGrabEarnings(String grabEarnings) {
		this.grabEarnings = grabEarnings;
	}

	public String getHqMoney() {
		return hqMoney;
	}

	public void setHqMoney(String hqMoney) {
		this.hqMoney = hqMoney;
	}

}
