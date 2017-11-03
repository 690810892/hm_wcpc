package com.hemaapp.wcpc_user.getui;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.hemaapp.wcpc_user.EventBusConfig;
import com.hemaapp.wcpc_user.EventBusModel;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.activity.MainActivity;
import com.hemaapp.wcpc_user.activity.MainNewActivity;
import com.hemaapp.wcpc_user.activity.NoticeListActivity;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
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

import static com.hemaapp.wcpc_user.EventBusConfig.NEW_MESSAGE;
import static com.hemaapp.wcpc_user.EventBusConfig.REFRESH_BLOG_LIST;


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
            EventBus.getDefault().post(new EventBusModel(REFRESH_BLOG_LIST));
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
            hm_WcpcUserApplication application = hm_WcpcUserApplication.getInstance();
          //  application.setPushModel(pushModel);//在application中保存推送数据
            switch (pushModel.getKeyType()) {//1系统通知
                case "1"://消息通知
                    intent = new Intent(context, NoticeListActivity.class);
                    intent.putExtra("pagerPosition", 1);
                    break;
                case "2"://司机确认上车
                    intent = new Intent(context, MainNewActivity.class);
                    intent.putExtra("pagerPosition", 0);
                    break;
                case "3"://
//                    intent = new Intent(context, LeaveActivity.class);
//                    intent.putExtra("pagerPosition", 1);
                    break;
                case "4"://司机确认送达
                    intent = new Intent(context, MainNewActivity.class);
                    intent.putExtra("pagerPosition", 0);
                    break;
                case "7"://司机已代付
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
}
