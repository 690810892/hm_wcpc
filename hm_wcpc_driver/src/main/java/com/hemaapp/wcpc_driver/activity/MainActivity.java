package com.hemaapp.wcpc_driver.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.hm_FrameWork.view.RefreshLoadmoreLayout;
import com.hemaapp.wcpc_driver.BaseActivity;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.adapter.OrderListAdapter;
import com.hemaapp.wcpc_driver.adapter.TripListAdapter;
import com.hemaapp.wcpc_driver.getui.PushUtils;
import com.hemaapp.wcpc_driver.getui.ServiceLocationGPS;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.OrderListInfor;
import com.hemaapp.wcpc_driver.module.TripListInfor;
import com.hemaapp.wcpc_driver.module.User;
import com.iflytek.sunflower.FlowerCollector;

import java.util.ArrayList;

import xtom.frame.util.XtomSharedPreferencesUtil;
import xtom.frame.util.XtomToastUtil;
import xtom.frame.view.XtomListView;
import xtom.frame.view.XtomRefreshLoadmoreLayout;

/**
 * Created by WangYuxia on 2016/5/24.
 * 首页
 */
public class MainActivity extends BaseActivity {

    private ImageView left; //个人中心
    private ImageView right; //系统通知
    private ImageView image_point_title;

    private ProgressBar progressBar;

    private LinearLayout view_myorder;
    private TextView text_myorder;
    private ImageView image_myorder;
    private RefreshLoadmoreLayout layout_myorder;
    private XtomListView listview_myorder;
    private TextView image_publish;

    private LinearLayout view_qiangdan;
    private TextView text_qiandan;
    private ImageView image_qiandan;
    private RefreshLoadmoreLayout layout_qiandan;
    private XtomListView listview_qiandan;

    private int page_order = 0;
    private ArrayList<OrderListInfor> orders = new ArrayList<>();
    private OrderListAdapter adapter_order;

    private boolean isFrist = true;
    private int page_trips = 0;
    private ArrayList<TripListInfor> trips = new ArrayList<>();
    private TripListAdapter adapter_trip;
    private String lng, lat, district;
    private int flag = 0;
    private User user;

