package com.ares.baggugu.dto.app;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class IndexAppDto implements Serializable {

	private static final long serialVersionUID = 1082330328080284389L;
	/**
	 * 活期
	 */
	private HQInfoAppDto hq;
	/**
	 * 定期
	 */
	private HQInfoAppDto dq;
	/**
	 * 20条滚动
	 */
	private List<Map<String, String>> top20;
	/**
	 * 通知消息
	 */
	private List<String> message;
	/**
	 * 攻略图片
	 */
	private ImageAppDto strategyImg;
	/**
	 * 安全保障图片
	 */
	private ImageAppDto safetyImg;
	/**
	 * 顶部图片
	 */
	private List<ImageAppDto> topImgs;
	/**
	 * 客服电话
	 */
	private String serviceTelphone;

	public HQInfoAppDto getHq() {
		return hq;
	}

	public void setHq(HQInfoAppDto hq) {
		this.hq = hq;
	}

	public HQInfoAppDto getDq() {
		return dq;
	}

	public void setDq(HQInfoAppDto dq) {
		this.dq = dq;
	}

	public List<Map<String, String>> getTop20() {
		return top20;
	}

	public void setTop20(List<Map<String, String>> top20) {
		this.top20 = top20;
	}

	public List<String> getMessage() {
		return message;
	}

	public void setMessage(List<String> message) {
		this.message = message;
	}

	public ImageAppDto getStrategyImg() {
		return strategyImg;
	}

	public void setStrategyImg(ImageAppDto strategyImg) {
		this.strategyImg = strategyImg;
	}

	public ImageAppDto getSafetyImg() {
		return safetyImg;
	}

	public void setSafetyImg(ImageAppDto safetyImg) {
		this.safetyImg = safetyImg;
	}

	public List<ImageAppDto> getTopImgs() {
		return topImgs;
	}

	public void setTopImgs(List<ImageAppDto> topImgs) {
		this.topImgs = topImgs;
	}

	public String getServiceTelphone() {
		return serviceTelphone;
	}

	public void setServiceTelphone(String serviceTelphone) {
		this.serviceTelphone = serviceTelphone;
	}

}
