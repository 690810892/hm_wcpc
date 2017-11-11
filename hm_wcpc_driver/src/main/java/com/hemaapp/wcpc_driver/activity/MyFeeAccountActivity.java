package com.hemaapp.wcpc_driver.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.hm_FrameWork.view.RefreshLoadmoreLayout;
import com.hemaapp.wcpc_driver.BaseActivity;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.BaseUtil;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.adapter.FeeAccountListAdapter;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.FeeAccountInfor;
import com.hemaapp.wcpc_driver.module.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import xtom.frame.util.XtomToastUtil;
import xtom.frame.view.XtomListView;
import xtom.frame.view.XtomRefreshLoadmoreLayout;

/**
 * 余额明细
 */
public class MyFeeAccountActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private ImageView right;

    private RefreshLoadmoreLayout layout;
    private ProgressBar progressBar;
    private XtomListView mListView;

    private int page = 0;
    private ArrayList<FeeAccountInfor> infors = new ArrayList<>();
    private FeeAccountListAdapter adapter;
    private User user;
    private String start="",end="";
    private Calendar selectedDate;//系统当前时间
    private Calendar startDate;
    private Calendar endDate;
    private TimePickerView pvDate,pvDate2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_feeaccount);
        super.onCreate(savedInstanceState);
        user = hm_WcpcDriverApplication.getInstance().getUser();
        getTripsList();
        title.postDelayed(new Runnable() {
            @Override
            public void run() {
                selectedDate = Calendar.getInstance();
                startDate = Calendar.getInstance();
                endDate = Calendar.getInstance();
                startDate.set(1931, 1, 10);
                endDate.set(2120, 2, 28);
            }
        }, 100);
    }

    private void getTripsList(){
        getNetWorker().accountRecordList(user.getToken(),start,end, page);
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case ACCOUNT_RECORD_LIST:
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case ACCOUNT_RECORD_LIST:
                progressBar.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);
                break;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void callBackForServerSuccess(HemaNetTask netTask,
                                            HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case ACCOUNT_RECORD_LIST:
                String page = netTask.getParams().get("page");
                HemaPageArrayResult<FeeAccountInfor> cResult = (HemaPageArrayResult<FeeAccountInfor>) baseResult;
                ArrayList<FeeAccountInfor> cashs = cResult.getObjects();
                if ("0".equals(page)) {// 刷新
                    layout.refreshSuccess();
                    infors.clear();
                    infors.addAll(cashs);

                    hm_WcpcDriverApplication application = hm_WcpcDriverApplication.getInstance();
                    int sysPagesize = application.getSysInitInfo()
                            .getSys_pagesize();
                    if (cashs.size() < sysPagesize)
                        layout.setLoadmoreable(false);
                    else
                        layout.setLoadmoreable(true);
                } else {// 更多
                    layout.loadmoreSuccess();
                    if (cashs.size() > 0)
                        infors.addAll(cashs);
                    else {
                        layout.setLoadmoreable(false);
                        XtomToastUtil.showShortToast(mContext, "已经到最后啦");
                    }
                }
                freshData();
                break;
        }
    }

    private void freshData() {
        if (adapter == null) {
            adapter = new FeeAccountListAdapter(mContext, infors);
            adapter.setEmptyString("暂时没有余额明细");
            mListView.setAdapter(adapter);
        } else {
            adapter.setEmptyString("暂时没有余额明细");
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case ACCOUNT_RECORD_LIST:
                String page = netTask.getParams().get("page");
                if ("0".equals(page)) {// 刷新
                    layout.refreshFailed();
                    freshData();
                } else {// 更多
                    layout.loadmoreFailed();
                }
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case ACCOUNT_RECORD_LIST:
                String page = netTask.getParams().get("page");
                if ("0".equals(page)) {// 刷新
                    layout.refreshFailed();
                    freshData();
                } else {// 更多
                    layout.loadmoreFailed();
                }
                break;
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        right = (ImageView) findViewById(R.id.title_btn_right);
        title = (TextView) findViewById(R.id.title_text);

        layout = (RefreshLoadmoreLayout) findViewById(R.id.refreshLoadmoreLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        mListView = (XtomListView) findViewById(R.id.listview);
    }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
        title.setText("账户明细");
        right.setImageResource(R.mipmap.img_right_time);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        layout.setOnStartListener(new XtomRefreshLoadmoreLayout.OnStartListener() {
            @Override
            public void onStartRefresh(XtomRefreshLoadmoreLayout xtomRefreshLoadmoreLayout) {
                page = 0;
                getTripsList();
            }

            @Override
            public void onStartLoadmore(XtomRefreshLoadmoreLayout xtomRefreshLoadmoreLayout) {
                page ++;
                getTripsList();
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvDate = new TimePickerView.Builder(mContext, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {//选中事件回调
                        start=BaseUtil.getTime2(date);
                       // tvRemindRegdate.setText(BaseUtil.getTime(date));
                    }
                }).setDate(selectedDate)
                        .setRangDate(startDate, endDate)
                        .setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {

                            @Override
                            public void customLayout(View v) {
                                final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                                tvSubmit.setText("下一步");
                                final TextView tvtitle = (TextView) v.findViewById(R.id.tv_title);
                                tvtitle.setText("选择开始时间");
                                TextView ivCancel = (TextView) v.findViewById(R.id.iv_cancel);
                                tvSubmit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        pvDate.returnData();
                                        pvDate.dismiss();
                                        pvDate2 = new TimePickerView.Builder(mContext, new TimePickerView.OnTimeSelectListener() {
                                            @Override
                                            public void onTimeSelect(Date date, View v) {//选中事件回调
                                                end=BaseUtil.getTime2(date);
                                                getTripsList();
                                                // tvRemindRegdate.setText(BaseUtil.getTime(date));
                                            }
                                        }).setDate(selectedDate)
                                                .setRangDate(startDate, endDate)
                                                .setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {

                                                    @Override
                                                    public void customLayout(View v) {
                                                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                                                        final TextView tvtitle = (TextView) v.findViewById(R.id.tv_title);
                                                        tvSubmit.setText("确定");
                                                        tvtitle.setText("选择截止时间");
                                                        TextView ivCancel = (TextView) v.findViewById(R.id.iv_cancel);
                                                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                pvDate2.returnData();
                                                                pvDate2.dismiss();
                                                            }
                                                        });
                                                        ivCancel.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                pvDate2.dismiss();
                                                            }
                                                        });
                                                    }
                                                }).setType(new boolean[]{true, true, true, false, false, false})
                                                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                                                .setDividerColor(0xff1aad19)
                                                .build();
                                        pvDate2.show();
                                    }
                                });
                                ivCancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        pvDate.dismiss();
                                    }
                                });
                            }
                        }).setType(new boolean[]{true, true, true, false, false, false})
                        .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                        .setDividerColor(0xff1aad19)
                        .build();
                pvDate.show();
            }
        });
    }
}
