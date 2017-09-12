package com.hemaapp.wcpc_user.activity;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.HemaUtil;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.User;

/**
 * Created by WangYuxia on 2016/5/19.
 */
public class FeedBackActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;
    private EditText editText;
    private TextView button;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_feedback);
        super.onCreate(savedInstanceState);
        user = hm_WcpcUserApplication.getInstance().getUser();
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case ADVICE_ADD:
                cancelProgressDialog();
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case ADVICE_ADD:
                showTextDialog("抱歉，操作失败，请稍后重试");
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
            case ADVICE_ADD:
                showTextDialog(baseResult.getMsg());
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForServerSuccess(HemaNetTask netTask,
                                            HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case ADVICE_ADD:
                showTextDialog("提交成功");
                title.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case ADVICE_ADD:
                showProgressDialog("请稍后...");
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
        editText = (EditText) findViewById(R.id.edittext);
        button = (TextView) findViewById(R.id.button);
    }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
        title.setText("意见反馈");
        right.setVisibility(View.INVISIBLE);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
                500) });
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String suggestion = editText.getText().toString();
                if (isNull(suggestion)) {
                    showTextDialog("反馈意见不能为空,请重新填写");
                    return;
                }

                String device = "android phone"; //设备
                String version = HemaUtil.getAppVersionForSever(mContext); //来源
                String brand = android.os.Build.BRAND+android.os.Build.MODEL; //品牌
                String system = android.os.Build.VERSION.RELEASE; //系统版本

                getNetWorker().adviceAdd(user.getToken(), device, version, brand, system, suggestion, "1");
            }
        });
    }
}
