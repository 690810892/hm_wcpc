package com.hemaapp.wcpc_user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.User;


/**
 * Created by WangYuxia on 2016/5/18.
 * 编辑银行卡信息
 */
public class EditBankCardActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private LinearLayout layout_bankkind;
    private TextView text_bankkind;
    private EditText edit_user;
    private EditText edit_card;
    private TextView button;

    private String bankname;
    private String bankuser;
    private String bankcard;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_editbankcard);
        super.onCreate(savedInstanceState);
        user = hm_WcpcUserApplication.getInstance().getUser();
        if(!isNull(bankname)){
            text_bankkind.setText(bankname);
            text_bankkind.setTextColor(mContext.getResources().getColor(R.color.black2));
        }
        if(!isNull(bankuser)){
            edit_user.setText(bankuser);
            edit_user.setSelection(bankuser.length());
        }

        if(!isNull(bankcard)){
            edit_card.setText(bankcard);
            edit_card.setSelection(bankcard.length());
        }
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask hemaNetTask) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case BANK_SAVE:
                showProgressDialog("请稍后...");
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask hemaNetTask) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case BANK_SAVE:
                cancelProgressDialog();
                break;
        }
    }

    @Override
    protected void callBackForServerSuccess(HemaNetTask hemaNetTask, HemaBaseResult hemaBaseResult) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case BANK_SAVE:
                showTextDialog("提交成功");
                title.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIntent.putExtra("name", bankname);
                        mIntent.putExtra("card", bankcard);
                        mIntent.putExtra("user", bankuser);
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
            case BANK_SAVE:
                showTextDialog("提交失败，请稍后重试");
                break;
        }
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask, HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information){
            case BANK_SAVE:
                showTextDialog(baseResult.getMsg());
                break;
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        right = (TextView) findViewById(R.id.title_btn_right);
        title = (TextView) findViewById(R.id.title_text);

        layout_bankkind = (LinearLayout) findViewById(R.id.layout);
        text_bankkind = (TextView) findViewById(R.id.textview);
        edit_user = (EditText) findViewById(R.id.edittext_0);
        edit_card = (EditText) findViewById(R.id.edittext_1);
        button = (TextView) findViewById(R.id.button);
    }

    @Override
    protected void getExras() {
        bankname = mIntent.getStringExtra("bankname");
        bankuser = mIntent.getStringExtra("bankuser");
        bankcard = mIntent.getStringExtra("bankcard");
    }

    @Override
    protected void setListener() {
        title.setText("编辑银行卡信息");
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        right.setVisibility(View.INVISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNull(bankname)){
                    showTextDialog("请选择卡类型");
                    return;
                }

                bankuser = edit_user.getText().toString();
                if(isNull(bankuser)){
                    showTextDialog("请填写持卡人姓名");
                    return;
                }

                bankcard = edit_card.getText().toString();
                if(isNull(bankcard)){
                    showTextDialog("请填写卡号");
                    return;
                }

                getNetWorker().bankSave(user.getToken(), bankuser, bankcard, bankname);
            }
        });

        layout_bankkind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(mContext, SelectBankListActivity.class);
                startActivityForResult(it, R.id.layout);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK)
            return;
        switch (requestCode){
            case R.id.layout:
                bankname = data.getStringExtra("name");
                text_bankkind.setText(bankname);
                text_bankkind.setTextColor(mContext.getResources().getColor(R.color.black2));
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
