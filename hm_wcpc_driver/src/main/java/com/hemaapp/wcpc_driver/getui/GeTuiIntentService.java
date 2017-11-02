package com.hemaapp.wcpc_driver.getui;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.hemaapp.wcpc_driver.EventBusConfig;
import com.hemaapp.wcpc_driver.EventBusModel;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.activity.MainActivity;
import com.hemaapp.wcpc_driver.activity.MainNewActivity;
import com.hemaapp.wcpc_driver.activity.NoticeListActivity;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.sunflower.FlowerCollector;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.message.FeedbackCmdMessage;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.igexin.sdk.message.SetTagCmdMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.greenrobot.event.EventBus;

import static com.hemaapp.wcpc_driver.EventBusConfig.NEW_MESSAGE;


/**
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 * onReceiveMessageData 处理透传消息<br>
 * onReceiveClientId 接收 cid <br>
 * onReceiveOnlineState cid 离线上线通知 <br>
 * onReceiveCommandResult 各种事件处理回执 <br>
 */
public class GeTuiIntentService extends GTIntentService {

    private static final String TAG = "GeTuiIntentService";

    /**
     * 为了观察透传数据变化.
     */
    private static int cnt;

    public GeTuiIntentService() {

    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
        Log.d(TAG, "onReceiveServicePid -> " + pid);
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage gtTransmitMessage) {
        String appid = gtTransmitMessage.getAppid();
        String taskid = gtTransmitMessage.getTaskId();
        String messageid = gtTransmitMessage.getMessageId();
        byte[] payload = gtTransmitMessage.getPayload();
        String pkg = gtTransmitMessage.getPkgName();
        String cid = gtTransmitMessage.getClientId();

        if (payload == null) {
            Log.i(TAG, "receiver payload = null");
        } else {
            String data = new String(payload);
            Log.e(TAG, data);
            String keyType = "";
            String keyId = "";
            String msg = "";
            String msg_nickname = "";
            String msg_avatar = "";
            try {
                JSONObject msgJson = new JSONObject(data);
                keyType = msgJson.getString("keyType");
                keyId = msgJson.getString("keyId");
                msg = msgJson.getString("msg");
            } catch (JSONException e) {
                Log.e("msgJsonFailed", e.getMessage());
                keyType = "-10";
                keyId = "-10";
                msg = "消息通知格式错误";
            }
            try {
                JSONObject msgJson = new JSONObject(data);
                msg_nickname = msgJson.getString("nickname");
                msg_avatar = msgJson.getString("avatar");
            } catch (JSONException e) {
                Log.e("msgJsonFailed", e.getMessage());
                msg_nickname = "-10";
                msg_avatar = "-10";
            }
            pushModel = new PushModel(keyType, keyId, msg,msg_nickname,msg_avatar);
            EventBus.getDefault().post(new EventBusModel(NEW_MESSAGE));
            EventBus.getDefault().post(new EventBusModel(EventBusConfig.REFRESH_BLOG_LIST));
            if(keyType.equals("10")||keyType.equals("11")||keyType.equals("6")){
                startRead(context, msg);
            }
            mynotify(context);
            PushUtils.savemsgreadflag(context, true, keyType);

            Intent msgIntent = new Intent();
            msgIntent.setAction("com.hemaapp.push.msg");
            // 发送 一个无序广播
            context.sendBroadcast(msgIntent);

        }

        Log.d(TAG, "----------------------------------------------------------------------------------------------");
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        EventBus.getDefault().post(new EventBusModel(EventBusConfig.CLIENT_ID, clientid));
        Log.e(TAG, "onReceiveClientId -> " + "clientid = " + clientid);
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
        Log.d(TAG, "onReceiveOnlineState -> " + (online ? "online" : "offline"));
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        Log.d(TAG, "onReceiveCommandResult -> " + cmdMessage);

        int action = cmdMessage.getAction();

        if (action == PushConsts.SET_TAG_RESULT) {
            setTagResult((SetTagCmdMessage) cmdMessage);
        } else if ((action == PushConsts.THIRDPART_FEEDBACK)) {
            feedbackResult((FeedbackCmdMessage) cmdMessage);
        }
    }

    private void setTagResult(SetTagCmdMessage setTagCmdMsg) {

    }

    private void feedbackResult(FeedbackCmdMessage feedbackCmdMsg) {
        String appid = feedbackCmdMsg.getAppid();
        String taskid = feedbackCmdMsg.getTaskId();
        String actionid = feedbackCmdMsg.getActionId();
        String result = feedbackCmdMsg.getResult();
        long timestamp = feedbackCmdMsg.getTimeStamp();
        String cid = feedbackCmdMsg.getClientId();

        Log.d(TAG, "onReceiveCommandResult -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nactionid = " + actionid + "\nresult = " + result
                + "\ncid = " + cid + "\ntimestamp = " + timestamp);
    }


    private PushModel pushModel;

    /**
     * @param context
     */
    public void mynotify(Context context) {
        Log.e(TAG, "notify-ing....");
        Log.e(TAG, "msg=" + pushModel.getMsg());
        Log.e(TAG, "keytype=" + pushModel.getKeyType());

        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);
        Notification notification = null;
        Intent intent = null;
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle(context.getString(R.string.app_name));
        builder.setContentText(pushModel.getMsg()).setTicker(pushModel.getMsg());

        if (isAppRunning(context)) {//已经运行
            hm_WcpcDriverApplication application = hm_WcpcDriverApplication.getInstance();
          //  application.setPushModel(pushModel);//在application中保存推送数据
            switch (pushModel.getKeyType()) {//1系统通知
                case "1"://消息通知
                    intent = new Intent(context, NoticeListActivity.class);
                    intent.putExtra("pagerPosition", 1);
                    break;
                case "3"://乘客确认上车
                    intent = new Intent(context, MainNewActivity.class);
                    intent.putExtra("pagerPosition", 0);
                    break;
                case "5"://5
                    intent = new Intent(context, MainNewActivity.class);
                    intent.putExtra("pagerPosition", 0);
                    break;
                case "6"://乘客已支付
                    intent = new Intent(context, MainNewActivity.class);
                    intent.putExtra("pagerPosition", 0);
                    break;
                case "10"://用户取消行程
                    intent = new Intent(context, MainNewActivity.class);
                    intent.putExtra("pagerPosition", 0);
                    break;
                default:
                    intent = new Intent(context, MainNewActivity.class);
                    intent.putExtra("pagerPosition", 0);
                    break;
            }
        } else {//未运行
            intent = context.getPackageManager().getLaunchIntentForPackage(context.getApplicationContext().getPackageName());
            intent.putExtra("PushModel", pushModel);
        }
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pi);
        notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_SOUND;
        nm.notify(0, notification);
    }

    /**
     * 判断是否运行
     *
     * @param context
     * @return
     */
    private boolean isAppRunning(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getApplicationContext().getPackageName();
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(packageName) || info.baseActivity.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }
    private boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }

        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }

        return false;
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
}
