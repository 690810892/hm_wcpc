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
import android.widget.RatingBar;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.dialog.HemaButtonDialog;
import com.hemaapp.hm_FrameWork.view.RoundedImageView;
import com.hemaapp.wcpc_driver.BaseNetWorker;
import com.hemaapp.wcpc_driver.BaseRecycleAdapter;
import com.hemaapp.wcpc_driver.BaseUtil;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.activity.SelectPositionActivity;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.CurrentTripsInfor;
import com.hemaapp.wcpc_driver.module.DataInfor;
import com.hemaapp.wcpc_driver.module.Reply;
import com.hemaapp.wcpc_driver.module.TripClient;
import com.hemaapp.wcpc_driver.module.User;
import com.hemaapp.wcpc_driver.view.FlowLayout.TagFlowLayout;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import xtom.frame.util.XtomTimeUtil;

/**
 * 首页
 */
public class ReplyAdapter extends BaseRecycleAdapter<Reply> {
    private Context mContext;
    private BaseNetWorker netWorker;
    public Reply blog;
    User user;
    ArrayList<Reply> clients = new ArrayList<Reply>();

    public ReplyAdapter(Context mContext, List<Reply> datas, BaseNetWorker netWorker) {
        super(datas);
        this.mContext = mContext;
        this.netWorker = netWorker;
        user = hm_WcpcDriverApplication.getInstance().getUser();
    }

    @Override
    protected void bindData(BaseViewHolder holder, final int position) {
        final Reply infor = datas.get(position);
        RoundedImageView imageView = (RoundedImageView) holder.getView(R.id.iv_image);
        ImageLoader.getInstance().displayImage(infor.getAvatar(), imageView, hm_WcpcDriverApplication.getInstance()
                .getOptions(R.mipmap.default_user));
        imageView.setCornerRadius(100);
        ((TextView) holder.getView(R.id.tv_name)).setText(infor.getRealname());
        ((TextView) holder.getView(R.id.tv_time)).setText(XtomTimeUtil.TransTime(infor.getRegdate(), "yyyy/MM/dd HH:mm"));
        ((TextView) holder.getView(R.id.tv_start)).setText(infor.getStartaddress());
        ((TextView) holder.getView(R.id.tv_end)).setText(infor.getEndaddress());
        ((RatingBar) holder.getView(R.id.rb_level)).setRating(Float.parseFloat(infor.getPoint()));
        ArrayList<DataInfor> tags=new ArrayList<>();
        String []sts=infor.getReply_str_text1().split(",");
        for (int i=0;i<sts.length;i++){
            tags.add(new DataInfor(sts[i]));
        }
        TagReplyAdapter tagReplyAdapter=new TagReplyAdapter(mContext,tags);
        ((TagFlowLayout) holder.getView(R.id.multitextview)).setAdapter(tagReplyAdapter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.listitem_reply;
    }

}
