package com.ares.baggugu.dto.app;

import java.io.Serializable;

/**
 * 物业代扣费用
 * 
 * @author sunshuai
 *
 */
public class PropertyDeductionAppDto implements Serializable {

	private static final long serialVersionUID = -1011432419438310902L;

	/**
	 * 图片
	 */
	private String img;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 金额
	 */
	private String money;
	/**
	 * 时间
	 */
	private String timeStr;
	/**
	 * 公司名称
	 */
	private String companyName;

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getTimeStr() {
		return timeStr;
	}

	public void setTimeStr(String timeStr) {
		this.timeStr = timeStr;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

}
