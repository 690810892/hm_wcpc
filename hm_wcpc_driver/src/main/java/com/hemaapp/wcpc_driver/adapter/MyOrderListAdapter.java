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
import com.hemaapp.wcpc_driver.activity.MyOrderActivity;
import com.hemaapp.wcpc_driver.activity.OrderDetailInforActivity;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.OrderListInfor;
import com.hemaapp.wcpc_driver.module.User;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import xtom.frame.image.load.XtomImageTask;
import xtom.frame.view.XtomListView;

/**
 * Created by WangYuxia on 2016/5/27.
 */
public class MyOrderListAdapter extends HemaAdapter {

    private ArrayList<OrderListInfor> infors;
    private XtomListView mListView;
    private OrderListInfor order;

    public MyOrderListAdapter(Context mContext, ArrayList<OrderListInfor> infors, XtomListView mListView) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_myorder1, null);
            holder = new ViewHolder();
            findview(holder, convertView);
            convertView.setTag(R.id.TAG, holder);
        }else{
            holder = (ViewHolder) convertView.getTag(R.id.TAG);
        }
        OrderListInfor infor = infors.get(position);
        setData(holder, convertView, infor, position);
        return convertView;
    }

    private void setData(ViewHolder holder, View view, OrderListInfor infor, int position){
        holder.text_time.setText(BaseUtil.transTimeChat(infor.getBegintime()));
        holder.text_takepersoncount.setText("乘车人数"+infor.getNumbers());
        try {
            URL url = new URL(infor.getAvatar());
            holder.image_avatar.setCornerRadius(90);
            XtomImageTask task = new XtomImageTask(holder.image_avatar, url, mContext);
            mListView.addTask(position, 0, task);
        } catch (MalformedURLException e) {
            holder.image_avatar.setImageResource(R.mipmap.default_user);
        }

        if("0".equals(infor.getGrabflag()))
            holder.image_grabflag.setVisibility(View.INVISIBLE);
        else
            holder.image_grabflag.setVisibility(View.VISIBLE);

        holder.text_realname.setText(infor.getRealname());
        if("男".equals(infor.getSex()))
            holder.image_sex.setImageResource(R.mipmap.img_sex_boy);
        else
            holder.image_sex.setImageResource(R.mipmap.img_sex_girl);
        holder.text_takecount.setText("乘车次数 "+infor.getTakecount());
        holder.text_startaddress.setText(infor.getStartaddress());
        holder.text_endaddress.setText(infor.getEndaddress());
        if(infor.getIs_pool().equals("1"))
            holder.text_money.setText(infor.getSuccessfee());
        else
            holder.text_money.setText(infor.getFailfee());

        holder.text_operate.setVisibility(View.INVISIBLE);
        holder.text_operate.setTag(R.id.button, infor);
        holder.text_operate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order = (OrderListInfor) v.getTag(R.id.button);
                User user = hm_WcpcDriverApplication.getInstance().getUser();
                ((MyOrderActivity)mContext).getNetWorker().orderOperate(user.getToken(),"2", order.getId(), "", "");
            }
        });

        view.setTag(R.id.button_0, infor);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderListInfor infor = (OrderListInfor) v.getTag(R.id.button_0);
                Intent it = new Intent(mContext, OrderDetailInforActivity.class);
                it.putExtra("id", infor.getId());
                ((MyOrderActivity)mContext).startActivityForResult(it, R.id.layout);
            }
        });
    }

    private void findview(ViewHolder holder, View view){
        holder.text_time = (TextView) view.findViewById(R.id.textview);
        holder.text_takepersoncount = (TextView) view.findViewById(R.id.textview_0);
        holder.image_avatar = (RoundedImageView) view.findViewById(R.id.imageview);
        holder.image_grabflag = (ImageView) view.findViewById(R.id.imageview_2);
        holder.text_realname = (TextView) view.findViewById(R.id.textview_1);
        holder.image_sex = (ImageView) view.findViewById(R.id.imageview_0);
        holder.text_takecount = (TextView) view.findViewById(R.id.textview_4);
        holder.text_startaddress = (TextView) view.findViewById(R.id.textview_2);
        holder.text_endaddress = (TextView) view.findViewById(R.id.textview_3);
        holder.text_money = (TextView) view.findViewById(R.id.textview_8);
        holder.text_operate = (TextView) view.findViewById(R.id.textview_9);
    }

    private static class ViewHolder{
        TextView text_time;
        TextView text_takepersoncount;
        RoundedImageView image_avatar;
        ImageView image_grabflag;
        TextView text_realname;
        ImageView image_sex;
        TextView text_takecount;
        TextView text_startaddress;
        TextView text_endaddress;
        TextView text_money;
        TextView text_operate;
    }
}
