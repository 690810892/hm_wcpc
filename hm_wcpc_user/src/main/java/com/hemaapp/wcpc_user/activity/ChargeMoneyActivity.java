package com.hemaapp.wcpc_user.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseConfig;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.alipay.Result;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.AlipayTrade;
import com.hemaapp.wcpc_user.module.UnionTrade;
import com.hemaapp.wcpc_user.module.User;
import com.hemaapp.wcpc_user.module.WeiXinPay;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;

import xtom.frame.util.XtomSharedPreferencesUtil;

/**
 * Created by WangYuxia on 2016/5/17.
 * 充值
 */
public class ChargeMoneyActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private EditText editText;
    private RelativeLayout layout_alipay;
    private CheckBox checkBox_alipay;
    private RelativeLayout layout_weixin;
    private CheckBox checkBox_weixin;
    private RelativeLayout layout_unipay;
    private CheckBox checkBox_unipay;

    private String money;
    private User user;
    IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_chargemoney);
        super.onCreate(savedInstanceState);
        user = hm_WcpcUserApplication.getInstance().getUser();
        msgApi.registerApp(BaseConfig.APPID_WEIXIN);
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask hemaNetTask) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case ALIPAY:
            case WEI_XIN:
            case UNIONPAY:
                showProgressDialog("请稍后...");
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask hemaNetTask) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case ALIPAY:
            case WEI_XIN:
            case UNIONPAY:
                cancelProgressDialog();
                break;
        }
    }

    @Override
    protected void callBackForServerSuccess(HemaNetTask hemaNetTask, HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case ALIPAY:
                HemaArrayResult<AlipayTrade> aResult = (HemaArrayResult<AlipayTrade>) baseResult;
                AlipayTrade trade = aResult.getObjects().get(0);
                String orderInfo = trade.getAlipaysign();
                new AlipayThread(orderInfo).start();
                break;
            case WEI_XIN:
                HemaArrayResult<WeiXinPay> wResult = (HemaArrayResult<WeiXinPay>) baseResult;
                WeiXinPay wTrade = wResult.getObjects().get(0);
                goWeixin(wTrade);
                break;
            case UNIONPAY:
                HemaArrayResult<UnionTrade> uResult = (HemaArrayResult<UnionTrade>) baseResult;
                UnionTrade uTrade = uResult.getObjects().get(0);
                String uInfo = uTrade.getTn();
                UPPayAssistEx.startPayByJAR(mContext, PayActivity.class, null,
                        null, uInfo, BaseConfig.UNIONPAY_TESTMODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*************************************************
         * 处理银联手机支付控件返回的支付结果
         ************************************************/
        if (data == null) {
            return;
        }

        String msg;
		/*
		 * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
		 */
        String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {
            msg = "支付成功！";
            showTextDialog(msg);
            title.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000);
        } else if (str.equalsIgnoreCase("fail")) {
            msg = "支付失败！";
            showTextDialog(msg);
        } else if (str.equalsIgnoreCase("cancel")) {
            msg = "您取消了支付";
            showTextDialog(msg);
        }

    }

    private void goWeixin(WeiXinPay trade) {
        XtomSharedPreferencesUtil.save(mContext, "order_id", "0");
        msgApi.registerApp(BaseConfig.APPID_WEIXIN);

        PayReq request = new PayReq();
        request.appId = trade.getAppid();
        request.partnerId = trade.getPartnerid();
        request.prepayId = trade.getPrepayid();
        request.packageValue = trade.getPpackage();
        request.nonceStr = trade.getNoncestr();
        request.timeStamp = trade.getTimestamp();
        request.sign = trade.getSign();
        msgApi.sendReq(request);
    }

    private class AlipayThread extends Thread {
        String orderInfo;
        AlipayHandler alipayHandler;

        public AlipayThread(String orderInfo) {
            this.orderInfo = orderInfo;
            alipayHandler = new AlipayHandler(ChargeMoneyActivity.this);
        }

        @Override
        public void run() {
            PayTask alipay = new PayTask(ChargeMoneyActivity.this);
            // 调用支付接口，获取支付结果
            String result = alipay.pay(orderInfo);

            Message msg = new Message();
            msg.obj = result;
            alipayHandler.sendMessage(msg);
        }
    }

    private static class AlipayHandler extends Handler {
        ChargeMoneyActivity activity;

        public AlipayHandler(ChargeMoneyActivity activity) {
            this.activity = activity;
        }

        public void handleMessage(android.os.Message msg) {
            Result result = new Result((String) msg.obj);
            int status = result.getResultStatus();
            if(status == 9000){
                activity.showTextDialog("支付成功");
                activity.title.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        activity.finish();
                    }
                }, 1000);
            }else {
                activity.showTextDialog(result.getResult());
            }
        };
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask hemaNetTask, int i) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case ALIPAY:
            case WEI_XIN:
            case UNIONPAY:
                break;
        }
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case ALIPAY:
            case UNIONPAY:
            case WEI_XIN:
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
        layout_alipay = (RelativeLayout) findViewById(R.id.layout_0);
        checkBox_alipay = (CheckBox) findViewById(R.id.checkbox_0);
        layout_weixin = (RelativeLayout) findViewById(R.id.layout_1);
        checkBox_weixin = (CheckBox) findViewById(R.id.checkbox_1);
        layout_unipay = (RelativeLayout) findViewById(R.id.layout_2);
        checkBox_unipay = (CheckBox) findViewById(R.id.checkbox_2);
    }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
        title.setText("充值");
        right.setVisibility(View.VISIBLE);
        right.setText("确定");
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        layout_alipay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkBox_alipay.isChecked()) {
                    checkBox_alipay.setChecked(false);
                    checkBox_weixin.setChecked(false);
                    checkBox_unipay.setChecked(false);
                } else {
                    checkBox_alipay.setChecked(true);
                    checkBox_weixin.setChecked(false);
                    checkBox_unipay.setChecked(false);
                }
            }
        });

        checkBox_alipay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkBox_weixin.setChecked(false);
                checkBox_unipay.setChecked(false);
            }
        });

        layout_weixin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkBox_weixin.isChecked()) {
                    checkBox_alipay.setChecked(false);
                    checkBox_weixin.setChecked(false);
                    checkBox_unipay.setChecked(false);
                } else {
                    checkBox_alipay.setChecked(false);
                    checkBox_weixin.setChecked(true);
                    checkBox_unipay.setChecked(false);
                }
            }
        });

        checkBox_weixin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkBox_alipay.setChecked(false);
                checkBox_unipay.setChecked(false);
            }
        });

        layout_unipay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkBox_unipay.isChecked()) {
                    checkBox_alipay.setChecked(false);
                    checkBox_weixin.setChecked(false);
                    checkBox_unipay.setChecked(false);
                } else {
                    checkBox_alipay.setChecked(false);
                    checkBox_weixin.setChecked(false);
                    checkBox_unipay.setChecked(true);
                }
            }
        });

        checkBox_unipay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkBox_alipay.setChecked(false);
                checkBox_weixin.setChecked(false);
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                money = editText.getText().toString();
                if(isNull(money)){
                    showTextDialog("请输入充值金额");
                    return;
                }

                if(money.length() > 9){
                    showTextDialog("抱歉，您输入的金额过大，请重新输入");
                    return;
                }

                if(!checkBox_alipay.isChecked() && !checkBox_weixin.isChecked() && !checkBox_unipay.isChecked()){
                    showTextDialog("请选择一种支付方式");
                    return;
                }

                if(checkBox_alipay.isChecked()){
                    getNetWorker().alipay(user.getToken(), "1", "0", money);
                }else if(checkBox_weixin.isChecked()){
                    getNetWorker().weixin(user.getToken(), "1", "0", money);
                }else if(checkBox_unipay.isChecked()){
                    checkUniopayRight();
                }
            }
        });
    }

    private void checkUniopayRight(){
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED)) {//判断是否拥有定位权限

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    5);
        } else {
            getNetWorker().unionpay(user.getToken(), "1", "0", money);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 5) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED){//
                title.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1500);
            } else {
                getNetWorker().unionpay(user.getToken(), "1", "0", money);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
