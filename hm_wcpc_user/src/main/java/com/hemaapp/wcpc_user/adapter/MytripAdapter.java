package com.hemaapp.wcpc_user.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.dialog.HemaButtonDialog;
import com.hemaapp.hm_FrameWork.view.RoundedImageView;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseNetWorker;
import com.hemaapp.wcpc_user.BaseRecycleAdapter;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.ToLogin;
import com.hemaapp.wcpc_user.activity.CancelOrderActivity;
import com.hemaapp.wcpc_user.activity.MListActivity;
import com.hemaapp.wcpc_user.activity.PingJiaActivity;
import com.hemaapp.wcpc_user.activity.SendActivity;
import com.hemaapp.wcpc_user.activity.ToPayActivity;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.CurrentTripsInfor;
import com.hemaapp.wcpc_user.module.User;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import xtom.frame.util.XtomBaseUtil;
import xtom.frame.util.XtomTimeUtil;

/**
 * 我的行程
 */
public class MytripAdapter extends BaseRecycleAdapter<CurrentTripsInfor> {
    private Context mContext;
    private BaseNetWorker netWorker;
    public CurrentTripsInfor blog;
    private String keytype;
    User user;
    private PopupWindow mWindow;
    private ViewGroup mViewGroup;

    public MytripAdapter(Context mContext, List<CurrentTripsInfor> datas, BaseNetWorker netWorker) {
        super(datas);
        this.mContext = mContext;
        this.netWorker = netWorker;
        user = hm_WcpcUserApplication.getInstance().getUser();
    }

