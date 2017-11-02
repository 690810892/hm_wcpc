package com.hemaapp.wcpc_user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.dialog.HemaButtonDialog;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.hm_FrameWork.view.RefreshLoadmoreLayout;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.EventBusModel;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.RecycleUtils;
import com.hemaapp.wcpc_user.ToLogin;
import com.hemaapp.wcpc_user.adapter.FeeRuleAdapter;
import com.hemaapp.wcpc_user.adapter.MytripAdapter;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.CurrentTripsInfor;
import com.hemaapp.wcpc_user.module.FeeRule;
import com.hemaapp.wcpc_user.module.User;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import xtom.frame.util.XtomToastUtil;
import xtom.frame.view.XtomRefreshLoadmoreLayout;

/**
 * 我的行程
 */
public class FeeRuleListActivity extends BaseActivity {
    @BindView(R.id.title_btn_left)
    ImageView titleBtnLeft;
    @BindView(R.id.title_btn_right)
    TextView titleBtnRight;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.title)
    LinearLayout title;
    @BindView(R.id.tv_fee)
    TextView tvFee;
    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.refreshLoadmoreLayout)
    RefreshLoadmoreLayout refreshLoadmoreLayout;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.empty)
    LinearLayout empty;
    @BindView(R.id.imageView)
    ImageView imageView;
    private User user;
    private String token = "", city_id = "1", city;
    private FeeRuleAdapter adapter;
    private ArrayList<FeeRule> blogs = new ArrayList<>();
    private Integer currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_feerule_list);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        user = hm_WcpcUserApplication.getInstance().getUser();
        if (user == null)
            token = "";
        else
            token = user.getToken();
        adapter = new FeeRuleAdapter(mContext, blogs);
        RecycleUtils.initVerticalRecyle(rvList);
        rvList.setAdapter(adapter);
        getList(currentPage);
    }

    private void getList(int page) {
        getNetWorker().feeRule(city_id, city);
    }

    public void onEventMainThread(EventBusModel event) {
        switch (event.getType()) {
            case REFRESH_BLOG_LIST:
                currentPage = 0;
                getList(currentPage);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        // TODO Auto-generated method stub
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case FEE_RULE:
                break;
            case TRIPS_SAVEOPERATE:
                showProgressDialog("请稍后");
                break;
            default:
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        // TODO Auto-generated method stub
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case FEE_RULE:
                progressbar.setVisibility(View.GONE);
                refreshLoadmoreLayout.setVisibility(View.VISIBLE);
                break;
            case TRIPS_SAVEOPERATE:
                cancelProgressDialog();
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForServerSuccess(HemaNetTask netTask,
                                            HemaBaseResult baseResult) {
        // TODO Auto-generated method stub
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case FEE_RULE:
                String page = netTask.getParams().get("page");
                @SuppressWarnings("unchecked")
                HemaArrayResult<FeeRule> gResult = (HemaArrayResult<FeeRule>) baseResult;
                ArrayList<FeeRule> goods = gResult.getObjects();
                refreshLoadmoreLayout.refreshSuccess();
                this.blogs.clear();
                this.blogs.addAll(goods);
                refreshLoadmoreLayout.setLoadmoreable(false);
                if (blogs.size() == 0) {
                    empty.setVisibility(View.VISIBLE);
                } else {
                    empty.setVisibility(View.INVISIBLE);
                }
                adapter.notifyDataSetChanged();
                break;
            case TRIPS_SAVEOPERATE:
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case FEE_RULE:
            case TRIPS_SAVEOPERATE:
                showTextDialog(baseResult.getMsg());
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
        // TODO Auto-generated method stub
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case FEE_RULE:
                showTextDialog("加载失败");
                break;
            case TRIPS_SAVEOPERATE:
                showTextDialog("操作失败");
                break;
            default:
                break;
        }
    }

    @Override
    protected void findView() {
    }

    @Override
    protected void getExras() {
        city = mIntent.getStringExtra("city");
        city_id = mIntent.getStringExtra("city_id");
    }

    @Override
    protected void setListener() {
        titleText.setText("计费规则-" + city);
        titleBtnRight.setText("");
        refreshLoadmoreLayout.setOnStartListener(new XtomRefreshLoadmoreLayout.OnStartListener() {

            @Override
            public void onStartRefresh(XtomRefreshLoadmoreLayout v) {
                currentPage = 0;
                getList(currentPage);
            }

            @Override
            public void onStartLoadmore(XtomRefreshLoadmoreLayout v) {
                currentPage++;
                getList(currentPage);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case 11:
                break;
        }
    }


    @OnClick({R.id.title_btn_left, R.id.title_btn_right, R.id.title_text})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_btn_left:
            case R.id.title_text:
                finish();
                break;
            case R.id.title_btn_right:
                break;
        }
    }


}
