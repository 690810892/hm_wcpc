package com.hemaapp.wcpc_user.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.dialog.HemaButtonDialog;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.BaseUtil;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.UpGrade;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.SysInitInfo;
import com.hemaapp.wcpc_user.module.User;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import xtom.frame.XtomActivityManager;
import xtom.frame.image.cache.XtomImageCache;
import xtom.frame.util.XtomFileUtil;
import xtom.frame.util.XtomSharedPreferencesUtil;
import xtom.frame.util.XtomToastUtil;

import static com.hemaapp.hm_FrameWork.HemaUtil.getAppVersionForSever;
import static com.hemaapp.hm_FrameWork.HemaUtil.isNeedUpDate;

/**
 * Created by WangYuxia on 2016/5/19.
 * 设置
 */
public class SetActivity extends BaseActivity implements View.OnClickListener, PlatformActionListener {

    private ImageView left;
    private TextView title;
    private TextView right;

    private TextView text_share;
    private TextView text_feedback;
    private LinearLayout layout_clearcache;
    private TextView text_clearcache;
    private TextView text_update;
    private TextView text_aboutus;
    private TextView text_dafen;
    private TextView text_shuoming;
    private TextView text_xieyi;

    private User user;
    private SysInitInfo infor;
    private PopupWindow mWindow_exit;//分享
    private ViewGroup mViewGroup_exit;
    private String sys_plugins;
    private String pathWX;
    private String imageurl;
    private OnekeyShare oks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_set);
        super.onCreate(savedInstanceState);
        ShareSDK.initSDK(this);
        user = hm_WcpcUserApplication.getInstance().getUser();
        // 获取图片的缓存
        long size1 = XtomImageCache.getInstance(mContext).getCacheSize();

        String content = BaseUtil.getSize(size1);
        text_clearcache.setText(content);
        SysInitInfo initInfo = getApplicationContext()
                .getSysInitInfo();
        sys_plugins = initInfo.getSys_plugins();
        pathWX = sys_plugins + "share/sdk.php?invitecode=" + user.getInvitecode() + "&keyid=0" + "&type=1";
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case INIT:
                cancelProgressDialog();
                break;
            case CLIENT_LOGINOUT:
                cancelProgressDialog();
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case INIT:
                showTextDialog("检查版本信息失败，请稍后重试");
                break;
            case CLIENT_LOGINOUT:
                showTextDialog("退出登录失败，请稍后重试");
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case INIT:
                showTextDialog("检查版本信息失败，请稍后重试");
                break;
            case CLIENT_LOGINOUT:
                showTextDialog("退出登录失败，请稍后重试");
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void callBackForServerSuccess(HemaNetTask netTask,
                                            HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case INIT:
                HemaArrayResult<SysInitInfo> sResult = (HemaArrayResult<SysInitInfo>) baseResult;
                infor = sResult.getObjects().get(0);
                String lastversion = getAppVersionForSever(mContext);
                String version = infor.getAndroid_last_version();
                if (isNeedUpDate(lastversion, version)) {
                    showContentDialog();
                } else {
                    showTextDialog("当前已经是最新版本了");
                }
                break;
            case CLIENT_LOGINOUT:
                XtomSharedPreferencesUtil.save(mContext, "isAutoLogin", "false");
                XtomActivityManager.finishAll();
                hm_WcpcUserApplication.getInstance().setUser(null);
                Intent it = new Intent(mContext, MainNewActivity.class);
                startActivity(it);
                break;
            default:
                break;
        }
    }

    private HemaButtonDialog updateDialog;

    private void showContentDialog() {
        if (updateDialog == null) {
            updateDialog = new HemaButtonDialog(mContext);
            updateDialog.setText("有新的软件版本\n是否升级？");
            updateDialog.setLeftButtonText("取消");
            updateDialog.setRightButtonText("升级");
            updateDialog.setButtonListener(new ButtonListener());
        }
        updateDialog.show();
    }

    @Override
    public void onClick(View view) {
        Intent it;
        SysInitInfo sysInitInfo;
        String path;
        switch (view.getId()) {
            case R.id.textview_0:
                share();
                break;
            case R.id.textview_1:
                it = new Intent(mContext, FeedBackActivity.class);
                startActivity(it);
                break;
            case R.id.layout:
                new ClearTask().execute();
                break;
            case R.id.textview_3:
                getNetWorker().init();
                break;
            case R.id.textview_4:
                it = new Intent(mContext, ShowInternetPageActivity.class);
                it.putExtra("name", "关于我们");
                sysInitInfo = hm_WcpcUserApplication.getInstance().getSysInitInfo();
                path = sysInitInfo.getSys_web_service() + "webview/parm/aboutus";
                it.putExtra("path", path);
                startActivity(it);
                break;
            case R.id.button:
                showExitDialog();
                break;
            case R.id.tv_dafen:
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                it = new Intent(Intent.ACTION_VIEW, uri);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(it);
                break;
            case R.id.tv_shuoming:
                it = new Intent(mContext, ShowInternetPageActivity.class);
                it.putExtra("name", "软件使用说明");
                sysInitInfo = hm_WcpcUserApplication.getInstance().getSysInitInfo();
                path = sysInitInfo.getSys_web_service() + "webview/parm/useinstruction";
                it.putExtra("path", path);
                startActivity(it);
                break;
            case R.id.tv_xieyi:
                it = new Intent(mContext, ShowInternetPageActivity.class);
                it.putExtra("name", "用户协议");
                sysInitInfo = hm_WcpcUserApplication.getInstance().getSysInitInfo();
                path = sysInitInfo.getSys_web_service() + "webview/parm/agreement";
                it.putExtra("path", path);
                startActivity(it);
                break;
        }
    }

    private class ButtonListener implements HemaButtonDialog.OnButtonListener {
        private UpGrade upGrade;

        @Override
        public void onLeftButtonClick(HemaButtonDialog dialog) {
            dialog.cancel();
        }

        @Override
        public void onRightButtonClick(HemaButtonDialog dialog) {
            dialog.cancel();
            upGrade = new UpGrade(mContext) {
                @Override
                public void NoNeedUpdate() {
                }
            };
            upGrade.upGrade(infor);
        }
    }


    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case INIT:
                showProgressDialog("请稍后...");
                break;
            case CLIENT_LOGINOUT:
                showProgressDialog("请稍后...");
                break;
            default:
                break;
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        right = (TextView) findViewById(R.id.title_btn_right);
        title = (TextView) findViewById(R.id.title_text);

        text_share = (TextView) findViewById(R.id.textview_0);
        text_feedback = (TextView) findViewById(R.id.textview_1);
        layout_clearcache = (LinearLayout) findViewById(R.id.layout);
        text_clearcache = (TextView) findViewById(R.id.textview_2);
        text_update = (TextView) findViewById(R.id.textview_3);
        text_aboutus = (TextView) findViewById(R.id.textview_4);
        exit = (TextView) findViewById(R.id.button);
        text_dafen = (TextView) findViewById(R.id.tv_dafen);
        text_shuoming = (TextView) findViewById(R.id.tv_shuoming);
        text_xieyi = (TextView) findViewById(R.id.tv_xieyi);

    }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
        title.setText("设置");
        right.setVisibility(View.INVISIBLE);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        text_dafen.setOnClickListener(this);
        text_shuoming.setOnClickListener(this);
        text_xieyi.setOnClickListener(this);

        text_share.setOnClickListener(this);
        text_feedback.setOnClickListener(this);
        layout_clearcache.setOnClickListener(this);
        text_update.setOnClickListener(this);
        text_aboutus.setOnClickListener(this);
        exit.setOnClickListener(this);
    }

    private PopupWindow mWindow;
    private ViewGroup mViewGroup;
    private TextView exit;
    private TextView cancel;

    private void showExitDialog() {
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
                R.layout.pop_exit, null);
        exit = (TextView) mViewGroup.findViewById(R.id.textview_1);
        cancel = (TextView) mViewGroup.findViewById(R.id.textview_0);
        mWindow.setContentView(mViewGroup);
        mWindow.showAtLocation(mViewGroup, Gravity.CENTER, 0, 0);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
                getNetWorker().clientLoginout(user.getToken(), "1");
            }
        });
    }

    private class ClearTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // 删除图片缓存
            XtomImageCache.getInstance(mContext).deleteCache();
            return null;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog("正在清除缓存");
        }

        @Override
        protected void onPostExecute(Void result) {
            cancelProgressDialog();
            text_clearcache.setText("0k");
            showTextDialog("清除完成");
        }
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
        if (oks == null) {
            oks = new OnekeyShare();
            oks.setTitle("念念不忘想起你，只想送你份好礼，注册即得50元代金券！");
            oks.setTitleUrl(pathWX); // 标题的超链接
            oks.setText("莱芜 ⇌ 济南25元；\n" +
                    "莱芜 ⇌ 泰安15元。");
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
                    getNetWorker().shareCallback(user.getToken(),"1","0","1");
                    Toast.makeText(getApplicationContext(), "微信分享成功", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    getNetWorker().shareCallback(user.getToken(),"1","0","2");
                    Toast.makeText(getApplicationContext(), "朋友圈分享成功", Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    getNetWorker().shareCallback(user.getToken(),"1","0","3");
                    Toast.makeText(getApplicationContext(), "QQ分享成功", Toast.LENGTH_LONG).show();
                    break;
                case 4:
                    getNetWorker().shareCallback(user.getToken(),"1","0","4");
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
