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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.BaseMyActivity;
import com.hemaapp.wcpc_user.BaseRecycleAdapter;
import com.hemaapp.wcpc_user.BaseUtil;
import com.hemaapp.wcpc_user.EventBusConfig;
import com.hemaapp.wcpc_user.EventBusModel;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.RecycleUtils;
import com.hemaapp.wcpc_user.adapter.PersonCountAdapter;
import com.hemaapp.wcpc_user.adapter.PersonCountRecyclerAdapter;
import com.hemaapp.wcpc_user.adapter.PopTimeAdapter;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.DistrictInfor;
import com.hemaapp.wcpc_user.module.PersonCountInfor;
import com.hemaapp.wcpc_user.module.SysInitInfo;
import com.hemaapp.wcpc_user.module.User;
import com.hemaapp.wcpc_user.view.wheelview.OnWheelScrollListener;
import com.hemaapp.wcpc_user.view.wheelview.WheelView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import xtom.frame.util.XtomSharedPreferencesUtil;

/**
 * 发布行程
 */
public class SendActivity extends BaseActivity {

    @BindView(R.id.title_btn_left)
    ImageView titleBtnLeft;
    @BindView(R.id.title_btn_right)
    TextView titleBtnRight;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.tv_start_city)
    TextView tvStartCity;
    @BindView(R.id.iv_change)
    ImageView ivChange;
    @BindView(R.id.tv_end_city)
    TextView tvEndCity;
    @BindView(R.id.lv_city)
    LinearLayout lvCity;
    @BindView(R.id.leftlin)
    LinearLayout leftlin;
    @BindView(R.id.tv_start)
    TextView tvStart;
    @BindView(R.id.tv_end)
    TextView tvEnd;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.tv_pin)
    TextView tvPin;
    @BindView(R.id.lv_pin)
    LinearLayout lvPin;
    @BindView(R.id.tv_charter)
    TextView tvCharter;
    @BindView(R.id.lv_charter)
    LinearLayout lvCharter;
    @BindView(R.id.ev_content)
    EditText evContent;
    @BindView(R.id.tv_coupon)
    TextView tvCoupon;
    @BindView(R.id.lv_coupon)
    LinearLayout lvCoupon;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_feeinfor)
    TextView tvFeeinfor;
    @BindView(R.id.tv_button)
    TextView tvButton;
    private User user;
    private SysInitInfo infor;
    private DistrictInfor startCity, endCity, temCity, myCity;
    private String start_address = "", end_address, start_lng, start_lat, end_lat, end_lng, begintime, coupon_vavle, coupon_id;

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
    private String begin, pin_start, pin_end;
    private String isAgreed = "1";
    private int count = 1, coupon = 0;
    private float totleFee = 0, price = 0, addstart = 0, addend = 0;
    private PopupWindow mWindow_exit;
    private ViewGroup mViewGroup_exit;
    private ArrayList<PersonCountInfor> counts = new ArrayList<>();
    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_send);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
        user = hm_WcpcUserApplication.getInstance().getUser();
        pin_start = XtomSharedPreferencesUtil.get(mContext, "pin_start");
        pin_end = XtomSharedPreferencesUtil.get(mContext, "pin_end");
        for (int i = 0; i < 4; i++) {
            counts.add(i, new PersonCountInfor(String.valueOf(i + 1), false));
        }
        initDay();
        initHour(0);
        initMinute(0);
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case TRIPS_ADD:
                cancelProgressDialog();
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case TRIPS_ADD:
                showTextDialog("发布失败");
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case TRIPS_ADD:
                showTextDialog(baseResult.getMsg());
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void callBackForServerSuccess(HemaNetTask netTask,
                                            HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case TRIPS_ADD:
                showTextDialog("发布成功");
                EventBus.getDefault().post(new EventBusModel(EventBusConfig.REFRESH_BLOG_LIST));
                titleText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        if (flag == 0) {
                            Intent it = new Intent(mContext, MyCurrentTrip2Activity.class);
                            startActivity(it);
                        }
                    }
                }, 1000);
                break;
            default:
                break;
        }
    }


    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case TRIPS_ADD:
                showProgressDialog("请稍后...");
                break;
            default:
                break;
        }
    }

    @Override
    protected void findView() {
    }

    @Override
    protected void getExras() {
        flag = mIntent.getIntExtra("flag", 0);
    }

    @Override
    protected void setListener() {
        titleBtnRight.setText("计费规则");
        titleBtnRight.setTextColor(mContext.getResources().getColor(R.color.title_color));
        titleText.setText("发布跨城行程");
    }

    @OnClick({R.id.title_btn_left, R.id.title_btn_right, R.id.tv_start_city, R.id.iv_change, R.id.tv_end_city, R.id.lv_city, R.id.tv_start, R.id.tv_end, R.id.tv_time, R.id.tv_count, R.id.lv_pin, R.id.lv_charter, R.id.lv_coupon, R.id.tv_feeinfor, R.id.tv_button})
    public void onViewClicked(View view) {
        Intent it;
        switch (view.getId()) {
            case R.id.title_btn_left:
                finish();
                break;
            case R.id.title_btn_right:
                it = new Intent(mContext, ShowInternetPageActivity.class);
                it.putExtra("name", "计费规则");
                SysInitInfo sysInitInfo = hm_WcpcUserApplication.getInstance().getSysInitInfo();
                String path = sysInitInfo.getSys_web_service() + "webview/parm/feeinstruction";
                it.putExtra("path", path);
                startActivity(it);
                break;
            case R.id.tv_start_city:
                it = new Intent(mContext, SelectCityActivity.class);
                it.putExtra("start_cityid", "0");
                startActivityForResult(it, 1);
                break;
            case R.id.iv_change:
                if (startCity == null) {
                    showTextDialog("请选择出发城市");
                    break;
                }
                if (endCity == null) {
                    showTextDialog("请选择到达城市");
                    break;
                }
                temCity = startCity;
                startCity = endCity;
                endCity = temCity;
                String tem_address = start_address;
                start_address = end_address;
                end_address = tem_address;
                String tem_lat = start_lat;
                start_lat = end_lat;
                end_lat = tem_lat;
                String tem_lng = start_lng;
                start_lng = end_lng;
                end_lng = tem_lng;
                if (startCity != null)
                    tvStartCity.setText(startCity.getName());
                else
                    tvStartCity.setText("");
                if (endCity != null)
                    tvEndCity.setText(endCity.getName());
                else
                    tvEndCity.setText("");
                tvStart.setText(start_address);
                tvEnd.setText(end_address);
                break;
            case R.id.tv_end_city:
                if (startCity == null) {
                    showTextDialog("请先选择出发城市");
                    break;
                }
                it = new Intent(mContext, SelectCityActivity.class);
                it.putExtra("start_cityid", startCity.getCity_id());
                startActivityForResult(it, 2);
                break;
            case R.id.lv_city:
                break;
            case R.id.tv_start:
                if (myCity != null) {
                    it = new Intent(mContext, MapStartActivity.class);
                    if (myCity.getCity_id().equals(startCity.getCity_id())) {//正常myCity就是endCity，但是点击交换之后，要特殊处理
                        it.putExtra("areas", myCity.getAreas2());
                        it.putExtra("center_city", myCity.getCenter_lnglat2());
                    } else {
                        it.putExtra("areas", myCity.getAreas1());
                        it.putExtra("center_city", myCity.getCenter_lnglat1());
                    }
                    it.putExtra("city", startCity.getName());
                    it.putExtra("title", "选择出发地");
                    startActivityForResult(it, 3);
                } else {
                    showTextDialog("请先选择城市");
                }
                break;
            case R.id.tv_end:
                if (myCity != null) {
                    it = new Intent(mContext, MapStartActivity.class);
                    if (myCity.getCity_id().equals(startCity.getCity_id())) {//正常myCity就是endCity，但是点击交换之后，要特殊处理
                        it.putExtra("areas", myCity.getAreas1());
                        it.putExtra("center_city", myCity.getCenter_lnglat1());
                    } else {
                        it.putExtra("areas", myCity.getAreas2());
                        it.putExtra("center_city", myCity.getCenter_lnglat2());
                    }
                    it.putExtra("city", endCity.getName());
                    it.putExtra("title", "选择目的地");
                    startActivityForResult(it, 4);
                } else {
                    showTextDialog("请先选择城市");
                }
                break;
            case R.id.tv_time:
                if (isNull(tvStart.getText().toString())) {
                    showTextDialog("请先选择出发地");
                    break;
                }
                if (isNull(tvEnd.getText().toString())) {
                    showTextDialog("请先选择目的地");
                    break;
                }
                showTimePopWindow();
                break;
            case R.id.tv_count:
                if (isNull(tvStart.getText().toString())) {
                    showTextDialog("请选择出发地");
                    break;
                }
                if (isNull(tvEnd.getText().toString())) {
                    showTextDialog("请选择目的地");
                    break;
                }
                if (isNull(tvTime.getText().toString())) {
                    showTextDialog("请选择出发时间");
                    break;
                }
                countDialog();
                break;
            case R.id.lv_pin:
                if (isNull(begintime)) {
                    showTextDialog("抱歉，请先选择出发时间");
                    return;
                }
                if (BaseUtil.compareTime(begin, pin_start) == 1 && BaseUtil.compareTime(pin_end, begin) == 1) {//在可拼单时间内
                    isAgreed = "1";
                    tvPin.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.mipmap.img_agree_s, 0);
                    tvCharter.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.mipmap.img_agree_n, 0);
                    resetPrice();
                } else {
                    String start = BaseUtil.TransTimeHour(pin_start, "HH:mm");
                    String end = BaseUtil.TransTimeHour(pin_end, "HH:mm");
                    showTextDialog("出发时间在" + start + "至" + end + "期间提供拼车服务");
                }
                break;
            case R.id.lv_charter:
                isAgreed = "0";
                tvPin.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                        R.mipmap.img_agree_n, 0);
                tvCharter.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                        R.mipmap.img_agree_s, 0);
                count = 4;//包车默认4人
                tvCount.setText(count + "人");
                resetPrice();
                break;
            case R.id.lv_coupon:
                if (isNull(start_address)) {
                    showTextDialog("抱歉，请先选择出发地");
                    return;
                }
                if (isNull(end_address)) {
                    showTextDialog("抱歉，请先选择目的地");
                    return;
                }
                it = new Intent(mContext, MyCouponListActivity.class);
                it.putExtra("keytype", "2");
                startActivityForResult(it, 5);
                break;
            case R.id.tv_feeinfor:
                if (startCity == null) {
                    showTextDialog("请选择出发地");
                    return;
                }
                if (endCity == null) {
                    showTextDialog("请选择目的地");
                    return;
                }
                it = new Intent(mContext, FeeInforActivity.class);
                it.putExtra("start", startCity.getName());
                it.putExtra("end", endCity.getName());
                it.putExtra("price", myCity.getPrice());
                if (isAgreed.equals("0")) {
                    it.putExtra("count", "4");
                } else {
                    it.putExtra("count", count + "");
                }

                it.putExtra("addstart", addstart + "");
                it.putExtra("addend", addend + "");
                it.putExtra("couple", coupon_vavle);
                it.putExtra("all", totleFee + "");
                startActivity(it);
                break;
            case R.id.tv_button:
                toPublish();
                break;
        }
    }

    private void toPublish() {
        if (startCity == null) {
            showTextDialog("请选择出发城市");
            return;
        }
        if (endCity == null) {
            showTextDialog("请选择到达城市");
            return;
        }
        if (isNull(start_address)) {
            showTextDialog("请选择目的地");
            return;
        }
        if (isNull(end_address)) {
            showTextDialog("请选择目的地");
            return;
        }
        if (isNull(begintime)) {
            showTextDialog("请选择出发时间");
            return;
        }
        if (isNull(tvCount.getText().toString())) {
            showTextDialog("请选择乘车人数");
            return;
        }
        if (isAgreed.equals("1")) {
            if (count == 0) {
                showTextDialog("请选择人数");
                return;
            }
        } else {
            count = 4;
        }
        String content = evContent.getText().toString();
        if (isNull(content))
            content = "";
        float allfee = totleFee + coupon;
        log_d("start_cityid=" + startCity.getCity_id());
        log_d("start_city=" + startCity.getName());
        log_d("end_cityid=" + endCity.getCity_id());
        log_d("end_city=" + endCity.getName());
        log_d("starttime=" + begintime);
        log_d("personcount=" + count);
        log_d("isAgreed=" + isAgreed);
        log_d("content=" + content);
        log_d("start_lng=" + start_lng);
        log_d("start_lat=" + start_lat);
        log_d("end_lng=" + end_lng);
        log_d("end_lat=" + end_lat);
        log_d("coupon_id=" + coupon_id);
        log_d("allfee=" + totleFee + coupon);
        if (isNull(coupon_id))
            coupon_id = "0";
        getNetWorker().tripsAdd(user.getToken(), start_address, startCity.getCity_id(), startCity.getName(), end_address, endCity.getCity_id(),
                endCity.getName(), begintime, count + "",
                isAgreed, content, start_lng, start_lat, end_lng, end_lat, "0", "0", "当前位置", coupon_id, allfee + "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case 1:
                startCity = (DistrictInfor) data.getSerializableExtra("infor");
                tvStartCity.setText(startCity.getName());
                endCity = null;
                myCity = null;
                tvEndCity.setText("");
                start_address = "";
                end_address = "";
                tvStart.setText("");
                tvEnd.setText("");
                price = 0;
                addend = 0;
                addstart = 0;
                resetPrice();
                break;
            case 2:
                endCity = (DistrictInfor) data.getSerializableExtra("infor");
                myCity = (DistrictInfor) data.getSerializableExtra("infor");
                price = Float.parseFloat(myCity.getPrice());
                tvEndCity.setText(endCity.getName());
                start_address = "";
                end_address = "";
                tvStart.setText("");
                tvEnd.setText("");
                resetPrice();
                break;
            case 3:
                start_lat = data.getStringExtra("lat");
                start_lng = data.getStringExtra("lng");
                log_e("start_lng=" + start_lng);
                log_e("start_lat=" + start_lat);

                start_address = data.getStringExtra("address");
                String adds = data.getStringExtra("addPrice");
                addstart = Float.parseFloat(adds);
                tvStart.setText(start_address);
                resetPrice();
                break;
            case 4:
                end_lat = data.getStringExtra("lat");
                end_lng = data.getStringExtra("lng");
                log_e("end_lng=" + end_lng);
                log_e("end_lat=" + end_lat);
                end_address = data.getStringExtra("address");
                tvEnd.setText(end_address);
                String adde = data.getStringExtra("addPrice");
                addend = Float.parseFloat(adde);
                resetPrice();
                break;
            case 5:
                coupon_id = data.getStringExtra("id");
                coupon_vavle = data.getStringExtra("money");
                tvCoupon.setText(coupon_vavle + "元");
                coupon = Integer.parseInt(coupon_vavle);
                resetPrice();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void resetPrice() {

        totleFee = price * count - coupon + addend + addstart;
        tvPrice.setText(totleFee + "元");
    }

    private void showTimePopWindow() {
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
                //timePop.dismiss();
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

                String time = times.get(t).substring(0, times.get(t).length() - 1);
                if (time.length() == 1)
                    time = "0" + time;
                String second = seconds.get(s).substring(0, seconds.get(s).length() - 1);
                if (second.length() == 1)
                    second = "0" + second;
                if (d == 0) {
                    t1 = "今天 " + time + ":" + second + "出发";
                } else if (d == 1) {
                    t1 = "明天" + time + ":" + second + "出发";
                } else if (d == 2) {
                    t1 = "后天" + time + ":" + second + "出发";
                } else {
                    t1 = days.get(d) + time + ":" + second + "出发";
                }


                SpannableString str = new SpannableString(t1);
                str.setSpan(new ForegroundColorSpan(0xff414141), 0,
                        t1.length() - 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                begintime = days1.get(d) + " " + time + ":"
                        + second + ":00";
                tvTime.setText(str);
                begin = time + ":" + second + ":00";
                if (BaseUtil.compareTime(begin, pin_start) == 1 && BaseUtil.compareTime(pin_end, begin) == 1) {//在可拼单时间内
                } else {
                    isAgreed = "0";
                    tvPin.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.mipmap.img_agree_n, 0);
                    tvCharter.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.mipmap.img_agree_s, 0);
                    count = 4;
                    tvCount.setText(count + "人");
                    resetPrice();
                }
            }
        });
    }

    private OnWheelScrollListener scrollListener = new OnWheelScrollListener() {

        @Override
        public void onScrollingStarted(WheelView wheel) {
        }

        @Override
        public void onScrollingFinished(WheelView wheel) {
            int current = wheel.getCurrentItem();
            if (wheel == dayListView) {
                if (current == 0) {
                    initHour(0);
                    initMinute(0);
                } else {
                    initHour(1);
                    initMinute(1);
                }
            } else if (wheel == timeListView) {
                if (current == 0)
                    initMinute(0);
                else if (current == times.size() - 1) {
                    initMinute(2);
                } else
                    initMinute(1);
            }
        }
    };

    private void initHour(int type) {
        times.clear();
        Calendar calendar = Calendar.getInstance();
        int min = calendar.get(Calendar.MINUTE);
        if (min > 30)
            calendar.add(Calendar.MINUTE, 180);
        else
            calendar.add(Calendar.MINUTE, 120); //将当前时间向后移动
        if (type == 0) {
            int hour = calendar.get(Calendar.HOUR_OF_DAY); //新的小时
            for (int i = hour; i < 24; i++) {
                times.add(i - hour, i + "点");
            }
        } else {
            int hour = calendar.get(Calendar.HOUR_OF_DAY); //新的小时
            for (int i = 0; i < hour - 1; i++) {
                times.add(i, i + "点");
            }
        }

        if (timeListView != null) {
            timeListView.setVisibleItems(4);
            time_adapter = new PopTimeAdapter(mContext, times);
            timeListView.setViewAdapter(time_adapter);
            timeListView.setCurrentItem(0);
            timeListView.addScrollingListener(scrollListener);
        }
    }

    //初始化小时
    private void initMinute(int type) {
        seconds.clear();
        Calendar calendar = Calendar.getInstance();
        if (type == 0) {
            int min = calendar.get(Calendar.MINUTE); //新的分钟
            if (min > 0 && min <= 30)
                seconds.add(0, "30分");
            else {
                seconds.add(0, "00分");
            }
        } else if (type == 1) {
            seconds.add(0, "00分");
            seconds.add(1, "30分");
        } else {
            int min = calendar.get(Calendar.MINUTE); //新的分钟
            if (min > 0 && min <= 30)
                seconds.add(0, "00分");
            else {
                seconds.add(0, "30分");
            }
        }
        if (secondListView != null) {
            secondListView.setVisibleItems(4);
            secondListView.setViewAdapter(new PopTimeAdapter(mContext, seconds));
            secondListView.setCurrentItem(0);
            secondListView.addScrollingListener(scrollListener);
        }
    }

    //初始化当前日期范围
    private void initDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
        String day1 = sdf.format(new Date());
        days.add(0, day1);
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        days1.add(0, sdf1.format(new Date()));
        for (int i = 1; i < 2; i++) {
            getNextDay(i);
        }
    }

    private void getNextDay(int day) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
        java.util.Date dt = new java.util.Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        calendar.add(Calendar.DAY_OF_YEAR, day);// 日期加day天
        java.util.Date dt1 = calendar.getTime();
        String day1 = sdf.format(dt1);
        days.add(day, day1);
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        days1.add(day, sdf1.format(dt1));
    }

    private void countDialog() {
        if (mWindow_exit != null) {
            mWindow_exit.dismiss();
        }
        mWindow_exit = new PopupWindow(mContext);
        mWindow_exit.setWidth(FrameLayout.LayoutParams.MATCH_PARENT);
        mWindow_exit.setHeight(FrameLayout.LayoutParams.MATCH_PARENT);
        mWindow_exit.setBackgroundDrawable(new BitmapDrawable());
        mWindow_exit.setFocusable(true);
        mWindow_exit.setAnimationStyle(R.style.PopupAnimation);
        mViewGroup_exit = (ViewGroup) LayoutInflater.from(mContext).inflate(
                R.layout.pop_count, null);
        TextView bt_ok = (TextView) mViewGroup_exit.findViewById(R.id.tv_button);
        TextView bt_cancel = (TextView) mViewGroup_exit.findViewById(R.id.tv_cancel);
        RecyclerView recyclerView = (RecyclerView) mViewGroup_exit.findViewById(R.id.recyclerView);
        mWindow_exit.setContentView(mViewGroup_exit);
        mWindow_exit.showAtLocation(mViewGroup_exit, Gravity.CENTER, 0, 0);
        bt_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mWindow_exit.dismiss();
            }
        });
        bt_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mWindow_exit.dismiss();
                for (PersonCountInfor infor : counts) {
                    if (infor.isChecked()) {
                        count = Integer.parseInt(infor.getCount());
                        resetPrice();
                        tvCount.setText(count + "人");
                        break;
                    }
                }
            }
        });
        final PersonCountAdapter adapter = new PersonCountAdapter(mContext, counts);
        RecycleUtils.initHorizontalRecyle(recyclerView);
        recyclerView.setAdapter(adapter);
    }
}
