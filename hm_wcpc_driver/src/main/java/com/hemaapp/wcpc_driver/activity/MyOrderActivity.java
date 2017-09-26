package com.hemaapp.wcpc_driver.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.hm_FrameWork.view.RefreshLoadmoreLayout;
import com.hemaapp.wcpc_driver.BaseActivity;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.adapter.MyOrderListAdapter;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.OrderListInfor;
import com.hemaapp.wcpc_driver.module.User;

import java.util.ArrayList;

import xtom.frame.util.XtomSharedPreferencesUtil;
import xtom.frame.util.XtomToastUtil;
import xtom.frame.view.XtomListView;
import xtom.frame.view.XtomRefreshLoadmoreLayout;

/**
 * Created by WangYuxia on 2016/5/27.
 * 我的乘客订单
 */
public class MyOrderActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private LinearLayout layout_orderby;
    private TextView tv_orderby;

    private RefreshLoadmoreLayout layout;
    private XtomListView mListView;
    private ProgressBar progressBar;

    private ArrayList<OrderListInfor> orders = new ArrayList<>();
    private MyOrderListAdapter adapter_notice;
    private int page_notice = 0;
    private String lng, lat, order="0";

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_person_order);
        super.onCreate(savedInstanceState);
        user = hm_WcpcDriverApplication.getInstance().getUser();
        lng = XtomSharedPreferencesUtil.get(mContext, "lng");
        lat = XtomSharedPreferencesUtil.get(mContext, "lat");
        getNoticeList();
    }

    private void getNoticeList(){
        getNetWorker().driverOrderList(user.getToken(), "4", page_notice, order, lng, lat);
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DRIVER_ORDER_LIST:
                break;
            case ORDER_OPERATE:
                showProgressDialog("请稍后...");
                break;
            default:
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DRIVER_ORDER_LIST:
                progressBar.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);
                break;
            case ORDER_OPERATE:
                cancelProgressDialog();
                break;
            default:
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
            case DRIVER_ORDER_LIST:
                String page = netTask.getParams().get("page");
                HemaPageArrayResult<OrderListInfor> cResult = (HemaPageArrayResult<OrderListInfor>) baseResult;
                ArrayList<OrderListInfor> cashs = cResult.getObjects();

                if ("0".equals(page)) {// 刷新
                    layout.refreshSuccess();
                    orders.clear();
                    orders.addAll(cashs);

                    hm_WcpcDriverApplication application = hm_WcpcDriverApplication.getInstance();
                    int sysPagesize = application.getSysInitInfo()
                            .getSys_pagesize();
                    if (cashs.size() < sysPagesize)
                        layout.setLoadmoreable(false);
                    else
                        layout.setLoadmoreable(true);

                } else {// 更多
                    layout.loadmoreSuccess();
                    if (cashs.size() > 0)
                        orders.addAll(cashs);
                    else {
                        layout.setLoadmoreable(false);
                        XtomToastUtil.showShortToast(mContext, "已经到最后啦");
                    }
                }
                freshData();
                break;
            case ORDER_OPERATE:
                page_notice = 0;
                getNoticeList();
                break;
        }
    }

    private void freshData() {
        if (adapter_notice == null) {
            adapter_notice = new MyOrderListAdapter(mContext, orders, mListView);
            adapter_notice.setEmptyString("暂时没有乘客订单");
            mListView.setAdapter(adapter_notice);
        } else {
            adapter_notice.setEmptyString("暂时没有乘客订单");
            adapter_notice.notifyDataSetChanged();
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
                    layout.refreshFailed();
                    freshData();
                } else {// 更多
                    layout.loadmoreFailed();
                }
                break;
            case ORDER_OPERATE:
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
                    layout.refreshFailed();
                    freshData();
                } else {// 更多
                    layout.loadmoreFailed();
                }
                break;
            case ORDER_OPERATE:
                showTextDialog("送达操作失败");
                break;
        }
    }

    @Override
    protected void findView() {
        title = (TextView) findViewById(R.id.title_text);
        left = (ImageView) findViewById(R.id.title_btn_left);
        right = (TextView) findViewById(R.id.title_btn_right);

        layout_orderby = (LinearLayout) findViewById(R.id.layout_orderby);
        tv_orderby = (TextView) findViewById(R.id.tv_orderby);

        layout = (RefreshLoadmoreLayout) findViewById(R.id.refreshLoadmoreLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        mListView = (XtomListView) findViewById(R.id.listview);
    }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
        title.setText("我的乘客订单");
        right.setVisibility(View.INVISIBLE);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        layout.setOnStartListener(new XtomRefreshLoadmoreLayout.OnStartListener() {
            @Override
            public void onStartRefresh(XtomRefreshLoadmoreLayout xtomRefreshLoadmoreLayout) {
                page_notice = 0;
                getNoticeList();
            }

            @Override
            public void onStartLoadmore(XtomRefreshLoadmoreLayout xtomRefreshLoadmoreLayout) {
                page_notice ++;
                getNoticeList();
            }
        });

        layout_orderby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupWindow();
            }
        });
    }

    private PopupWindow mWindow;
    private ViewGroup mViewGroup;
    private TextView tv_orderby_time;
    private TextView tv_orderby_distance;

    private void showPopupWindow(){
        if(progressBar.getVisibility() == View.VISIBLE)
            return;

        tv_orderby.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                R.mipmap.img_arrow_up, 0);

        if (mWindow != null) {
            mWindow.dismiss();
        }
        mWindow = new PopupWindow(mContext);
        mWindow.setWidth(FrameLayout.LayoutParams.MATCH_PARENT);
        mWindow.setHeight(FrameLayout.LayoutParams.MATCH_PARENT);
        mWindow.setBackgroundDrawable(new BitmapDrawable());
        mWindow.setFocusable(true);
        mWindow.setAnimationStyle(R.style.PopupAnimation);
        mViewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(
                R.layout.pop_orderby, null);
        tv_orderby_time = (TextView) mViewGroup.findViewById(R.id.textview);
        tv_orderby_distance = (TextView) mViewGroup.findViewById(R.id.textview_0);
        mWindow.setContentView(mViewGroup);
        mWindow.showAtLocation(mViewGroup, Gravity.CENTER, 0, 0);

        tv_orderby_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                order = "1";
                page_notice = 0;
                getNoticeList();
                mWindow.dismiss();
            }
        });

        tv_orderby_distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                order = "2";
                page_notice = 0;
                getNetWorker();
                mWindow.dismiss();
            }
        });
        mWindow.showAsDropDown(layout_orderby);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK)
            return;
        switch (requestCode){
            case R.id.layout:
                page_notice = 0;
                getNoticeList();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
