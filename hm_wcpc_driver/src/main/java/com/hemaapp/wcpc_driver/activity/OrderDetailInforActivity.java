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

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.view.RoundedImageView;
import com.hemaapp.wcpc_driver.BaseActivity;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.BaseUtil;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.module.OrderDetailInfor;
import com.hemaapp.wcpc_driver.module.User;

import java.net.MalformedURLException;
import java.net.URL;

import xtom.frame.image.load.XtomImageTask;

/**
 * Created by WangYuxia on 2016/5/24.
 * 订单详情
 */
public class OrderDetailInforActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private TextView text_money;
    private ImageView image_qiandan;
    private RoundedImageView image_avatar;
    private TextView text_realname;
    private ImageView image_sex;
    private TextView text_takecount;
    private TextView text_remaincount;
    private ImageView image_phone;
    private TextView text_starttime;
    private TextView text_startposition;
    private TextView text_endposition;
    private TextView text_agree;
    private TextView text_thankfee;
    private TextView text_beizhu;
    private ImageView image_map;
    private TextView text_join; //送达按钮

    private String order_id;

    private OrderDetailInfor infor;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_orderdetail);
        super.onCreate(savedInstanceState);
        user = getUser();
        getOrderDetail();
    }

    private void getOrderDetail(){
        getNetWorker().driverOrderGet(user.getToken(), order_id);
    }

    private void initOrderData(){
        if("1".equals(infor.getIs_pool()))
            text_money.setText(infor.getSuccessfee());
        else
            text_money.setText(infor.getFailfee());

        if("0".equals(infor.getGrabflag()))
            image_qiandan.setVisibility(View.INVISIBLE);
        else{
            image_qiandan.setVisibility(View.VISIBLE);
        }
        try {
            URL url = new URL(infor.getAvatar());
            image_avatar.setCornerRadius(90);
            imageWorker.loadImage(new XtomImageTask(image_avatar,url, mContext));
        } catch (MalformedURLException e) {
            image_avatar.setImageResource(R.mipmap.default_user);
        }

        text_realname.setText(infor.getRealname());
        if("男".equals(infor.getSex()))
            image_sex.setImageResource(R.mipmap.img_sex_boy);
        else
            image_sex.setImageResource(R.mipmap.img_sex_girl);

        text_takecount.setText("乘车次数  "+(isNull(infor.getTakecount())?"0":infor.getTakecount()));
        text_remaincount.setText("乘车人数 "+infor.getNumbers());

        text_starttime.setText(BaseUtil.transTimeChat(infor.getBegintime()));
        text_startposition.setText(infor.getStartaddress());
        text_endposition.setText(infor.getEndaddress());
        if("0".equals(infor.getCarpoolflag()))
            text_agree.setText("不愿意");
        else
            text_agree.setText("愿意");
        text_thankfee.setText(infor.getThankfee()+"元");
        text_beizhu.setText(isNull(infor.getRemarks())?"乘客未填写备注":infor.getRemarks());
        if("0".equals(infor.getStatusflag())){
            text_join.setText("接到乘客");
        }else{
            text_join.setText("送达");
        }
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DRIVER_ORDER_GET:
                showProgressDialog("请稍后...");
                break;
            case ORDER_OPERATE:
                showProgressDialog("请稍后...");
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DRIVER_ORDER_GET:
                cancelProgressDialog();
                break;
            case ORDER_OPERATE:
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
            case DRIVER_ORDER_GET:
                HemaArrayResult<OrderDetailInfor> uResult = (HemaArrayResult<OrderDetailInfor>) baseResult;
                infor = uResult.getObjects().get(0);
                initOrderData();
                break;
            case ORDER_OPERATE:
                String keytype = netTask.getParams().get("keytype");
                if("7".equals(keytype)){
                    text_join.setText("到达");
                    infor.setStatusflag("2");
                }else{
                    showTextDialog("操作成功");
                    title.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setResult(RESULT_OK, mIntent);
                            finish();
                        }
                    }, 1000);
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
            case DRIVER_ORDER_GET:
                showTextDialog(baseResult.getMsg());
                break;
            case ORDER_OPERATE:
                showTextDialog(baseResult.getMsg());
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DRIVER_ORDER_GET:
                showTextDialog("抱歉，获取数据失败");
                break;
            case ORDER_OPERATE:
                showTextDialog("操作失败");
                break;
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        title = (TextView) findViewById(R.id.title_text);
        right = (TextView) findViewById(R.id.title_btn_right);

        text_money = (TextView) findViewById(R.id.textview_0);
        image_qiandan = (ImageView) findViewById(R.id.imageview_3);
        image_avatar = (RoundedImageView) findViewById(R.id.imageview);
        text_realname = (TextView) findViewById(R.id.textview_1);
        image_sex = (ImageView) findViewById(R.id.imageview_0);
        text_takecount = (TextView) findViewById(R.id.textview_9);

        image_phone = (ImageView) findViewById(R.id.imageview_1);
        text_remaincount = (TextView) findViewById(R.id.textview_2);
        text_starttime = (TextView) findViewById(R.id.textview_3);
        text_startposition = (TextView) findViewById(R.id.textview_4);
        text_endposition = (TextView) findViewById(R.id.textview_5);
        text_agree = (TextView) findViewById(R.id.textview_6);
        text_thankfee = (TextView) findViewById(R.id.textview_7);
        text_beizhu = (TextView) findViewById(R.id.textview_8);
        image_map = (ImageView)findViewById(R.id.imageview_2);
        text_join = (TextView) findViewById(R.id.button);
    }

    @Override
    protected void getExras() {
        order_id = mIntent.getStringExtra("id");
    }

    @Override
    protected void setListener() {
        title.setText("乘客行程详情");
        right.setVisibility(View.INVISIBLE);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        image_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhoneWindow();
            }
        });

        image_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(mContext, TripMapActivity.class);
                it.putExtra("lng_start", infor.getLng_start());
                it.putExtra("lat_start", infor.getLat_start());
                it.putExtra("lng_end", infor.getLng_end());
                it.putExtra("lat_end", infor.getLat_end());
                it.putExtra("lng", infor.getLng());
                it.putExtra("lat",infor.getLat());
                it.putExtra("address", infor.getAddress());
                it.putExtra("avatar", infor.getAvatar());
                startActivity(it);
            }
        });

        text_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = ((TextView) v).getText().toString();
                if("接到乘客".equals(value)){
                    getNetWorker().orderOperate(user.getToken(), "7", order_id, "", "");
                }else if("送达".equals(value)){
                    getNetWorker().orderOperate(user.getToken(), "2", order_id, "", "");
                }
            }
        });
    }

    private PopupWindow mWindow;
    private ViewGroup mViewGroup;
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
        content1.setText("拨打乘客电话");
        content2.setText(infor.getMobile());
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
                String phone = infor.getMobile();
                //Intent.ACTION_CALL 直接拨打电话，就是进入拨打电话界面，电话已经被拨打出去了。
                //Intent.ACTION_DIAL 是进入拨打电话界面，电话号码已经输入了，但是需要人为的按拨打电话键，才能播出电话。
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                        + phone));
                startActivity(intent);
            }
        });
    }
}
