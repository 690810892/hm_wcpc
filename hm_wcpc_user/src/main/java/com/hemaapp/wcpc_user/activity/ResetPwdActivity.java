package com.hemaapp.wcpc_user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.R;

import xtom.frame.util.XtomSharedPreferencesUtil;

/**
 * Created by WangYuxia on 2016/5/5.
 * 重设密码 -- 找回密码
 * type = 1:设置登录密码
 * type = 2:找回支付/提现密码
 */
public class ResetPwdActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private EditText edit_pwd;
    private ImageView image_clear;
    private EditText edit_pwd_again;
    private ImageView image_reclear;
    private TextView button;

    private String temp_token;
    private String password;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_resetpwd);
        super.onCreate(savedInstanceState);
        if("1".equals(type))
            title.setText("设置密码");
        else {
            title.setText("设置支付密码");
        }
    }

    @Override
    protected void getExras() {
        temp_token = mIntent.getStringExtra("tempToken");
        type = mIntent.getStringExtra("keytype");
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        title = (TextView) findViewById(R.id.title_text);
        right = (TextView) findViewById(R.id.title_btn_right);

        edit_pwd = (EditText) findViewById(R.id.edittext);
        image_clear = (ImageView) findViewById(R.id.imageview);
        edit_pwd_again = (EditText) findViewById(R.id.edittext_0);
        image_reclear = (ImageView) findViewById(R.id.imageview_0);
        button = (TextView) findViewById(R.id.button);
    }

    @Override
    protected void setListener() {
        left.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        right.setVisibility(View.INVISIBLE);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                password = edit_pwd.getText().toString();
                String repeat = edit_pwd_again.getText().toString();
                if(isNull(password)){
                    showTextDialog("请输入密码");
                    return;
                }

                if(isNull(repeat)){
                    showTextDialog("请输入确认密码");
                    return;
                }

                if(!password.equals(repeat)){
                    showTextDialog("新密码与确认密码不一致，请重新填写");
                    return;
                }

                if(!(password.length() >= 6 && password.length() <= 12)){
                    showTextDialog("抱歉，密码长度为6-12位");
                    return;
                }
                getNetWorker().passwordReset(temp_token, type, "1", password);
            }
        });
        edit_pwd.addTextChangedListener(new OnTextChangeListener(0));
        edit_pwd_again.addTextChangedListener(new OnTextChangeListener(1));

        image_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_pwd.setText("");
            }
        });
        image_reclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_pwd_again.setText("");
            }
        });
    }

    private class OnTextChangeListener implements TextWatcher {
        int type;
        public OnTextChangeListener(int type){
            this.type = type;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if(type == 0){
                if(count > 0)
                    image_clear.setVisibility(View.VISIBLE);
                else
                    image_clear.setVisibility(View.GONE);
            }else if(type == 1){
                if(count > 0)
                    image_reclear.setVisibility(View.VISIBLE);
                else
                    image_reclear.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            checkNextable();
        }
    }

    private void checkNextable() {
        password = edit_pwd.getText().toString();
        String repeat = edit_pwd_again.getText().toString();

        boolean c = !isNull(password) && password.equals(repeat);
        if (c) {
            button.setEnabled(true);
        } else {
            button.setEnabled(false);
        }
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case PASSWORD_RESET:
                showProgressDialog("正在重设密码...");
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
            case PASSWORD_RESET:
                cancelProgressDialog();
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForServerSuccess(HemaNetTask netTask,
                                            HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case PASSWORD_RESET:
                if("1".equals(type))
                    XtomSharedPreferencesUtil.save(mContext, "password", password);
                showTextDialog("设置密码成功");
                title.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if("1".equals(type)){
                            Intent it = new Intent(mContext, LoginActivity.class);
                            startActivity(it);
                            finish();
                        }else {
                            finish();
                        }
                    }
                }, 1000);
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
            case PASSWORD_RESET:
                showTextDialog(baseResult.getMsg());
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
            case PASSWORD_RESET:
                showTextDialog("重设密码失败,请稍后重试");
                break;
            default:
                break;
        }
    }
}
