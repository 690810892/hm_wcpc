package com.hemaapp.wcpc_driver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaAdapter;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.module.TypeInfor;

import java.util.ArrayList;

/**
 * Created by wangyuxia on 2017/10/12.
 */

public class TypePopAdapter extends HemaAdapter {

    private ArrayList<TypeInfor> types;
    private int index;

    public TypePopAdapter(Context mContext, ArrayList<TypeInfor> types, int index) {
        super(mContext);
        if(types==null)
            this.types=new ArrayList<>();
        else
            this.types = types;
        this.index = index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setTypes(ArrayList<TypeInfor> types, int index) {
        if (types == null)
            this.types = new ArrayList<>();
        else
            this.types = types;
        this.index = index;

    }

    @Override
    public int getCount() {
        return types.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.listitem_type, null);
            holder = new ViewHolder();
            holder.text_type = (TextView) view.findViewById(R.id.textview);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        TypeInfor type = types.get(i);
        holder.text_type.setText(type.getName());
        if(i == index)
            holder.text_type.setTextColor(0xff212121);
        else
            holder.text_type.setTextColor(0xff3f3f3f);
        return view;
    }

    private static class ViewHolder{
        TextView text_type;
    }
}
