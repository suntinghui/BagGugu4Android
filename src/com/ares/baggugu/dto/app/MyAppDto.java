package com.ares.baggugu.dto.app;

import java.io.Serializable;
import java.util.List;

/**
 * 我的页面数据
 * 
 * @author sunshuai
 * 
 */
public class MyAppDto implements Serializable {

	private static final long serialVersionUID = 6814924365151036162L;
	/**
	 * 用户头像
	 */
	private String logoUrl;
	/**
	 * 加密后的手机号
	 */
	private String encryptTelphone;
	/**
	 * 总资产
	 */
	private String totalMoney;
	/**
	 * 剩余可用/可提现金额
	 */
	private String surplusMoney;
	/**
	 * 累计收益
	 */
	private String totalEarnings;
	/**
	 * 三个点 0灰1红
	 */
	private List<Integer> point;

	/**
	 * 待收本金
	 */
	private String waitPrincipal;
	/**
	 * 待收收益
	 */
	private String waitEarnings;
	/**
	 * 特权金
	 */
	private String registGiveMoney;
	/**
	 * 特权金还剩多少天
	 */
	private int registGiveMoneyDay;
	/**
	 * 好友获利
	 */
	private String receiveEarnings;
	/**
	 * 顶部图片
	 */
	private String topImg;

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getEncryptTelphone() {
		return encryptTelphone;
	}

	public void setEncryptTelphone(String encryptTelphone) {
		this.encryptTelphone = encryptTelphone;
	}

	public String getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(String totalMoney) {
		this.totalMoney = totalMoney;
	}

	public String getSurplusMoney() {
		return surplusMoney;
	}

	public void setSurplusMoney(String surplusMoney) {
		this.surplusMoney = surplusMoney;
	}

	public String getTotalEarnings() {
		return totalEarnings;
	}

	public void setTotalEarnings(String totalEarnings) {
		this.totalEarnings = totalEarnings;
	}

	public List<Integer> getPoint() {
		return point;
	}

	public void setPoint(List<Integer> point) {
		this.point = point;
	}

	public String getWaitPrincipal() {
		return waitPrincipal;
	}

	public void setWaitPrincipal(String waitPrincipal) {
		this.waitPrincipal = waitPrincipal;
	}

	public String getWaitEarnings() {
		return waitEarnings;
	}

	public void setWaitEarnings(String waitEarnings) {
		this.waitEarnings = waitEarnings;
	}

	public String getRegistGiveMoney() {
		return registGiveMoney;
	}

	public void setRegistGiveMoney(String registGiveMoney) {
		this.registGiveMoney = registGiveMoney;
	}

	public int getRegistGiveMoneyDay() {
		return registGiveMoneyDay;
	}

	public void setRegistGiveMoneyDay(int registGiveMoneyDay) {
		this.registGiveMoneyDay = registGiveMoneyDay;
	}

	public String getReceiveEarnings() {
		return receiveEarnings;
	}

	public void setReceiveEarnings(String receiveEarnings) {
		this.receiveEarnings = receiveEarnings;
	}

	public String getTopImg() {
		return topImg;
	}

	public void setTopImg(String topImg) {
		this.topImg = topImg;
	}
}
