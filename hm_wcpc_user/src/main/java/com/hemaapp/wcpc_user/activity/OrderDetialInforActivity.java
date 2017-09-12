package com.hemaapp.wcpc_user.activity;

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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.hm_FrameWork.view.RoundedImageView;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.BaseUtil;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.DataInfor;
import com.hemaapp.wcpc_user.module.OrderDetailInfor;
import com.hemaapp.wcpc_user.module.ReplyItems;
import com.hemaapp.wcpc_user.module.User;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import xtom.frame.image.load.XtomImageTask;

/**
 * Created by WangYuxia on 2016/5/16.
 */
public class OrderDetialInforActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private TextView text_orderno;
    private RoundedImageView image_avatar;
    private TextView text_realname;
    private ImageView image_sex;
    private TextView text_carbrand;
    private TextView text_carnumbers;
    private ImageView image_total_star_0, image_total_star_1, image_total_star_2, image_total_star_3, image_total_star_4;
    private ImageView image_phone;
    private TextView text_feekind;
    private TextView text_money;

    private LinearLayout layout_reply;
    private ImageView image_star_0, image_star_1, image_star_2, image_star_3, image_star_4;
    private LinearLayout layout;
    private TextView text_replycontent;
    private LinearLayout layout_bottom;
    private TextView text_operate_0; //实心按钮
    private TextView text_operate_1; //空心按钮

    private String id;
    private User user;
    private OrderDetailInfor infor;
    private ArrayList<DataInfor> datas = new ArrayList<>();

    private PopupWindow mWindow;
    private ViewGroup mViewGroup;
    private TextView content2;
    private TextView ok;
    private TextView cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_orderdetail);
        super.onCreate(savedInstanceState);
        user = hm_WcpcUserApplication.getInstance().getUser();
        getOrderDetail();
    }

    private void getOrderDetail() {
        getNetWorker().clientOrderGet(user.getToken(), id);
    }

    private void initOrderData() {
        text_orderno.setText("订单号：" + infor.getOrder_no());
        try {
            URL url = new URL(infor.getAvatar());
            image_avatar.setCornerRadius(90);
            imageWorker.loadImage(new XtomImageTask(image_avatar, url, mContext));
        } catch (MalformedURLException e) {
            image_avatar.setImageResource(R.mipmap.default_driver);
        }
        text_realname.setText(infor.getRealname());
        if ("男".equals(infor.getSex()))
            image_sex.setImageResource(R.mipmap.img_sex_boy);
        else
            image_sex.setImageResource(R.mipmap.img_sex_girl);
        text_carbrand.setText(infor.getCarbrand());
        text_carnumbers.setText(infor.getCarnumbers());
        BaseUtil.transScore(image_total_star_0, image_total_star_1, image_total_star_2,
                image_total_star_3, image_total_star_4, infor.getReplycount(), infor.getTotalpoint());
        if ("0".equals(infor.getPayflag())) {
            text_feekind.setText("需支付车费");
        } else {
            text_feekind.setText("车费");
        }
        if ("0".equals(infor.getIs_pool()))
            text_money.setText(infor.getFailfee());
        else
            text_money.setText(infor.getSuccessfee());
        if ("0".equals(infor.getPayflag())) { //未支付
            layout_reply.setVisibility(View.GONE);
            layout_bottom.setVisibility(View.VISIBLE);
            if ("0".equals(infor.getReachflag())) {

                if("0".equals(infor.getStatusflag())){
                    text_operate_1.setVisibility(View.VISIBLE);
                    text_operate_0.setVisibility(View.VISIBLE);
                }else{
                    text_operate_1.setVisibility(View.GONE);
                    text_operate_0.setVisibility(View.GONE);
                }

                text_operate_0.setText("确认上车");
                text_operate_0.setBackgroundResource(R.drawable.bt_login);
                text_operate_0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getNetWorker().orderOperate(user.getToken(), "8", id, "", "");
                    }
                });

                text_operate_1.setText("取消订单");
                text_operate_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(mContext, CancelOrderActivity.class);
                        it.putExtra("id", id);
                        startActivityForResult(it, R.id.layout_1);
                    }
                });
            } else if ("1".equals(infor.getReachflag())) {
                text_operate_0.setVisibility(View.VISIBLE);
                text_operate_0.setBackgroundResource(R.drawable.bt_login);
                text_operate_1.setVisibility(View.GONE);
                text_operate_0.setText("去支付");
                text_operate_0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(mContext, ToPayActivity.class);
                        it.putExtra("id", id);
                        if ("0".equals(infor.getIs_pool()))
                            it.putExtra("total_fee", infor.getFailfee());
                        else
                            it.putExtra("total_fee", infor.getSuccessfee());
                        startActivityForResult(it, R.id.layout_0);
                    }
                });
            }
        } else if ("1".equals(infor.getPayflag())) { //已支付
            layout_reply.setVisibility(View.GONE);
            layout_bottom.setVisibility(View.VISIBLE);
            text_operate_0.setVisibility(View.GONE);
            text_operate_1.setVisibility(View.VISIBLE);
            text_operate_1.setText("去评价");
            text_operate_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, PingJiaActivity.class);
                    it.putExtra("id", id);
                    startActivityForResult(it, R.id.layout);
                }
            });
        } else if ("2".equals(infor.getPayflag())) { //已评价
            layout_reply.setVisibility(View.VISIBLE);
            layout_bottom.setVisibility(View.GONE);
            ReplyItems item = infor.getReplyItems().get(0);
            BaseUtil.transScoreByPoint1(image_star_0, image_star_1, image_star_2, image_star_3, image_star_4, item.getPoint());
            text_replycontent.setText(isNull(item.getContent()) ? "用户没有填写评价" : item.getContent());
            initReplyContent(item.getReply_str());
        } else if ("3".equals(infor.getPayflag())) { //已取消
            layout_reply.setVisibility(View.GONE);
            layout_bottom.setVisibility(View.VISIBLE);
            text_operate_0.setVisibility(View.VISIBLE);
            text_operate_1.setVisibility(View.GONE);
            text_operate_0.setText("订单已取消");
            text_operate_0.setBackgroundResource(R.drawable.bt_cancel);
        }
    }

    private void initReplyContent(String ids) {
        ArrayList<String> values = new ArrayList<>();
        if (ids.contains(",")) {
            for (int i = 0; i < ids.split(",").length; i++)
                values.add(i, ids.split(",")[i]);
        } else
            values.add(ids);
        if (datas == null || datas.size() == 0)
            layout.setVisibility(View.GONE);
        else {
            layout.setVisibility(View.VISIBLE);
            int m = datas.size() / 2;
            int n = datas.size() % 2;
            int count;
            if (n == 0)
                count = m;
            else
                count = m + 1;
            layout.removeAllViews();
            for (int i = 0; i < count; i++) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.listitem_pingjia, null);
                TextView textView = (TextView) view.findViewById(R.id.textview_0);
                TextView textView1 = (TextView) view.findViewById(R.id.textview_1);

                textView.setText(datas.get(i * 2).getName());
                if (values.contains(datas.get(i * 2).getId()))
                    textView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.img_agree_s, 0,
                            0, 0);
                else
                    textView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.img_agree_n, 0,
                            0, 0);

                if (i * 2 + 1 < datas.size()) {
                    textView1.setText(datas.get(i * 2 + 1).getName());
                    if (values.contains(datas.get(i * 2 + 1).getId()))
                        textView1.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.img_agree_s, 0,
                                0, 0);
                    else
                        textView1.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.img_agree_n, 0,
                                0, 0);
                }
                layout.addView(view);
            }
        }
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_ORDER_GET:
            case DATA_LIST:
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
            case CLIENT_ORDER_GET:
            case DATA_LIST:
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
            case CLIENT_ORDER_GET:
                HemaArrayResult<OrderDetailInfor> uResult = (HemaArrayResult<OrderDetailInfor>) baseResult;
                infor = uResult.getObjects().get(0);
                if ("2".equals(infor.getPayflag())) {
                    getNetWorker().dataList("2");
                } else if ("3".equals(infor.getPayflag()))
                    getNetWorker().dataList("1");
                else
                    initOrderData();
                break;
            case DATA_LIST:
                HemaPageArrayResult<DataInfor> dResult = (HemaPageArrayResult<DataInfor>) baseResult;
                datas = dResult.getObjects();
                initOrderData();
                break;
            case ORDER_OPERATE:
                showTextDialog(baseResult.getMsg());
                getOrderDetail();
                break;
        }
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_ORDER_GET:
            case DATA_LIST:
                showTextDialog(baseResult.getMsg());
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_ORDER_GET:
            case DATA_LIST:
                showTextDialog("抱歉，获取数据失败");
                break;
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        right = (TextView) findViewById(R.id.title_btn_right);
        title = (TextView) findViewById(R.id.title_text);

        text_orderno = (TextView) findViewById(R.id.textview_0);
        image_avatar = (RoundedImageView) findViewById(R.id.imageview);
        text_realname = (TextView) findViewById(R.id.textview_1);
        image_sex = (ImageView) findViewById(R.id.imageview_0);
        text_carbrand = (TextView) findViewById(R.id.textview_2);
        text_carnumbers = (TextView) findViewById(R.id.textview_3);
        image_phone = (ImageView) findViewById(R.id.imageview_1);
        image_total_star_0 = (ImageView) findViewById(R.id.imageview_2);
        image_total_star_1 = (ImageView) findViewById(R.id.imageview_3);
        image_total_star_2 = (ImageView) findViewById(R.id.imageview_4);
        image_total_star_3 = (ImageView) findViewById(R.id.imageview_5);
        image_total_star_4 = (ImageView) findViewById(R.id.imageview_6);
        text_feekind = (TextView) findViewById(R.id.textview_4);
        text_money = (TextView) findViewById(R.id.textview_5);

        layout_reply = (LinearLayout) findViewById(R.id.layout);
        image_star_0 = (ImageView) findViewById(R.id.imageview_7);
        image_star_1 = (ImageView) findViewById(R.id.imageview_8);
        image_star_2 = (ImageView) findViewById(R.id.imageview_9);
        image_star_3 = (ImageView) findViewById(R.id.imageview_10);
        image_star_4 = (ImageView) findViewById(R.id.imageview_11);

        layout = (LinearLayout) findViewById(R.id.linearlayout);
        text_replycontent = (TextView) findViewById(R.id.textview_6);
        layout_bottom = (LinearLayout) findViewById(R.id.layout_0);
        text_operate_0 = (TextView) findViewById(R.id.button_0);
        text_operate_1 = (TextView) findViewById(R.id.button);
    }

    @Override
    protected void getExras() {
        id = mIntent.getStringExtra("id");
    }

    @Override
    protected void setListener() {
        title.setText("订单详情");
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
    }

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
        content2 = (TextView) mViewGroup.findViewById(R.id.textview_0);
        cancel = (TextView) mViewGroup.findViewById(R.id.textview_1);
        ok = (TextView) mViewGroup.findViewById(R.id.textview_2);
        mWindow.setContentView(mViewGroup);
        mWindow.showAtLocation(mViewGroup, Gravity.CENTER, 0, 0);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case R.id.layout:
            case R.id.layout_0:
            case R.id.layout_1:
                getOrderDetail();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
