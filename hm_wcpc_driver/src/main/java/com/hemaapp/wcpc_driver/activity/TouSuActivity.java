package com.hemaapp.wcpc_driver.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.wcpc_driver.BaseActivity;
import com.hemaapp.wcpc_driver.BaseConfig;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.adapter.SendImageNoticeAdapter;
import com.hemaapp.wcpc_driver.adapter.TagListAdapter;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.DataInfor;
import com.hemaapp.wcpc_driver.module.User;
import com.hemaapp.wcpc_driver.view.FlowLayout.TagFlowLayout;
import com.hemaapp.wcpc_driver.view.ImageWay;
import com.hemaapp.wcpc_driver.view.MyGridView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import xtom.frame.util.XtomFileUtil;
import xtom.frame.util.XtomImageUtil;

/**
 * 投诉
 */
public class TouSuActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;
    ScrollView view;
    private TagFlowLayout group_textview;
    private EditText editText;
    private TextView text_submit;
    private MyGridView gridview;
    private String order_id, driver_id, content, reply_str, complain_id;
    private ArrayList<DataInfor> infors = new ArrayList<>();
    private TagListAdapter adapter;
    private User user;
    private ImageWay imageWay;
    private ArrayList<String> images = new ArrayList<String>();
    private SendImageNoticeAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_tousu);
        super.onCreate(savedInstanceState);
        user = hm_WcpcDriverApplication.getInstance().getUser();
        getKinds();
        imageWay = new ImageWay(mContext, 1, 2) {
            @Override
            public void album() {
                // 注意：若不重写该方法则使用系统相册选取(对应的onActivityResult中的处理方法也应不同)
                Intent it = new Intent(mContext, AlbumActivity.class);
                it.putExtra("limitCount", 4 - images.size());// 图片选择张数限制
                startActivityForResult(it, albumRequestCode);
            }
        };
        imageAdapter = new SendImageNoticeAdapter(mContext, view, images);
        gridview.setAdapter(imageAdapter);
    }

    public void showImageWay() {
        imageWay.show();
    }

    private void getKinds() {
        getNetWorker().dataList("5");
    }

    private void initUserData() {
        if (infors == null || infors.size() == 0)
            group_textview.setVisibility(View.GONE);
        else {
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
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_GET:
            case COMPLAIN_ADD:
                showProgressDialog("请稍后...");
                break;
            case FILE_UPLOAD:
                showProgressDialog("正在上传图片");
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DATA_LIST:
            case COMPLAIN_ADD:
            case FILE_UPLOAD:
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
            case COMPLAIN_ADD:
                HemaArrayResult<String> sResult = (HemaArrayResult<String>) baseResult;
                complain_id = sResult.getObjects().get(0);
                if (images.size() > 0) {
                    fileUpload();
                } else {
                    showTextDialog("投诉成功！");
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
                    showTextDialog("投诉成功！");
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
        }
    }

    private void fileUpload() {
        String imagePath = images.get(0);
        getNetWorker().fileUpload(user.getToken(), "12", complain_id, "0", "0", "无", imagePath);
        images.remove(imagePath);
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DATA_LIST:
            case COMPLAIN_ADD:
            case FILE_UPLOAD:
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
            case COMPLAIN_ADD:
                showTextDialog("抱歉，提交失败，请稍后重试");
                break;
            case FILE_UPLOAD:
                showTextDialog("图片上传失败");
                break;
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        right = (TextView) findViewById(R.id.title_btn_right);
        title = (TextView) findViewById(R.id.title_text);
        view = (ScrollView) findViewById(R.id.view);
        group_textview = (TagFlowLayout) findViewById(R.id.multitextview);
        editText = (EditText) findViewById(R.id.edittext);
        text_submit = (TextView) findViewById(R.id.button);
        gridview = (MyGridView) findViewById(R.id.gridview);
    }

    @Override
    protected void getExras() {
        order_id = mIntent.getStringExtra("order_Id");
        driver_id = mIntent.getStringExtra("driver_id");
    }

    @Override
    protected void setListener() {
        title.setText("投诉");
        right.setVisibility(View.INVISIBLE);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        text_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idstr = "", idstr_text = "";
                for (int i = 0; i < infors.size(); i++) {
                    if (infors.get(i).isChecked()) {
                        if (isNull(idstr_text)) {
                            idstr_text = infors.get(i).getName();
                            idstr = infors.get(i).getId();
                        } else {
                            idstr = idstr + "," + infors.get(i).getId();
                            idstr_text = idstr_text + "," + infors.get(i).getName();
                        }
                    }

                }

                content = editText.getText().toString();
                if (isNull(idstr_text)) {
                    showTextDialog("请选择投诉原因");
                    return;
                }
                getNetWorker().complainAdd(user.getToken(), order_id, driver_id, idstr, idstr_text, content);
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
