package com.hemaapp.wcpc_driver.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
import com.hemaapp.wcpc_driver.adapter.HistoryOrderListAdapter;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.OrderListInfor;
import com.hemaapp.wcpc_driver.module.User;

import java.util.ArrayList;

import xtom.frame.util.XtomToastUtil;
import xtom.frame.view.XtomListView;
import xtom.frame.view.XtomRefreshLoadmoreLayout;

/**
 * Created by WangYuxia on 2016/5/27.
 */
public class HistoryOrderActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private ProgressBar progressBar;
    private TextView text_0, text_1, text_2, text_3;
    private ImageView image_0, image_1, image_2, image_3;
    private RefreshLoadmoreLayout layout_0, layout_1, layout_2, layout_3;
    private XtomListView mListView_0, mListView_1, mListView_2, mListView_3;
    private ArrayList<TextView> texts_entity = new ArrayList<>();
    private ArrayList<ImageView> images_entity = new ArrayList<>();
    private ArrayList<RefreshLoadmoreLayout> layouts_entity = new ArrayList<>();
    private ArrayList<XtomListView> listviews_entity = new ArrayList<>();
    private ArrayList<Boolean> isFirst_entity = new ArrayList<>();
    private ArrayList<OrderListInfor> goods_0, goods_1, goods_2, goods_3;
    private HistoryOrderListAdapter adapter_0, adapter_1, adapter_2, adapter_3;
    private ArrayList<Integer> page_entity = new ArrayList<>();

    private int position = 0;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_historyorder);
        super.onCreate(savedInstanceState);
        user = hm_WcpcDriverApplication.getInstance().getUser();
        setEntityVisibility(position);
        initGoods();
        isFirst_entity.set(position, false);
        getOrderList(0);
    }

    private void getOrderList(int page){
        getNetWorker().driverOrderList(user.getToken(), String.valueOf(position), page);
    }

    private void initGoods(){
        goods_0 = new ArrayList<>();
        goods_1 = new ArrayList<>();
        goods_2 = new ArrayList<>();
        goods_3 = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            isFirst_entity.add(i, true);
            page_entity.add(i, 0);
        }
    }

    private void setEntityVisibility(int position){
        for(int i = 0; i<texts_entity.size(); i++){
            if(i == position){
                texts_entity.get(i).setTextColor(mContext.getResources().getColor(R.color.yellow));
                images_entity.get(i).setVisibility(View.VISIBLE);
                layouts_entity.get(i).setVisibility(View.VISIBLE);
            }else {
                texts_entity.get(i).setTextColor(mContext.getResources().getColor(R.color.shenhui));
                images_entity.get(i).setVisibility(View.INVISIBLE);
                layouts_entity.get(i).setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case DRIVER_ORDER_LIST:
                break;
            case ORDER_OPERATE:
                showProgressDialog("请稍后...");
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int type) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case DRIVER_ORDER_LIST:
                String page = netTask.getParams().get("page");
                if ("0".equals(page)) {// 刷新
                    layouts_entity.get(position).refreshFailed();
                    freshData();
                } else {// 更多
                    layouts_entity.get(position).loadmoreFailed();
                }
                break;
            case ORDER_OPERATE:
                showTextDialog("操作失败，请稍后重试");
                break;
            default:
                break;
        }
    }
    @SuppressWarnings("unchecked")
    @Override
    protected void callBackForServerSuccess(HemaNetTask netTask,
                                            HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case DRIVER_ORDER_LIST:
                String page = netTask.getParams().get("page");
                HemaPageArrayResult<OrderListInfor> cResult = (HemaPageArrayResult<OrderListInfor>) baseResult;
                ArrayList<OrderListInfor> cashs = cResult.getObjects();
                if ("0".equals(page)) {// 刷新
                    layouts_entity.get(position).refreshSuccess();
                    switch (position) {
                        case 0:
                            goods_0.clear();
                            goods_0.addAll(cashs);
                            break;
                        case 1:
                            goods_1.clear();
                            goods_1.addAll(cashs);
                            break;
                        case 2:
                            goods_2.clear();
                            goods_2.addAll(cashs);
                            break;
                        case 3:
                            goods_3.clear();
                            goods_3.addAll(cashs);
                            break;
                    }

                    hm_WcpcDriverApplication application = hm_WcpcDriverApplication.getInstance();
                    int sysPagesize = application.getSysInitInfo()
                            .getSys_pagesize();
                    if (cashs.size() < sysPagesize)
                        layouts_entity.get(position).setLoadmoreable(false);
                    else
                        layouts_entity.get(position).setLoadmoreable(true);
                } else {// 更多
                    layouts_entity.get(position).loadmoreSuccess();
                    if (cashs.size() > 0)
                        switch (position) {
                            case 0:
                                goods_0.addAll(cashs);
                                break;
                            case 1:
                                goods_1.addAll(cashs);
                                break;
                            case 2:
                                goods_2.addAll(cashs);
                                break;
                            case 3:
                                goods_3.addAll(cashs);
                                break;
                        }
                    else {
                        layouts_entity.get(position).setLoadmoreable(false);
                        XtomToastUtil.showShortToast(mContext, "已经到最后啦");
                    }
                }
                freshData();
                break;
            case ORDER_OPERATE:
                showTextDialog(baseResult.getMsg());
                String type = netTask.getParams().get("keytype");
                switch (position) {
                    case 0:
                        if("4".equals(type)){
                            goods_0.remove(adapter_0.order);
                            adapter_0.notifyDataSetChanged();
                        }else {
                            deleteall();
                            getOrderList(page_entity.get(position));
                        }
                        break;
                    case 1:
                        if("4".equals(type)){
                            goods_1.remove(adapter_1.order);
                            adapter_1.notifyDataSetChanged();
                        }else {
                            deleteall();
                            getOrderList(page_entity.get(position));
                        }
                        break;
                    case 2:
                        if("4".equals(type)){
                            goods_2.remove(adapter_2.order);
                            adapter_2.notifyDataSetChanged();
                        }else {
                            deleteall();
                            getOrderList(page_entity.get(position));
                        }
                        break;
                    case 3:
                        if("4".equals(type)){
                            goods_3.remove(adapter_3.order);
                            adapter_3.notifyDataSetChanged();
                        }else {
                            deleteall();
                            getOrderList(page_entity.get(position));
                        }
                        break;
                }
                break;
        }
    }

    private void deleteall(){
        goods_0.clear();
        goods_1.clear();
        goods_2.clear();
        goods_3.clear();
        page_entity.set(0, 0);
        page_entity.set(1, 0);
        page_entity.set(2, 0);
        page_entity.set(3, 0);
    }

    private void freshData(){
        switch (position) {
            case 0:
                if(adapter_0 == null){
                    adapter_0 = new HistoryOrderListAdapter(mContext, goods_0, mListView_0);
                    adapter_0.setEmptyString("抱歉，暂时没有数据");
                    mListView_0.setAdapter(adapter_0);
                }else {
                    adapter_0.setEmptyString("抱歉，暂时没有数据");
                    adapter_0.notifyDataSetChanged();
                }
                break;
            case 1:
                if(adapter_1 == null){
                    adapter_1 = new HistoryOrderListAdapter(mContext, goods_1, mListView_1);
                    adapter_1.setEmptyString("抱歉，暂时没有数据");
                    mListView_1.setAdapter(adapter_1);
                }else {
                    adapter_1.setEmptyString("抱歉，暂时没有数据");
                    adapter_1.notifyDataSetChanged();
                }
                break;
            case 2:
                if(adapter_2 == null){
                    adapter_2 = new HistoryOrderListAdapter(mContext, goods_2, mListView_2);
                    adapter_2.setEmptyString("抱歉，暂时没有数据");
                    mListView_2.setAdapter(adapter_2);
                }else {
                    adapter_2.setEmptyString("抱歉，暂时没有数据");
                    adapter_2.notifyDataSetChanged();
                }
                break;
            case 3:
                if(adapter_3 == null){
                    adapter_3 = new HistoryOrderListAdapter(mContext, goods_3, mListView_3);
                    adapter_3.setEmptyString("抱歉，暂时没有数据");
                    mListView_3.setAdapter(adapter_3);
                }else {
                    adapter_3.setEmptyString("抱歉，暂时没有数据");
                    adapter_3.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case DRIVER_ORDER_LIST:
                progressBar.setVisibility(View.GONE);
                break;
            case ORDER_OPERATE:
                cancelProgressDialog();
                break;
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        right = (TextView) findViewById(R.id.title_btn_right);
        title = (TextView) findViewById(R.id.title_text);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        text_0 = (TextView) findViewById(R.id.textview_0);
        image_0 = (ImageView) findViewById(R.id.imageview_0);
        text_1 = (TextView) findViewById(R.id.textview_1);
        image_1 = (ImageView) findViewById(R.id.imageview_1);
        text_2 = (TextView) findViewById(R.id.textview_2);
        image_2 = (ImageView) findViewById(R.id.imageview_2);
        text_3 = (TextView) findViewById(R.id.textview_3);
        image_3 = (ImageView) findViewById(R.id.imageview_3);
        texts_entity.add(0, text_0);
        texts_entity.add(1, text_1);
        texts_entity.add(2, text_2);
        texts_entity.add(3, text_3);

        images_entity.add(0, image_0);
        images_entity.add(1, image_1);
        images_entity.add(2, image_2);
        images_entity.add(3, image_3);

        layout_0 = (RefreshLoadmoreLayout) findViewById(R.id.refreshLoadmoreLayout_0);
        mListView_0 = (XtomListView) findViewById(R.id.listview);
        layout_1 = (RefreshLoadmoreLayout) findViewById(R.id.refreshLoadmoreLayout_1);
        mListView_1 = (XtomListView) findViewById(R.id.listview1);
        layout_2 = (RefreshLoadmoreLayout) findViewById(R.id.refreshLoadmoreLayout_2);
        mListView_2 = (XtomListView) findViewById(R.id.listview2);
        layout_3 = (RefreshLoadmoreLayout) findViewById(R.id.refreshLoadmoreLayout_3);
        mListView_3 = (XtomListView) findViewById(R.id.listview3);
        layouts_entity.add(0, layout_0);
        layouts_entity.add(1, layout_1);
        layouts_entity.add(2, layout_2);
        layouts_entity.add(3, layout_3);
        listviews_entity.add(0, mListView_0);
        listviews_entity.add(1, mListView_1);
        listviews_entity.add(2, mListView_2);
        listviews_entity.add(3, mListView_3);
    }


    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
        title.setText("历史订单");
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
                delete(0);
            }
        });

        text_0.setOnClickListener(getOnClickListener(0));
        text_1.setOnClickListener(getOnClickListener(1));
        text_2.setOnClickListener(getOnClickListener(2));
        text_3.setOnClickListener(getOnClickListener(3));

        layout_0.setOnStartListener(getOnStartListener(0));
        layout_1.setOnStartListener(getOnStartListener(1));
        layout_2.setOnStartListener(getOnStartListener(2));
        layout_3.setOnStartListener(getOnStartListener(3));
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
            mDialog.setText("确定要清空所有订单?");
            mDialog.setButtonListener(new ButtonListener(type));
        }else{
            mDialog.setText("确定要删除当前订单?");
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
            if(type == 0)
                getNetWorker().orderOperate(user.getToken(), "3", "0", "", "");
            else{
                if(position == 0){
                    getNetWorker().orderOperate(user.getToken(), "1", adapter_0.order.getId(), "", "");
                }else if(position == 1){
                    getNetWorker().orderOperate(user.getToken(), "1", adapter_1.order.getId(), "", "");
                }else if(position == 2){
                    getNetWorker().orderOperate(user.getToken(), "1", adapter_2.order.getId(), "", "");
                }else if(position == 3){
                    getNetWorker().orderOperate(user.getToken(), "1", adapter_3.order.getId(), "", "");
                }
            }
        }
    }


    private XtomRefreshLoadmoreLayout.OnStartListener getOnStartListener(final int position){
        return new XtomRefreshLoadmoreLayout.OnStartListener() {

            @Override
            public void onStartRefresh(XtomRefreshLoadmoreLayout arg0) {
                page_entity.set(position, 0);
                getOrderList(page_entity.get(position));
            }

            @Override
            public void onStartLoadmore(XtomRefreshLoadmoreLayout arg0) {
                int page = page_entity.get(position);
                page++;
                page_entity.set(position, page);
                getOrderList(page_entity.get(position));
            }
        };
    }

    private View.OnClickListener getOnClickListener(final int pos){

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (pos) {
                    case 0: //全部订单
                        position = 0;
                        setEntityVisibility(0);
                        if(isFirst_entity.get(0)){
                            isFirst_entity.set(0, false);
                            progressBar.setVisibility(View.VISIBLE);
                            getOrderList(0);
                        }
                        break;
                    case 1: //已取消
                        position = 1;
                        setEntityVisibility(1);
                        if(isFirst_entity.get(1)){
                            isFirst_entity.set(1, false);
                            progressBar.setVisibility(View.VISIBLE);
                            getOrderList(0);
                        }
                        break;
                    case 2: //待评价
                        position = 2;
                        setEntityVisibility(2);
                        if(isFirst_entity.get(2)){
                            isFirst_entity.set(2, false);
                            progressBar.setVisibility(View.VISIBLE);
                            getOrderList(0);
                        }
                        break;
                    case 3: //已完成
                        position = 3;
                        setEntityVisibility(3);
                        if(isFirst_entity.get(3)){
                            isFirst_entity.set(3, false);
                            progressBar.setVisibility(View.VISIBLE);
                            getOrderList(0);
                        }
                        break;
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case R.id.layout://去支付
            case R.id.layout_0: //去订单详情界面返回的数据;
            case R.id.layout_1:
                if(position == 0){ //全部订单
                    page_entity.set(0, 0);
                    getOrderList(0);
                }else if(position == 1){
                    page_entity.set(1, 0);
                    getOrderList(0);
                }else if(position == 2){
                    page_entity.set(2, 0);
                    getOrderList(0);
                }else if(position == 3){
                    page_entity.set(3, 0);
                    getOrderList(0);
                }

                for(int i = 0; i < 4; i++){
                    isFirst_entity.set(i, true);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}