package com.gugu.model;

import com.ares.baggugu.dto.app.BankEntity;
import com.gugu.GuguApplication;

import java.io.Serializable;

/**
 * Created by sth on 11/28/15.
 */
public class BankEntityEx extends BankEntity implements Serializable {

    public String getLimitStr() {

        StringBuffer limitSB = new StringBuffer();
        if (this.getSingle() != 0) {
            limitSB.append("本卡单笔限额" + this.getSingle() + "元 ");
        }
        if (this.getDay() != 0) {
            limitSB.append("单日限额" + this.getDay() + "元 ");
        }
        if (this.getMonth() != 0) {
            limitSB.append("单月限额" + this.getMonth() + "元");
        }

        return limitSB.toString();
    }

    public int getLogoId() {
        return GuguApplication.getInstance().getResources().getIdentifier(this.getImg(), "drawable", GuguApplication.getInstance().getPackageName());
    }

}
