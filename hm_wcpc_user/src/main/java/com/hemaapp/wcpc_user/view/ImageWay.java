package com.hemaapp.wcpc_user.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.hemaapp.wcpc_user.R;

import java.io.File;

import xtom.frame.XtomObject;
import xtom.frame.util.XtomBaseUtil;
import xtom.frame.util.XtomFileUtil;

public class ImageWay extends XtomObject {
	
	private Activity mContext;// 上下文对象
	private Fragment mFragment;// 上下文对象
	protected int albumRequestCode;// 相册选择时startActivityForResult方法的requestCode值
	protected int cameraRequestCode;// 拍照选择时startActivityForResult方法的requestCode值
	private static final String IMAGE_TYPE = ".jpg";// 图片名后缀
	private String imagePathByCamera;// 拍照时图片保存路径
	
	private PopupWindow mWindow_exit;
	private ViewGroup mViewGroup_exit;
	private TextView man;
	private TextView bt_cancel;
	private TextView woman;
	public ImageWay(Activity mContext, int albumRequestCode,
                    int cameraRequestCode) {
		this.mContext = mContext;
		this.albumRequestCode = albumRequestCode;
		this.cameraRequestCode = cameraRequestCode;
	}
	
	public ImageWay(Fragment mFragment, int albumRequestCode,
                    int cameraRequestCode) {
		this.mFragment = mFragment;
		this.albumRequestCode = albumRequestCode;
		this.cameraRequestCode = cameraRequestCode;
	}


	public void show() {
		// TODO Auto-generated method stub
		if (mWindow_exit != null) {
			mWindow_exit.dismiss();
		}
		mWindow_exit = new PopupWindow(mContext);
		mWindow_exit.setWidth(LayoutParams.MATCH_PARENT);
		mWindow_exit.setHeight(LayoutParams.MATCH_PARENT);
		mWindow_exit.setBackgroundDrawable(new BitmapDrawable());
		mWindow_exit.setFocusable(true);
		mWindow_exit.setAnimationStyle(R.style.PopupAnimation);
			mViewGroup_exit = (ViewGroup) LayoutInflater.from(mContext).inflate(
					R.layout.pop_sex, null);
			man = (TextView) mViewGroup_exit.findViewById(R.id.textview);
			bt_cancel = (TextView) mViewGroup_exit.findViewById(R.id.textview_2);
			woman = (TextView) mViewGroup_exit.findViewById(R.id.textview_0);
			mWindow_exit.setContentView(mViewGroup_exit);
			mWindow_exit.showAtLocation(mViewGroup_exit, Gravity.CENTER, 0, 0);
			man.setText("拍照");
			woman.setText("从相册选取");
			bt_cancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					mWindow_exit.dismiss();
				}
			});
			man.setOnClickListener(new  View.OnClickListener() {

				@Override
				public void onClick(View v) {
					mWindow_exit.dismiss();
					camera();
					
				}
			});
			woman.setOnClickListener(new  View.OnClickListener() {

				@Override
				public void onClick(View v) {
					mWindow_exit.dismiss();
					album();
					
				}
			});
	}

	public void album() {
		Intent it1 = new Intent(Intent.ACTION_PICK,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		if (mContext != null)
			mContext.startActivityForResult(it1, albumRequestCode);
		else
			mFragment.startActivityForResult(it1, albumRequestCode);
	}

	public void camera() {
		String imageName = XtomBaseUtil.getFileName() + IMAGE_TYPE;
		Intent it3 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		String imageDir = XtomFileUtil
				.getTempFileDir(mContext == null ? mFragment.getActivity()
						: mContext);
		imagePathByCamera = imageDir + imageName;
		File file = new File(imageDir);
		if (!file.exists())
			file.mkdir();
		// 设置图片保存路径
		File out = new File(file, imageName);
		Uri uri = Uri.fromFile(out);
		it3.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		if (mContext != null)
			mContext.startActivityForResult(it3, cameraRequestCode);
		else
			mFragment.startActivityForResult(it3, cameraRequestCode);
	}

	/**
	 * 获取拍照图片路径
	 * 
	 * @return 图片路径
	 */
	public String getCameraImage() {
		return imagePathByCamera;
	}
	
	
}
