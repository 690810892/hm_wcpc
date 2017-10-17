package com.hemaapp.wcpc_driver.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.adapter.TypePopAdapter;
import com.hemaapp.wcpc_driver.module.TypeInfor;

import java.util.ArrayList;

import xtom.frame.XtomObject;

/**
 * 筛选
 * */
public class TypePopupMenu extends XtomObject {
	private Context mContext;
	private PopupWindow mWindow;
	private ViewGroup mViewGroup;
	private ProgressBar progressbar;
	private ListView listview;
	
	private ArrayList<TypeInfor> items;
	private int sel_index;
	
	private TypePopAdapter adapter;

	@SuppressWarnings("deprecation")
	public TypePopupMenu(Context context, ArrayList<TypeInfor> items, int sel) {
		mContext = context;
		this.items = items;
		sel_index = sel;
		
		mWindow = new PopupWindow(mContext);
		mWindow.setWidth(LayoutParams.MATCH_PARENT);
		mWindow.setHeight(LayoutParams.MATCH_PARENT);
		mWindow.setBackgroundDrawable(new BitmapDrawable());
		mWindow.setFocusable(true);
		mViewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(
				R.layout.popup_list, null);
		View downview = mViewGroup.findViewById(R.id.downview);
		downview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dimiss();
			}
		});
		
		progressbar = (ProgressBar) mViewGroup.findViewById(R.id.progressBar);
		listview = (ListView) mViewGroup.findViewById(R.id.listview);
		
        if(items!=null && items.size() >0){
			adapter = new TypePopAdapter(context, this.items, sel_index);
			listview.setAdapter(adapter);
			adapter.setIndex(sel_index);
			progressbar.setVisibility(View.GONE);
			listview.setVisibility(View.VISIBLE);
        }
		mWindow.setContentView(mViewGroup);
	}
	
	public void show() {
		mWindow.showAtLocation(mViewGroup, Gravity.CENTER, 0, 0);
	}
	
	public void showAsDropDown(View anchor) {
		mWindow.showAsDropDown(anchor);
	}
	
	public void setlistviewclick(OnItemClickListener l ) {
		listview.setOnItemClickListener(l);
	}
	
	public void setitems(ArrayList<TypeInfor> items, int sel) {
		this.items = items;
		sel_index = sel;
		
		if(adapter==null){
			adapter = new TypePopAdapter(mContext, this.items, sel_index);
			listview.setAdapter(adapter);
			adapter.setIndex(sel_index);
			progressbar.setVisibility(View.GONE);
			listview.setVisibility(View.VISIBLE);
		}else{
		    adapter.setTypes(items, sel_index);
		    adapter.notifyDataSetChanged();
		}
	}
	
	public void setitems_sel(int sel) {
		sel_index = sel;
		if(adapter!=null){
		    adapter.setIndex(sel_index);
		    adapter.notifyDataSetChanged();
		}
	}
	
    public void setondismisslisener(PopupWindow.OnDismissListener l) {
    	mWindow.setOnDismissListener(l);
    }

	public void dimiss() {
		mWindow.dismiss();
	}
}
