package com.hemaapp.wcpc_user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.HemaUtil;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.User;

import xtom.frame.XtomConfig;
import xtom.frame.util.Md5Util;
import xtom.frame.util.XtomSharedPreferencesUtil;

/**
 * Created by WangYuxia on 2016/5/18.
 * 设置支付密码
 */
public class ResetPayPasswordActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private EditText usernameEditText;
    private ImageView img_clearinput;
    private TextView nextText;
    private EditText codeEditText;
    private TextView sendButton;
    private LinearLayout secondLayout;
    private TextView secondTextView;

    private EditText edit_password;
    private ImageView img_password;
    private EditText edit_password_again;
    private ImageView img_password_again;

    private String username;
    private TimeThread timeThread;
    private String keytype1 = "1", keytype2 = "1", password, keytype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_findbackpwd);
        super.onCreate(savedInstanceState);
        username = XtomSharedPreferencesUtil.get(mContext, "username");
        if (!isNull(username)) {
            usernameEditText.setText(username);
            usernameEditText.setSelection(username.length());
        }
    }

    @Override
    protected void onDestroy() {
        if (timeThread != null)
            timeThread.cancel();
        super.onDestroy();
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_VERIFY:
                showProgressDialog("正在验证手机号");
                break;
            case CODE_GET:
                showProgressDialog("正在获取验证码");
                break;
            case CODE_VERIFY:
                showProgressDialog("正在验证随机码");
                break;
            case PASSWORD_RESET:
                showProgressDialog("正在设置...");
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_VERIFY:
            case CODE_GET:
            case CODE_VERIFY:
            case PASSWORD_RESET:
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
            case CLIENT_VERIFY:
                username = netTask.getParams().get("username");
                secondLayout.setVisibility(View.VISIBLE);
                sendButton.setVisibility(View.GONE);
                getNetWorker().codeGet(username); //获取短信验证码
                break;
            case CODE_GET:
                timeThread = new TimeThread(new TimeHandler(this));
                timeThread.start();
                break;
            case CODE_VERIFY:
                HemaArrayResult<String> sResult = (HemaArrayResult<String>) baseResult;
                String tempToken = sResult.getObjects().get(0);
                getNetWorker().passwordReset(tempToken, "2", "1", password);
                break;
            case PASSWORD_RESET:
                User user = hm_WcpcUserApplication.getInstance().getUser();
                user.setPaypassword(Md5Util.getMd5(XtomConfig.DATAKEY
                        + Md5Util.getMd5(password)));
                showTextDialog("设置密码成功");
                title.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case R.id.textview:
                setResult(RESULT_OK, data);
                finish();
                break;
        }
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_VERIFY:
                showTextDialog(baseResult.getMsg());
                break;
            case CODE_GET:
                showTextDialog(baseResult.getMsg());
                break;
            case CODE_VERIFY:
                showTextDialog(baseResult.getMsg());
                break;
            case PASSWORD_RESET:
                showTextDialog(baseResult.getMsg());
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_VERIFY:
                showTextDialog("验证手机号失败");
                break;
            case CODE_GET:
                showTextDialog("获取验证码失败");
                break;
            case CODE_VERIFY:
                showTextDialog("验证随机码失败");
                break;
            case PASSWORD_RESET:
                showTextDialog("重设密码失败,请稍后重试");
                break;
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        right = (TextView) findViewById(R.id.title_btn_right);
        title = (TextView) findViewById(R.id.title_text);

        usernameEditText = (EditText) findViewById(R.id.username);
        img_clearinput = (ImageView) findViewById(R.id.img_clearinput);
        nextText = (TextView) findViewById(R.id.button);
        codeEditText = (EditText) findViewById(R.id.code);
        secondTextView = (TextView) findViewById(R.id.second);
        secondLayout = (LinearLayout) findViewById(R.id.linearlayout);
        sendButton = (TextView) findViewById(R.id.sendcode);

        edit_password = (EditText) findViewById(R.id.edit_password);
        img_password = (ImageView) findViewById(R.id.img_pwd_visible);
        edit_password_again = (EditText) findViewById(R.id.edit_password_again);
        img_password_again = (ImageView) findViewById(R.id.img_pwd_visible1);
    }

    @Override
    protected void getExras() {
        keytype = mIntent.getStringExtra("keytype");
        if (isNull(keytype))
            keytype = "1";
    }

    @Override
    protected void setListener() {
        right.setVisibility(View.INVISIBLE);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (keytype.equals("1"))
            title.setText("设置支付密码");
        else
            title.setText("忘记支付密码");
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0)
                    img_clearinput.setVisibility(View.VISIBLE);
                else
                    img_clearinput.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        img_clearinput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usernameEditText.setText("");
                img_clearinput.setVisibility(View.INVISIBLE);
            }
        });

        img_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("1".equals(keytype1)) {
                    edit_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    img_password.setImageResource(R.mipmap.img_eye_open);
                    keytype1 = "2";
                } else {
                    edit_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    img_password.setImageResource(R.mipmap.img_eye_close);
                    keytype1 = "1";
                }

            }
        });

        img_password_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("1".equals(keytype2)) {
                    edit_password_again.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    img_password_again.setImageResource(R.mipmap.img_eye_open);
                    keytype2 = "2";
                } else {
                    edit_password_again.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    img_password_again.setImageResource(R.mipmap.img_eye_close);
                    keytype2 = "1";
                }
            }
        });

        sendButton.setOnClickListener(new SendButtonListener());
        nextText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isNull(username)) {
                    showTextDialog("请先验证手机号");
                    return;
                }

                if (!username.equals(usernameEditText.getText().toString())) {
                    showTextDialog("抱歉，当前手机号与获取\n验证码的手机号不同,请重新获取");
                    return;
                }

                password = edit_password.getText().toString();
                String repeat = edit_password_again.getText().toString();
                if (isNull(password)) {
                    showTextDialog("请输入密码");
                    return;
                }

                if (isNull(repeat)) {
                    showTextDialog("请输入确认密码");
                    return;
                }

                if (!password.equals(repeat)) {
                    showTextDialog("新密码与确认密码不一致，请重新填写");
                    return;
                }

                if (!(password.length() >= 6 && password.length() <= 20)) {
                    showTextDialog("抱歉，密码长度为6-20位");
                    return;
                }

                String code = codeEditText.getText().toString();
                if (isNull(code)) {
                    showTextDialog("抱歉，请输入验证码");
                    return;
                }
                getNetWorker().codeVerify(username, code);
            }
        });
    }

    private class SendButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String username = usernameEditText.getText().toString();
            if (isNull(username)) {
                showTextDialog("请输入手机号");
                return;
            }

            String mobile = "^[1][3-8]+\\d{9}";
            if (!username.matches(mobile)) {
                showTextDialog("您输入的手机号不正确");
                return;
            }

            getNetWorker().clientVerify(username, "1");
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
        ResetPayPasswordActivity activity;

        public TimeHandler(ResetPayPasswordActivity activity) {
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
}

