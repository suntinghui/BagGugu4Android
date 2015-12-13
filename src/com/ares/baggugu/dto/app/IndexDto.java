package com.ares.baggugu.dto.app;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 首页统计数据
 * 
 * @author sunshuai
 * 
 */
public class IndexDto implements Serializable {
	private static final long serialVersionUID = -4095391046775570551L;
	/**
	 * 是否有可以购买的债券
	 */
	private boolean hasBuy;
	/**
	 * 投资总额
	 */
	private String total;

	// 只有外层map的key=top20时此对象有值
	/**
	 * 滚动数据 map key telphone=手机号 money=投资金额 type=类型 定投、抢投、收购 earnings=利率带百分比的
	 */
	private List<Map<String, String>> top20;

	public boolean isHasBuy() {
		return hasBuy;
	}

	public void setHasBuy(boolean hasBuy) {
		this.hasBuy = hasBuy;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public List<Map<String, String>> getTop20() {
		return top20;
	}

	public void setTop20(List<Map<String, String>> top20) {
		this.top20 = top20;
	}

}
