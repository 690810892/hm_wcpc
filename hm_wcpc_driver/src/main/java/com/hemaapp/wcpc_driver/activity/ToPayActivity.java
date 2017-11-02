package com.hemaapp.wcpc_driver.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.hemaapp.wcpc_driver.BaseActivity;
import com.hemaapp.wcpc_driver.BaseConfig;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.BaseUtil;
import com.hemaapp.wcpc_driver.EventBusConfig;
import com.hemaapp.wcpc_driver.EventBusModel;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.alipay.PayResult;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.AlipayTrade;
import com.hemaapp.wcpc_driver.module.UnionTrade;
import com.hemaapp.wcpc_driver.module.User;
import com.hemaapp.wcpc_driver.module.WeiXinPay;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.unionpay.UPPayAssistEx;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import xtom.frame.util.XtomSharedPreferencesUtil;

/**
 * 订单的支付界面
 */
public class ToPayActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private TextView text_needpay; //需要支付的金额

    private RelativeLayout layout_alipay;
    private CheckBox checkBox_alipay;
    private RelativeLayout layout_weixin;
    private CheckBox checkBox_weixin;
    private RelativeLayout layout_unipay;
    private CheckBox checkBox_unipay;

    private String order_id, total_fee;
    private User user;
    IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);

    private int flag = 3; //1:余额支付，2:优惠券支付，3:其他支付
    private WXPayReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_topay);
        super.onCreate(savedInstanceState);
        user = hm_WcpcDriverApplication.getInstance().getUser();
        msgApi.registerApp(BaseConfig.APPID_WEIXIN);
        mReceiver = new WXPayReceiver();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction("com.hemaapp.wcpc_user.wxpay");
        registerReceiver(mReceiver, mFilter);
        getNetWorker().clientGet(user.getToken(), user.getId());
    }

    private void initUserData() {
        text_needpay.setText(total_fee);
    }

    private class WXPayReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.hemaapp.wcpc_user.wxpay".equals(intent.getAction())) {
                int code = intent.getIntExtra("code", -1);
                switch (code) {
                    case 0:
                        EventBus.getDefault().post(new EventBusModel(EventBusConfig.REFRESH_BLOG_LIST));
                        buySuccess();
                        break;
                    case -1:
                        showTextDialog("支付失败");
                        break;
                    case -2:
                        showTextDialog("您取消了支付");
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void buySuccess() {
        showTextDialog("支付成功");
        title.postDelayed(new Runnable() {

            @Override
            public void run() {
                setResult(RESULT_OK);
                finish();
            }
        }, 1000);
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_GET:
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
                HemaArrayResult<UnionTrade> uResult1 = (HemaArrayResult<UnionTrade>) baseResult;
                UnionTrade uTrade = uResult1.getObjects().get(0);
                String uInfo = uTrade.getTn();
                UPPayAssistEx.startPay(this, null, null, uInfo, BaseConfig.UNIONPAY_TESTMODE);
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
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        right = (TextView) findViewById(R.id.title_btn_right);
        title = (TextView) findViewById(R.id.title_text);

        text_needpay = (TextView) findViewById(R.id.textview_0);

        layout_alipay = (RelativeLayout) findViewById(R.id.layout_0);
        checkBox_alipay = (CheckBox) findViewById(R.id.checkbox_0);
        layout_weixin = (RelativeLayout) findViewById(R.id.layout_1);
        checkBox_weixin = (CheckBox) findViewById(R.id.checkbox_1);
        layout_unipay = (RelativeLayout) findViewById(R.id.layout_2);
        checkBox_unipay = (CheckBox) findViewById(R.id.checkbox_2);
    }

    @Override
    protected void getExras() {
        order_id = mIntent.getStringExtra("id");
        total_fee = mIntent.getStringExtra("total_fee");
    }

    @Override
    protected void setListener() {
        title.setText("支付");
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

                if (!checkBox_alipay.isChecked()
                        && !checkBox_weixin.isChecked()
                        && !checkBox_unipay.isChecked()) {
                    showTextDialog("请选择一种支付方式");
                    return;
                } else {
                    if (checkBox_alipay.isChecked()) {
                        getNetWorker().alipay(user.getToken(), "3", order_id, total_fee);
                    } else if (checkBox_weixin.isChecked()) {
                        getNetWorker().weixin(user.getToken(), "3", order_id, total_fee);
                    } else if (checkBox_unipay.isChecked()) {
                        getNetWorker().unionpay(user.getToken(), "3", order_id, total_fee);
                    }

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case R.id.layout:
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
                    EventBus.getDefault().post(new EventBusModel(EventBusConfig.REFRESH_BLOG_LIST));
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
            // 构造PayTask 对象
            PayTask alipay = new PayTask(ToPayActivity.this);
            // 调用支付接口，获取支付结果
            String result = alipay.pay(orderInfo);

            log_i("result = " + result);
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

        public void handleMessage(Message msg) {
            if (msg == null) {
                activity.showTextDialog("支付失败");
                return;
            }
            PayResult result = new PayResult((String) msg.obj);
            String staus = result.getResultStatus();
            switch (staus) {
                case "9000":
                    activity.showTextDialog("支付成功");
                    EventBus.getDefault().post(new EventBusModel(EventBusConfig.REFRESH_BLOG_LIST));
                    postAtTime(new Runnable() {

                        @Override
                        public void run() {
                            activity.finish();
                        }
                    }, 1500);
                    break;
                case "8000":
                    activity.showTextDialog("支付结果确认中");
                    break;
                default:
                    activity.showTextDialog("您取消了支付");
                    break;
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
