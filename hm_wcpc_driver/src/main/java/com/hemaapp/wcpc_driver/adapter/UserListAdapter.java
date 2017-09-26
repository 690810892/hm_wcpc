package com.hemaapp.wcpc_driver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaAdapter;
import com.hemaapp.hm_FrameWork.view.RoundedImageView;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.module.User;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import xtom.frame.image.load.XtomImageTask;
import xtom.frame.view.XtomListView;

/**
 * Created by WangYuxia on 2016/5/27.
 * 我的乘客的数据司陪器
 */
public class UserListAdapter extends HemaAdapter {

    private ArrayList<User> infors;
    private XtomListView mListView;

    public UserListAdapter(Context mContext, ArrayList<User> infors, XtomListView mListView) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_userlist, null);
            holder = new ViewHolder();
            holder.image_avatar = (RoundedImageView) convertView.findViewById(R.id.imageview);
            holder.text_realname = (TextView) convertView.findViewById(R.id.textview);
            holder.image_sex = (ImageView) convertView.findViewById(R.id.imageview_0);
            convertView.setTag(R.id.TAG, holder);
        }else{
            holder = (ViewHolder) convertView.getTag(R.id.TAG);
        }

        User user = infors.get(position);
        try {
            URL url = new URL(user.getAvatar());
            holder.image_avatar.setCornerRadius(90);
            XtomImageTask task = new XtomImageTask(holder.image_avatar, url, mContext);
            mListView.addTask(position, 0, task);
        } catch (MalformedURLException e) {
            holder.image_avatar.setImageResource(R.mipmap.default_user);
        }

        holder.text_realname.setText(user.getRealname());
        if("男".equals(user.getSex()))
            holder.image_sex.setImageResource(R.mipmap.img_sex_boy);
        else{
            holder.image_sex.setImageResource(R.mipmap.img_sex_girl);
        }
        return convertView;
    }

    private static class ViewHolder{
        RoundedImageView image_avatar;
        TextView text_realname;
        ImageView image_sex;
    }
}
