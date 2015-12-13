package com.ares.baggugu.dto.app;

import java.io.Serializable;

public class ImageAppDto implements Serializable {

	private static final long serialVersionUID = -6402610132835576503L;

	private String name;

	private String imgUrl;

	private String linkUrl;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

}
