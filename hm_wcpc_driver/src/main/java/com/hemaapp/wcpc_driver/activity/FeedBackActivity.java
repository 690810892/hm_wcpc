package com.hemaapp.wcpc_driver.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.HemaUtil;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_driver.BaseActivity;
import com.hemaapp.wcpc_driver.BaseConfig;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.adapter.SendImageAdviceAdapter;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.User;
import com.hemaapp.wcpc_driver.view.ImageWay;
import com.hemaapp.wcpc_driver.view.MyGridView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import xtom.frame.util.XtomFileUtil;
import xtom.frame.util.XtomImageUtil;

/**
 * 意见反馈
 */
public class FeedBackActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;
    private EditText editText;
    private MyGridView gridView;
    private TextView button;
    ScrollView view;
    private User user;
    private ImageWay imageWay;
    private ArrayList<String> images = new ArrayList<String>();
    private SendImageAdviceAdapter imageAdapter;
    String advice_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_feedback);
        super.onCreate(savedInstanceState);
        user = hm_WcpcDriverApplication.getInstance().getUser();
        imageWay = new ImageWay(mContext, 1, 2) {
            @Override
            public void album() {
                // 注意：若不重写该方法则使用系统相册选取(对应的onActivityResult中的处理方法也应不同)
                Intent it = new Intent(mContext, AlbumActivity.class);
                it.putExtra("limitCount", 4 - images.size());// 图片选择张数限制
                startActivityForResult(it, albumRequestCode);
            }
        };
        imageAdapter = new SendImageAdviceAdapter(mContext, view, images);
        gridView.setAdapter(imageAdapter);
    }
    public void showImageWay() {
        imageWay.show();
    }
    @Override
    protected void onDestroy() {
        deleteCompressPics();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case 1:// 相册选择图片
                // albumSystem(data);
                album(data);
                break;
            case 2:// 拍照
                camera();
                break;
            case 3:
                break;
        }
    }

    private void camera() {
        String imagepath = imageWay.getCameraImage();
        new CompressPicTask().execute(imagepath);
    }

    // 自定义相册选择时处理方法
    private void album(Intent data) {
        if (data == null)
            return;
        ArrayList<String> imgList = data.getStringArrayListExtra("images");
        if (imgList == null)
            return;
        for (String img : imgList) {
            log_i(img);
            new CompressPicTask().execute(img);
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case ADVICE_ADD:
            case FILE_UPLOAD:
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
            case ADVICE_ADD:
                showTextDialog("抱歉，操作失败，请稍后重试");
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
            case ADVICE_ADD:
            case FILE_UPLOAD:
                showTextDialog(baseResult.getMsg());
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForServerSuccess(HemaNetTask netTask,
                                            HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case ADVICE_ADD:
                HemaArrayResult<String> sResult = (HemaArrayResult<String>) baseResult;
                advice_id = sResult.getObjects().get(0);
                if (images.size() > 0) {
                    fileUpload();
                } else {
                    showTextDialog("提交成功！");
                    //EventBus.getDefault().post(new EventBusModel(EventBusConfig.REFRESH_NOTICE_LIST));
                    title.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            setResult(RESULT_OK);
                            finish();
                        }
                    }, 1000);
                }
                break;
            case FILE_UPLOAD:
                if (images.size() > 0) {
                    fileUpload();
                } else {
                    showTextDialog("提交成功！");
                    //EventBus.getDefault().post(new EventBusModel(EventBusConfig.REFRESH_NOTICE_LIST));
                    title.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            setResult(RESULT_OK);
                            finish();
                        }
                    }, 1000);
                }
                break;
            default:
                break;
        }
    }
    private void fileUpload() {
        String imagePath = images.get(0);
        getNetWorker().fileUpload(user.getToken(), "13", advice_id, "0", "0", "无", imagePath);
        images.remove(imagePath);
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case ADVICE_ADD:
                showProgressDialog("请稍后...");
                break;
            case FILE_UPLOAD:
                showProgressDialog("正在上传图片");
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
        editText = (EditText) findViewById(R.id.edittext);
        gridView= (MyGridView) findViewById(R.id.gridview);
        button= (TextView) findViewById(R.id.button);
        view= (ScrollView) findViewById(R.id.view);
    }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
        title.setText("意见反馈");
        right.setVisibility(View.GONE);
        right.setText("提交");
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
                500) });
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String suggestion = editText.getText().toString();
                if (isNull(suggestion)) {
                    showTextDialog("反馈意见不能为空,请重新填写");
                    return;
                }

                String device = "android phone"; //设备
                String version = HemaUtil.getAppVersionForSever(mContext); //来源
                String brand = android.os.Build.BRAND+android.os.Build.MODEL; //品牌
                String system = android.os.Build.VERSION.RELEASE; //系统版本

                getNetWorker().adviceAdd(user.getToken(), device, version, brand, system, suggestion, "2");
            }
        });
    }

    // 删除临时图片文件
    private void deleteCompressPics() {
        for (String string : images) {
            File file = new File(string);
            file.delete();
        }
    }

    /**
     * 压缩图片
     */
    private class CompressPicTask extends AsyncTask<String, Void, Integer> {
        String compressPath;

        @Override
        protected Integer doInBackground(String... params) {
            try {
                String path = params[0];
                String savedir = XtomFileUtil.getTempFileDir(mContext);
                compressPath = XtomImageUtil.compressPictureDepthWithSaveDir(path,
                        BaseConfig.IMAGE_HEIGHT, BaseConfig.IMAGE_WIDTH,
                        BaseConfig.IMAGE_QUALITY, savedir, mContext);
                return 0;
            } catch (IOException e) {
                return 1;
            }
        }

        @Override
        protected void onPreExecute() {
            //showProgressDialog("正在压缩图片");
        }

        @Override
        protected void onPostExecute(Integer result) {
            cancelProgressDialog();
            switch (result) {
                case 0:
                    images.add(compressPath);
                    imageAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    showTextDialog("图片压缩失败");
                    break;
            }
        }
    }
}
