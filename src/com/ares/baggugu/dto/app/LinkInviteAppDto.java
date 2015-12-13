package com.ares.baggugu.dto.app;

import java.io.Serializable;
import java.util.List;

public class LinkInviteAppDto implements Serializable {

	private static final long serialVersionUID = 3237640576863959248L;

	/**
	 * 共领取多少积分
	 */
	private int integral;

	/**
	 * 邀请统计列表
	 */
	private List<LinkInviteListAppDto> appDtos;

	public int getIntegral() {
		return integral;
	}

	public void setIntegral(int integral) {
		this.integral = integral;
	}

	public List<LinkInviteListAppDto> getAppDtos() {
		return appDtos;
	}

	public void setAppDtos(List<LinkInviteListAppDto> appDtos) {
		this.appDtos = appDtos;
	}

}
