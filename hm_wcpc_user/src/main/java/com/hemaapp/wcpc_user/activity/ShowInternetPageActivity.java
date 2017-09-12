package com.hemaapp.wcpc_user.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.view.HemaWebView;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.R;

/**
 * Created by WangYuxia on 2016/5/4.
 */
public class ShowInternetPageActivity extends BaseActivity {

    private TextView titleText;
    private ImageView titleLeft;
    private TextView titleRight;

    private HemaWebView webView;
    private String titlename;
    private String path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_showinternetpage);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
    }

    @Override
    protected void callBackForServerSuccess(HemaNetTask netTask,
                                            HemaBaseResult baseResult) {

    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
    }

    @Override
    protected void findView() {
        titleText = (TextView) findViewById(R.id.title_text);
        titleLeft = (ImageView) findViewById(R.id.title_btn_left);
        titleRight = (TextView) findViewById(R.id.title_btn_right);

        webView = (HemaWebView) findViewById(R.id.webview);
    }

    @Override
    protected void getExras() {
        titlename = mIntent.getStringExtra("name");
        path = mIntent.getStringExtra("path");
    }

    @Override
    protected void setListener() {
        titleText.setText(titlename);
        titleLeft.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleRight.setVisibility(View.GONE);
        webView.loadUrl(path);
    }
}