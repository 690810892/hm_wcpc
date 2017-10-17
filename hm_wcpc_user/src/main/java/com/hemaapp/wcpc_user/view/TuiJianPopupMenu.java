package com.hemaapp.wcpc_user.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;


import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.adapter.TuiJianPopAdapter;
import com.hemaapp.wcpc_user.module.PopItem;

import java.util.ArrayList;

import xtom.frame.XtomObject;

public class TuiJianPopupMenu extends XtomObject {
	private Context mContext;
	private PopupWindow mWindow;
	private ViewGroup mViewGroup;
	private ProgressBar progressbar;
	private ListView listview;
	private FrameLayout layout;

	private ArrayList<PopItem> items;

	private TuiJianPopAdapter adapter;

	@SuppressWarnings("deprecation")
	public TuiJianPopupMenu(Context context) {
		mContext = context;

		mWindow = new PopupWindow(mContext);
		mWindow.setWidth(LayoutParams.MATCH_PARENT);
		mWindow.setHeight(LayoutParams.MATCH_PARENT);
		mWindow.setBackgroundDrawable(new BitmapDrawable());
		mWindow.setFocusable(true);
		mViewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(
				R.layout.popup_goodslist, null);
		View downview = mViewGroup.findViewById(R.id.downview);
		downview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dimiss();
			}
		});

		progressbar = (ProgressBar) mViewGroup.findViewById(R.id.progressBar2);
		listview = (ListView) mViewGroup.findViewById(R.id.listview);
		layout = (FrameLayout) mViewGroup.findViewById(R.id.framelayout);

		mWindow.setContentView(mViewGroup);
	}

	public void show() {
		mWindow.showAtLocation(mViewGroup, Gravity.CENTER, 0, 0);
	}

	public void showAsDropDown(View anchor) {
		mWindow.showAsDropDown(anchor);
	}

	public void setlistviewclick(OnItemClickListener l) {
		listview.setOnItemClickListener(l);
	}

	public void setitems(ArrayList<PopItem> items, int position) {
		this.items = items;
		if (adapter == null) {
			adapter = new TuiJianPopAdapter(mContext, this.items, 0);
			listview.setAdapter(adapter);
			progressbar.setVisibility(View.GONE);
			setListViewHeightBasedOnChildren(listview, layout);
			listview.setVisibility(View.VISIBLE);
		} else {
			adapter.setfenleis(items, position);
			setListViewHeightBasedOnChildren(listview, layout);
			adapter.notifyDataSetChanged();
		}
	}
	
	public static void setListViewHeightBasedOnChildren(ListView listView, FrameLayout layout) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {  
            return;  
        }  
  
        int totalHeight = 0;  
        for (int i = 0; i < listAdapter.getCount(); i++) {  
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);  
            totalHeight += listItem.getMeasuredHeight();  
        }  
  
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));  
        if(params.height > 660)
        	params.height = 660;
        listView.setLayoutParams(params);
        
        LayoutParams p = new LayoutParams(params.width, params.height+20);
        if(p.height>680)
        	p.height = 690;
        layout.setLayoutParams(p);
    }  

	public void setondismisslisener(PopupWindow.OnDismissListener l) {
		mWindow.setOnDismissListener(l);
	}

	public void dimiss() {
		mWindow.dismiss();
	}
}
