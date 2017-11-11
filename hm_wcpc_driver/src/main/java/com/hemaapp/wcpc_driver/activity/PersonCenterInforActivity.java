package com.hemaapp.wcpc_driver.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import com.hemaapp.hm_FrameWork.view.RoundedImageView;
import com.hemaapp.wcpc_driver.BaseActivity;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.EventBusModel;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.SysInitInfo;
import com.hemaapp.wcpc_driver.module.User;
import com.iflytek.sunflower.FlowerCollector;

import java.net.MalformedURLException;
import java.net.URL;

import de.greenrobot.event.EventBus;
import xtom.frame.image.load.XtomImageTask;
import xtom.frame.util.XtomSharedPreferencesUtil;

/**
 * 个人中心
 */
public class PersonCenterInforActivity extends BaseActivity {

    private ImageView left;
    private ImageView right;
    private ImageView view;
    private RoundedImageView image_avatar;
    private TextView text_realname;
    private ImageView image_sex;
    private TextView text_status;

    private TextView text_changeloginstatus;
    private TextView text_myorder;
    private TextView text_account;
    private TextView text_changepwd;
    private TextView text_history;
    private TextView text_reply;
    private TextView tv_phone;
    private View lv_phone;

    private User user;
    private PopupWindow mWindow;
    private ViewGroup mViewGroup;
    private TextView text_ok;
    private TextView text_cancel;
    private TextView text_chuche;
    private TextView text_xiuche;

