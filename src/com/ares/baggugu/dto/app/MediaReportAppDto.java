package com.ares.baggugu.dto.app;

import java.io.Serializable;

public class MediaReportAppDto implements Serializable {

	private static final long serialVersionUID = 1326614937247413722L;

	private String logo;

	private String title;

	private String content;

	private String url;

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
