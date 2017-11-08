package com.hemaapp.wcpc_user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.gyf.barlibrary.ImmersionBar;
import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.BaseNetWorker;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.module.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xtom.frame.util.XtomSharedPreferencesUtil;

/**
 */
public class IntroductionActivity extends BaseActivity {

    @BindView(R.id.iv_image)
    ImageView ivImage;
    private int indext = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_introduction);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.reset().init();
        ivImage.setImageResource(R.mipmap.intro_1);
    }


    @Override
    protected void onDestroy() {
        XtomSharedPreferencesUtil.save(mContext, "isFirst", "false"); //
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
    }

    private void toLogin() {
        Intent it = new Intent(mContext, LoginActivity.class);
        startActivity(it);
    }

    @Override
    protected void findView() {
    }

    @Override
    protected void getExras() {

    }

    @Override
    protected boolean onKeyBack() {
        return false;
    }

    @Override
    protected boolean onKeyMenu() {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void setListener() {

    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {

    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {

    }

    @SuppressWarnings("unchecked")
    @Override
    protected void callBackForServerSuccess(HemaNetTask netTask,
                                            HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_LOGIN:
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_LOGIN:
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_LOGIN:
                break;
            default:
                break;
        }
    }

    @OnClick(R.id.iv_image)
    public void onViewClicked() {
        if (indext==0){
            ivImage.setImageResource(R.mipmap.intro_2);
            indext=1;
        }else  if (indext==1){
            ivImage.setImageResource(R.mipmap.intro_3);
            indext=2;
        }else  if (indext==2){
            ivImage.setImageResource(R.mipmap.intro_4);
            indext=3;
        }else  if (indext==3){
            finish();
        }
    }
}
