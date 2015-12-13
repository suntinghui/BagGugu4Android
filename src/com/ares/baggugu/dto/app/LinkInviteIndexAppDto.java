package com.ares.baggugu.dto.app;

import java.io.Serializable;
import java.util.List;

public class LinkInviteIndexAppDto implements Serializable {

	private static final long serialVersionUID = -7380748999504419237L;

	/**
	 * 共邀请人数
	 */
	private int totalInvite;

	/**
	 * 邀请的注册人数
	 */
	private int regist;

	/**
	 * 所有邀请的用户资产总额
	 */
	private String totalMoney;

	/**
	 * 代收获利
	 */
	private String waitEarnings;

	/**
	 * 已获利
	 */
	private String receiveEarnings;
	/**
	 * 顶部图片
	 */
	private List<ImageAppDto> topImgs;
	/**
	 * 反佣收益
	 */
	private String rate;

	public int getTotalInvite() {
		return totalInvite;
	}

	public void setTotalInvite(int totalInvite) {
		this.totalInvite = totalInvite;
	}

	public int getRegist() {
		return regist;
	}

	public void setRegist(int regist) {
		this.regist = regist;
	}

	public String getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(String totalMoney) {
		this.totalMoney = totalMoney;
	}

	public String getWaitEarnings() {
		return waitEarnings;
	}

	public void setWaitEarnings(String waitEarnings) {
		this.waitEarnings = waitEarnings;
	}

	public String getReceiveEarnings() {
		return receiveEarnings;
	}

	public void setReceiveEarnings(String receiveEarnings) {
		this.receiveEarnings = receiveEarnings;
	}

	public List<ImageAppDto> getTopImgs() {
		return topImgs;
	}

	public void setTopImgs(List<ImageAppDto> topImgs) {
		this.topImgs = topImgs;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

}
