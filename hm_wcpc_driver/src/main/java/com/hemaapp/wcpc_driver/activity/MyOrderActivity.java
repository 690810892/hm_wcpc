package com.hemaapp.wcpc_driver.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

    private RefreshLoadmoreLayout layout;
    private XtomListView mListView;
    private ProgressBar progressBar;

    private ArrayList<OrderListInfor> orders = new ArrayList<>();
    private MyOrderListAdapter adapter_notice;
    private int page_notice = 0;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_person_order);
        super.onCreate(savedInstanceState);
        user = hm_WcpcDriverApplication.getInstance().getUser();
        getNoticeList();
    }

    private void getNoticeList(){
        getNetWorker().driverOrderList(user.getToken(), "4", page_notice);
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
