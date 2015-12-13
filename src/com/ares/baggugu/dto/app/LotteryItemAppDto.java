package com.ares.baggugu.dto.app;

import java.io.Serializable;

public class LotteryItemAppDto implements Serializable {

	private static final long serialVersionUID = -2162249219797340757L;

	/**
	 * MONEY现金 INTEGRAL积分 QT_BONUS抢投红包 NULL恭喜发财 IPAD
	 */
	private String name;

	/**
	 * 数值
	 */
	private String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
