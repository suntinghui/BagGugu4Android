package com.ares.baggugu.dto.app;

/**
 * 物业保证金
 * 
 * @author sunshuai
 *
 */
public class PropertyGuaranteeMoneyAppDto {

	/**
	 * 保证金金额
	 */
	private String money;
	/**
	 * 是否申请退还 true申请中
	 */
	private boolean submit;
	/**
	 * 申请时间
	 */
	private String timeStr;

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public boolean isSubmit() {
		return submit;
	}

	public void setSubmit(boolean submit) {
		this.submit = submit;
	}

	public String getTimeStr() {
		return timeStr;
	}

	public void setTimeStr(String timeStr) {
		this.timeStr = timeStr;
	}

}
