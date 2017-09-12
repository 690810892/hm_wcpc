package com.hemaapp.wcpc_driver.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.dialog.HemaButtonDialog;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.hm_FrameWork.view.RefreshLoadmoreLayout;
import com.hemaapp.wcpc_driver.BaseActivity;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.adapter.MyTripsListAdapter;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.MyTripsInfor;
import com.hemaapp.wcpc_driver.module.User;

import java.util.ArrayList;

import xtom.frame.util.XtomToastUtil;
import xtom.frame.view.XtomListView;
import xtom.frame.view.XtomRefreshLoadmoreLayout;

/**
 * Created by WangYuxia on 2016/5/27.
 */
public class MyTripsActivity extends BaseActivity {
    private ImageView left;
    private TextView title;
    private TextView right;

    private LinearLayout layout_all, layout_togo, layout_complete;
    private TextView text_all, text_togo, text_complete;
    private ImageView image_all, image_togo, image_complete;

    private RefreshLoadmoreLayout allLayout, togoLayout, completeLayout;
    private XtomListView allList, togoList, completeList;
    private ProgressBar progressBar;

    private ArrayList<MyTripsInfor> allInfors = new ArrayList<>();
    private ArrayList<MyTripsInfor> togoInfors = new ArrayList<>();
    private ArrayList<MyTripsInfor> completeInfors = new ArrayList<>();

    private String keytype = "0"; //0:全部， 1: 未出行，2：已完成
    private int page_all = 0, page_togo=0, page_complete=0;
    private MyTripsListAdapter adapter_all, adapter_togo, adapter_complete;

    private User user;
    private ArrayList<LinearLayout> layouts = new ArrayList<>();
    private ArrayList<ImageView> images = new ArrayList<>();
    private ArrayList<TextView> texts = new ArrayList<>();

