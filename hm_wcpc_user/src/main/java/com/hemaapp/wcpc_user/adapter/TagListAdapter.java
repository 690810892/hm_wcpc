package com.hemaapp.wcpc_user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.activity.CancelOrderActivity;
import com.hemaapp.wcpc_user.activity.PingJiaActivity;
import com.hemaapp.wcpc_user.activity.TouSuActivity;
import com.hemaapp.wcpc_user.module.DataInfor;
import com.hemaapp.wcpc_user.view.FlowLayout.FlowLayout;
import com.hemaapp.wcpc_user.view.FlowLayout.TagAdapter;

import java.util.ArrayList;

/**
 * Created by wangyuxia on 2017/9/21.
 * 取消订单中，原因的数据适配器
 */
public class TagListAdapter extends TagAdapter<DataInfor> {

    private ArrayList<DataInfor> datas;
    private CancelOrderActivity mActivity;
    private PingJiaActivity mActivity1;
    private TouSuActivity mActivity2;
    public DataInfor attrItem;

    public TagListAdapter(ArrayList<DataInfor> datas, Context mActivity) {
        super(datas);
        this.datas = datas;
        if (mActivity instanceof CancelOrderActivity)
            this.mActivity = (CancelOrderActivity) mActivity;
        else if (mActivity instanceof TouSuActivity)
            this.mActivity2 = (TouSuActivity) mActivity;
        else
            this.mActivity1 = (PingJiaActivity) mActivity;

    }

    @Override
    public View getView(FlowLayout parent, int position, DataInfor attrChildItemsEntity) {
        LayoutInflater inflater;
        if (mActivity != null)
            inflater = LayoutInflater.from(mActivity);
        else if (mActivity1 != null)
            inflater = LayoutInflater.from(mActivity1);
        else
            inflater = LayoutInflater.from(mActivity2);
        TextView textView = (TextView) inflater.inflate(R.layout.listitem_attritem, parent, false);
        textView.setText(datas.get(position).getName());
        if (attrChildItemsEntity.isChecked()) {//已选中
            attrItem = attrChildItemsEntity;
            textView.setTextColor(0xfffc6b01);
            textView.setBackgroundResource(R.drawable.bg_cancelorder_s);
        } else {
            textView.setTextColor(0xff575b61);
            textView.setBackgroundResource(R.drawable.bg_cancelorder_n);
        }

        textView.setTag(attrChildItemsEntity);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//加个选择状态标记一下就好了
                attrItem = (DataInfor) v.getTag();
                if (attrItem.isChecked())
                    attrItem.setChecked(false);
                else
                    attrItem.setChecked(true);
                notifyDataChanged();//通知适配器变化
                if (mActivity != null)
                    mActivity.changeStatus();//检索价格
                else if (mActivity1 != null)
                    mActivity1.changeStatus();
                else
                    mActivity2.changeStatus();

            }
        });
        return textView;
    }
}

