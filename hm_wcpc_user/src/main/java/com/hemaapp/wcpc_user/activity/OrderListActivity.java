package com.hemaapp.wcpc_user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.dialog.HemaButtonDialog;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.hm_FrameWork.view.RefreshLoadmoreLayout;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.adapter.MyOrderListAdapter;
import com.hemaapp.wcpc_user.adapter.MyTripsViewPagerAdapter;
import com.hemaapp.wcpc_user.adapter.OrderListViewPagerAdapter;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.OrderListInfor;
import com.hemaapp.wcpc_user.module.User;

import java.util.ArrayList;

import xtom.frame.util.XtomToastUtil;
import xtom.frame.view.XtomListView;
import xtom.frame.view.XtomRefreshLoadmoreLayout;

/**
 * Created by WangYuxia on 2016/5/18.
 * 订单列表
 */
public class OrderListActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private TabLayout tabLayout;
    private ViewPager pager;
    private OrderListViewPagerAdapter adapter;
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_orderlist);
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        pager.setOffscreenPageLimit(100);
        adapter = new OrderListViewPagerAdapter(this, getParams(), position);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);//将TabLayout和ViewPager关联起来。
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//设置tab模式，可以滚动);
        pager.setCurrentItem(position);
    }

    private ArrayList<OrderListViewPagerAdapter.Params> getParams(){
        ArrayList<OrderListViewPagerAdapter.Params> list = new ArrayList<>();
        list.add(0,new OrderListViewPagerAdapter.Params("0")); //全部
        list.add(1,new OrderListViewPagerAdapter.Params("1")); //待支付
        list.add(2,new OrderListViewPagerAdapter.Params("2")); //未出行
        list.add(2,new OrderListViewPagerAdapter.Params("3")); //已取消
        list.add(2,new OrderListViewPagerAdapter.Params("4")); //待评价
        list.add(2,new OrderListViewPagerAdapter.Params("5")); //已完成
        return list;
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case ORDER_OPERATE:
                showProgressDialog("请稍后...");
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case ORDER_OPERATE:
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
            case ORDER_OPERATE:
                position = pager.getCurrentItem();
                init();
                break;
        }
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
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
            case ORDER_OPERATE:
                showTextDialog("删除失败，请稍后重试");
                break;
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        right = (TextView) findViewById(R.id.title_btn_right);
        title = (TextView) findViewById(R.id.title_text);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        pager = (ViewPager) findViewById(R.id.viewpager);
    }

    @Override
    protected void getExras() {
        position = mIntent.getIntExtra("position", 0);
    }

    @Override
    protected void setListener() {
        title.setText("我的订单");
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        right.setText("清空");
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
    }

    private HemaButtonDialog mDialog;

    public void delete(){
        if (mDialog == null) {
            mDialog = new HemaButtonDialog(mContext);
            mDialog.setLeftButtonText("取消");
            mDialog.setRightButtonText("确定");
            mDialog.setText("确定要清空所有订单?");
            mDialog.setButtonListener(new ButtonListener());
            mDialog.setRightButtonTextColor(mContext.getResources().getColor(R.color.yellow));
        }
        mDialog.show();
    }

    private class ButtonListener implements HemaButtonDialog.OnButtonListener {

        @Override
        public void onLeftButtonClick(HemaButtonDialog dialog) {
            dialog.cancel();
        }

        @Override
        public void onRightButtonClick(HemaButtonDialog dialog) {
            dialog.cancel();
            User user = hm_WcpcUserApplication.getInstance().getUser();
            getNetWorker().orderOperate(user.getToken(), "5", "0", "", "");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case R.id.layout://去支付
            case R.id.layout_0: //去订单详情界面返回的数据;
            case R.id.layout_1:
                position = pager.getCurrentItem();
                init();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
