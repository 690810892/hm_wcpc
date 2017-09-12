package com.hemaapp.wcpc_user.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.activity.ShowInternetPageActivity;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.AddListInfor;
import com.hemaapp.wcpc_user.module.SysInitInfo;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import xtom.frame.image.load.XtomImageTask;

/**
 * Created by WangYuxia on 2016/5/6.
 */
public class TopAddViewPagerAdapter extends PagerAdapter {

    protected Context mContext;
    private View view;
    private RadioGroup mIndicator;
    private ArrayList<AddListInfor> infors;

    private int size;

    public TopAddViewPagerAdapter(Context mContext, ArrayList<AddListInfor> infors,
                                  View view) {
        this.mContext = mContext;
        this.infors = infors;
        this.view = view;
        init();
    }

    public void setInfors(ArrayList<AddListInfor> infors) {
        this.infors = infors;
        getCount();
        init();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    private void init() {
        size = ((BitmapDrawable) mContext.getResources().getDrawable(
                R.mipmap.img_indicater_s)).getBitmap().getWidth();
        mIndicator = (RadioGroup) view.findViewById(R.id.radiogroup);
        mIndicator.removeAllViews();
        if (getCount() > 1){
            mIndicator.setVisibility(View.VISIBLE);
            for (int i = 0; i < getCount(); i++) {
                RadioButton button = new RadioButton(mContext);
                button.setButtonDrawable(R.drawable.indicator_show1);
                button.setId(i);
                button.setClickable(false);
                LayoutParams params2 = new LayoutParams(
                        (i == getCount() - 1) ? size : size * 2, size);
                button.setLayoutParams(params2);
                if (i == 0)
                    button.setChecked(true);
                mIndicator.addView(button);
            }
        }else{
            mIndicator.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        init();
        getCount();
        super.notifyDataSetChanged();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) { // 这个方法用来实例化页卡
        View mView;
        mView = LayoutInflater.from(mContext).inflate(
                R.layout.viewpager_imageview, null);
        ImageView imageView = (ImageView) mView.findViewById(R.id.imageview);

        if(infors.get(position).getImgurl() == null || "".equals(infors.get(position).getImgurl())
                || "null".equals(infors.get(position).getImgurl())){
            imageView.setBackgroundResource(R.mipmap.default_image_big);
        } else
            try {
                URL url = new URL(infors.get(position).getImgurl());
                ((BaseActivity) mContext).imageWorker.loadImage(new XtomImageTask(
                        imageView, url, mContext));
            } catch (IOException e) {
                imageView.setBackgroundResource(R.mipmap.default_image_big);
            }

        mView.setTag(R.id.TAG, infors.get(position));
        mView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AddListInfor add = (AddListInfor) v.getTag(R.id.TAG);
                SysInitInfo infor = hm_WcpcUserApplication.getInstance().getSysInitInfo();
                String path = infor.getSys_plugins();
                Intent it;
                if("1".equals(add.getKeytype())){ //网页
                    it = new Intent(mContext, ShowInternetPageActivity.class);
                    it.putExtra("name", "详情");
                    it.putExtra("path", path+"share/advertise.php?id="+add.getId());
                    mContext.startActivity(it);
                }
            }
        });
        container.addView(mView);
        return mView;
    }

    @Override
    public int getCount() {
        return infors == null || infors.size() == 0? 0: infors.size() ;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    public ViewGroup getIndicator() {
        return mIndicator;
    }
}
