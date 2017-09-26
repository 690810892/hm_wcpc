package com.hemaapp.wcpc_driver.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_driver.BaseActivity;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.User;

/**
 * Created by WangYuxia on 2016/5/18.
 * 支付宝提现
 */
public class CashAddByAlipayActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private RelativeLayout layout_account;
    private TextView text_notice;
    private LinearLayout layout_mycount;
    private TextView text_mycount;
    private TextView text_money;
    private FrameLayout layout_inputvalue;
    private LinearLayout layout_noticevalue;
    private EditText edit_value;
    private EditText edit_password;
    private TextView text_submit;

    private User user;
    private String applyfee, paypassword, alipay_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_cashbyalipay);
        super.onCreate(savedInstanceState);
        user = hm_WcpcDriverApplication.getInstance().getUser();
        getNetWorker().clientGet(user.getToken(), user.getId());
    }

    private void init(){
        alipay_no = user.getAlipay_no();
        if(isNull(user.getAlipay_no())){
            text_notice.setVisibility(View.VISIBLE);
            layout_mycount.setVisibility(View.GONE);
        }else{
            text_notice.setVisibility(View.GONE);
            layout_mycount.setVisibility(View.VISIBLE);
            text_mycount.setText(user.getAlipay_no());
        }
        text_money.setText((isNull(user.getFeeaccount())?"0":user.getFeeaccount()));
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_GET:
            case CASH_ADD:
                showProgressDialog("请稍后...");
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_GET:
            case CASH_ADD:
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
            case CLIENT_GET:
                HemaArrayResult<User> uResult = (HemaArrayResult<User>) baseResult;
                user = uResult.getObjects().get(0);
                init();
                break;
            case CASH_ADD:
                showTextDialog("申请已经提交，请耐心等待..");
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
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_GET:
            case CASH_ADD:
                showTextDialog(baseResult.getMsg());
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_GET:
                showTextDialog("抱歉，获取数据失败");
                break;
            case CASH_ADD:
                showTextDialog("抱歉，申请提交失败");
                break;
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        right = (TextView) findViewById(R.id.title_btn_right);
        title = (TextView) findViewById(R.id.title_text);

        layout_account = (RelativeLayout) findViewById(R.id.layout);
        text_notice = (TextView) findViewById(R.id.textview);
        layout_mycount = (LinearLayout) findViewById(R.id.layout_0);
        text_mycount = (TextView) findViewById(R.id.textview_0);
        text_money = (TextView) findViewById(R.id.textview_2);
        layout_inputvalue = (FrameLayout) findViewById(R.id.layout_2);
        layout_noticevalue = (LinearLayout) findViewById(R.id.layout_3);
        edit_value = (EditText) findViewById(R.id.edittext);
        edit_password = (EditText) findViewById(R.id.edittext_0);
        text_submit = (TextView) findViewById(R.id.button);
    }

    @Override
    protected void getExras() {

    }

    @Override
    protected void setListener() {
        title.setText("支付宝提现");
        right.setVisibility(View.INVISIBLE);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setListener(layout_account);
        setListener(layout_inputvalue);
        setListener(text_submit);

        edit_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() == 0){
                    edit_value.setVisibility(View.GONE);
                    layout_noticevalue.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private void setListener(View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it;
                switch (v.getId()){
                    case R.id.layout:
                        it = new Intent(mContext, EditAlipayActivity.class);
                        it.putExtra("data", user.getAlipay_no());
                        startActivityForResult(it, R.id.layout);
                        break;
                    case R.id.layout_2:
                        if(edit_value.getVisibility() == View.GONE){
                            layout_noticevalue.setVisibility(View.GONE);
                            edit_value.setVisibility(View.VISIBLE);
                            edit_value.requestFocus();
                        }
                        break;
                    case R.id.button:
                        if(isNull(alipay_no)){
                            showTextDialog("请编辑支付宝账号");
                            return;
                        }

                        applyfee = edit_value.getText().toString();
                        if(isNull(applyfee)){
                            showTextDialog("请输入提现金额");
                            return;
                        }

                        if(Integer.parseInt(applyfee) > Double.parseDouble(user.getFeeaccount())){
                            showTextDialog("抱歉，您的钱包余额不足，无法提现");
                            return;
                        }

                        Integer money = Integer.parseInt(applyfee) % 100;
                        if(money != 0){
                            showTextDialog("抱歉，您提现的金额必须是100的整数倍");
                            return;
                        }

                        paypassword = edit_password.getText().toString();
                        if(isNull(paypassword)){
                            showTextDialog("请输入支付密码");
                            return;
                        }

                        getNetWorker().cashAdd(user.getToken(), "1", applyfee, paypassword);
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK)
            return;
        switch (requestCode){
            case R.id.layout:
                alipay_no = data.getStringExtra("data");
                user.setAlipay_no(alipay_no);
                layout_mycount.setVisibility(View.VISIBLE);
                text_mycount.setText(alipay_no);
                text_notice.setVisibility(View.GONE);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
