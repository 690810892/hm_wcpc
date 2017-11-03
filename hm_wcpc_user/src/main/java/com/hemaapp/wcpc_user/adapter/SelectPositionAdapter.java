package com.hemaapp.wcpc_user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.hemaapp.hm_FrameWork.HemaAdapter;
import com.hemaapp.wcpc_user.R;

import java.util.List;

/**
 */
public class SelectPositionAdapter extends HemaAdapter {

    private List<PoiItem> items;

    public SelectPositionAdapter(Context mContext, List<PoiItem> items) {
        super(mContext);
        this.items = items;
    }

    public void setItems(List<PoiItem> items) {
        this.items = items;
    }

    @Override
    public boolean isEmpty() {
        if(items == null || items.size() == 0)
            return true;
        return false;
    }

    @Override
    public int getCount() {
        return items == null || items.size() == 0 ? 1 : items.size();
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_position, null);
            holder = new ViewHolder();
            holder.text_content = (TextView) convertView.findViewById(R.id.textview);
            convertView.setTag(R.id.tag_0, holder);
        }else{
            holder = (ViewHolder) convertView.getTag(R.id.tag_0);
        }

        PoiItem item = items.get(position);
        String value = item.getTitle();
        String out = "provinceName = "+item.getProvinceName() +"\n cityName = "+item.getCityName() +"\n adName = "+item.getAdName()
                +"\n snippet = "+item.getSnippet() +"\n title = "+item.getTitle();
        log_i(out);
        holder.text_content.setText(value);

        return convertView;
    }

    private static class ViewHolder{
        TextView text_content;
    }
}
