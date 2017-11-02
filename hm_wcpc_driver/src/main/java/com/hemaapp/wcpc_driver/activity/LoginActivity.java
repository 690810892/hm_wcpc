package com.hemaapp.wcpc_driver.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_driver.BaseActivity;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.SysInitInfo;
import com.hemaapp.wcpc_driver.module.User;
import com.hemaapp.wcpc_driver.view.ClearEditText;

import xtom.frame.util.XtomSharedPreferencesUtil;

/**
 * Created by WangYuxia on 2016/4/21.
 * 登录界面
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private ImageView title_left;

    private EditText edit_username;
    private ImageView img_username_clear;
    private EditText edit_password;
    private ImageView img_password_clear;

    private LinearLayout view_remember;
    private ImageView image_remember;
    private TextView text_login; // 登录
    private int flag = 0; // 表示未记住密码
    private TextView text_forgetpwd;
    private TextView text_phone;

    private String username, password, phone, keytype = "1"; //1：密码不可见， 2:密码可见;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        super.onCreate(savedInstanceState);

        String isRemember = XtomSharedPreferencesUtil.get(mContext, "isRemembered");
        username = XtomSharedPreferencesUtil.get(mContext, "username");
        password = XtomSharedPreferencesUtil.get(mContext, "password");
        if("true".equals(isRemember)){
            flag = 1;
            image_remember.setImageResource(R.mipmap.img_checkbox_s);
        } else {
            flag = 0;
            image_remember.setImageResource(R.mipmap.img_checkbox_n);
        }

        if(flag == 1){
            if(!isNull(username)&& !isNull(password)){
                edit_username.setText(username);
                edit_username.setSelection(username.length());
                edit_password.setText(password);
                edit_password.setSelection(password.length());
            }
        }else {
            if(!isNull(username)){
                edit_username.setText(username);
                edit_username.setSelection(username.length());
                edit_password.setText("");
            }
        }

        SysInitInfo sysInitInfo = hm_WcpcDriverApplication.getInstance().getSysInitInfo();
        phone = sysInitInfo.getSys_service_phone();
        text_phone.setText(phone);
        text_phone.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
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
                hm_WcpcDriverApplication.getInstance().setUser(user);
                XtomSharedPreferencesUtil.save(mContext, "username", username);
                XtomSharedPreferencesUtil.save(mContext, "password", password);
                XtomSharedPreferencesUtil.save(mContext, "isAutoLogin", "true");
                XtomSharedPreferencesUtil.save(mContext, "loginflag", user.getLoginflag());

                Intent it = new Intent(this, MainNewActivity.class);
                startActivity(it);
                finish();
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
        title_left = (ImageView) findViewById(R.id.title_btn_left);
        edit_username = (EditText) findViewById(R.id.clear_username);
        img_username_clear = (ImageView) findViewById(R.id.imageview_2);
        edit_password = (EditText) findViewById(R.id.clear_password);
        img_password_clear = (ImageView) findViewById(R.id.imageview_3);

        view_remember = (LinearLayout) findViewById(R.id.view);
        image_remember = (ImageView) findViewById(R.id.imageview_4);
        text_login = (TextView) findViewById(R.id.button);
        text_forgetpwd = (TextView) findViewById(R.id.textview);
        text_phone = (TextView) findViewById(R.id.textview_1);
    }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
        title_left.setOnClickListener(this);
        img_username_clear.setOnClickListener(this);
        img_password_clear.setOnClickListener(this);
        text_login.setOnClickListener(this);
        view_remember.setOnClickListener(this);
        text_forgetpwd.setOnClickListener(this);
        text_phone.setOnClickListener(this);

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

    private PopupWindow mWindow;
    private ViewGroup mViewGroup;
    private TextView content1;
    private TextView content2;
    private TextView ok;
    private TextView cancel;

    private void showPhoneWindow() {
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
                R.layout.pop_phone, null);
        content1 = (TextView) mViewGroup.findViewById(R.id.textview);
        content2 = (TextView) mViewGroup.findViewById(R.id.textview_0);
        cancel = (TextView) mViewGroup.findViewById(R.id.textview_1);
        ok = (TextView) mViewGroup.findViewById(R.id.textview_2);
        mWindow.setContentView(mViewGroup);
        mWindow.showAtLocation(mViewGroup, Gravity.CENTER, 0, 0);
        content1.setText("拨打乘客电话");
        content2.setText(phone);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
                //Intent.ACTION_CALL 直接拨打电话，就是进入拨打电话界面，电话已经被拨打出去了。
                //Intent.ACTION_DIAL 是进入拨打电话界面，电话号码已经输入了，但是需要人为的按拨打电话键，才能播出电话。
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                        + phone));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent it;
        switch (view.getId()){
            case R.id.title_btn_left: //左键
                finish();
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

                if(isNull(username)){
                    showTextDialog("请填写账号");
                    return;
                }

                if(isNull(password)){
                    showTextDialog("请填写密码");
                    return;
                }

                if(password.length() < 6 || password.length() > 20){
                    showTextDialog("密码长度为6-20位");
                    return;
                }

                getNetWorker().clientLogin(username, password);
                break;
            case R.id.textview_1:
                showPhoneWindow();
                break;
        }
    }
}