    private ArrayList<RefreshLoadmoreLayout> reLayouts = new ArrayList<>();
    private boolean isNeedFresh_0 = false, isNeedFresh_1 = false, isNeedFresh_2 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_mytrips);
        super.onCreate(savedInstanceState);
        user = hm_WcpcDriverApplication.getInstance().getUser();
        init();
        getTripList(page_all);
    }

    private void init(){
        layouts.add(0, layout_all);
        layouts.add(1, layout_togo);
        layouts.add(2, layout_complete);

        images.add(0, image_all);
        images.add(1, image_togo);
        images.add(2, image_complete);

        texts.add(0, text_all);
        texts.add(1, text_togo);
        texts.add(2, text_complete);

        reLayouts.add(0, allLayout);
        reLayouts.add(1, togoLayout);
        reLayouts.add(2, completeLayout);
    }

    private void getTripList(int page){
        getNetWorker().myTripsList(user.getToken(), keytype, "2", page);
    }

    private HemaButtonDialog mDialog;

    public void delete(int type){
        if (mDialog == null) {
            mDialog = new HemaButtonDialog(mContext);
            mDialog.setLeftButtonText("取消");
            mDialog.setRightButtonText("确定");
            mDialog.setRightButtonTextColor(mContext.getResources().getColor(R.color.yellow));
        }
        if(type == 0){
            mDialog.setText("确定要删除当前行程?");
            mDialog.setButtonListener(new ButtonListener(type));
        }else if(type == 1){
            mDialog.setText("确定要清空所有行程?");
            mDialog.setButtonListener(new ButtonListener(type));
        }else if(type == 2){
            mDialog.setText("确定要取消当前行程?");
            mDialog.setButtonListener(new ButtonListener(type));
        }
        mDialog.show();
    }

    private class ButtonListener implements HemaButtonDialog.OnButtonListener {
        private int type;

        public ButtonListener(int type){
            this.type = type;
        }

        @Override
        public void onLeftButtonClick(HemaButtonDialog dialog) {
            dialog.cancel();
        }

        @Override
        public void onRightButtonClick(HemaButtonDialog dialog) {
            dialog.cancel();
            User user = hm_WcpcDriverApplication.getInstance().getUser();
            if(type == 0){ //删除
                String order_id ="0";
                if("0".equals(keytype))
                    order_id = adapter_all.deleteinfor.getId();
                else if("1".equals(keytype))
                    order_id = adapter_togo.deleteinfor.getId();
                else if("2".equals(keytype))
                    order_id = adapter_complete.deleteinfor.getId();

                getNetWorker().myTripsOperate(user.getToken(), "2", "1", order_id);
            }else if(type == 1){ //清空
                getNetWorker().myTripsOperate(user.getToken(), "2", "2", "0");
            }else if(type == 2){ //取消
                String order_id ="0";
                if("0".equals(keytype))
                    order_id = adapter_all.deleteinfor.getId();
                else if("1".equals(keytype))
                    order_id = adapter_togo.deleteinfor.getId();
                else if("2".equals(keytype))
                    order_id = adapter_complete.deleteinfor.getId();

                getNetWorker().myTripsOperate(user.getToken(), "2", "1", order_id);
            }
        }
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case MY_TRIPS_LIST:
                break;
            case MY_TRIPS_OPERATE:
                showProgressDialog("请稍后...");
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case MY_TRIPS_LIST:
                progressBar.setVisibility(View.GONE);
                setVisible();
                break;
            case MY_TRIPS_OPERATE:
                cancelProgressDialog();
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
            case MY_TRIPS_LIST:
                String page = netTask.getParams().get("page");
                HemaPageArrayResult<MyTripsInfor> cResult = (HemaPageArrayResult<MyTripsInfor>) baseResult;
                ArrayList<MyTripsInfor> cashs = cResult.getObjects();
                if ("0".equals(page)) {// 刷新
                    setRefresh(cashs);

                    hm_WcpcDriverApplication application = hm_WcpcDriverApplication.getInstance();
                    int sysPagesize = application.getSysInitInfo()
                            .getSys_pagesize();
                    if (cashs.size() < sysPagesize)
                        setLoadmore(false);
                    else
                        setLoadmore(true);
                } else {// 更多
                    loadSuccess(0);
                    if (cashs.size() > 0)
                        addAll(cashs);
                    else {
                        setLoadmore(false);
                        XtomToastUtil.showShortToast(mContext, "已经到最后啦");
                    }
                }
                freshData();
                break;
            case MY_TRIPS_OPERATE:
                String type = netTask.getParams().get("keytype");
                if("0".equals(keytype)){
                    if("1".equals(type)){
                        allInfors.remove(adapter_all.deleteinfor);
                        isNeedFresh_0 = true;
                        isNeedFresh_1 = true;
                        isNeedFresh_2 = true;
                    } else
                        allInfors.clear();
                    adapter_all.notifyDataSetChanged();
                }else if("1".equals(keytype)){
                    if("1".equals(type)) {
                        togoInfors.remove(adapter_togo.deleteinfor);
                        isNeedFresh_0 = true;
                        isNeedFresh_1 = true;
                        isNeedFresh_2 = true;
                    } else
                        togoInfors.clear();
                    adapter_togo.notifyDataSetChanged();
                }else{
                    if("1".equals(type)) {
                        completeInfors.remove(adapter_complete.deleteinfor);
                        isNeedFresh_0 = true;
                        isNeedFresh_1 = true;
                        isNeedFresh_2 = true;
                    } else
                        completeInfors.clear();
                    adapter_complete.notifyDataSetChanged();
                }
                break;
        }
    }

    private void freshData() {
        if("0".equals(keytype)){
            if (adapter_all == null) {
                adapter_all = new MyTripsListAdapter(mContext, allInfors);
                adapter_all.setEmptyString("暂时没有数据");
                allList.setAdapter(adapter_all);
            } else {
                adapter_all.setEmptyString("暂时没有数据");
                adapter_all.notifyDataSetChanged();
            }
        }else if("1".equals(keytype)){
            if (adapter_togo == null) {
                adapter_togo = new MyTripsListAdapter(mContext, togoInfors);
                adapter_togo.setEmptyString("暂时没有数据");
                togoList.setAdapter(adapter_togo);
            } else {
                adapter_togo.setEmptyString("暂时没有数据");
                adapter_togo.notifyDataSetChanged();
            }
        }else if("2".equals(keytype)){
            if (adapter_complete == null) {
                adapter_complete = new MyTripsListAdapter(mContext, completeInfors);
                adapter_complete.setEmptyString("暂时没有数据");
                completeList.setAdapter(adapter_complete);
            } else {
                adapter_complete.setEmptyString("暂时没有数据");
                adapter_complete.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case MY_TRIPS_LIST:
                String page = netTask.getParams().get("page");
                if ("0".equals(page)) {// 刷新
                    refreshFiald();
                    freshData();
                } else {// 更多
                    loadSuccess(1);
                }
                break;
            case MY_TRIPS_OPERATE:
                showTextDialog(baseResult.getMsg());
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case MY_TRIPS_LIST:
                String page = netTask.getParams().get("page");
                if ("0".equals(page)) {// 刷新
                    refreshFiald();
                    freshData();
                } else {// 更多
                    loadSuccess(1);
                }
                break;
            case MY_TRIPS_OPERATE:
                showTextDialog("删除失败，请稍后重试");
                break;
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        right = (TextView) findViewById(R.id.title_btn_right);
        title = (TextView) findViewById(R.id.title_text);

        layout_all = (LinearLayout) findViewById(R.id.layout_0);
        image_all = (ImageView) findViewById(R.id.imageview_0);
        text_all = (TextView) findViewById(R.id.textview_0);
        layout_togo = (LinearLayout) findViewById(R.id.layout_1);
        image_togo = (ImageView) findViewById(R.id.imageview_1);
        text_togo = (TextView) findViewById(R.id.textview_1);
        layout_complete = (LinearLayout) findViewById(R.id.layout_2);
        image_complete = (ImageView) findViewById(R.id.imageview_2);
        text_complete = (TextView) findViewById(R.id.textview_2);

        allLayout = (RefreshLoadmoreLayout) findViewById(R.id.refreshLoadmoreLayout);
        allList = (XtomListView) findViewById(R.id.listview);
        togoLayout = (RefreshLoadmoreLayout) findViewById(R.id.refreshLoadmoreLayout1);
        togoList = (XtomListView) findViewById(R.id.listview1);
        completeLayout = (RefreshLoadmoreLayout) findViewById(R.id.refreshLoadmoreLayout2);
        completeList = (XtomListView) findViewById(R.id.listview2);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
    }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
        title.setText("我发布的行程");
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        right.setText("清空");
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(1);
            }
        });

        layout_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keytype = "0";
                setBackGround(0);
                if(isNeedFresh_0){
                    isNeedFresh_0 = false;
                    page_all = 0;
                    getTripList(page_all);
                }
            }
        });
        layout_togo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keytype = "1";
                setBackGround(1);
                if(togoInfors == null || togoInfors.size() == 0){
                    progressBar.setVisibility(View.VISIBLE);
                    getTripList(page_togo);
                }else if(isNeedFresh_1){
                    isNeedFresh_1 = false;
                    page_togo = 0;
                    getTripList(page_togo);
                }
            }
        });

        layout_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keytype = "2";
                setBackGround(2);
                if(completeInfors == null || completeInfors.size() == 0){
                    progressBar.setVisibility(View.VISIBLE);
                    getTripList(page_togo);
                }else if(isNeedFresh_2){
                    isNeedFresh_2 = false;
                    page_complete = 0;
                    getTripList(page_complete);
                }
            }
        });
        allLayout.setOnStartListener(new XtomRefreshLoadmoreLayout.OnStartListener() {
            @Override
            public void onStartRefresh(XtomRefreshLoadmoreLayout xtomRefreshLoadmoreLayout) {
                page_all = 0;
                getTripList(page_all);
            }

            @Override
            public void onStartLoadmore(XtomRefreshLoadmoreLayout xtomRefreshLoadmoreLayout) {
                page_all ++;
                getTripList(page_all);
            }
        });

        togoLayout.setOnStartListener(new XtomRefreshLoadmoreLayout.OnStartListener() {
            @Override
            public void onStartRefresh(XtomRefreshLoadmoreLayout xtomRefreshLoadmoreLayout) {
                page_togo = 0;
                getTripList(page_togo);
            }

            @Override
            public void onStartLoadmore(XtomRefreshLoadmoreLayout xtomRefreshLoadmoreLayout) {
                page_togo ++;
                getTripList(page_togo);
            }
        });

        completeLayout.setOnStartListener(new XtomRefreshLoadmoreLayout.OnStartListener() {
            @Override
            public void onStartRefresh(XtomRefreshLoadmoreLayout xtomRefreshLoadmoreLayout) {
                page_complete = 0;
                getTripList(page_complete);
            }

            @Override
            public void onStartLoadmore(XtomRefreshLoadmoreLayout xtomRefreshLoadmoreLayout) {
                page_complete ++;
                getTripList(page_complete);
            }
        });
    }

    private void setBackGround(int position){
        for(int i = 0; i< layouts.size(); i++){
            if(i == position){
                texts.get(i).setTextColor(mContext.getResources().getColor(R.color.yellow));
                images.get(i).setVisibility(View.VISIBLE);
                reLayouts.get(i).setVisibility(View.VISIBLE);
            }else{
                texts.get(i).setTextColor(mContext.getResources().getColor(R.color.shenhui));
                images.get(i).setVisibility(View.INVISIBLE);
                reLayouts.get(i).setVisibility(View.GONE);
            }
        }
    }

    private void setVisible(){
        int type = Integer.parseInt(keytype);
        for(int i = 0; i< reLayouts.size(); i++){
            if(i == type)
                reLayouts.get(i).setVisibility(View.VISIBLE);
            else
                reLayouts.get(i).setVisibility(View.GONE);
        }
    }

    private void setRefresh(ArrayList<MyTripsInfor> infors){
        int type = Integer.parseInt(keytype);
        for(int i = 0; i< reLayouts.size(); i++){
            if(i == type){
                reLayouts.get(i).refreshSuccess();
                if(type == 0){
                    allInfors.clear();
                    allInfors.addAll(infors);
                }else if(type == 1){
                    togoInfors.clear();
                    togoInfors.addAll(infors);
                }else{
                    completeInfors.clear();
                    completeInfors.addAll(infors);
                }
            }
        }
    }

    private void setLoadmore(boolean result){
        int type = Integer.parseInt(keytype);
        for(int i = 0; i< reLayouts.size(); i++){
            if(i == type)
                reLayouts.get(i).setLoadmoreable(result);
        }
    }

    private void loadSuccess(int flag){
        int type = Integer.parseInt(keytype);
        for(int i = 0; i< reLayouts.size(); i++){
            if(i == type){
                if(flag == 0){
                    reLayouts.get(i).loadmoreSuccess();
                }else{
                    reLayouts.get(i).loadmoreFailed();
                }
            }
        }
    }

    private void addAll(ArrayList<MyTripsInfor> infors){
        int type = Integer.parseInt(keytype);
        if(type == 0)
            allInfors.addAll(infors);
        else if(type == 1)
            togoInfors.addAll(infors);
        else if(type == 2)
            completeInfors.addAll(infors);
    }

    private void refreshFiald(){
        int type = Integer.parseInt(keytype);
        for(int i = 0; i< reLayouts.size(); i++){
            if(i == type)
                reLayouts.get(i).refreshFailed();
        }
    }
}
