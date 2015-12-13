package com.gugu.client;

import java.util.ArrayList;
import java.util.Stack;

import com.gugu.activity.MainActivity;

import android.app.Activity;
import android.util.Log;

public class ActivityManager {

	private Stack<Activity> stack = new Stack<Activity>();

	private static ActivityManager instance = null;

	public static ActivityManager getInstance() {
		if (instance == null) {
			instance = new ActivityManager();
		}

		return instance;
	}

	public ArrayList<Activity> getAllActivity() {
		ArrayList<Activity> list = new ArrayList<Activity>();

		if (null == stack || stack.isEmpty()) {
			return list;
		}

		for (int i = 0; i < stack.size(); i++) {
			list.add(stack.get(i));
		}

		return list;
	}

	public void pushActivity(Activity act) {
		try {
			stack.push(act);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void popActivity() {
		try {
			stack.pop();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void popToMainActivity() {
		while(true) {
			Activity peekActivity = stack.peek();
			if (!(peekActivity instanceof MainActivity)) {
				Log.e("===", "+++++++++++++++++++++");
				
				stack.pop();
				
			} else {
				Log.e("===", "^^^^^^^^^^^^^^^^^^^^^^^^^^^");
				
				break;
			}
			
		}
	}

}
