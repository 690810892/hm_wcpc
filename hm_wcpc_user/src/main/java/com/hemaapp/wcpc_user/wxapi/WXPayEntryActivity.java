package com.hemaapp.wcpc_user.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.hemaapp.wcpc_user.BaseConfig;
import com.hemaapp.wcpc_user.R;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

//	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_result);

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
		// 0成功，展示成功页面
		// -1错误，可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
		// -2用户取消，无需处理。发生场景：用户不支付了，点击取消，返回APP。

//		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
//		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle("提示");
//			builder.setMessage("errCode=" + resp.errCode);
//			builder.show();
//		}
		Intent it = new Intent();
		it.setAction("com.hemaapp.wcpc_user.wxpay");
		it.putExtra("code", resp.errCode);
		sendBroadcast(it);
		finish();
	}
}