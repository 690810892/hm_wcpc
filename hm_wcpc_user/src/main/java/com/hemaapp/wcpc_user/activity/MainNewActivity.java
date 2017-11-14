package com.hemaapp.wcpc_user.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.BaseUtil;
import com.hemaapp.wcpc_user.EventBusModel;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.ToLogin;
import com.hemaapp.wcpc_user.UpGrade;
import com.hemaapp.wcpc_user.getui.GeTuiIntentService;
import com.hemaapp.wcpc_user.getui.PushUtils;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.DistrictInfor;
import com.hemaapp.wcpc_user.module.TimeRule;
import com.hemaapp.wcpc_user.module.User;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.PushService;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import xtom.frame.XtomActivityManager;
import xtom.frame.util.XtomDeviceUuidFactory;
import xtom.frame.util.XtomSharedPreferencesUtil;
import xtom.frame.util.XtomToastUtil;

/**
 * 首页
 */
public class MainNewActivity extends BaseActivity implements View.OnClickListener {
    private ImageView image_left;
    private ImageView image_right;
    private ImageView image_point;

    private ImageView ivMytrip; //我的行程
    private ImageView ivSend; //
    private User user;
    private int count;

    private long time;// 用于判断二次点击返回键的时间间隔
    private static MainNewActivity activity;
    public ArrayList<DistrictInfor> allDistricts = new ArrayList<>();

