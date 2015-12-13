package com.gugu.utils;

public class LotteryModel {

	private String id;
	private int index;
	private String desc;

	public LotteryModel(String id, int index, String desc) {
		super();
		this.id = id;
		this.index = index;
		this.desc = desc;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
