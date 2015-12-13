package com.ares.baggugu.dto.app;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class WelfareInfoAppDto implements Serializable {

	private static final long serialVersionUID = -1769829924404795345L;
	/**
	 * 标识
	 */
	private int id;
	/**
	 * 福利总数
	 */
	private String value;
	/**
	 * 单个福利数
	 */
	private String singleValue;
	/**
	 * 总任务数
	 */
	private int totalCount;
	/**
	 * 充值手机号
	 */
	private String telphone;
	/**
	 * 本月剩余多少天
	 */
	private int surplusDay;
	/**
	 * 已完成总数
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
	 * 已添加好友 或者是已投资个数
	 */
	private int addCount;
	/**
	 * 好友注册个数
	 */
	private int registCount;
	/**
	 * 列表数据
	 */
	private List<Map<String, String>> data;

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

	public String getSingleValue() {
		return singleValue;
	}

	public void setSingleValue(String singleValue) {
		this.singleValue = singleValue;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public String getTelphone() {
		return telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
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

	public int getAddCount() {
		return addCount;
	}

	public void setAddCount(int addCount) {
		this.addCount = addCount;
	}

	public int getRegistCount() {
		return registCount;
	}

	public void setRegistCount(int registCount) {
		this.registCount = registCount;
	}

	public List<Map<String, String>> getData() {
		return data;
	}

	public void setData(List<Map<String, String>> data) {
		this.data = data;
	}

	public char getStatus() {
		return status;
	}

	public void setStatus(char status) {
		this.status = status;
	}

}
