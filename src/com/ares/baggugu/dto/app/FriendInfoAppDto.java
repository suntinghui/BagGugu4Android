package com.ares.baggugu.dto.app;

import java.io.Serializable;

/**
 * 好友信息
 * 
 * @author sunshuai
 * 
 */
public class FriendInfoAppDto implements Serializable {

	private static final long serialVersionUID = -2831536691785459317L;
	/**
	 * 用户标示
	 */
	private int userId;
	/**
	 * 用户头像
	 */
	private String logo;
	/**
	 * 用户真实姓名
	 */
	private String realName;
	/**
	 * 是否是本网站用户 如果不是那头像就不用拼服务器路径了
	 */
	private boolean isSite;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public boolean isSite() {
		return isSite;
	}

	public void setSite(boolean isSite) {
		this.isSite = isSite;
	}

}