    public static MainNewActivity getInstance() {
        return activity;
    }
    private PopupWindow mWindow;
    private ViewGroup mViewGroup;
    private String phone;
    private String isFirst;
    private UpGrade upGrade;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        setContentView(R.layout.activity_main_new);
        super.onCreate(savedInstanceState);
//        mImmersionBar = ImmersionBar.with(this);
//        mImmersionBar.reset().init();
        EventBus.getDefault().register(this);
        upGrade = new UpGrade(mContext) {
            @Override
            public void NoNeedUpdate() {
            }
        };
        isFirst=XtomSharedPreferencesUtil.get(mContext,"isFirst");
        if (isNull(isFirst)){
            isFirst="true";
        }
        user = hm_WcpcUserApplication.getInstance().getUser();
        phone=hm_WcpcUserApplication.getInstance().getSysInitInfo().getSys_service_phone();
        getNetWorker().timeRule();
        if (user == null) {
            image_point.setVisibility(View.INVISIBLE);
        } else
            getNetWorker().noticeUnread(user.getToken(), "2", "1");
        getNetWorker().cityList("0");//获取已开通城市
        image_left.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!BaseUtil.isOPen(mContext)){
                   GpsTip();
                }
            }
        },800);
        image_left.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFirst.equals("true")){
                    Intent it=new Intent(mContext,IntroductionActivity.class);
                    startActivity(it,R.anim.bottom_in,R.anim.bottom_in);
                }
            }
        },900);
    }

    public void onEventMainThread(EventBusModel event) {
        switch (event.getType()) {
            case NEW_MESSAGE:
                getNetWorker().noticeUnread(user.getToken(), "2", "1");
                break;
            case CLIENT_ID:
                saveDevice(event.getContent());
                break;
            case REFRESH_CUSTOMER_INFO:
                getNetWorker().clientGet(user.getToken(), user.getId());
                break;
        }
    }

    @Override
    protected boolean onKeyBack() {
        if ((System.currentTimeMillis() - time) >= 2000) {
            XtomToastUtil.showShortToast(mContext, "再按一次返回键退出程序");
            time = System.currentTimeMillis();
        } else {
            XtomActivityManager.finishAll();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        user = hm_WcpcUserApplication.getInstance().getUser();
        checkPermission();
        upGrade.check();
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask hemaNetTask) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information) {
            case ADVERTISE_LIST:
                showProgressDialog("请稍后...");
                break;
            case NOTICE_UNREAD:
                break;
            case CAN_TRIPS:
                showProgressDialog("请稍后...");
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask hemaNetTask) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information) {
            case ADVERTISE_LIST:
            case CAN_TRIPS:
                cancelProgressDialog();
                break;
            case NOTICE_UNREAD:
                break;
        }
    }

    @Override
    protected void callBackForServerSuccess(HemaNetTask hemaNetTask, HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information) {
            case NOTICE_UNREAD:
                HemaArrayResult<String> cResult = (HemaArrayResult<String>) baseResult;
                count = Integer.parseInt(isNull(cResult.getObjects().get(0)) ? "0" : cResult.getObjects().get(0));
                if (count == 0)
                    image_point.setVisibility(View.INVISIBLE);
                else
                    image_point.setVisibility(View.VISIBLE);
                break;
            case CAN_TRIPS:
                cancelProgressDialog();
                HemaArrayResult<String> sResult = (HemaArrayResult<String>) baseResult;
                String keytype = sResult.getObjects().get(0);
                if ("1".equals(keytype)) {
                    Intent it = new Intent(mContext, SendActivity.class);
                    startActivity(it);
                } else if ("2".equals(keytype)) {
                    CanNotTip();
                    return;
                } else {
//                    Intent it = new Intent(mContext, SendActivity.class);
//                    startActivity(it);
                    String start = BaseUtil.TransTimeHour(XtomSharedPreferencesUtil.get(mContext, "order_start"), "HH:mm");
                    String end = BaseUtil.TransTimeHour(XtomSharedPreferencesUtil.get(mContext, "order_end"), "HH:mm");
                    if (isNull(start)) {
                        start = "5:00";
                        end="20:00";
                    }
                    TimeTip(start,end);
                }
                break;
            case CITY_LIST:
                HemaArrayResult<DistrictInfor> CResult = (HemaArrayResult<DistrictInfor>) baseResult;
                allDistricts = CResult.getObjects();
                String citys = "";
                for (DistrictInfor infor : allDistricts) {
                    citys = citys + infor.getName();
                }
                XtomSharedPreferencesUtil.save(mContext, "citys", citys);
                break;
            case TIME_RULE:
                HemaArrayResult<TimeRule> tResult = (HemaArrayResult<TimeRule>) baseResult;
                TimeRule rule = tResult.getObjects().get(0);
                XtomSharedPreferencesUtil.save(mContext, "order_start", rule.getTime1_begin());
                XtomSharedPreferencesUtil.save(mContext, "order_end", rule.getTime1_end());
                XtomSharedPreferencesUtil.save(mContext, "pin_end", rule.getTime2_end());
                XtomSharedPreferencesUtil.save(mContext, "pin_start", rule.getTime2_begin());
                break;
            case CLIENT_GET:
                HemaArrayResult<User> uResult = (HemaArrayResult<User>) baseResult;
                user = uResult.getObjects().get(0);
                hm_WcpcUserApplication.getInstance().setUser(user);
                break;
        }
    }

    public ArrayList<DistrictInfor> getCitys() {
        return allDistricts;
    }


    @Override
    protected void callBackForGetDataFailed(HemaNetTask hemaNetTask, int i) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information) {
            case ADVERTISE_LIST:
                showTextDialog("获取数据失败");
                break;
            case CAN_TRIPS:
                showTextDialog("检查失败，请稍后重试");
                break;
        }
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case ADVERTISE_LIST:
            case CAN_TRIPS:
                showTextDialog(baseResult.getMsg());
                break;
        }
    }


    @Override
    protected void findView() {
        image_left = (ImageView) findViewById(R.id.title_btn_left);
        image_right = (ImageView) findViewById(R.id.title_btn_right_image);
        image_point = (ImageView) findViewById(R.id.title_point);
        ivMytrip = (ImageView) findViewById(R.id.iv_mytrip);
        ivSend = (ImageView) findViewById(R.id.iv_send);
    }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
        image_left.setOnClickListener(this);
        image_right.setOnClickListener(this);
        ivMytrip.setOnClickListener(this);
        ivSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        user = hm_WcpcUserApplication.getInstance().getUser();
        Intent it;
        switch (view.getId()) {
            case R.id.title_btn_left:
                if (user == null) {
                    ToLogin.showLogin(mContext);
                } else {
                    it = new Intent(mContext, PersonCenterNewActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.title_btn_right_image:
                if (user == null) {
                    ToLogin.showLogin(mContext);
                } else {
                    it = new Intent(mContext, NoticeListActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.iv_mytrip:
                if (user == null) {
                    ToLogin.showLogin(mContext);
                } else {
                    it = new Intent(mContext, MyCurrentTrip2Activity.class);
                    startActivity(it);
                }
                break;
            case R.id.tv_car_owner:
//                it = new Intent(mContext, CarOwerListActivity.class);
//                startActivity(it);
                break;
            case R.id.iv_send:
                if (user == null) {
                    ToLogin.showLogin(mContext);
                } else
                    getNetWorker().canTrips(user.getToken());
                break;
        }
    }

    /*个推相关*/
    // DemoPushService.class 自定义服务名称, 核心服务
    private Class userPushService = PushService.class;
    private static final int REQUEST_PERMISSION = 0;

    private void checkPermission() {
        PackageManager pkgManager = getPackageManager();

        // 读写 sd card 权限非常重要, android6.0默认禁止的, 建议初始化之前就弹窗让用户赋予该权限
        boolean sdCardWritePermission =
                pkgManager.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName()) == PackageManager.PERMISSION_GRANTED;

        // read phone state用于获取 imei 设备信息
        boolean phoneSatePermission =
                pkgManager.checkPermission(Manifest.permission.READ_PHONE_STATE, getPackageName()) == PackageManager.PERMISSION_GRANTED;

        if (Build.VERSION.SDK_INT >= 23 && !sdCardWritePermission || !phoneSatePermission) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                    REQUEST_PERMISSION);
        } else {
            PushManager.getInstance().initialize(this.getApplicationContext(), userPushService);
        }

        // 注册 intentService 后 PushDemoReceiver 无效, sdk 会使用 DemoIntentService 传递数据,
        // AndroidManifest 对应保留一个即可(如果注册 DemoIntentService, 可以去掉 PushDemoReceiver, 如果注册了
        // IntentService, 必须在 AndroidManifest 中声明)
        PushManager.getInstance().initialize(mContext.getApplicationContext(), PushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), GeTuiIntentService.class);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if ((grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                PushManager.getInstance().initialize(this.getApplicationContext(), userPushService);
            } else {
                Log.e("MainActivity", "We highly recommend that you need to grant the special permissions before initializing the SDK, otherwise some "
                        + "functions will not work");
                PushManager.getInstance().initialize(this.getApplicationContext(), userPushService);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void saveDevice(String channelId) {
        User user = getApplicationContext().getUser();
        if (user == null) {
            return;
        }
        String deviceId = PushUtils.getUserId(mContext);
        if (isNull(deviceId)) {// 如果deviceId为空时，保存为手机串号
            deviceId = XtomDeviceUuidFactory.get(mContext);
        }
        getNetWorker().deviceSave(user.getToken(),
                deviceId, "2", channelId);
    }

    /*个推相关结束*/

    private void TimeTip(String start,String end) {
        if (mWindow != null) {
            mWindow.dismiss();
        }
        mWindow = new PopupWindow(mContext);
        mWindow.setWidth(FrameLayout.LayoutParams.MATCH_PARENT);
        mWindow.setHeight(FrameLayout.LayoutParams.MATCH_PARENT);
        mWindow.setBackgroundDrawable(new BitmapDrawable());
        mWindow.setFocusable(true);
        mWindow.setAnimationStyle(R.style.PopupAnimation);
        mViewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(
                R.layout.pop_first_tip, null);
        TextView  cancel = (TextView) mViewGroup.findViewById(R.id.textview_1);
        TextView  ok = (TextView) mViewGroup.findViewById(R.id.textview_2);
        TextView  title1 = (TextView) mViewGroup.findViewById(R.id.textview);
        TextView  title2 = (TextView) mViewGroup.findViewById(R.id.textview_0);
        mWindow.setContentView(mViewGroup);
        mWindow.showAtLocation(mViewGroup, Gravity.CENTER, 0, 0);
        title1.setText("当前时间段不支持手机下单，\n如有需要请联系客服"+phone);
        title2.setText("可下单时间段为"+start+"-"+end);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
                //Intent.ACTION_CALL 直接拨打电话，就是进入拨打电话界面，电话已经被拨打出去了。
                //Intent.ACTION_DIAL 是进入拨打电话界面，电话号码已经输入了，但是需要人为的按拨打电话键，才能播出电话。
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                        + phone));
                startActivity(intent);
            }
        });
    }
    private void CanNotTip() {
        if (mWindow != null) {
            mWindow.dismiss();
        }
        mWindow = new PopupWindow(mContext);
        mWindow.setWidth(FrameLayout.LayoutParams.MATCH_PARENT);
        mWindow.setHeight(FrameLayout.LayoutParams.MATCH_PARENT);
        mWindow.setBackgroundDrawable(new BitmapDrawable());
        mWindow.setFocusable(true);
        mWindow.setAnimationStyle(R.style.PopupAnimation);
        mViewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(
                R.layout.pop_first_tip, null);
        TextView  cancel = (TextView) mViewGroup.findViewById(R.id.textview_1);
        TextView  ok = (TextView) mViewGroup.findViewById(R.id.textview_2);
        TextView  title1 = (TextView) mViewGroup.findViewById(R.id.textview);
        TextView  title2 = (TextView) mViewGroup.findViewById(R.id.textview_0);
        mWindow.setContentView(mViewGroup);
        mWindow.showAtLocation(mViewGroup, Gravity.CENTER, 0, 0);
        title1.setText(" 您有未完成的行程，\n暂不能发布新的行程");
        title2.setText("前去我的行程查看正在进行的行程？");
        cancel.setText("取消");
        ok.setText("确定");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
                Intent it = new Intent(mContext, MyCurrentTrip2Activity.class);
                startActivity(it);
            }
        });
    }
    private void GpsTip() {
        if (mWindow != null) {
            mWindow.dismiss();
        }
        mWindow = new PopupWindow(mContext);
        mWindow.setWidth(FrameLayout.LayoutParams.MATCH_PARENT);
        mWindow.setHeight(FrameLayout.LayoutParams.MATCH_PARENT);
        mWindow.setBackgroundDrawable(new BitmapDrawable());
        mWindow.setFocusable(true);
        mWindow.setAnimationStyle(R.style.PopupAnimation);
        mViewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(
                R.layout.pop_first_tip, null);
        TextView  cancel = (TextView) mViewGroup.findViewById(R.id.textview_1);
        TextView  ok = (TextView) mViewGroup.findViewById(R.id.textview_2);
        TextView  title1 = (TextView) mViewGroup.findViewById(R.id.textview);
        TextView  title2 = (TextView) mViewGroup.findViewById(R.id.textview_0);
        mWindow.setContentView(mViewGroup);
        mWindow.showAtLocation(mViewGroup, Gravity.CENTER, 0, 0);
        title1.setText("请打开GPS定位");
        title2.setText("开启GPS定位功能才能正常使用地图功能");
        cancel.setText("取消");
        ok.setText("去打开");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
                // 让用户打开GPS
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 0);
            }
        });
    }
}
