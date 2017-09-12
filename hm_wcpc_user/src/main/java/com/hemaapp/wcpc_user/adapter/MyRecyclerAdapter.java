package com.hemaapp.wcpc_user.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.activity.FeeRuleActivity;
import com.hemaapp.wcpc_user.module.FeeRuleListInfor;

import java.util.ArrayList;

/**
 * Created by WangYuxia on 2016/5/9.
 */
public class MyRecyclerAdapter extends
        RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {

    private ArrayList<FeeRuleListInfor> types;
    private FeeRuleActivity mContext;

    public MyRecyclerAdapter(FeeRuleActivity mContext,
                             ArrayList<FeeRuleListInfor> types) {
        this.types = types;
        this.mContext = mContext;
    }

    @Override
    public int getItemCount() {
        return types == null || types.size() == 0 ? 0 : types.size();
    }

    // 赋值
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.textView.setText(types.get(position).getNumbers());
        if (types.get(position).isChecked()) {
            holder.textView.setTextColor(mContext.getResources().getColor(R.color.yellow));
            holder.textView.setBackgroundResource(R.drawable.bg_selectcount);
        } else {
            holder.textView.setTextColor(mContext.getResources().getColor(R.color.qianhui));
            holder.textView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        }

        holder.textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mContext.operate(position);
            }
        });
    }

    // 创建
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
        View view ;
        view = LayoutInflater.from(mContext).inflate(
                R.layout.item_personcount, null);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textview);
        }
    }
}
