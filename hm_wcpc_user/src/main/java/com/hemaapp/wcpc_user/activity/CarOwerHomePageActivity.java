package com.hemaapp.wcpc_user.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.hm_FrameWork.view.RefreshLoadmoreLayout;
import com.hemaapp.hm_FrameWork.view.RoundedImageView;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.BaseUtil;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.adapter.ReplyListAdapter;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.Driver;
import com.hemaapp.wcpc_user.module.ReplyListInfor;
import com.hemaapp.wcpc_user.module.User;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import xtom.frame.image.load.XtomImageTask;
import xtom.frame.util.XtomToastUtil;
import xtom.frame.view.XtomListView;
import xtom.frame.view.XtomRefreshLoadmoreLayout;

/**
 * Created by WangYuxia on 2016/5/12.
 */
public class CarOwerHomePageActivity extends BaseActivity {

    private ImageView left;
    private RoundedImageView image_avatar;
    private TextView text_realname;
    private ImageView image_sex;
    private TextView text_carbrand;
    private TextView text_carnumber;
    private ImageView image_star_0, image_star_1,image_star_2,image_star_3,image_star_4;
    private TextView text_servicecount; //服务次数

    private RefreshLoadmoreLayout layout;
    private XtomListView mListView;

    private ArrayList<ReplyListInfor> replys = new ArrayList<>();
    private ReplyListAdapter adapter;

    private String id; //司机的id
    private User user;
    private Driver infor;
    private int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_driverhomepage);
        super.onCreate(savedInstanceState);
        getNetWorker().driverGet(id);
    }

    private void getReplyList(){
        getNetWorker().replyList("1", id, page);
    }

    private void freshData(){
        try {
            URL url = new URL(infor.getAvatar());
            image_avatar.setCornerRadius(90);
            imageWorker.loadImage(new XtomImageTask(image_avatar, url, mContext));
        } catch (MalformedURLException e) {
            image_avatar.setImageResource(R.mipmap.default_driver);
        }

        text_realname.setText(infor.getRealname());
        if("男".equals(infor.getSex()))
            image_sex.setImageResource(R.mipmap.img_sex_boy);
        else
            image_sex.setImageResource(R.mipmap.img_sex_girl);
        text_carbrand.setText(infor.getCarbrand());
        text_carnumber.setText(infor.getCarnumbers());
        text_servicecount.setText("服务次数:"+infor.getServicecount()+"次");
        BaseUtil.transScore(image_star_0, image_star_1, image_star_2, image_star_3, image_star_4, infor.getReplycount(), infor.getTotalpoint());
        if(adapter == null){
            adapter = new ReplyListAdapter(mContext, replys);
            mListView.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DRIVER_GET:
                showProgressDialog("请稍后...");
                break;
            case REPLY_LIST:
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
            case DRIVER_GET:
                cancelProgressDialog();
                break;
            case REPLY_LIST:
                cancelProgressDialog();
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
            case DRIVER_GET:
                HemaArrayResult<Driver> uResult = (HemaArrayResult<Driver>) baseResult;
                infor = uResult.getObjects().get(0);
                getReplyList();
                break;
            case REPLY_LIST:
                String page = netTask.getParams().get("page");
                HemaPageArrayResult<ReplyListInfor> cResult = (HemaPageArrayResult<ReplyListInfor>) baseResult;
                ArrayList<ReplyListInfor> cashs = cResult.getObjects();
                if ("0".equals(page)) {// 刷新
                    layout.refreshSuccess();
                    replys.clear();
                    replys.addAll(cashs);

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
                        replys.addAll(cashs);
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

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DRIVER_GET:
                showTextDialog(baseResult.getMsg());
                break;
            case REPLY_LIST:
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
            case DRIVER_GET:
                showTextDialog("抱歉，获取数据失败");
                break;
            case REPLY_LIST:
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
        image_avatar = (RoundedImageView) findViewById(R.id.imageview);
        text_realname = (TextView) findViewById(R.id.textview_0);
        image_sex = (ImageView) findViewById(R.id.imageview_0);
        text_carbrand = (TextView) findViewById(R.id.textview_1);
        text_carnumber = (TextView) findViewById(R.id.textview_2);
        image_star_0 = (ImageView) findViewById(R.id.imageview_1);
        image_star_1 = (ImageView) findViewById(R.id.imageview_2);
        image_star_2 = (ImageView) findViewById(R.id.imageview_3);
        image_star_3 = (ImageView) findViewById(R.id.imageview_4);
        image_star_4 = (ImageView) findViewById(R.id.imageview_5);
        text_servicecount = (TextView) findViewById(R.id.textview_3);

        layout = (RefreshLoadmoreLayout) findViewById(R.id.refreshLoadmoreLayout);
        mListView = (XtomListView) findViewById(R.id.listview);
    }

    @Override
    protected void getExras() {
        id = mIntent.getStringExtra("id");
    }

    @Override
    protected void setListener() {

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
                getReplyList();
            }

            @Override
            public void onStartLoadmore(XtomRefreshLoadmoreLayout xtomRefreshLoadmoreLayout) {
                page ++;
                getReplyList();
            }
        });
    }
}
