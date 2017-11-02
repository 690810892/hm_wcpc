package com.hemaapp.wcpc_user.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;


import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * 图片轮播图
 * Created by UU on 2016/9/24.
 */

public class ImageCarouselBanner extends LinearLayout implements ViewPager.OnPageChangeListener {

    boolean limitLess = true;
    boolean loadOnlyWifi = false;
    int mChangeTime = 3000;//默认3s换一次
    int mIndexDrawResId;
    int defaultImageResId = R.mipmap.default_image_big;

    Context mContext;
    ViewPager mViewPager;
    RadioGroup mRadioGroup;
    ImageCarouselListener mListener;
    ImageViewPagerAdapter mAdapter;
    ArrayList<String> mDatas;

    public ImageCarouselBanner(Context context) {
        super(context);
    }

    public ImageCarouselBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWidget(context, attrs);
    }

    public ImageCarouselBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWidget(context, attrs);
    }

    public void startShow() {
        stopShow();
        postDelayed(autoRunnable, mChangeTime);
    }

    public void stopShow() {
        removeCallbacks(autoRunnable);
    }

    public boolean isLimitLess() {
        return limitLess;
    }

    public void setLimitLess(boolean limitLess) {
        this.limitLess = limitLess;
    }

    public int getmChangeTime() {
        return mChangeTime;
    }

    public void setmChangeTime(int changeTime) {
        this.mChangeTime = mChangeTime;
    }

    public int getCurrentItem() {
        return mViewPager.getCurrentItem();
    }

    public void setCurrentItem(int currentItem) {
        mViewPager.setCurrentItem(currentItem);
    }

    private void initWidget(Context context, AttributeSet attrs) {
        mContext = context;
        LayoutInflater layoutInflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View container = layoutInflater.inflate(R.layout.layout_image_carousel_banner, this, true);
        mViewPager = (ViewPager) container.findViewById(R.id.viewpager);
        mRadioGroup = (RadioGroup) container.findViewById(R.id.radiogroup);
    }

    public void onInstance(Context context, ArrayList<String> datas, int indexResId, ImageCarouselListener listener) {
        this.mDatas = datas;
        this.mIndexDrawResId = indexResId;
        this.mListener = listener;
        mAdapter = new ImageViewPagerAdapter();
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setCurrentItem(datas.size()*1500);
        mViewPager.setOffscreenPageLimit(mDatas.size());
        startShow();
    }

    private Runnable autoRunnable = new Runnable() {

        @Override
        public void run() {
            if (mDatas != null && mDatas.size() > 0) {
                int count = mDatas.size();
                if (count > 0) {
                    int next = mViewPager.getCurrentItem() + 1;
                    if (next == count)
                        next = 0;
                    mViewPager.setCurrentItem(next, true);
                    startShow();
                }
            }
        }
    };

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        RadioButton rbt = (RadioButton) mRadioGroup.getChildAt(position % mDatas.size());
        if (rbt != null)
            rbt.setChecked(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class ImageViewPagerAdapter extends PagerAdapter {

        public ImageViewPagerAdapter() {
            mRadioGroup.removeAllViews();
            int size = ((BitmapDrawable) mContext.getResources().getDrawable(R.mipmap.indicator_show_s)).getBitmap().getWidth();
            for (int i = 0; i < mDatas.size(); i++) {
                RadioButton button = new RadioButton(mContext);
                button.setButtonDrawable(mIndexDrawResId);
                button.setId(i);
                button.setClickable(false);
                LayoutParams params2 = new LayoutParams(
                        (i == mDatas.size() - 1) ? size : size * 2, size);
                button.setLayoutParams(params2);
                if (i == 0)
                    button.setChecked(true);
                mRadioGroup.addView(button);
            }
        }

        @Override
        public int getCount() {
            return mDatas == null || mDatas.size() <= 1? 1: Integer.MAX_VALUE ;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View mView = LayoutInflater.from(mContext).inflate(
                    R.layout.viewpager_imageview, null);
            ImageView imageView = (ImageView) mView.findViewById(R.id.imageview);
            if(mDatas == null || mDatas.size() == 0){
                imageView.setImageResource(defaultImageResId);
            }else {
                int index = position % mDatas.size();
                ImageLoader.getInstance().displayImage(mDatas.get(index),imageView,
                        hm_WcpcUserApplication.getInstance().getOptions(defaultImageResId));
            }
            mView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null && mDatas.size() != 0)
                        mListener.onItemClickListener(position % mDatas.size());
                }
            });
            container.addView(mView);
            return mView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
//            Glide.clear((View) object);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int widthResult = MeasureSpec.getSize(widthMeasureSpec);
//
//        int heightResult = 0;
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//        if (heightMode == MeasureSpec.EXACTLY) {
//            heightResult = heightSize;
//        } else {
//            heightResult = widthResult/2;
//            if (heightMode == MeasureSpec.AT_MOST) {
//                heightResult = Math.min(heightResult, heightSize);
//            }
//        }
//        setMeasuredDimension(widthResult, heightResult);
//    }

    public interface ImageCarouselListener {
        void onItemClickListener(int index);
    }
}
