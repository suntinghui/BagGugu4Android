package com.ares.baggugu.dto.app;

import java.io.Serializable;

public class RansomDebtAppDto implements Serializable {

	private static final long serialVersionUID = 3156476963347636124L;

	/**
	 * 标识
	 */
	private int id;
	/**
	 * 开始时间
	 */
	private String beginTime;
	/**
	 * 进行了多少天
	 */
	private int day;
	/**
	 * 总天数
	 */
	private int totalDay;
	/**
	 * 转让扣除收益
	 */
	private String transferDeductEarnings;
	/**
	 * 当前共收利息
	 */
	private String earnings;
	/**
	 * 进行进度百分比
	 */
	private int progress;
	/**
	 * 扣除进度百分比
	 */
	private int deductProgress;
	/**
	 * 定投扣除多少天30 抢投扣除百分比75
	 */
	private int deduct;
	/**
	 * 赎回按指定受益率计算
	 */
	private int ransomRate;
	/**
	 * 转让/赎回状态 a正常 b转让中 c完成
	 */
	private char zrStatus;
	/**
	 * 是否允许取消转让
	 */
	private boolean isCancelTransfer;
	/**
	 * 债权本金
	 */
	private String principal;
	/**
	 * 债权编号
	 */
	private String dpnum;
	/**
	 * 实际到账金额
	 */
	private String actual;
	/**
	 * 实际收益
	 */
	private String actualEarnings;
	/**
	 * 是否允许转让/赎回
	 */
	private boolean isTransfer;
	/**
	 * 赎回提示
	 */
	private String ransomHint1;
	/**
	 * 赎回弹出框提示
	 */
	private String ransomHint2;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public String getTransferDeductEarnings() {
		return transferDeductEarnings;
	}

	public void setTransferDeductEarnings(String transferDeductEarnings) {
		this.transferDeductEarnings = transferDeductEarnings;
	}

	public String getEarnings() {
		return earnings;
	}

	public void setEarnings(String earnings) {
		this.earnings = earnings;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public int getDeductProgress() {
		return deductProgress;
	}

	public void setDeductProgress(int deductProgress) {
		this.deductProgress = deductProgress;
	}

	public char getZrStatus() {
		return zrStatus;
	}

	public void setZrStatus(char zrStatus) {
		this.zrStatus = zrStatus;
	}

	public boolean isCancelTransfer() {
		return isCancelTransfer;
	}

	public void setCancelTransfer(boolean isCancelTransfer) {
		this.isCancelTransfer = isCancelTransfer;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public String getDpnum() {
		return dpnum;
	}

	public void setDpnum(String dpnum) {
		this.dpnum = dpnum;
	}

	public int getDeduct() {
		return deduct;
	}

	public void setDeduct(int deduct) {
		this.deduct = deduct;
	}

	public String getActual() {
		return actual;
	}

	public void setActual(String actual) {
		this.actual = actual;
	}

	public String getActualEarnings() {
		return actualEarnings;
	}

	public void setActualEarnings(String actualEarnings) {
		this.actualEarnings = actualEarnings;
	}

	public int getTotalDay() {
		return totalDay;
	}

	public void setTotalDay(int totalDay) {
		this.totalDay = totalDay;
	}

	public boolean isTransfer() {
		return isTransfer;
	}

	public void setTransfer(boolean isTransfer) {
		this.isTransfer = isTransfer;
	}

	public int getRansomRate() {
		return ransomRate;
	}

	public void setRansomRate(int ransomRate) {
		this.ransomRate = ransomRate;
	}

	public String getRansomHint1() {
		return ransomHint1;
	}

	public void setRansomHint1(String ransomHint1) {
		this.ransomHint1 = ransomHint1;
	}

	public String getRansomHint2() {
		return ransomHint2;
	}

	public void setRansomHint2(String ransomHint2) {
		this.ransomHint2 = ransomHint2;
	}

}
