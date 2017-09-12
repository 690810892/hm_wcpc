package com.hemaapp.wcpc_driver.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaAdapter;
import com.hemaapp.hm_FrameWork.view.RoundedImageView;
import com.hemaapp.wcpc_driver.BaseUtil;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.activity.MainActivity;
import com.hemaapp.wcpc_driver.activity.OrderDetailInforActivity;
import com.hemaapp.wcpc_driver.module.OrderListInfor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import xtom.frame.image.load.XtomImageTask;
import xtom.frame.view.XtomListView;

/**
 * Created by WangYuxia on 2016/5/24.
 */
public class OrderListAdapter extends HemaAdapter {

    private ArrayList<OrderListInfor> infors;
    private XtomListView mListView;
    private OrderListInfor order;

    public OrderListAdapter(Context mContext, ArrayList<OrderListInfor> infors, XtomListView mListView) {
        super(mContext);
        this.infors = infors;
        this.mListView = mListView;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_myorder, null);
            holder = new ViewHolder();
            findview(holder, convertView);
            convertView.setTag(R.id.TAG, holder);
        }else{
            holder = (ViewHolder) convertView.getTag(R.id.TAG);
        }
        OrderListInfor infor = infors.get(position);
        setData(holder, convertView, infor, position);
        return convertView;
    }

    private void setData(ViewHolder holder, View view, OrderListInfor infor, int position){
        holder.text_time.setText(BaseUtil.transTimeChat(infor.getBegintime()));
        holder.text_takepersoncount.setText("乘车人数"+infor.getNumbers()+"人");
        try {
            URL url = new URL(infor.getAvatar());
            holder.image_avatar.setCornerRadius(90);
            XtomImageTask task = new XtomImageTask(holder.image_avatar, url, mContext);
            mListView.addTask(position, 0, task);
        } catch (MalformedURLException e) {
            holder.image_avatar.setImageResource(R.mipmap.default_user);
        }
        holder.text_realname.setText(infor.getRealname());
        if("男".equals(infor.getSex()))
            holder.image_sex.setImageResource(R.mipmap.img_sex_boy);
        else
            holder.image_sex.setImageResource(R.mipmap.img_sex_girl);
        holder.text_takecount.setText("乘车次数 "+infor.getTakecount()+"次");
        holder.text_startaddress.setText(infor.getStartaddress());
        holder.text_endaddress.setText(infor.getEndaddress());
        if(infor.getIs_pool().equals("1"))
            holder.text_money.setText(infor.getSuccessfee());
        else
            holder.text_money.setText(infor.getFailfee());
        holder.text_operate.setTag(R.id.button, infor);
        holder.text_operate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order = (OrderListInfor) v.getTag(R.id.button);
                showDialog();
            }
        });

        view.setTag(R.id.button_0, infor);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderListInfor infor = (OrderListInfor) v.getTag(R.id.button_0);
                Intent it = new Intent(mContext, OrderDetailInforActivity.class);
                it.putExtra("id", infor.getId());
                ((MainActivity)mContext).startActivityForResult(it, R.id.layout);
            }
        });
    }

    private PopupWindow mWindow;
    private ViewGroup mViewGroup;
    private TextView content1;
    private TextView content2;
    private TextView ok;
    private TextView cancel;

    private void showDialog(){
        if (mWindow != null) {
            mWindow.dismiss();
        }
        mWindow = new PopupWindow(mContext);
        mWindow.setWidth(FrameLayout.LayoutParams.MATCH_PARENT);
        mWindow.setHeight(FrameLayout.LayoutParams.MATCH_PARENT);
        mWindow.setBackgroundDrawable(new BitmapDrawable());
        mWindow.setFocusable(true);
        mWindow.setAnimationStyle(R.style.PopupAnimation);
        mViewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(
                R.layout.pop_phone, null);
        content1 = (TextView) mViewGroup.findViewById(R.id.textview);
        content2 = (TextView) mViewGroup.findViewById(R.id.textview_0);
        cancel = (TextView) mViewGroup.findViewById(R.id.textview_1);
        ok = (TextView) mViewGroup.findViewById(R.id.textview_2);
        mWindow.setContentView(mViewGroup);
        mWindow.showAtLocation(mViewGroup, Gravity.CENTER, 0, 0);
        content1.setText("拨打乘客电话");
        content2.setText(order.getMobile());
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
                String phone = order.getMobile();
                //Intent.ACTION_CALL 直接拨打电话，就是进入拨打电话界面，电话已经被拨打出去了。
                //Intent.ACTION_DIAL 是进入拨打电话界面，电话号码已经输入了，但是需要人为的按拨打电话键，才能播出电话。
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                        + phone));
                mContext.startActivity(intent);
            }
        });
    }

    private void findview(ViewHolder holder, View view){
        holder.text_time = (TextView) view.findViewById(R.id.textview);
        holder.text_takepersoncount = (TextView) view.findViewById(R.id.textview_0);
        holder.image_avatar = (RoundedImageView) view.findViewById(R.id.imageview);
        holder.text_realname = (TextView) view.findViewById(R.id.textview_1);
        holder.image_sex = (ImageView) view.findViewById(R.id.imageview_0);
        holder.text_takecount = (TextView) view.findViewById(R.id.textview_4);
        holder.text_startaddress = (TextView) view.findViewById(R.id.textview_2);
        holder.text_endaddress = (TextView) view.findViewById(R.id.textview_3);
        holder.text_money = (TextView) view.findViewById(R.id.textview_8);
        holder.text_operate = (TextView) view.findViewById(R.id.textview_9);
    }

    private static class ViewHolder{
        TextView text_time;
        TextView text_takepersoncount;
        RoundedImageView image_avatar;
        TextView text_realname;
        ImageView image_sex;
        TextView text_takecount;
        TextView text_startaddress;
        TextView text_endaddress;
        TextView text_money;
        TextView text_operate;
    }
}
