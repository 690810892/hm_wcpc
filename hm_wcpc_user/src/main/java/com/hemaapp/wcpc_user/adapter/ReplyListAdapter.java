package com.hemaapp.wcpc_user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaAdapter;
import com.hemaapp.wcpc_user.BaseUtil;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.module.ReplyListInfor;

import java.util.ArrayList;

/**
 * Created by WangYuxia on 2016/5/13.
 */
public class ReplyListAdapter extends HemaAdapter {

    private ArrayList<ReplyListInfor> infors;

    public ReplyListAdapter(Context mContext, ArrayList<ReplyListInfor> infors) {
        super(mContext);
        this.infors = infors;
    }

    @Override
    public int getCount() {
        return infors == null || infors.size() == 0 ? 0 : infors.size();
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
        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_replylist, null);
            holder = new ViewHolder();
            findview(convertView, holder);
            convertView.setTag(R.id.button, holder);
        }else{
            holder = (ViewHolder) convertView.getTag(R.id.button);
        }
        setdata(holder, position);
        return convertView;
    }

    private void setdata(ViewHolder holder, int position){
        ReplyListInfor infor = infors.get(position);
        holder.text_time.setText(isNull(infor.getRegdate())?"时间错误":infor.getRegdate().substring(0, infor.getRegdate().length()-3));
        BaseUtil.transScoreByPoint(holder.image_star_0, holder.image_star_1, holder.image_star_2, holder.image_star_3, holder.image_star_4, infor.getPoint());
        holder.text_content.setText(infor.getContent());
        if(position == infors.size() - 1){
            holder.image_xian.setVisibility(View.INVISIBLE);
        }else {
            holder.image_xian.setVisibility(View.VISIBLE);
        }
    }

    private void findview(View view, ViewHolder holder){
        holder.text_time = (TextView) view.findViewById(R.id.textview_0);
        holder.image_star_0 = (ImageView) view.findViewById(R.id.imageview_1);
        holder.image_star_1 = (ImageView) view.findViewById(R.id.imageview_2);
        holder.image_star_2 = (ImageView) view.findViewById(R.id.imageview_3);
        holder.image_star_3 = (ImageView) view.findViewById(R.id.imageview_4);
        holder.image_star_4 = (ImageView) view.findViewById(R.id.imageview_5);
        holder.text_content = (TextView) view.findViewById(R.id.textview_1);
        holder.image_xian = (ImageView) view.findViewById(R.id.imageview);
    }

    private static class ViewHolder{
        TextView text_time;
        ImageView image_star_0, image_star_1, image_star_2, image_star_3, image_star_4;
        TextView text_content;
        ImageView image_xian;
    }
}
