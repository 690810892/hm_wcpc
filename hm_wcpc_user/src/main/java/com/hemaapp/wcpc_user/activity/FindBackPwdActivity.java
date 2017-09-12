package com.hemaapp.wcpc_user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
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

import xtom.frame.util.XtomSharedPreferencesUtil;

/**
 * Created by WangYuxia on 2016/5/4.
 * 找回密码
 * type = 1:忘记密码,找回登录密码
 * type = 2:找回支付密码
 */
public class FindBackPwdActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private EditText usernameEditText;
    private ImageView image_clear;
    private TextView nextText;
    private EditText codeEditText;
    private TextView sendButton;
    private LinearLayout secondLayout;
    private TextView secondTextView;

    private String username;
    private TimeThread timeThread;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_findbackpwd);
        super.onCreate(savedInstanceState);
        if("1".equals(type))
            title.setText("忘记密码");
        else if("2".equals(type))
            title.setText("找回支付/提现密码");
        username = XtomSharedPreferencesUtil.get(mContext, "username");
        if(!isNull(username)){
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
            default:
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
                Intent it = new Intent(mContext, ResetPwdActivity.class);
                it.putExtra("username", username);
                it.putExtra("tempToken", tempToken);
                it.putExtra("keytype", type);
                startActivity(it);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!= RESULT_OK)
            return;
        switch (requestCode) {
            case R.id.textview:
                setResult(RESULT_OK, data);
                finish();
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
            case CLIENT_VERIFY:
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
            default:
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
        nextText = (TextView) findViewById(R.id.button);
        codeEditText = (EditText) findViewById(R.id.code);
        secondTextView = (TextView) findViewById(R.id.second);
        secondLayout = (LinearLayout) findViewById(R.id.linearlayout);
        sendButton = (TextView) findViewById(R.id.sendcode);
    }

    @Override
    protected void getExras() {
        type = mIntent.getStringExtra("type");
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

        usernameEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() > 0)
                    image_clear.setVisibility(View.VISIBLE);
                else
                    image_clear.setVisibility(View.GONE);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        image_clear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                usernameEditText.setText("");
                username = "";
            }
        });

        sendButton.setOnClickListener(new SendButtonListener());
        codeEditText.addTextChangedListener(new OnTextChangeListener());
        nextText.setOnClickListener(new View.OnClickListener() {

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
        FindBackPwdActivity activity;

        public TimeHandler(FindBackPwdActivity activity) {
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
            checkNextable();
        }
    }

    private void checkNextable() {
        String code = codeEditText.getText().toString();
        // code.matches("\\d{4}$")
        boolean c = !isNull(code);
        if (c) {
            nextText.setEnabled(true);
        } else {
            nextText.setEnabled(false);
        }
    }
}

