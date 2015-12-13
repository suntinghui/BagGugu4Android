package com.gugu.model;

import android.graphics.Color;

import com.ares.baggugu.dto.app.MyDebtPackage;
import com.gugu.GuguApplication;
import com.gugu.activity.BaseActivity;
import com.wufriends.gugu.R;

import java.io.Serializable;

/**
 * Created by sth on 8/25/15.
 */
public class MyDebtPackageEx extends MyDebtPackage implements Serializable {

    public String getTypeStr() {

        return this.getType() == 'a' ? "定投" : (this.getType() == 'b' ? "抢投" : "转让");
    }

    public String getStatusStr() {
        String status = "未知";

        // 先判断支付状态c支付确认中 d支付成功
        if (this.getPayStatus() == 'c') {
            status = "支付确认中";

        } else {
            // 先判断债权状态 a等待返息 b返息中 c完成 d已赎回 z已删除
            if (this.getStatus() == 'a') {
                status = "等待返息";
                // 抢排名时间 毫秒值 -1还没开始 0抢投中 Long.MAX_VALUE:9223372036854775807抢投结束 其他表示开抢时间
                if (this.getType() == 'b') {
                    if (this.getGrabTime() == -1) {
                        status = "竞买中...";
                    } else if (this.getGrabTime() == 0) {
                        status = "抢投中";
                    } else if (this.getGrabTime() != -2) {
                        status = "等待开抢";
                    }
                }
            } else if (this.getStatus() == 'b') {
                status = "返息中";
                // 返息中的时候才会出现转让 a正常 b转让中 c完成
                if (this.getZrStatus() == 'b') {
                    status = "转让中";
                }
            } else if (this.getStatus() == 'c' || this.getStatus() == 'd') {
                status = "已完成";
                if (this.getZrStatus() == 'c') {
                    status = "已转让";
                }
                if (this.getStatus() == 'd') {
                    status = "已赎回";
                }
            }
        }

        return status;
    }

    public int getStatusColor() {
        int color = Color.parseColor("#666666");

        String status = "未知";

        // 先判断支付状态c支付确认中 d支付成功
        if (this.getPayStatus() == 'c') {
            status = "支付确认中";

        } else {
            // 先判断债权状态 a等待返息 b返息中 c完成 d已赎回 z已删除
            if (this.getStatus() == 'a') {
                status = "等待返息";

                color = Color.parseColor("#666666");

                // 抢排名时间 毫秒值 -1还没开始 0抢投中 Long.MAX_VALUE:9223372036854775807抢投结束 其他表示开抢时间
                if (this.getType() == 'b') {
                    if (this.getGrabTime() == -1) {
                        status = "竞买中...";
                    } else if (this.getGrabTime() == 0) {
                        status = "抢投中";
                    } else if (this.getGrabTime() != -2) {
                        status = "等待开抢";
                    }

                    color = GuguApplication.getInstance().getResources().getColor(R.color.orange);

                }
            } else if (this.getStatus() == 'b') {
                status = "返息中";

                color = GuguApplication.getInstance().getResources().getColor(R.color.blueme);

                // 返息中的时候才会出现转让 a正常 b转让中 c完成
                if (this.getZrStatus() == 'b') {
                    status = "转让中";
                }
            } else if (this.getStatus() == 'c' || this.getStatus() == 'd') {
                status = "已完成";

                if (this.getZrStatus() == 'c') {
                    status = "已转让";

                    color = GuguApplication.getInstance().getResources().getColor(R.color.redme);
                }
                if (this.getStatus() == 'd') {
                    status = "已赎回";

                    color = GuguApplication.getInstance().getResources().getColor(R.color.redme);
                }
            }
        }

        return color;
    }
}
