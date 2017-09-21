package com.hemaapp.wcpc_user.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
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
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;
import xtom.frame.XtomActivityManager;
import xtom.frame.image.cache.XtomImageCache;
import xtom.frame.util.XtomFileUtil;
import xtom.frame.util.XtomSharedPreferencesUtil;
import xtom.frame.util.XtomToastUtil;

import static com.hemaapp.hm_FrameWork.HemaUtil.getAppVersionForSever;
import static com.hemaapp.hm_FrameWork.HemaUtil.isNeedUpDate;
import static com.hemaapp.wcpc_user.BaseHttpInformation.INIT;

/**
 * Created by WangYuxia on 2016/5/19.
 * 设置
 */
public class SetActivity extends BaseActivity implements View.OnClickListener{

    private ImageView left;
    private TextView title;
    private TextView right;

    private TextView text_share;
    private TextView text_feedback;
    private LinearLayout layout_clearcache;
    private TextView text_clearcache;
    private TextView text_update;
    private TextView text_aboutus;

    private User user;
    private SysInitInfo infor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_set);
        super.onCreate(savedInstanceState);

        user = hm_WcpcUserApplication.getInstance().getUser();
        // 获取图片的缓存
        long size1 = XtomImageCache.getInstance(mContext).getCacheSize();

        String content = BaseUtil.getSize(size1);
        text_clearcache.setText(content);
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
                Intent it = new Intent(mContext, MainActivity.class);
                startActivity(it);
                break;
            default:
                break;
        }
    }

    private HemaButtonDialog updateDialog;

    private void showContentDialog(){
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
        Intent it ;
        switch (view.getId()){
            case R.id.textview_0:
                showshare();
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
                SysInitInfo sysInitInfo = hm_WcpcUserApplication.getInstance().getSysInitInfo();
                String path = sysInitInfo.getSys_web_service()+"webview/parm/aboutus";
                it.putExtra("path", path);
                startActivity(it);
                break;
            case R.id.button:
                showExitDialog();
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
            upGrade = new UpGrade(mContext){
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

    private void showExitDialog(){
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

    private void showshare() {
        final OnekeyShare oks = new OnekeyShare();

        oks.setTitle(mContext.getResources().getString(R.string.app_name));

        SysInitInfo initInfo = getApplicationContext().getSysInitInfo();

        // text是分享文本，所有平台都需要这个字段
        String text = initInfo.getMsg_invite();
        oks.setText(text);

        String imgUrl = getLogoImagePath();
        oks.setImagePath(imgUrl);

        //必须加上
        String imageulr = "http://101.200.191.117/hm_sfzc/download/mobile/";
        oks.setUrl(imageulr);

        oks.setComment("给力，相当给力！");

        oks.setTheme(OnekeyShareTheme.CLASSIC);

        // 令编辑页面显示为Dialog模式
        oks.setDialogMode();

        // 在自动授权时可以禁用SSO方式
        // if(!CustomShareFieldsPage.getBoolean("enableSSO", true))
        // oks.disableSSOWhenAuthorize();

        // 去除注释，则快捷分享的操作结果将通过OneKeyShareCallback回调
        oks.setCallback(new PlatformActionListener() {

            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                XtomToastUtil.showShortToast(mContext, "分享失败");
            }

            @Override
            public void onComplete(Platform arg0, int arg1,
                                   HashMap<String, Object> arg2) {
                XtomToastUtil.showShortToast(mContext, "分享成功");
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
                XtomToastUtil.showShortToast(mContext, "分享取消");
            }
        });
        oks.show(mContext);
    }

    // 获取软件Logo文件地址
    private String getLogoImagePath() {
        String imagePath;
        try {
            String cachePath_internal = XtomFileUtil.getCacheDir(mContext)
                    + "/images/";// 获取缓存路径
            File dirFile = new File(cachePath_internal);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            imagePath = cachePath_internal + "icon.png";
            File file = new File(imagePath);
            if (!file.exists()) {
                file.createNewFile();
                Bitmap pic = BitmapFactory.decodeResource(
                        mContext.getResources(), R.mipmap.ic_launcher);
                FileOutputStream fos = new FileOutputStream(file);
                pic.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            imagePath = null;
        }
        return imagePath;
    }
	/* 分享相关end */
}
