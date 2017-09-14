package com.hemaapp.wcpc_user.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
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

/**
 * Created by WangYuxia on 2016/5/4.
 * 注册第一步
 */
public class RegistorStepOneActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private EditText usernameEditText;
    private ImageView image_clear;
    private TextView textView;
    private EditText codeEditText;
    private TextView sendButton;
    private LinearLayout secondLayout;
    private TextView secondTextView;
    private TextView agreementTextView;
    private CheckBox checkBox;

    private EditText passwordEditText;
    private ImageView image_clear_pwd;
    private EditText repeatEditText;
    private ImageView image_reclear;

    private TextView text_submit;

    private String username;
    private String password;
    private String tempToken;
    private TimeThread timeThread;
    private String keytype1 = "1", keytype2 = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_register0);
        super.onCreate(savedInstanceState);
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
                showTextDialog("该手机号已经被注册了");
                break;
            case CODE_GET:
                textView.setText("验证码已发送到 " + HemaUtil.hide(username, "1"));
                textView.setVisibility(View.VISIBLE);
                timeThread = new TimeThread(new TimeHandler(this));
                timeThread.start();
                break;
            case CODE_VERIFY:
                HemaArrayResult<String> sResult = (HemaArrayResult<String>) baseResult;
                tempToken = sResult.getObjects().get(0);
                Intent it = new Intent(mContext, RegisterStepThreeActivity.class);
                it.putExtra("username", username);
                it.putExtra("password", password);
                it.putExtra("tempToken", tempToken);
                startActivity(it);
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
                username = netTask.getParams().get("username");
                getNetWorker().codeGet(username);
                break;
            case CODE_GET:
                showTextDialog(baseResult.getMsg());
                break;
            case CODE_VERIFY:
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
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        right = (TextView) findViewById(R.id.title_btn_right);
        title = (TextView) findViewById(R.id.title_text);

        usernameEditText = (EditText) findViewById(R.id.username);
        image_clear = (ImageView) findViewById(R.id.imageview);
        textView = (TextView) findViewById(R.id.textview);
        codeEditText = (EditText) findViewById(R.id.code);
        secondTextView = (TextView) findViewById(R.id.second);
        secondLayout = (LinearLayout) findViewById(R.id.linearlayout);
        sendButton = (TextView) findViewById(R.id.sendcode);
        agreementTextView = (TextView) findViewById(R.id.areement);
        checkBox = (CheckBox) findViewById(R.id.checkbox);

        passwordEditText = (EditText) findViewById(R.id.edit_password);
        image_clear_pwd = (ImageView) findViewById(R.id.img_pwd_visible);
        repeatEditText = (EditText) findViewById(R.id.edit_password_again);
        image_reclear = (ImageView) findViewById(R.id.img_pwd_visible1);

        text_submit = (TextView) findViewById(R.id.button);
    }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
        title.setText("注册");
        left.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        right.setVisibility(View.INVISIBLE);

        agreementTextView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
        agreementTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String sys_web_service = getApplicationContext().getSysInitInfo()
                        .getSys_web_service();
                String pathStr = sys_web_service + "webview/parm/protocal";

                Intent it = new Intent(mContext, ShowInternetPageActivity.class);
                it.putExtra("name", "注册协议");
                it.putExtra("path", pathStr);
                startActivity(it);
            }
        });

        image_clear_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if("1".equals(keytype1)){
                    passwordEditText.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
                    image_clear_pwd.setImageResource(R.mipmap.img_eye_open);
                    keytype1 = "2";
                }else{
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    image_clear_pwd.setImageResource(R.mipmap.img_eye_close);
                    keytype1 = "1";
                }

            }
        });

        image_reclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if("1".equals(keytype2)){
                    repeatEditText.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
                    image_reclear.setImageResource(R.mipmap.img_eye_open);
                    keytype2 = "2";
                }else{
                    repeatEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    image_reclear.setImageResource(R.mipmap.img_eye_close);
                    keytype2 = "1";
                }
            }
        });

        sendButton.setOnClickListener(new SendButtonListener());
        codeEditText.addTextChangedListener(new OnTextChangeListener());
        text_submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isNull(username)) {
                    showTextDialog("请先验证手机号");
                    return;
                }

                if(!username.equals(usernameEditText.getText().toString())){
                    showTextDialog("抱歉，当前手机号与获取\n验证码的手机号不同,请重新获取");
                    return;
                }

                String code = codeEditText.getText().toString();
                if(isNull(code)){
                    showTextDialog("请输入验证码");
                    return;
                }

                if(!checkBox.isChecked()){
                    showTextDialog("请同意注册协议");
                    return;
                }

                password = passwordEditText.getText().toString();
                if(isNull(password)){
                    showTextDialog("抱歉，密码需要6-12位");
                    return;
                }
                if(!(password.length() >= 6 && password.length() <= 20)){
                    showTextDialog("抱歉，密码需要6-20位");
                    return;
                }
                String repeat = repeatEditText.getText().toString();
                boolean c = password.equals(repeat);
                if(!c){//密码不为空且两次输入一样
                    showTextDialog("密码输入不一致,请重试");
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
                    // ignore
                }
                curr--;
            }
            timeHandler.sendEmptyMessage(-1);
        }
    }

    private static class TimeHandler extends Handler {
        RegistorStepOneActivity activity;

        public TimeHandler(RegistorStepOneActivity activity) {
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

    private class OnTextChangeListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            if(count > 0){
                image_clear.setVisibility(View.VISIBLE);
            }else
                image_clear.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

}
