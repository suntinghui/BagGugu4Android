package com.ares.baggugu.dto.app;

/**
 * 工资记录
 * 
 * @author sunshuai
 *
 */
public class WageListAppDto {

	/**
	 * 工资
	 */
	private String money;
	/**
	 * 公司名称
	 */
	private String companyName;
	/**
	 * 到账时间
	 */
	private String timeStr;

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getTimeStr() {
		return timeStr;
	}

	public void setTimeStr(String timeStr) {
		this.timeStr = timeStr;
	}

}
