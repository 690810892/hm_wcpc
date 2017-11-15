package com.hemaapp.wcpc_user.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.ClientAdd;
import com.hemaapp.wcpc_user.module.SysInitInfo;
import com.hemaapp.wcpc_user.module.User;
import com.hemaapp.wcpc_user.view.ClearEditText;

import xtom.frame.util.XtomSharedPreferencesUtil;

/**
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

    private LinearLayout lvName;
    private LinearLayout lvphone;
    private ClearEditText ev_phone;
    private EditText ev_code;
    private TextView sendButton;
    private LinearLayout secondLayout;
    private TextView secondTextView;
    private RadioGroup type;

    private String username, password, keytype = "1", phone, username2; //1：密码不可见， 2:密码可见
    private TextView text_phone;
    private int loginFlag = 1;
    private TimeThread timeThread;
    private String login_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        super.onCreate(savedInstanceState);
        String login_type = XtomSharedPreferencesUtil.get(mContext, "login_type");
        if (isNull(login_type))
            login_type = "1";
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
        if (login_type.equals("1")) {
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
        }else {
            if (!isNull(username)) {
                ev_phone.setText(username);
            }
        }
        SysInitInfo sysInitInfo = hm_WcpcUserApplication.getInstance().getSysInitInfo();
        phone = sysInitInfo.getSys_service_phone();
        text_phone.setText(phone);
        text_phone.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case CLIENT_LOGIN:
            case CODE_GET:
            case CODE_VERIFY:
            case CLIENT_LOGIN_BYVERIFYCODE:
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
            case CODE_GET:
                showTextDialog("获取验证码失败");
                break;
            case CODE_VERIFY:
                showTextDialog("验证随机码失败");
                break;
            case CLIENT_LOGIN_BYVERIFYCODE:
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
                XtomSharedPreferencesUtil.save(mContext, "login_type", "1");
                XtomSharedPreferencesUtil.save(mContext, "username", username);
                XtomSharedPreferencesUtil.save(mContext, "password", password);
                XtomSharedPreferencesUtil.save(mContext, "isAutoLogin", "true");

                Intent it = new Intent(this, MainNewActivity.class);
                startActivity(it);
                finish();
                break;
            case CODE_GET:
                username2 = netTask.getParams().get("username");
                timeThread = new TimeThread(new TimeHandler(this));
                timeThread.start();
                break;
            case CODE_VERIFY:
                HemaArrayResult<String> sResult = (HemaArrayResult<String>) baseResult;
                String tempToken = sResult.getObjects().get(0);
                getNetWorker().loginByPhone(tempToken, username2);
                break;
            case CLIENT_LOGIN_BYVERIFYCODE:
                HemaArrayResult<User> cResult = (HemaArrayResult<User>) baseResult;
                User user1 = cResult.getObjects().get(0);
                hm_WcpcUserApplication.getInstance().setUser(user1);
                XtomSharedPreferencesUtil.save(mContext, "login_type", "2");
                XtomSharedPreferencesUtil.save(mContext, "username", username2);
                XtomSharedPreferencesUtil.save(mContext, "password", user1.getPassword());
                XtomSharedPreferencesUtil.save(mContext, "isAutoLogin", "true");
                if (user1.getIs_reg().equals("1")) {
                    showCouponWindow();
                } else {
                    Intent it1 = new Intent(this, MainNewActivity.class);
                    startActivity(it1);
                    finish();
                }
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
            case CLIENT_LOGIN_BYVERIFYCODE:
                showTextDialog(baseResult.getMsg());
                break;
            case CODE_GET:
                showTextDialog(baseResult.getMsg());
                break;
            case CODE_VERIFY:
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
            case CLIENT_LOGIN_BYVERIFYCODE:
                showProgressDialog("请稍后...");
                break;
            case CODE_GET:
                showProgressDialog("正在获取验证码");
                break;
            case CODE_VERIFY:
                showProgressDialog("正在验证随机码");
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
        text_phone = (TextView) findViewById(R.id.textview_1);

        lvName = (LinearLayout) findViewById(R.id.lv_name);
        lvphone = (LinearLayout) findViewById(R.id.lv_phone);
        ev_phone = (ClearEditText) findViewById(R.id.ev_phone);
        ev_code = (EditText) findViewById(R.id.ev_code);
        secondTextView = (TextView) findViewById(R.id.second);
        secondLayout = (LinearLayout) findViewById(R.id.linearlayout);
        sendButton = (TextView) findViewById(R.id.sendcode);
        type = (RadioGroup) findViewById(R.id.type);
    }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
        sendButton.setOnClickListener(new SendButtonListener());
        type.setOnCheckedChangeListener(new TypeListener());
        title_left.setOnClickListener(this);
        title_right.setOnClickListener(this);
        img_password_clear.setOnClickListener(this);
        img_username_clear.setOnClickListener(this);
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
                if (charSequence.toString().length() > 0)
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
                if (keytype.equals("1")) { //不可见
                    edit_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    img_password_clear.setImageResource(R.mipmap.img_eye_open);
                    keytype = "2";
                } else {
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
                if (loginFlag == 1) {
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
                    XtomSharedPreferencesUtil.save(mContext, "login_type", "1");
                    getNetWorker().clientLogin(username, password);
                } else {
                    String un = ev_phone.getText().toString();
                    if (isNull(un)) {
                        showTextDialog("请填写手机号");
                        return;
                    }
                    if (isNull(username2)) {
                        showTextDialog("请获取验证码");
                        return;
                    }
                    if (!username2.equals(un)) {
                        showTextDialog("抱歉，当前手机号与获取\n验证码的手机号不同,请重新获取");
                        return;
                    }
                    String code = ev_code.getText().toString();
                    if (isNull(code)) {
                        showTextDialog("抱歉，请输入验证码");
                        return;
                    }
                    getNetWorker().codeVerify(username2, code);
                }
                break;
            case R.id.textview_1:
                showPhoneWindow();
                break;
        }
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
        content1.setText("拨打客服电话");
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

    private class TypeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rbt0://
                    loginFlag = 1;
                    lvName.setVisibility(View.VISIBLE);
                    view_remember.setVisibility(View.VISIBLE);
                    lvphone.setVisibility(View.GONE);
                    text_forgetpwd.setVisibility(View.VISIBLE);
                    break;
                case R.id.rbt1://
                    loginFlag = 2;
                    lvphone.setVisibility(View.VISIBLE);
                    lvName.setVisibility(View.GONE);
                    view_remember.setVisibility(View.GONE);
                    text_forgetpwd.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private class SendButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String username = ev_phone.getText().toString();
            if (isNull(username)) {
                showTextDialog("请输入手机号");
                return;
            }

            String mobile = "^[1][3-8]+\\d{9}";
            if (!username.matches(mobile)) {
                showTextDialog("您输入的手机号不正确");
                return;
            }

            secondLayout.setVisibility(View.VISIBLE);
            sendButton.setVisibility(View.GONE);
            getNetWorker().codeGet(username); //获取短信验证码
        }
    }

    private class TimeThread extends Thread {
        private int curr;

        private TimeHandler timeHandler;

        public TimeThread(TimeHandler timeHandler) {
            this.timeHandler = timeHandler;
        }

        void cancel() {
            curr = 0;
        }

        @Override
        public void run() {
            curr = 60;
            while (curr > 0) {
                timeHandler.sendEmptyMessage(curr);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                curr--;
            }
            timeHandler.sendEmptyMessage(-1);
        }
    }

    private static class TimeHandler extends Handler {
        LoginActivity activity;

        public TimeHandler(LoginActivity activity) {
            this.activity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -1:
                    activity.sendButton.setText("重新发送");
                    activity.sendButton.setVisibility(View.VISIBLE);
                    activity.secondLayout.setVisibility(View.GONE);
                    break;
                default:
                    activity.sendButton.setVisibility(View.GONE);
                    activity.secondTextView.setText("" + msg.what);
                    activity.secondLayout.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private void showCouponWindow() {
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
                R.layout.pop_couple, null);
        TextView count = (TextView) mViewGroup.findViewById(R.id.tv_count);
        TextView price = (TextView) mViewGroup.findViewById(R.id.tv_price);
        TextView price2 = (TextView) mViewGroup.findViewById(R.id.tv_price2);
        TextView tv_time = (TextView) mViewGroup.findViewById(R.id.tv_time);
        TextView tv_button = (TextView) mViewGroup.findViewById(R.id.tv_button);
        mWindow.setContentView(mViewGroup);
        mWindow.showAtLocation(mViewGroup, Gravity.CENTER, 0, 0);
        User user = hm_WcpcUserApplication.getInstance().getUser();
        count.setText("恭喜您获得" + user.getCoupon_count() + "张");
        price.setText(user.getCoupon_value() + "元");
        price2.setText(user.getCoupon_value());
        tv_time.setText("有效期至 " + user.getCoupon_dateline());
        tv_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
                Intent it1 = new Intent(mContext, MainNewActivity.class);
                startActivity(it1);
                finish();
            }
        });
    }
}
