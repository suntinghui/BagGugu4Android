package com.ares.baggugu.dto.app;

import java.io.Serializable;

public class WelfareAppDto implements Serializable {

	private static final long serialVersionUID = 5425986985126111628L;
	/**
	 * 标识
	 */
	private int id;
	/**
	 * 加息或话费充值
	 */
	private String value;
	/**
	 * 剩余天数
	 */
	private int surplusDay;
	/**
	 * 本月总天数
	 */
	private int totalDay;
	/**
	 * 已完成多少
	 */
	private String completeTotal;
	/**
	 * 是否已完成
	 */
	private boolean isComplete;
	/**
	 * 充话费申请状态 a未申请 b已申请 e完成
	 */
	private char status;
	/**
	 * 1月加息2送话费
	 */
	private int type;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getSurplusDay() {
		return surplusDay;
	}

	public void setSurplusDay(int surplusDay) {
		this.surplusDay = surplusDay;
	}

	public String getCompleteTotal() {
		return completeTotal;
	}

	public void setCompleteTotal(String completeTotal) {
		this.completeTotal = completeTotal;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	public int getTotalDay() {
		return totalDay;
	}

	public void setTotalDay(int totalDay) {
		this.totalDay = totalDay;
	}

	public char getStatus() {
		return status;
	}

	public void setStatus(char status) {
		this.status = status;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
