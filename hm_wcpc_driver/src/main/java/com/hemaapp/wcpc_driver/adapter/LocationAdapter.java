package com.hemaapp.wcpc_driver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaAdapter;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.activity.SelectCityActivity;
import com.hemaapp.wcpc_driver.module.DistrictInfor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by WangYuxia on 2016/6/24.
 */
public class LocationAdapter extends HemaAdapter {

    private SelectCityActivity mActivity;
    private List<DistrictInfor> list;
    private HashMap<String, Integer> alphaIndexer;

    public LocationAdapter(Context mContext) {
        super(mContext);
        mActivity = (SelectCityActivity) mContext;
    }

    public void setList(List<DistrictInfor> list) {
        if (list == null)
            this.list = new ArrayList<>();
        else {
            this.list = list;
        }

        alphaIndexer = new HashMap<>();

        for (int i = 0; i < list.size(); i++) {
            // 当前汉语拼音首字母
            // getAlpha(list.get(i));
            String currentStr = list.get(i).getCharindex();
            // 上一个汉语拼音首字母，如果不存在为“ ”
            String previewStr = (i - 1) >= 0 ? list.get(i - 1).getCharindex()
                    : " ";
            if (!previewStr.equals(currentStr)) {
                String name = list.get(i).getCharindex();
                alphaIndexer.put(name, i);
            }
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_city, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
            convertView.setTag(R.id.TAG, holder);
        }else {
            holder = (ViewHolder) convertView.getTag(R.id.TAG);
        }

        holder.name.setText(list.get(position).getName());
        String currentStr = list.get(position).getCharindex();
        String previewStr = (position - 1) >= 0 ? list.get(
                position - 1).getCharindex() : " ";
        if (!previewStr.equals(currentStr)) {
            holder.alpha.setVisibility(View.VISIBLE);
            holder.alpha.setText(currentStr);
        } else {
            holder.alpha.setVisibility(View.GONE);
        }

        holder.name.setTag(R.id.button, list.get(position));
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DistrictInfor infor = (DistrictInfor) v.getTag(R.id.button);
                mActivity.onItemClick(infor);
            }
        });

        return convertView;
    }

    public HashMap<String, Integer> getAlphaIndexer() {
        return alphaIndexer;
    }

    private static class ViewHolder {
        TextView alpha;
        TextView name;
    }
}
