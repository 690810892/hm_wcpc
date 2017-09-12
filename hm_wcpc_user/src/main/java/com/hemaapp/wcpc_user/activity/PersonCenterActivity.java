package com.hemaapp.wcpc_user.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.view.RoundedImageView;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.SysInitInfo;
import com.hemaapp.wcpc_user.module.User;

import java.net.MalformedURLException;
import java.net.URL;

import xtom.frame.image.load.XtomImageTask;

/**
 * Created by WangYuxia on 2016/5/6.
 */
public class PersonCenterActivity extends BaseActivity {

    private ScrollView scrollView;
    private FrameLayout frameLayout;
    private ImageView f_left;
    private ImageView f_right;

    private ImageView s_left;
    private ImageView s_right;
    private RoundedImageView image_avatar;
    private TextView text_realname;
    private ImageView image_sex;
    private TextView text_servicecount;

    private RelativeLayout layout_mypurse;//我的钱包
    private TextView text_youhuiquan; //优惠券
    private TextView text_feeaccount; //账户余额
    private TextView text_charge; //充值
    private TextView text_tixian; //提现

    private RelativeLayout layout_allorder; //全部订单
    private TextView text_topay; //待支付
    private TextView text_togo; //未出行
    private TextView text_cancel; //已取消
    private TextView text_pingjia; //待评价
    private TextView text_complete; //已完成

    private TextView text_mytrips; //我的行程
    private TextView text_changepwd; //修改密码
    private TextView text_set; //设置

    private PopupWindow mWindow;
    private ViewGroup mViewGroup;
    private TextView content1;
    private TextView content2;
    private TextView ok;
    private TextView cancel;

