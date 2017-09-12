package com.hemaapp.wcpc_driver.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_driver.BaseActivity;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.User;

/**
 * Created by WangYuxia on 2016/5/18.
 */
public class EditAlipayActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private EditText editText;

    private User user;
    private String account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_editalipay);
        super.onCreate(savedInstanceState);
        user = hm_WcpcDriverApplication.getInstance().getUser();
        if(!isNull(account)){
            editText.setText(account);
            editText.setSelection(account.length());
        }
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask hemaNetTask) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case ALIPAY_SAVE:
                showProgressDialog("请稍后...");
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask hemaNetTask) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case ALIPAY_SAVE:
                cancelProgressDialog();
                break;
        }
    }

    @Override
    protected void callBackForServerSuccess(HemaNetTask hemaNetTask, HemaBaseResult hemaBaseResult) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case ALIPAY_SAVE:
                showTextDialog("提交成功");
                title.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIntent.putExtra("data", account);
                        setResult(RESULT_OK, mIntent);
                        finish();
                    }
                }, 1000);
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask hemaNetTask, int i) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case ALIPAY_SAVE:
                showTextDialog("提交失败，请稍后重试");
                break;
        }
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask, HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information){
            case ALIPAY_SAVE:
                showTextDialog(baseResult.getMsg());
                break;
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        right = (TextView) findViewById(R.id.title_btn_right);
        title = (TextView) findViewById(R.id.title_text);
        editText = (EditText) findViewById(R.id.edittext);
    }

    @Override
    protected void getExras() {
        account = mIntent.getStringExtra("data");
    }

    @Override
    protected void setListener() {
        title.setText("编辑支付宝账号");
        right.setText("保存");
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account = editText.getText().toString();
                if(isNull(account)){
                    showTextDialog("请输入支付宝账号");
                    return;
                }

                getNetWorker().alipaySave(user.getToken(), account);
            }
        });
    }
}
