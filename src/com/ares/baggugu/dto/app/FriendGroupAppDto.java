package com.ares.baggugu.dto.app;

import java.io.Serializable;
import java.util.List;

/**
 * 好友分组
 * 
 * @author sunshuai
 * 
 */
public class FriendGroupAppDto implements Serializable {

	private static final long serialVersionUID = 1589770537564167500L;
	/**
	 * 分组标示
	 */
	private int id;
	/**
	 * 分组名称
	 */
	private String name;
	/**
	 * 分组图片
	 */
	private String img;
	/**
	 * 好友
	 */
	private List<FriendAppDto> friends;
	/**
	 * 是否是分期好友
	 */
	private boolean isFQ;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<FriendAppDto> getFriends() {
		return friends;
	}

	public void setFriends(List<FriendAppDto> friends) {
		this.friends = friends;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public boolean isFQ() {
		return isFQ;
	}

	public void setFQ(boolean isFQ) {
		this.isFQ = isFQ;
	}

}
