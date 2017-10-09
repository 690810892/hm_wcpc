package com.hemaapp.wcpc_user.activity;

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
import android.widget.EditText;
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
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.BaseUtil;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.adapter.PersonCountRecyclerAdapter;
import com.hemaapp.wcpc_user.adapter.PopTimeAdapter;
import com.hemaapp.wcpc_user.adapter.ThankFeeRecyclerAdapter;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.FeeCalculationInfor;
import com.hemaapp.wcpc_user.module.PersonCountInfor;
import com.hemaapp.wcpc_user.module.Route;
import com.hemaapp.wcpc_user.module.User;
import com.hemaapp.wcpc_user.view.wheelview.OnWheelScrollListener;
import com.hemaapp.wcpc_user.view.wheelview.WheelView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import xtom.frame.util.XtomSharedPreferencesUtil;

/**
 * Created by WangYuxia on 2016/5/6.
 * type = 2: 发布跨城形成
 */
public class PublishInforActivity extends BaseActivity implements RouteSearch.OnRouteSearchListener{

    private ImageView left;
    private TextView title;
    private TextView right;

    private TextView text_startposition; //出发地
    private TextView text_endposition; //目的地

    private TextView text_starttime; //出发时间
    private PopupWindow timePop;
    private ViewGroup timeViewGroup;
    private LinearLayout layout_father;
    private WheelView dayListView;
    private WheelView timeListView;
    private WheelView secondListView;
    private TextView time_clear;
    private TextView time_ok;
    private ArrayList<String> days = new ArrayList<>();
    private ArrayList<String> days1 = new ArrayList<>();
    private ArrayList<String> times = new ArrayList<>();
    private ArrayList<String> seconds = new ArrayList<>();
    private PopTimeAdapter time_adapter;


    private ImageView background;
    private TextView text_personcount; //选择乘车人数
    public LinearLayout countfatherView;
    private RecyclerView recyclerView;
    private TextView count_cancle;
    private TextView count_ok;
    private ArrayList<PersonCountInfor> counts = new ArrayList<>();
    private PersonCountInfor infor; //选中的人数
    private PersonCountRecyclerAdapter recyclerAdapter;

    private LinearLayout layout_agree; //是否同意拼车
    private TextView text_agree;
    private String isAgreed = "0";

    private TextView text_thankfee; //感谢费
    private LinearLayout feefatherView;
    private RecyclerView feerecyclerView;
    private TextView fee_cancle;
    private TextView fee_ok;
    private ArrayList<PersonCountInfor> fees = new ArrayList<>();
    private PersonCountInfor feeinfor;
    private ThankFeeRecyclerAdapter feerecyclerAdapter;

    private EditText editText; //备注
    private LinearLayout layout_yugu;//预估价格
    private TextView text_button;
    private LinearLayout layout_result; //预估的结果
    private TextView text_success, text_failed;
    private TextView text_publish; //发布数据

