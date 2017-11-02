package com.hemaapp.wcpc_driver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.activity.PingJiaActivity;
import com.hemaapp.wcpc_driver.activity.TouSuActivity;
import com.hemaapp.wcpc_driver.module.DataInfor;
import com.hemaapp.wcpc_driver.view.FlowLayout.FlowLayout;
import com.hemaapp.wcpc_driver.view.FlowLayout.TagAdapter;

import java.util.ArrayList;

/**
 */
public class TagReplyAdapter extends TagAdapter<DataInfor> {

    private ArrayList<DataInfor> datas;
    public DataInfor attrItem;
    private Context mContext;

    public TagReplyAdapter(Context mContext,ArrayList<DataInfor> datas) {
        super(datas);
        this.datas = datas;
        this.mContext = mContext;
    }

    @Override
    public View getView(FlowLayout parent, int position, DataInfor attrChildItemsEntity) {
        LayoutInflater inflater;
            inflater = LayoutInflater.from(mContext);
        TextView textView = (TextView) inflater.inflate(R.layout.listitem_replytag, parent, false);
        textView.setText(datas.get(position).getName());
        textView.setTag(attrChildItemsEntity);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//加个选择状态标记一下就好了
            }
        });
        return textView;
    }
}

