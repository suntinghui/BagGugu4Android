package com.ares.baggugu.dto.app;

import java.io.Serializable;

/**
 * 天天收益
 * 
 * @author sunshuai
 * 
 */
public class DayEarningsInfoAppDto implements Serializable {

	private static final long serialVersionUID = -979541855129540264L;
	/**
	 * 昨日收益
	 */
	private String earnings;
	/**
	 * 是否显示收益
	 */
	private boolean isShow;
	/**
	 * 投资中本金
	 */
	private String usedPrincipal;
	/**
	 * 活期投资中本金
	 */
	private String hqPrincipal;
	/**
	 * 定投投资中本金
	 */
	private String dtPrincipal;
	/**
	 * 平均万份收益
	 */
	private String tenthousandEarnings;
	/**
	 * 累计收益
	 */
	private String totalEarnings;
	/**
	 * 活期累计收益
	 */
	private String hqTotalEarnings;
	/**
	 * 定投累计收益
	 */
	private String dtTotalEarnings;
	/**
	 * 平均年化收益率
	 */
	private String rate;
	/**
	 * 投资项目
	 */
	private int count;
	/**
	 * 抢投最高收益
	 */
	private String qtHighest;
	/**
	 * 余额宝收益
	 */
	private String yuebaoEarnings;
	/**
	 * 银行定期
	 */
	private String bankEarnings;
	/**
	 * 活期本金比例
	 */
	private int hqPrincipalProportion;
	/**
	 * 定期本金比例
	 */
	private int dtPrincipalProportion;
	/**
	 * 活期收益比例
	 */
	private int hqEarningsProportion;
	/**
	 * 定投收益比例
	 */
	private int dtEarningsProportion;
	/**
	 * 物业宝昨日收益
	 */
	private String wybEarnings;
	/**
	 * 活期昨日收益
	 */
	private String hqEarnings;
	/**
	 * 定期昨日收益
	 */
	private String dqEarnings;

	public String getEarnings() {
		return earnings;
	}

	public void setEarnings(String earnings) {
		this.earnings = earnings;
	}

	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

	public String getUsedPrincipal() {
		return usedPrincipal;
	}

	public void setUsedPrincipal(String usedPrincipal) {
		this.usedPrincipal = usedPrincipal;
	}

	public String getTenthousandEarnings() {
		return tenthousandEarnings;
	}

	public void setTenthousandEarnings(String tenthousandEarnings) {
		this.tenthousandEarnings = tenthousandEarnings;
	}

	public String getTotalEarnings() {
		return totalEarnings;
	}

	public void setTotalEarnings(String totalEarnings) {
		this.totalEarnings = totalEarnings;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getQtHighest() {
		return qtHighest;
	}

	public void setQtHighest(String qtHighest) {
		this.qtHighest = qtHighest;
	}

	public String getYuebaoEarnings() {
		return yuebaoEarnings;
	}

	public void setYuebaoEarnings(String yuebaoEarnings) {
		this.yuebaoEarnings = yuebaoEarnings;
	}

	public String getBankEarnings() {
		return bankEarnings;
	}

	public void setBankEarnings(String bankEarnings) {
		this.bankEarnings = bankEarnings;
	}

	public String getHqPrincipal() {
		return hqPrincipal;
	}

	public void setHqPrincipal(String hqPrincipal) {
		this.hqPrincipal = hqPrincipal;
	}

	public String getDtPrincipal() {
		return dtPrincipal;
	}

	public void setDtPrincipal(String dtPrincipal) {
		this.dtPrincipal = dtPrincipal;
	}

	public String getHqTotalEarnings() {
		return hqTotalEarnings;
	}

	public void setHqTotalEarnings(String hqTotalEarnings) {
		this.hqTotalEarnings = hqTotalEarnings;
	}

	public String getDtTotalEarnings() {
		return dtTotalEarnings;
	}

	public void setDtTotalEarnings(String dtTotalEarnings) {
		this.dtTotalEarnings = dtTotalEarnings;
	}

	public int getHqPrincipalProportion() {
		return hqPrincipalProportion;
	}

	public void setHqPrincipalProportion(int hqPrincipalProportion) {
		this.hqPrincipalProportion = hqPrincipalProportion;
	}

	public int getDtPrincipalProportion() {
		return dtPrincipalProportion;
	}

	public void setDtPrincipalProportion(int dtPrincipalProportion) {
		this.dtPrincipalProportion = dtPrincipalProportion;
	}

	public int getHqEarningsProportion() {
		return hqEarningsProportion;
	}

	public void setHqEarningsProportion(int hqEarningsProportion) {
		this.hqEarningsProportion = hqEarningsProportion;
	}

	public int getDtEarningsProportion() {
		return dtEarningsProportion;
	}

	public void setDtEarningsProportion(int dtEarningsProportion) {
		this.dtEarningsProportion = dtEarningsProportion;
	}

	public String getWybEarnings() {
		return wybEarnings;
	}

	public void setWybEarnings(String wybEarnings) {
		this.wybEarnings = wybEarnings;
	}

	public String getHqEarnings() {
		return hqEarnings;
	}

	public void setHqEarnings(String hqEarnings) {
		this.hqEarnings = hqEarnings;
	}

	public String getDqEarnings() {
		return dqEarnings;
	}

	public void setDqEarnings(String dqEarnings) {
		this.dqEarnings = dqEarnings;
	}
}
