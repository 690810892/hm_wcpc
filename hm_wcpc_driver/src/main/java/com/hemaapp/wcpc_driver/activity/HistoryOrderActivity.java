package com.hemaapp.wcpc_driver.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_driver.BaseActivity;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.adapter.HistoryOrderListViewPagerAdapter;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.User;
import com.hemaapp.wcpc_driver.view.ButtonDialog;

import java.util.ArrayList;

/**
 * Created by WangYuxia on 2016/5/27.
 * 历史订单
 */
public class HistoryOrderActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private TabLayout tabLayout;
    private ViewPager pager;
    private HistoryOrderListViewPagerAdapter adapter;

    private int position = 0;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_historyorder);
        super.onCreate(savedInstanceState);
        user = hm_WcpcDriverApplication.getInstance().getUser();
        init();
    }

    private void init(){
        pager.setOffscreenPageLimit(100);
        adapter = new HistoryOrderListViewPagerAdapter(this, getParams(), position);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);//将TabLayout和ViewPager关联起来。
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//设置tab模式，可以滚动);
        pager.setCurrentItem(position);
    }

    private ArrayList<HistoryOrderListViewPagerAdapter.Params> getParams(){
        ArrayList<HistoryOrderListViewPagerAdapter.Params> list = new ArrayList<>();
        list.add(0,new HistoryOrderListViewPagerAdapter.Params("0")); //全部
        list.add(1,new HistoryOrderListViewPagerAdapter.Params("1")); //已取消
        list.add(2,new HistoryOrderListViewPagerAdapter.Params("2")); //待评价
        list.add(3,new HistoryOrderListViewPagerAdapter.Params("3")); //已完成
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
    }

    @Override
    protected void setListener() {
        title.setText("历史订单");
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

    private ButtonDialog mDialog;

    private void delete(){
        if (mDialog == null) {
            mDialog = new ButtonDialog(mContext);
            mDialog.setLeftButtonText("取消");
            mDialog.setRightButtonText("确定");
            mDialog.setText("您确定清空订单?一旦清空无法找回");
            mDialog.setButtonListener(new ButtonListener());
            mDialog.setRightButtonTextColor(mContext.getResources().getColor(R.color.yellow));
        }
        mDialog.show();
    }

    private class ButtonListener implements ButtonDialog.OnButtonListener {

        @Override
        public void onLeftButtonClick(ButtonDialog dialog) {
            dialog.cancel();
        }

        @Override
        public void onRightButtonClick(ButtonDialog dialog) {
            dialog.cancel();
            getNetWorker().orderOperate(user.getToken(), "3", "0", "", "");
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
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}