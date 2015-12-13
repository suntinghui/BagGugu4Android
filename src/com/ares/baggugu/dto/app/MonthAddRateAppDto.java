package com.ares.baggugu.dto.app;

import java.io.Serializable;
import java.util.List;

public class MonthAddRateAppDto implements Serializable {

	private static final long serialVersionUID = 6406608661358143741L;

	/**
	 * 单个用户加息值
	 */
	private String singel;

	/**
	 * 总加息值
	 */
	private String total;

	/**
	 * 现在加息值
	 */
	private String now;

	/**
	 * 剩余多少天
	 */
	private int day;

	/**
	 * 几位用户帮我加息
	 */
	private int usedCount;

	/**
	 * 加息用户列表
	 */
	private List<MonthAddRateListAppDto> appDtos;

	public String getSingel() {
		return singel;
	}

	public void setSingel(String singel) {
		this.singel = singel;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getNow() {
		return now;
	}

	public void setNow(String now) {
		this.now = now;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getUsedCount() {
		return usedCount;
	}

	public void setUsedCount(int usedCount) {
		this.usedCount = usedCount;
	}

	public List<MonthAddRateListAppDto> getAppDtos() {
		return appDtos;
	}

	public void setAppDtos(List<MonthAddRateListAppDto> appDtos) {
		this.appDtos = appDtos;
	}

}