    private long time;// 用于判断二次点击返回键的时间间隔
    private boolean isSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        user = hm_WcpcDriverApplication.getInstance().getUser();
        image_point_title.setVisibility(View.INVISIBLE);
        if(user == null)
            image_point_title.setVisibility(View.INVISIBLE);
        getNoticeUnread();
        getOrderList();
        lng = XtomSharedPreferencesUtil.get(mContext, "lng");
        lat = XtomSharedPreferencesUtil.get(mContext, "lat");
        district = XtomSharedPreferencesUtil.get(mContext, "district");
        startGeTuiPush();
        startService();
    }

    private void startService(){
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {//判断是否拥有定位权限

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    3);
        } else {
            startService(new Intent(mContext, ServiceLocationGPS.class));
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        startService();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 3) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED)//未获得定位权限
            {
                showTextDialog("没有定位权限，无法获取抢单数据");
                left.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1500);
            } else {
                startService();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected boolean onKeyBack() {
        if ((System.currentTimeMillis() - time) >= 2000) {
            XtomToastUtil.showShortToast(mContext, "再按一次返回键退出程序");
            time = System.currentTimeMillis();
        } else {
            finish();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        stopGeTuiPush();
        super.onDestroy();
        com.hemaapp.wcpc_driver.getui.PushReceiver.stop();
    }

    private void getNoticeUnread(){
        getNetWorker().noticeUnread(user.getToken(), "1", "2");
    }

    private void getOrderList(){
        getNetWorker().driverOrderList(getUser().getToken(), "4", page_order);
    }

    private void getTripsList(){
        getNetWorker().tripsList("1", lng+","+lat, "0", page_trips, district);
    }

    @Override
    protected void onResume() {
        //移动数据统计分析
        FlowerCollector.onResume(mContext);
        FlowerCollector.onPageStart("MainActivity");
        super.onResume();
        if(!isFrist && flag == 1){
            page_trips = 0;
            getTripsList();
        }
        getNoticeUnread();
    }


    @Override
    protected void onPause() {
        //移动数据统计分析
        FlowerCollector.onPageEnd("MainActivity");
        FlowerCollector.onPause(mContext);
        super.onPause();
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case NOTICE_UNREAD:
                showProgressDialog("请稍后...");
                break;
            case DRIVER_ORDER_LIST:
                break;
            case TRIPS_LIST:
                break;
            case GRAP_TRIPS:
                showProgressDialog("");
                break;
        }
    }


    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case NOTICE_UNREAD:
                cancelProgressDialog();
                break;
            case DRIVER_ORDER_LIST:
                progressBar.setVisibility(View.GONE);
                layout_myorder.setVisibility(View.VISIBLE);
                image_publish.setVisibility(View.VISIBLE);
                layout_qiandan.setVisibility(View.GONE);
                break;
            case TRIPS_LIST:
                progressBar.setVisibility(View.GONE);
                layout_myorder.setVisibility(View.GONE);
                image_publish.setVisibility(View.GONE);
                layout_qiandan.setVisibility(View.VISIBLE);
                break;
            case GRAP_TRIPS:
                cancelProgressDialog();
                break;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void callBackForServerSuccess(HemaNetTask netTask,
                                            HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case NOTICE_UNREAD:
                HemaArrayResult<String> sResult = (HemaArrayResult<String>) baseResult;
                int count = Integer.parseInt(isNull(sResult.getObjects().get(0))?"0":sResult.getObjects().get(0));
                if(count > 0)
                    image_point_title.setVisibility(View.VISIBLE);
                else
                    image_point_title.setVisibility(View.INVISIBLE);
                break;
            case DRIVER_ORDER_LIST:
                String page = netTask.getParams().get("page");
                HemaPageArrayResult<OrderListInfor> cResult = (HemaPageArrayResult<OrderListInfor>) baseResult;
                ArrayList<OrderListInfor> cashs = cResult.getObjects();

                if ("0".equals(page)) {// 刷新
                    layout_myorder.refreshSuccess();
                    orders.clear();
                    orders.addAll(cashs);

                    hm_WcpcDriverApplication application = hm_WcpcDriverApplication.getInstance();
                    int sysPagesize = application.getSysInitInfo()
                            .getSys_pagesize();
                    if (cashs.size() < sysPagesize)
                        layout_myorder.setLoadmoreable(false);
                    else
                        layout_myorder.setLoadmoreable(true);

                } else {// 更多
                    layout_myorder.loadmoreSuccess();
                    if (cashs.size() > 0)
                        orders.addAll(cashs);
                    else {
                        layout_myorder.setLoadmoreable(false);
                        XtomToastUtil.showShortToast(mContext, "已经到最后啦");
                    }
                }
                freshData(0);
                break;
            case TRIPS_LIST:
                String page1 = netTask.getParams().get("page");
                HemaPageArrayResult<TripListInfor> tResult = (HemaPageArrayResult<TripListInfor>) baseResult;
                ArrayList<TripListInfor> infors = tResult.getObjects();

                if ("0".equals(page1)) {// 刷新
                    layout_qiandan.refreshSuccess();
                    trips.clear();
                    trips.addAll(infors);

                    hm_WcpcDriverApplication application = hm_WcpcDriverApplication.getInstance();
                    int sysPagesize = application.getSysInitInfo()
                            .getSys_pagesize();
                    if (infors.size() < sysPagesize)
                        layout_qiandan.setLoadmoreable(false);
                    else
                        layout_qiandan.setLoadmoreable(true);

                } else {// 更多
                    layout_qiandan.loadmoreSuccess();
                    if (infors.size() > 0)
                        trips.addAll(infors);
                    else {
                        layout_qiandan.setLoadmoreable(false);
                        XtomToastUtil.showShortToast(mContext, "已经到最后啦");
                    }
                }
                freshData(1);
                break;
            case GRAP_TRIPS:
                page_trips = 0;
                getTripsList();
                break;
            case DEVICE_SAVE:
                isSuccess = true;
                break;
        }
    }

    private void freshData(int type) {
        if(type == 0){
            if (adapter_order == null) {
                adapter_order = new OrderListAdapter(mContext, orders, listview_myorder);
                adapter_order.setEmptyString("您暂时没有任何订单");
                listview_myorder.setAdapter(adapter_order);
            } else {
                adapter_order.setEmptyString("您暂时没有任何订单");
                adapter_order.notifyDataSetChanged();
            }
        }else {
            if (adapter_trip == null) {
                adapter_trip = new TripListAdapter(mContext, trips, listview_myorder);
                adapter_trip.setEmptyString("您暂时没有任何订单");
                listview_qiandan.setAdapter(adapter_trip);
            } else {
                adapter_trip.setEmptyString("您暂时没有任何订单");
                adapter_trip.notifyDataSetChanged();
            }
        }

    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DRIVER_ORDER_LIST:
                String page = netTask.getParams().get("page");
                if ("0".equals(page)) {// 刷新
                    layout_myorder.refreshFailed();
                    freshData(0);
                } else {// 更多
                    layout_myorder.loadmoreFailed();
                }
                break;
            case TRIPS_LIST:
                String page1 = netTask.getParams().get("page");
                if ("0".equals(page1)) {// 刷新
                    layout_qiandan.refreshFailed();
                    freshData(1);
                } else {// 更多
                    layout_qiandan.loadmoreFailed();
                }
                break;
            case GRAP_TRIPS:
                showTextDialog(baseResult.getMsg());
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DRIVER_ORDER_LIST:
                String page = netTask.getParams().get("page");
                if ("0".equals(page)) {// 刷新
                    layout_myorder.refreshFailed();
                    freshData(0);
                } else {// 更多
                    layout_myorder.loadmoreFailed();
                }
                break;
            case TRIPS_LIST:
                String page1 = netTask.getParams().get("page");
                if ("0".equals(page1)) {// 刷新
                    layout_qiandan.refreshFailed();
                    freshData(1);
                } else {// 更多
                    layout_qiandan.loadmoreFailed();
                }
                break;
            case GRAP_TRIPS:
                showTextDialog("抢单失败，请稍后重试");
                break;
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        right = (ImageView) findViewById(R.id.title_btn_right_image);
        image_point_title = (ImageView) findViewById(R.id.title_point);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        view_myorder = (LinearLayout) findViewById(R.id.layout_0);
        text_myorder = (TextView) findViewById(R.id.textview_0);
        image_myorder = (ImageView) findViewById(R.id.imageview_0);
        layout_myorder = (RefreshLoadmoreLayout)findViewById(R.id.refreshLoadmoreLayout);
        listview_myorder = (XtomListView) findViewById(R.id.listview);
        image_publish = (TextView) findViewById(R.id.textview);

        view_qiangdan = (LinearLayout) findViewById(R.id.layout_1);
        text_qiandan = (TextView) findViewById(R.id.textview_1);
        image_qiandan = (ImageView) findViewById(R.id.imageview_1);
        layout_qiandan = (RefreshLoadmoreLayout)findViewById(R.id.refreshLoadmoreLayout1);
        listview_qiandan = (XtomListView) findViewById(R.id.listview1);
    }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(mContext, PersonCenterInforActivity.class);
                startActivity(it);
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(mContext, NoticeListActivity.class);
                startActivity(it);
            }
        });

        view_myorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_myorder.setTextColor(mContext.getResources().getColor(R.color.yellow));
                image_myorder.setVisibility(View.VISIBLE);
                layout_myorder.setVisibility(View.VISIBLE);
                image_publish.setVisibility(View.VISIBLE);
                text_qiandan.setTextColor(mContext.getResources().getColor(R.color.shenhui));
                image_qiandan.setVisibility(View.INVISIBLE);
                layout_qiandan.setVisibility(View.GONE);
                flag = 0;
            }
        });

        view_qiangdan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_myorder.setTextColor(mContext.getResources().getColor(R.color.yellow));
                image_myorder.setVisibility(View.INVISIBLE);
                layout_myorder.setVisibility(View.GONE);
                image_publish.setVisibility(View.GONE);
                text_qiandan.setTextColor(mContext.getResources().getColor(R.color.yellow));
                image_qiandan.setVisibility(View.VISIBLE);
                layout_qiandan.setVisibility(View.VISIBLE);
                flag = 1;
                if(isFrist){
                    isFrist = false;
                    progressBar.setVisibility(View.VISIBLE);
                    getTripsList();
                }
            }
        });

        layout_myorder.setOnStartListener(new XtomRefreshLoadmoreLayout.OnStartListener() {
            @Override
            public void onStartRefresh(XtomRefreshLoadmoreLayout xtomRefreshLoadmoreLayout) {
                page_order = 0;
                getOrderList();
            }

            @Override
            public void onStartLoadmore(XtomRefreshLoadmoreLayout xtomRefreshLoadmoreLayout) {
                page_order++;
                getOrderList();
            }
        });

        layout_qiandan.setOnStartListener(new XtomRefreshLoadmoreLayout.OnStartListener() {
            @Override
            public void onStartRefresh(XtomRefreshLoadmoreLayout xtomRefreshLoadmoreLayout) {
                page_trips = 0;
                getTripsList();
            }

            @Override
            public void onStartLoadmore(XtomRefreshLoadmoreLayout xtomRefreshLoadmoreLayout) {
                page_trips ++;
                getTripsList();
            }
        });

        image_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("0".equals(XtomSharedPreferencesUtil.get(mContext, "loginflag"))){
                    showTextDialog("抱歉，您目前处于休车状态，无法发布行程");
                    return;
                }else{
                    Intent it = new Intent(mContext, PublishInforActivity.class);
                    startActivityForResult(it, R.id.layout_1);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK)
            return;
        switch (requestCode){
            case R.id.layout:
            case R.id.layout_1:
                page_order = 0;
                getOrderList();
                break;
            case R.id.layout_0:
                page_trips = 0;
                getTripsList();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /* 推送相关 */
    private PushReceiver pushReceiver;

    private void startGeTuiPush() {
        com.igexin.sdk.PushManager.getInstance().initialize(mContext);
        registerPushReceiver();
    }

    private void stopGeTuiPush() {
        unregisterPushReceiver();
    }

    private void registerPushReceiver() {
        if (pushReceiver == null) {
            pushReceiver = new PushReceiver();
            IntentFilter mFilter = new IntentFilter("com.hemaapp.push.connect");
            mFilter.addAction("com.hemaapp.push.msg");
            mFilter.addAction("com.hemaapp.push.location");
            registerReceiver(pushReceiver, mFilter);
        }
    }

    private void unregisterPushReceiver() {
        if (pushReceiver != null)
            unregisterReceiver(pushReceiver);
    }

    private class PushReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            handleEvent(intent);
        }

        private void handleEvent(Intent intent) {
            String action = intent.getAction();
            if ("com.hemaapp.push.connect".equals(action)) {
                if(!isSuccess)
                    saveDevice();
            } else if ("com.hemaapp.push.msg".equals(action)) {
                boolean unread = PushUtils.getmsgreadflag(
                        getApplicationContext(), "2");
                if (unread) {
                    if(flag == 1){
                        page_trips = 0;
                        getNoticeUnread();
                    }
                }
            }else if("com.hemaapp.push.location".equals(action)){
                String mlng = XtomSharedPreferencesUtil.get(mContext, "lng");
                String mlat = XtomSharedPreferencesUtil.get(mContext, "lat");
                getNetWorker().positionSave(user.getToken(), "2", mlng, mlat);
            }
        }
    }

    public void saveDevice() {
        User user = getApplicationContext().getUser();
        getNetWorker().deviceSave(user.getToken(),
                PushUtils.getChannelId(mContext),
                PushUtils.getChannelId(mContext));
    }

	/* 推送相关end */
}
