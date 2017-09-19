package com.hemaapp.wcpc_user.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.dialog.HemaButtonDialog;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.adapter.MyTripsViewPagerAdapter;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.User;

import java.util.ArrayList;

/**
 * Created by WangYuxia on 2016/5/19.
 * 我发布的行程
 */
public class MyTripsListActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private TabLayout tabLayout;
    private ViewPager pager;
    private MyTripsViewPagerAdapter adapter;

    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_mytrips);
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        pager.setOffscreenPageLimit(100);
        adapter = new MyTripsViewPagerAdapter(this, getParams(), position);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);//将TabLayout和ViewPager关联起来。
        tabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，可以滚动);
        pager.setCurrentItem(position);
    }

    private ArrayList<MyTripsViewPagerAdapter.Params> getParams(){
        ArrayList<MyTripsViewPagerAdapter.Params> list = new ArrayList<>();
        list.add(0,new MyTripsViewPagerAdapter.Params("0")); //全部
        list.add(1,new MyTripsViewPagerAdapter.Params("1")); //未出行
        list.add(2,new MyTripsViewPagerAdapter.Params("2")); //已完成
        return list;
    }

    private HemaButtonDialog mDialog;

    public void delete(){
        if (mDialog == null) {
            mDialog = new HemaButtonDialog(mContext);
            mDialog.setLeftButtonText("取消");
            mDialog.setRightButtonText("确定");
            mDialog.setText("确定要清空所有行程?");
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
            getNetWorker().myTripsOperate(user.getToken(), "1", "2", "0");
        }
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case MY_TRIPS_OPERATE:
                showProgressDialog("请稍后...");
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case MY_TRIPS_OPERATE:
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
            case MY_TRIPS_OPERATE:
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
            case MY_TRIPS_OPERATE:
                showTextDialog(baseResult.getMsg());
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case MY_TRIPS_OPERATE:
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
        title.setText("我的行程");
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
}