    private String loginflag;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_personcenter);
        super.onCreate(savedInstanceState);
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.reset().init();
        user = hm_WcpcDriverApplication.getInstance().getUser();
        EventBus.getDefault().register(this);
        getNetWorker().clientGet(user.getToken(), user.getId());
        SysInitInfo initInfo = getApplicationContext()
                .getSysInitInfo();
        phone = initInfo.getSys_service_phone();
        tv_phone.setText(phone);
    }

    public void onEventMainThread(EventBusModel event) {
        switch (event.getType()) {
            case REFRESH_USER:
                getNetWorker().clientGet(user.getToken(), user.getId());
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        FlowerCollector.onResume(mContext);
        FlowerCollector.onPageStart("PushReceiver");
        super.onResume();
    }

    @Override
    protected void onPause() {
        FlowerCollector.onPageEnd("PushReceiver");
        FlowerCollector.onPause(mContext);
        super.onPause();
    }

    private void initUserData() {
        try {
            URL url = new URL(user.getAvatar());
            image_avatar.setCornerRadius(90);
            imageWorker.loadImage(new XtomImageTask(image_avatar, url, mContext));
        } catch (MalformedURLException e) {
            image_avatar.setImageResource(R.mipmap.default_driver);
        }
        text_realname.setText(user.getRealname());
        if ("男".equals(user.getSex()))
            image_sex.setImageResource(R.mipmap.img_sex_boy);
        else
            image_sex.setImageResource(R.mipmap.img_sex_girl);
        if ("0".equals(user.getLoginflag())) {
            text_status.setText("休车");
            text_status.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.img_center_status_n, 0, 0, 0);
            text_status.setTextColor(mContext.getResources().getColor(R.color.qianhui));
        } else {
            text_status.setText("出车");
            text_status.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.img_center_status_s, 0, 0, 0);
            text_status.setTextColor(mContext.getResources().getColor(R.color.yellow));
        }
        loginflag = user.getLoginflag();
        XtomSharedPreferencesUtil.save(mContext, "loginflag", user.getLoginflag());
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_GET:
            case LOGINFLAG_SAVE:
                showProgressDialog("请稍后...");
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_GET:
            case LOGINFLAG_SAVE:
                cancelProgressDialog();
                break;
            default:
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
                hm_WcpcDriverApplication.getInstance().setUser(user);
                initUserData();
                break;
            case LOGINFLAG_SAVE:
                user.setLoginflag(loginflag);
                hm_WcpcDriverApplication.getInstance().setUser(user);
                XtomSharedPreferencesUtil.save(mContext, "loginflag", user.getLoginflag());
                if ("0".equals(loginflag)) {
                    text_status.setText("休车");
                    text_status.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.img_center_status_n, 0, 0, 0);
                    text_status.setTextColor(mContext.getResources().getColor(R.color.qianhui));
                } else {
                    text_status.setText("出车");
                    text_status.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.img_center_status_s, 0, 0, 0);
                    text_status.setTextColor(mContext.getResources().getColor(R.color.yellow));
                }
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
        right = (ImageView) findViewById(R.id.title_btn_right2);
        image_avatar = (RoundedImageView) findViewById(R.id.imageview);
        text_realname = (TextView) findViewById(R.id.textview_0);
        image_sex = (ImageView) findViewById(R.id.imageview_0);
        text_status = (TextView) findViewById(R.id.textview_1);
        text_changeloginstatus = (TextView) findViewById(R.id.textview_2);
        text_myorder = (TextView) findViewById(R.id.textview_3);
        text_changepwd = (TextView) findViewById(R.id.textview_5);
        text_history = (TextView) findViewById(R.id.textview_6);
        text_reply = (TextView) findViewById(R.id.textview_7);
        text_account = (TextView) findViewById(R.id.textview_8);
        tv_phone = (TextView) findViewById(R.id.tv_tel);
        lv_phone = findViewById(R.id.lv_tel);
    }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setListener(right);
        setListener(image_avatar);
        setListener(text_changeloginstatus);
        setListener(text_myorder);
        setListener(text_changepwd);
        setListener(text_history);
        setListener(text_reply);
        setListener(text_account);
        setListener(lv_phone);
    }

    private void setListener(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it;
                switch (v.getId()) {
                    case R.id.title_btn_right2: //设置
                        it = new Intent(mContext, SetActivity.class);
                        startActivity(it);
                        break;
                    case R.id.imageview: //个人信息编辑
                        it = new Intent(mContext, PersonInforActivity.class);
                        startActivity(it);
                        break;
                    case R.id.textview_2: //选择状态
                        showStatusPopWindow();
                        break;
                    case R.id.textview_3: //我的乘客订单----已删
//                        it = new Intent(mContext, MyOrderActivity.class);
//                        startActivity(it);
                        break;
                    case R.id.textview_5: //账户密码修改
                        it = new Intent(mContext, PassWord0Activity.class);
                        startActivity(it);
                        break;
                    case R.id.textview_6: //历史订单
                        it = new Intent(mContext, HistoryActivity.class);
                        startActivity(it);
                        break;
                    case R.id.textview_7: //我的评价
                        it = new Intent(mContext, ReplyListActivity.class);
                        startActivity(it);
                        break;
                    case R.id.textview_8: //我的账户
                        it = new Intent(mContext, MyAccountActivity.class);
                        startActivity(it);
                        break;
                    case R.id.lv_tel:
                        showPhoneWindow();
                        break;
                }
            }
        });
    }

    private void showStatusPopWindow() {
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
                R.layout.pop_changestatus, null);
        text_ok = (TextView) mViewGroup.findViewById(R.id.textview_1);
        text_cancel = (TextView) mViewGroup.findViewById(R.id.textview_0);
        text_xiuche = (TextView) mViewGroup.findViewById(R.id.textview_2);
        text_chuche = (TextView) mViewGroup.findViewById(R.id.textview_3);
        mWindow.setContentView(mViewGroup);
        mWindow.showAtLocation(mViewGroup, Gravity.CENTER, 0, 0);
        text_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
            }
        });

        if ("0".equals(user.getLoginflag())) {
            text_xiuche.setTextColor(mContext.getResources().getColor(R.color.white));
            text_xiuche.setBackgroundResource(R.drawable.bg_changestatus);
            text_chuche.setTextColor(0xff737373);
            text_chuche.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        } else {
            text_chuche.setTextColor(mContext.getResources().getColor(R.color.white));
            text_chuche.setBackgroundResource(R.drawable.bg_changestatus);
            text_xiuche.setTextColor(0xff737373);
            text_xiuche.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        }

        text_xiuche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginflag = "0";
                text_xiuche.setTextColor(mContext.getResources().getColor(R.color.white));
                text_xiuche.setBackgroundResource(R.drawable.bg_changestatus);
                text_chuche.setTextColor(0xff737373);
                text_chuche.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
            }
        });

        text_chuche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginflag = "1";
                text_chuche.setTextColor(mContext.getResources().getColor(R.color.white));
                text_chuche.setBackgroundResource(R.drawable.bg_changestatus);
                text_xiuche.setTextColor(0xff737373);
                text_xiuche.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
            }
        });

        text_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
                getNetWorker().loginflagSave(user.getToken(), loginflag);
            }
        });
    }
    private TextView content1;
    private TextView content2;
    private TextView ok;
    private TextView cancel;

    private void showPhoneWindow() {
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
                R.layout.pop_phone, null);
        content1 = (TextView) mViewGroup.findViewById(R.id.textview);
        content2 = (TextView) mViewGroup.findViewById(R.id.textview_0);
        cancel = (TextView) mViewGroup.findViewById(R.id.textview_1);
        ok = (TextView) mViewGroup.findViewById(R.id.textview_2);
        mWindow.setContentView(mViewGroup);
        mWindow.showAtLocation(mViewGroup, Gravity.CENTER, 0, 0);
        content1.setText("拨打客服电话");
        content2.setText(phone);
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
}
