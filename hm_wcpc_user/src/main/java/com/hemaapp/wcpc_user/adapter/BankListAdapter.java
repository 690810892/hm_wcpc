package com.hemaapp.wcpc_user.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaAdapter;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.activity.SelectBankListActivity;
import com.hemaapp.wcpc_user.module.Bank;

import java.util.ArrayList;

/**
 * Created by WangYuxia on 2016/5/18.
 * 银行列表的数据适配
 */
public class BankListAdapter extends HemaAdapter {

    private ArrayList<Bank> infors;
    private SelectBankListActivity mActivity;

    public BankListAdapter(Context mContext, ArrayList<Bank> infors) {
        super(mContext);
        this.infors = infors;
        mActivity = (SelectBankListActivity) mContext;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_bank, null);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.textview);
            convertView.setTag(R.id.TAG, holder);
        }else{
            holder = (ViewHolder) convertView.getTag(R.id.TAG);
        }
        Bank bank = infors.get(position);
        holder.textView.setText(bank.getName());
        convertView.setTag(R.id.button_0, bank);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bank infor = (Bank) v.getTag(R.id.button_0);
                Intent it = mActivity.getIntent();
                it.putExtra("name", infor.getName());
                mActivity.setResult(mActivity.RESULT_OK, it);
                mActivity.finish();
            }
        });
        return convertView;
    }

    private static class ViewHolder{
        TextView textView;
    }
}
