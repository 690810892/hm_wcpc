package com.hemaapp.wcpc_user;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.File;

import xtom.frame.XtomObject;
import xtom.frame.util.XtomBaseUtil;
import xtom.frame.util.XtomFileUtil;

/**
 * '
 */
public class BaseImageWay extends XtomObject {
	private Activity mContext;// K
	private Fragment mFragment;// K

	private PopupWindow mWindow;
	private ViewGroup mViewGroup;
	private TextView boy;
	private TextView girl;
	private TextView cancel;
	
	protected int albumRequestCode;// startActivityForResultrequestCode
	protected int cameraRequestCode;// startActivityForResultrequestCode
	private static final String IMAGE_TYPE = ".jpg";//
	private String imagePathByCamera;// ·

	/**
	 * '
	 * 
	 * @param mContext
	 *            K
	 * @param albumRequestCode
	 *            startActivityForResultrequestCode
	 * @param cameraRequestCode
	 *            startActivityForResultrequestCode
	 */
	public BaseImageWay(Activity mContext, int albumRequestCode,
                        int cameraRequestCode) {
		this.mContext = mContext;
		this.albumRequestCode = albumRequestCode;
		this.cameraRequestCode = cameraRequestCode;
	}

	/**
	 * '
	 *
	 *            K
	 * @param albumRequestCode
	 *            startActivityForResultrequestCode
	 * @param cameraRequestCode
	 *            startActivityForResultrequestCode
	 */
	public BaseImageWay(Fragment mFragment, int albumRequestCode,
                        int cameraRequestCode) {
		this.mFragment = mFragment;
		this.albumRequestCode = albumRequestCode;
		this.cameraRequestCode = cameraRequestCode;
	}

	/**
	 * 
	 */
	@SuppressWarnings("deprecation")
	public void show() {
		
		if (mWindow != null) {
			mWindow.dismiss();
		}
		mWindow = new PopupWindow(mContext);
		mWindow.setWidth(LayoutParams.MATCH_PARENT);
		mWindow.setHeight(LayoutParams.MATCH_PARENT);
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
		boy.setText("拍照");
		boy.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mWindow.dismiss();
				click(1);
			}
		});
		
		girl.setText("从手机相册选择");
		girl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mWindow.dismiss();
				click(0);
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mWindow.dismiss();
			}
		});
	}
	
	

	private void click(int which) {
		switch (which) {
		case 0:
			checkAlbum();
			break;
		case 1:
			camera();
			break;
		case 2:
			break;
		}
	}

	//检查相册权限
	private void checkAlbum(){
		if(mContext != null){
			if (ContextCompat.checkSelfPermission(mContext,
					Manifest.permission.WRITE_EXTERNAL_STORAGE)
					!= PackageManager.PERMISSION_GRANTED) {//判断是否拥有读取相册的权限
				ActivityCompat.requestPermissions(mContext,
						new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,},
						3);
			} else {
				album();
			}
		}else if(mFragment != null){
			if (ContextCompat.checkSelfPermission(mFragment.getActivity(),
					Manifest.permission.WRITE_EXTERNAL_STORAGE)
					!= PackageManager.PERMISSION_GRANTED) {//判断是否拥有读取相册的权限
				ActivityCompat.requestPermissions(mFragment.getActivity(),
						new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
						3);
			} else {
				album();
			}
		}
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
		// ·
		File out = new File(file, imageName);
		Uri uri = Uri.fromFile(out);
		it3.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		if (mContext != null)
			mContext.startActivityForResult(it3, cameraRequestCode);
		else
			mFragment.startActivityForResult(it3, cameraRequestCode);
	}

	/**
	 * ·
	 * 
	 * @return ·
	 */
	public String getCameraImage() {
		return imagePathByCamera;
	}

}