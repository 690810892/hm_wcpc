package com.hemaapp.wcpc_user.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.HemaNetWorker;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.hm_FrameWork.view.RefreshLoadmoreLayout;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.BaseNetTaskExecuteListener;
import com.hemaapp.wcpc_user.BaseNetWorker;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.activity.MyTripsListActivity;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.MyTripsInfor;
import com.hemaapp.wcpc_user.module.User;

import java.util.ArrayList;

import xtom.frame.XtomObject;
import xtom.frame.util.XtomToastUtil;
import xtom.frame.view.XtomListView;
import xtom.frame.view.XtomRefreshLoadmoreLayout;

/**
 * Created by wangyuxia on 2017/9/19.
 * 我的行程的viewpager数据适配器
 */
public class MyTripsViewPagerAdapter extends PagerAdapter {
    private ArrayList<Params> paramsList;
    private MyTripsListActivity mContext;
    private Params currParams;
    private String tabTitles[] = new String[]{"全部", "未出行", "已完成"};
    private int pos;
    private boolean isFrist = true;

    public MyTripsViewPagerAdapter(MyTripsListActivity mContext, ArrayList<Params> paramsList, int position) {
        this.paramsList = paramsList;
        this.mContext = mContext;
        this.pos = position;
    }

    @Override
    public int getCount() {
        return paramsList == null ? 0 : paramsList.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        currParams = paramsList.get(position);
        if (currParams.isFirstSetPrimary) {
            RefreshLoadmoreLayout layout = (RefreshLoadmoreLayout) view
                    .findViewById(R.id.refreshLoadmoreLayout);
            layout.getOnStartListener().onStartRefresh(layout);
            currParams.isFirstSetPrimary = false;
        }
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (isFrist && pos > 0) {
            isFrist = false;
            for (int i = 0; i < pos; i++) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                View view = inflater.inflate(R.layout.pageritem_rllistview_progress,
                        null);
                container.addView(view, i);
            }
        }
        View view = container.getChildAt(position);
        Params params = paramsList.get(position);
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.pageritem_rllistview_progress,
                    null);
            RefreshLoadmoreLayout layout = (RefreshLoadmoreLayout) view
                    .findViewById(R.id.refreshLoadmoreLayout);
            BaseNetWorker netWorker = new BaseNetWorker(mContext);
            netWorker.setOnTaskExecuteListener(new OnTaskExecuteListener(
                    mContext, view, params, netWorker));
            view.setTag(netWorker);
            layout.setOnStartListener(new OnStartListener(params, netWorker,
                    view));
            container.addView(view);
        }else {
            if (params.isFirstSetPrimary) {
                RefreshLoadmoreLayout layout = (RefreshLoadmoreLayout) view
                        .findViewById(R.id.refreshLoadmoreLayout);
                BaseNetWorker netWorker = new BaseNetWorker(mContext);
                netWorker.setOnTaskExecuteListener(new OnTaskExecuteListener(
                        mContext, view, params, netWorker));
                view.setTag(netWorker);
                layout.setOnStartListener(new OnStartListener(params, netWorker,
                        view));
            }
        }
        return view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    private class OnTaskExecuteListener extends BaseNetTaskExecuteListener {
        private XtomRefreshLoadmoreLayout layout;
        private XtomListView listView;
        private ProgressBar progressBar;
        private ArrayList<MyTripsInfor> goodsList = new ArrayList<>();
        private MyTripsListAdapter adapter;
        private MyTripsListActivity activity;
        private BaseNetWorker netWorker;
        private Params params;
        private User user;

        private OnTaskExecuteListener(MyTripsListActivity mContext, View v, Params params,
                                      BaseNetWorker netWorker) {
            super(mContext);
            this.activity = mContext;
            this.layout = (XtomRefreshLoadmoreLayout) v
                    .findViewById(R.id.refreshLoadmoreLayout);
            this.progressBar = (ProgressBar) v.findViewById(R.id.progressbar);
            this.listView = (XtomListView) layout.findViewById(R.id.listview);
            this.netWorker = netWorker;
            this.params = params;
            user = hm_WcpcUserApplication.getInstance().getUser();
        }

        @Override
        public void onPreExecute(HemaNetWorker netWorker, HemaNetTask netTask) {
            BaseHttpInformation information = (BaseHttpInformation) netTask
                    .getHttpInformation();
            switch (information) {
                case MY_TRIPS_LIST:
                    break;
                case MY_TRIPS_OPERATE:
                    activity.showProgressDialog("请稍后...");
                    break;
            }
        }

        @Override
        public void onPostExecute(HemaNetWorker netWorker, HemaNetTask netTask) {
            BaseHttpInformation information = (BaseHttpInformation) netTask
                    .getHttpInformation();
            switch (information) {
                case MY_TRIPS_LIST:
                    progressBar.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                    break;
                case MY_TRIPS_OPERATE:
                    activity.cancelProgressDialog();
                    break;
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onServerSuccess(HemaNetWorker netWorker,
                                    HemaNetTask netTask, HemaBaseResult baseResult) {
            BaseHttpInformation information = (BaseHttpInformation) netTask
                    .getHttpInformation();
            switch (information) {
                case MY_TRIPS_LIST:
                    String page = netTask.getParams().get("page");
                    HemaPageArrayResult<MyTripsInfor> gResult = (HemaPageArrayResult<MyTripsInfor>) baseResult;
                    ArrayList<MyTripsInfor> notices = gResult.getObjects();
                    if ("0".equals(page)) {// 刷新
                        layout.refreshSuccess();
                        goodsList.clear();
                        goodsList.addAll(notices);

                        hm_WcpcUserApplication application = hm_WcpcUserApplication
                                .getInstance();
                        int sysPagesize = application.getSysInitInfo()
                                .getSys_pagesize();
                        if (notices.size() < sysPagesize)
                            layout.setLoadmoreable(false);
                        else
                            layout.setLoadmoreable(true);
                    } else {// 更多
                        layout.loadmoreSuccess();
                        if (notices.size() > 0)
                            goodsList.addAll(notices);
                        else {
                            layout.setLoadmoreable(false);
                            XtomToastUtil.showShortToast(mContext, "已经到最后啦");
                        }
                    }
                    freshData();
                    break;
                case MY_TRIPS_OPERATE:
                    this.netWorker.clientOrderList(user.getToken(), params.type_name, 0);
                    break;
            }
        }

        private void freshData() {
            if (adapter == null) {
                adapter = new MyTripsListAdapter(activity, goodsList, netWorker);
                adapter.setEmptyString("该状态订单您已处理完毕。");
                listView.setAdapter(adapter);
            } else {
                adapter.setEmptyString("该状态订单您已处理完毕。");
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onServerFailed(HemaNetWorker netWorker,
                                   HemaNetTask netTask, HemaBaseResult baseResult) {
            BaseHttpInformation information = (BaseHttpInformation) netTask
                    .getHttpInformation();
            switch (information) {
                case CLIENT_ORDER_LIST:
                    String page = netTask.getParams().get("page");
                    if ("0".equals(page)) {// 刷新
                        layout.refreshFailed();
                        freshData();
                    } else {// 更多
                        layout.loadmoreFailed();
                    }
                    break;
                case MY_TRIPS_OPERATE:
                    activity.showTextDialog(baseResult.getMsg());
                    break;
            }
        }

        @Override
        public void onExecuteFailed(HemaNetWorker netWorker,
                                    HemaNetTask netTask, int failedType) {
            BaseHttpInformation information = (BaseHttpInformation) netTask
                    .getHttpInformation();
            switch (information) {
                case CLIENT_ORDER_LIST:
                    String page = netTask.getParams().get("page");
                    if ("0".equals(page)) {// 刷新
                        layout.refreshFailed();
                        freshData();
                    } else {// 更多
                        layout.loadmoreFailed();
                    }
                    break;
                case MY_TRIPS_OPERATE:
                    activity.showTextDialog("操作失败，请稍后重试");
                    break;
            }
        }
    }

    private class OnStartListener implements
            XtomRefreshLoadmoreLayout.OnStartListener {
        private Integer current_page = 0;
        private Params params;
        private BaseNetWorker netWorker;
        private User user;

        private OnStartListener(Params params, HemaNetWorker netWorker, View v) {
            this.params = params;
            this.netWorker = (BaseNetWorker) netWorker;
            user = hm_WcpcUserApplication.getInstance().getUser();
        }

        @Override
        public void onStartRefresh(XtomRefreshLoadmoreLayout v) {
            current_page = 0;
            netWorker.clientOrderList(user.getToken(), params.type_name, current_page);
        }

        @Override
        public void onStartLoadmore(XtomRefreshLoadmoreLayout v) {
            current_page++;
            netWorker.clientOrderList(user.getToken(), params.type_name, current_page);
        }
    }

    public static class Params extends XtomObject {
        boolean isFirstSetPrimary = true;// 第一次显示时需要自动加载数据
        String type_name;

        public Params(String type_name) {
            super();
            this.type_name = type_name;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}




