package com.hemaapp.wcpc_user.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.BaseUtil;
import com.hemaapp.wcpc_user.EventBusConfig;
import com.hemaapp.wcpc_user.EventBusModel;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.adapter.TagListAdapter;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.DataInfor;
import com.hemaapp.wcpc_user.module.SysInitInfo;
import com.hemaapp.wcpc_user.module.User;
import com.hemaapp.wcpc_user.view.FlowLayout.TagFlowLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import de.greenrobot.event.EventBus;
import xtom.frame.util.XtomFileUtil;

/**
 * Created by WangYuxia on 2016/5/20.
 * 评价订单
 */
public class PingJiaActivity extends BaseActivity implements PlatformActionListener {

    private ImageView left;
    private TextView title;
    private TextView right;

    private ImageView image_0, image_1, image_2, image_3, image_4;
    private TagFlowLayout group_textview;
    private EditText editText;
    private TextView text_submit;
    private TextView text_other; //跳过此页

    private ArrayList<ImageView> images = new ArrayList<>();
    private String order_Id, point = "5", driver_id;
    private ArrayList<DataInfor> infors = new ArrayList<>();
    private TagListAdapter adapter;
    private User user;

    private PopupWindow mWindow;
    private ViewGroup mViewGroup;
    private TextView ok;
    private TextView cancel;

