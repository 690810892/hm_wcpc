package com.hemaapp.wcpc_user.activity;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.EventBusConfig;
import com.hemaapp.wcpc_user.EventBusModel;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.adapter.TagListAdapter;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.DataInfor;
import com.hemaapp.wcpc_user.module.User;
import com.hemaapp.wcpc_user.view.FlowLayout.TagFlowLayout;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by WangYuxia on 2016/5/20.
 * 取消订单
 */
public class CancelOrderActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private TagFlowLayout group_textview;
    private EditText editText;
    private TextView text_submit;

    private String order_id, content, reply_str,reply_id;
    private ArrayList<DataInfor> infors = new ArrayList<>();
    private TagListAdapter adapter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_cancelorder);
        super.onCreate(savedInstanceState);
        user = hm_WcpcUserApplication.getInstance().getUser();
        getKinds();
    }

    private void getKinds(){
        getNetWorker().dataList("1");
    }

    private void initUserData(){
        if(infors == null || infors.size() == 0)
            group_textview.setVisibility(View.GONE);
        else{
            group_textview.setVisibility(View.VISIBLE);
            adapter = new TagListAdapter(infors, mContext);
            group_textview.setAdapter(adapter);
        }
    }

    public void changeStatus(){
        for(int i = 0; i < infors.size(); i++){
            DataInfor attrItem = infors.get(i);
            if(attrItem.getId().equals(adapter.attrItem.getId()))
                attrItem.setChecked(adapter.attrItem.isChecked());
        }
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case CLIENT_GET:
            case ORDER_OPERATE:
                showProgressDialog("请稍后...");
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DATA_LIST:
            case ORDER_OPERATE:
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
            case DATA_LIST:
                HemaPageArrayResult<DataInfor> uResult = (HemaPageArrayResult<DataInfor>) baseResult;
                infors = uResult.getObjects();
                initUserData();
                break;
            case ORDER_OPERATE:
                showTextDialog("取消成功");
                EventBus.getDefault().post(new EventBusModel(EventBusConfig.REFRESH_BLOG_LIST));
                title.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setResult(RESULT_OK, mIntent);
                        finish();
                    }
                }, 1000);
                break;
        }
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DATA_LIST:
            case ORDER_OPERATE:
                showTextDialog(baseResult.getMsg());
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DATA_LIST:
                showTextDialog("抱歉，获取数据失败");
                break;
            case ORDER_OPERATE:
                showTextDialog("抱歉，提交失败，请稍后重试");
                break;
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        right = (TextView) findViewById(R.id.title_btn_right);
        title = (TextView) findViewById(R.id.title_text);

        group_textview = (TagFlowLayout) findViewById(R.id.multitextview);
        editText = (EditText) findViewById(R.id.edittext);
        text_submit = (TextView) findViewById(R.id.button);
    }

    @Override
    protected void getExras() {
        order_id = mIntent.getStringExtra("id");
    }

    @Override
    protected void setListener() {
        title.setText("取消订单的原因");
        right.setVisibility(View.INVISIBLE);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        text_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i< infors.size(); i++){
                    if(infors.get(i).isChecked()){
                        if (isNull(reply_str)) {
                            reply_str = infors.get(i).getName();
                            reply_id= infors.get(i).getId();
                        }else {
                            reply_str = reply_str + "," + infors.get(i).getName();
                            reply_id= reply_id + "," + infors.get(i).getId();
                        }
                    }

                }

                content = editText.getText().toString();
                if(isNull(content)){
                    content="";
                }
                showDialog();
            }
        });
    }
    private PopupWindow mWindow;
    private ViewGroup mViewGroup;
    private TextView exit;
    private TextView cancel;
    private TextView pop_content;

    private void showDialog(){
        if (mWindow != null) {
            mWindow.dismiss();
        }
        mWindow = new PopupWindow(mContext);
        mWindow.setWidth(FrameLayout.LayoutParams.MATCH_PARENT);
        mWindow.setHeight(FrameLayout.LayoutParams.MATCH_PARENT);
        mWindow.setBackgroundDrawable(new BitmapDrawable());
        mWindow.setFocusable(true);
        mWindow.setAnimationStyle(R.style.PopupAnimation);
        mViewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(
                R.layout.pop_exit, null);
        exit = (TextView) mViewGroup.findViewById(R.id.textview_1);
        cancel = (TextView) mViewGroup.findViewById(R.id.textview_0);
        pop_content = (TextView) mViewGroup.findViewById(R.id.textview);
        mWindow.setContentView(mViewGroup);
        mWindow.showAtLocation(mViewGroup, Gravity.CENTER, 0, 0);
        pop_content.setText("确定取消订单？");
        cancel.setText("再等一会");
        exit.setText("确定取消");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
                getNetWorker().orderOperate(user.getToken(), "1", order_id,reply_id, reply_str,content);
            }
        });
    }

}
