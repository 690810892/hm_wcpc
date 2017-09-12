package com.hemaapp.wcpc_user.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.hemaapp.wcpc_user.BaseConfig;
import com.hemaapp.wcpc_user.R;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by WangYuxia on 2016/5/18.
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    private IWXAPI api;
    TextView textView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pay_result);
        textView = (TextView) findViewById(R.id.textview);
        api = WXAPIFactory.createWXAPI(this, BaseConfig.APPID_WEIXIN);

        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.d("TAG", "onPayFinish, errCode = " + resp.errCode);

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            switch (resp.errCode) {
                case 0: //成功
                    textView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setResult(RESULT_OK, getIntent());
                            finish();
                        }
                    }, 1000);
                    break;
                case -1: //支付失败
                    finish();
                    break;
                case -2: //支付取消
                    finish();
                    break;
            }
        }
    }
}