    private SysInitInfo infor;
    private PopupWindow mWindow_exit;//分享
    private ViewGroup mViewGroup_exit;
    private String sys_plugins;
    private String pathWX;
    private String imageurl;
    private OnekeyShare oks;
    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_pingjia);
        super.onCreate(savedInstanceState);
        ShareSDK.initSDK(this);
        user = hm_WcpcUserApplication.getInstance().getUser();
        SysInitInfo initInfo = getApplicationContext()
                .getSysInitInfo();
        sys_plugins = initInfo.getSys_plugins();
        pathWX = sys_plugins + "share/sdk.php?invitecode=" + user.getInvitecode() + "&keyid=" + order_Id + "&type=1";
        getKinds();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (flag==1){
            title.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setResult(RESULT_OK, mIntent);
                    finish();
                }
            }, 1000);
        }
    }

    private void getKinds() {
        getNetWorker().dataList("2");
    }

    private void initUserData() {
        if (infors == null || infors.size() == 0)
            group_textview.setVisibility(View.GONE);
        else {
            group_textview.setVisibility(View.VISIBLE);
            group_textview.setVisibility(View.VISIBLE);
            adapter = new TagListAdapter(infors, mContext);
            group_textview.setAdapter(adapter);
        }
    }

    public void changeStatus() {
        for (int i = 0; i < infors.size(); i++) {
            DataInfor attrItem = infors.get(i);
            if (attrItem.getId().equals(adapter.attrItem.getId()))
                attrItem.setChecked(adapter.attrItem.isChecked());
        }
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_GET:
            case REPLY_ADD:
                showProgressDialog("请稍后...");
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DATA_LIST:
            case REPLY_ADD:
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
            case DATA_LIST:
                HemaPageArrayResult<DataInfor> uResult = (HemaPageArrayResult<DataInfor>) baseResult;
                infors = uResult.getObjects();
                initUserData();
                break;
            case REPLY_ADD:
                flag = 1;
//                showTextDialog("评价成功");
                EventBus.getDefault().post(new EventBusModel(EventBusConfig.REFRESH_BLOG_LIST));
                toShare();
                break;
        }
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DATA_LIST:
            case REPLY_ADD:
                showTextDialog(baseResult.getMsg());
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DATA_LIST:
                showTextDialog("抱歉，获取数据失败");
                break;
            case REPLY_ADD:
                showTextDialog("抱歉，提交失败，请稍后重试");
                break;
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        right = (TextView) findViewById(R.id.title_btn_right);
        title = (TextView) findViewById(R.id.title_text);

        image_0 = (ImageView) findViewById(R.id.imageview_0);
        image_1 = (ImageView) findViewById(R.id.imageview_1);
        image_2 = (ImageView) findViewById(R.id.imageview_2);
        image_3 = (ImageView) findViewById(R.id.imageview_3);
        image_4 = (ImageView) findViewById(R.id.imageview_4);
        images.add(0, image_0);
        images.add(1, image_1);
        images.add(2, image_2);
        images.add(3, image_3);
        images.add(4, image_4);

        group_textview = (TagFlowLayout) findViewById(R.id.multitextview);
        editText = (EditText) findViewById(R.id.edittext);
        text_submit = (TextView) findViewById(R.id.button);
        text_other = (TextView) findViewById(R.id.button_0);
    }

    @Override
    protected void getExras() {
        order_Id = mIntent.getStringExtra("id");
        driver_id = mIntent.getStringExtra("driver_id");
    }

    @Override
    protected void setListener() {
        title.setText("评价");
        right.setText("投诉");
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseUtil.hideInput(mContext,text_submit);
                Intent it = new Intent(mContext, TouSuActivity.class);
                it.putExtra("driver_id", driver_id);
                it.putExtra("order_Id", order_Id);
                startActivity(it);
            }
        });
        image_0.setOnClickListener(getOnClickListener(0));
        image_1.setOnClickListener(getOnClickListener(1));
        image_2.setOnClickListener(getOnClickListener(2));
        image_3.setOnClickListener(getOnClickListener(3));
        image_4.setOnClickListener(getOnClickListener(4));

        text_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseUtil.hideInput(mContext,text_submit);
                if ("0".equals(point)) {
                    showTextDialog("请对本次服务进行评分");
                    return;
                }
                int count = 0;
                String reply_str = "", reply_str_text = "";
                for (int i = 0; i < infors.size(); i++) {
                    if (infors.get(i).isChecked()) {
                        count++;
                        if (count == 1) {
                            reply_str = reply_str + infors.get(i).getId();
                            reply_str_text = reply_str_text + infors.get(i).getName();
                        } else {
                            reply_str = reply_str +
                                    "," + infors.get(i).getId();
                            reply_str_text = reply_str_text +
                                    "," + infors.get(i).getName();
                        }
                    }
                }
                String content = editText.getText().toString();
                getNetWorker().replyAdd(user.getToken(), "1", order_Id, reply_str, point, content, reply_str_text);
            }
        });

        text_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNetWorker().replyAdd(user.getTakecount(), "1", order_Id, "", point, "", "");
            }
        });
    }

    private View.OnClickListener getOnClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                point = String.valueOf(position + 1);
                setImages(position);
            }
        };
    }

    private void setImages(int position) {
        for (int i = 0; i < 5; i++) {
            if (i <= position) {
                images.get(i).setImageResource(R.mipmap.img_pingjia_s);
            } else {
                images.get(i).setImageResource(R.mipmap.img_pingjia_n);
            }
        }
    }

    private void toShare() {
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
                R.layout.pop_toshare, null);
        cancel = (TextView) mViewGroup.findViewById(R.id.textview_1);
        ok = (TextView) mViewGroup.findViewById(R.id.textview_2);
        mWindow.setContentView(mViewGroup);
        mWindow.showAtLocation(mViewGroup, Gravity.CENTER, 0, 0);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
                title.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setResult(RESULT_OK, mIntent);
                        finish();
                    }
                }, 1000);
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
                share();
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void share() {
        if (mWindow_exit != null) {
            mWindow_exit.dismiss();
        }
        mWindow_exit = new PopupWindow(mContext);
        mWindow_exit.setWidth(FrameLayout.LayoutParams.MATCH_PARENT);
        mWindow_exit.setHeight(FrameLayout.LayoutParams.MATCH_PARENT);
        mWindow_exit.setBackgroundDrawable(new BitmapDrawable());
        mWindow_exit.setFocusable(true);
        mWindow_exit.setAnimationStyle(R.style.PopupAnimation);
        mViewGroup_exit = (ViewGroup) LayoutInflater.from(mContext).inflate(
                R.layout.pop_share, null);
        TextView wechat = (TextView) mViewGroup_exit.findViewById(R.id.wechat);
        TextView moment = (TextView) mViewGroup_exit.findViewById(R.id.moment);
        TextView qqshare = (TextView) mViewGroup_exit.findViewById(R.id.qq);
        TextView qzone = (TextView) mViewGroup_exit.findViewById(R.id.zone);
        cancel = (TextView) mViewGroup_exit.findViewById(R.id.tv_cancel);
        View all = mViewGroup_exit.findViewById(R.id.allitem);
        mWindow_exit.setContentView(mViewGroup_exit);
        mWindow_exit.showAtLocation(mViewGroup_exit, Gravity.CENTER, 0, 0);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mWindow_exit.dismiss();
            }
        });
        all.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mWindow_exit.dismiss();
            }
        });
        qqshare.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showShare(QQ.NAME);
                mWindow_exit.dismiss();
            }
        });
        wechat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showShare(Wechat.NAME);
                mWindow_exit.dismiss();
            }
        });
        moment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showShare(WechatMoments.NAME);
                mWindow_exit.dismiss();
            }
        });
        qzone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showShare(QZone.NAME);
                mWindow_exit.dismiss();
            }
        });
    }

    private void showShare(String platform) {
        if (isNull(imageurl))
            imageurl = initImagePath();
        if (oks == null) {
            oks = new OnekeyShare();
            oks.setTitle("念念不忘想起你，只想送你份好礼，注册即得50元代金券！");
            oks.setTitleUrl(pathWX); // 标题的超链接
            oks.setText("莱芜 ⇌ 济南25元；\n" +
                    "莱芜 ⇌ 泰安15元。");
            oks.setFilePath(imageurl);
            imageurl = initImagePath();
            oks.setImagePath(imageurl);
            oks.setUrl(pathWX);
            oks.setSiteUrl(pathWX);
            oks.setCallback(this);
        }
        oks.setPlatform(platform);
        oks.show(mContext);
    }

    private String initImagePath() {
        String imagePath;
        try {

            String cachePath_internal = XtomFileUtil.getCacheDir(mContext)
                    + "images/";// 获取缓存路径
            File dirFile = new File(cachePath_internal);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            imagePath = cachePath_internal + "share.png";
            File file = new File(imagePath);
            if (!file.exists()) {
                file.createNewFile();
                Bitmap pic;

                pic = BitmapFactory.decodeResource(mContext.getResources(),
                        R.mipmap.ic_launcher);

                FileOutputStream fos = new FileOutputStream(file);
                pic.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            imagePath = null;
        }
        log_i("imagePath:" + imagePath);
        return imagePath;
    }

    @Override
    public void onComplete(Platform arg0, int arg1, HashMap<String, Object> hashMap) {
        if (arg0.getName().equals(Wechat.NAME)) {// 判断成功的平台是不是微信
            handler.sendEmptyMessage(1);
        }
        if (arg0.getName().equals(WechatMoments.NAME)) {// 判断成功的平台是不是微信朋友圈
            handler.sendEmptyMessage(2);
        }
        if (arg0.getName().equals(QQ.NAME)) {// 判断成功的平台是不是QQ
            handler.sendEmptyMessage(3);
        }
        if (arg0.getName().equals(QZone.NAME)) {// 判断成功的平台是不是空间
            handler.sendEmptyMessage(4);
        }
        if (arg0.getName().equals(WechatFavorite.NAME)) {// 判断成功的平台是不是微信收藏
            handler.sendEmptyMessage(5);
        }
    }

    @Override
    public void onError(Platform arg0, int arg1, Throwable arg2) {
        arg2.printStackTrace();
        Message msg = new Message();
        msg.what = 6;
        msg.obj = arg2.getMessage();
        handler.sendMessage(msg);

    }

    @Override
    public void onCancel(Platform platform, int i) {
        handler.sendEmptyMessage(7);
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(getApplicationContext(), "微信分享成功", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "朋友圈分享成功", Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(), "QQ分享成功", Toast.LENGTH_LONG).show();
                    break;
                case 4:
                    Toast.makeText(getApplicationContext(), "QQ空间分享成功", Toast.LENGTH_LONG).show();
                    break;
                case 5:
                    Toast.makeText(getApplicationContext(), "微信收藏分享成功", Toast.LENGTH_LONG).show();
                    break;
                case 7:
                    Toast.makeText(getApplicationContext(), "取消分享", Toast.LENGTH_LONG).show();
                    break;
                case 6:
                    Toast.makeText(getApplicationContext(), "分享失败", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

    };
}
