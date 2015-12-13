package com.ares.baggugu.dto.app;

import java.io.Serializable;

/**
 * 立即购买详细
 * 
 * @author sunshuai
 * 
 */
public class DebtPackageInfo2AppDto implements Serializable {

	private static final long serialVersionUID = -2839040603488038929L;
	/**
	 * 用户账号余额
	 */
	private String money;

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

}
