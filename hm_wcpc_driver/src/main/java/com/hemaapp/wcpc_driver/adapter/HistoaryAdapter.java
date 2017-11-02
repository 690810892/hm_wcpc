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

import com.hemaapp.hm_FrameWork.dialog.HemaButtonDialog;
import com.hemaapp.hm_FrameWork.view.RoundedImageView;
import com.hemaapp.wcpc_driver.BaseNetWorker;
import com.hemaapp.wcpc_driver.BaseRecycleAdapter;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.activity.PingJiaActivity;
import com.hemaapp.wcpc_driver.activity.SelectPositionActivity;
import com.hemaapp.wcpc_driver.activity.ToPayActivity;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.CurrentTripsInfor;
import com.hemaapp.wcpc_driver.module.TripClient;
import com.hemaapp.wcpc_driver.module.User;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import xtom.frame.util.XtomTimeUtil;

/**
 * 历史订单
 */
public class HistoaryAdapter extends BaseRecycleAdapter<CurrentTripsInfor> {
    private Context mContext;
    private BaseNetWorker netWorker;
    public CurrentTripsInfor blog;
    User user;
    private String keytype;
    ArrayList<TripClient> clients = new ArrayList<TripClient>();

    public HistoaryAdapter(Context mContext, List<CurrentTripsInfor> datas, BaseNetWorker netWorker) {
        super(datas);
        this.mContext = mContext;
        this.netWorker = netWorker;
        user = hm_WcpcDriverApplication.getInstance().getUser();
    }

