package com.hemaapp.wcpc_user.module;

import xtom.frame.XtomObject;

/**
 * Created by WangYuxia on 2016/5/12.
 */
public class PersonCountInfor extends XtomObject {

    private String count;
    private boolean isChecked = false;

    public PersonCountInfor(String count, boolean isChecked) {
        this.count = count;
        this.isChecked = isChecked;
    }

    public String getCount() {
        return count;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
