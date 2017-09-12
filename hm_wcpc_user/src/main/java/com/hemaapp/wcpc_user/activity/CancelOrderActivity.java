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
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.DataInfor;
import com.hemaapp.wcpc_user.module.User;

import java.util.ArrayList;

/**
 * Created by WangYuxia on 2016/5/20.
 */
public class CancelOrderActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private LinearLayout layout;
    private EditText editText;
    private TextView text_submit;

    private String order_id, content, reply_str;
    private ArrayList<DataInfor> infors = new ArrayList<>();
    private User user;
    private ArrayList<TextView> contents = new ArrayList<>();

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
            layout.setVisibility(View.GONE);
        else{
            layout.setVisibility(View.VISIBLE);
            int m = infors.size() / 2;
            int n = infors.size() % 2;
            int count ;
            if(n == 0)
                count = m;
            else
                count = m + 1;

            for(int i = 0; i< count; i++){
                View view = LayoutInflater.from(mContext).inflate(R.layout.listitem_cancelorder, null);
                TextView textView = (TextView) view.findViewById(R.id.textview_0);
                TextView textView1 = (TextView) view.findViewById(R.id.textview_1);
                textView.setText(infors.get(i*2).getName());
                contents.add(i*2, textView);
                textView.setTag(R.id.button_0, infors.get(i*2));
                textView.setTag(R.id.button_1, i*2);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = (Integer) v.getTag(R.id.button_1);
                        DataInfor data = (DataInfor) v.getTag(R.id.button_0);
                        if(!data.isChecked()) {
                            allfalse(pos, true);
                        } else {
                            allfalse(pos, false);
                        }
                    }
                });

                if(i*2+1 < infors.size()){
                    textView1.setVisibility(View.VISIBLE);
                    textView1.setText(infors.get(i*2+1).getName());
                    contents.add(i*2+1, textView1);
                    textView1.setTag(R.id.tag_0, infors.get(i*2+1));
                    textView1.setTag(R.id.tag_1, i*2+1);
                    textView1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int pos = (Integer) v.getTag(R.id.tag_1);
                            DataInfor data = (DataInfor) v.getTag(R.id.tag_0);
                            if(!data.isChecked()) {
                                allfalse(pos, true);
                            } else {
                                allfalse(pos, false);
                            }
                        }
                    });
                }else{
                    textView1.setVisibility(View.INVISIBLE);
                }
                layout.addView(view);
            }
        }
    }

    private void allfalse(int position, boolean result){
        for(int i = 0; i< infors.size(); i++){
            if(i == position){
                if(result){
                    contents.get(i).setBackgroundResource(R.drawable.bg_cancelorder_s);
                    contents.get(i).setTextColor(0xfff79405);
                }else {
                    contents.get(i).setBackgroundResource(R.drawable.bg_cancelorder_n);
                    contents.get(i).setTextColor(0xff606060);
                }
                infors.get(i).setChecked(result);
            }else{
                infors.get(i).setChecked(!result);
                contents.get(i).setBackgroundResource(R.drawable.bg_cancelorder_n);
                contents.get(i).setTextColor(0xff606060);
            }
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

        layout = (LinearLayout) findViewById(R.id.linearlayout);
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
                    if(infors.get(i).isChecked())
                        reply_str = infors.get(i).getId();
                }

                if(isNull(reply_str)){
                    showTextDialog("请选择取消订单的原因");
                    return;
                }

                content = editText.getText().toString();
                getNetWorker().orderOperate(user.getToken(), "4", order_id, reply_str, content);
            }
        });
    }
}
