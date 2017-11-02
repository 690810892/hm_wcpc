package com.hemaapp.wcpc_user;

/**
 * EventBus的配置类型类
 * Created by CoderHu on 2017-03-10.
 */

public enum EventBusConfig {

    LOGIN_SUCCESS(0, "登录成功"),
    LOGOUT_SUCCESS(1, "退出成功"),
    CLIENT_ID(2, "个推注册成功"),
    NEW_MESSAGE(3, "收到新消息"),
    LOAN_ADD_SUCCESS(4, "添加贷款成功"),
    REFRESH_CUSTOMER_INFO(5, "刷新客户信息"),
    REFRESH_MAIN_DATA(6, "刷新首页数据"),
    NOTICE_SAVE(7, "通知操作"),
    REFRESH_GROUP_LIST(8, "刷新群主页"),
    REFRESH_BLOG_LIST(9, "刷新生意圈"),
    REFRESH_BLOG_INFOR(10, "刷新帖子详情"),
    REFRESH_USER(11, "刷新我的详情"),
    REFRESH_ZIXUN_LIST(12, "刷新资讯列表"),
    REFRESH_CARTYPE(13, "刷新接车时间列表"),
    PAY_WECHAT(14, "微信支付"),
    REFRESH_Custom_LIST(15, "刷新客户列表"),
    REFRESH_CAR_LIST(16, "刷新接车列表"),
    REFRESH_Custom_INFOR(17, "刷新客户详情"),
    REFRESH_CAR_INFOR(18, "刷新接车详情"),
    REFRESH_folder_LIST(19, "刷新文件夹"),
    REFRESH_MEMO_LIST(20, "刷新备忘录"),
    GET_COLOR(21, "获取颜色"),
    GET_FROMS(22, "获取引流液来源");


    private int id;
    private String description;

    EventBusConfig(int id, String description) {
        this.id = id;
        this.description = description;
    }
}
