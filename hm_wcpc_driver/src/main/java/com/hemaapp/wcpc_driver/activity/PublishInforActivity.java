package com.hemaapp.wcpc_driver.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_driver.BaseActivity;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.BaseUtil;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.adapter.PersonCountRecyclerAdapter;
import com.hemaapp.wcpc_driver.adapter.PopTimeAdapter;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.FeeCalculationInfor;
import com.hemaapp.wcpc_driver.module.PersonCountInfor;
import com.hemaapp.wcpc_driver.module.Route;
import com.hemaapp.wcpc_driver.module.User;
import com.hemaapp.wcpc_driver.view.wheelview.OnWheelScrollListener;
import com.hemaapp.wcpc_driver.view.wheelview.WheelView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by WangYuxia on 2016/5/25.
 */
public class PublishInforActivity extends BaseActivity implements RouteSearch.OnRouteSearchListener{

    private ImageView left;
    private TextView title;
    private TextView right;

    private LinearLayout in_layout, out_layout;
    private TextView in_text, out_text;
    private ImageView in_image, out_image;

    private LinearLayout in_view, out_view;
    private TextView in_text_startaddress, in_text_endaddress, in_text_begintime, in_text_numbers, in_text_publish;
    private TextView out_text_startaddress, out_text_endaddress, out_text_begintime, out_text_numbers, out_text_publish;

    private LinearLayout in_layout_calculate, out_layout_calculate;
    private TextView in_text_calculate, out_text_calculate;
    private LinearLayout in_layout_show, out_layout_show;
    private TextView in_text_success, in_text_failed, out_text_success, out_text_failed;

    private String in_start_lng, in_start_lat, in_end_lng, in_end_lat;
    private String out_start_lng, out_start_lat, out_end_lng, out_end_lat;

    private String in_startaddress, in_endaddress, in_city;
    private String out_startaddress, out_endaddress, out_city;
    private String in_starttime, out_starttime, in_distance, out_distance, in_money_success, in_momey_failed, out_money_success, out_money_failed;
    private String in_personcount, out_personcount;
    private boolean in_calculate = false, out_calculate = false;
    private String lat, lng, address, district;

    private PopupWindow timePop;
    private ViewGroup timeViewGroup;
    private LinearLayout time_father;
    private WheelView dayListView;
    private WheelView timeListView;
    private WheelView secondListView;
    private TextView time_clear;
    private TextView time_ok;
    private ArrayList<String> days = new ArrayList<>();
    private ArrayList<String> days1 = new ArrayList<>();
    private ArrayList<String> times = new ArrayList<>();
    private ArrayList<String> seconds = new ArrayList<>();

    private int flag = 0; // 0 :市内行程，1：跨城行程

    private ImageView background;
    private LinearLayout in_fatherview;
    private TextView in_ok;
    private TextView in_cancel;
    private RecyclerView in_recyclerView;
    private PersonCountRecyclerAdapter in_recyclerAdapter;

    private LinearLayout out_fatherview;
    private TextView out_ok;
    private TextView out_cancel;
    private RecyclerView out_recyclerView;
    private PersonCountRecyclerAdapter out_recyclerAdapter;

    private ArrayList<PersonCountInfor> in_counts = new ArrayList<>();
    private PersonCountInfor in_infor; //选中的人数
    private ArrayList<PersonCountInfor> out_counts = new ArrayList<>();
    private PersonCountInfor out_infor; //选中的人数

