package com.hemaapp.wcpc_user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.User;
import com.hemaapp.wcpc_user.view.ClearEditText;

import xtom.frame.util.XtomSharedPreferencesUtil;

/**
 * Created by WangYuxia on 2016/4/21.
 * 登录界面
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private ImageView title_left;
    private TextView title_right;

    private EditText edit_username;
    private ImageView img_username_clear;
    private EditText edit_password;
    private ImageView img_password_clear;

    private LinearLayout view_remember;
    private ImageView image_remember;
    private TextView text_login; // 登录
    private int flag = 0; // 表示未记住密码
    private TextView text_forgetpwd;

    private String username, password, keytype = "1"; //1：密码不可见， 2:密码可见

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        super.onCreate(savedInstanceState);

        String isRemember = XtomSharedPreferencesUtil.get(mContext, "isRemembered");
        username = XtomSharedPreferencesUtil.get(mContext, "username");
        password = XtomSharedPreferencesUtil.get(mContext, "password");
        if ("true".equals(isRemember)) {
            flag = 1;
            image_remember.setImageResource(R.mipmap.img_checkbox_s);
        } else {
            flag = 0;
            image_remember.setImageResource(R.mipmap.img_checkbox_n);
        }

        if (flag == 1) {
            if (!isNull(username) && !isNull(password)) {
                edit_username.setText(username);
                edit_username.setSelection(username.length());
                edit_password.setText(password);
                edit_password.setSelection(password.length());
            }
        } else {
            if (!isNull(username)) {
                edit_username.setText(username);
                edit_username.setSelection(username.length());
                edit_password.setText("");
            }
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case CLIENT_LOGIN:
                cancelProgressDialog();
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedtype) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case CLIENT_LOGIN:
                showTextDialog("登录失败");
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void callBackForServerSuccess(HemaNetTask netTask,
                                            HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case CLIENT_LOGIN:
                HemaArrayResult<User> uResult = (HemaArrayResult<User>) baseResult;
                User user = uResult.getObjects().get(0);
                hm_WcpcUserApplication.getInstance().setUser(user);
                XtomSharedPreferencesUtil.save(mContext, "username", username);
                XtomSharedPreferencesUtil.save(mContext, "password", password);
                XtomSharedPreferencesUtil.save(mContext, "isAutoLogin", "true");

                Intent it = new Intent(this, MainNewActivity.class);
                startActivity(it);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case CLIENT_LOGIN:
                showTextDialog(baseResult.getMsg());
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case CLIENT_LOGIN:
                showProgressDialog("请稍后...");
                break;
            default:
                break;
        }
    }

    @Override
    protected void findView() {
        title_right = (TextView) findViewById(R.id.title_btn_right);
        title_left = (ImageView) findViewById(R.id.title_btn_left);
        edit_username = (EditText) findViewById(R.id.clear_username);
        img_username_clear = (ImageView) findViewById(R.id.imageview_2);
        edit_password = (EditText) findViewById(R.id.clear_password);
        img_password_clear = (ImageView) findViewById(R.id.imageview_3);

        view_remember = (LinearLayout) findViewById(R.id.view);
        image_remember = (ImageView) findViewById(R.id.imageview_4);
        text_login = (TextView) findViewById(R.id.button);
        text_forgetpwd = (TextView) findViewById(R.id.textview);
    }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
        title_left.setOnClickListener(this);
        title_right.setOnClickListener(this);
        img_password_clear.setOnClickListener(this);
        img_username_clear.setOnClickListener(this);
        text_login.setOnClickListener(this);
        view_remember.setOnClickListener(this);
        text_forgetpwd.setOnClickListener(this);

        edit_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().length() > 0)
                    img_username_clear.setVisibility(View.VISIBLE);
                else
                    img_username_clear.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent it;
        switch (view.getId()) {
            case R.id.title_btn_left: //左键
                finish();
                break;
            case R.id.title_btn_right: //右键 注册
                it = new Intent(mContext, RegistorStepOneActivity.class);
                startActivity(it);
                break;
            case R.id.imageview_2: //清空用户名的输入
                edit_username.setText("");
                break;
            case R.id.imageview_3: //清空密码框的输入
                if(keytype.equals("1")){ //不可见
                    edit_password.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
                    img_password_clear.setImageResource(R.mipmap.img_eye_open);
                    keytype = "2";
                }else{
                    edit_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    img_password_clear.setImageResource(R.mipmap.img_eye_close);
                    keytype = "1";
                }
                break;
            case R.id.view: //记住密码
                if (flag == 0) {
                    flag = 1;
                    image_remember
                            .setImageResource(R.mipmap.img_checkbox_s);
                    XtomSharedPreferencesUtil.save(mContext, "isRemembered",
                            "true");
                } else {
                    flag = 0;
                    image_remember.setImageResource(R.mipmap.img_checkbox_n);
                    XtomSharedPreferencesUtil.save(mContext, "isRemembered",
                            "false");
                }
                break;
            case R.id.textview: //忘记密码
                it = new Intent(mContext, FindBackPwdActivity.class);
                it.putExtra("type", "1");
                startActivity(it);
                break;
            case R.id.button:
                username = edit_username.getText().toString();
                password = edit_password.getText().toString();

                if (isNull(username)) {
                    showTextDialog("请填写账号");
                    return;
                }

                if (isNull(password)) {
                    showTextDialog("请填写密码");
                    return;
                }

                if (password.length() < 6 || password.length() > 12) {
                    showTextDialog("密码长度为6-12位");
                    return;
                }

                getNetWorker().clientLogin(username, password);
                break;
        }
    }
}
