/*
 * Copyright (C) 2014 The Android Client Of QK Project
 * 
 *     The BeiJing PingChuanJiaHeng Technology Co., Ltd.
 * 
 * Author:Yang ZiTian
 * You Can Contact QQ:646172820 Or Email:mail_yzt@163.com
 */
package com.hemaapp.wcpc_user.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 
 */
public class AutoChangeViewPager extends ViewPager {
	private Runnable nextRunnable = new Runnable() {

		@Override
		public void run() {
			PagerAdapter adapter = getAdapter();
			if (adapter != null) {
				int count = adapter.getCount();
				if (count > 0) {
					int next = getCurrentItem() + 1;
					if (next == count)
						next = 0;
					setCurrentItem(next, true);
				}
				startNext();
			}
		}
	};

	public void setAdapter(PagerAdapter adapter) {
		super.setAdapter(adapter);
		startNext();
	};

	public void startNext() {
		stopNext();
		postDelayed(nextRunnable, 4000);
	}

	public void stopNext() {
		removeCallbacks(nextRunnable);
	}

	/**
	 * @param context
	 */
	public AutoChangeViewPager(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public AutoChangeViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		stopNext();
		switch (ev.getAction()) {
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			startNext();
			break;
		default:
			break;
		}
		return super.onTouchEvent(ev);
	}

}
