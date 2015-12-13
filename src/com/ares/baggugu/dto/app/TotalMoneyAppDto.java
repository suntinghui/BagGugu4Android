package com.ares.baggugu.dto.app;

import java.io.Serializable;
import java.util.List;

/**
 * 总资产
 * 
 * @author sunshuai
 * 
 */
public class TotalMoneyAppDto implements Serializable {

	private static final long serialVersionUID = -2226932503624830228L;
	/**
	 * 总资产
	 */
	private String totalMoney;
	/**
	 * 收益排名
	 */
	private List<EarningsHistorySequenceAppDto> earningsHistorySequences;
	/**
	 * 余额
	 */
	private String surplus;
	/**
	 * 待收本金
	 */
	private String waitPrincipal;
	/**
	 * 待收收益
	 */
	private String waitEarnings;
	/**
	 * 收购奖励
	 */
	private String purchaseReward;
	/**
	 * 提现中
	 */
	private String withdraw;
	/**
	 * 红包
	 */
	private String bonus;
	/**
	 * 特权金
	 */
	private String registGiveMoney;
	/**
	 * 特权金还剩多少天
	 */
	private int registGiveMoneyDay;
	/**
	 * 活期本金
	 */
	private String hqMoney;
	/**
	 * 物业保障金
	 */
	private String wybMoney;

	public String getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(String totalMoney) {
		this.totalMoney = totalMoney;
	}

	public List<EarningsHistorySequenceAppDto> getEarningsHistorySequences() {
		return earningsHistorySequences;
	}

	public void setEarningsHistorySequences(
			List<EarningsHistorySequenceAppDto> earningsHistorySequences) {
		this.earningsHistorySequences = earningsHistorySequences;
	}

	public String getSurplus() {
		return surplus;
	}

	public void setSurplus(String surplus) {
		this.surplus = surplus;
	}

	public String getWaitPrincipal() {
		return waitPrincipal;
	}

	public void setWaitPrincipal(String waitPrincipal) {
		this.waitPrincipal = waitPrincipal;
	}

	public String getWaitEarnings() {
		return waitEarnings;
	}

	public void setWaitEarnings(String waitEarnings) {
		this.waitEarnings = waitEarnings;
	}

	public String getPurchaseReward() {
		return purchaseReward;
	}

	public void setPurchaseReward(String purchaseReward) {
		this.purchaseReward = purchaseReward;
	}

	public String getWithdraw() {
		return withdraw;
	}

	public void setWithdraw(String withdraw) {
		this.withdraw = withdraw;
	}

	public String getBonus() {
		return bonus;
	}

	public void setBonus(String bonus) {
		this.bonus = bonus;
	}

	public String getRegistGiveMoney() {
		return registGiveMoney;
	}

	public void setRegistGiveMoney(String registGiveMoney) {
		this.registGiveMoney = registGiveMoney;
	}

	public int getRegistGiveMoneyDay() {
		return registGiveMoneyDay;
	}

	public void setRegistGiveMoneyDay(int registGiveMoneyDay) {
		this.registGiveMoneyDay = registGiveMoneyDay;
	}

	public String getHqMoney() {
		return hqMoney;
	}

	public void setHqMoney(String hqMoney) {
		this.hqMoney = hqMoney;
	}

	public String getWybMoney() {
		return wybMoney;
	}

	public void setWybMoney(String wybMoney) {
		this.wybMoney = wybMoney;
	}

}
