package com.hemaapp.wcpc_user.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaAdapter;
import com.hemaapp.hm_FrameWork.dialog.HemaButtonDialog;
import com.hemaapp.hm_FrameWork.view.RoundedImageView;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseNetWorker;
import com.hemaapp.wcpc_user.BaseUtil;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.activity.CancelOrderActivity;
import com.hemaapp.wcpc_user.activity.OrderDetialInforActivity;
import com.hemaapp.wcpc_user.activity.OrderListActivity;
import com.hemaapp.wcpc_user.activity.PingJiaActivity;
import com.hemaapp.wcpc_user.activity.ToPayActivity;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.OrderListInfor;
import com.hemaapp.wcpc_user.module.User;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import xtom.frame.image.load.XtomImageTask;

/**
 * Created by WangYuxia on 2016/5/19.
 * 订单列表的数据适配器
 */
public class MyOrderListAdapter extends HemaAdapter {

    public OrderListInfor order;
    private ArrayList<OrderListInfor> infors;
    private BaseNetWorker netWorker;

    public MyOrderListAdapter(Context mContext, ArrayList<OrderListInfor> infors, BaseNetWorker netWorker) {
        super(mContext);
        this.infors = infors;
        this.netWorker = netWorker;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_order, null);
            holder = new ViewHolder();
            findview(holder, convertView);
            convertView.setTag(R.id.TAG, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.id.TAG);
        }

        OrderListInfor infor = infors.get(position);
        setData(holder, infor, convertView);
        return convertView;
    }

    private void setData(ViewHolder holder, OrderListInfor infor, View view) {
        holder.text_time.setText(BaseUtil.transTimeChat(infor.getBegintime()));
        if ("0".equals(infor.getPayflag())) { //未支付
            if ("0".equals(infor.getReachflag())) {
                holder.text_status.setText("未出行");
                holder.text_status.setTextColor(mContext.getResources().getColor(R.color.status_togo));
                holder.text_operate.setText("取消订单");
            } else if ("1".equals(infor.getReachflag())) {
                holder.text_status.setText("待支付");
                holder.text_status.setTextColor(mContext.getResources().getColor(R.color.status_togo));
                holder.text_operate.setText("去支付");
            }
        } else if ("1".equals(infor.getPayflag())) { //已支付
            holder.text_status.setText("待评价");
            holder.text_status.setTextColor(mContext.getResources().getColor(R.color.shenhui));
            holder.text_operate.setText("去评价");
        } else if ("2".equals(infor.getPayflag())) { //已评价
            holder.text_status.setText("已完成");
            holder.text_status.setTextColor(mContext.getResources().getColor(R.color.status_complete));
            holder.text_operate.setText("删除订单");
        } else if ("3".equals(infor.getPayflag())) { //已取消
            holder.text_status.setText("已取消");
            holder.text_status.setTextColor(mContext.getResources().getColor(R.color.shenhui));
            holder.text_operate.setText("删除订单");
        }

        try {
            URL url = new URL(infor.getAvatar());
            holder.image_avatar.setCornerRadius(90);
            ((BaseActivity) mContext).imageWorker.loadImage(new XtomImageTask(holder.image_avatar, url, mContext));
        } catch (MalformedURLException e) {
            holder.image_avatar.setImageResource(R.mipmap.default_driver);
        }
        holder.text_realname.setText(infor.getRealname());
        if ("男".equals(infor.getSex()))
            holder.image_sex.setImageResource(R.mipmap.img_sex_boy);
        else
            holder.image_sex.setImageResource(R.mipmap.img_sex_girl);

        holder.text_startaddress.setText(infor.getStartaddress());
        holder.text_endaddress.setText(infor.getEndaddress());
        holder.text_carbrand.setText(infor.getCarbrand());
        holder.text_carnumbers.setText(infor.getCarnumber());
        if (infor.getIs_pool().equals("1"))
            holder.text_money.setText(infor.getSuccessfee());
        else
            holder.text_money.setText(infor.getFailfee());

        holder.text_remaincount.setVisibility(View.INVISIBLE);

//        holder.image_avatar.setTag(R.id.button, infor);
//        holder.image_avatar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                OrderListInfor infor = (OrderListInfor) v.getTag(R.id.button);
//                Intent it = new Intent(mContext, CarOwerHomePageActivity.class);
//                it.putExtra("id", infor.getDriver_id());
//                mContext.startActivity(it);
//            }
//        });

        view.setTag(R.id.button_0, infor);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderListInfor infor = (OrderListInfor) v.getTag(R.id.button_0);
                Intent it = new Intent(mContext, OrderDetialInforActivity.class);
                it.putExtra("id", infor.getId());
                mContext.startActivity(it);
            }
        });

        holder.text_operate.setTag(R.id.tag_0, infor);
        holder.text_operate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order = (OrderListInfor) v.getTag(R.id.tag_0);
                String value = ((TextView) v).getText().toString();
                Intent it;
                if ("去支付".equals(value)) {
                    it = new Intent(mContext, ToPayActivity.class);
                    it.putExtra("id", order.getId());
                    if ("1".equals(order.getIs_pool()))
                        it.putExtra("total_fee", order.getSuccessfee());
                    else
                        it.putExtra("total_fee", order.getFailfee());
                    ((OrderListActivity) mContext).startActivityForResult(it, R.id.layout);
                } else if ("去评价".equals(value)) {
                    it = new Intent(mContext, PingJiaActivity.class);
                    it.putExtra("id", order.getId());
                    it.putExtra("driver_id", order.getDriver_id());
                    ((OrderListActivity) mContext).startActivityForResult(it, R.id.layout_0);
                } else if ("取消订单".equals(value)) {
                    it = new Intent(mContext, CancelOrderActivity.class);
                    it.putExtra("id", order.getId());
                    ((OrderListActivity) mContext).startActivityForResult(it, R.id.layout_1);
                } else if ("删除订单".equals(value)) {
                    delete();
                }
            }
        });
    }

    private HemaButtonDialog mDialog;

    public void delete(){
        if (mDialog == null) {
            mDialog = new HemaButtonDialog(mContext);
            mDialog.setLeftButtonText("取消");
            mDialog.setRightButtonText("确定");
            mDialog.setText("确定要清空所有订单?");
            mDialog.setButtonListener(new ButtonListener());
            mDialog.setRightButtonTextColor(mContext.getResources().getColor(R.color.yellow));
        }
        mDialog.show();
    }

    private class ButtonListener implements HemaButtonDialog.OnButtonListener {

        @Override
        public void onLeftButtonClick(HemaButtonDialog dialog) {
            dialog.cancel();
        }

        @Override
        public void onRightButtonClick(HemaButtonDialog dialog) {
            dialog.cancel();
            User user = hm_WcpcUserApplication.getInstance().getUser();
            //netWorker.orderOperate(user.getToken(), "6", order.getId(), "", "");
        }
    }

    private void findview(ViewHolder holder, View view) {
        holder.text_time = (TextView) view.findViewById(R.id.textview);
        holder.text_status = (TextView) view.findViewById(R.id.textview_0);
        holder.image_avatar = (RoundedImageView) view.findViewById(R.id.imageview);
        holder.text_realname = (TextView) view.findViewById(R.id.textview_1);
        holder.image_sex = (ImageView) view.findViewById(R.id.imageview_0);
        holder.text_startaddress = (TextView) view.findViewById(R.id.textview_2);
        holder.text_endaddress = (TextView) view.findViewById(R.id.textview_3);
        holder.text_carbrand = (TextView) view.findViewById(R.id.textview_4);
        holder.text_carnumbers = (TextView) view.findViewById(R.id.textview_5);
        holder.text_money = (TextView) view.findViewById(R.id.textview_8);
        holder.text_operate = (TextView) view.findViewById(R.id.textview_9);
        holder.text_remaincount = (TextView) view.findViewById(R.id.tv_remain_count);
    }

    private static class ViewHolder {
        TextView text_time;
        TextView text_status;
        RoundedImageView image_avatar;
        TextView text_realname;
        ImageView image_sex;
        TextView text_startaddress;
        TextView text_endaddress;
        TextView text_carbrand;
        TextView text_carnumbers;
        TextView text_remaincount;
        TextView text_money;
        TextView text_operate;
    }
}