    private SysInitInfo sysInitInfo;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_personcenter);
        super.onCreate(savedInstanceState);
        sysInitInfo = hm_WcpcUserApplication.getInstance().getSysInitInfo();
        user = hm_WcpcUserApplication.getInstance().getUser();
        getNetWorker().clientGet(user.getToken(), user.getId());
    }

    private void initUserData(){
        try {
            URL url = new URL(user.getAvatar());
            image_avatar.setCornerRadius(90);
            imageWorker.loadImage(new XtomImageTask(image_avatar, url, mContext));
        } catch (MalformedURLException e) {
            image_avatar.setImageResource(R.mipmap.default_user);
        }
        text_realname.setText(user.getRealname());
        if("男".equals(user.getSex()))
            image_sex.setImageResource(R.mipmap.img_sex_boy);
        else
            image_sex.setImageResource(R.mipmap.img_sex_girl);
        text_servicecount.setText("乘车次数 "+(isNull(user.getTakecount())?"0" :user.getTakecount()));
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_GET:
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
                initUserData();
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
        frameLayout = (FrameLayout) findViewById(R.id.framelayout);
        f_left = (ImageView) findViewById(R.id.title_btn_left1);
        f_right = (ImageView) findViewById(R.id.title_btn_right1);

        scrollView = (ScrollView) findViewById(R.id.scrolllayout);
        s_left = (ImageView) findViewById(R.id.title_btn_left);
        s_right = (ImageView) findViewById(R.id.title_btn_right2);
        image_avatar = (RoundedImageView) findViewById(R.id.imageview);
        text_realname = (TextView) findViewById(R.id.textview_0);
        image_sex = (ImageView) findViewById(R.id.imageview_0);
        text_servicecount = (TextView) findViewById(R.id.textview_1);

        layout_mypurse = (RelativeLayout) findViewById(R.id.layout);
        text_youhuiquan = (TextView) findViewById(R.id.textview_2);
        text_feeaccount = (TextView) findViewById(R.id.textview_3);
        text_charge = (TextView) findViewById(R.id.textview_4);
        text_tixian = (TextView) findViewById(R.id.textview_5);

        layout_allorder = (RelativeLayout) findViewById(R.id.layout_0);
        text_topay = (TextView) findViewById(R.id.textview_6);
        text_togo = (TextView) findViewById(R.id.textview_7);
        text_cancel = (TextView) findViewById(R.id.textview_8);
        text_pingjia = (TextView) findViewById(R.id.textview_9);
        text_complete = (TextView) findViewById(R.id.textview_10);

        text_mytrips = (TextView) findViewById(R.id.textview_11);
        text_changepwd = (TextView) findViewById(R.id.textview_12);
        text_set = (TextView) findViewById(R.id.textview_13);
     }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY();
                if(scrollY > 40)
                    frameLayout.setVisibility(View.VISIBLE);
                else
                    frameLayout.setVisibility(View.GONE);
            }
        });

        setListener(s_left);
        setListener(s_right);
        setListener(f_left);
        setListener(f_right);

        setListener(image_avatar);
        setListener(layout_mypurse);
        setListener(text_youhuiquan);
        setListener(text_feeaccount);
        setListener(text_charge);
        setListener(text_tixian);

        setListener(layout_allorder);
        setListener(text_togo);
        setListener(text_topay);
        setListener(text_cancel);
        setListener(text_pingjia);
        setListener(text_complete);

        setListener(text_mytrips);
        setListener(text_changepwd);
        setListener(text_set);

    }

    private void setListener(View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it ;
                switch (v.getId()){
                    case R.id.title_btn_left:
                    case R.id.title_btn_left1:
                        finish();
                        break;
                    case R.id.title_btn_right2:
                    case R.id.title_btn_right1:
                        toMakePhone();
                        break;
                    case R.id.imageview:
                        it = new Intent(mContext, PersonInforActivity.class);
                        startActivityForResult(it, R.id.layout);
                        break;
                    case R.id.layout:
                        it = new Intent(mContext, MyPurseActivity.class);
                        startActivity(it);
                        break;
                    case R.id.textview_2:
                        it = new Intent(mContext, MyCouponListActivity.class);
                        it.putExtra("keytype", "1");
                        startActivity(it);
                        break;
                    case R.id.textview_3:
                        it = new Intent(mContext, MyFeeAccountActivity.class);
                        startActivity(it);
                        break;
                    case R.id.textview_4:
                        it = new Intent(mContext, ChargeMoneyActivity.class);
                        startActivity(it);
                        break;
                    case R.id.textview_5:
                        showSelectPopWindow();
                        break;
                    case R.id.layout_0:
                        it = new Intent(mContext, OrderListActivity.class);
                        it.putExtra("position", 0);
                        startActivity(it);
                        break;
                    case R.id.textview_6:
                        it = new Intent(mContext, OrderListActivity.class);
                        it.putExtra("position", 1);
                        startActivity(it);
                        break;
                    case R.id.textview_7:
                        it = new Intent(mContext, OrderListActivity.class);
                        it.putExtra("position", 2);
                        startActivity(it);
                        break;
                    case R.id.textview_8:
                        it = new Intent(mContext, OrderListActivity.class);
                        it.putExtra("position", 3);
                        startActivity(it);
                        break;
                    case R.id.textview_9:
                        it = new Intent(mContext, OrderListActivity.class);
                        it.putExtra("position", 4);
                        startActivity(it);
                        break;
                    case R.id.textview_10:
                        it = new Intent(mContext, OrderListActivity.class);
                        it.putExtra("position", 5);
                        startActivity(it);
                        break;
                    case R.id.textview_11: //我发布的行程
                        it = new Intent(mContext, MyTripsListActivity.class);
                        startActivity(it);
                        break;
                    case R.id.textview_12:
                        it = new Intent(mContext, ChangePwdActivity.class);
                        it.putExtra("keytype", "1");
                        startActivity(it);
                        break;
                    case R.id.textview_13:
                        it = new Intent(mContext, SetActivity.class);
                        startActivity(it);
                        break;
                }
            }
        });
    }

    private PopupWindow mWindow1;
    private ViewGroup mViewGroup1;
    private TextView boy;
    private TextView girl;
    private TextView cancel1;

    private void showSelectPopWindow(){
        if (mWindow1 != null) {
            mWindow1.dismiss();
        }
        mWindow1 = new PopupWindow(mContext);
        mWindow1.setWidth(FrameLayout.LayoutParams.MATCH_PARENT);
        mWindow1.setHeight(FrameLayout.LayoutParams.MATCH_PARENT);
        mWindow1.setBackgroundDrawable(new BitmapDrawable());
        mWindow1.setFocusable(true);
        mWindow1.setAnimationStyle(R.style.PopupAnimation);
        mViewGroup1 = (ViewGroup) LayoutInflater.from(mContext).inflate(
                R.layout.pop_sex, null);
        boy = (TextView) mViewGroup1.findViewById(R.id.textview);
        girl = (TextView) mViewGroup1.findViewById(R.id.textview_0);
        cancel1 = (TextView) mViewGroup1.findViewById(R.id.textview_2);
        mWindow1.setContentView(mViewGroup1);
        mWindow1.showAtLocation(mViewGroup1, Gravity.CENTER, 0, 0);
        boy.setText("支付宝");
        girl.setText("银行卡");
        cancel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow1.dismiss();
            }
        });

        boy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow1.dismiss();
                Intent it = new Intent(mContext, CashAddByAlipayActivity.class);
                startActivity(it);
            }
        });

        girl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow1.dismiss();
                Intent it = new Intent(mContext, CashAddByUnionpayActivity.class);
                startActivity(it);
            }
        });
    }

    private void toMakePhone() {
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
        content2.setText(sysInitInfo.getSys_service_phone());
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
                String phone = sysInitInfo.getSys_service_phone();
                //Intent.ACTION_CALL 直接拨打电话，就是进入拨打电话界面，电话已经被拨打出去了。
                //Intent.ACTION_DIAL 是进入拨打电话界面，电话号码已经输入了，但是需要人为的按拨打电话键，才能播出电话。
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                        + phone));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK)
            return;
        switch (requestCode){
            case R.id.layout:
                getNetWorker().clientGet(user.getToken(), user.getId());
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
