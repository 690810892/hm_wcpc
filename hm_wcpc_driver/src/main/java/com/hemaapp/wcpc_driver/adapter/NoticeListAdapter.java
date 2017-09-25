package com.hemaapp.wcpc_driver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaAdapter;
import com.hemaapp.hm_FrameWork.dialog.HemaButtonDialog;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.activity.NoticeListActivity;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.NoticeListInfor;
import com.hemaapp.wcpc_driver.module.User;

import java.util.ArrayList;

/**
 * Created by WangYuxia on 2016/5/16.
 * 消息列表的数据适配器
 */
public class NoticeListAdapter extends HemaAdapter {

    private ArrayList<NoticeListInfor> infors;
    public NoticeListInfor deleteinfor;

    public NoticeListAdapter(Context mContext, ArrayList<NoticeListInfor> infors) {
        super(mContext);
        this.infors = infors;
    }

    @Override
    public boolean isEmpty() {
        if (infors == null || infors.size() == 0)
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
        if (isEmpty())
            return getEmptyView(parent);
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_notice_system, null);
            holder = new ViewHolder();
            holder.image_point = (ImageView) convertView.findViewById(R.id.title_point);
            holder.text_time = (TextView) convertView.findViewById(R.id.textview_0);
            holder.text_content = (TextView) convertView.findViewById(R.id.textview_1);
            convertView.setTag(R.id.TAG, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.id.TAG);
        }
        NoticeListInfor infor = infors.get(position);
        holder.text_time.setText(isNull(infor.getRegdate()) ? "时间错误" : infor.getRegdate().substring(0, infor.getRegdate().length() - 3));
        holder.text_content.setText(infor.getComtent());
        if ("1".equals(infor.getLooktype()))
            holder.image_point.setVisibility(View.VISIBLE);
        else
            holder.image_point.setVisibility(View.INVISIBLE);

        convertView.setTag(R.id.button, infor);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteinfor = (NoticeListInfor) v.getTag(R.id.button);
                if ("1".equals(deleteinfor.getLooktype())) {
                    User user = hm_WcpcDriverApplication.getInstance().getUser();
                    ((NoticeListActivity) mContext).getNetWorker().noticeSaveOperate(user.getToken(), deleteinfor.getId(), "1", "2", "1");
                }
            }
        });
        convertView.setTag(R.id.button_0, infor);
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteinfor = (NoticeListInfor) v.getTag(R.id.button_0);
                showDialog();
                return false;
            }
        });
        return convertView;
    }

    private HemaButtonDialog dialog;

    private void showDialog() {
        if (dialog == null) {
            dialog = new HemaButtonDialog(mContext);
            dialog.setLeftButtonText("取消");
            dialog.setRightButtonText("确定");
            dialog.setText("确定删除此记录?");
            dialog.setRightButtonTextColor(mContext.getResources()
                    .getColor(R.color.yellow));
        }
        dialog.setButtonListener(new ButtonListener());
        dialog.show();
    }

    private class ButtonListener implements HemaButtonDialog.OnButtonListener {

        @Override
        public void onLeftButtonClick(HemaButtonDialog dialog) {
            dialog.cancel();
        }

        @Override
        public void onRightButtonClick(HemaButtonDialog dialog) {
            dialog.cancel();
            User user = hm_WcpcDriverApplication.getInstance().getUser();
            ((NoticeListActivity) mContext).getNetWorker().noticeSaveOperate(user.getToken(), deleteinfor.getId(), "1", "2", "3");
        }
    }

    private static class ViewHolder {
        TextView text_time;
        ImageView image_point;
        TextView text_content;
    }
}

