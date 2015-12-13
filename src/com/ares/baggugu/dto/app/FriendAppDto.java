package com.ares.baggugu.dto.app;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 好友列表
 * 
 * @author sunshuai
 * 
 */
public class FriendAppDto implements Serializable {

	private static final long serialVersionUID = -4632215377375306913L;
	/**
	 * 标示
	 */
	private int id;
	/**
	 * 好友标示
	 */
	private int userId;
	/**
	 * 好友头像
	 */
	private String userImg;
	/**
	 * 好友名称
	 */
	private String userName;
	/**
	 * 状态 a申请中 b对方拒绝 c已是好友
	 */
	private char status;
	/**
	 * 是否是网站用户
	 */
	private boolean isSiteUser;
	/**
	 * 是否是我发送的
	 */
	private boolean isMySend;
	/**
	 * 发送时间
	 */
	private String time;
	/**
	 * 子账户Id。由32个英文字母和阿拉伯数字组成的子账户唯一标识符
	 */
	private String voipAccount;
	/**
	 * 好友手机号
	 */
	private String telphone;
	/**
	 * 分组名称
	 */
	@JsonIgnore
	private String groupName;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserImg() {
		return userImg;
	}

	public void setUserImg(String userImg) {
		this.userImg = userImg;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public char getStatus() {
		return status;
	}

	public void setStatus(char status) {
		this.status = status;
	}

	public boolean isSiteUser() {
		return isSiteUser;
	}

	public void setSiteUser(boolean isSiteUser) {
		this.isSiteUser = isSiteUser;
	}

	public boolean isMySend() {
		return isMySend;
	}

	public void setMySend(boolean isMySend) {
		this.isMySend = isMySend;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getVoipAccount() {
		return voipAccount;
	}

	public void setVoipAccount(String voipAccount) {
		this.voipAccount = voipAccount;
	}

	public String getTelphone() {
		return telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

}
