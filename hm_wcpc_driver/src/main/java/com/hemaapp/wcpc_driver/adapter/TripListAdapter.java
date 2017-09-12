package com.hemaapp.wcpc_driver.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaAdapter;
import com.hemaapp.hm_FrameWork.view.RoundedImageView;
import com.hemaapp.wcpc_driver.BaseUtil;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.activity.GrapTripDetailInforActivity;
import com.hemaapp.wcpc_driver.activity.MainActivity;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.TripListInfor;
import com.hemaapp.wcpc_driver.module.User;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import xtom.frame.image.load.XtomImageTask;
import xtom.frame.util.XtomSharedPreferencesUtil;
import xtom.frame.view.XtomListView;

/**
 * Created by WangYuxia on 2016/5/25.
 */
public class TripListAdapter extends HemaAdapter {

    private ArrayList<TripListInfor> infors;
    private XtomListView mListView;
    private TripListInfor trip;

    public TripListAdapter(Context mContext, ArrayList<TripListInfor> infors, XtomListView mListView) {
        super(mContext);
        this.infors = infors;
        this.mListView = mListView;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_qiandan, null);
            holder = new ViewHolder();
            findview(holder, convertView);
            convertView.setTag(R.id.TAG, holder);
        }else{
            holder = (ViewHolder) convertView.getTag(R.id.TAG);
        }
        TripListInfor infor = infors.get(position);
        setdata(infor, convertView, holder, position);
        return convertView;
    }

    private void setdata(TripListInfor infor, View view, ViewHolder holder, int position){
        holder.text_time.setText(BaseUtil.transTimeChat(infor.getBegintime()));
        holder.text_takepersoncount.setText("乘车人数"+infor.getNumbers()+"人");
        try {
            URL url = new URL(infor.getAvatar());
            holder.image_avatar.setCornerRadius(90);
            XtomImageTask task = new XtomImageTask(holder.image_avatar, url, mContext);
            mListView.addTask(position, 0, task);
        } catch (MalformedURLException e) {
            holder.image_avatar.setImageResource(R.mipmap.default_user);
        }
        holder.text_realname.setText(infor.getRealname());
        if("男".equals(infor.getSex()))
            holder.image_sex.setImageResource(R.mipmap.img_sex_boy);
        else
            holder.image_sex.setImageResource(R.mipmap.img_sex_girl);
        holder.text_takecount.setText("乘车次数 "+infor.getTakecount()+"次");
        holder.text_startaddress.setText(infor.getStartaddress());
        holder.text_endaddress.setText(infor.getEndaddress());
        holder.text_money.setText(infor.getSuccessfee());
        holder.text_operate.setTag(R.id.button, infor);
        holder.text_operate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trip = (TripListInfor) v.getTag(R.id.button);
                User user = hm_WcpcDriverApplication.getInstance().getUser();
                if("0".equals(XtomSharedPreferencesUtil.get(mContext, "loginflag"))){
                    ((MainActivity)mContext).showTextDialog("抱歉，您目前处于休车状态，无法抢单");
                    return;
                }else{
                    ((MainActivity)mContext).getNetWorker().grapTrips(user.getToken(), trip.getId());
                }
            }
        });

        if("0".equals(infor.getDriver_id())){
            holder.text_operate.setVisibility(View.VISIBLE);
            holder.image_tripget.setVisibility(View.GONE);
        }else{
            holder.text_operate.setVisibility(View.GONE);
            holder.image_tripget.setVisibility(View.VISIBLE);
        }

        view.setTag(R.id.button_0, infor);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TripListInfor infor = (TripListInfor) v.getTag(R.id.button_0);
                if("0".equals(infor.getDriver_id())){
                    Intent it = new Intent(mContext, GrapTripDetailInforActivity.class);
                    it.putExtra("id", infor.getId());
                    ((MainActivity)mContext).startActivityForResult(it, R.id.layout_0);
                }
            }
        });
    }

    private void findview(ViewHolder holder, View view){
        holder.text_time = (TextView) view.findViewById(R.id.textview);
        holder.text_takepersoncount = (TextView) view.findViewById(R.id.textview_0);
        holder.image_avatar = (RoundedImageView) view.findViewById(R.id.imageview);
        holder.text_realname = (TextView) view.findViewById(R.id.textview_1);
        holder.image_sex = (ImageView) view.findViewById(R.id.imageview_0);
        holder.text_takecount = (TextView) view.findViewById(R.id.textview_4);
        holder.text_startaddress = (TextView) view.findViewById(R.id.textview_2);
        holder.text_endaddress = (TextView) view.findViewById(R.id.textview_3);
        holder.text_money = (TextView) view.findViewById(R.id.textview_8);
        holder.text_operate = (TextView) view.findViewById(R.id.textview_9);
        holder.image_tripget = (ImageView) view.findViewById(R.id.imageview_3);
    }

    private static class ViewHolder{
        TextView text_time;
        TextView text_takepersoncount;
        RoundedImageView image_avatar;
        TextView text_realname;
        ImageView image_sex;
        TextView text_takecount;
        TextView text_startaddress;
        TextView text_endaddress;
        TextView text_money;
        TextView text_operate;
        ImageView image_tripget;
    }
}
