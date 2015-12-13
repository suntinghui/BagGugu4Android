package com.ares.baggugu.dto.app;

import java.io.Serializable;

/**
 * 薪资宝详细
 * 
 * @author sunshuai
 *
 */
public class WageInfoAppDto implements Serializable {

	private static final long serialVersionUID = 2258223014215920214L;
	/**
	 * 公司LOGO
	 */
	private String companyLogo;
	/**
	 * 公司名称
	 */
	private String companyName;
	/**
	 * 收益率
	 */
	private String rate;
	/**
	 * 用户手机号
	 */
	private String telphone;
	/**
	 * 共计到账薪资
	 */
	private String money;
	/**
	 * 用户头像
	 */
	private String userLogo;
	/**
	 * 用户名称
	 */
	private String realName;

	public String getCompanyLogo() {
		return companyLogo;
	}

	public void setCompanyLogo(String companyLogo) {
		this.companyLogo = companyLogo;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getTelphone() {
		return telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getUserLogo() {
		return userLogo;
	}

	public void setUserLogo(String userLogo) {
		this.userLogo = userLogo;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

}
