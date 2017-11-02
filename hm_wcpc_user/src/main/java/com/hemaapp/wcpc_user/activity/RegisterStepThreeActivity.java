package com.hemaapp.wcpc_user.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.view.RoundedImageView;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseConfig;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.BaseImageWay;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.module.ClientAdd;
import com.hemaapp.wcpc_user.module.User;
import com.hemaapp.wcpc_user.view.IDCard;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import xtom.frame.XtomActivityManager;
import xtom.frame.XtomConfig;
import xtom.frame.image.load.XtomImageTask;
import xtom.frame.util.Md5Util;
import xtom.frame.util.XtomBaseUtil;
import xtom.frame.util.XtomFileUtil;
import xtom.frame.util.XtomSharedPreferencesUtil;

/**
 * Created by WangYuxia on 2016/5/5.
 * 注册最后一步，完善个人信息
 */
public class RegisterStepThreeActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private RoundedImageView image_addavatar;
    private EditText edit_username;
    private LinearLayout layout_sex;
    private TextView text_sex;

    private String username;
    private String tempToken;
    private String password;
    private String invitecode;

    public BaseImageWay imageWay;
    private String tempPath;
    private String imagePathCamera;

    private String nickname, sex = "";

    private PopupWindow mWindow;
    private ViewGroup mViewGroup;
    private TextView boy;
    private TextView girl;
    private TextView cancel;
    private ClientAdd clientAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_register2);
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            imageWay = new BaseImageWay(mContext, 1, 2);
        } else {
            imagePathCamera = savedInstanceState.getString("imagePathCamera");
            imageWay = new BaseImageWay(mContext, 1, 2);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (imageWay != null)
            outState.putString("imagePathCamera", imageWay.getCameraImage());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        deleteCompressPics();
        super.onDestroy();
    }

    // 删除临时图片文件
    private void deleteCompressPics() {
        if (!isNull(imagePathCamera)) {
            File file = new File(imagePathCamera);
            file.delete();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case 1:// 相册选择图片
                album(data);
                break;
            case 2:// 拍照
                camera();
                break;
            case 3:// 裁剪
                image_addavatar.setCornerRadius(90);
                imageWorker.loadImage(new XtomImageTask(image_addavatar, tempPath, mContext));
                break;
        }
    }

    private void camera() {
        if (imagePathCamera == null) {
            imagePathCamera = imageWay.getCameraImage();
        }
        editImage(imagePathCamera, 3);
    }

    private void album(Intent data) {
        if (data == null)
            return;
        Uri selectedImageUri = data.getData();
        startPhotoZoom(selectedImageUri, 3);
    }

    private void editImage(String path, int requestCode) {
        File file = new File(path);
        startPhotoZoom(Uri.fromFile(file), requestCode);
    }

    private void startPhotoZoom(Uri uri, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        //进行适当的放大缩小
        intent.putExtra("scaleUpIfNeeded", true);

        intent.putExtra("noFaceDetection", false);
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", BaseConfig.IMAGE_WIDTH);
        intent.putExtra("aspectY", BaseConfig.IMAGE_WIDTH);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", BaseConfig.IMAGE_WIDTH);
        intent.putExtra("outputY", BaseConfig.IMAGE_WIDTH);
        intent.putExtra("return-data", false);
        startActivityForResult(intent, requestCode);
    }

    private Uri getTempUri() {
        return Uri.fromFile(getTempFile());
    }

    private File getTempFile() {
        String savedir = XtomFileUtil.getTempFileDir(mContext);
        File dir = new File(savedir);
        if (!dir.exists())
            dir.mkdirs();
        // 保存入sdCard
        tempPath = savedir + XtomBaseUtil.getFileName() + ".jpg";// 保存路径
        File file = new File(tempPath);
        try {
            file.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        return file;
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_ADD:
                showProgressDialog("正在保存注册信息");
                break;
            case FILE_UPLOAD:
                showProgressDialog("正在上传头像");
                break;
            case CLIENT_LOGIN:
                showProgressDialog("正在登录");
                break;
            default:
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        // TODO Auto-generated method stub
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_ADD:
            case FILE_UPLOAD:
            case CLIENT_LOGIN:
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
            case CLIENT_ADD:
                HemaArrayResult<ClientAdd> sResult = (HemaArrayResult<ClientAdd>) baseResult;
                clientAdd = sResult.getObjects().get(0);
                String token = clientAdd.getToken();
                if (isNull(tempPath)) {
                    if (clientAdd.getCoupon_count().equals("0"))
                        getNetWorker().clientLogin(username, password);
                    else
                        showCouponWindow();
                } else {
                    getNetWorker().fileUpload(token, "1", "0", "0", "0", "无",
                            tempPath);
                }
                break;
            case FILE_UPLOAD:
                if (clientAdd.getCoupon_count().equals("0"))
                    getNetWorker().clientLogin(username, password);
                else
                    showCouponWindow();
                break;
            case CLIENT_LOGIN:
                HemaArrayResult<User> uResult = (HemaArrayResult<User>) baseResult;
                User user = uResult.getObjects().get(0);
                getApplicationContext().setUser(user);
                XtomSharedPreferencesUtil.save(mContext, "username", username);
                XtomSharedPreferencesUtil.save(mContext, "password", password);
                XtomSharedPreferencesUtil.save(mContext, "isAutoLogin", "true");
                XtomActivityManager.finishAll();
                Intent it = new Intent(this, MainActivity.class);
                startActivity(it);
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
            case CLIENT_ADD:
                showTextDialog(baseResult.getMsg());
                break;
            case FILE_UPLOAD:
                showTextDialog("上传头像失败");
                getNetWorker().clientLogin(username, Md5Util.getMd5(XtomConfig.DATAKEY
                        + Md5Util.getMd5(password)));
                break;
            case CLIENT_LOGIN:
                toLogin();
                break;
            default:
                break;
        }
    }

    private void toLogin() {
        XtomActivityManager.finishAll();
        Intent it = new Intent(mContext, MainActivity.class);
        startActivity(it);
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_ADD:
                showTextDialog("保存注册信息失败");
                break;
            case FILE_UPLOAD:
                showTextDialog("上传头像失败");
                getNetWorker().clientLogin(username, Md5Util.getMd5(XtomConfig.DATAKEY
                        + Md5Util.getMd5(password)));
                break;
            case CLIENT_LOGIN:
                toLogin();
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

        image_addavatar = (RoundedImageView) findViewById(R.id.imageview);
        edit_username = (EditText) findViewById(R.id.edittext);
        layout_sex = (LinearLayout) findViewById(R.id.layout_0);
        text_sex = (TextView) findViewById(R.id.textview_0);
    }

    @Override
    protected void getExras() {
        username = mIntent.getStringExtra("username");
        password = mIntent.getStringExtra("password");
        tempToken = mIntent.getStringExtra("tempToken");
        invitecode = mIntent.getStringExtra("invitecode");
    }

    @Override
    protected void setListener() {
        title.setText("个人信息");
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        right.setText("提交");
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nickname = edit_username.getText().toString();
                if (isNull(nickname)) {
                    showTextDialog("请填写姓名");
                    return;
                }

                String content = "^[\u4E00-\u9FA5]+$";
                char[] str = nickname.toCharArray();
                int count = 0;
                for (int i = 0; i < str.length; i++) {
                    char c = str[i];
                    if (String.valueOf(c).matches(content))
                        count++;
                }
                int length = nickname.length() + count;
                if (length > 16) {
                    showTextDialog("昵称不能超过16个字符,请重新填写");
                    return;
                }

                if (isNull(sex)) {
                    showTextDialog("请选择性别");
                    return;
                }

                String district_name = XtomSharedPreferencesUtil.get(mContext, "district_name");
                if (isNull(invitecode))
                    invitecode = "";
                getNetWorker().clientAdd(tempToken, username, password, nickname, sex, district_name, invitecode);
            }
        });

        image_addavatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageWay.show();
            }
        });

        layout_sex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                showPopWindow();
            }
        });

    }

    private void showPopWindow() {
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
                R.layout.pop_sex, null);
        boy = (TextView) mViewGroup.findViewById(R.id.textview);
        girl = (TextView) mViewGroup.findViewById(R.id.textview_0);
        cancel = (TextView) mViewGroup.findViewById(R.id.textview_2);
        mWindow.setContentView(mViewGroup);
        mWindow.showAtLocation(mViewGroup, Gravity.CENTER, 0, 0);
        boy.setText("男");
        girl.setText("女");
        setListener(boy);
        setListener(girl);
        setListener(cancel);
    }

    private void setListener(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
                switch (v.getId()) {
                    case R.id.textview: // 男
                        sex = "男";
                        text_sex.setText(sex);
                        text_sex.setTextColor(mContext.getResources().getColor(R.color.shenhui));
                        break;
                    case R.id.textview_0: // 女
                        sex = "女";
                        text_sex.setText(sex);
                        text_sex.setTextColor(mContext.getResources().getColor(R.color.shenhui));
                        break;
                    case R.id.textview_2:
                        break;
                }
            }
        });
    }

    //读取相册的权限
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 3) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imageWay.album();
            } else {
                showTextDialog("没有相册权限，请添加后重试");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showCouponWindow() {
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
                R.layout.pop_couple, null);
        TextView count = (TextView) mViewGroup.findViewById(R.id.tv_count);
        TextView price = (TextView) mViewGroup.findViewById(R.id.tv_price);
        TextView price2 = (TextView) mViewGroup.findViewById(R.id.tv_price2);
        TextView tv_time = (TextView) mViewGroup.findViewById(R.id.tv_time);
        TextView tv_button = (TextView) mViewGroup.findViewById(R.id.tv_button);
        mWindow.setContentView(mViewGroup);
        mWindow.showAtLocation(mViewGroup, Gravity.CENTER, 0, 0);
        count.setText("恭喜您获得" + clientAdd.getCoupon_count() + "张");
        price.setText(clientAdd.getCoupon_value() + "元");
        price2.setText(clientAdd.getCoupon_value());
        tv_time.setText("有效期至 " + clientAdd.getCoupon_dateline());
        tv_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNetWorker().clientLogin(username, password);
            }
        });
    }
}
