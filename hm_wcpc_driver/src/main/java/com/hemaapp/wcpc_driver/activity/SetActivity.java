package com.hemaapp.wcpc_driver.activity;

import android.content.Intent;
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
import com.hemaapp.wcpc_driver.BaseActivity;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.BaseUtil;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.UpGrade;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.SysInitInfo;
import com.hemaapp.wcpc_driver.module.User;

import xtom.frame.XtomActivityManager;
import xtom.frame.image.cache.XtomImageCache;
import xtom.frame.util.XtomSharedPreferencesUtil;

import static com.hemaapp.hm_FrameWork.HemaUtil.getAppVersionForSever;
import static com.hemaapp.hm_FrameWork.HemaUtil.isNeedUpDate;

/**
 * Created by WangYuxia on 2016/5/26.
 */
public class SetActivity extends BaseActivity {
    private ImageView left;
    private TextView title;
    private TextView right;

    private TextView text_introduction;
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

        user = hm_WcpcDriverApplication.getInstance().getUser();
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
                Intent it = new Intent(mContext, LoginActivity.class);
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

        text_introduction = (TextView) findViewById(R.id.textview_0);
        layout_clearcache = (LinearLayout) findViewById(R.id.layout);
        text_clearcache = (TextView) findViewById(R.id.textview_1);
        text_update = (TextView) findViewById(R.id.textview_2);
        text_aboutus = (TextView) findViewById(R.id.textview_3);
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

        setListener(text_introduction);
        setListener(layout_clearcache);
        setListener(text_update);
        setListener(text_aboutus);
        setListener(exit);
    }

    private void setListener(View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sys_web_service = getApplicationContext().getSysInitInfo()
                        .getSys_web_service();
                Intent it;
                switch (v.getId()){
                    case R.id.textview_0:
                        String pathStr = sys_web_service + "webview/parm/useinstruction";
                        it = new Intent(mContext, ShowInternetPageActivity.class);
                        it.putExtra("name", "使用说明");
                        it.putExtra("path", pathStr);
                        startActivity(it);
                        break;
                    case R.id.layout:
                        new ClearTask().execute();
                        break;
                    case R.id.textview_2:
                        getNetWorker().init();
                        break;
                    case R.id.textview_3:
                        it = new Intent(mContext, ShowInternetPageActivity.class);
                        it.putExtra("name", "关于我们");
                        it.putExtra("path", sys_web_service+"webview/parm/aboutus_driver");
                        startActivity(it);
                        break;
                    case R.id.button:
                        showExitDialog();
                        break;
                }
            }
        });
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
        exit = (TextView) mViewGroup.findViewById(R.id.textview);
        cancel = (TextView) mViewGroup.findViewById(R.id.textview_1);
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
                getNetWorker().clientLoginout(user.getToken(), "2");
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
}
