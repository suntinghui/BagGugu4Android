package com.ares.baggugu.dto.app;

import java.io.Serializable;
import java.util.List;

public class LotteryAppDto implements Serializable {

	private static final long serialVersionUID = -7194060458682681472L;

	/**
	 * 剩余积分
	 */
	private int integral;

	/**
	 * 朋友加息
	 */
	private String rate;

	/**
	 * 转盘每次积分
	 */
	private int singelIntegral;

	/**
	 * 中奖结果
	 */
	private List<String> result;

	public int getIntegral() {
		return integral;
	}

	public void setIntegral(int integral) {
		this.integral = integral;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public int getSingelIntegral() {
		return singelIntegral;
	}

	public void setSingelIntegral(int singelIntegral) {
		this.singelIntegral = singelIntegral;
	}

	public List<String> getResult() {
		return result;
	}

	public void setResult(List<String> result) {
		this.result = result;
	}

}
