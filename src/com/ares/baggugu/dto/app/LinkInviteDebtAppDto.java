package com.ares.baggugu.dto.app;

import java.io.Serializable;
import java.util.List;

public class LinkInviteDebtAppDto implements Serializable {

	private static final long serialVersionUID = -6465736975506346329L;

	/**
	 * 邀请的所有用户总资产
	 */
	private String totalMoney;

	/**
	 * 累计获得收益
	 */
	private String receiveEarnings;

	/**
	 * 收益率
	 */
	private String rate;

	/**
	 * 列表数据
	 */
	private List<LinkInviteDebtListAppDto> appDtos;

	public String getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(String totalMoney) {
		this.totalMoney = totalMoney;
	}

	public String getReceiveEarnings() {
		return receiveEarnings;
	}

	public void setReceiveEarnings(String receiveEarnings) {
		this.receiveEarnings = receiveEarnings;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public List<LinkInviteDebtListAppDto> getAppDtos() {
		return appDtos;
	}

	public void setAppDtos(List<LinkInviteDebtListAppDto> appDtos) {
		this.appDtos = appDtos;
	}

}
