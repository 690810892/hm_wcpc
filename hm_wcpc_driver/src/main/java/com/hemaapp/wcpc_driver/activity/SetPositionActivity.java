package com.hemaapp.wcpc_driver.activity;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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
import com.hemaapp.wcpc_driver.BaseActivity;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.User;

/**
 * Created by wangyuxia on 2017/9/25.
 * 常用位置设置
 */
public class SetPositionActivity extends BaseActivity{

    private ImageView left;
    private TextView title;
    private TextView right;

    private LinearLayout layout_distance;
    private TextView tv_distance;

    private User user;
    private String distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_set_position);
        super.onCreate(savedInstanceState);
        user = hm_WcpcDriverApplication.getInstance().getUser();
        tv_distance.setText(isNull(user.getMylength())?"默认" : (user.getMylength()+"千米"));
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask hemaNetTask) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case MY_LENGTH:
                showProgressDialog("正在保存");
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask hemaNetTask) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case MY_LENGTH:
                cancelProgressDialog();
                break;
        }
    }

    @Override
    protected void callBackForServerSuccess(HemaNetTask hemaNetTask, HemaBaseResult hemaBaseResult) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case MY_LENGTH:
                showTextDialog(hemaBaseResult.getMsg());
                tv_distance.setText(distance);
                user.setMylength(distance);
                getApplicationContext().setUser(user);
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask hemaNetTask, int i) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case MY_LENGTH:
                showTextDialog("操作失败，请稍后重试");
                break;
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        title = (TextView) findViewById(R.id.title_text);
        right = (TextView) findViewById(R.id.title_btn_right);

        layout_distance = (LinearLayout) findViewById(R.id.layout_distance);
        tv_distance = (TextView) findViewById(R.id.tv_distance);
    }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
        title.setText("常用位置");
        right.setVisibility(View.INVISIBLE);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        layout_distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopWindow();
            }
        });
    }

    private PopupWindow mWindow;
    private ViewGroup mViewGroup;
    private EditText editText;
    private TextView tv_cancel;
    private TextView tv_ok;

    private void showPopWindow(){
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
                R.layout.pop_set_distance, null);
        editText = (EditText) mViewGroup.findViewById(R.id.edittext);
        tv_cancel = (TextView) mViewGroup.findViewById(R.id.textview_0);
        tv_ok = (TextView) mViewGroup.findViewById(R.id.textview_1);
        mWindow.setContentView(mViewGroup);
        mWindow.showAtLocation(mViewGroup, Gravity.CENTER, 0, 0);

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
            }
        });
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
                distance = editText.getText().toString();
                if(isNull(distance)){
                    showTextDialog("抱歉，请输入距离");
                    return;
                }
                getNetWorker().myLength(user.getToken(), distance);
            }
        });
    }
}
