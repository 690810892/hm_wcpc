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
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.view.RoundedImageView;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.BaseUtil;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.ToLogin;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.TripListInfor;
import com.hemaapp.wcpc_user.module.User;

import java.net.MalformedURLException;
import java.net.URL;

import xtom.frame.image.load.XtomImageTask;

/**
 * Created by WangYuxia on 2016/5/12.
 */
public class TripDetailInforActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private TextView text_money;
    private RoundedImageView image_avatar;
    private TextView text_realname;
    private ImageView image_sex;
    private TextView text_carbrand;
    private TextView text_carnumber;
    private TextView text_remaincount;
    private ImageView image_phone;
    private TextView text_starttime;
    private TextView text_startposition;
    private TextView text_endposition;
    private ImageView image_map;
    private TextView text_join;

    private String id;
    private TripListInfor infor;

    private PopupWindow mWindow;
    private ViewGroup mViewGroup;
    private TextView content2;
    private TextView ok;
    private TextView cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_tripdetail);
        super.onCreate(savedInstanceState);
        getNetWorker().driverTripsGet(id);
    }

    private void initData() {
        text_starttime.setText(BaseUtil.transTimeChat(infor.getBegintime()));
        try {
            URL url = new URL(infor.getAvatar());
            image_avatar.setCornerRadius(90);
            ((BaseActivity) mContext).imageWorker.loadImage(new XtomImageTask(image_avatar, url, mContext));
        } catch (MalformedURLException e) {
            image_avatar.setImageResource(R.mipmap.default_driver);
        }
        text_realname.setText(infor.getRealname());
        if ("男".equals(infor.getSex()))
            image_sex.setImageResource(R.mipmap.img_sex_boy);
        else
            image_sex.setImageResource(R.mipmap.img_sex_girl);

        text_startposition.setText(infor.getStartaddress());
        text_endposition.setText(infor.getEndaddress());
        text_carbrand.setText(infor.getCarbrand());
        text_carnumber.setText(infor.getCarnumber());
        text_remaincount.setText("剩余" + (isNull(infor.getRemainnum()) ? "0" : infor.getRemainnum()) + "座");

        int maxnum = Integer.parseInt(infor.getMaxnumbers());
        int remain = Integer.parseInt(infor.getRemainnum());

        if (remain < maxnum)
            text_money.setText(infor.getSuccessfee());
        else
            text_money.setText(infor.getFailfee());

        if("0".equals(infor.getRemainnum())){
            text_join.setVisibility(View.INVISIBLE);
        }else
            text_join.setVisibility(View.VISIBLE);
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
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DRIVER_TRIPS_GET:
                showProgressDialog("请稍后...");
                break;
            default:
                break;
        }
    }
    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DRIVER_TRIPS_GET:
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
            case DRIVER_TRIPS_GET:
                HemaArrayResult<TripListInfor> cResult = (HemaArrayResult<TripListInfor>) baseResult;
                infor = cResult.getObjects().get(0);
                initData();
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DRIVER_TRIPS_GET:
               showTextDialog(baseResult.getMsg());
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DRIVER_TRIPS_GET:
                showTextDialog("抱歉，获取数据失败");
                break;
            default:
                break;
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        title = (TextView) findViewById(R.id.title_text);
        right = (TextView) findViewById(R.id.title_btn_right);

        text_money = (TextView) findViewById(R.id.textview_0);
        image_avatar = (RoundedImageView) findViewById(R.id.imageview);
        text_realname = (TextView) findViewById(R.id.textview_1);
        image_sex = (ImageView) findViewById(R.id.imageview_0);
        text_carbrand = (TextView) findViewById(R.id.textview_2);
        text_carnumber = (TextView) findViewById(R.id.textview_3);
        image_phone = (ImageView) findViewById(R.id.imageview_1);
        text_remaincount = (TextView) findViewById(R.id.textview_4);
        text_starttime = (TextView) findViewById(R.id.textview_5);
        text_startposition = (TextView) findViewById(R.id.textview_6);
        text_endposition = (TextView) findViewById(R.id.textview_7);
        image_map = (ImageView)findViewById(R.id.imageview_2);
        text_join = (TextView) findViewById(R.id.button);
    }

    @Override
    protected void getExras() {
        id = mIntent.getStringExtra("id");
    }

    @Override
    protected void setListener() {
        title.setText("车主行程详情");
        right.setVisibility(View.INVISIBLE);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        image_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(mContext, CarOwerHomePageActivity.class);
                it.putExtra("id", infor.getClient_id());
                mContext.startActivity(it);
            }
        });

        image_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(mContext, DriverMapDetailInforActivity.class);
                it.putExtra("start_lng", infor.getLng_start());
                it.putExtra("start_lat", infor.getLat_start());
                it.putExtra("end_lng", infor.getLng_end());
                it.putExtra("end_lat", infor.getLat_end());
                it.putExtra("lng", infor.getLng());
                it.putExtra("lat", infor.getLat());
                it.putExtra("avatar", infor.getAvatar());
                it.putExtra("address", infor.getAddress());
                startActivity(it);
            }
        });

        text_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = hm_WcpcUserApplication.getInstance().getUser();
                if(user == null){
                    ToLogin.showLogin(mContext);
                    return;
                }else{
                    Intent it = new Intent(mContext, JoinTripsActivity.class);
                    it.putExtra("id", id);
                    it.putExtra("success", infor.getSuccessfee());
                    it.putExtra("maxnumbers", infor.getMaxnumbers());
                    it.putExtra("remaincount", infor.getRemainnum());
                    it.putExtra("numbers", infor.getNumbers());
                    it.putExtra("franchisee_id", infor.getFranchisee_id());
                    it.putExtra("keytype", infor.getKeytype());
                    it.putExtra("distance", infor.getDistance());
                    startActivityForResult(it, R.id.layout);
                }
            }
        });

        image_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhoneWindow();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK)
            return;
        switch (requestCode){
            case R.id.layout:
                getNetWorker().driverTripsGet(id);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
