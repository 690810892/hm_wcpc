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
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_driver.BaseActivity;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.User;

/**
 * Created by WangYuxia on 2016/6/16.
 * 我的账户
 */
public class MyAccountActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private TextView text_feeaccount;
    private TextView text_tixian; //提现

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_myaccount);
        super.onCreate(savedInstanceState);
        user = hm_WcpcDriverApplication.getInstance().getUser();
    }

    @Override
    protected void onResume() {
        getNetWorker().clientGet(user.getToken(), user.getId());
        super.onResume();
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_GET:
                showProgressDialog("请稍后...");
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_GET:
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
            case CLIENT_GET:
                HemaArrayResult<User> uResult = (HemaArrayResult<User>) baseResult;
                user = uResult.getObjects().get(0);
                text_feeaccount.setText((isNull(user.getFeeaccount())?"0":user.getFeeaccount())+"元");
                break;
        }
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_GET:
                showTextDialog(baseResult.getMsg());
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_GET:
                showTextDialog("抱歉，获取数据失败");
                break;
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        right = (TextView) findViewById(R.id.title_btn_right);
        title = (TextView) findViewById(R.id.title_text);

        text_feeaccount = (TextView) findViewById(R.id.textview_1);
        text_tixian = (TextView) findViewById(R.id.textview_3);
    }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
        title.setText("我的钱包");
        right.setVisibility(View.INVISIBLE);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setListener(text_tixian);

    }

    private void setListener(View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.textview_3:
                        showSelectPopWindow();
                        break;
                }
            }
        });
    }

    private PopupWindow mWindow;
    private ViewGroup mViewGroup;
    private TextView boy;
    private TextView girl;
    private TextView cancel;

    private void showSelectPopWindow(){
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
                R.layout.pop_sex, null);
        boy = (TextView) mViewGroup.findViewById(R.id.textview);
        girl = (TextView) mViewGroup.findViewById(R.id.textview_0);
        cancel = (TextView) mViewGroup.findViewById(R.id.textview_2);
        mWindow.setContentView(mViewGroup);
        mWindow.showAtLocation(mViewGroup, Gravity.CENTER, 0, 0);
        boy.setText("支付宝");
        girl.setText("银行卡");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
            }
        });

        boy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
                Intent it = new Intent(mContext, CashAddByAlipayActivity.class);
                startActivity(it);
            }
        });

        girl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
                Intent it = new Intent(mContext, CashAddByUnionpayActivity.class);
                startActivity(it);
            }
        });
    }
}
