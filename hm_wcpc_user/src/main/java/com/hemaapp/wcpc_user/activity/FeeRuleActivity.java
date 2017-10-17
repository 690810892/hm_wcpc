package com.hemaapp.wcpc_user.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.adapter.MyRecyclerAdapter;
import com.hemaapp.wcpc_user.module.FeeRuleListInfor;

import java.util.ArrayList;

import xtom.frame.util.XtomSharedPreferencesUtil;

/**
 * Created by WangYuxia on 2016/5/9.
 * webview的内容没有设置显示
 */
public class FeeRuleActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private TextView tv_city;
    private TextView out_text_count;
    private TextView out_text_distance, out_text_distance_fee, out_text_distance_over, out_text_distance_over_fee;
    private TextView out_fail_distance, out_fail_fee, out_fail_distance_over, out_fail_fee_over;
    private ArrayList<FeeRuleListInfor> cityouts = new ArrayList<>();

    private String city;

    private ImageView background;

    private LinearLayout out_fatherview;
    private TextView out_ok;
    private TextView out_cancel;
    private RecyclerView out_recyclerView;
    private MyRecyclerAdapter out_recyclerAdapter;

    private FeeRuleListInfor out_infor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_feerule);
        super.onCreate(savedInstanceState);
        getInfor();
        city = XtomSharedPreferencesUtil.get(mContext, "city");
        tv_city.setText(city);
    }

    private void getInfor(){
        String district = XtomSharedPreferencesUtil.get(mContext, "district");
        getNetWorker().feeRuleList("0", district);
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask hemaNetTask) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case FEE_RULE_LIST:
                showProgressDialog("请稍后...");
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask hemaNetTask) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case FEE_RULE_LIST:
                cancelProgressDialog();
                break;
        }
    }

    @Override
    protected void callBackForServerSuccess(HemaNetTask hemaNetTask, HemaBaseResult hemaBaseResult) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case FEE_RULE_LIST:
                HemaPageArrayResult<FeeRuleListInfor> fReslut = (HemaPageArrayResult<FeeRuleListInfor>) hemaBaseResult;
                cityouts = fReslut.getObjects();
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask hemaNetTask, int i) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case FEE_RULE_LIST:
                showTextDialog("抱歉，获取数据失败");
                break;
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        right = (TextView) findViewById(R.id.title_btn_right);
        title = (TextView) findViewById(R.id.title_text);

        tv_city = (TextView) findViewById(R.id.tv_city);
        out_text_count = (TextView) findViewById(R.id.textview_3);
        out_text_distance = (TextView) findViewById(R.id.textview_4);
        out_text_distance_fee = (TextView) findViewById(R.id.textview_5);
        out_text_distance_over= (TextView) findViewById(R.id.textview_6);
        out_text_distance_over_fee = (TextView) findViewById(R.id.textview_7);
        out_fail_distance = (TextView) findViewById(R.id.fail_4);
        out_fail_fee = (TextView) findViewById(R.id.fail_5);
        out_fail_distance_over = (TextView) findViewById(R.id.fail_6);
        out_fail_fee_over = (TextView) findViewById(R.id.fail_7);

        background = (ImageView) findViewById(R.id.imageview);
        out_fatherview = (LinearLayout) findViewById(R.id.father1);
        out_ok = (TextView) out_fatherview.findViewById(R.id.textview_1);
        out_cancel = (TextView) out_fatherview.findViewById(R.id.textview_0);
        out_recyclerView = (RecyclerView) out_fatherview.findViewById(R.id.recyclerView1);
    }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
        title.setText("计费规则");
        right.setVisibility(View.INVISIBLE);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        out_text_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectCount();
            }
        });

        setListener(background);
        setListener(out_cancel);
    }

    private void showSelectCount(){
        background.setVisibility(View.VISIBLE);
        out_fatherview.setVisibility(View.VISIBLE);

        out_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                background.setVisibility(View.GONE);
                out_fatherview.setVisibility(View.GONE);
                out_text_count.setText(out_infor.getNumbers()+"人");

                out_text_distance.setText(out_infor.getMileage()+"公里内");
                out_text_distance_fee.setText(out_infor.getSuccessfee()+"元");
                out_text_distance_over.setText(out_infor.getMileage()+"公里以上部分");
                out_text_distance_over_fee.setText(out_infor.getSuccessoverfee()+"元/公里");

                out_fail_distance.setText(out_infor.getMileage()+"公里内");
                out_fail_fee.setText(out_infor.getFailfee()+"元");
                out_fail_distance_over.setText(out_infor.getMileage()+"公里以上部分");
                out_fail_fee_over.setText(out_infor.getFailoverfee()+"元/公里");
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        out_recyclerView.setLayoutManager(linearLayoutManager);
        //设置适配器
        out_recyclerAdapter = new MyRecyclerAdapter(FeeRuleActivity.this, cityouts);
        out_recyclerView.setAdapter(out_recyclerAdapter);
    }

    public void operate(int position){
        for(int i = 0; i < cityouts.size(); i++){
            if(i == position) {
                cityouts.get(i).setChecked(true);
                out_infor = cityouts.get(i);
            } else
                cityouts.get(i).setChecked(false);
        }
        out_recyclerAdapter.notifyDataSetChanged();
    }

    private void setListener(View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.imageview:
                    case R.id.textview_0:
                        background.setVisibility(View.GONE);
                        out_fatherview.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

}
