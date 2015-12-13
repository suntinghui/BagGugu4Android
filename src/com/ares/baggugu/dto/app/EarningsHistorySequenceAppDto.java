package com.ares.baggugu.dto.app;

import java.io.Serializable;

/**
 * 总资产收益排名
 * 
 * @author sunshuai
 * 
 */
public class EarningsHistorySequenceAppDto implements Serializable {

	private static final long serialVersionUID = -8901629731821386786L;
	/**
	 * 日期字符串
	 */
	private String dateStr;
	/**
	 * 年化收益
	 */
	private String rate;

	public String getDateStr() {
		return dateStr;
	}

	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

}
