package com.hemaapp.wcpc_user.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaAdapter;
import com.hemaapp.hm_FrameWork.view.RoundedImageView;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseUtil;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.activity.CarOwerHomePageActivity;
import com.hemaapp.wcpc_user.activity.TripDetailInforActivity;
import com.hemaapp.wcpc_user.module.TripListInfor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import xtom.frame.image.load.XtomImageTask;
import xtom.frame.view.XtomListView;

/**
 * Created by WangYuxia on 2016/5/12.
 */
public class TripListAdapter extends HemaAdapter {

    private ArrayList<TripListInfor> infors;
    private XtomListView mListView;

    public TripListAdapter(Context mContext, ArrayList<TripListInfor> infors,
                           XtomListView mListView) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_triplist, null);
            holder = new ViewHolder();
            findview(convertView, holder);
            convertView.setTag(R.id.TAG, holder);
        }else{
            holder = (ViewHolder) convertView.getTag(R.id.TAG);
        }
        TripListInfor infor = infors.get(position);
        setListener(holder, infor, convertView);
        return convertView;
    }

    private void setListener(ViewHolder holder, TripListInfor infor, View view ){
        holder.text_starttime.setText(BaseUtil.transTimeChat(infor.getBegintime()));
        try {
            URL url = new URL(infor.getAvatar());
            holder.image_avater.setCornerRadius(90);
            ((BaseActivity)mContext).imageWorker.loadImage(new XtomImageTask(holder.image_avater, url, mContext, mListView));
        } catch (MalformedURLException e) {
            holder.image_avater.setImageResource(R.mipmap.default_driver);
        }
        holder.text_nickname.setText(infor.getRealname());
        if("男".equals(infor.getSex()))
            holder.image_sex.setImageResource(R.mipmap.img_sex_boy);
        else
            holder.image_sex.setImageResource(R.mipmap.img_sex_girl);

        holder.text_startposition.setText(infor.getStartaddress());
        holder.text_endposition.setText(infor.getEndaddress());
        holder.text_kind.setText(infor.getCarbrand());
        holder.text_number.setText(infor.getCarnumber());
        holder.text_personcount.setText("剩余"+(isNull(infor.getRemainnum())?"0":infor.getRemainnum())+"座");
        holder.text_money.setText(infor.getSuccessfee());

        holder.image_avater.setTag(R.id.button, infor);
        holder.image_avater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TripListInfor infor = (TripListInfor) v.getTag(R.id.button);
                Intent it = new Intent(mContext, CarOwerHomePageActivity.class);
                it.putExtra("id", infor.getClient_id());
                mContext.startActivity(it);
            }
        });

        view.setTag(R.id.textview, infor);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TripListInfor infor = (TripListInfor) v.getTag(R.id.textview);
                Intent it = new Intent(mContext, TripDetailInforActivity.class);
                it.putExtra("id", infor.getId());
                mContext.startActivity(it);
            }
        });
    }

    private void findview(View view, ViewHolder holder){
        holder.text_starttime = (TextView) view.findViewById(R.id.textview_0);
        holder.image_avater = (RoundedImageView) view.findViewById(R.id.imageview);
        holder.text_nickname = (TextView) view.findViewById(R.id.textview_1);
        holder.image_sex = (ImageView) view.findViewById(R.id.imageview_0);
        holder.text_startposition = (TextView) view.findViewById(R.id.textview_2);
        holder.text_endposition = (TextView) view.findViewById(R.id.textview_3);
        holder.text_kind = (TextView) view.findViewById(R.id.textview_4);
        holder.text_number = (TextView) view.findViewById(R.id.textview_5);
        holder.text_personcount = (TextView) view.findViewById(R.id.textview_6);
        holder.text_money = (TextView) view.findViewById(R.id.textview_8);
    }

    private static class ViewHolder{
        TextView text_starttime;
        RoundedImageView image_avater;
        TextView text_nickname;
        ImageView image_sex;
        TextView text_startposition;
        TextView text_endposition;
        TextView text_kind; //汽车牌子
        TextView text_number;
        TextView text_personcount; //剩余的位数
        TextView text_money; //钱
    }
}
