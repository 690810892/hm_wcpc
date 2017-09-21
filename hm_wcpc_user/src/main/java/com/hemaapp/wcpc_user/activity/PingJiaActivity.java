package com.hemaapp.wcpc_user.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.adapter.TagListAdapter;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.DataInfor;
import com.hemaapp.wcpc_user.module.User;
import com.hemaapp.wcpc_user.view.FlowLayout.TagFlowLayout;

import java.util.ArrayList;

/**
 * Created by WangYuxia on 2016/5/20.
 * 评价订单
 */
public class PingJiaActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private ImageView image_0, image_1, image_2, image_3, image_4;
    private TagFlowLayout group_textview;
    private EditText editText;
    private TextView text_submit;
    private TextView text_other; //跳过此页

    private ArrayList<ImageView> images = new ArrayList<>();
    private String order_Id, point = "5";
    private ArrayList<DataInfor> infors = new ArrayList<>();
    private TagListAdapter adapter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_pingjia);
        super.onCreate(savedInstanceState);
        user = hm_WcpcUserApplication.getInstance().getUser();
        getKinds();
    }

    private void getKinds(){
        getNetWorker().dataList("2");
    }

    private void initUserData(){
        if(infors == null || infors.size() == 0)
            group_textview.setVisibility(View.GONE);
        else{
            group_textview.setVisibility(View.VISIBLE);
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
            case REPLY_ADD:
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
            case REPLY_ADD:
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
            case REPLY_ADD:
                showTextDialog("评价成功");
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
            case REPLY_ADD:
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
            case REPLY_ADD:
                showTextDialog("抱歉，提交失败，请稍后重试");
                break;
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        right = (TextView) findViewById(R.id.title_btn_right);
        title = (TextView) findViewById(R.id.title_text);

        image_0 = (ImageView) findViewById(R.id.imageview_0);
        image_1 = (ImageView) findViewById(R.id.imageview_1);
        image_2 = (ImageView) findViewById(R.id.imageview_2);
        image_3 = (ImageView) findViewById(R.id.imageview_3);
        image_4 = (ImageView) findViewById(R.id.imageview_4);
        images.add(0, image_0);
        images.add(1, image_1);
        images.add(2, image_2);
        images.add(3, image_3);
        images.add(4, image_4);

        group_textview = (TagFlowLayout) findViewById(R.id.multitextview);
        editText = (EditText) findViewById(R.id.edittext);
        text_submit = (TextView) findViewById(R.id.button);
        text_other = (TextView) findViewById(R.id.button_0);
    }

    @Override
    protected void getExras() {
        order_Id = mIntent.getStringExtra("id");
    }

    @Override
    protected void setListener() {
        title.setText("评价");
        right.setVisibility(View.INVISIBLE);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        image_0.setOnClickListener(getOnClickListener(0));
        image_1.setOnClickListener(getOnClickListener(1));
        image_2.setOnClickListener(getOnClickListener(2));
        image_3.setOnClickListener(getOnClickListener(3));
        image_4.setOnClickListener(getOnClickListener(4));

        text_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("0".equals(point)){
                    showTextDialog("请对本次服务进行评分");
                    return;
                }
                int count = 0;
                String reply_str = "";
                for(int i = 0; i< infors.size(); i++){
                    if(infors.get(i).isChecked()){
                        count ++;
                        if(count == 1)
                            reply_str = reply_str + infors.get(i).getId();
                        else
                            reply_str = reply_str +
                                   "," + infors.get(i).getId();
                    }
                }
                String content = editText.getText().toString();
                getNetWorker().replyAdd(user.getToken(), "1", order_Id, reply_str, point, content);
            }
        });

        text_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNetWorker().replyAdd(user.getTakecount(), "1", order_Id, "", point, "");
            }
        });
    }

    private View.OnClickListener getOnClickListener(final int position){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                point = String.valueOf(position + 1);
                setImages(position);
            }
        };
    }

    private void setImages(int position){
        for(int i = 0; i< 5; i++){
            if(i <= position){
                images.get(i).setImageResource(R.mipmap.img_pingjia_s);
            }else{
                images.get(i).setImageResource(R.mipmap.img_pingjia_n);
            }
        }
    }
}
