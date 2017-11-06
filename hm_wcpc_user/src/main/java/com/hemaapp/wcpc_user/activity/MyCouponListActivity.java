package com.hemaapp.wcpc_user.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.hm_FrameWork.view.RefreshLoadmoreLayout;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.adapter.CouponListAdapter;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.CouponListInfor;
import com.hemaapp.wcpc_user.module.User;

import java.util.ArrayList;

import xtom.frame.util.XtomToastUtil;
import xtom.frame.view.XtomListView;
import xtom.frame.view.XtomRefreshLoadmoreLayout;

/**
 * Created by WangYuxia on 2016/5/17.
 * keytype = "1"：我的优惠券
 * keytype = "2"：可用优惠券
 */
public class MyCouponListActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private RefreshLoadmoreLayout layout;
    private ProgressBar progressBar;
    private XtomListView mListView;

    private int page = 0;
    private ArrayList<CouponListInfor> infors = new ArrayList<>();
    private CouponListAdapter adapter;
    private User user;
    private String keytype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_mycouponlist);
        super.onCreate(savedInstanceState);
        user = hm_WcpcUserApplication.getInstance().getUser();
        getTripsList();
    }

    private void getTripsList(){
       getNetWorker().couponsList(user.getToken(), keytype, page);
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case COUPONS_LIST:
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case COUPONS_LIST:
                progressBar.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);
                break;
            default:
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
            case COUPONS_LIST:
                String page = netTask.getParams().get("page");
                HemaPageArrayResult<CouponListInfor> cResult = (HemaPageArrayResult<CouponListInfor>) baseResult;
                ArrayList<CouponListInfor> cashs = cResult.getObjects();
                if ("0".equals(page)) {// 刷新
                    layout.refreshSuccess();
                    infors.clear();
                    infors.addAll(cashs);

                    hm_WcpcUserApplication application = hm_WcpcUserApplication.getInstance();
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
            default:
                break;
        }
    }

    private void freshData() {
        if (adapter == null) {
            adapter = new CouponListAdapter(mContext, infors);
            adapter.setEmptyString("暂时没有代金券");
            mListView.setAdapter(adapter);
        } else {
            adapter.setEmptyString("暂时没有代金券");
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case COUPONS_LIST:
                String page = netTask.getParams().get("page");
                if ("0".equals(page)) {// 刷新
                    layout.refreshFailed();
                    freshData();
                } else {// 更多
                    layout.loadmoreFailed();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case COUPONS_LIST:
                String page = netTask.getParams().get("page");
                if ("0".equals(page)) {// 刷新
                    layout.refreshFailed();
                    freshData();
                } else {// 更多
                    layout.loadmoreFailed();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        right = (TextView) findViewById(R.id.title_btn_right);
        title = (TextView) findViewById(R.id.title_text);

        layout = (RefreshLoadmoreLayout) findViewById(R.id.refreshLoadmoreLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        mListView = (XtomListView) findViewById(R.id.listview);
    }

    @Override
    protected void getExras() {
        keytype = mIntent.getStringExtra("keytype");
    }

    @Override
    protected void setListener() {
        title.setText("我的代金券");
        right.setVisibility(View.INVISIBLE);
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

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(infors.size()>0 && "2".equals(keytype)){
                    CouponListInfor infor = infors.get(position);
                    mIntent.putExtra("money", infor.getValue());
                    mIntent.putExtra("id", infor.getId());
                    setResult(RESULT_OK, mIntent);
                    finish();
                }
            }
        });

    }
}
