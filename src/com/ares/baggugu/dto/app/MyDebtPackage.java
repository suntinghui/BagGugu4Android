package com.ares.baggugu.dto.app;

import java.io.Serializable;

/**
 * 我购买的债权
 * 
 * @author sunshuai
 * 
 */
public class MyDebtPackage implements Serializable {

	private static final long serialVersionUID = -6914025766044536725L;
	/**
	 * 标示
	 */
	private int id;
	/**
	 * 债权编号
	 */
	private String dpnum;
	/**
	 * 债权标识
	 */
	private int dpid;
	/**
	 * 债权类型 a定投 b抢投 c转让
	 */
	private char type;
	/**
	 * 债权本金
	 */
	private String totalPrincipal;
	/**
	 * 实际投资
	 */
	private String principal;
	/**
	 * 年收益率
	 */
	private String rate;
	/**
	 * 奖励收益
	 */
	private String rewardRate;
	/**
	 * 总收益率
	 */
	private String totalRate;
	/**
	 * 奖励
	 */
	private String reward;
	/**
	 * 还本时间
	 */
	private String endDate;
	/**
	 * 到期本息收益
	 */
	private String endTotalMoney;
	/**
	 * 是否允许转让
	 */
	private boolean isTransfer;
	/**
	 * 是否允许取消转让
	 */
	private boolean isCancelTransfer;
	/**
	 * 抢投排名结果
	 */
	private int orderby;
	/**
	 * 状态 a等待返息 b返息中 c完成 d已赎回 z已删除
	 */
	private char status;
	/**
	 * 转让状态 a正常 b转让中 c完成
	 */
	private char zrStatus;
	/**
	 * 抢排名时间 毫秒值 -1还没开始 0抢投中 -2抢投结束 其他开抢时间
	 */
	private long grabTime;
	/**
	 * 项目周期 抢投的如果在抢投结束之前有可能是0
	 */
	private int period;
	/**
	 * 进行进度百分比
	 */
	private int progress;
	/**
	 * 开始返息时间
	 */
	private String beginDate;
	/**
	 * 当前进行了多少天
	 */
	private int day;
	/**
	 * 剩余多少天
	 */
	private int surplusDay;
	/**
	 * 当前共收利息
	 */
	private String earnings;
	/**
	 * 支付状态 c支付确认中 d支付成功
	 */
	private char payStatus;
	/**
	 * 系统时间
	 */
	private long sysTime;

	/**
	 * 已经卖了多少份
	 */
	private int soldCount;
	/**
	 * 总共多少份
	 */
	private int totalCount;
	/**
	 * 购买时间
	 */
	private long buyTime;

	public String getDpnum() {
		return dpnum;
	}

	public void setDpnum(String dpnum) {
		this.dpnum = dpnum;
	}

	public char getType() {
		return type;
	}

	public void setType(char type) {
		this.type = type;
	}

	public String getTotalPrincipal() {
		return totalPrincipal;
	}

	public void setTotalPrincipal(String totalPrincipal) {
		this.totalPrincipal = totalPrincipal;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getReward() {
		return reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getEndTotalMoney() {
		return endTotalMoney;
	}

	public void setEndTotalMoney(String endTotalMoney) {
		this.endTotalMoney = endTotalMoney;
	}

	public boolean isTransfer() {
		return isTransfer;
	}

	public void setTransfer(boolean isTransfer) {
		this.isTransfer = isTransfer;
	}

	public int getOrderby() {
		return orderby;
	}

	public void setOrderby(int orderby) {
		this.orderby = orderby;
	}

	public char getStatus() {
		return status;
	}

	public void setStatus(char status) {
		this.status = status;
	}

	public char getZrStatus() {
		return zrStatus;
	}

	public void setZrStatus(char zrStatus) {
		this.zrStatus = zrStatus;
	}

	public long getGrabTime() {
		return grabTime;
	}

	public void setGrabTime(long grabTime) {
		this.grabTime = grabTime;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public String getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public String getEarnings() {
		return earnings;
	}

	public void setEarnings(String earnings) {
		this.earnings = earnings;
	}

	public boolean isCancelTransfer() {
		return isCancelTransfer;
	}

	public void setCancelTransfer(boolean isCancelTransfer) {
		this.isCancelTransfer = isCancelTransfer;
	}

	public int getSurplusDay() {
		return surplusDay;
	}

	public void setSurplusDay(int surplusDay) {
		this.surplusDay = surplusDay;
	}

	public int getDpid() {
		return dpid;
	}

	public void setDpid(int dpid) {
		this.dpid = dpid;
	}

	public String getRewardRate() {
		return rewardRate;
	}

	public void setRewardRate(String rewardRate) {
		this.rewardRate = rewardRate;
	}

	public String getTotalRate() {
		return totalRate;
	}

	public void setTotalRate(String totalRate) {
		this.totalRate = totalRate;
	}

	public char getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(char payStatus) {
		this.payStatus = payStatus;
	}

	public long getSysTime() {
		return sysTime;
	}

	public void setSysTime(long sysTime) {
		this.sysTime = sysTime;
	}

	public int getSoldCount() {
		return soldCount;
	}

	public void setSoldCount(int soldCount) {
		this.soldCount = soldCount;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public long getBuyTime() {
		return buyTime;
	}

	public void setBuyTime(long buyTime) {
		this.buyTime = buyTime;
	}
}
