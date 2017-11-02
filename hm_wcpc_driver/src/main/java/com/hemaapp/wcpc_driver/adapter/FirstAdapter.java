package com.hemaapp.wcpc_driver.adapter;

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
import com.hemaapp.wcpc_driver.BaseNetWorker;
import com.hemaapp.wcpc_driver.BaseRecycleAdapter;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.activity.SelectPositionActivity;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.CurrentTripsInfor;
import com.hemaapp.wcpc_driver.module.TripClient;
import com.hemaapp.wcpc_driver.module.User;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import xtom.frame.util.XtomBaseUtil;
import xtom.frame.util.XtomTimeUtil;

/**
 * 首页
 */
public class FirstAdapter extends BaseRecycleAdapter<CurrentTripsInfor> {
    private Context mContext;
    private BaseNetWorker netWorker;
    public CurrentTripsInfor blog;
    User user;
    private String keytype;
    ArrayList<TripClient> clients = new ArrayList<TripClient>();

    public FirstAdapter(Context mContext, List<CurrentTripsInfor> datas, BaseNetWorker netWorker) {
        super(datas);
        this.mContext = mContext;
        this.netWorker = netWorker;
        user = hm_WcpcDriverApplication.getInstance().getUser();
    }

    @Override
    protected void bindData(BaseViewHolder holder, final int position) {
        final CurrentTripsInfor infor = datas.get(position);
        RoundedImageView imageView = (RoundedImageView) holder.getView(R.id.iv_avatar);
        ImageLoader.getInstance().displayImage(infor.getAvatar(), imageView, hm_WcpcDriverApplication.getInstance()
                .getOptions(R.mipmap.default_user));
        imageView.setCornerRadius(100);
        if (infor.getSex().equals("男")) {
            ((ImageView) holder.getView(R.id.iv_sex)).setImageResource(R.mipmap.img_sex_boy);
        } else {
            ((ImageView) holder.getView(R.id.iv_sex)).setImageResource(R.mipmap.img_sex_girl);
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
            ((TextView) holder.getView(R.id.tv_button)).setText("接到乘客");
        } else if (infor.getStatus().equals("3")) {//待送达
            ((TextView) holder.getView(R.id.tv_staut)).setText("乘客已上车");
            ((TextView) holder.getView(R.id.tv_staut)).setTextColor(0xfff49400);
            ((TextView) holder.getView(R.id.tv_button)).setText("送达");
        }
        ((TextView) holder.getView(R.id.name)).setText(infor.getNickname());
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
        ((TextView) holder.getView(R.id.tv_fee)).setText(infor.getDriver_fee());
        ((TextView) holder.getView(R.id.tv_person_count)).setText("乘车人数 " + infor.getNumbers());
        holder.getView(R.id.iv_loc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blog = infor;
                TripClient client = new TripClient(blog.getAvatar(), blog.getNickname(), blog.getSex(), blog.getUsername(), blog.getRemarks(),
                        blog.getStartaddress(), blog.getEndaddress(), blog.getLng_start(), blog.getLat_start(), blog.getLng_end(), blog.getLat_end(),
                        false);
                clients.clear();
                clients.add(client);
                String allgetflag = "0";
                if (blog.getStatus().equals("1"))
                    allgetflag = "0";
                else
                    allgetflag = "1";
                Intent it = new Intent(mContext, SelectPositionActivity.class);
                it.putExtra("client", clients);
                it.putExtra("flag", 0);
                it.putExtra("allgetflag", allgetflag);
               mContext.startActivity(it);
            }
        });
        holder.getView(R.id.iv_tel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blog = infor;
                showTelDialog();
            }
        });
        holder.getView(R.id.tv_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blog = infor;
                if (blog.getStatus().equals("1")) {
                    keytype = "2";
                } else {
                    keytype = "4";
                }
                dialog();
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
        return R.layout.listitem_first;
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
