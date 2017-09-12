package com.hemaapp.wcpc_driver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaAdapter;
import com.hemaapp.wcpc_driver.BaseUtil;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.activity.MyTripsActivity;
import com.hemaapp.wcpc_driver.module.MyTripsInfor;

import java.util.ArrayList;

/**
 * Created by WangYuxia on 2016/5/27.
 */
public class MyTripsListAdapter extends HemaAdapter {

    private ArrayList<MyTripsInfor> infors;
    public MyTripsInfor deleteinfor;

    public MyTripsListAdapter(Context mContext, ArrayList<MyTripsInfor> infors) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_mytripslist, null);
            holder = new ViewHolder();
            findview(holder, convertView);
            convertView.setTag(R.id.TAG, holder);
        }else{
            holder = (ViewHolder) convertView.getTag(R.id.TAG);
        }

        MyTripsInfor infor = infors.get(position);
        holder.text_time.setText(BaseUtil.transTimeChat(infor.getBegintime()));
        holder.text_startaddress.setText(infor.getStartaddress());
        holder.text_endaddress.setText(infor.getEndaddress());

        if("1".equals(infor.getStatus())){
            holder.text_status.setText("已完成");
            holder.text_status.setTextColor(mContext.getResources().getColor(R.color.status_complete));
            holder.text_operate.setText("删除行程");
        }else{
            holder.text_status.setText("未出行");
            holder.text_status.setTextColor(mContext.getResources().getColor(R.color.status_togo));
            holder.text_operate.setText("取消行程");
        }
        holder.text_money.setText(infor.getSuccessfee());

        holder.text_operate.setTag(R.id.button, infor);
        holder.text_operate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteinfor = (MyTripsInfor) v.getTag(R.id.button);
                String content = ((TextView) v).getText().toString();
                if("删除行程".equals(content))
                    ((MyTripsActivity)mContext).delete(0);
                else
                    ((MyTripsActivity)mContext).delete(2);

            }
        });
        return convertView;
    }

    private void findview(ViewHolder holder, View view){
        holder.text_time = (TextView) view.findViewById(R.id.textview_0);
        holder.text_status = (TextView) view.findViewById(R.id.textview_1);
        holder.text_startaddress = (TextView) view.findViewById(R.id.textview_2);
        holder.text_endaddress = (TextView) view.findViewById(R.id.textview_3);
        holder.text_money = (TextView) view.findViewById(R.id.textview_6);
        holder.text_operate = (TextView) view.findViewById(R.id.textview_4);
    }

    private static class ViewHolder{
        TextView text_time;
        TextView text_status;
        TextView text_startaddress;
        TextView text_endaddress;
        TextView text_money;
        TextView text_operate;
    }
}
