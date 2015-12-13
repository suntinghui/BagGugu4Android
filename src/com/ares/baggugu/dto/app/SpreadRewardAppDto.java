package com.ares.baggugu.dto.app;

import java.io.Serializable;
import java.util.List;

public class SpreadRewardAppDto implements Serializable {

	private static final long serialVersionUID = 5943954437089689914L;

	/**
	 * 总资产
	 */
	private String totalMoney;

	/**
	 * 列表数据
	 */
	private List<SpreadRewardListAppDto> appDtos;

	public String getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(String totalMoney) {
		this.totalMoney = totalMoney;
	}

	public List<SpreadRewardListAppDto> getAppDtos() {
		return appDtos;
	}

	public void setAppDtos(List<SpreadRewardListAppDto> appDtos) {
		this.appDtos = appDtos;
	}

}
