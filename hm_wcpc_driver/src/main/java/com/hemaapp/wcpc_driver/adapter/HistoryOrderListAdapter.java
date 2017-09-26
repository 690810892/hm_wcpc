package com.hemaapp.wcpc_driver.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaAdapter;
import com.hemaapp.hm_FrameWork.view.RoundedImageView;
import com.hemaapp.wcpc_driver.BaseActivity;
import com.hemaapp.wcpc_driver.BaseNetWorker;
import com.hemaapp.wcpc_driver.BaseUtil;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.activity.CenterOrderDetailInforActivity;
import com.hemaapp.wcpc_driver.activity.HistoryOrderActivity;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.OrderListInfor;
import com.hemaapp.wcpc_driver.module.User;
import com.hemaapp.wcpc_driver.view.ButtonDialog;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import xtom.frame.image.load.XtomImageTask;
import xtom.frame.view.XtomListView;

/**
 * Created by WangYuxia on 2016/5/27.
 * 历史订单的数据适配器
 */
public class HistoryOrderListAdapter extends HemaAdapter {

    public OrderListInfor order;
    private ArrayList<OrderListInfor> infors;
    private BaseNetWorker netWorker;

    public HistoryOrderListAdapter(Context mContext, ArrayList<OrderListInfor> infors, BaseNetWorker netWorker) {
        super(mContext);
        this.infors = infors;
        this.netWorker = netWorker;
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
            return  getEmptyView(parent);
        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_order, null);
            holder = new ViewHolder();
            findview(holder, convertView);
            convertView.setTag(R.id.TAG, holder);
        }else{
            holder = (ViewHolder) convertView.getTag(R.id.TAG);
        }

        OrderListInfor infor = infors.get(position);
        setData(holder, infor, convertView);
        return convertView;
    }

    private void setData(ViewHolder holder, OrderListInfor infor, View view){
        holder.text_time.setText(BaseUtil.transTimeChat(infor.getBegintime()));
        if("1".equals(infor.getPayflag())){ //已支付
            holder.text_status.setText("待评价");
            holder.text_status.setTextColor(mContext.getResources().getColor(R.color.shenhui));
            holder.text_operate.setText("删除订单");
        }else if("2".equals(infor.getPayflag())){ //已评价
            holder.text_status.setText("已完成");
            holder.text_status.setTextColor(mContext.getResources().getColor(R.color.status_complete));
            holder.text_operate.setText("查看评价");
        }else if("3".equals(infor.getPayflag())){ //已取消
            holder.text_status.setText("已取消");
            holder.text_status.setTextColor(mContext.getResources().getColor(R.color.shenhui));
            holder.text_operate.setText("删除订单");
        }

        try {
            URL url = new URL(infor.getAvatar());
            holder.image_avatar.setCornerRadius(90);
            ((BaseActivity)mContext).imageWorker.loadImage(new XtomImageTask(holder.image_avatar, url, mContext));
        } catch (MalformedURLException e) {
            holder.image_avatar.setImageResource(R.mipmap.default_user);
        }
        holder.text_realname.setText(infor.getRealname());
        if("男".equals(infor.getSex()))
            holder.image_sex.setImageResource(R.mipmap.img_sex_boy);
        else
            holder.image_sex.setImageResource(R.mipmap.img_sex_girl);

        holder.text_startaddress.setText(infor.getStartaddress());
        holder.text_endaddress.setText(infor.getEndaddress());
        holder.text_takecount.setText("乘车次数 "+(isNull(infor.getTakecount())? "0":infor.getTakecount()));
        holder.text_numbers.setText("乘车人数 "+(isNull(infor.getNumbers())? "0": infor.getNumbers()));
        if(infor.getIs_pool().equals("1"))
            holder.text_money.setText(infor.getSuccessfee());
        else
            holder.text_money.setText(infor.getFailfee());

        view.setTag(R.id.button_0, infor);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderListInfor infor = (OrderListInfor) v.getTag(R.id.button_0);
                Intent it = new Intent(mContext, CenterOrderDetailInforActivity.class);
                it.putExtra("id", infor.getId());
                mContext.startActivity(it);
            }
        });

        holder.text_operate.setTag(R.id.tag_0, infor);
        holder.text_operate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = ((TextView)v).getText().toString();
                order = (OrderListInfor) v.getTag(R.id.tag_0);
                if("删除订单".equals(content))
                    delete();
                else{
                    Intent it = new Intent(mContext, CenterOrderDetailInforActivity.class);
                    it.putExtra("id", order.getId());
                    mContext.startActivity(it);
                }
            }
        });
    }

    private ButtonDialog mDialog;

    private void delete(){
        if (mDialog == null) {
            mDialog = new ButtonDialog(mContext);
            mDialog.setLeftButtonText("取消");
            mDialog.setRightButtonText("确定");
            mDialog.setText("您确定清空订单?一旦清空无法找回");
            mDialog.setButtonListener(new ButtonListener());
            mDialog.setRightButtonTextColor(mContext.getResources().getColor(R.color.yellow));
        }
        mDialog.show();
    }

    private class ButtonListener implements ButtonDialog.OnButtonListener {

        @Override
        public void onLeftButtonClick(ButtonDialog dialog) {
            dialog.cancel();
        }

        @Override
        public void onRightButtonClick(ButtonDialog dialog) {
            dialog.cancel();
            User user = hm_WcpcDriverApplication.getInstance().getUser();
            netWorker.orderOperate(user.getToken(), "1", order.getId(), "", "");
        }
    }

    private void findview(ViewHolder holder, View view){
        holder.text_time = (TextView) view.findViewById(R.id.textview);
        holder.text_status = (TextView) view.findViewById(R.id.textview_0);
        holder.image_avatar = (RoundedImageView) view.findViewById(R.id.imageview);
        holder.text_realname = (TextView) view.findViewById(R.id.textview_1);
        holder.image_sex = (ImageView) view.findViewById(R.id.imageview_0);
        holder.text_startaddress = (TextView) view.findViewById(R.id.textview_2);
        holder.text_endaddress = (TextView) view.findViewById(R.id.textview_3);
        holder.text_takecount = (TextView) view.findViewById(R.id.textview_4);
        holder.text_numbers = (TextView) view.findViewById(R.id.textview_5);
        holder.text_money = (TextView) view.findViewById(R.id.textview_8);
        holder.text_operate = (TextView) view.findViewById(R.id.textview_9);
    }

    private static class ViewHolder{
        TextView text_time;
        TextView text_status;
        RoundedImageView image_avatar;
        TextView text_realname;
        ImageView image_sex;
        TextView text_startaddress;
        TextView text_endaddress;
        TextView text_takecount;
        TextView text_numbers;
        TextView text_money;
        TextView text_operate;
    }
}
