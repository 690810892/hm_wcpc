package com.hemaapp.wcpc_driver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaAdapter;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.module.FeeAccountInfor;

import java.util.ArrayList;

/**
 * Created by WangYuxia on 2016/5/17.
 * 余额明细的数据适配器
 */
public class FeeAccountListAdapter extends HemaAdapter {

    private ArrayList<FeeAccountInfor> infors;

    public FeeAccountListAdapter(Context mContext, ArrayList<FeeAccountInfor> infors) {
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
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listtiem_feeaccount, null);
            holder = new ViewHolder();
            holder.text_paykind = (TextView)convertView.findViewById(R.id.textview_0);
            holder.text_time = (TextView) convertView.findViewById(R.id.textview_1);
            holder.text_money = (TextView) convertView.findViewById(R.id.textview_2);
            convertView.setTag(R.id.button_0, holder);
        }else{
            holder = (ViewHolder) convertView.getTag(R.id.button_0);
        }

        FeeAccountInfor infor = infors.get(position);
        if("1".equals(infor.getKeytype())){
            holder.text_paykind.setText(infor.getName());
            holder.text_money.setTextColor(0xffff8400);
            holder.text_money.setText(infor.getAmount());
        }else if("2".equals(infor.getKeytype())){
            holder.text_money.setTextColor(0xff35b87f);
            holder.text_money.setText(infor.getAmount());
        }else if("3".equals(infor.getKeytype())){
            holder.text_money.setTextColor(0xff35b87f);
            holder.text_money.setText(infor.getAmount());
        }else if("4".equals(infor.getKeytype())){
            holder.text_money.setTextColor(0xffff8400);
            holder.text_money.setText(infor.getAmount());
        }
        holder.text_paykind.setText(infor.getName());
        holder.text_time.setText(isNull(infor.getRegdate())?"时间错误":infor.getRegdate().substring(0, infor.getRegdate().length()-3));
        return convertView;
    }

    private static class ViewHolder{
        TextView text_time;
        TextView text_paykind;
        TextView text_money;
    }
}