    private String start_lng, start_lat, startposition, success, fail, content, start_city;
    private String end_lng, end_lat, endposition, distance, starttime, personcount, fee="0.0";
    private String pos_lng, pos_lat, pos_address;
    private String district;
    private FeeCalculationInfor calinfor;
    private boolean isCalculated = false;
    private RouteSearch routeSearch;
    private ArrayList<Route> carRoutes = new ArrayList<>();
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_publishinfor);
        super.onCreate(savedInstanceState);
        title.setText("发布跨城行程");
        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);
        user = hm_WcpcUserApplication.getInstance().getUser();
        district = XtomSharedPreferencesUtil.get(mContext, "district");
        if(!isNull(startposition)){
            text_startposition.setTextColor(0xff3f3f3f);
            text_startposition.setText(startposition);
        }

        initTime();
        initDay();
        initHour(0);
        initMinute(0);
    }

    private void initTime(){
        for(int i = 0; i< 6; i++){
            counts.add(i, new PersonCountInfor(String.valueOf(i+1), false));
        }
        fees.add(0, new PersonCountInfor("2",false));
        fees.add(1, new PersonCountInfor("4",false));
        fees.add(2, new PersonCountInfor("6",false));
        fees.add(3, new PersonCountInfor("8",false));
        fees.add(4, new PersonCountInfor("10",false));
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
            time_adapter = new PopTimeAdapter(mContext, times);
            timeListView.setViewAdapter(time_adapter);
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
                calinfor = fResult.getObjects().get(0);
                success = calinfor.getSuccessfee();
                fail = calinfor.getFailfee();
                layout_yugu.setVisibility(View.GONE);
                layout_result.setVisibility(View.VISIBLE);
                text_success.setText(BaseUtil.get2double(Double.parseDouble(success)));
                text_failed.setText(BaseUtil.get2double(Double.parseDouble(fail)));
                isCalculated = true;
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

        text_startposition = (TextView) findViewById(R.id.textview_0);
        text_endposition = (TextView) findViewById(R.id.textview_1);
        text_starttime = (TextView) findViewById(R.id.textview_2);
        text_personcount = (TextView) findViewById(R.id.textview_3);

        layout_agree = (LinearLayout) findViewById(R.id.layout_2);
        text_agree = (TextView) findViewById(R.id.textview_4);
        editText = (EditText) findViewById(R.id.edittext);
        text_thankfee = (TextView) findViewById(R.id.textview_5);

        layout_yugu = (LinearLayout) findViewById(R.id.layout_3);
        text_button = (TextView)findViewById(R.id.textview_6);
        layout_result = (LinearLayout) findViewById(R.id.layout_4);
        text_success = (TextView) findViewById(R.id.textview_7);
        text_failed = (TextView) findViewById(R.id.textview_8);
        text_publish = (TextView) findViewById(R.id.button);

        background = (ImageView) findViewById(R.id.imageview);
        countfatherView = (LinearLayout) findViewById(R.id.father);
        count_ok = (TextView) countfatherView.findViewById(R.id.textview_1);
        count_cancle = (TextView) countfatherView.findViewById(R.id.textview_0);
        recyclerView = (RecyclerView) countfatherView.findViewById(R.id.recyclerView);

        feefatherView = (LinearLayout) findViewById(R.id.father1);
        fee_ok = (TextView) feefatherView.findViewById(R.id.textview_1);
        fee_cancle = (TextView) feefatherView.findViewById(R.id.textview_0);
        feerecyclerView = (RecyclerView) feefatherView.findViewById(R.id.recyclerView1);
    }

    @Override
    protected void getExras() {
        start_lng = mIntent.getStringExtra("start_lng");
        start_lat = mIntent.getStringExtra("start_lat");
        startposition = mIntent.getStringExtra("start_position");
        start_city = mIntent.getStringExtra("start_city");
    }

    @Override
    protected void setListener() {
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        right.setText("计费规则");
        right.setTextColor(mContext.getResources().getColor(R.color.title_color));
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(mContext, FeeRuleActivity.class);
                startActivity(it);
            }
        });

        setListener(right);
        setListener(text_startposition);
        setListener(text_endposition);
        setListener(text_starttime);
        setListener(text_personcount);
        setListener(layout_agree);
        setListener(text_thankfee);
        setListener(text_button);
        setListener(text_publish);
        setListener(background);
    }

    private void setListener(View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it ;
                switch (v.getId()){
                    case R.id.title_btn_right: //计费规则
                        it = new Intent(mContext, FeeRuleActivity.class);
                        startActivity(it);
                        break;
                    case R.id.textview_0: //选择出发地
                        it = new Intent(mContext, StartPositionMapActivity.class);
                        it.putExtra("lng", start_lng);
                        it.putExtra("lat", start_lat);
                        if(isNull(pos_lat) && isNull(pos_lng))
                            it.putExtra("isReturn", true);
                        it.putExtra("address", startposition);
                        startActivityForResult(it, R.id.textview_0);
                        break;
                    case R.id.textview_1: //选择目的地
                        if(!isNull(start_lng) && !isNull(start_lat)){
                            it = new Intent(mContext, EndPositionMapActivity.class);
                            it.putExtra("start_lng", start_lng);
                            it.putExtra("start_lat", start_lat);
                            it.putExtra("end_lng", end_lng);
                            it.putExtra("end_lat", end_lat);
                            it.putExtra("city", start_city);
                            startActivityForResult(it, R.id.textview_1);
                        }else {
                            showTextDialog("抱歉，请先选择出发地");
                            return;
                        }
                        break;
                    case R.id.textview_2:
                        showTimePopWindow();
                        break;
                    case R.id.textview_3:
                        showPersonCountPopWindow();
                        break;
                    case R.id.layout_2:
                        if(isAgreed.equals("0")){
                            isAgreed = "1";
                            text_agree.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                                    R.mipmap.img_agree_s, 0);
                        }else{
                            isAgreed = "0";
                            text_agree.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                                    R.mipmap.img_agree_n, 0);
                        }
                        break;
                    case R.id.textview_5:
                        showThankFeePopWindow();
                        break;
                    case R.id.textview_6:
                        if(isNull(start_lat) && isNull(start_lng)){
                            showTextDialog("请选择起点");
                            return;
                        }
                        if(isNull(end_lat) && isNull(end_lng)){
                            showTextDialog("请选择目的地");
                            return;
                        }
                        if(isNull(personcount)){
                            showTextDialog("请选择乘车人数");
                            return;
                        }
                        getReDistance();
                        break;
                    case R.id.button:
                        toPublish();
                        break;
                    case R.id.imageview:
                        background.setVisibility(View.GONE);
                        countfatherView.setVisibility(View.GONE);
                        feefatherView.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    private void toPublish(){
        if(isNull(start_lat) || isNull(start_lng)){
            showTextDialog("请选择出发地");
            return;
        }
        if(isNull(end_lat) || isNull(end_lng)){
            showTextDialog("请选择目的地");
            return;
        }
        if(isNull(starttime)){
            showTextDialog("请选择出发时间");
            return;
        }
        if(isNull(personcount)){
            showTextDialog("请选择人数");
            return;
        }
        if(isNull(success) || isNull(fail)){
            showTextDialog("请先预估费用");
            return;
        }

        if(!isNull(fee)){
            success = String.valueOf(Double.parseDouble(success) + Double.parseDouble(fee));
            fail = String.valueOf(Double.parseDouble(fail) + Double.parseDouble(fee));
        }

        content = editText.getText().toString();
        if(isNull(content))
            content = "";
        getNetWorker().tripsAdd(user.getToken(), startposition, endposition, starttime, personcount,
                isAgreed, fee, content, start_lng, start_lat, end_lng, end_lat, success, fail, distance, pos_lng, pos_lat, pos_address, district);
    }

    private void toCalculate(){
        getNetWorker().feeCalculation("0", distance, personcount, district);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK)
            return;
        switch (requestCode){
            case R.id.textview_0:
                start_lng = data.getStringExtra("lng");
                start_lat = data.getStringExtra("lat");
                startposition = data.getStringExtra("data");
                start_city = data.getStringExtra("city");
                if(isNull(pos_lng))
                    pos_lng = data.getStringExtra("pos_lng");
                if(isNull(pos_lat))
                    pos_lat = data.getStringExtra("pos_lat");
                if(isNull(pos_address))
                    pos_address = data.getStringExtra("pos_address");
                text_startposition.setText(startposition);
                text_startposition.setTextColor(0xff3f3f3f);
                if(isCalculated){
                    getReDistance();
                }
                break;
            case R.id.textview_1:
                end_lng = data.getStringExtra("lng");
                end_lat = data.getStringExtra("lat");
                endposition = data.getStringExtra("data");
                text_endposition.setText(endposition);
                text_endposition.setTextColor(0xffff9900);
                if(isCalculated){
                    getReDistance();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private LatLonPoint fromPointExra;
    private LatLonPoint toPointExra;
    private void getReDistance(){
        fromPointExra = new LatLonPoint(Double.parseDouble(start_lat), Double.parseDouble(start_lng));
        toPointExra = new LatLonPoint(Double.parseDouble(end_lat), Double.parseDouble(end_lng));
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                fromPointExra, toPointExra);
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo,
                RouteSearch.DrivingDefault, null, null, "");
        routeSearch.calculateDriveRouteAsyn(query);
    }

    private void showThankFeePopWindow(){
        background.setVisibility(View.VISIBLE);
        feefatherView.setVisibility(View.VISIBLE);
        TextView text_name = (TextView) feefatherView.findViewById(R.id.textview);
        text_name.setText("选择感谢费");
        fee_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                background.setVisibility(View.GONE);
                feefatherView.setVisibility(View.GONE);
            }
        });

        fee_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                background.setVisibility(View.GONE);
                feefatherView.setVisibility(View.GONE);
                if(feeinfor == null){
                    showTextDialog("抱歉，请选择感谢费");
                    return;
                }
                fee = feeinfor.getCount();
                text_thankfee.setText(fee+"元");
                text_thankfee.setTextColor(mContext.getResources().getColor(R.color.word_black));
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        feerecyclerView.setLayoutManager(linearLayoutManager);
        //设置适配器
        feerecyclerAdapter = new ThankFeeRecyclerAdapter(PublishInforActivity.this, fees);
        feerecyclerView.setAdapter(feerecyclerAdapter);
    }

    private void showPersonCountPopWindow(){
        background.setVisibility(View.VISIBLE);
        countfatherView.setVisibility(View.VISIBLE);
        count_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                background.setVisibility(View.GONE);
                countfatherView.setVisibility(View.GONE);
            }
        });

        count_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                background.setVisibility(View.GONE);
                countfatherView.setVisibility(View.GONE);
                if(infor == null){
                    showTextDialog("抱歉，请选择乘车人数");
                }

                personcount = infor.getCount();
                text_personcount.setText(personcount+"人");
                text_personcount.setTextColor(mContext.getResources().getColor(R.color.word_black));
                if(isCalculated){
                    getReDistance();
                }
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        //设置适配器
        recyclerAdapter = new PersonCountRecyclerAdapter(PublishInforActivity.this, counts);
        recyclerView.setAdapter(recyclerAdapter);
    }

    public void operate(int position, int type){
        if(type == 1){
            for(int i = 0; i < counts.size(); i++){
                if(i == position) {
                    counts.get(i).setChecked(true);
                    infor = counts.get(i);
                } else
                    counts.get(i).setChecked(false);
            }
            recyclerAdapter.notifyDataSetChanged();
        }else{
            for(int i = 0; i < fees.size(); i++){
                if(i == position) {
                    fees.get(i).setChecked(true);
                    feeinfor = fees.get(i);
                } else
                    fees.get(i).setChecked(false);
            }
          feerecyclerAdapter.notifyDataSetChanged();
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
        layout_father = (LinearLayout) timeViewGroup.findViewById(R.id.father);
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
        layout_father.setOnClickListener(new View.OnClickListener() {
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
                starttime = days1.get(d) + " " + time + ":"
                            + second + ":00";
                text_starttime.setText(str);
            }
        });
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int i) {
        if (result != null && result.getPaths() != null
                && result.getPaths().size() > 0) {
            List<DrivePath> paths = result.getPaths();
            if(carRoutes != null || carRoutes.size() > 0)
                carRoutes.clear();
            for (DrivePath drivePath : paths) {
                long duration = drivePath.getDuration();
                String time = "预计" + BaseUtil.transDuration(duration);
                float dist = drivePath.getDistance();
                String distance = BaseUtil.transDistance(dist);
                String name = distance + "," + time;
                carRoutes.add(new Route(name, time, distance, null, drivePath,
                        null, fromPointExra, toPointExra));
            }

            distance = carRoutes.get(0).getDistance();
            toCalculate();
        } else {
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
    }
}
