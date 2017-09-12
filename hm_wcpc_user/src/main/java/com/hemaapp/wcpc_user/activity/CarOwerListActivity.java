package com.hemaapp.wcpc_user.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.hm_FrameWork.view.RefreshLoadmoreLayout;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.adapter.TripListAdapter;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.PopItem;
import com.hemaapp.wcpc_user.module.TripListInfor;
import com.hemaapp.wcpc_user.view.TuiJianPopupMenu;

import java.util.ArrayList;

import xtom.frame.util.XtomSharedPreferencesUtil;
import xtom.frame.util.XtomToastUtil;
import xtom.frame.view.XtomListView;
import xtom.frame.view.XtomRefreshLoadmoreLayout;

/**
 * Created by WangYuxia on 2016/5/6.
 * type = 1:市内车主
 * type = 2:跨城车主
 */
public class CarOwerListActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private LinearLayout tuijian_lay;
    private TextView tuijiantxt;
    private TuiJianPopupMenu tuiJianPopMenu = null;
    private ArrayList<PopItem> tuijianItems;

    private RefreshLoadmoreLayout layout;
    private ProgressBar progressBar;
    private XtomListView mListView;

    private String type, keytype, lng, lat, orderby = "0", district;
    private int page = 0, index = 0;
    private ArrayList<TripListInfor> infors = new ArrayList<>();
    private TripListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_carowerlist);
        super.onCreate(savedInstanceState);
        if("1".equals(type)) {
            title.setText("市内车主");
            keytype = "2";
        } else {
            title.setText("跨城车主");
            keytype = "3";
        }
        lng = XtomSharedPreferencesUtil.get(mContext, "lng");
        lat = XtomSharedPreferencesUtil.get(mContext, "lat");
        district = XtomSharedPreferencesUtil.get(mContext, "district");
        getTripsList();
    }

    private void getTripsList(){
        getNetWorker().tripsList(keytype, lng+","+lat, orderby, page, district);
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case TRIPS_LIST:
                break;
            default:
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case TRIPS_LIST:
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
            case TRIPS_LIST:
                String page = netTask.getParams().get("page");
                HemaPageArrayResult<TripListInfor> cResult = (HemaPageArrayResult<TripListInfor>) baseResult;
                ArrayList<TripListInfor> cashs = cResult.getObjects();
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
            adapter = new TripListAdapter(mContext, infors, mListView);
            adapter.setEmptyString("暂时没有记录");
            mListView.setAdapter(adapter);
        } else {
            adapter.setEmptyString("暂时没有记录");
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case TRIPS_LIST:
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
            case TRIPS_LIST:
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

        tuijian_lay = (LinearLayout) findViewById(R.id.layout);
        tuijiantxt = (TextView) findViewById(R.id.textview);
        layout = (RefreshLoadmoreLayout) findViewById(R.id.refreshLoadmoreLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        mListView = (XtomListView) findViewById(R.id.listview);
    }

    @Override
    protected void getExras() {
        type = mIntent.getStringExtra("type");
    }

    @Override
    protected void setListener() {
        right.setVisibility(View.INVISIBLE);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tuijian_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTuijianPopMenu(v);
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
    }

    private void showTuijianPopMenu(View v) {
        if (View.VISIBLE == progressBar.getVisibility()) {
            return;
        }

        tuijiantxt.setTextColor(mContext.getResources().getColor(R.color.yellow));
        // 是设置Drawable显示在text的左、上、右、下位置。
        tuijiantxt.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                R.mipmap.img_arrow_up, 0);

        if (tuiJianPopMenu == null) {
            tuiJianPopMenu = new TuiJianPopupMenu(this);
            tuijianItems = new ArrayList<>();
            tuijianItems.add(new PopItem("智能筛选", "0", true));
            tuijianItems.add(new PopItem("出发时间最早", "1", false));
            tuijianItems.add(new PopItem("距我最近", "2", true));
            tuijianItems.add(new PopItem("男士优先", "3", false));
            tuijianItems.add(new PopItem("女士优先", "4", false));
        }

        tuiJianPopMenu.setitems(tuijianItems, index);
        tuiJianPopMenu.setlistviewclick(popItemClickListener2);
        tuiJianPopMenu.setondismisslisener(new OnDismissListener());
        tuiJianPopMenu.showAsDropDown(v);
    }

    private AdapterView.OnItemClickListener popItemClickListener2 = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            PopItem item = (PopItem) parent.getItemAtPosition(position);
            tuijiantxt.setText(item.name);
            if ("智能筛选".equals(item.name)) {
                orderby = "0";
            } else if ("出发时间最早".equals(item.name)) {
                orderby = "1";
            } else if ("距我最近".equals(item.name)) {
                orderby = "2";
            } else if ("男士优先".equals(item.name)) {
                orderby = "3";
            } else if ("女士优先".equals(item.name)) {
                orderby = "4";
            }
            for (PopItem it : tuijianItems)
                it.checked = false;

            tuiJianPopMenu.dimiss();
            page = 0;
            getTripsList();
            progressBar.setVisibility(View.VISIBLE);
            layout.setVisibility(View.INVISIBLE);
            item.checked = true;
            index = position;
        }
    };

    private class OnDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            tuijiantxt.setTextColor(0xff616161);
            tuijiantxt.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.mipmap.img_arrow_down, 0);
        }
    }
}
