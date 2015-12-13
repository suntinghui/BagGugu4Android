package com.ares.baggugu.dto.app;

import java.io.Serializable;

public class DiscoveryAppDto implements Serializable {

	private static final long serialVersionUID = -8349803343291427897L;
	/**
	 * 公司logo
	 */
	private String logo;
	/**
	 * 收益率
	 */
	private String rate;
	/**
	 * 公司名称
	 */
	private String name;

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
