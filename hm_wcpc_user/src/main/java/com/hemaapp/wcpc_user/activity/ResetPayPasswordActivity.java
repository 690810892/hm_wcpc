package com.hemaapp.wcpc_user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

/**
 * Created by WangYuxia on 2016/5/18.
 */
public class ResetPayPasswordActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private TextView text_username;
    private TextView textView;
    private EditText codeEditText;
    private TextView sendButton;
    private LinearLayout secondLayout;
    private TextView secondTextView;

    private TextView text_button;

    private String username;
    private User user;
    private TimeThread timeThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_resetpaypwd);
        super.onCreate(savedInstanceState);
        user = hm_WcpcUserApplication.getInstance().getUser();
        username = user.getUsername();
        text_username.setText(username);
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
            case CODE_GET:
                textView.setText("验证码已发送到 " + HemaUtil.hide(username, "1"));
                textView.setVisibility(View.VISIBLE);
                timeThread = new TimeThread(new TimeHandler(this));
                timeThread.start();
                break;
            case CODE_VERIFY:
                HemaArrayResult<String> sResult = (HemaArrayResult<String>) baseResult;
                Intent it = new Intent(mContext, ResetPwdActivity.class);
                it.putExtra("keytype", "2");
                it.putExtra("tempToken", sResult.getObjects().get(0));
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
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
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

        text_username = (TextView) findViewById(R.id.username);
        textView = (TextView) findViewById(R.id.textview);
        codeEditText = (EditText) findViewById(R.id.code);
        secondTextView = (TextView) findViewById(R.id.second);
        secondLayout = (LinearLayout) findViewById(R.id.linearlayout);
        sendButton = (TextView) findViewById(R.id.sendcode);
        text_button = (TextView) findViewById(R.id.button);
    }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
        title.setText("设置支付密码");
        right.setVisibility(View.INVISIBLE);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNetWorker().codeGet(username);
            }
        });

        text_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNull(codeEditText.getText().toString())){
                    showTextDialog("请填写验证码");
                    return;
                }

                getNetWorker().codeVerify(username, codeEditText.getText().toString());
            }
        });
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
