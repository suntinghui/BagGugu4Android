package com.ares.baggugu.dto.app;

import java.io.Serializable;

public class MyWelfareAppDto implements Serializable {

	private static final long serialVersionUID = -7461491099732215805L;
	/**
	 * 总资产
	 */
	private String totalMoney;
	/**
	 * false 即将领取 true可领取
	 */
	private boolean receiveMoney;
	/**
	 * 领取金额 如果是null空的话说明已经全部领完了
	 */
	private String receiveMoneyValue;
	/**
	 * 积分
	 */
	private int integral;
	/**
	 * 加息剩余天数
	 */
	private int day;
	/**
	 * 加息
	 */
	private String rate;
	/**
	 * 话费
	 */
	private int telphoneMoney;
	/**
	 * 抢投红包
	 */
	private String qtBonus;
	/**
	 * 好友红包
	 */
	private String bonus;
	/**
	 * 未领取红包个数
	 */
	private int bonusWaitCount;

	public String getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(String totalMoney) {
		this.totalMoney = totalMoney;
	}

	public boolean isReceiveMoney() {
		return receiveMoney;
	}

	public void setReceiveMoney(boolean receiveMoney) {
		this.receiveMoney = receiveMoney;
	}

	public int getIntegral() {
		return integral;
	}

	public void setIntegral(int integral) {
		this.integral = integral;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public int getTelphoneMoney() {
		return telphoneMoney;
	}

	public void setTelphoneMoney(int telphoneMoney) {
		this.telphoneMoney = telphoneMoney;
	}

	public String getQtBonus() {
		return qtBonus;
	}

	public void setQtBonus(String qtBonus) {
		this.qtBonus = qtBonus;
	}

	public String getBonus() {
		return bonus;
	}

	public void setBonus(String bonus) {
		this.bonus = bonus;
	}

	public String getReceiveMoneyValue() {
		return receiveMoneyValue;
	}

	public void setReceiveMoneyValue(String receiveMoneyValue) {
		this.receiveMoneyValue = receiveMoneyValue;
	}

	public int getBonusWaitCount() {
		return bonusWaitCount;
	}

	public void setBonusWaitCount(int bonusWaitCount) {
		this.bonusWaitCount = bonusWaitCount;
	}

}
