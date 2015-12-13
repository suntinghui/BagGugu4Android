package com.gugu.utils;

import java.util.ArrayList;

public class LotteryUtil {

	public static ArrayList<LotteryModel> lotteryList = null;

	public static LotteryModel getLottery(String id) {
		if (lotteryList == null) {
			initLotteryList();
		}

		for (LotteryModel model : lotteryList) {
			if (model.getId().equals(id)) {
				return model;
			}
		}

		return new LotteryModel("NULL0", 4, "恭喜发财");
	}

	private static void initLotteryList() {
		lotteryList = new ArrayList<LotteryModel>();

		// { "20积分", "现金0.5元", "iPad", "现金1元", "恭喜发财", "抢投红包2元" };
		lotteryList.add(new LotteryModel("INTEGRAL20", 0, "20积分"));
		lotteryList.add(new LotteryModel("MONEY0.5", 1, "现金0.5元"));
		lotteryList.add(new LotteryModel("IPAD", 2, "iPad"));
		//lotteryList.add(new LotteryModel("MONEY1", 3, "现金1元"));
		lotteryList.add(new LotteryModel("MONEY0.1", 3, "现金0.1元"));
		lotteryList.add(new LotteryModel("NULL0", 4, "恭喜发财"));
		lotteryList.add(new LotteryModel("MONEY1", 5, "现金1元"));


		lotteryList.add(new LotteryModel("QT_BONUS5", 6, "抢投红包5元"));
		lotteryList.add(new LotteryModel("INTEGRAL10", 7, "10积分"));
		lotteryList.add(new LotteryModel("INTEGRAL5", 8, "5积分"));


	}

}
