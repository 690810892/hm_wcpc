package com.hemaapp.wcpc_driver.module;

import xtom.frame.XtomObject;

/**
 * Created by wangyuxia on 2017/10/12.
 */

public class TypeInfor extends XtomObject{

    private String id;
    private String name;
    private boolean isCheck = false;

    public TypeInfor(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isCheck() {
        return isCheck;
    }
}
