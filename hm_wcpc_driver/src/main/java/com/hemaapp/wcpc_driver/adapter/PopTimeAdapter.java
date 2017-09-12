package com.hemaapp.wcpc_driver.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;


import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.view.wheelview.adapter.AbstractWheelTextAdapter;

import java.util.ArrayList;

/**
 * Created by WangYuxia on 2016/5/11.
 */
public class PopTimeAdapter extends AbstractWheelTextAdapter {

    private ArrayList<String> times;

    public PopTimeAdapter(Context context, ArrayList<String> times) {
        super(context, R.layout.tempitem, NO_RESOURCE);
        this.times = times;
        setItemTextResource(R.id.tempValue);
    }

    @Override
    public View getItem(int index, View convertView, ViewGroup parent) {
        return super.getItem(index, convertView, parent);
    }

    @Override
    public int getItemsCount() {
        return times.size();
    }

    @Override
    protected CharSequence getItemText(int index) {
        return times.get(index);
    }

}
