package com.ares.baggugu.dto.app;

public class ConfigAppDto {

	/**
	 * 客服电话
	 */
	private String telphone;
	/**
	 * 客服微信
	 */
	private String weixin;
	/**
	 * 公众号
	 */
	private String gongzhonghao;

	public String getTelphone() {
		return telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	public String getWeixin() {
		return weixin;
	}

	public void setWeixin(String weixin) {
		this.weixin = weixin;
	}

	public String getGongzhonghao() {
		return gongzhonghao;
	}

	public void setGongzhonghao(String gongzhonghao) {
		this.gongzhonghao = gongzhonghao;
	}

}
