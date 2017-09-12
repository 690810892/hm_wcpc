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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.hm_FrameWork.view.RoundedImageView;
import com.hemaapp.wcpc_driver.BaseActivity;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.BaseUtil;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.DataInfor;
import com.hemaapp.wcpc_driver.module.OrderDetailInfor;
import com.hemaapp.wcpc_driver.module.ReplyItems;
import com.hemaapp.wcpc_driver.module.User;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import xtom.frame.image.load.XtomImageTask;

/**
 * Created by WangYuxia on 2016/5/27.
 */
public class CenterOrderDetailInforActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private TextView text_orderno;
    private RoundedImageView image_avatar;
    private ImageView image_grab;
    private TextView text_realname;
    private ImageView image_sex;
    private TextView text_takecount;
    private TextView text_number;
    private ImageView image_phone;
    private TextView text_money;

    private LinearLayout layout_success;
    private ImageView image_star_0, image_star_1, image_star_2, image_star_3, image_star_4;
    private LinearLayout linearLayout;
    private TextView text_content;

    private LinearLayout layout_failed;
    private TextView text_selectreason;
    private TextView text_inputreason;

    private OrderDetailInfor infor;
    private User user;
    private String order_id;
    private ArrayList<DataInfor> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_historyorderdetail);
        super.onCreate(savedInstanceState);
        user = hm_WcpcDriverApplication.getInstance().getUser();
        getOrderDetail();
    }

    private void getOrderDetail(){
        getNetWorker().driverOrderGet(user.getToken(), order_id);
    }

    private void initOrderData(){
        text_orderno.setText("订单号："+infor.getOrder_no());
        try {
            URL url = new URL(infor.getAvatar());
            image_avatar.setCornerRadius(90);
            imageWorker.loadImage(new XtomImageTask(image_avatar, url, mContext));
        } catch (MalformedURLException e) {
            image_avatar.setImageResource(R.mipmap.default_user);
        }
        text_realname.setText(infor.getRealname());
        if("男".equals(infor.getSex()))
            image_sex.setImageResource(R.mipmap.img_sex_boy);
        else
            image_sex.setImageResource(R.mipmap.img_sex_girl);
        if("0".equals(infor.getGrabflag()))
            image_grab.setVisibility(View.INVISIBLE);
        else
            image_grab.setVisibility(View.VISIBLE);
        text_takecount.setText("乘车次数 "+(isNull(infor.getTakecount())? "0":infor.getTakecount()));
        text_number.setText("乘车人数 "+(isNull(infor.getNumbers())? "0":infor.getNumbers()));

        if("0".equals(infor.getIs_pool()))
            text_money.setText(infor.getFailfee());
        else
            text_money.setText(infor.getSuccessfee());

        if("1".equals(infor.getPayflag())){ //待评价
            layout_failed.setVisibility(View.GONE);
            layout_success.setVisibility(View.GONE);
        }else if("2".equals(infor.getPayflag())){ //已评价
            layout_success.setVisibility(View.VISIBLE);
            layout_failed.setVisibility(View.GONE);
            ReplyItems item = infor.getReplyItems().get(0);
            BaseUtil.transScoreByPoint(image_star_0, image_star_1, image_star_2, image_star_3, image_star_4, item.getPoint());
            text_content.setText(isNull(item.getContent())?"用户没有填写评价":item.getContent());
            initReplyContent(item.getReply_str());
        }else if("3".equals(infor.getPayflag())){ //已取消
            layout_success.setVisibility(View.GONE);
            layout_failed.setVisibility(View.VISIBLE);
            for(int i = 0; i< datas.size(); i++){
                DataInfor d = datas.get(i);
                if(d.getId().equals(infor.getReason_str()))
                    text_selectreason.setText(d.getName());
            }
            text_inputreason.setText(isNull(infor.getContent())?"取消原因: 用户暂时没有填写":"取消原因: "+infor.getContent());
        }
    }

    private void initReplyContent(String ids){
        ArrayList<String> values = new ArrayList<>();
        if(ids.contains(",")){
            for(int i = 0; i< ids.split(",").length; i++)
                values.add(i, ids.split(",")[i]);
        }else
            values.add(ids);
        if(datas == null || datas.size() == 0)
            linearLayout.setVisibility(View.GONE);
        else{
            linearLayout.setVisibility(View.VISIBLE);
            int m = datas.size() / 2;
            int n = datas.size() % 2;
            int count ;
            if(n == 0)
                count = m;
            else
                count = m + 1;

            for(int i = 0; i< count; i++){
                View view = LayoutInflater.from(mContext).inflate(R.layout.listitem_pingjia, null);
                TextView textView = (TextView) view.findViewById(R.id.textview_0);
                TextView textView1 = (TextView) view.findViewById(R.id.textview_1);

                textView.setText(datas.get(i*2).getName());

                if(values.contains(datas.get(i*2).getId()))
                    textView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.img_agree_s, 0,
                            0, 0);
                else
                    textView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.img_agree_n, 0,
                            0, 0);

                if(i*2+1<datas.size()){
                    textView1.setVisibility(View.VISIBLE);
                    textView1.setText(datas.get(i*2+1).getName());
                    if(values.contains(datas.get(i*2+1).getId()))
                        textView1.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.img_agree_s, 0,
                                0, 0);
                    else
                        textView1.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.img_agree_n, 0,
                                0, 0);
                    linearLayout.addView(view);
                }else{
                    textView1.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DRIVER_ORDER_GET:
            case DATA_LIST:
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
            case DATA_LIST:
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
                if("2".equals(infor.getPayflag())){
                    getNetWorker().dataList("2");
                }else if("3".equals(infor.getPayflag())){
                    getNetWorker().dataList("1");
                }
                    initOrderData();
                break;
            case DATA_LIST:
                HemaPageArrayResult<DataInfor> dResult = (HemaPageArrayResult<DataInfor>) baseResult;
                datas = dResult.getObjects();
                initOrderData();
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
            case DRIVER_ORDER_GET:
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
        image_grab = (ImageView) findViewById(R.id.imageview_2);
        text_realname = (TextView) findViewById(R.id.textview_1);
        image_sex = (ImageView) findViewById(R.id.imageview_0);
        text_takecount = (TextView) findViewById(R.id.textview_2);
        text_number = (TextView) findViewById(R.id.textview_3);
        text_money = (TextView) findViewById(R.id.textview_5);
        image_phone = (ImageView) findViewById(R.id.imageview_1);

        layout_success = (LinearLayout) findViewById(R.id.success);
        image_star_0 = (ImageView) findViewById(R.id.imageview_3);
        image_star_1 = (ImageView) findViewById(R.id.imageview_4);
        image_star_2 = (ImageView) findViewById(R.id.imageview_5);
        image_star_3 = (ImageView) findViewById(R.id.imageview_6);
        image_star_4 = (ImageView) findViewById(R.id.imageview_7);
        linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
        text_content = (TextView) findViewById(R.id.textview_6);

        layout_failed = (LinearLayout) findViewById(R.id.failed);
        text_selectreason = (TextView) findViewById(R.id.textview_7);
        text_inputreason = (TextView) findViewById(R.id.textview_8);
    }

    @Override
    protected void getExras() {
        order_id = mIntent.getStringExtra("id");
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
