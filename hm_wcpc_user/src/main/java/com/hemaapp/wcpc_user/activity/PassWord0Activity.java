package com.hemaapp.wcpc_user.activity;

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
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 密码管理
 */
public class PassWord0Activity extends BaseActivity {

    @BindView(R.id.title_btn_left)
    ImageView titleBtnLeft;
    @BindView(R.id.title_btn_right)
    TextView titleBtnRight;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.title)
    LinearLayout title;
    @BindView(R.id.tv_password)
    TextView tvPassword;
    @BindView(R.id.lv_paypassword)
    LinearLayout lvPaypassword;
    @BindView(R.id.lv_set_paypassword)
    LinearLayout lvSetPaypassword;
    @BindView(R.id.lv_forget_password)
    LinearLayout lvForgetPassword;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_password0);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
        user = hm_WcpcUserApplication.getInstance().getUser();
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
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_GET:
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
                if (isNull(user.getPaypassword())){
                    lvForgetPassword.setVisibility(View.GONE);
                    lvPaypassword.setVisibility(View.GONE);
                    lvSetPaypassword.setVisibility(View.VISIBLE);
                }else {
                    lvForgetPassword.setVisibility(View.VISIBLE);
                    lvPaypassword.setVisibility(View.VISIBLE);
                    lvSetPaypassword.setVisibility(View.GONE);
                }
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
    }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
        titleText.setText("密码管理");
        titleBtnRight.setText("");
    }


    @OnClick({R.id.title_btn_left, R.id.tv_password, R.id.lv_paypassword, R.id.lv_set_paypassword, R.id.lv_forget_password})
    public void onViewClicked(View view) {
        Intent it;
        switch (view.getId()) {
            case R.id.title_btn_left:
                finish();
                break;
            case R.id.tv_password:
                it = new Intent(mContext, ChangePwdActivity.class);
                it.putExtra("keytype", "1");
                startActivity(it);
                break;
            case R.id.lv_paypassword:
                it = new Intent(mContext, ChangePwdActivity.class);
                it.putExtra("keytype", "2");
                startActivity(it);
                break;
            case R.id.lv_set_paypassword:
                it = new Intent(mContext, ResetPayPasswordActivity.class);
                it.putExtra("keytype", "1");
                startActivity(it);
                break;
            case R.id.lv_forget_password:
                it = new Intent(mContext, ResetPayPasswordActivity.class);
                it.putExtra("keytype", "2");
                startActivity(it);
                break;
        }
    }
}
