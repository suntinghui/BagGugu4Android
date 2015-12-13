package com.ares.baggugu.dto.app;

import java.io.Serializable;
import java.util.List;

public class BonusAppDto implements Serializable {

	private static final long serialVersionUID = 2034507090221319263L;

	private String total;

	private List<BonusListAppDto> appDtos;

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public List<BonusListAppDto> getAppDtos() {
		return appDtos;
	}

	public void setAppDtos(List<BonusListAppDto> appDtos) {
		this.appDtos = appDtos;
	}

}
