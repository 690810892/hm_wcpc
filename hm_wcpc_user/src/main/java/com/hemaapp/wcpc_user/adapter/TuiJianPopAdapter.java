package com.hemaapp.wcpc_user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.module.PopItem;

import java.util.ArrayList;

import xtom.frame.XtomAdapter;

/**
 * Created by WangYuxia on 2016/5/12.
 */
public class TuiJianPopAdapter extends XtomAdapter {

    private Context mContext;
    private ArrayList<PopItem> fenleis;
    private int sel_index;

    public TuiJianPopAdapter(Context mContext, ArrayList<PopItem> fenleis, int sel) {
        super(mContext);
        this.mContext = mContext;
        if(fenleis==null)
            this.fenleis=new ArrayList<PopItem>();
        else
            this.fenleis=fenleis;

        sel_index = sel;
    }

    public void setfenleis(ArrayList<PopItem> fenleis, int sel) {
        if (fenleis == null)
            this.fenleis = new ArrayList<PopItem>();
        else
            this.fenleis = fenleis;
        sel_index = sel;
    }

    public void setfenleis_sel(int sel) {
        sel_index = sel;
    }

    @Override
    public int getCount() {
        return fenleis.size();
    }

    @Override
    public Object getItem(int position) {
        return fenleis.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.listitem_fenleipop, null);
            holder = new ViewHolder();
            holder.textview = (TextView) convertView
                    .findViewById(R.id.textview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textview.setText(fenleis.get(position).name);
        if(sel_index == position){
            holder.textview.setTextColor(mContext.getResources().getColor(R.color.yellow));
        }else{
            holder.textview.setTextColor(mContext.getResources().getColor(R.color.shenhui));
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView textview;
    }
}

