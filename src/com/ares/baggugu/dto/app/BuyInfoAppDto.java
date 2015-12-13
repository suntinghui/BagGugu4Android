package com.ares.baggugu.dto.app;

import java.io.Serializable;

/**
 * 投资记录
 * 
 * @author sunshuai
 * 
 */
public class BuyInfoAppDto implements Serializable {

	private static final long serialVersionUID = 2623669398343010373L;
	/**
	 * 手机号
	 */
	private String telphone;
	/**
	 * 时间
	 */
	private String time;
	/**
	 * 投资金额
	 */
	private String money;
	/**
	 * 年化利率
	 */
	private String rate;
	/**
	 * 购买用户标示
	 */
	private int userId;
	/**
	 * 是否已经是好友
	 */
	private boolean isFriend;
	/**
	 * 奖励收益
	 */
	private String rewardRate;

	public String getTelphone() {
		return telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public boolean isFriend() {
		return isFriend;
	}

	public void setFriend(boolean isFriend) {
		this.isFriend = isFriend;
	}

	public String getRewardRate() {
		return rewardRate;
	}

	public void setRewardRate(String rewardRate) {
		this.rewardRate = rewardRate;
	}

}
