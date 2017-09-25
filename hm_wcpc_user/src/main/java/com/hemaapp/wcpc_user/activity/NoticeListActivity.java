package com.hemaapp.wcpc_user.activity;

import android.content.Intent;
import android.os.Bundle;
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
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.adapter.NoticeListAdapter;
import com.hemaapp.wcpc_user.adapter.OrderListAdapter;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.NoticeListInfor;
import com.hemaapp.wcpc_user.module.User;

import java.util.ArrayList;

import xtom.frame.util.XtomToastUtil;
import xtom.frame.view.XtomListView;
import xtom.frame.view.XtomRefreshLoadmoreLayout;

/**
 * Created by WangYuxia on 2016/5/6.
 * 消息列表
 */
public class NoticeListActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private LinearLayout order;
    private TextView text_order;
    private ImageView image_order;
    private ImageView image_point;

    private LinearLayout system;
    private TextView text_system;
    private ImageView image_system;

    private RefreshLoadmoreLayout layout_order; //订单
    private XtomListView list_order;
    private RefreshLoadmoreLayout layout_system; //系统消息
    private XtomListView list_system;
    private ProgressBar progressBar;

    private String flag = "1"; //flag = 1:订单消息，flag = 2：系统消息
    private ArrayList<NoticeListInfor> orders = new ArrayList<>();
    private int page_order = 0;
    private NoticeListAdapter adapter_order;
    private ArrayList<NoticeListInfor> notices = new ArrayList<>();
    private OrderListAdapter adapter_notice;
    private int page_notice = 0;
    private boolean isFrist = true;

    private User user;
    private int count_order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_noticelist);
        super.onCreate(savedInstanceState);
        user = hm_WcpcUserApplication.getInstance().getUser();
        getNoticeUnread();
    }

    private void getNoticeUnread(){
        getNetWorker().noticeUnread(user.getToken(), "2", "1");
    }

    private void getNoticeList(String keytype, int page){
        getNetWorker().noticeList(user.getToken(), keytype, "1", page);
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case NOTICE_LIST:
                break;
            case NOTICE_UNREAD:
                break;
            case NOTICE_SAVEOPERATE:
                showProgressDialog("请稍后...");
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
            case NOTICE_UNREAD:
                break;
            case NOTICE_LIST:
                progressBar.setVisibility(View.GONE);
                if("1".equals(flag)){
                    layout_order.setVisibility(View.VISIBLE);
                    layout_system.setVisibility(View.GONE);
                }else{
                    layout_order.setVisibility(View.GONE);
                    layout_system.setVisibility(View.VISIBLE);
                }
                break;
            case NOTICE_SAVEOPERATE:
                cancelProgressDialog();
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
            case NOTICE_UNREAD:
                HemaArrayResult<String> sResult = (HemaArrayResult<String>) baseResult;
                count_order = Integer.parseInt(isNull(sResult.getObjects().get(0))?"0": sResult.getObjects().get(0));
                if(count_order == 0)
                    image_point.setVisibility(View.INVISIBLE);
                else{
                    image_point.setVisibility(View.VISIBLE);
                }
                getNoticeList("2", page_order);
                break;
            case NOTICE_LIST:
                String page = netTask.getParams().get("page");
                String keytype = netTask.getParams().get("keytype");
                HemaPageArrayResult<NoticeListInfor> cResult = (HemaPageArrayResult<NoticeListInfor>) baseResult;
                ArrayList<NoticeListInfor> cashs = cResult.getObjects();
                hm_WcpcUserApplication application = hm_WcpcUserApplication.getInstance();
                int sysPagesize = application.getSysInitInfo()
                        .getSys_pagesize();

                if ("0".equals(page)) {// 刷新
                    if("2".equals(keytype)){
                        layout_order.refreshSuccess();
                        orders.clear();
                        orders.addAll(cashs);

                        if (cashs.size() < sysPagesize)
                            layout_order.setLoadmoreable(false);
                        else
                            layout_order.setLoadmoreable(true);
                    }else{
                        layout_system.refreshSuccess();
                        notices.clear();
                        notices.addAll(cashs);

                        if (cashs.size() < sysPagesize)
                            layout_system.setLoadmoreable(false);
                        else
                            layout_system.setLoadmoreable(true);
                    }

                } else {// 更多
                    if("2".equals(keytype)){
                        layout_order.loadmoreSuccess();
                        if (cashs.size() > 0)
                            orders.addAll(cashs);
                        else {
                            layout_order.setLoadmoreable(false);
                            XtomToastUtil.showShortToast(mContext, "已经到最后啦");
                        }
                    }else{
                        layout_system.loadmoreSuccess();
                        if (cashs.size() > 0)
                            notices.addAll(cashs);
                        else {
                            layout_system.setLoadmoreable(false);
                            XtomToastUtil.showShortToast(mContext, "已经到最后啦");
                        }
                    }
                }
                freshData();
                break;
            case NOTICE_SAVEOPERATE:
                if("1".equals(flag)){
                    String operatetype = netTask.getParams().get("operatetype");
                    if("3".equals(operatetype)){
                        orders.remove(adapter_order.deleteinfor);
                        adapter_order.notifyDataSetChanged();
                    }else if("1".equals(operatetype)){
                        adapter_order.deleteinfor.setLooktype("2");
                        adapter_order.notifyDataSetChanged();
                    }else if("4".equals(operatetype)){
                        getNoticeUnread();
                    }
                }else{
                    String operatetype = netTask.getParams().get("operatetype");
                    if("3".equals(operatetype)){
                        notices.remove(adapter_notice.deleteinfor);
                        adapter_notice.notifyDataSetChanged();
                    }else if("4".equals(operatetype)){
                        notices.clear();
                        adapter_notice.notifyDataSetChanged();
                    }
                }
                break;
        }
    }

    private void freshData() {
        if("1".equals(flag)){
            if (adapter_order == null) {
                adapter_order = new NoticeListAdapter(mContext, orders);
                adapter_order.setEmptyString("暂时没有记录");
                list_order.setAdapter(adapter_order);
            } else {
                adapter_order.setEmptyString("暂时没有记录");
                adapter_order.notifyDataSetChanged();
            }
        }else{
            if (adapter_notice == null) {
                adapter_notice = new OrderListAdapter(mContext, notices);
                adapter_notice.setEmptyString("暂时没有记录");
                list_system.setAdapter(adapter_notice);
            } else {
                adapter_notice.setEmptyString("暂时没有记录");
                adapter_notice.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case NOTICE_LIST:
                String page = netTask.getParams().get("page");
                if ("0".equals(page)) {// 刷新
                    if("1".equals(flag))
                        layout_order.refreshFailed();
                    else
                        layout_system.refreshFailed();
                    freshData();
                } else {// 更多
                    if("1".equals(flag))
                        layout_order.loadmoreFailed();
                    else
                        layout_system.loadmoreFailed();
                }
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case NOTICE_LIST:
                String page = netTask.getParams().get("page");
                if ("0".equals(page)) {// 刷新
                    if("1".equals(flag))
                        layout_order.refreshFailed();
                    else
                        layout_system.refreshFailed();
                    freshData();
                } else {// 更多
                    if("1".equals(flag))
                        layout_order.loadmoreFailed();
                    else
                        layout_system.loadmoreFailed();
                }
                break;
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        right = (TextView) findViewById(R.id.title_btn_right);
        title = (TextView) findViewById(R.id.title_text);

        order = (LinearLayout) findViewById(R.id.layout_0);
        text_order = (TextView) findViewById(R.id.textview_0);
        image_order = (ImageView) findViewById(R.id.imageview_0);
        image_point = (ImageView) findViewById(R.id.imageview_2);

        system = (LinearLayout) findViewById(R.id.layout_1);
        text_system = (TextView) findViewById(R.id.textview_1);
        image_system = (ImageView) findViewById(R.id.imageview_1);

        layout_order = (RefreshLoadmoreLayout) findViewById(R.id.refreshLoadmoreLayout);
        list_order = (XtomListView) findViewById(R.id.listview);
        layout_system = (RefreshLoadmoreLayout) findViewById(R.id.refreshLoadmoreLayout1);
        list_system = (XtomListView) findViewById(R.id.listview1);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
    }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
        title.setText("消息");
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
                showButtonDialog(flag);
            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_order.setTextColor(mContext.getResources().getColor(R.color.yellow));
                image_order.setVisibility(View.VISIBLE);
                text_system.setTextColor(mContext.getResources().getColor(R.color.cl_a0a0a0));
                image_system.setVisibility(View.INVISIBLE);
                layout_order.setVisibility(View.VISIBLE);
                layout_system.setVisibility(View.GONE);
                flag = "1";
            }
        });

        system.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_order.setTextColor(mContext.getResources().getColor(R.color.cl_a0a0a0));
                image_order.setVisibility(View.INVISIBLE);
                text_system.setTextColor(mContext.getResources().getColor(R.color.yellow));
                image_system.setVisibility(View.VISIBLE);
                layout_order.setVisibility(View.GONE);
                layout_system.setVisibility(View.VISIBLE);
                flag = "2";
                if(isFrist){
                    isFrist = false;
                    progressBar.setVisibility(View.VISIBLE);
                    getNoticeList("1", page_notice);
                }
            }
        });

        layout_order.setOnStartListener(new XtomRefreshLoadmoreLayout.OnStartListener() {
            @Override
            public void onStartRefresh(XtomRefreshLoadmoreLayout xtomRefreshLoadmoreLayout) {
                page_order = 0;
               getNoticeUnread();
            }

            @Override
            public void onStartLoadmore(XtomRefreshLoadmoreLayout xtomRefreshLoadmoreLayout) {
                page_order ++;
                getNoticeList("2", page_order);
            }
        });

        layout_system.setOnStartListener(new XtomRefreshLoadmoreLayout.OnStartListener() {
            @Override
            public void onStartRefresh(XtomRefreshLoadmoreLayout xtomRefreshLoadmoreLayout) {
                page_notice = 0;
                getNoticeList("1", page_notice);
            }

            @Override
            public void onStartLoadmore(XtomRefreshLoadmoreLayout xtomRefreshLoadmoreLayout) {
                page_notice ++;
                getNoticeList("1", page_notice);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK)
            return;
        switch (requestCode){
            case R.id.layout:
                page_order = 0;
                getNoticeList("1", page_order);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private HemaButtonDialog mDialog;
    private void showButtonDialog(String flag){
        if (mDialog == null) {
            mDialog = new HemaButtonDialog(mContext);
            mDialog.setLeftButtonText("取消");
            mDialog.setRightButtonText("确定");
            mDialog.setText("确定要清空所有信息?");
            mDialog.setRightButtonTextColor(mContext.getResources().getColor(R.color.yellow));
        }
        mDialog.setButtonListener(new ButtonListener(flag));
        mDialog.show();
    }

    private class ButtonListener implements HemaButtonDialog.OnButtonListener {

        private String type;
        public ButtonListener(String type){
            this.type = type;
        }

        @Override
        public void onLeftButtonClick(HemaButtonDialog dialog) {
            dialog.cancel();
        }

        @Override
        public void onRightButtonClick(HemaButtonDialog dialog) {
            dialog.cancel();
            User user = hm_WcpcUserApplication.getInstance().getUser();
            if("1".equals(type))
                getNetWorker().noticeSaveOperate(user.getToken(), "0", "2", "1", "4");
            else
                getNetWorker().noticeSaveOperate(user.getToken(), "0", "1", "1", "4");
        }
    }
}