    private RouteSearch routeSearch;
    private ArrayList<Route> in_carRoutes = new ArrayList<>();
    private ArrayList<Route> out_carRoutes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_publishinfor);
        super.onCreate(savedInstanceState);
        for(int i = 0; i< 6; i++){
            in_counts.add(i, new PersonCountInfor(String.valueOf(i+1), false));
            out_counts.add(i, new PersonCountInfor(String.valueOf(i+1), false));
        }
        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);
        initDay();
        initHour(0);
        initMinute(0);
    }

    private void initHour(int type){
        times.clear();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 20); //将当前时间向后移动20分钟
        if(type == 0){
            int hour = calendar.get(Calendar.HOUR_OF_DAY); //新的小时
            for(int i = hour; i < 24; i++){
                times.add(i-hour, i+"点");
            }
        }else{
            for(int i = 0; i < 24; i++){
                times.add(i, i+"点");
            }
        }

        if(timeListView != null){
            timeListView.setVisibleItems(4);
            timeListView.setViewAdapter(new PopTimeAdapter(mContext, times));
            timeListView.setCurrentItem(0);
            timeListView.addScrollingListener(scrollListener);
        }
    }

    //初始化小时
    private void initMinute(int type){
        seconds.clear();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 20); //将当前时间向后移动20分钟
        if(type == 0){
            int min = calendar.get(Calendar.MINUTE); //新的分钟
            for(int i = min; i< 60; i++){
                seconds.add(i-min, i+"分");
            }
        }else{
            for(int i = 0; i< 60; i++){
                seconds.add(i, i+"分");
            }
        }
        if(secondListView != null){
            secondListView.setVisibleItems(4);
            secondListView.setViewAdapter(new PopTimeAdapter(mContext, seconds));
            secondListView.setCurrentItem(0);
            secondListView.addScrollingListener(scrollListener);
        }
    }

    private OnWheelScrollListener scrollListener = new OnWheelScrollListener() {

        @Override
        public void onScrollingStarted(WheelView wheel) {
        }

        @Override
        public void onScrollingFinished(WheelView wheel) {
            int current = wheel.getCurrentItem();
            if(wheel == dayListView){
                if(current == 0){
                    initHour(0);
                    initMinute(0);
                }else{
                    initHour(1);
                    initMinute(1);
                }
            }else if(wheel == timeListView){
                if(current == 0)
                    initMinute(0);
                else
                    initMinute(1);
            }
        }
    };

    //初始化当前日期范围
    private void initDay(){
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
        String day1 = sdf.format(new Date());
        days.add(0,day1);
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        days1.add(0, sdf1.format(new Date()));
        for(int i = 1; i< 30; i++){
            getNextDay(i);
        }
    }

    private void getNextDay(int day){
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
        java.util.Date dt = new java.util.Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        calendar.add(Calendar.DAY_OF_YEAR, day);// 日期加day天
        java.util.Date dt1 = calendar.getTime();
        String day1 = sdf.format(dt1);
        days.add(day,day1);
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        days1.add(day, sdf1.format(dt1));
    }

    private void toCalculate(){
        User user = hm_WcpcDriverApplication.getInstance().getUser();
        if(flag == 0)
            getNetWorker().feeCalculation(user.getFranchisee_id(), in_distance, in_personcount, district);
        else
            getNetWorker().feeCalculation(user.getFranchisee_id(), out_distance, out_personcount, district);
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask hemaNetTask) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case FEE_CALCULATION:
                showProgressDialog("请稍后...");
                break;
            case TRIPS_ADD:
                showProgressDialog("请稍后...");
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask hemaNetTask) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case FEE_CALCULATION:
            case TRIPS_ADD:
                cancelProgressDialog();
                break;
        }
    }

    @Override
    protected void callBackForServerSuccess(HemaNetTask hemaNetTask, HemaBaseResult hemaBaseResult) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case FEE_CALCULATION:
                HemaArrayResult<FeeCalculationInfor> fResult = (HemaArrayResult<FeeCalculationInfor>)hemaBaseResult;
                FeeCalculationInfor calinfor = fResult.getObjects().get(0);
                String keytype = hemaNetTask.getParams().get("keytype");
                if("1".equals(keytype)){
                    in_layout_calculate.setVisibility(View.GONE);
                    in_layout_show.setVisibility(View.VISIBLE);
                    if(calinfor.getSuccessfee().equals("0")){
                        in_money_success = "0.00";
                    }else{
                        in_money_success = BaseUtil.get2double(Double.parseDouble(calinfor.getSuccessfee()));
                    }
                    in_text_success.setText(in_money_success);
                    if(calinfor.getFailfee().equals("0")){
                        in_momey_failed = "0.00";
                    }else{
                        in_momey_failed = BaseUtil.get2double(Double.parseDouble(calinfor.getFailfee()));
                    }
                    in_text_failed.setText(in_momey_failed);
                    in_calculate = true;
                }else{
                    out_layout_calculate.setVisibility(View.GONE);
                    out_layout_show.setVisibility(View.VISIBLE);
                    if(calinfor.getSuccessfee().equals("0"))
                        out_money_success = "0.00";
                    else
                        out_money_success =  BaseUtil.get2double(Double.parseDouble(calinfor.getSuccessfee()));
                    out_text_success.setText(out_money_success);
                    if(calinfor.getFailfee().equals("0")){
                        out_money_failed = "0.00";
                    }else{
                        out_money_failed = BaseUtil.get2double(Double.parseDouble(calinfor.getFailfee()));
                    }
                    out_text_failed.setText(out_money_failed);
                    out_calculate = true;
                }
                break;
            case TRIPS_ADD:
                showTextDialog("发布成功");
                title.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask hemaNetTask, int i) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case FEE_CALCULATION:
                showTextDialog("预估费用失败");
                break;
            case TRIPS_ADD:
                showTextDialog("发布行程失败");
                break;
        }
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask, HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information){
            case FEE_CALCULATION:
                showTextDialog(baseResult.getMsg());
                break;
            case TRIPS_ADD:
                showTextDialog(baseResult.getMsg());
                break;
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        right = (TextView) findViewById(R.id.title_btn_right);
        title = (TextView) findViewById(R.id.title_text);

        in_layout = (LinearLayout) findViewById(R.id.layout_0);
        in_text = (TextView) findViewById(R.id.textview_0);
        in_image = (ImageView) findViewById(R.id.imageview_0);

        out_layout = (LinearLayout) findViewById(R.id.layout_1);
        out_text = (TextView) findViewById(R.id.textview_1);
        out_image = (ImageView) findViewById(R.id.imageview_1);

        in_view = (LinearLayout) findViewById(R.id.layout_2);
        in_text_startaddress = (TextView) in_view.findViewById(R.id.textview_0);
        in_text_endaddress = (TextView) in_view.findViewById(R.id.textview_1);
        in_text_begintime = (TextView) in_view.findViewById(R.id.textview_2);
        in_text_numbers = (TextView) in_view.findViewById(R.id.textview_3);
        in_text_publish = (TextView) in_view.findViewById(R.id.button);
        in_layout_calculate = (LinearLayout) in_view.findViewById(R.id.layout_4);
        in_text_calculate = (TextView) in_view.findViewById(R.id.textview_8);
        in_layout_show = (LinearLayout)in_view.findViewById(R.id.layout_5);
        in_text_success = (TextView) in_view.findViewById(R.id.textview_9);
        in_text_failed = (TextView) in_view.findViewById(R.id.textview_13);

        out_view = (LinearLayout) findViewById(R.id.layout_3);
        out_text_startaddress = (TextView) out_view.findViewById(R.id.textview_4);
        out_text_endaddress = (TextView) out_view.findViewById(R.id.textview_5);
        out_text_begintime = (TextView) out_view.findViewById(R.id.textview_6);
        out_text_numbers = (TextView) out_view.findViewById(R.id.textview_7);
        out_text_publish = (TextView) out_view.findViewById(R.id.button_0);
        out_layout_calculate = (LinearLayout) out_view.findViewById(R.id.layout_10);
        out_text_calculate = (TextView) out_view.findViewById(R.id.textview_10);
        out_layout_show = (LinearLayout)out_view.findViewById(R.id.layout_7);
        out_text_success = (TextView) out_view.findViewById(R.id.textview_11);
        out_text_failed = (TextView) out_view.findViewById(R.id.textview_12);

        background = (ImageView) findViewById(R.id.imageview);
        in_fatherview = (LinearLayout) findViewById(R.id.father);
        in_ok = (TextView) in_fatherview.findViewById(R.id.textview_1);
        in_cancel = (TextView) in_fatherview.findViewById(R.id.textview_0);
        in_recyclerView = (RecyclerView) in_fatherview.findViewById(R.id.recyclerView);

        out_fatherview = (LinearLayout) findViewById(R.id.father1);
        out_ok = (TextView) out_fatherview.findViewById(R.id.textview_1);
        out_cancel = (TextView) out_fatherview.findViewById(R.id.textview_0);
        out_recyclerView = (RecyclerView) out_fatherview.findViewById(R.id.recyclerView1);
    }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
        title.setText("发布行程");
        right.setText("计费规则");
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(mContext, FeeRuleActivity.class);
                startActivity(it);
            }
        });

        in_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                in_text.setTextColor(mContext.getResources().getColor(R.color.yellow));
                in_image.setVisibility(View.VISIBLE);
                in_view.setVisibility(View.VISIBLE);

                out_text.setTextColor(mContext.getResources().getColor(R.color.qianhui));
                out_image.setVisibility(View.INVISIBLE);
                out_view.setVisibility(View.GONE);
                flag = 0;
            }
        });

        out_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                in_text.setTextColor(mContext.getResources().getColor(R.color.qianhui));
                in_image.setVisibility(View.INVISIBLE);
                in_view.setVisibility(View.GONE);

                out_text.setTextColor(mContext.getResources().getColor(R.color.yellow));
                out_image.setVisibility(View.VISIBLE);
                out_view.setVisibility(View.VISIBLE);
                flag = 1;
            }
        });

        setListener(in_text_startaddress);
        setListener(out_text_startaddress);
        setListener(in_text_endaddress);
        setListener(out_text_endaddress);
        setListener(in_text_begintime);
        setListener(out_text_begintime);
        setListener(in_text_numbers);
        setListener(out_text_numbers);
        setListener(in_text_calculate);
        setListener(out_text_calculate);
        setListener(background);

        in_text_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNull(in_start_lat) && isNull(in_start_lng)){
                    showTextDialog("抱歉，请选择出发地");
                    return;
                }

                if(isNull(in_end_lat) && isNull(in_end_lng)){
                    showTextDialog("抱歉，请选择目的地");
                    return;
                }

                if(isNull(in_starttime)){
                    showTextDialog("抱歉，请选择出发时间");
                    return;
                }

                if(isNull(in_personcount)){
                    showTextDialog("抱歉，请选择可乘车人数");
                    return;
                }

                if(!in_calculate){
                    showTextDialog("请先进行价格预估");
                    return;
                }

                getNetWorker().tripsAdd(getUser().getToken(), in_startaddress, in_endaddress, in_starttime,
                        in_personcount,"", "", "", in_start_lng, in_start_lat, in_end_lng, in_end_lat, in_money_success,
                        in_momey_failed, in_distance, lng, lat, address, district);
            }
        });

        out_text_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNull(out_start_lat) && isNull(out_start_lng)){
                    showTextDialog("抱歉，请选择出发地");
                    return;
                }

                if(isNull(out_end_lat) && isNull(out_end_lng)){
                    showTextDialog("抱歉，请选择目的地");
                    return;
                }

                if(isNull(out_starttime)){
                    showTextDialog("抱歉，请选择出发时间");
                    return;
                }

                if(isNull(out_personcount)){
                    showTextDialog("抱歉，请选择可乘车人数");
                    return;
                }

                if(!out_calculate){
                    showTextDialog("请先进行价格预估");
                    return;
                }

                getNetWorker().tripsAdd(getUser().getToken(), out_startaddress, out_endaddress, out_starttime,
                        out_personcount,"", "", "", out_start_lng, out_start_lat, out_end_lng, out_end_lat, out_money_success,
                        out_money_failed, out_distance, lng, lat, address, district);
            }
        });
    }

    private void setListener(View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it ;
                switch (v.getId()){
                    case R.id.textview_0:
                        it = new Intent(mContext, StartPositionMapActivity.class);
                        it.putExtra("lng", in_start_lng);
                        it.putExtra("lat", in_start_lat);
                        if(isNull(lng) && isNull(lat)){
                            it.putExtra("isReturn", true);
                        }
                        it.putExtra("address", in_startaddress);
                        it.putExtra("keytype", flag);
                        startActivityForResult(it, R.id.textview_0);
                        break;
                    case R.id.textview_4:
                        it = new Intent(mContext, StartPositionMapActivity.class);
                        it.putExtra("lng", out_start_lng);
                        it.putExtra("lat", out_start_lat);
                        if(isNull(lng) && isNull(lat)){
                            it.putExtra("isReturn", true);
                        }
                        it.putExtra("address", out_startaddress);
                        it.putExtra("keytype", flag);
                        startActivityForResult(it, R.id.textview_4);
                        break;
                    case R.id.textview_1:
                        if(!isNull(in_start_lng) && !isNull(in_start_lat)){
                            it = new Intent(mContext, EndPositionMapActivity.class);
                            it.putExtra("start_lng", in_start_lng);
                            it.putExtra("start_lat", in_start_lat);
                            it.putExtra("end_lng", in_end_lng);
                            it.putExtra("end_lat", in_end_lat);
                            it.putExtra("keytype", flag);
                            it.putExtra("city", in_city);
                            startActivityForResult(it, R.id.textview_1);
                        }else {
                            showTextDialog("抱歉，请先选择出发地");
                            return;
                        }
                        break;
                    case R.id.textview_5:
                        if(!isNull(out_start_lng) && !isNull(out_start_lat)){
                            it = new Intent(mContext, EndPositionMapActivity.class);
                            it.putExtra("start_lng", out_start_lng);
                            it.putExtra("start_lat", out_start_lat);
                            it.putExtra("end_lng", out_end_lng);
                            it.putExtra("end_lat", out_end_lat);
                            it.putExtra("keytype", flag);
                            it.putExtra("city", out_city);
                            startActivityForResult(it, R.id.textview_5);
                        }else {
                            showTextDialog("抱歉，请先选择出发地");
                            return;
                        }
                        break;
                    case R.id.textview_2:
                        showTimePopWindow();
                        break;
                    case R.id.textview_6:
                        showTimePopWindow();
                        break;
                    case R.id.textview_3:
                        showPersonCountPopWindow();
                        break;
                    case R.id.textview_7:
                        showPersonCountPopWindow();
                        break;
                    case R.id.textview_8:
                        if(isNull(in_start_lat) && isNull(in_start_lng)){
                            showTextDialog("抱歉，请选择出发地");
                            return;
                        }

                        if(isNull(in_end_lat) && isNull(in_end_lng)){
                            showTextDialog("抱歉，请选择目的地");
                            return;
                        }

                        if(isNull(in_personcount)){
                            showTextDialog("请选择乘车人数");
                            return;
                        }
                        getReDistance();
                        break;
                    case R.id.textview_10:
                        if(isNull(out_start_lat) && isNull(out_start_lng)){
                            showTextDialog("抱歉，请选择出发地");
                            return;
                        }

                        if(isNull(out_end_lat) && isNull(out_end_lng)){
                            showTextDialog("抱歉，请选择目的地");
                            return;
                        }

                        if(isNull(out_personcount)){
                            showTextDialog("请选择乘车人数");
                            return;
                        }
                        getReDistance();
                        break;
                    case R.id.imageview:
                        background.setVisibility(View.GONE);
                        in_fatherview.setVisibility(View.GONE);
                        out_fatherview.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    private LatLonPoint fromPointExra;
    private LatLonPoint toPointExra;
    private void getReDistance(){
        if(flag == 0){
            fromPointExra = new LatLonPoint(Double.parseDouble(in_start_lat), Double.parseDouble(in_start_lng));
            toPointExra = new LatLonPoint(Double.parseDouble(in_end_lat), Double.parseDouble(in_end_lng));
        }else{
            fromPointExra = new LatLonPoint(Double.parseDouble(out_start_lat), Double.parseDouble(out_start_lng));
            toPointExra = new LatLonPoint(Double.parseDouble(out_end_lat), Double.parseDouble(out_end_lng));
        }
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                fromPointExra, toPointExra);
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo,
                RouteSearch.DrivingDefault, null, null, "");
        routeSearch.calculateDriveRouteAsyn(query);
    }

    private void showPersonCountPopWindow(){

        if(flag == 0){
            background.setVisibility(View.VISIBLE);
            in_fatherview.setVisibility(View.VISIBLE);
            in_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    background.setVisibility(View.GONE);
                    in_fatherview.setVisibility(View.GONE);
                }
            });

            in_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    background.setVisibility(View.GONE);
                    in_fatherview.setVisibility(View.GONE);
                    if(in_infor == null){
                        showTextDialog("请选择乘车人数");
                        return;
                    }
                    in_personcount = in_infor.getCount();
                    in_text_numbers.setText(in_personcount+"人");
                    in_text_numbers.setTextColor(mContext.getResources().getColor(R.color.word_black));
                    if(in_calculate)
                        toCalculate();
                }
            });

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            in_recyclerView.setLayoutManager(linearLayoutManager);
            //设置适配器
            in_recyclerAdapter = new PersonCountRecyclerAdapter(PublishInforActivity.this, in_counts);
            in_recyclerView.setAdapter(in_recyclerAdapter);
        }else{
            background.setVisibility(View.VISIBLE);
            out_fatherview.setVisibility(View.VISIBLE);
            out_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    background.setVisibility(View.GONE);
                    out_fatherview.setVisibility(View.GONE);
                }
            });

            out_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    background.setVisibility(View.GONE);
                    out_fatherview.setVisibility(View.GONE);
                    if(out_infor == null){
                        showTextDialog("请选择乘车人数");
                        return;
                    }
                    out_personcount = out_infor.getCount();
                    out_text_numbers.setText(out_personcount+"人");
                    out_text_numbers.setTextColor(mContext.getResources().getColor(R.color.word_black));
                    if(out_calculate)
                        toCalculate();
                }
            });

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            out_recyclerView.setLayoutManager(linearLayoutManager);
            //设置适配器
            out_recyclerAdapter =new PersonCountRecyclerAdapter(PublishInforActivity.this, out_counts);
            out_recyclerView.setAdapter(out_recyclerAdapter);
        }

    }

    private void showTimePopWindow(){
        if (timePop != null) {
            timePop.dismiss();
        }
        timePop = new PopupWindow(mContext);
        timePop.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        timePop.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        timePop.setBackgroundDrawable(new BitmapDrawable());
        timePop.setFocusable(true);
        timePop.setAnimationStyle(R.style.PopupAnimation);
        timeViewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(
                R.layout.pop_time3, null);
        time_father = (LinearLayout) timeViewGroup.findViewById(R.id.father);
        dayListView = (WheelView) timeViewGroup
                .findViewById(R.id.listview0);
        timeListView = (WheelView) timeViewGroup
                .findViewById(R.id.listview1);
        secondListView = (WheelView) timeViewGroup
                .findViewById(R.id.listview2);
        time_clear = (TextView) timeViewGroup
                .findViewById(R.id.clear);
        time_ok = (TextView) timeViewGroup.findViewById(R.id.ok);
        timePop.setContentView(timeViewGroup);
        timePop.showAtLocation(timeViewGroup, Gravity.CENTER, 0, 0);

        dayListView.setVisibleItems(4);
        dayListView.setViewAdapter(new PopTimeAdapter(mContext, days));
        dayListView.setCurrentItem(0);
        dayListView.addScrollingListener(scrollListener);

        timeListView.setVisibleItems(4);
        timeListView.setViewAdapter(new PopTimeAdapter(mContext, times));
        timeListView.setCurrentItem(0);
        timeListView.addScrollingListener(scrollListener);

        secondListView.setVisibleItems(4);
        secondListView.setViewAdapter(new PopTimeAdapter(mContext, seconds));
        secondListView.setCurrentItem(0);
        secondListView.addScrollingListener(scrollListener);

        time_clear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                timePop.dismiss();
            }
        });

        time_father.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePop.dismiss();
            }
        });

        time_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                timePop.dismiss();
                String t1;
                int d = dayListView.getCurrentItem();
                int t = timeListView.getCurrentItem();
                int s = secondListView.getCurrentItem();

                String time = times.get(t).substring(0, times.get(t).length()-1);
                if(time.length() == 1)
                    time = "0"+time;
                String second = seconds.get(s).substring(0, seconds.get(s).length()-1);
                if(second.length() == 1)
                    second = "0"+second;
                if(d == 0) {
                    t1 = "今天 " + time + ":" + second + "出发";
                } else if(d == 1){
                    t1 = "明天" + time + ":" + second + "出发";
                } else if(d == 2){
                    t1 = "后天" + time + ":" + second + "出发";
                } else {
                    t1 = days.get(d) + time + ":" + second + "出发";
                }


                SpannableString str = new SpannableString(t1);
                str.setSpan(new ForegroundColorSpan(0xff414141), 0,
                        t1.length() - 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if(flag == 0) {
                    in_starttime = days1.get(d) + " " + time + ":"
                            + second + ":00";
                    in_text_begintime.setText(str);
                }else{
                    out_starttime = days1.get(d)+" " + time + ":" + second + ":00";
                    out_text_begintime.setText(str);
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK)
            return;
        switch (requestCode){
            case R.id.textview_0:
                in_start_lng = data.getStringExtra("lng");
                in_start_lat = data.getStringExtra("lat");
                in_startaddress = data.getStringExtra("data");
                in_city = data.getStringExtra("city");
                in_text_startaddress.setText(in_startaddress);
                in_text_startaddress.setTextColor(mContext.getResources().getColor(R.color.word_black));
                if(isNull(lng))
                    lng = data.getStringExtra("pos_lng");
                if(isNull(lat))
                    lat = data.getStringExtra("pos_lat");
                if(isNull(address))
                    address = data.getStringExtra("pos_address");
                if(isNull(district)){
                    district = data.getStringExtra("my_address");
                }
                if(in_calculate)
                    getReDistance();
                break;
            case R.id.textview_4:
                out_start_lng = data.getStringExtra("lng");
                out_start_lat = data.getStringExtra("lat");
                out_city = data.getStringExtra("city");
                out_startaddress = data.getStringExtra("data");
                if(isNull(district)){
                    district = data.getStringExtra("my_address");
                }
                out_text_startaddress.setText(out_startaddress);
                out_text_startaddress.setTextColor(mContext.getResources().getColor(R.color.word_black));
                if(out_calculate)
                    getReDistance();
                break;
            case R.id.textview_1:
                in_end_lng = data.getStringExtra("lng");
                in_end_lat = data.getStringExtra("lat");
                in_endaddress= data.getStringExtra("data");
                in_text_endaddress.setText(in_endaddress);
                in_text_endaddress.setTextColor(mContext.getResources().getColor(R.color.word_black));
                if(in_calculate)
                    getReDistance();
                break;
            case R.id.textview_5:
                out_end_lng = data.getStringExtra("lng");
                out_end_lat = data.getStringExtra("lat");
                out_endaddress = data.getStringExtra("data");
                out_text_endaddress.setText(out_endaddress);
                out_text_endaddress.setTextColor(mContext.getResources().getColor(R.color.word_black));
                if(out_calculate)
                    getReDistance();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void operate(int position){
        if(flag == 0){
            for(int i = 0; i < in_counts.size(); i++){
                if(i == position) {
                    in_counts.get(i).setChecked(true);
                    in_infor = in_counts.get(i);
                } else
                    in_counts.get(i).setChecked(false);
            }
            in_recyclerAdapter.notifyDataSetChanged();
        }else{
            for(int i = 0; i < out_counts.size(); i++){
                if(i == position) {
                    out_counts.get(i).setChecked(true);
                    out_infor = out_counts.get(i);
                } else
                    out_counts.get(i).setChecked(false);
            }
            out_recyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int i) {
        if (result != null && result.getPaths() != null
                && result.getPaths().size() > 0) {
            List<DrivePath> paths = result.getPaths();
            if(flag == 0){
                if(in_carRoutes != null && in_carRoutes.size() > 0)
                    in_carRoutes.clear();
            }else{
                if(out_carRoutes != null && out_carRoutes.size() > 0)
                    out_carRoutes.clear();
            }
            for (DrivePath drivePath : paths) {
                long duration = drivePath.getDuration();
                String time = "预计" + BaseUtil.transDuration(duration);
                float dist = drivePath.getDistance();
                String distance = BaseUtil.transDistance(dist);
                String name = distance + "," + time;
                if(flag == 0)
                    in_carRoutes.add(new Route(name, time, distance, null, drivePath,
                            null, fromPointExra, toPointExra));
                else
                    out_carRoutes.add(new Route(name, time, distance, null, drivePath,
                            null, fromPointExra, toPointExra));
            }
            if(flag == 0){
                in_distance = in_carRoutes.get(0).getDistance();
            }else
                out_distance = out_carRoutes.get(0).getDistance();
            toCalculate();
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
    }
}
