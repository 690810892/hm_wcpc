package com.hemaapp.wcpc_user.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.dialog.HemaButtonDialog;
import com.hemaapp.hm_FrameWork.view.RoundedImageView;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseNetWorker;
import com.hemaapp.wcpc_user.BaseRecycleAdapter;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.ToLogin;
import com.hemaapp.wcpc_user.activity.CancelOrderActivity;
import com.hemaapp.wcpc_user.activity.PingJiaActivity;
import com.hemaapp.wcpc_user.activity.ToPayActivity;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.CurrentTripsInfor;
import com.hemaapp.wcpc_user.module.FeeRule;
import com.hemaapp.wcpc_user.module.User;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import xtom.frame.util.XtomTimeUtil;

/**
 * 计费规则
 */
public class FeeRuleAdapter extends BaseRecycleAdapter<FeeRule> {
    private Context mContext;
    public FeeRule blog;
    private String keytype;
    User user;

    public FeeRuleAdapter(Context mContext, List<FeeRule> datas) {
        super(datas);
        this.mContext = mContext;
        user = hm_WcpcUserApplication.getInstance().getUser();
    }

    @Override
    protected void bindData(BaseViewHolder holder, final int position) {
        final FeeRule infor = datas.get(position);
        ((TextView) holder.getView(R.id.tv_name)).setText(infor.getCity1() + "--" + infor.getCity2());
        ((TextView) holder.getView(R.id.tv_fee)).setText(infor.getPrice() + "元");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.listitem_fee_rule;
    }


}
