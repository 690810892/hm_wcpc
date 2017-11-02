package com.hemaapp.wcpc_driver.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.dialog.HemaButtonDialog;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.hm_FrameWork.view.RefreshLoadmoreLayout;
import com.hemaapp.wcpc_driver.BaseActivity;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.BaseUtil;
import com.hemaapp.wcpc_driver.EventBusModel;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.RecycleUtils;
import com.hemaapp.wcpc_driver.adapter.HistoaryAdapter;
import com.hemaapp.wcpc_driver.adapter.ReplyAdapter;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.CurrentTripsInfor;
import com.hemaapp.wcpc_driver.module.Reply;
import com.hemaapp.wcpc_driver.module.User;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import xtom.frame.util.XtomToastUtil;
import xtom.frame.view.XtomRefreshLoadmoreLayout;

/**
 * 我的评价
 */
public class ReplyListActivity extends BaseActivity {

    @BindView(R.id.title_btn_left)
    ImageView titleBtnLeft;
    @BindView(R.id.title_btn_right)
    TextView titleBtnRight;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.title)
    LinearLayout title;
    @BindView(R.id.rb_level)
    RatingBar rbLevel;
    @BindView(R.id.tv_point)
    TextView tvPoint;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.refreshLoadmoreLayout)
    RefreshLoadmoreLayout refreshLoadmoreLayout;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.empty)
    LinearLayout empty;
    private User user;
    private String token = "";
    private ReplyAdapter adapter;
    private ArrayList<Reply> blogs = new ArrayList<>();
    private Integer currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_reply_list);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        user = hm_WcpcDriverApplication.getInstance().getUser();
        if (user == null)
            token = "";
        else
            token = user.getToken();
        adapter = new ReplyAdapter(mContext, blogs, getNetWorker());
        RecycleUtils.initVerticalRecyle(rvList);
        rvList.setAdapter(adapter);
        getList(currentPage);
        int count = Integer.parseInt(user.getReplycount());
        float point = 0;
        if (count > 0) {
            point = Float.parseFloat(BaseUtil.divide(user.getTotalpoint(), user.getReplycount(), 0));
        }
        rbLevel.setRating(point);
        tvPoint.setText(point + "");
        tvCount.setText(user.getReplycount() + "人评价");
    }

    private void getList(int page) {
        getNetWorker().replyList(token, "1", user.getId(), page);
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
            case REPLY_LIST:
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
            case REPLY_LIST:
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
            case REPLY_LIST:
                String page = netTask.getParams().get("page");
                @SuppressWarnings("unchecked")
                HemaPageArrayResult<Reply> gResult = (HemaPageArrayResult<Reply>) baseResult;
                ArrayList<Reply> goods = gResult.getObjects();
                if (page.equals("0")) {// 刷新
                    refreshLoadmoreLayout.refreshSuccess();
                    this.blogs.clear();
                    this.blogs.addAll(goods);
                    int sysPagesize = getApplicationContext().getSysInitInfo()
                            .getSys_pagesize();
                    if (goods.size() < sysPagesize)
                        refreshLoadmoreLayout.setLoadmoreable(false);
                    else
                        refreshLoadmoreLayout.setLoadmoreable(true);
                } else {// 更多
                    refreshLoadmoreLayout.loadmoreSuccess();
                    if (goods.size() > 0)
                        this.blogs.addAll(goods);
                    else {
                        refreshLoadmoreLayout.setLoadmoreable(false);
                        XtomToastUtil.showShortToast(mContext, "已经到最后啦");
                    }
                }
                if (blogs.size() == 0) {
                    empty.setVisibility(View.VISIBLE);
                } else {
                    empty.setVisibility(View.INVISIBLE);
                }
                adapter.notifyDataSetChanged();
                break;
            case TRIPS_SAVEOPERATE:
                String keytype = netTask.getParams().get("keytype");
                currentPage = 0;
                getList(0);
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
            case REPLY_LIST:
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
            case REPLY_LIST:
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
    }

    @Override
    protected void setListener() {
        titleText.setText("我的评价");
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


    @OnClick({R.id.title_btn_left, R.id.title_btn_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_btn_left:
                finish();
                break;
            case R.id.title_btn_right:
                break;
        }
    }

}