    @Override
    protected void bindData(BaseViewHolder holder, final int position) {
        final CurrentTripsInfor infor = datas.get(position);
        RoundedImageView imageView = (RoundedImageView) holder.getView(R.id.imageview);
        ImageLoader.getInstance().displayImage(infor.getAvatar(), imageView, hm_WcpcDriverApplication.getInstance()
                .getOptions(R.mipmap.default_user));
        imageView.setCornerRadius(100);
        if (infor.getSex().equals("男")) {
            ((ImageView) holder.getView(R.id.imageview_0)).setImageResource(R.mipmap.img_sex_boy);
        } else {
            ((ImageView) holder.getView(R.id.imageview_0)).setImageResource(R.mipmap.img_sex_girl);
        }
        String today = XtomTimeUtil.getCurrentTime("yyyy-MM-dd");
        String day = XtomTimeUtil.TransTime(infor.getBegintime(), "yyyy-MM-dd");
        String time = XtomTimeUtil.TransTime(infor.getBegintime(), "HH:mm");
        if (day.equals(today))
            day = "今天";
        ((TextView) holder.getView(R.id.tv_time)).setText(day + " " + time);
        if (infor.getStatus().equals("1")) {//未上车
            ((TextView) holder.getView(R.id.tv_staut)).setText("乘客未上车");
            ((TextView) holder.getView(R.id.tv_staut)).setTextColor(0xff636363);
            ((TextView) holder.getView(R.id.tv_button0)).setVisibility(View.GONE);
            ((TextView) holder.getView(R.id.tv_button1)).setText("接到乘客");
            ((TextView) holder.getView(R.id.tv_button1)).setBackgroundResource(R.drawable.bt_qiangdan);
        } else if (infor.getStatus().equals("3")) {//待送达
            ((TextView) holder.getView(R.id.tv_staut)).setText("乘客已上车");
            ((TextView) holder.getView(R.id.tv_staut)).setTextColor(0xfff49400);//黄
            ((TextView) holder.getView(R.id.tv_button0)).setVisibility(View.GONE);
            ((TextView) holder.getView(R.id.tv_button1)).setText("送达");
            ((TextView) holder.getView(R.id.tv_button1)).setBackgroundResource(R.drawable.bt_qiangdan);
        } else if (infor.getStatus().equals("5")) {//待支付
            ((TextView) holder.getView(R.id.tv_staut)).setText("待支付");
            ((TextView) holder.getView(R.id.tv_staut)).setTextColor(0xfff49400);//黄
            ((TextView) holder.getView(R.id.tv_button0)).setVisibility(View.GONE);
            ((TextView) holder.getView(R.id.tv_button1)).setText("司机代付");
            ((TextView) holder.getView(R.id.tv_button1)).setBackgroundResource(R.drawable.bg_operate);
        } else if (infor.getStatus().equals("6")) {//已支付
            if (infor.getReplyflag2().equals("0")) {//司机未评价
                ((TextView) holder.getView(R.id.tv_staut)).setText("待评价");
                ((TextView) holder.getView(R.id.tv_staut)).setTextColor(0xff636363);
                ((TextView) holder.getView(R.id.tv_button0)).setVisibility(View.VISIBLE);
                ((TextView) holder.getView(R.id.tv_button0)).setText("去评价");
                ((TextView) holder.getView(R.id.tv_button1)).setText("删除订单");
                ((TextView) holder.getView(R.id.tv_button1)).setBackgroundResource(R.drawable.bg_operate);
            } else {
                ((TextView) holder.getView(R.id.tv_staut)).setText("已完成");
                ((TextView) holder.getView(R.id.tv_staut)).setTextColor(0xff636363);
                ((TextView) holder.getView(R.id.tv_button0)).setVisibility(View.GONE);
                ((TextView) holder.getView(R.id.tv_button1)).setText("删除订单");
                ((TextView) holder.getView(R.id.tv_button1)).setBackgroundResource(R.drawable.bg_operate);
            }
        } else if (infor.getStatus().equals("10") || infor.getStatus().equals("11")) {//已取消
            ((TextView) holder.getView(R.id.tv_staut)).setText("已取消");
            ((TextView) holder.getView(R.id.tv_staut)).setTextColor(0xff636363);
            ((TextView) holder.getView(R.id.tv_button0)).setVisibility(View.GONE);
            ((TextView) holder.getView(R.id.tv_button1)).setText("删除订单");
            ((TextView) holder.getView(R.id.tv_button1)).setBackgroundResource(R.drawable.bg_operate);
        }
        ((TextView) holder.getView(R.id.tv_name)).setText(infor.getNickname());
        ((TextView) holder.getView(R.id.tv_count)).setText("乘车次数 " + infor.getTakecount());
        ((TextView) holder.getView(R.id.tv_start)).setText(infor.getStartaddress());
        ((TextView) holder.getView(R.id.tv_end)).setText(infor.getEndaddress());
        if (infor.getCarpoolflag().equals("1")) {
            ((TextView) holder.getView(R.id.tv_pin)).setText("拼车");
            ((TextView) holder.getView(R.id.tv_pin)).setCompoundDrawablesWithIntrinsicBounds(R.mipmap.img_first_pin, 0, 0, 0);
        } else {
            ((TextView) holder.getView(R.id.tv_pin)).setText("包车");
            ((TextView) holder.getView(R.id.tv_pin)).setCompoundDrawablesWithIntrinsicBounds(R.mipmap.img_first_bao, 0, 0, 0);
        }
        ((TextView) holder.getView(R.id.tv_content)).setText(infor.getRemarks());
        ((TextView) holder.getView(R.id.tv_price)).setText(infor.getDriver_fee());
        ((TextView) holder.getView(R.id.tv_num)).setText("乘车人数 " + infor.getNumbers());
        holder.getView(R.id.tv_button0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blog = infor;
                //去评价
                Intent it = new Intent(mContext, PingJiaActivity.class);
                it.putExtra("id", infor.getId());
                it.putExtra("driver_id", infor.getClient_id());
                mContext.startActivity(it);
            }
        });
        holder.getView(R.id.tv_button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blog = infor;
                if (blog.getStatus().equals("1")) {
                    keytype = "2";//接到乘客
                    dialog();
                } else if (blog.getStatus().equals("3")) {
                    keytype = "4";//送达
                    dialog();
                } else if (blog.getStatus().equals("5")) {
                    //支付
                    Intent it;
                    it = new Intent(mContext, ToPayActivity.class);
                    it.putExtra("id", datas.get(position).getId());
                    it.putExtra("total_fee", datas.get(position).getTotal_fee());
                    mContext.startActivity(it);
                } else {
                    keytype = "7";//删除
                    dialog();
                }
//                showTelDialog();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent it;
//                it = new Intent(mContext, BlogInforActivity.class);
//                it.putExtra("id", datas.get(position).getId());
//                mContext.startActivity(it);

            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.listitem_order_history;
    }

    private HemaButtonDialog mDialog;

    public void dialog() {
        if (mDialog == null) {
            mDialog = new HemaButtonDialog(mContext);
        }
        if (keytype.equals("2")) {
            mDialog.setLeftButtonText("取消");
            mDialog.setRightButtonText("确定");
            mDialog.setText("确定已接到乘客？");
        } else if (keytype.equals("4")) {
            mDialog.setLeftButtonText("取消");
            mDialog.setRightButtonText("确定");
            mDialog.setText("确定已送达乘客到目的地？");
        } else if (keytype.equals("7")) {
            mDialog.setLeftButtonText("取消");
            mDialog.setRightButtonText("确定");
            mDialog.setText("确定删除该订单？删除订单后不可恢复");
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
            User user = hm_WcpcDriverApplication.getInstance().getUser();
            netWorker.tripsOperate(user.getToken(), keytype, blog.getId(), "");
        }
    }

    private PopupWindow mWindow;
    private ViewGroup mViewGroup;
    private TextView content1;
    private TextView content2;
    private TextView ok;
    private TextView cancel;

    private void showTelDialog() {
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
        content2.setText(blog.getUsername());
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
                String phone = blog.getUsername();
                //Intent.ACTION_CALL 直接拨打电话，就是进入拨打电话界面，电话已经被拨打出去了。
                //Intent.ACTION_DIAL 是进入拨打电话界面，电话号码已经输入了，但是需要人为的按拨打电话键，才能播出电话。
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                        + phone));
                mContext.startActivity(intent);
            }
        });
    }
}