    @Override
    protected void bindData(BaseViewHolder holder, final int position) {
        final CurrentTripsInfor infor = datas.get(position);
        RoundedImageView imageView = (RoundedImageView) holder.getView(R.id.iv_avatar);
        ImageLoader.getInstance().displayImage(infor.getDriver_avatar(), imageView, hm_WcpcUserApplication.getInstance()
                .getOptions(R.mipmap.default_driver));
        imageView.setCornerRadius(100);
        ((TextView) holder.getView(R.id.tv_name)).setText(infor.getRealname());
        if (infor.getDriver_sex().equals("男")) {
            ((ImageView) holder.getView(R.id.iv_sex)).setImageResource(R.mipmap.img_sex_boy);
        } else {
            ((ImageView) holder.getView(R.id.iv_sex)).setImageResource(R.mipmap.img_sex_girl);
        }

        ((TextView) holder.getView(R.id.car)).setText(infor.getCarbrand() + " " + infor.getCarnumber());
        ((TextView) holder.getView(R.id.tv_start)).setText(infor.getStartaddress());
        ((TextView) holder.getView(R.id.tv_end)).setText(infor.getEndaddress());
        ((TextView) holder.getView(R.id.tv_content)).setText(infor.getRemarks());
        String today = XtomTimeUtil.getCurrentTime("yyyy-MM-dd");
        String day = XtomTimeUtil.TransTime(infor.getBegintime(), "yyyy-MM-dd");
        String time = XtomTimeUtil.TransTime(infor.getBegintime(), "HH:mm");
        if (day.equals(today))
            day = "今天";
        ((TextView) holder.getView(R.id.tv_time)).setText(day + " " + time);
        if (infor.getCarpoolflag().equals("1")) {
            ((TextView) holder.getView(R.id.tv_flag)).setText(" 拼车 ");
            ((TextView) holder.getView(R.id.tv_num)).setText("我的乘车人数：" + infor.getNumbers() + "人");
        } else {
            ((TextView) holder.getView(R.id.tv_flag)).setText(" 包车 ");
            ((TextView) holder.getView(R.id.tv_num)).setText("");
        }
        ((TextView) holder.getView(R.id.tv_price)).setText(infor.getTotal_fee() + "元");
        if (infor.getCoupon_fee().equals("0.00")) {
            holder.getView(R.id.tv_couple).setVisibility(View.GONE);
        } else {
            holder.getView(R.id.tv_couple).setVisibility(View.VISIBLE);
            ((TextView) holder.getView(R.id.tv_couple)).setText(" 代金券抵扣" + infor.getCoupon_fee() + "元 ");
        }
        if (infor.getStatus().equals("0")) {
            ((TextView) holder.getView(R.id.status)).setText("待派单");
            holder.getView(R.id.iv_avatar).setVisibility(View.GONE);
            holder.getView(R.id.lv_name).setVisibility(View.GONE);
            holder.getView(R.id.car).setVisibility(View.GONE);
            ((TextView) holder.getView(R.id.tv_button0)).setText("取消订单");
            ((TextView) holder.getView(R.id.tv_button0)).setVisibility(View.VISIBLE);
            ((TextView) holder.getView(R.id.tv_button1)).setVisibility(View.GONE);
        } else {
            holder.getView(R.id.iv_avatar).setVisibility(View.VISIBLE);
            holder.getView(R.id.lv_name).setVisibility(View.VISIBLE);
            holder.getView(R.id.car).setVisibility(View.VISIBLE);
            if (infor.getStatus().equals("1")) {
                ((TextView) holder.getView(R.id.status)).setText("未上车");
                ((TextView) holder.getView(R.id.tv_button0)).setText("取消订单");
                ((TextView) holder.getView(R.id.tv_button1)).setText("确认上车 ");
                ((TextView) holder.getView(R.id.tv_button0)).setVisibility(View.VISIBLE);
                ((TextView) holder.getView(R.id.tv_button1)).setVisibility(View.VISIBLE);
            } else if (infor.getStatus().equals("3")) {
                ((TextView) holder.getView(R.id.status)).setText("进行中");
                ((TextView) holder.getView(R.id.tv_button1)).setText("到达目的地");
                ((TextView) holder.getView(R.id.tv_button0)).setVisibility(View.GONE);
                ((TextView) holder.getView(R.id.tv_button1)).setVisibility(View.VISIBLE);
            } else if (infor.getStatus().equals("5")) {
                ((TextView) holder.getView(R.id.status)).setText("待支付");
                ((TextView) holder.getView(R.id.tv_button1)).setText("  去支付  ");
                ((TextView) holder.getView(R.id.tv_button0)).setVisibility(View.GONE);
                ((TextView) holder.getView(R.id.tv_button1)).setVisibility(View.VISIBLE);
            } else if (infor.getStatus().equals("6")) {
                if (infor.getReplyflag1().equals("0")) {
                    ((TextView) holder.getView(R.id.status)).setText("待评价");
                    ((TextView) holder.getView(R.id.tv_button0)).setText("删除订单");
                    ((TextView) holder.getView(R.id.tv_button1)).setText("  去评价  ");
                    ((TextView) holder.getView(R.id.tv_button0)).setVisibility(View.VISIBLE);
                    ((TextView) holder.getView(R.id.tv_button1)).setVisibility(View.VISIBLE);
                } else {
                    ((TextView) holder.getView(R.id.status)).setText("已完成");
                    ((TextView) holder.getView(R.id.tv_button0)).setText("删除订单");
                    ((TextView) holder.getView(R.id.tv_button0)).setVisibility(View.VISIBLE);
                    ((TextView) holder.getView(R.id.tv_button1)).setVisibility(View.GONE);
                }

            } else if (infor.getStatus().equals("10") || infor.getStatus().equals("11")) {
                if (infor.getDriver_id().equals("0")) {
                    holder.getView(R.id.iv_avatar).setVisibility(View.GONE);
                    holder.getView(R.id.lv_name).setVisibility(View.GONE);
                    holder.getView(R.id.car).setVisibility(View.GONE);
                }
                ((TextView) holder.getView(R.id.status)).setText("已取消");
                ((TextView) holder.getView(R.id.tv_button0)).setText("删除订单");
                ((TextView) holder.getView(R.id.tv_button0)).setVisibility(View.VISIBLE);
                ((TextView) holder.getView(R.id.tv_button1)).setVisibility(View.GONE);
            }
        }
        holder.getView(R.id.tv_button0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blog = infor;
                User user = hm_WcpcUserApplication.getInstance().getUser();
                if (user == null) {
                    ToLogin.showLogin((BaseActivity) mContext);
                    return;
                }
                if (blog.getStatus().equals("0") || blog.getStatus().equals("1")) {
                    CancelTip();
//                    Intent it = new Intent(mContext, CancelOrderActivity.class);
//                    it.putExtra("id", infor.getId());
//                    mContext.startActivity(it);
                } else if (blog.getStatus().equals("6") || blog.getStatus().equals("10") || blog.getStatus().equals("11")) {
                    keytype = "6";
                    dialog();
                }
            }
        });
        holder.getView(R.id.tv_button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blog = infor;
                User user = hm_WcpcUserApplication.getInstance().getUser();
                if (user == null) {
                    ToLogin.showLogin((BaseActivity) mContext);
                    return;
                }
                if (blog.getStatus().equals("1")) {//确认上车
                    keytype = "3";
                    dialog();
                } else if (blog.getStatus().equals("3")) {//到达目的地
                    keytype = "5";
                    dialog();
                } else if (blog.getStatus().equals("5")) {//去支付
                    Intent it = new Intent(mContext, ToPayActivity.class);
                    it.putExtra("id", infor.getId());
                    it.putExtra("total_fee", infor.getTotal_fee());
                    it.putExtra("driver_id", infor.getDriver_id());
                    mContext.startActivity(it);
                } else if (blog.getStatus().equals("6")) {//去评价
                    Intent it = new Intent(mContext, PingJiaActivity.class);
                    it.putExtra("id", infor.getId());
                    it.putExtra("driver_id", infor.getDriver_id());
                    mContext.startActivity(it);
                }
            }
        });
        holder.getView(R.id.iv_back).setVisibility(View.GONE);
        holder.getView(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blog = infor;
                Intent it=new Intent(mContext, SendActivity.class);

                mContext.startActivity(it);
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.listitem_mytripslist_new;
    }

    private HemaButtonDialog mDialog;

    public void dialog() {
        if (mDialog == null) {
            mDialog = new HemaButtonDialog(mContext);
        }
        if (keytype.equals("3")) {
            mDialog.setLeftButtonText("取消");
            mDialog.setRightButtonText("确定");
            mDialog.setText("是否确认上车？");
        } else if (keytype.equals("5")) {
            mDialog.setLeftButtonText("取消");
            mDialog.setRightButtonText("确定");
            mDialog.setText("为了保障您的出行，请谨慎操作。\n确定到达目的地吗？");
        } else if (keytype.equals("6")) {
            mDialog.setLeftButtonText("取消");
            mDialog.setRightButtonText("确定");
            mDialog.setText("您确定要删除该订单?一旦删除无法找回");
        }
        mDialog.setButtonListener(new ButtonListener());
        mDialog.setRightButtonTextColor(mContext.getResources().getColor(R.color.yellow));

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
            netWorker.tripsOperate(user.getToken(), keytype, blog.getId(), "");
        }
    }

    private void CancelTip() {
        User user = hm_WcpcUserApplication.getInstance().getUser();
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
                R.layout.pop_first_tip, null);
        TextView cancel = (TextView) mViewGroup.findViewById(R.id.textview_1);
        TextView ok = (TextView) mViewGroup.findViewById(R.id.textview_2);
        TextView title1 = (TextView) mViewGroup.findViewById(R.id.textview);
        TextView title2 = (TextView) mViewGroup.findViewById(R.id.textview_0);
        mWindow.setContentView(mViewGroup);
        mWindow.showAtLocation(mViewGroup, Gravity.CENTER, 0, 0);
        if (user.getToday_cancel_count().equals("3")) {
            title1.setText("您今天已取消3次订单！");
        } else
            title1.setText("确定要取消吗？");
        title2.setText("一天内订单取消不能超过3次,您已取消" + user.getToday_cancel_count() + "次");
        cancel.setText("取消");
        ok.setText("确定");
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
                User user = hm_WcpcUserApplication.getInstance().getUser();
                if (user.getToday_cancel_count().equals("3")) {
                    return;
                }
                Intent it = new Intent(mContext, CancelOrderActivity.class);
                it.putExtra("id", blog.getId());
                if (blog.getStatus().equals("0"))
                    it.putExtra("keytype", "1");
                else
                    it.putExtra("keytype", "6");
                ((BaseActivity) mContext).startActivityForResult(it, 1);
            }
        });
    }
}
