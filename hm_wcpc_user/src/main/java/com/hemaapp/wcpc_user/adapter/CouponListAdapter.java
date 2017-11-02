package com.hemaapp.wcpc_user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaAdapter;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.module.CouponListInfor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by WangYuxia on 2016/5/17.
 * 优惠券列表的数据适配器
 */
public class CouponListAdapter extends HemaAdapter {

    private ArrayList<CouponListInfor> infors;

    public CouponListAdapter(Context mContext, ArrayList<CouponListInfor> infors) {
        super(mContext);
        this.infors = infors;
    }

    @Override
    public boolean isEmpty() {
        if(infors == null || infors.size() == 0)
            return true;
        return false;
    }

    @Override
    public int getCount() {
        return infors == null || infors.size() == 0 ? 1 : infors.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(isEmpty())
            return getEmptyView(parent);
        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_couponlist, null);
            holder = new ViewHolder();
            holder.image_bg = (ImageView) convertView.findViewById(R.id.imageview);
            holder.text_money = (TextView) convertView.findViewById(R.id.textview_0);
            holder.text_regdate = (TextView) convertView.findViewById(R.id.textview_1);
            holder.text_danwei = (TextView) convertView.findViewById(R.id.tv_danwei);

            convertView.setTag(R.id.TAG, holder);
        }else{
            holder = (ViewHolder) convertView.getTag(R.id.TAG);
        }
        CouponListInfor infor = infors.get(position);
        holder.text_money.setText(infor.getValue());
        if("1".equals(infor.getUseflag())){
            holder.image_bg.setImageResource(R.mipmap.bg_coupon_used);
            holder.text_regdate.setTextColor(0xffffffff);
            holder.text_money.setTextColor(0xffffffff);
            holder.text_danwei.setTextColor(0xffffffff);
        }else if("1".equals(infor.getDateflag())){
            holder.image_bg.setImageResource(R.mipmap.bg_coupon_outdate);
            holder.text_regdate.setTextColor(0xffffffff);
            holder.text_money.setTextColor(0xffffffff);
            holder.text_danwei.setTextColor(0xffffffff);
        }else {
            holder.image_bg.setImageResource(R.mipmap.bg_coupon_using);
            holder.text_money.setTextColor(0xfff49400);
            holder.text_regdate.setTextColor(0xfff49400);
            holder.text_danwei.setTextColor(0xfff49400);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd");
        try {
            Date d = sdf.parse(infor.getDateline());
            String time = sdf1.format(d);
            holder.text_regdate.setText("有效期至"+time);
        } catch (ParseException e) {
            holder.text_regdate.setText("有效期至");
        }
        return convertView;
    }

    private static class ViewHolder{
        ImageView image_bg;
        TextView text_money;
        TextView text_regdate;
        TextView text_danwei;
    }
}
