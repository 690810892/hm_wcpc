package com.hemaapp.wcpc_user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.R;

/**
 * Created by WangYuxia on 2016/5/4.
 */
public class RegisterStepTwoActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private EditText passwordEditText;
    private ImageView image_clear;
    private EditText repeatEditText;
    private ImageView image_reclear;

    private TextView text_submit;

    private String username;
    private String tempToken;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_register1);
        super.onCreate(savedInstanceState);
    }
    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
    }

    @Override
    protected void callBackForServerSuccess(HemaNetTask netTask,
                                            HemaBaseResult baseResult) {
    }
    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
    }
    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        right = (TextView) findViewById(R.id.title_btn_right);
        title = (TextView) findViewById(R.id.title_text);

        passwordEditText = (EditText) findViewById(R.id.edittext);
        image_clear = (ImageView) findViewById(R.id.imageview);
        repeatEditText = (EditText) findViewById(R.id.edittext_0);
        image_reclear = (ImageView) findViewById(R.id.imageview_0);

        text_submit = (TextView) findViewById(R.id.button);
    }

    @Override
    protected void getExras() {
        username = mIntent.getStringExtra("username");
        tempToken = mIntent.getStringExtra("tempToken");
    }

    @Override
    protected void setListener() {
        title.setText("设置密码");
        left.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        right.setVisibility(View.INVISIBLE);
        text_submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                password = passwordEditText.getText().toString();
                if(isNull(password)){
                    showTextDialog("抱歉，密码需要6-12位");
                    return;
                }
                if(!(password.length() >= 6 && password.length() <= 12)){
                    showTextDialog("抱歉，密码需要6-12位");
                    return;
                }
                String repeat = repeatEditText.getText().toString();
                boolean c = password.equals(repeat);
                if(c){//密码不为空且两次输入一样
                    Intent it = new Intent(mContext, RegisterStepThreeActivity.class);
                    it.putExtra("username", username);
                    it.putExtra("password", password);
                    it.putExtra("tempToken", tempToken);
                    startActivity(it);
                }else {
                    showTextDialog("密码输入不一致,请重试");
                    return;
                }
            }
        });
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(count > 0)
                    image_clear.setVisibility(View.VISIBLE);
                else
                    image_clear.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        repeatEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(count > 0)
                    image_reclear.setVisibility(View.VISIBLE);
                else
                    image_reclear.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
