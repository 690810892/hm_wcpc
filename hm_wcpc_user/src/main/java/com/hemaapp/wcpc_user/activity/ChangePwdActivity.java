package com.hemaapp.wcpc_user.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.User;

import xtom.frame.util.XtomSharedPreferencesUtil;

/**
 * Created by WangYuxia on 2016/5/19.
 * keytype = 1:修改登录密码
 * keytype = 2:修改支付密码
 */
public class ChangePwdActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private EditText edit_old;
    private ImageView image_oldclear;
    private EditText edit_pwd;
    private ImageView image_clear;
    private EditText edit_pwd_again;
    private ImageView image_reclear;
    private TextView button;

    private String old, password;
    private String type;

    private User user;
    private String keytype1 = "1", keytype2="1", keytype3= "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_changepwd);
        super.onCreate(savedInstanceState);
        user = hm_WcpcUserApplication.getInstance().getUser();
        if("1".equals(type))
            title.setText("修改密码");
        else
            title.setText("修改支付密码");
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case PASSWORD_SAVE:
                showProgressDialog("正在重设密码...");
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
            case PASSWORD_SAVE:
                cancelProgressDialog();
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForServerSuccess(HemaNetTask netTask,
                                            HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case PASSWORD_SAVE:
                if("1".equals(type)){
                    XtomSharedPreferencesUtil.save(mContext, "password", password);
                    showTextDialog("修改密码成功");
                }else{
                    showTextDialog("修改支付密码成功");
                }
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
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case PASSWORD_SAVE:
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
            case PASSWORD_SAVE:
                showTextDialog("修改密码失败,请稍后重试");
                break;
            default:
                break;
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        title = (TextView) findViewById(R.id.title_text);
        right = (TextView) findViewById(R.id.title_btn_right);

        edit_old = (EditText) findViewById(R.id.edittext_1);
        image_oldclear = (ImageView) findViewById(R.id.imageview_1);
        edit_pwd = (EditText) findViewById(R.id.edittext);
        image_clear = (ImageView) findViewById(R.id.imageview);
        edit_pwd_again = (EditText) findViewById(R.id.edittext_0);
        image_reclear = (ImageView) findViewById(R.id.imageview_0);
        button = (TextView) findViewById(R.id.button);
    }

    @Override
    protected void getExras() {
        type = mIntent.getStringExtra("keytype");
    }

    @Override
    protected void setListener() {
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
                old = edit_old.getText().toString();
                password = edit_pwd.getText().toString();
                String repeat = edit_pwd_again.getText().toString();

                if(isNull(old)){
                    showTextDialog("抱歉，请输入旧密码");
                    return;
                }

                if(isNull(password)){
                    showTextDialog("抱歉，请输入新密码");
                    return;
                }

                if(!(password.length() >= 6 && password.length() <= 20)){
                    showTextDialog("抱歉，密码长度为6-20位");
                    return;
                }

                if(isNull(repeat)){
                    showTextDialog("抱歉，请输入确认密码");
                    return;
                }

                if(!password.equals(repeat)){
                    showTextDialog("抱歉，新密码与确认密码不一致，请重新输入");
                    return;
                }

                getNetWorker().passwordSave(user.getToken(), type, "1", old, password);
            }
        });

        image_oldclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(keytype1.equals("1")){ //不可见
                    edit_old.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
                    image_oldclear.setImageResource(R.mipmap.img_eye_open);
                    keytype1 = "2";
                }else{
                    edit_old.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    image_oldclear.setImageResource(R.mipmap.img_eye_close);
                    keytype1 = "1";
                }
            }
        });
        image_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(keytype2.equals("1")){ //不可见
                    edit_pwd.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
                    image_clear.setImageResource(R.mipmap.img_eye_open);
                    keytype2 = "2";
                }else{
                    edit_pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    image_clear.setImageResource(R.mipmap.img_eye_close);
                    keytype2 = "1";
                }
            }
        });
        image_reclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(keytype3.equals("1")){ //不可见
                    edit_pwd_again.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
                    image_reclear.setImageResource(R.mipmap.img_eye_open);
                    keytype3 = "2";
                }else{
                    edit_pwd_again.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    image_reclear.setImageResource(R.mipmap.img_eye_close);
                    keytype3 = "1";
                }
            }
        });
    }

}
