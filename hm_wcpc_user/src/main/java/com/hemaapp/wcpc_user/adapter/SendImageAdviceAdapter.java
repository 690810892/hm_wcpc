package com.hemaapp.wcpc_user.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.hemaapp.hm_FrameWork.HemaAdapter;
import com.hemaapp.hm_FrameWork.view.RoundedImageView;
import com.hemaapp.hm_FrameWork.view.ShowLargeImageView;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.activity.FeedBackActivity;
import com.hemaapp.wcpc_user.activity.TouSuActivity;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.io.File;
import java.util.ArrayList;

/**
 * 意见反馈
 */
public class SendImageAdviceAdapter extends HemaAdapter implements OnClickListener {
	private static final int TYPE_ADD = 0;
	private static final int TYPE_IMAGE = 1;

	private View rootView;
	private ArrayList<String> images;

	public SendImageAdviceAdapter(Context mContext, View rootView,
                                  ArrayList<String> images) {
		super(mContext);
		this.rootView = rootView;
		this.images = images;
	}

	@Override
	public int getCount() {
		int count;
		int size = images == null ? 0 : images.size();
		if (size < 8)
			count = size + 1;
		else
			count = 8;
		return count;
	}

	@Override
	public boolean isEmpty() {
		int size = images == null ? 0 : images.size();
		return size == 0;
	}

	@Override
	public int getItemViewType(int position) {
		int size = images == null ? 0 : images.size();
		int count = getCount();
		if (size < 8) {
			if (position == count - 1)
				return TYPE_ADD;
			else
				return TYPE_IMAGE;
		} else {
			return TYPE_IMAGE;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		ViewHolder holder = null;
		if (convertView == null) {
			switch (type) {
				case TYPE_ADD:
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.griditem_sendblog_add, null);
					holder = new ViewHolder();
					findView(convertView, holder);
					convertView.setTag(R.id.TAG_VIEWHOLDER, holder);
					break;
				case TYPE_IMAGE:
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.griditem_sendblog_image, null);
					holder = new ViewHolder();
					findView(convertView, holder);
					convertView.setTag(R.id.TAG_VIEWHOLDER, holder);
					break;
			}
		} else {
			holder = (ViewHolder) convertView.getTag(R.id.TAG_VIEWHOLDER);
		}

		switch (type) {
			case TYPE_ADD:
				setDataAdd(position, holder);
				break;
			case TYPE_IMAGE:
				setDataImage(position, holder);
				break;
		}

		return convertView;
	}

	private void setDataAdd(int position, ViewHolder holder) {
		holder.addButton.setOnClickListener(this);
	}

	private void setDataImage(int position, ViewHolder holder) {
		String path = images.get(position);
		holder.deleteButton.setTag(path);
		holder.deleteButton.setOnClickListener(this);
		if(path.indexOf("http") != -1){//包含http，说明是网上获取的，否则是本地图片地址
			ImageLoader.getInstance().displayImage(path, holder.imageView, hm_WcpcUserApplication.getInstance().getOptions(R.mipmap.img_sendblog));
		}else{
			ImageLoader.getInstance().displayImage(ImageDownloader.Scheme.FILE.wrap(path),
					holder.imageView, hm_WcpcUserApplication.getInstance().getOptions(R.mipmap.img_sendblog));
		}
		holder.imageView.setTag(R.id.TAG, path);
		holder.imageView.setOnClickListener(this);
	}

	private void findView(View view, ViewHolder holder) {
		holder.addButton = (Button) view.findViewById(R.id.button);
		holder.imageView = (RoundedImageView) view.findViewById(R.id.imageview);
		holder.deleteButton = (ImageButton) view.findViewById(R.id.delete);
	}

	private static class ViewHolder {
		Button addButton;
		RoundedImageView imageView;
		ImageButton deleteButton;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button:
				FeedBackActivity activity = (FeedBackActivity) mContext;
				activity.showImageWay();
				break;
			case R.id.delete:
				String dPath = (String) v.getTag();
				File file = new File(dPath);
				file.delete();
				images.remove(dPath);
				notifyDataSetChanged();
				break;
			case R.id.imageview:
				String iPath = (String) v.getTag(R.id.TAG);
				mView = new ShowLargeImageView((Activity) mContext, rootView);
				mView.show();
				if(iPath.indexOf("http") != -1)
					mView.setImageURL(iPath);
				else
					mView.setImagePath(iPath);
				break;
			default:
				break;
		}
	}

	private ShowLargeImageView mView;
}
