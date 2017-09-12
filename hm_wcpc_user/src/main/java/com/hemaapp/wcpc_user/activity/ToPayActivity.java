package com.hemaapp.wcpc_user.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseConfig;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.BaseUtil;
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

import java.util.ArrayList;

import xtom.frame.util.XtomSharedPreferencesUtil;

/**
 * Created by WangYuxia on 2016/5/20.
 */
public class ToPayActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private TextView text_needpay; //需要支付的金额
    private RelativeLayout layout_selectcoupon; //选择优惠券
    private LinearLayout layout_coupon; //显示金额
    private TextView text_coupon;

    private RelativeLayout layout_feeaccount;
    private TextView text_feeaccount;
    private CheckBox checkBox_feeaccount;

    private RelativeLayout layout_alipay;
    private CheckBox checkBox_alipay;
    private RelativeLayout layout_weixin;
    private CheckBox checkBox_weixin;
    private RelativeLayout layout_unipay;
    private CheckBox checkBox_unipay;
    private TextView text_submit;

    private String order_id, total_fee, fee, coupons_id="0", paypassword;
    private User user;
    IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);

    private int flag = 1; //1:余额支付，2:优惠券支付，3:其他支付

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_topay);
        super.onCreate(savedInstanceState);
        user = hm_WcpcUserApplication.getInstance().getUser();
        msgApi.registerApp(BaseConfig.APPID_WEIXIN);
        getNetWorker().clientGet(user.getToken(), user.getId());
    }

    private void initUserData(){
        text_needpay.setText(total_fee);
        text_feeaccount.setText("余额"+user.getFeeaccount()+"元");
        layout_coupon.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_GET:
                showProgressDialog("请稍后...");
                break;
            case ORDER_SAVE:
                showProgressDialog("请稍后...");
                break;
            case ALIPAY:
            case WEI_XIN:
            case UNIONPAY:
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_GET:
                cancelProgressDialog();
                break;
            case ORDER_SAVE:
                cancelProgressDialog();
                break;
            case ALIPAY:
            case WEI_XIN:
            case UNIONPAY:
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
            case CLIENT_GET:
                HemaArrayResult<User> uResult = (HemaArrayResult<User>) baseResult;
                user = uResult.getObjects().get(0);
                initUserData();
                break;
            case ORDER_SAVE:
                String paytype = netTask.getParams().get("paytype");
                if("3".equals(paytype)){
                    if(checkBox_alipay.isChecked()){
                        getNetWorker().alipay(user.getToken(), "2", order_id, total_fee);
                    }else if(checkBox_weixin.isChecked()){
                        getNetWorker().weixin(user.getToken(), "2", order_id, total_fee);
                    }else if(checkBox_unipay.isChecked()){
                        getNetWorker().unionpay(user.getToken(), "2", order_id, total_fee);
                    }
                }else{
                    title.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setResult(RESULT_OK, mIntent);
                            finish();
                        }
                    }, 1000);
                }
                break;
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
                HemaArrayResult<UnionTrade> unResult = (HemaArrayResult<UnionTrade>) baseResult;
                UnionTrade uTrade = unResult.getObjects().get(0);
                String uInfo = uTrade.getTn();
                UPPayAssistEx.startPayByJAR(mContext, PayActivity.class, null,
                        null, uInfo, BaseConfig.UNIONPAY_TESTMODE);
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
                showTextDialog(baseResult.getMsg());
                break;
            case ORDER_SAVE:
                String paytype = netTask.getParams().get("paytype");
                if(!"3".equals(paytype)){
                    showTextDialog(baseResult.getMsg());
                }
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
            case ORDER_SAVE:
                String paytype = netTask.getParams().get("paytype");
                if(!"3".equals(paytype)){
                    showTextDialog("支付失败");
                }
                break;
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        right = (TextView) findViewById(R.id.title_btn_right);
        title = (TextView) findViewById(R.id.title_text);

        text_needpay = (TextView) findViewById(R.id.textview_0);
        layout_selectcoupon = (RelativeLayout) findViewById(R.id.layout);
        layout_coupon = (LinearLayout) findViewById(R.id.layout_4);
        text_coupon = (TextView) findViewById(R.id.textview_1);

        layout_feeaccount = (RelativeLayout) findViewById(R.id.layout_3);
        text_feeaccount = (TextView) findViewById(R.id.textview_2);
        checkBox_feeaccount = (CheckBox) findViewById(R.id.checkbox_3);

        layout_alipay = (RelativeLayout) findViewById(R.id.layout_0);
        checkBox_alipay = (CheckBox) findViewById(R.id.checkbox_0);
        layout_weixin = (RelativeLayout) findViewById(R.id.layout_1);
        checkBox_weixin = (CheckBox) findViewById(R.id.checkbox_1);
        layout_unipay = (RelativeLayout) findViewById(R.id.layout_2);
        checkBox_unipay = (CheckBox) findViewById(R.id.checkbox_2);

        text_submit = (TextView) findViewById(R.id.button);
    }

    @Override
    protected void getExras() {
        order_id = mIntent.getStringExtra("id");
        total_fee = mIntent.getStringExtra("total_fee");
        fee = mIntent.getStringExtra("total_fee");
    }

    @Override
    protected void setListener() {
        title.setText("支付");
        right.setVisibility(View.INVISIBLE);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        layout_selectcoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(mContext, MyCouponListActivity.class);
                it.putExtra("keytype", "2");
                startActivityForResult(it, R.id.layout);
            }
        });

        layout_feeaccount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkBox_feeaccount.isChecked()) {
                    checkBox_feeaccount.setChecked(false);
                    checkBox_alipay.setChecked(false);
                    checkBox_weixin.setChecked(false);
                    checkBox_unipay.setChecked(false);
                } else {
                    checkBox_feeaccount.setChecked(true);
                    checkBox_alipay.setChecked(false);
                    checkBox_weixin.setChecked(false);
                    checkBox_unipay.setChecked(false);
                }
            }
        });

        checkBox_feeaccount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkBox_alipay.setChecked(false);
                checkBox_weixin.setChecked(false);
                checkBox_unipay.setChecked(false);
            }
        });

        layout_alipay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkBox_alipay.isChecked()) {
                    checkBox_feeaccount.setChecked(false);
                    checkBox_alipay.setChecked(false);
                    checkBox_weixin.setChecked(false);
                    checkBox_unipay.setChecked(false);
                } else {
                    checkBox_feeaccount.setChecked(false);
                    checkBox_alipay.setChecked(true);
                    checkBox_weixin.setChecked(false);
                    checkBox_unipay.setChecked(false);
                }
            }
        });

        checkBox_alipay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkBox_feeaccount.setChecked(false);
                checkBox_weixin.setChecked(false);
                checkBox_unipay.setChecked(false);
            }
        });

        layout_weixin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkBox_weixin.isChecked()) {
                    checkBox_feeaccount.setChecked(false);
                    checkBox_alipay.setChecked(false);
                    checkBox_weixin.setChecked(false);
                    checkBox_unipay.setChecked(false);
                } else {
                    checkBox_feeaccount.setChecked(false);
                    checkBox_alipay.setChecked(false);
                    checkBox_weixin.setChecked(true);
                    checkBox_unipay.setChecked(false);
                }
            }
        });

        checkBox_weixin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkBox_feeaccount.setChecked(false);
                checkBox_alipay.setChecked(false);
                checkBox_unipay.setChecked(false);
            }
        });

        layout_unipay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkBox_unipay.isChecked()) {
                    checkBox_feeaccount.setChecked(false);
                    checkBox_alipay.setChecked(false);
                    checkBox_weixin.setChecked(false);
                    checkBox_unipay.setChecked(false);
                } else {
                    checkBox_feeaccount.setChecked(false);
                    checkBox_alipay.setChecked(false);
                    checkBox_weixin.setChecked(false);
                    checkBox_unipay.setChecked(true);
                }
            }
        });

        checkBox_unipay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkBox_feeaccount.setChecked(false);
                checkBox_alipay.setChecked(false);
                checkBox_weixin.setChecked(false);
            }
        });

        text_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( !checkBox_feeaccount.isChecked() && !checkBox_alipay.isChecked()
                        && !checkBox_weixin.isChecked()
                        && !checkBox_unipay.isChecked()){
                    showTextDialog("请选择一种支付方式");
                    return;
                }else{
                    if(flag == 2){
                        getNetWorker().orderSave(user.getToken(), order_id, "2", coupons_id, total_fee, "0");
                    }else{
                        if(checkBox_feeaccount.isChecked()) {
                            String feeaccount = user.getFeeaccount();
                            double fee = Double.parseDouble(feeaccount);
                            if(fee < Double.parseDouble(total_fee)){
                                showTextDialog("抱歉，您的余额不足，无法支付");
                                return;
                            }
                            showInputPassword();
                        }else{
                            getNetWorker().orderSave(user.getToken(), order_id, "3", coupons_id, total_fee, "0");
                        }
                    }
                }
            }
        });
    }

    private PopupWindow pwdWindow;
    private ViewGroup mViewGroup;
    private ImageView image_close;
    private TextView text_ok;
    private EditText editText;
    private TextView text_forget;
    private ArrayList<TextView> textviews = new ArrayList<>();

    private void showInputPassword(){
        if (pwdWindow != null) {
            pwdWindow.dismiss();
            textviews.clear();
        }
        pwdWindow = new PopupWindow(mContext);
        pwdWindow.setWidth(FrameLayout.LayoutParams.MATCH_PARENT);
        pwdWindow.setHeight(FrameLayout.LayoutParams.MATCH_PARENT);
        pwdWindow.setBackgroundDrawable(new BitmapDrawable());
        pwdWindow.setFocusable(true);
        pwdWindow.setAnimationStyle(R.style.PopupAnimation);
        mViewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(
                R.layout.pop_inputpwd, null);
        image_close = (ImageView) mViewGroup.findViewById(R.id.close);
        text_ok = (TextView) mViewGroup.findViewById(R.id.button);
        editText = (EditText) mViewGroup.findViewById(R.id.edittext);
        text_forget = (TextView) mViewGroup.findViewById(R.id.textview);
        pwdWindow.setContentView(mViewGroup);
        pwdWindow.showAtLocation(mViewGroup, Gravity.CENTER, 0, 0);
        image_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwdWindow.dismiss();
            }
        });

        text_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwdWindow.dismiss();
                paypassword = editText.getText().toString();
                if(isNull(paypassword.replace(" ", ""))){
                    showTextDialog("请输入支付密码");
                    return;
                }

                getNetWorker().orderSave(user.getToken(), order_id, "1", coupons_id, total_fee, paypassword);
            }
        });

        text_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwdWindow.dismiss();
                Intent it = new Intent(mContext, ResetPayPasswordActivity.class);
                startActivity(it);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK)
            return;
        switch (requestCode){
            case R.id.layout:
                String money = data.getStringExtra("money");
                coupons_id = data.getStringExtra("id");
                double d = Double.parseDouble(money);
                double need = Double.parseDouble(fee);
                if(d >= need) {
                    total_fee = "0";
                    flag = 2;
                }else{
                    total_fee = String.valueOf(BaseUtil.get2double(need - d));
                }
                text_needpay.setText(total_fee);
                text_coupon.setText(money);
                layout_coupon.setVisibility(View.VISIBLE);
                break;
            default:
                if (data == null)
                    return;
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
                            setResult(RESULT_OK, mIntent);
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
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
            alipayHandler = new AlipayHandler(ToPayActivity.this);
        }

        @Override
        public void run() {
            PayTask alipay = new PayTask(ToPayActivity.this);
            // 调用支付接口，获取支付结果
            String result = alipay.pay(orderInfo);

            Message msg = new Message();
            msg.obj = result;
            alipayHandler.sendMessage(msg);
        }
    }

    private static class AlipayHandler extends Handler {
        ToPayActivity activity;

        public AlipayHandler(ToPayActivity activity) {
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
                        activity.setResult(RESULT_OK, activity.mIntent);
                        activity.finish();
                    }
                }, 1000);
            }else {
                activity.showTextDialog(result.getResult());
            }
        };
    }

}
