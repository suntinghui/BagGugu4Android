package com.ares.baggugu.dto.app;

import java.util.List;
import java.util.Map;

/**
 * 抢排名
 * 
 * @author sunshuai
 * 
 */
public class GrabOrderByAppDto extends MyDebtPackage {

	private static final long serialVersionUID = 854855998351572858L;
	/**
	 * 排名结果 key=telphone手机号 key=rate年化利率
	 */
	private List<Map<String, String>> orderbys;

	public List<Map<String, String>> getOrderbys() {
		return orderbys;
	}

	public void setOrderbys(List<Map<String, String>> orderbys) {
		this.orderbys = orderbys;
	}

}
