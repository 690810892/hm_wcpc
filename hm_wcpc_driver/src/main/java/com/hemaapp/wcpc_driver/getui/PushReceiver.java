package com.hemaapp.wcpc_driver.getui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.activity.NoticeListActivity;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.sunflower.FlowerCollector;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

import org.json.JSONException;
import org.json.JSONObject;

public class PushReceiver extends BroadcastReceiver {

	public static final String TAG = "PushReceiver";
	
	
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));

        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                // String appid = bundle.getString("appid");
                byte[] payload = bundle.getByteArray("payload");

                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");

                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
                System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));

                if (payload != null) {
                    String data = new String(payload);

                    Log.d("GetuiSdkDemo", "receiver payload : " + data);
                    
                    String msg_content = "";
            		String msg_keytype = "";
            		String msg_keyid = "";
            		try {
            			JSONObject msgJson = new JSONObject(data);
            			msg_content = msgJson.getString("msg");
            			msg_keytype = msgJson.getString("keyType");
            			msg_keyid = msgJson.getString("keyId");
            		} catch (JSONException e) {
            			return;
            		}

            		if (msg_content == null || TextUtils.isEmpty(msg_content))
            			msg_content = context.getString(R.string.app_name);

            		mynotify(context, msg_content, msg_keytype,msg_keyid);
            		PushUtils.savemsgreadflag(context, true, msg_keytype);

            		Intent msgIntent = new Intent();
            		msgIntent.setAction("com.hemaapp.push.msg");
            		// 发送 一个无序广播
            		context.sendBroadcast(msgIntent);
					if(msg_keytype.equals("3")){
						startRead(context, msg_content);
					}
                }
                break;

            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                String userId = bundle.getString("clientid");
                PushUtils.saveuid(context, userId);
        		PushUtils.saveChannelId(context, userId);
        		Intent clientIntent = new Intent();
        		clientIntent.setAction("com.hemaapp.push.connect");
        		// 发送 一个无序广播
        		context.sendBroadcast(clientIntent);
                break;
            case PushConsts.THIRDPART_FEEDBACK:
                break;
            default:
            	System.out.println(bundle);
                break;
        }
    }

	/**
	 *  ---------------语音播报开始----------------
	 * */
	// 语音合成对象
	private static SpeechSynthesizer mTts;
	private String content;

	private void startRead(Context context, String msg_content){
		content = msg_content;
		if(mTts == null){
			mTts = SpeechSynthesizer.createSynthesizer(context, null);
		}

		// 移动数据分析，收集开始合成事件
		FlowerCollector.onEvent(context, "tts_play");
		// 设置参数
		setParam();
		mTts.startSpeaking(content, mTtsListener);
	}

	/**
	 * 合成回调监听。
	 */
	private SynthesizerListener mTtsListener = new SynthesizerListener() {

		@Override
		public void onSpeakBegin() {
		}

		@Override
		public void onSpeakPaused() {
		}

		@Override
		public void onSpeakResumed() {
		}

		@Override
		public void onBufferProgress(int percent, int beginPos, int endPos,
									 String info) {
		}

		@Override
		public void onSpeakProgress(int percent, int beginPos, int endPos) {
		}

		@Override
		public void onCompleted(SpeechError error) {
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
		}
	};

	/**
	 * 参数设置
	 * @return
	 */
	private void setParam(){
		// 清空参数
		mTts.setParameter(SpeechConstant.PARAMS, null);
		// 根据合成引擎设置相应参数
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		// 设置在线合成发音人
		mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
		//设置合成语速
		mTts.setParameter(SpeechConstant.SPEED, "50");
		//设置合成音调
		mTts.setParameter(SpeechConstant.PITCH, "50");
		//设置合成音量
		mTts.setParameter(SpeechConstant.VOLUME, "50");
		//设置播放器音频流类型
		mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
		// 设置播放合成音频打断音乐播放，默认为true
		mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
		//设置云端
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
	}

	public static void stop(){
		if(mTts != null){
			mTts.stopSpeaking();
			// 退出时释放连接
			mTts.destroy();
		}
	}

	/**
	 *  ---------------语音播报结束----------------
	 * */
    
	public void mynotify(Context context, String content,
                         String keytype, String keyid) {
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context);
		Notification notification ;
		Intent it ;
		builder.setSmallIcon(R.mipmap.ic_launcher);
		builder.setWhen(System.currentTimeMillis());
		builder.setContentTitle(context.getString(R.string.app_name));
		builder.setContentText(content).setTicker(content);
		it = new Intent(context, NoticeListActivity.class);
		PendingIntent pi = PendingIntent.getActivity(context, 0, it,
				PendingIntent.FLAG_CANCEL_CURRENT);
		builder.setContentIntent(pi);
		notification = builder.build();
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.defaults = Notification.DEFAULT_LIGHTS;
		notification.defaults |= Notification.DEFAULT_SOUND;
		nm.notify(0, notification);
	}
}
